package com.certplatform.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending certificate completion emails.
 */
@Service
public class CertificateEmailService {

    private static final Logger log = LoggerFactory.getLogger(CertificateEmailService.class);

    private final JavaMailSender mailSender;

    // @Value("${mail.from}")
    private String fromAddress = "ashish5691206@gmail.com";

    public CertificateEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a certificate completion email to the student.
     *
     * @param to recipient email address
     * @param studentName student's name
     * @param projectName project name
     */
    public void sendCertificateEmail(String to, String studentName, String projectName) {
        log.info("[sendCertificateEmail] Preparing email for student={} project={} recipient={}", studentName, projectName, to);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Certificate of Completion from Remotask.in");
            message.setText(prepareBody(studentName, projectName));
            message.setFrom(fromAddress);

            mailSender.send(message);
            log.info("[sendCertificateEmail] Email sent successfully to {}", to);

        } catch (MailException ex) {
            log.error("[sendCertificateEmail] Failed to send email to {}", to, ex);
            throw new RuntimeException("Failed to send certificate email", ex);
        }
    }

    /**
     * Prepares the body of the certificate email.
     */
    private String prepareBody(String studentName, String projectName) {
        return String.format(
            "Dear %s,\n\n" +
            "Congratulations! You have successfully completed the project internship \"%s\" at Remotask.in.\n\n" +
            "Please find your certificate attached.\n\n" +
            "Thank you for your hard work and dedication!",
            studentName, projectName
        );
    }
}
