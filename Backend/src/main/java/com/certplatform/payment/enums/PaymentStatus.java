package com.certplatform.payment.enums;

/**
 * Enum representing the status of a payment order.
 */
public enum PaymentStatus {
    PENDING,   // Payment initiated but not yet completed
    SUCCESS,   // Payment completed successfully
    FAILED;    // Payment failed or was declined
}
