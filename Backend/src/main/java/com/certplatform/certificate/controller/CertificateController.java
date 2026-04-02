package com.certplatform.certificate.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.certplatform.certificate.dto.CertificateRequest;
import com.certplatform.certificate.dto.CertificateResponse;
import com.certplatform.certificate.service.CertificateService;
import com.certplatform.certificate.service.CertificateGenerationService;

@RestController
@RequestMapping("/api/student/projects")
public class CertificateController {

    private static final Logger log = LoggerFactory.getLogger(CertificateController.class);

    private final CertificateService certificateService;
    private final CertificateGenerationService certificateGenerationService;

    public CertificateController(CertificateService certificateService,
                                 CertificateGenerationService certificateGenerationService) {
        this.certificateService = certificateService;
        this.certificateGenerationService = certificateGenerationService;
    }

    /**
     * Download certificate if already issued.
     */
    @PostMapping("/downloadCertificate")
    public ResponseEntity<?> downloadCertificate(@RequestBody CertificateRequest request) {
        log.info("[downloadCertificate] Request received for studentId={} projectId={}",
                 request.getStudentId(), request.getProjectId());
        try {
            String certificateUrl = certificateService.getCertificateUrl(
                    request.getStudentId(), request.getProjectId());
            log.info("[downloadCertificate] Generated signed URL for studentId={} projectId={}",
                     request.getStudentId(), request.getProjectId());
            return ResponseEntity.ok(new CertificateResponse(certificateUrl));

        } catch (IllegalArgumentException ex) {
            log.warn("[downloadCertificate] Certificate not found for studentId={} projectId={}",
                     request.getStudentId(), request.getProjectId(), ex);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", ex.getMessage()));

        } catch (Exception ex) {
            log.error("[downloadCertificate] Unexpected error for studentId={} projectId={}",
                      request.getStudentId(), request.getProjectId(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error occurred"));
        }
    }

    /**
     * Generate certificate immediately (testing endpoint).
     */
    @PostMapping("/certificate/generate")
    public ResponseEntity<?> generateCertificate(@RequestBody CertificateRequest request) {
        log.info("[generateCertificate] Request received for studentId={} projectId={}",
                 request.getStudentId(), request.getProjectId());
        try {
            CertificateResponse response = certificateGenerationService.generateCertificate(
                    request.getStudentId(), request.getProjectId());
            log.info("[generateCertificate] Certificate generated successfully for studentId={} projectId={} pathKey={}",
                     request.getStudentId(), request.getProjectId(), response.getCertificatePathKey());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException ex) {
            log.warn("[generateCertificate] Invalid request for studentId={} projectId={}",
                     request.getStudentId(), request.getProjectId(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", ex.getMessage()));

        } catch (Exception ex) {
            log.error("[generateCertificate] Failed to generate certificate for studentId={} projectId={}",
                      request.getStudentId(), request.getProjectId(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate certificate"));
        }
    }
}
