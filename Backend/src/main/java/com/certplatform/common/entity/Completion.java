package com.certplatform.common.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import com.certplatform.project.entity.Project;

@Entity
@Table(name = "completion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Completion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
