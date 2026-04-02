package com.certplatform.payment.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // ✅ Link to student
    @JoinColumn(name = "order_id", nullable = false)
    private UUID orderId;

    // ✅ Link to student
    @JoinColumn(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "gateway_txn_id", nullable = false)
    private String gatewayTxnId;

    // ✅ Creation date
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // constructors, getters, setters
}