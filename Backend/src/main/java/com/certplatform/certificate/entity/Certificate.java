package com.certplatform.certificate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // ✅ Link to student
    @JoinColumn(name = "student_id", nullable = false)
    private UUID studentId;

    // ✅ Link to project
    @JoinColumn(name = "project_id", nullable = false)
    private UUID projectId;

    // ✅ Cloudinary path or public_id
    @Column(name = "certificate_path_key", nullable = false)
    private String certificatePathKey;

    // ✅ Creation date
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}

