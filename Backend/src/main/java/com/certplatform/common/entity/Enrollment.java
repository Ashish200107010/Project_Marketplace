package com.certplatform.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
