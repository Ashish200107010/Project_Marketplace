package com.certplatform.review.service;

import org.springframework.stereotype.Service;

import com.certplatform.review.ReviewStatus;
import com.certplatform.review.entity.Review;
import com.certplatform.review.repository.ReviewRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review saveCompleted(UUID submissionId, String reviewJson, String fileUrl) {
        Review review = new Review();
        review.setSubmissionId(submissionId);
        review.setContent(reviewJson);
        review.setFileUrl(fileUrl);
        review.setStatus(ReviewStatus.COMPLETED);
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }
}
