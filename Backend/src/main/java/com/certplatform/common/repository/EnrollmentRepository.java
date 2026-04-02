package com.certplatform.common.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.certplatform.common.entity.Enrollment;

/**
 * Repository interface for Enrollment entity.
 * Provides CRUD operations and custom queries for student-project enrollments.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    /**
     * Find all enrollments for a given student.
     *
     * @param studentId the student's UUID
     * @return list of Enrollment records
     */
    List<Enrollment> findByStudentId(UUID studentId);

    /**
     * Delete an enrollment by studentId and projectId.
     *
     * @param studentId the student's UUID
     * @param projectId the project's UUID
     */
    void deleteByStudentIdAndProjectId(UUID studentId, UUID projectId);

    /**
     * Find a specific enrollment by studentId and projectId.
     *
     * @param studentId the student's UUID
     * @param projectId the project's UUID
     * @return Enrollment record if found, null otherwise
     */
    Enrollment findByStudentIdAndProjectId(UUID studentId, UUID projectId);
}
