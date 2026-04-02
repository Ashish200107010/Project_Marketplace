package com.certplatform.review.dto;

import com.certplatform.submission.enums.SubmissionStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private SubmissionStatus status;
    private String fileUrl;
    private String content;
}

