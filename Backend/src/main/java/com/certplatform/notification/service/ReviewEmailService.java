package com.certplatform.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending review completion emails.
 */
@Service
public class ReviewEmailService {

    private static final Logger log = LoggerFactory.getLogger(ReviewEmailService.class);

    private final JavaMailSender mailSender;

    // @Value("${mail.from}")
    private String fromAddress = "ashish5691206@gmail.com";

    public ReviewEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends a review completion email to the student.
     *
     * @param to recipient email address
     * @param studentName student's name
     * @param projectName project name
     */
    public void sendReviewEmail(String to, String studentName, String projectName) {
        log.info("[sendReviewEmail] Preparing review email for student={} project={} recipient={}", studentName, projectName, to);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Review Completed for Your Submission");
            message.setText(prepareBody(studentName, projectName));
            message.setFrom(fromAddress);

            mailSender.send(message);
            log.info("[sendReviewEmail] Review email sent successfully to {}", to);

        } catch (MailException ex) {
            log.error("[sendReviewEmail] Failed to send review email to {}", to, ex);
            throw new RuntimeException("Failed to send review email", ex);
        }
    }

    /**
     * Prepares the body of the review email.
     */
    private String prepareBody(String studentName, String projectName) {
        return String.format(
            "Dear %s,\n\n" +
            "Your submission for the project \"%s\" has been reviewed. " +
            "Please find the attached review report.\n\n" +
            "Thank you for your work!",
            studentName, projectName
        );
    }
}
