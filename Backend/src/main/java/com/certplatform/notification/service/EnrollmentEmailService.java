package com.certplatform.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending enrollment confirmation emails.
 */
@Service
public class EnrollmentEmailService {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentEmailService.class);

    private final JavaMailSender mailSender;

        // @Value("${mail.from}")
    private String fromAddress = "ashish5691206@gmail.com";

    public EnrollmentEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an enrollment confirmation email to the student.
     *
     * @param to recipient email address
     * @param studentName student's name
     * @param projectName project name
     */
    public void sendEnrollmentEmail(String to, String studentName, String projectName) {
        log.info("[sendEnrollmentEmail] Preparing email for student={} project={} recipient={}", studentName, projectName, to);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Appointment Confirmation at Remotask.in");
            message.setText(prepareBody(studentName, projectName));
            message.setFrom(fromAddress);

            mailSender.send(message);
            log.info("[sendEnrollmentEmail] Email sent successfully to {}", to);

        } catch (MailException ex) {
            log.error("[sendEnrollmentEmail] Failed to send email to {}", to, ex);
            throw new RuntimeException("Failed to send enrollment email", ex);
        }
    }

    /**
     * Prepares the body of the enrollment email.
     */
    private String prepareBody(String studentName, String projectName) {
        return String.format(
            "Dear %s,\n\n" +
            "You are successfully appointed as the project intern for the project \"%s\" at Remotask.in.\n\n" +
            "Thank you for choosing Remotask.in!",
            studentName, projectName
        );
    }
}
