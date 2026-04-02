package com.certplatform.review.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.certplatform.review.ReviewStatus;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private UUID id;

    private UUID submissionId;

    @Column(columnDefinition = "TEXT")
    private String content; // raw JSON/text from Codacy

    private String fileUrl;  // presigned PDF URL

    @Enumerated(EnumType.STRING)
    private ReviewStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // getters/setters
}
