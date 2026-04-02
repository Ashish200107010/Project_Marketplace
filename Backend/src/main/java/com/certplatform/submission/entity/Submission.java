package com.certplatform.submission.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

import com.certplatform.submission.enums.SubmissionStatus;

@Getter
@Setter
@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "git_link", nullable = false)
    private String gitLink; // GitHub or file link

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubmissionStatus status;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // optional notes/feedback URL

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Getters and setters
}
