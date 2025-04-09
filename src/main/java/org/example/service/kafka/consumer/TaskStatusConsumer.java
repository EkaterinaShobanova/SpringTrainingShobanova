package org.example.service.kafka.consumer;
import org.example.dto.TaskStatusUpdateDto;
import org.example.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;

@Service
public class TaskStatusConsumer {
    private final NotificationService notificationService;

    public TaskStatusConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "task-status-updates", groupId = "notification-group")
    public void handleTaskUpdate(TaskStatusUpdateDto update) {
        System.out.printf("Received update: taskId=%d, %s%n",
                update.taskId(),  update.newStatus());

        notificationService.sendStatusChangeNotification(
                update.taskId(),
                update.newStatus()
        );
    }
}
