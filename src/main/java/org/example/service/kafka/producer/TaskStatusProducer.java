package org.example.service.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.TaskStatusUpdateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class TaskStatusProducer {
    @Value("${notification.topic}")
    private String topic;

    private final KafkaTemplate<String, TaskStatusUpdateDto> kafkaTemplate;

    public TaskStatusProducer(KafkaTemplate<String, TaskStatusUpdateDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendStatusUpdate(Long taskId, String newStatus) {
        TaskStatusUpdateDto update = new TaskStatusUpdateDto(taskId, newStatus);

        log.debug("Preparing to send status update for task {} with status {}", taskId, newStatus);

        CompletableFuture<SendResult<String, TaskStatusUpdateDto>> future =
                kafkaTemplate.send(topic, taskId.toString(), update);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent successfully: taskId={}, status={}, partition={}, offset={}",
                        taskId,
                        newStatus,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send message for taskId={}", taskId, ex);
            }
        });
    }
}
