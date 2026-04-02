package com.certplatform.common.repository;

import com.certplatform.common.entity.Completion;
import com.certplatform.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Completion entity.
 * Provides CRUD operations and custom queries for completed projects.
 */
@Repository
public interface CompletedProjectRepository extends JpaRepository<Completion, UUID> {

    /**
     * Find all projects completed by a given student.
     *
     * @param userId the student's UUID
     * @return list of completed Project entities
     */
    @Query("SELECT c.project FROM Completion c WHERE c.studentId = :userId")
    List<Project> findCompletedProjectsByUserId(@Param("userId") UUID userId);
}
