package org.example.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JavaMailSender mailSender;

    public void sendStatusChangeNotification(Long taskId, String newStatus) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("user@example.com"); // Замените на реальный email
        message.setSubject("Task Status Updated");
        message.setText(String.format(
                "Task #%d status changed:\nTo: %s",
                taskId, newStatus
        ));

        mailSender.send(message);
        System.out.println("Notification email sent for task: " + taskId);
    }
}
