package com.certplatform.project.service;

import com.certplatform.project.dto.ProjectDTO;
import com.certplatform.project.entity.Project;
import com.certplatform.project.repository.ProjectRepository;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Project createProject(ProjectDTO dto) {
        log.info("[createProject] Attempting to create project title={}", dto.getTitle());
        try {
            Project project = new Project();
            project.setTitle(dto.getTitle());
            project.setRequirementDocKey(dto.getRequirementDocKey());
            project.setDescription(dto.getDescription());
            project.setDifficulty(dto.getDifficulty());
            project.setDurationDays(dto.getDurationDays());
            project.setReviewMode(dto.getReviewMode());
            project.setSkillsAssociated(dto.getSkillsAssociated());
            project.setRolesAssociated(dto.getRolesAssociated());

            Project saved = projectRepository.save(project);
            log.info("[createProject] Project created successfully id={}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("[createProject] Failed to create project title={}", dto.getTitle(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create project", e);
        }
    }

    public List<Project> getAllProjects() {
        log.info("[getAllProjects] Fetching all projects");
        try {
            return projectRepository.findAll();
        } catch (Exception e) {
            log.error("[getAllProjects] Failed to fetch projects", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch projects", e);
        }
    }

    @Transactional
    public Project updateProject(Project dto) {
        log.info("[updateProject] Attempting to update project id={}", dto.getId());
        try {
            Project project = projectRepository.findById(dto.getId())
                    .orElseThrow(() -> {
                        log.warn("[updateProject] Project not found id={}", dto.getId());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
                    });

            project.setTitle(dto.getTitle());
            project.setDescription(dto.getDescription());
            project.setRequirementDocKey(dto.getRequirementDocKey());
            project.setDifficulty(dto.getDifficulty());
            project.setDurationDays(dto.getDurationDays());
            project.setReviewMode(dto.getReviewMode());
            project.setSkillsAssociated(dto.getSkillsAssociated());
            project.setRolesAssociated(dto.getRolesAssociated());

            Project updated = projectRepository.save(project);
            log.info("[updateProject] Project updated successfully id={}", updated.getId());
            return updated;
        } catch (ResponseStatusException ex) {
            throw ex; // already logged above
        } catch (Exception e) {
            log.error("[updateProject] Failed to update project id={}", dto.getId(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update project", e);
        }
    }

    @Transactional
    public void deleteById(UUID id) {
        log.info("[deleteById] Attempting to delete project id={}", id);
        try {
            if (!projectRepository.existsById(id)) {
                log.warn("[deleteById] Project not found id={}", id);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
            }
            projectRepository.deleteById(id);
            log.info("[deleteById] Project deleted successfully id={}", id);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("[deleteById] Failed to delete project id={}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete project", e);
        }
    }

    @Transactional
    public void updateRequirementDocKey(UUID projectId, String key) {
        log.info("[updateRequirementDocKey] Attempting to update requirement doc for projectId={}", projectId);
        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> {
                        log.warn("[updateRequirementDocKey] Project not found id={}", projectId);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found");
                    });
            project.setRequirementDocKey(key);
            projectRepository.save(project);
            log.info("[updateRequirementDocKey] Requirement doc updated successfully for projectId={}", projectId);
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception e) {
            log.error("[updateRequirementDocKey] Failed to update requirement doc for projectId={}", projectId, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update requirement doc", e);
        }
    }
}
