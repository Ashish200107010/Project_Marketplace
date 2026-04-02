package com.certplatform.payment.interfaces;

import com.certplatform.payment.entity.PaymentOrder;

/**
 * Contract for integrating with external payment gateways.
 * Provides methods for generating payment requests and verifying webhook signatures.
 */
public interface PaymentGatewayClient {

    /**
     * Generate a QR code payment request for the given order.
     *
     * @param order the payment order details
     * @return QR code URL or encoded string
     */
    String generateQr(PaymentOrder order);

    /**
     * Generate a UPI collect request for the given order.
     *
     * @param order the payment order details
     * @param upiId the customer's UPI ID
     * @return UPI payment request URL or transaction reference
     */
    String generateUPICollect(PaymentOrder order, String upiId);

    /**
     * Verify the webhook signature received from the payment gateway.
     *
     * @param payload   the raw webhook payload
     * @param signature the signature provided by the gateway
     * @return true if signature is valid, false otherwise
     */
    boolean verifyWebhookSignature(String payload, String signature);
}
