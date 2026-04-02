package com.certplatform.certificate.dto;

import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * Request DTO for certificate operations.
 * Contains identifiers for student and project.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateRequest {

    @NotNull(message = "StudentId must not be null")
    private UUID studentId;

    @NotNull(message = "ProjectId must not be null")
    private UUID projectId;
}
