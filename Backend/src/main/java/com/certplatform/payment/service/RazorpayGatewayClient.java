package com.certplatform.payment.service;

import com.certplatform.payment.entity.PaymentOrder;
import com.certplatform.payment.interfaces.PaymentGatewayClient;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;

import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

import org.slf4j.Logger;
import com.razorpay.Order;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RazorpayGatewayClient implements PaymentGatewayClient {

    private static final Logger log = LoggerFactory.getLogger(RazorpayGatewayClient.class);
    private final RazorpayClient client;


    public RazorpayGatewayClient( @Value("${razorpay.key}") String key, @Value("${razorpay.secret}") String secret ) throws Exception {
        if (key == null || key.isBlank() || secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("Razorpay credentials are missing");
        } 
        this.client = new RazorpayClient(key, secret);
    }

    @Override
    public String generateQr(PaymentOrder order) {
        try {
            JSONObject request = new JSONObject();
            // Amount in paise (₹299 → 29900)
            request.put("amount", order.getAmount().multiply(new BigDecimal(100)));
            request.put("currency", "INR");
            request.put("description", "Platform Maintenance Fee");

            //Ashish needs to work here
            // Optional: customer details (helps Razorpay show name/email on checkout)
            // JSONObject customer = new JSONObject();
            // customer.put("name", order.getStudentName());
            // customer.put("email", order.getStudentEmail());
            // request.put("customer", customer);

            // Notifications (SMS/Email)
            JSONObject notify = new JSONObject();
            notify.put("sms", Boolean.TRUE);
            notify.put("email", Boolean.TRUE);
            request.put("notify", notify);

            // Callback URL (where Razorpay redirects after payment)
            request.put("callback_url", "https://yourdomain.com/payment/callback");
            request.put("callback_method", "get");

            // Create Payment Link
            PaymentLink paymentLink = client.paymentLink.create(request);

            // Razorpay returns a working short URL
            return paymentLink.get("short_url");
        } catch (Exception e) {
            log.error("[generateQr] Failed to generate QR payment link for orderId={}", order.getId(), e);
            throw new RuntimeException("Failed to generate Payment Link {QR}", e);
        }
    }



    @Override
    public String generateUPICollect(PaymentOrder order, String upiId) {
        try {
            log.info("[generateUPICollect] Initiating UPI payment for orderId={} upiId={}", order.getId(), upiId);            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", order.getAmount().multiply(new BigDecimal(100)));
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", order.getId().toString());
            orderRequest.put("payment_capture", 1);

            log.info("[generateUPICollect] Creating Razorpay Order with request: {}", orderRequest.toString());
            Order razorpayOrder = client.orders.create(orderRequest);

            log.info("[generateUPICollect] Razorpay Order created: {}", razorpayOrder.toString());
            JSONObject paymentRequest = new JSONObject();
            paymentRequest.put("amount", order.getAmount().multiply(new BigDecimal(100)));
            paymentRequest.put("currency", "INR");
            paymentRequest.put("order_id", razorpayOrder.get("id").toString());
            paymentRequest.put("method", "upi");

            log.info("[generateUPICollect] Using UPI ID: {}", upiId);
            JSONObject upi = new JSONObject();
            upi.put("vpa", upiId);
            paymentRequest.put("upi", upi);

            log.info("[generateUPICollect] Payment Request: {}", paymentRequest.toString());
            Order payment = client.orders.create(paymentRequest);

            return payment.get("id"); // Razorpay payment ID
        } catch (Exception e) {
            log.error("[generateUPICollect] Failed for orderId={}", order.getId(), e);
            throw new RuntimeException("Failed to initiate UPI payment", e);
        }
    }





    @Override
    public boolean verifyWebhookSignature(String payload, String signature) {
        // Razorpay provides utility to verify webhook signatures
        try {
            return com.razorpay.Utils.verifyWebhookSignature(payload, signature, "RAZORPAY_SECRET");
        } catch (Exception e) {
            log.error("[verifyWebhookSignature] Signature verification failed", e); 
            return false;
        }
    }
}

