package com.certplatform.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending OTP emails during registration.
 */
@Service
public class OtpEmailService {

    private static final Logger log = LoggerFactory.getLogger(OtpEmailService.class);

    private final JavaMailSender mailSender;

    // @Value("${mail.from}")
    private String fromAddress = "ashish5691206@gmail.com";

    public OtpEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an OTP email to the recipient.
     *
     * @param to recipient email address
     * @param otp one-time password
     */
    public void sendOtpEmail(String to, String otp) {
        log.info("[sendOtpEmail] Preparing OTP email for recipient={}", to);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Your OTP for CertPlatform Registration");
            message.setText(prepareBody(otp));
            message.setFrom(fromAddress);

            mailSender.send(message);
            log.info("[sendOtpEmail] OTP email sent successfully to {}", to);

        } catch (MailException ex) {
            log.error("[sendOtpEmail] Failed to send OTP email to {}", to, ex);
            throw new RuntimeException("Failed to send OTP email", ex);
        }
    }

    /**
     * Prepares the body of the OTP email.
     */
    private String prepareBody(String otp) {
        return String.format(
            "Your OTP is: %s%nIt will expire in 5 minutes.",
            otp
        );
    }
}
