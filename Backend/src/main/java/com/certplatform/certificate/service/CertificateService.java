package com.certplatform.certificate.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.certplatform.common.repository.EnrollmentRepository;
import com.certplatform.payment.repository.TransactionRepository;

@Service
public class CertificateService {

    private static final Logger log = LoggerFactory.getLogger(CertificateService.class);

    private final EnrollmentRepository enrollmentRepo;
    private final R2SignedURLService r2SignedURLService;
    private final TransactionRepository transactionRepo;

    public CertificateService(EnrollmentRepository enrollmentRepo,
                              R2SignedURLService r2SignedURLService,
                              TransactionRepository transactionRepo) {
        this.enrollmentRepo = enrollmentRepo;
        this.r2SignedURLService = r2SignedURLService;
        this.transactionRepo = transactionRepo;
    }

    /**
     * Retrieve signed certificate URL for a student/project.
     */
    public String getCertificateUrl(UUID studentId, UUID projectId) {
        log.info("[getCertificateUrl] Request received for studentId={} projectId={}", studentId, projectId);

        try {
            // ✅ Verify enrollment
            try {
                if (enrollmentRepo.findByStudentIdAndProjectId(studentId, projectId) == null) {
                    log.warn("[getCertificateUrl] Enrollment not found for studentId={} projectId={}", studentId, projectId);
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not enrolled in project");
                }
                log.info("[getCertificateUrl] Enrollment verified for studentId={} projectId={}", studentId, projectId);
            } catch (ResponseStatusException ex) {
                throw ex;
            } catch (Exception ex) {
                log.error("[getCertificateUrl] Enrollment verification failed for studentId={} projectId={}", studentId, projectId, ex);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Enrollment verification failed", ex);
            }

            // ✅ Verify payment
            try {
                boolean hasPaid = transactionRepo.existsByStudentId(studentId);
                if (!hasPaid) {
                    log.warn("[getCertificateUrl] Payment pending for studentId={}", studentId);
                    throw new ResponseStatusException(HttpStatus.PAYMENT_REQUIRED, "Platform maintenance fee is pending");
                }
                log.info("[getCertificateUrl] Payment verified for studentId={}", studentId);
            } catch (ResponseStatusException ex) {
                throw ex;
            } catch (Exception ex) {
                log.error("[getCertificateUrl] Payment verification failed for studentId={}", studentId, ex);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Payment verification failed", ex);
            }

            // ✅ Generate signed URL
            try {
                String certificatePath = r2SignedURLService.generateSignedUrl(studentId, projectId, 3600);
                log.info("[getCertificateUrl] Signed URL generated successfully for studentId={} projectId={}", studentId, projectId);
                return certificatePath;
            } catch (Exception ex) {
                log.error("[getCertificateUrl] Error generating signed URL for studentId={} projectId={}", studentId, projectId, ex);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate certificate URL", ex);
            }

        } catch (ResponseStatusException ex) {
            // Business error already logged above
            throw ex;
        } catch (Exception ex) {
            log.error("[getCertificateUrl] Unexpected error for studentId={} projectId={}", studentId, projectId, ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Certificate not found for this project", ex);
        }
    }
}
