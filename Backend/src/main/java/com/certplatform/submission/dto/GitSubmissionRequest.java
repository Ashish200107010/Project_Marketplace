package com.certplatform.submission.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GitSubmissionRequest {
    private UUID projectId;
    private UUID studentId;
    private String gitLink;
}
