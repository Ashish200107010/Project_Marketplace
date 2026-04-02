package com.certplatform.project.controller;

import com.certplatform.project.dto.ProjectDTO;
import com.certplatform.project.entity.Project;
import com.certplatform.project.enums.Difficulty;
import com.certplatform.project.enums.ReviewMode;
import com.certplatform.project.service.ProjectService;
import com.certplatform.project.service.R2UploadService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Admin controller for managing projects.
 */
@RestController
@RequestMapping("/admin/projects")
public class ProjectAdminController {

    private static final Logger log = LoggerFactory.getLogger(ProjectAdminController.class);

    private final ProjectService projectService;
    private final R2UploadService r2UploadService;

    public ProjectAdminController(ProjectService projectService, R2UploadService r2UploadService) {
        this.projectService = projectService;
        this.r2UploadService = r2UploadService;
    }

    @PostMapping("/create-with-file")
    public ResponseEntity<Project> createProjectWithFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("difficulty") Difficulty difficulty,
            @RequestParam("duration_days") Integer durationDays,
            @RequestParam("review_mode") ReviewMode reviewMode,
            @RequestParam("skills_associated") List<String> skillsAssociated,
            @RequestParam("roles_associated") List<String> rolesAssociated
    ) {
        log.info("[createProjectWithFile] Creating project title={} difficulty={}", title, difficulty);
        try {
            String key = r2UploadService.uploadProjectRequirementDoc(file);

            ProjectDTO project = new ProjectDTO();
            project.setTitle(title);
            project.setDescription(description);
            project.setRequirementDocKey(key);
            project.setDifficulty(difficulty);
            project.setDurationDays(durationDays);
            project.setReviewMode(reviewMode);
            project.setSkillsAssociated(skillsAssociated);
            project.setRolesAssociated(rolesAssociated);

            Project saved = projectService.createProject(project);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception ex) {
            log.error("[createProjectWithFile] Failed to upload requirement doc", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<Project>> getAllProjects() {
        log.info("[getAllProjects] Listing all projects");
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateProject(@PathVariable("id") UUID id, @Valid @RequestBody Project proj) {
        log.info("[updateProject] Updating project id={}", id);
        proj.setId(id);
        projectService.updateProject(proj);
        return ResponseEntity.ok(Map.of("message", "Project updated successfully"));
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable("id") UUID id) {
        log.info("[deleteProject] Deleting project id={}", id);
        projectService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
