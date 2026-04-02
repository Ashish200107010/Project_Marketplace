package com.certplatform.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.certplatform.project.enums.Difficulty;
import com.certplatform.project.enums.ReviewMode;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private UUID id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(name = "requirement_doc_key", nullable = false)
  private String requirementDocKey; // R2 key for requirement PDF

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Difficulty difficulty;

  @Column(nullable = false)
  private Integer durationDays;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReviewMode reviewMode;

  @ElementCollection
  @CollectionTable(name = "project_skills", joinColumns = @JoinColumn(name = "project_id"))
  @Column(name = "skill")
  private List<String> skillsAssociated = new ArrayList<>();

  @ElementCollection
  @CollectionTable(name = "project_roles", joinColumns = @JoinColumn(name = "project_id"))
  @Column(name = "role")
  private List<String> rolesAssociated = new ArrayList<>();

  @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

}
