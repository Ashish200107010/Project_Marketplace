
package com.certplatform.project.service;

import com.certplatform.project.entity.Project;
import com.certplatform.project.repository.ProjectStudentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Service
public class R2PresignedUrlService {

    private static final Logger log = LoggerFactory.getLogger(R2PresignedUrlService.class);

    private final ProjectStudentRepository projectStudentRepository;
      
        @Value("${r2.projects.bucket}")
        private String projectsBucketName;

        @Value("${r2.reviews.bucket}")
        private String reviewsBucket;

        @Value("${r2.account.id}")
        private String accountId;

        @Value("${r2.account.endpoint}")
        private String accountEndpoint;

        @Value("${r2.custom.domain}")
        private String customDomain;

        @Value("${r2.access.key}")
        private String accessKey;

        @Value("${r2.secret.key}")
        private String secretKey;

        @Value("${r2.endpoint}")
        private String r2Endpoint;



    
//     private final String projectsBucketName = "certplatform-projects-test";
//     private final String accountEndpoint = "https://9b61f36e02a914e17549eeb013840a00.r2.cloudflarestorage.com";
//     private final String customDomain = "https://files.remotask.in";

//     private final String reviewsBucket = "certplatform-reviews-test"; // Bucket for reviews
//     private final String accountId = "9b61f36e02a914e17549eeb013840a00";
//     private final String accessKey = "2af57c39ef8963a0d7bbbcdab087f685";
//     private final String secretKey = "e06d7b179b35169a6f43ce2647605ca3e0a4329854667ad652b09c7a32c2e881";
//     private final String r2Endpoint = "https://" + accountId + ".r2.cloudflarestorage.com";

    private final S3Client s3Client;

    public R2PresignedUrlService(ProjectStudentRepository projectStudentRepository,
                                 @Value("${r2.projects.bucket}") String projectsBucketName,
                                 @Value("${r2.reviews.bucket}") String reviewsBucket,
                                 @Value("${r2.account.endpoint}") String accountEndpoint,
                                 @Value("${r2.custom.domain}") String customDomain,
                                 @Value("${r2.access.key}") String accessKey,
                                 @Value("${r2.secret.key}") String secretKey,
                                 @Value("${r2.endpoint}") String r2Endpoint) {
        this.projectStudentRepository = projectStudentRepository;
        this.projectsBucketName = projectsBucketName;
        this.reviewsBucket = reviewsBucket;
        this.accountEndpoint = accountEndpoint;
        this.customDomain = customDomain;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.r2Endpoint = r2Endpoint;

        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .endpointOverride(URI.create(r2Endpoint))
                .build();
    }

    public String generateSignedUrl(UUID projectId, int expirySeconds) {
        log.info("[generateSignedUrl] Generating presigned URL for projectId={}", projectId);
        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .endpointOverride(URI.create(accountEndpoint))
                .build()) {

            Project project = projectStudentRepository.findById(projectId)
                    .orElseThrow(() -> {
                        log.warn("[generateSignedUrl] Project not found id={}", projectId);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
                    });

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(projectsBucketName)
                    .key(project.getRequirementDocKey())
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(
                    GetObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofSeconds(expirySeconds))
                            .getObjectRequest(getObjectRequest)
                            .build()
            );

            String signedUrl = presignedRequest.url().toString().replace(accountEndpoint, customDomain);
            log.info("[generateSignedUrl] Presigned URL generated for projectId={}", projectId);
            return signedUrl;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("[generateSignedUrl] Failed for projectId={}", projectId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate signed URL", e);
        }
    }

    public String uploadAndPresignReviewPdf(UUID submissionId, byte[] pdfBytes, int expirySeconds) {
        log.info("[uploadAndPresignReviewPdf] Uploading review PDF for submissionId={}", submissionId);
        String key = "reviews/" + submissionId + "-" + UUID.randomUUID() + ".pdf";

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(reviewsBucket)
                    .key(key)
                    .contentType("application/pdf")
                    .build();

            s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromBytes(pdfBytes));
            log.info("[uploadAndPresignReviewPdf] PDF uploaded key={}", key);

            try (S3Presigner presigner = S3Presigner.builder()
                    .region(Region.US_EAST_1)
                    .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                    .endpointOverride(URI.create(r2Endpoint))
                    .build()) {

                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(reviewsBucket)
                        .key(key)
                        .build();

                GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofSeconds(expirySeconds))
                        .getObjectRequest(getObjectRequest)
                        .build();

                String presignedUrl = presigner.presignGetObject(presignRequest).url().toString();
                log.info("[uploadAndPresignReviewPdf] Presigned URL generated for submissionId={}", submissionId);
                return presignedUrl;
            }
        } catch (Exception e) {
            log.error("[uploadAndPresignReviewPdf] Failed for submissionId={}", submissionId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload and presign review PDF", e);
        }
    }
}
