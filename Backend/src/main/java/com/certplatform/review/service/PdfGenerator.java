package com.certplatform.review.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

// Stub: replace with iText/PDFBox implementation
@Service
public class PdfGenerator {

    public byte[] generateFromJson(String reviewJson, String repoUrl, String projectName) {
        // Minimal placeholder: convert JSON to bytes; replace with real PDF rendering
        String content = String.format(
            "Certify.io Review Report\n\n" +
            "Project: %s\n" +
            "Repository: %s\n\n" +
            "Codacy Review (JSON):\n%s",
            projectName, repoUrl, reviewJson
        );
        return content.getBytes(StandardCharsets.UTF_8);
    }
}
