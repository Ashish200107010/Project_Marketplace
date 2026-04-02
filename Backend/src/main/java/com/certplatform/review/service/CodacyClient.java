package com.certplatform.review.service;

import java.util.HashMap; import java.util.Map;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CodacyClient {

    private final RestTemplate restTemplate;
    private final String codacyApiUrl = "https://api.codacy.com/v3";
    private final String apiToken = "YOUR_CODACY_API_TOKEN";

    public CodacyClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public String fetchReviewJson(String gitLink, String requirementsPdfUrl) {
        try {
            // Build request payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("repositoryUrl", gitLink);
            if (requirementsPdfUrl != null) {
                payload.put("requirements", requirementsPdfUrl);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiToken);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            // Call Codacy API
            ResponseEntity<String> response = restTemplate.postForEntity(
                codacyApiUrl + "/analysis", request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody(); // JSON string
            } else {
                throw new RuntimeException("Codacy API failed: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error calling Codacy API", e);
        }
    }
}

