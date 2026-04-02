package com.certplatform.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.certplatform.payment.dto.QRPaymentResponse;
import com.certplatform.payment.dto.UPIPaymentResponse;
import com.certplatform.payment.entity.PaymentOrder;
import com.certplatform.payment.entity.TransactionRecord;
import com.certplatform.payment.enums.PaymentStatus;
import com.certplatform.payment.interfaces.PaymentGatewayClient;
import com.certplatform.payment.repository.PaymentRepository;
import com.certplatform.payment.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service layer for handling payment operations.
 */
@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepo;
    private final TransactionRepository transactionRepo;
    private final PaymentGatewayClient gatewayClient;

    public PaymentService(PaymentRepository paymentRepo,
                          TransactionRepository transactionRepo,
                          PaymentGatewayClient gatewayClient) {
        this.paymentRepo = paymentRepo;
        this.transactionRepo = transactionRepo;
        this.gatewayClient = gatewayClient;
    }

    @Transactional
    public UPIPaymentResponse initiateUPIPayment(UUID studentId, String upiId, BigDecimal amount) {
        log.info("[initiateUPIPayment] studentId={} upiId={} amount={}", studentId, upiId, amount);

        try {
            PaymentOrder order = new PaymentOrder();
            order.setStudentId(studentId);
            order.setAmount(amount);
            order.setStatus(PaymentStatus.PENDING);
            paymentRepo.save(order);

            String paymentLink = gatewayClient.generateUPICollect(order, upiId);
            log.info("[initiateUPIPayment] Payment order={} created successfully", order.getId());

            return new UPIPaymentResponse(paymentLink, order.getId().toString());
        } catch (Exception ex) {
            log.error("[initiateUPIPayment] Failed for studentId={}", studentId, ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to initiate UPI payment", ex);
        }
    }

    @Transactional
    public QRPaymentResponse initiateQRPayment(UUID studentId, BigDecimal amount) {
        log.info("[initiateQRPayment] studentId={} amount={}", studentId, amount);

        try {
            PaymentOrder order = new PaymentOrder();
            order.setStudentId(studentId);
            order.setAmount(amount);
            order.setStatus(PaymentStatus.PENDING);
            paymentRepo.save(order);

            String qrCodeUrl = gatewayClient.generateQr(order);
            log.info("[initiateQRPayment] Payment order={} created successfully", order.getId());

            return new QRPaymentResponse(qrCodeUrl, order.getId().toString());
        } catch (Exception ex) {
            log.error("[initiateQRPayment] Failed for studentId={}", studentId, ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to initiate QR payment", ex);
        }
    }

    @Transactional
    public void handleUpiWebhook(UUID orderId, String gatewayTxnId, String status) {
        log.info("[handleUpiWebhook] orderId={} gatewayTxnId={} status={}", orderId, gatewayTxnId, status);

        PaymentOrder order = paymentRepo.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("[handleUpiWebhook] Order not found orderId={}", orderId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
                });

        PaymentStatus newStatus;
        try {
            newStatus = PaymentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            log.error("[handleUpiWebhook] Unknown status={} for orderId={}", status, orderId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payment status: " + status);
        }

        if (newStatus == PaymentStatus.SUCCESS) {
            order.setStatus(PaymentStatus.SUCCESS);
            order.setUpdatedAt(LocalDateTime.now());
            paymentRepo.save(order);

            TransactionRecord record = new TransactionRecord();
            record.setOrderId(order.getId());
            record.setStudentId(order.getStudentId());
            record.setGatewayTxnId(gatewayTxnId);
            record.setCreatedAt(LocalDateTime.now());

            transactionRepo.save(record);
            log.info("[handleUpiWebhook] Transaction recorded for orderId={}", orderId);

            // TODO: enroll student atomically here
        } else {
            order.setStatus(PaymentStatus.FAILED);
            order.setUpdatedAt(LocalDateTime.now());
            paymentRepo.save(order);
            log.info("[handleUpiWebhook] Order={} marked FAILED", orderId);
        }
    }

    public boolean isPaymentSuccessful(UUID orderId) {
        log.info("[isPaymentSuccessful] Checking status for orderId={}", orderId);
        return paymentRepo.findById(orderId)
                .map(order -> PaymentStatus.SUCCESS.equals(order.getStatus()))
                .orElse(false);
    }

}

