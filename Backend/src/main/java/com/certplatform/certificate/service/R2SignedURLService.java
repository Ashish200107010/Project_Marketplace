package com.certplatform.certificate.service;

import com.certplatform.certificate.entity.Certificate;
import com.certplatform.certificate.repository.CertificateRepository;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Service
public class R2SignedURLService {

    private static final Logger log = LoggerFactory.getLogger(R2SignedURLService.class);

    private final CertificateRepository certificateRepo;

    public R2SignedURLService(CertificateRepository certificateRepo) { this.certificateRepo = certificateRepo; }

    private final String certificatesBucketName = "certplatform-certificates-test";
    private final String accountEndpoint = "https://9b61f36e02a914e17549eeb013840a00.r2.cloudflarestorage.com";
    private final String customDomain = "https://files.remotask.in";

    private final String accessKey = "2af57c39ef8963a0d7bbbcdab087f685";
    private final String secretKey = "e06d7b179b35169a6f43ce2647605ca3e0a4329854667ad652b09c7a32c2e881";

     /**
     * Generate a signed URL for a certificate.
     */
    public String generateSignedUrl(UUID studentId, UUID projectId, int expirySeconds) {
        log.info("[generateSignedUrl] Request received for studentId={} projectId={}", studentId, projectId);

        Certificate certificate = certificateRepo.findByStudentIdAndProjectId(studentId, projectId)
                .orElseThrow(() -> {
                    log.warn("[generateSignedUrl] Certificate not found for studentId={} projectId={}", studentId, projectId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate not found for this project");
                });

        String certificatePathKey = certificate.getCertificatePathKey();
        if (certificatePathKey == null || certificatePathKey.isBlank()) {
            log.warn("[generateSignedUrl] Certificate path missing for studentId={} projectId={}", studentId, projectId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Certificate path not found for this project");
        }

        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
                )
                .endpointOverride(URI.create(accountEndpoint))
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(certificatesBucketName)
                    .key(certificatePathKey)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(
                    GetObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofSeconds(expirySeconds))
                            .getObjectRequest(getObjectRequest)
                            .build()
            );

            String signedUrl = presignedRequest.url().toString()
                    .replace(accountEndpoint, customDomain);

            log.info("[generateSignedUrl] Signed URL generated successfully for studentId={} projectId={}", studentId, projectId);
            return signedUrl;

        } catch (Exception ex) {
            log.error("[generateSignedUrl] Error generating signed URL for studentId={} projectId={}", studentId, projectId, ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate signed URL", ex);
        }
    }
}
