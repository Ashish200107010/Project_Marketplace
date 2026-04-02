package com.certplatform.payment.repository;

import com.certplatform.payment.entity.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for TransactionRecord entity.
 * Provides CRUD operations and custom queries for payment transactions.
 */
@Repository
public interface TransactionRepository extends JpaRepository<TransactionRecord, UUID> {

    /**
     * Check if at least one transaction exists for a given student.
     *
     * @param studentId the student's UUID
     * @return true if at least one transaction exists
     */
    boolean existsByStudentId(UUID studentId);

    /**
     * Find all transactions for a given student.
     *
     * @param studentId the student's UUID
     * @return list of TransactionRecord entities
     */
    List<TransactionRecord> findByStudentId(UUID studentId);

    /**
     * Find a transaction by gateway transaction ID.
     *
     * @param gatewayTxnId the external payment gateway transaction ID
     * @return Optional containing TransactionRecord if found
     */
    Optional<TransactionRecord> findByGatewayTxnId(String gatewayTxnId);
}
