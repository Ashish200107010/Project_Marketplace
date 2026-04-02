package com.certplatform.payment.repository;

import com.certplatform.payment.entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for PaymentOrder entity.
 * Provides CRUD operations and custom queries for payment orders.
 */
@Repository
public interface PaymentRepository extends JpaRepository<PaymentOrder, UUID> {

    /**
     * Find a payment order by its external orderId.
     *
     * @param orderId the unique order identifier
     * @return Optional containing PaymentOrder if found
     */
    Optional<PaymentOrder> findById(UUID orderId);

    /**
     * Check if a payment order exists by its orderId.
     *
     * @param orderId the unique order identifier
     * @return true if exists, false otherwise
     */
    boolean existsById(UUID orderId);
}
