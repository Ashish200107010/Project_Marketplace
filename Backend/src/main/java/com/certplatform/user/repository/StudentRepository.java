package com.certplatform.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.certplatform.user.entity.Student;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    Optional<Student> findByEmail(String email);
    Optional<Student> findById(UUID id);
}

