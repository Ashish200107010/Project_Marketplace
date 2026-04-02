package com.certplatform.job.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.certplatform.job.enums.JobStatus;

@Getter
@Setter
@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private UUID id;

    @NotNull
    private UUID submissionId;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private int retryCount;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}

