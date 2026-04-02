package com.certplatform.payment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.certplatform.payment.enums.PaymentStatus;

/**
 * Entity representing a payment order.
 */
@Entity
@Table(name = "payment_orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Link to student (foreign key reference).
     * Using @Column instead of @JoinColumn since this is not a JPA relationship.
     */
    @Column(name = "student_id", nullable = false, updatable = false)
    private UUID studentId;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
