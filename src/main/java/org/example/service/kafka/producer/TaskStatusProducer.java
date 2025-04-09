package org.example.service.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.TaskStatusUpdateDto;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class TaskStatusProducer {
    private static final String TOPIC = "task-status-updates";

    private final KafkaTemplate<String, TaskStatusUpdateDto> kafkaTemplate;

    public TaskStatusProducer(KafkaTemplate<String, TaskStatusUpdateDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendStatusUpdate(Long taskId, String newStatus) {
        TaskStatusUpdateDto update = new TaskStatusUpdateDto(taskId, newStatus);

        CompletableFuture<SendResult<String, TaskStatusUpdateDto>> future =
                kafkaTemplate.send(TOPIC, taskId.toString(), update);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent successfully: taskId={}, status={}, offset={}",
                        taskId, newStatus, result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send: taskId=" + taskId, ex);
            }
        });
    }
}
