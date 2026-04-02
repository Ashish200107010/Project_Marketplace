package com.certplatform.admin.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.certplatform.admin.entity.Admin;

/**
 * Repository interface for Admin entity.
 * Provides CRUD operations and custom queries.
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {

    /**
     * Find an admin by email address.
     * @param email the admin's email
     * @return Optional containing Admin if found, empty otherwise
     */
    Optional<Admin> findByEmail(String email);
}
