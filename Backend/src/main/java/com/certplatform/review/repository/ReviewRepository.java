package com.certplatform.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.certplatform.review.entity.Review;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for accessing Review entities.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    /**
     * Find the most recent review for a given submission.
     *
     * @param submissionId the submission UUID
     * @return optional containing the latest review if present
     */
    Optional<Review> findTopBySubmissionIdOrderByCreatedAtDesc(UUID submissionId);

    /**
     * Find all reviews for a submission, ordered by creation time descending.
     *
     * @param submissionId the submission UUID
     * @return list of reviews sorted by newest first
     */
    List<Review> findBySubmissionIdOrderByCreatedAtDesc(UUID submissionId);
}
