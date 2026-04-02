package com.certplatform.project.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * Configuration for Cloudflare R2 integration using AWS SDK v2.
 */
@Configuration
public class R2Config {

    private static final Logger log = LoggerFactory.getLogger(R2Config.class);

    // @Value("${r2.account-id}")
    // private String accountId;

    // @Value("${r2.access-key}")
    // private String accessKey;

    // @Value("${r2.secret-key}")
    // private String secretKey;

    // @Value("${r2.endpoint}")
    // private String r2Endpoint;

    private final String accountId = "YOUR_CLOUDFLARE_ACCOUNT_ID";
    private final String accessKey = "YOUR_R2_ACCESS_KEY";
    private final String secretKey = "YOUR_R2_SECRET_KEY";
    private final String r2Endpoint = "https://" + accountId + ".r2.cloudflarestorage.com";

    @Bean
    public S3Presigner s3Presigner() {
        log.info("[R2Config] Initializing S3Presigner for endpoint={} accountId={}", r2Endpoint, accountId);

        return S3Presigner.builder()
                .region(Region.US_EAST_1) // R2 requires us-east-1
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
                )
                .endpointOverride(URI.create(r2Endpoint))
                .build();
    }
}
