package com.certplatform.payment.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.certplatform.payment.dto.QRPaymentRequest;
import com.certplatform.payment.dto.QRPaymentResponse;
import com.certplatform.payment.dto.UPIPaymentRequest;
import com.certplatform.payment.dto.UPIPaymentResponse;
import com.certplatform.payment.service.PaymentService;

import jakarta.validation.Valid;
import java.util.UUID;

/**
 * REST controller for handling student payment operations.
 */
@RestController
@RequestMapping("/api/student/payments")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Initiate UPI Payment.
     */
    @PostMapping("/initiate-upi")
    public ResponseEntity<UPIPaymentResponse> initiateUPIPayment(@Valid @RequestBody UPIPaymentRequest request) {
        log.info("[initiateUPIPayment] studentId={} upiId={} amount={}", request.getStudentId(), request.getUpiId(), request.getAmount());
        try {
            UPIPaymentResponse response = paymentService.initiateUPIPayment(
                    request.getStudentId(),
                    request.getUpiId(),
                    request.getAmount()
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            log.error("[initiateUPIPayment] Failed for studentId={}", request.getStudentId(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Initiate QR Payment.
     */
    @PostMapping("/initiate-qr")
    public ResponseEntity<QRPaymentResponse> initiateQRPayment(@Valid @RequestBody QRPaymentRequest request) {
        log.info("[initiateQRPayment] studentId={} amount={}", request.getStudentId(), request.getAmount());
        try {
            QRPaymentResponse response = paymentService.initiateQRPayment(
                    request.getStudentId(),
                    request.getAmount()
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            log.error("[initiateQRPayment] Failed for studentId={}", request.getStudentId(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Webhook callback from gateway.
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestParam UUID orderId,
                                                @RequestParam String gatewayTxnId,
                                                @RequestParam String status) {
        log.info("[handleWebhook] orderId={} gatewayTxnId={} status={}", orderId, gatewayTxnId, status);
        try {
            paymentService.handleUpiWebhook(orderId, gatewayTxnId, status);
            return ResponseEntity.ok("Webhook processed successfully");
        } catch (Exception ex) {
            log.error("[handleWebhook] Failed for orderId={}", orderId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook processing failed");
        }
    }

    /**
     * Check payment status.
     */
    @GetMapping("/status/{orderId}")
    public ResponseEntity<Boolean> checkStatus(@PathVariable UUID orderId) {
        log.info("[checkStatus] orderId={}", orderId);
        try {
            boolean paid = paymentService.isPaymentSuccessful(orderId);
            return ResponseEntity.ok(paid);
        } catch (Exception ex) {
            log.error("[checkStatus] Failed for orderId={}", orderId, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}
