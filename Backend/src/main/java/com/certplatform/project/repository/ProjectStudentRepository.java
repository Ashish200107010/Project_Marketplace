package com.certplatform.project.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.certplatform.project.entity.Project;
import com.certplatform.project.enums.Difficulty;

/**
 * Repository for student-facing project queries.
 */
@Repository
public interface ProjectStudentRepository extends JpaRepository<Project, UUID> {

    /**
     * Find projects by associated roles.
     *
     * @param roles list of role names
     * @return list of matching projects
     */
    @Query("SELECT DISTINCT p FROM Project p JOIN p.rolesAssociated r WHERE r IN :roles")
    List<Project> findProjectsByRoles(@Param("roles") List<String> roles);

    /**
     * Find projects by IDs.
     * Note: Spring Data already provides findAllById(ids).
     *
     * @param ids list of project IDs
     * @return list of matching projects
     */
    @Query("SELECT p FROM Project p WHERE p.id IN :ids")
    List<Project> findByIds(@Param("ids") List<UUID> ids);

    /**
     * Find project difficulty by project ID.
     *
     * @param projectId the project ID
     * @return the difficulty level of the project
     */ 
    @Query("SELECT p.difficulty FROM Project p WHERE p.id = :projectId")
    Difficulty findDifficultyByProjectId(@Param("projectId") UUID projectId);

}
