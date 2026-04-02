package com.certplatform.project.repository;

import com.certplatform.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository interface for Project entity.
 * Provides CRUD operations and custom queries for projects.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

}
