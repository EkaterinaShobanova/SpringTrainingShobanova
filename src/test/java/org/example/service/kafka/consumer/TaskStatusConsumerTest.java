package org.example.service.kafka.consumer;

import org.example.dto.TaskStatusUpdateDto;
import org.example.entity.TaskStatus;
import org.example.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskStatusConsumerTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private TaskStatusConsumer taskStatusConsumer;

    @Test
    void handleTaskUpdate_ShouldProcessSuccessfully() {
        TaskStatusUpdateDto update = new TaskStatusUpdateDto(1L, TaskStatus.COMPLETED.toString());

        taskStatusConsumer.handleTaskUpdate(update, acknowledgment);

        verify(notificationService).sendStatusChangeNotification(1L, TaskStatus.COMPLETED.toString());
        verify(acknowledgment).acknowledge();
        verifyNoMoreInteractions(notificationService, acknowledgment);
    }

    @Test
    void handleTaskUpdate_ShouldLogErrorWhenNotificationFails() {
        TaskStatusUpdateDto update = new TaskStatusUpdateDto(2L, TaskStatus.FAILED.toString());
        doThrow(new RuntimeException("Notification failed"))
                .when(notificationService)
                .sendStatusChangeNotification(any(), any());

        taskStatusConsumer.handleTaskUpdate(update, acknowledgment);

        verify(notificationService).sendStatusChangeNotification(2L, TaskStatus.FAILED.toString());
        verify(acknowledgment, never()).acknowledge();
    }
}
