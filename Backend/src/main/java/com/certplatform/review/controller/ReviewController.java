package com.certplatform.review.controller;

import com.certplatform.review.dto.ReviewResponse;
import com.certplatform.review.repository.ReviewRepository;
import com.certplatform.submission.entity.Submission;
import com.certplatform.submission.enums.SubmissionStatus;
import com.certplatform.submission.repository.SubmissionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/student/projects")
public class ReviewController {

    private static final Logger log = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewRepository reviewRepository;
    private final SubmissionRepository submissionRepository;

    public ReviewController(ReviewRepository reviewRepository, SubmissionRepository submissionRepository) {
        this.reviewRepository = reviewRepository;
        this.submissionRepository = submissionRepository;
    }

    @GetMapping("/{submissionId}/review")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable UUID submissionId) {
        log.info("[getReview] Fetching review for submissionId={}", submissionId);

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> {
                    log.warn("[getReview] Submission not found submissionId={}", submissionId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found");
                });

        ReviewResponse response = new ReviewResponse();
        response.setStatus(submission.getStatus());

        if (submission.getStatus() == SubmissionStatus.APPROVED || submission.getStatus() == SubmissionStatus.REJECTED) {
            reviewRepository.findTopBySubmissionIdOrderByCreatedAtDesc(submissionId)
                    .ifPresent(r -> {
                        response.setFileUrl(r.getFileUrl());
                        response.setContent(r.getContent());
                        log.info("[getReview] Review found for submissionId={} fileUrl={}", submissionId, r.getFileUrl());
                    });
        } else {
            log.info("[getReview] Submission status={} → no review attached", submission.getStatus());
        }

        return ResponseEntity.ok(response);
    }
}
