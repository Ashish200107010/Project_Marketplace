package com.certplatform.submission.controller;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.certplatform.submission.dto.GitSubmissionRequest;
import com.certplatform.submission.service.SubmissionService;

@RestController
@RequestMapping("/api/student/projects")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitRepo(@RequestBody GitSubmissionRequest request) {
        try {
            System.out.println("Received submission request: " + request);
            submissionService.createSubmission(
                request.getProjectId(),
                request.getStudentId(),
                request.getGitLink()
            );
            System.out.println("Submission processed successfully for request: " + request);
            return ResponseEntity.ok("Submission received!");
        } catch (IllegalArgumentException e) {
            // business validation failure (e.g. completion % too low)
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // unexpected errors
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                                .body("Submission failed: " + e.getMessage());
        }
    }

}