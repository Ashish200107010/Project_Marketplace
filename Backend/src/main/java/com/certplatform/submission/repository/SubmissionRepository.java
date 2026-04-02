package com.certplatform.submission.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.certplatform.submission.entity.Submission;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    Optional<Submission> findTopByStudentIdAndProjectIdOrderByCreatedAtDesc(UUID studentId, UUID projectId);
}

