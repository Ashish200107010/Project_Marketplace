package com.certplatform.job.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.certplatform.job.entity.Job;
import com.certplatform.job.enums.JobStatus;
import com.certplatform.job.repository.JobRepository;
import com.certplatform.project.repository.ProjectRepository;
import com.certplatform.submission.dto.JobQueue;
import com.certplatform.submission.dto.ReviewJob;
import com.certplatform.submission.entity.Submission;
import com.certplatform.submission.repository.SubmissionRepository;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class JobQueueService implements JobQueue {

    private static final Logger log = LoggerFactory.getLogger(JobQueueService.class);

    private final JobRepository jobRepository;
    private final SubmissionRepository submissionRepository;
    private final ProjectRepository projectRepository;

    @Override
    public void enqueue(ReviewJob reviewJob) {
        log.info("[enqueue] Request received for submissionId={}", reviewJob.getSubmissionId());
        try {
            // 1. Find submission
            Submission submission = submissionRepository.findById(reviewJob.getSubmissionId())
                    .orElseThrow(() -> {
                        log.warn("[enqueue] Invalid submissionId={}", reviewJob.getSubmissionId());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid submissionId");
                    });

            // 2. Extract projectId
            UUID projectId = submission.getProjectId();

            // 3. Find project
            projectRepository.findById(projectId)
                    .orElseThrow(() -> {
                        log.warn("[enqueue] Invalid projectId={}", projectId);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid projectId");
                    });

            // 4. Build Job entity
            Job job = new Job();
            job.setSubmissionId(submission.getId());
            job.setStatus(JobStatus.PENDING);
            job.setRetryCount(3);
            job.setCreatedAt(LocalDateTime.now());
            job.setUpdatedAt(LocalDateTime.now());

            // 5. Save job
            jobRepository.save(job);
            log.info("[enqueue] Job enqueued successfully for submissionId={} projectId={}", submission.getId(), projectId);

        } catch (ResponseStatusException ex) {
            throw ex; // bubble up business errors
        } catch (Exception ex) {
            log.error("[enqueue] Failed to enqueue job for submissionId={}", reviewJob.getSubmissionId(), ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to enqueue job", ex);
        }
    }

    @Override
    public List<Job> pollBatch(int batchSize) {
        log.info("[pollBatch] Request received with batchSize={}", batchSize);
        try {
            // 🔎 Validation
            if (batchSize <= 0) {
                log.warn("[pollBatch] Invalid batchSize={}", batchSize);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Batch size must be greater than zero");
            }

            // 🔎 Fetch jobs
            List<Job> jobs = jobRepository.findTop10ByStatusOrderByCreatedAtAsc(JobStatus.PENDING);

            if (jobs == null || jobs.isEmpty()) {
                log.info("[pollBatch] No pending jobs found");
            } else {
                log.info("[pollBatch] Retrieved {} pending jobs", jobs.size());
            }

            return jobs;

        } catch (ResponseStatusException ex) {
            throw ex; // bubble up business errors
        } catch (Exception ex) {
            log.error("[pollBatch] Error polling jobs batch (size={})", batchSize, ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to poll jobs", ex);
        }
    }
}
