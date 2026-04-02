package com.certplatform.certificate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.certplatform.certificate.entity.Certificate;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Certificate entity.
 * Provides CRUD operations and custom queries.
 */
@Repository
public interface CertificateRepository extends JpaRepository<Certificate, UUID> {

    /**
     * Find a certificate by studentId and projectId.
     *
     * @param studentId the student's UUID
     * @param projectId the project's UUID
     * @return Optional containing Certificate if found, empty otherwise
     */
    Optional<Certificate> findByStudentIdAndProjectId(UUID studentId, UUID projectId);
}
