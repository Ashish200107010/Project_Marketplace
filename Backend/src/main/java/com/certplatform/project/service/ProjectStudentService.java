package com.certplatform.project.service;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.certplatform.common.entity.Enrollment;
import com.certplatform.common.repository.CompletedProjectRepository;
import com.certplatform.common.repository.EnrollmentRepository;
import com.certplatform.project.dto.ProjectResponseDto;
import com.certplatform.project.entity.Project;
import com.certplatform.project.repository.ProjectStudentRepository;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class ProjectStudentService {

    private static final Logger log = LoggerFactory.getLogger(ProjectStudentService.class);

    private final ProjectStudentRepository projectStudentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CompletedProjectRepository completedProjectRepository;
    private final S3Presigner s3Presigner;

    private final String bucketName = "your-bucket-name";

    public ProjectStudentService(ProjectStudentRepository projectStudentRepository,
                                 EnrollmentRepository enrollmentRepository,
                                 CompletedProjectRepository completedProjectRepository,
                                 S3Presigner s3Presigner) {
        this.projectStudentRepository = projectStudentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.completedProjectRepository = completedProjectRepository;
        this.s3Presigner = s3Presigner;
    }

    public List<ProjectResponseDto> getAvailableProjects() {
        log.debug("[getAvailableProjects] Fetching all projects");
        try {
            List<Project> projects = projectStudentRepository.findAll();
            log.info("[getAvailableProjects] {} projects found", projects.size());

            List<ProjectResponseDto> responseList = new ArrayList<>();
            for (Project project : projects) {
                ProjectResponseDto dto = new ProjectResponseDto();
                dto.setId(project.getId());
                dto.setTitle(project.getTitle());
                dto.setDescription(project.getDescription());
                dto.setDifficulty(project.getDifficulty());
                dto.setDurationDays(project.getDurationDays());
                dto.setSkillsAssociated(project.getSkillsAssociated());
                dto.setRolesAssociated(project.getRolesAssociated());
                responseList.add(dto);
            }
            return responseList;
        } catch (Exception e) {
            log.error("[getAvailableProjects] Failed to fetch projects", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch projects", e);
        }
    }

    public List<ProjectResponseDto> getRelevantProjects(List<String> roles) {
        log.debug("[getRelevantProjects] Called with roles={}", roles);
        try {
            List<Project> projects = projectStudentRepository.findProjectsByRoles(roles);
            log.info("[getRelevantProjects] {} projects retrieved", projects.size());

            List<ProjectResponseDto> responseList = new ArrayList<>();
            for (Project project : projects) {
                ProjectResponseDto dto = new ProjectResponseDto();
                dto.setId(project.getId());
                dto.setTitle(project.getTitle());
                dto.setDescription(project.getDescription());
                dto.setDifficulty(project.getDifficulty());
                dto.setDurationDays(project.getDurationDays());
                dto.setSkillsAssociated(project.getSkillsAssociated());
                dto.setRolesAssociated(project.getRolesAssociated());
                responseList.add(dto);
            }
            return responseList;
        } catch (Exception e) {
            log.error("[getRelevantProjects] Failed for roles={}", roles, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch relevant projects", e);
        }
    }

    public List<Project> getEnrolledProjectsByUserId(UUID userId) {
        log.debug("[getEnrolledProjectsByUserId] Fetching enrollments for student {}", userId);
        try {
            List<Enrollment> enrollments = enrollmentRepository.findByStudentId(userId);
            List<UUID> projectIds = enrollments.stream().map(Enrollment::getProjectId).toList();
            return projectStudentRepository.findByIds(projectIds);
        } catch (Exception e) {
            log.error("[getEnrolledProjectsByUserId] Failed for userId={}", userId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch enrolled projects", e);
        }
    }

    @Transactional
    public Enrollment createEnrollment(UUID studentId, UUID projectId, LocalDate startDate) {
        log.info("[createEnrollment] Creating enrollment studentId={} projectId={}", studentId, projectId);
        try {
            Enrollment enrollment = new Enrollment();
            enrollment.setId(UUID.randomUUID());
            enrollment.setStudentId(studentId);
            enrollment.setProjectId(projectId);
            enrollment.setStartDate(startDate);
            enrollment.setCreatedAt(LocalDateTime.now());

            Enrollment saved = enrollmentRepository.save(enrollment);
            log.info("[createEnrollment] Enrollment saved id={}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("[createEnrollment] Failed studentId={} projectId={}", studentId, projectId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create enrollment", e);
        }
    }

    public List<Project> getCompletedProjectsByUserId(UUID userId) {
        log.debug("[getCompletedProjectsByUserId] Fetching completed projects for student {}", userId);
        try {
            return completedProjectRepository.findCompletedProjectsByUserId(userId);
        } catch (Exception e) {
            log.error("[getCompletedProjectsByUserId] Failed for userId={}", userId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch completed projects", e);
        }
    }

    @Transactional
    public void unenrollProject(UUID studentId, UUID projectId) {
        log.info("[unenrollProject] Unenrolling studentId={} projectId={}", studentId, projectId);
        try {
            enrollmentRepository.deleteByStudentIdAndProjectId(studentId, projectId);
            log.info("[unenrollProject] Enrollment removed studentId={} projectId={}", studentId, projectId);
        } catch (Exception e) {
            log.error("[unenrollProject] Failed studentId={} projectId={}", studentId, projectId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to unenroll project", e);
        }
    }

    public URL generateRequirementPdfUrl(UUID projectId) {
        log.info("[generateRequirementPdfUrl] Generating presigned URL for projectId={}", projectId);
        try {
            Project project = projectStudentRepository.findById(projectId)
                    .orElseThrow(() -> {
                        log.warn("[generateRequirementPdfUrl] Project not found id={}", projectId);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
                    });

            String key = project.getRequirementDocKey();
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(5))
                    .getObjectRequest(getObjectRequest)
                    .build();

            URL url = s3Presigner.presignGetObject(presignRequest).url();
            log.info("[generateRequirementPdfUrl] Presigned URL generated for projectId={}", projectId);
            return url;
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("[generateRequirementPdfUrl] Failed for projectId={}", projectId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate requirement PDF URL", e);
        }
    }
}
