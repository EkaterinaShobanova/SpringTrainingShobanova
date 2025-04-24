package org.example.service.kafka.consumer;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.TaskStatusUpdateDto;
import org.example.service.NotificationService;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@Service
public class TaskStatusConsumer {
    private final NotificationService notificationService;

    public TaskStatusConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "${notification.topic}",
            containerFactory = "kafkaListenerContainerFactory")
    public void handleTaskUpdate(TaskStatusUpdateDto update,
                                 Acknowledgment acknowledgment) {
        try {
            log.info("Received status update: taskId={}, newStatus={}",
                    update.taskId(), update.newStatus());

            notificationService.sendStatusChangeNotification(
                    update.taskId(),
                    update.newStatus()
            );

            acknowledgment.acknowledge();
            log.debug("Acknowledged message for taskId={}", update.taskId());

        } catch (Exception e) {
            log.error("Failed to process status update for taskId={}", update.taskId(), e);
        }
    }
}