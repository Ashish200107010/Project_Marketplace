package com.certplatform.project.controller;


import com.certplatform.project.dto.FileRequest;
import com.certplatform.project.dto.ProjectResponseDto;
import com.certplatform.project.entity.Project;
import com.certplatform.project.service.ProjectStudentService;
import com.certplatform.project.service.R2PresignedUrlService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/projects")
public class ProjectStudentController {

  private static final Logger log = LoggerFactory.getLogger(ProjectStudentController.class);

  private final ProjectStudentService projectStudentService;
  private final R2PresignedUrlService r2PresignedUrlService;

  public ProjectStudentController(ProjectStudentService projectStudentService, R2PresignedUrlService r2PresignedUrlService) {
    this.projectStudentService = projectStudentService;
    this.r2PresignedUrlService = r2PresignedUrlService;
  }

  @GetMapping("/available")
  public ResponseEntity<List<ProjectResponseDto>> getAvailableProjects() {
    log.info("[getAvailableProjects] Fetching available projects");
    List<ProjectResponseDto> projects = projectStudentService.getAvailableProjects();
    return ResponseEntity.ok(projects);
  }

  @GetMapping("/relevant")
  public ResponseEntity<List<ProjectResponseDto>> getRelevantProjects( @RequestParam(name = "roles", required = false) List<String> roles) {
    log.info("[getRelevantProjects] roles={}", roles);
    if (roles == null || roles.isEmpty()) {
      return ResponseEntity.ok(List.of());
    }
    List<ProjectResponseDto> projects = projectStudentService.getRelevantProjects(roles);
    return ResponseEntity.ok(projects);
  }


  @GetMapping("/enrolled")
  public ResponseEntity<List<Project>> getEnrolledProjects(
          @RequestParam(name = "userId") UUID userId) {
      log.info("[getEnrolledProjects] userId={}", userId);
      List<Project> projects = projectStudentService.getEnrolledProjectsByUserId(userId);
      return ResponseEntity.ok(projects);
  }

  @DeleteMapping("/unenroll")
  public ResponseEntity<String> unenrollProject(
          @RequestParam("userId") UUID userId,
          @RequestParam("projectId") UUID projectId) {
      log.info("[unenrollProject] userId={} projectId={}", userId, projectId);
      projectStudentService.unenrollProject(userId, projectId);
      return ResponseEntity.ok("Enrollment removed successfully");
      
  }


  @GetMapping("/completed")
  public ResponseEntity<List<Project>> getCompletedProjects(@RequestParam UUID userId) {
    log.info("[getCompletedProjects] userId={}", userId);
    List<Project> completedProjects = projectStudentService.getCompletedProjectsByUserId(userId);
    return ResponseEntity.ok(completedProjects);
  }

  @PostMapping("/enroll")
    public ResponseEntity<String> createNewEnrollment(
            @RequestParam(name = "userId") UUID userId,
            @RequestParam(name = "projectId") UUID projectId,
            @RequestParam(name = "startDate") LocalDate startDate) {

        log.info("[createNewEnrollment] userId={} projectId={} startDate={}", userId, projectId, startDate);
        try {
            projectStudentService.createEnrollment(userId, projectId, startDate);
            return ResponseEntity.ok("Enrollment created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("Enrollment failed: " + e.getMessage());
        }
    }
    
    @PostMapping("/files/access")
    public ResponseEntity<Map<String, String>> getFileAccessUrl(@RequestBody FileRequest request) {
      log.info("[getFileAccessUrl] projectId={}", request.getProjectId());
      try {
        int expiry = 300;
        String signedUrl = r2PresignedUrlService.generateSignedUrl(request.getProjectId(), expiry);
        Map<String, String> response = new HashMap<>(); response.put("accessUrl", signedUrl);
        return ResponseEntity.ok(response);
      } catch (Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Unable to generate secure access link");
        return ResponseEntity.status(500).body(error);
      }
    }

}

