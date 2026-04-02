package com.certplatform.certificate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.certplatform.certificate.dto.CertificateResponse;
import com.certplatform.certificate.entity.Certificate;
import com.certplatform.certificate.repository.CertificateRepository;
import com.certplatform.common.entity.Enrollment;
import com.certplatform.common.repository.EnrollmentRepository;
import com.certplatform.project.service.R2UploadService;
import com.certplatform.user.entity.Student;
import com.certplatform.user.repository.StudentRepository;

import lombok.AllArgsConstructor;

import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Service
public class CertificateGenerationService {

    private static final Logger log = LoggerFactory.getLogger(CertificateGenerationService.class);

    private final EnrollmentRepository enrollmentRepo;
    private final CertificateRepository certificateRepo;
    private final StudentRepository studentRepo;
    private final R2UploadService r2UploadService;

    /**
     * Generate and issue a certificate for a student/project.
     */
    @Transactional
    public CertificateResponse generateCertificate(UUID studentId, UUID projectId) {
        log.info("[generateCertificate] Request received for studentId={} projectId={}", studentId, projectId);

        try {
            // ✅ Validate enrollment
            Enrollment enrollment = enrollmentRepo.findByStudentIdAndProjectId(studentId, projectId);
            if (enrollment == null) {
                log.warn("[generateCertificate] No enrollment found for studentId={} projectId={}", studentId, projectId);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found");
            }

            // ✅ Check if certificate already exists
            certificateRepo.findByStudentIdAndProjectId(studentId, projectId)
                .ifPresent(cert -> {
                    log.warn("[generateCertificate] Certificate already exists for studentId={} projectId={}", studentId, projectId);
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Certificate already issued for this project");
                });

            // --- Step 1: Generate certificate file (PDF) ---
            String certificateFilePath = "certificates/student_" + studentId + "_project_" + projectId + ".pdf";
            log.info("[generateCertificate] Generating certificate PDF at {}", certificateFilePath);

            Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
            String studentName = student.getName();

            log.info("[generateCertificate] Generating certificate PDF for student={}", studentName);
            CertificatePdfGenerator.generateCertificatePdf(studentName, certificateFilePath);

            // --- Step 2: Upload to R2 ---
            log.info("[generateCertificate] Uploading certificate PDF to R2 for studentId={} projectId={}", studentId, projectId);
            File pdfFile = new File(certificateFilePath);
            String certificatePathKey = r2UploadService.uploadCertificate(pdfFile);

            // --- Step 3: Save Certificate entity ---
            Certificate certificate = new Certificate();
            certificate.setStudentId(enrollment.getStudentId());
            certificate.setProjectId(enrollment.getProjectId());
            certificate.setCertificatePathKey(certificatePathKey);
            certificate.setCreatedAt(LocalDateTime.now());

            certificateRepo.save(certificate);
            log.info("[generateCertificate] Certificate saved successfully for studentId={} projectId={}", studentId, projectId);

            return new CertificateResponse(certificatePathKey);

        } catch (ResponseStatusException ex) {
            log.error("[generateCertificate] Business error for studentId={} projectId={}", studentId, projectId, ex);
            throw ex;
        } catch (Exception ex) {
            log.error("[generateCertificate] Unexpected error for studentId={} projectId={}", studentId, projectId, ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate certificate", ex);
        }
    }
}
