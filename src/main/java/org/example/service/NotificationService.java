package org.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final JavaMailSender mailSender;
    private final String recipientEmail;

    public NotificationService(JavaMailSender mailSender,
                               @Value("${notification.email.recipient}") String recipientEmail) {
        this.mailSender = mailSender;
        this.recipientEmail = recipientEmail;
    }

    public void sendStatusChangeNotification(Long taskId, String newStatus) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject("Task Status Updated");
        message.setText(String.format(
                "Task #%d status changed:\nTo: %s",
                taskId, newStatus
        ));
        mailSender.send(message);
    }
}
