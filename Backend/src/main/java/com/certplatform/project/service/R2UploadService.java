package com.certplatform.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Service
public class R2UploadService {

    private static final Logger log = LoggerFactory.getLogger(R2UploadService.class);

    private final String projectRequirementsBucket;
    private final String certificatesBucket;
    private final S3Client s3Client;

    public R2UploadService(@Value("${r2.projects.bucket}") String projectRequirementsBucket,
                           @Value("${r2.certificates.bucket}") String certificatesBucket,
                           @Value("${r2.access.key}") String accessKey,
                           @Value("${r2.secret.key}") String secretKey,
                           @Value("${r2.endpoint}") String r2Endpoint) {
        this.projectRequirementsBucket = projectRequirementsBucket;
        this.certificatesBucket = certificatesBucket;

        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_1) // R2 requires us-east-1
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
                )
                .endpointOverride(URI.create(r2Endpoint))
                .build();
    }

    public String uploadProjectRequirementDoc(MultipartFile file) {
        String key = "requirements/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        log.info("[uploadProjectRequirementDoc] Uploading requirement doc key={}", key);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(projectRequirementsBucket)
                    .key(key)
                    .contentType("application/pdf")
                    .build();

            s3Client.putObject(putObjectRequest,
                    software.amazon.awssdk.core.sync.RequestBody.fromBytes(file.getBytes()));

            log.info("[uploadProjectRequirementDoc] Upload complete key={}", key);
            return key;
        } catch (IOException e) {
            log.error("[uploadProjectRequirementDoc] Failed to read file {}", file.getOriginalFilename(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid file upload", e);
        } catch (Exception e) {
            log.error("[uploadProjectRequirementDoc] Failed to upload file {}", file.getOriginalFilename(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload requirement document", e);
        }
    }

    public String uploadCertificate(File file) {
        String key = "certificates/" + UUID.randomUUID() + "-" + file.getName();
        log.info("[uploadCertificate] Uploading certificate key={}", key);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(certificatesBucket)
                    .key(key)
                    .contentType("application/pdf")
                    .build();

            s3Client.putObject(putObjectRequest,
                    software.amazon.awssdk.core.sync.RequestBody.fromFile(file));

            log.info("[uploadCertificate] Upload complete key={}", key);
            return key;
        } catch (Exception e) {
            log.error("[uploadCertificate] Failed to upload certificate {}", file.getName(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload certificate", e);
        }
    }
}
