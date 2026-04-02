package com.certplatform.review.service;

import com.certplatform.job.entity.Job;
import com.certplatform.job.enums.JobStatus;
import com.certplatform.job.repository.JobRepository;
import com.certplatform.project.entity.Project;
import com.certplatform.project.repository.ProjectRepository;
import com.certplatform.project.service.R2PresignedUrlService;
import com.certplatform.submission.entity.Submission;
import com.certplatform.submission.enums.SubmissionStatus;
import com.certplatform.submission.repository.SubmissionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class ReviewWorker {

    private final JobRepository jobRepository;
    private final SubmissionRepository submissionRepository;
    private final ProjectRepository projectRepository;
    private final R2PresignedUrlService r2PresignedUrlService;
    private final CodacyClient codacyClient;
    private final PdfGenerator pdfGenerator;
    private final ReviewService reviewService;

    @Scheduled(fixedDelay = 60000) // every 1 min
    public void processJobs() {
        List<Job> jobs = jobRepository.findTop10ByStatusOrderByCreatedAtAsc(JobStatus.PENDING);

        for (Job job : jobs) {
            job.setStatus(JobStatus.IN_PROGRESS);
            jobRepository.save(job);

            try {
                Submission submission = submissionRepository.findById(job.getSubmissionId()).orElseThrow();
                Project project = projectRepository.findById(submission.getProjectId()).orElseThrow();

                // Requirements PDF (if Codacy needs it)
                String requirementsPdfUrl = r2PresignedUrlService.generateSignedUrl(submission.getProjectId(), 3600);

                // Fetch review JSON from Codacy
                String reviewJson = codacyClient.fetchReviewJson(submission.getGitLink(), requirementsPdfUrl);

                // Generate review PDF
                byte[] pdfBytes = pdfGenerator.generateFromJson(
                    reviewJson,
                    submission.getGitLink(),
                    project.getTitle()
                );

                // Upload and presign review PDF
                String reviewPdfUrl = r2PresignedUrlService.uploadAndPresignReviewPdf(submission.getId(), pdfBytes, 3600);

                // Save review record
                reviewService.saveCompleted(submission.getId(), reviewJson, reviewPdfUrl);

                // Update submission status
                boolean b = checkIfPassedOrNot(reviewJson, project.getDifficulty().toString());
                if(b){
                    submission.setStatus(SubmissionStatus.APPROVED);
                } else {
                    submission.setStatus(SubmissionStatus.REJECTED);
                }
                submissionRepository.save(submission);

                job.setStatus(JobStatus.COMPLETED);
            } catch (Exception e) {
                job.setStatus(JobStatus.FAILED);
                job.setRetryCount(job.getRetryCount() + 1);
            }

            jobRepository.save(job);
        }
    
    }

    private boolean checkIfPassedOrNot(String reviewJson, String difficulty) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(reviewJson);

            // Extract properties
            String grade = root.path("grade").asText();
            int coverage = root.path("coverage").asInt();
            int errorCount = root.path("errorCount").asInt();

            boolean passed = false;

            if ("EASY".equalsIgnoreCase(difficulty)) {
                // Easy: lenient thresholds
                passed = (grade.compareTo("D") <= 0)   // A–D allowed
                    && (coverage >= 30);              // at least 30% coverage
            } else if ("MEDIUM".equalsIgnoreCase(difficulty)) {
                // Medium: moderate thresholds
                passed = (grade.compareTo("C") <= 0)   // A–C allowed
                    && (coverage >=50)              // at least 50% coverage
                    && (errorCount <= 7);            // up to 7 errors
            } else if ("HARD".equalsIgnoreCase(difficulty)) {
                // Hard: strict thresholds
                passed = (grade.compareTo("B") <= 0)   // A–B only
                    && (coverage >= 75)              // at least 75% coverage
                    && (errorCount <= 4);            // max  4 errors
            } else {
                throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
            }

            return passed;

        } catch (Exception e) {
            // If JSON parsing fails, treat as fail
            System.out.println("Failed to parse reviewJson");
            return false;
        }
    }

}
