package org.example.service.kafka.producer;
import org.example.dto.TaskStatusUpdateDto;
import org.example.entity.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import java.util.concurrent.CompletableFuture;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskStatusProducerTest {

    @Mock
    private KafkaTemplate<String, TaskStatusUpdateDto> kafkaTemplate;

    @InjectMocks
    private TaskStatusProducer taskStatusProducer;

    @Test
    void sendStatusUpdate_ShouldSendMessageToKafka() {
        Long taskId = 1L;
        String newStatus = "COMPLETED";
        String testTopic = "task-status-topic";

        CompletableFuture<SendResult<String, TaskStatusUpdateDto>> future =
                CompletableFuture.completedFuture(mock(SendResult.class));

        when(kafkaTemplate.send(eq(testTopic), eq(taskId.toString()), any(TaskStatusUpdateDto.class)))
                .thenReturn(future);

        taskStatusProducer.setTopic(testTopic);

        taskStatusProducer.sendStatusUpdate(taskId, newStatus);

        verify(kafkaTemplate).send(
                eq(testTopic),
                eq(taskId.toString()),
                argThat(dto ->
                        dto.taskId().equals(taskId) &&
                                dto.newStatus().equals(newStatus)
                ));

    }

    @Test
    void sendStatusUpdate_ShouldLogErrorOnFailure() {
        Long taskId = 2L;
        String newStatus = TaskStatus.FAILED.toString();

        CompletableFuture<SendResult<String, TaskStatusUpdateDto>> future =
                new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka error"));

        when(kafkaTemplate.send(
                anyString(),
                eq(taskId.toString()),
                any(TaskStatusUpdateDto.class)
        )).thenReturn(future);

        taskStatusProducer.sendStatusUpdate(taskId, newStatus);

        verify(kafkaTemplate).send(
                anyString(),
                eq(taskId.toString()),
                any(TaskStatusUpdateDto.class)
        );
    }
}

