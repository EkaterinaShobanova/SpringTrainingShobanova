package org.example.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void sendStatusChangeNotification_ShouldSendEmail() {
        Long taskId = 1L;
        String newStatus = "COMPLETED";

        try {
            var field = NotificationService.class.getDeclaredField("recipientEmail");
            field.setAccessible(true);
            field.set(notificationService, "test@example.com");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        notificationService.sendStatusChangeNotification(taskId, newStatus);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}