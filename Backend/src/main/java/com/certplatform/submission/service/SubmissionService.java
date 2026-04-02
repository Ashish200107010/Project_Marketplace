package com.certplatform.submission.service;

import org.springframework.stereotype.Service;

import com.certplatform.submission.dto.JobQueue;
import com.certplatform.submission.dto.ReviewJob;
import com.certplatform.submission.entity.Submission;
import com.certplatform.submission.enums.SubmissionStatus;
import com.certplatform.submission.repository.SubmissionRepository;

import java.time.Instant;
import java.util.UUID;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final JobQueue jobQueue;

    public SubmissionService(SubmissionRepository submissionRepository, JobQueue jobQueue) {
        this.submissionRepository = submissionRepository;
        this.jobQueue = jobQueue;
    }

    public Submission createSubmission(UUID projectId, UUID studentId, String gitLink) {
        try {
            // ✅ Basic validation
            if (projectId == null || studentId == null || gitLink == null || gitLink.isBlank()) {
                throw new IllegalArgumentException("Project ID, Student ID, and Git link are required.");
            }

            // ✅ Build submission
            Submission submission = new Submission();
            submission.setProjectId(projectId);
            submission.setStudentId(studentId);
            submission.setGitLink(gitLink);
            submission.setStatus(SubmissionStatus.PENDING);
            submission.setCreatedAt(
                Instant.now().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
            );

            // ✅ Save submission
            Submission saved = submissionRepository.save(submission);

            // ✅ Enqueue job
            jobQueue.enqueue(new ReviewJob(saved.getId()));

            return saved;

        } catch (IllegalArgumentException e) {
            // Business validation failure
            System.out.println("Submission validation failed: {} " + e.getMessage());
            throw e; // bubble up as 400 Bad Request in controller
        } catch (Exception e) {
            // Unexpected system error
            System.out.println("Error creating submission" + e.getMessage());
            throw new RuntimeException("Submission failed due to internal error");
        }
    }

}


