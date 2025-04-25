package org.example.service;

import org.example.PostgresContainer;
import org.example.dto.TaskDto;
import org.example.entity.Task;
import org.example.entity.TaskStatus;
import org.example.mapping.TaskMapper;
import org.example.repository.TaskRepository;
import org.example.service.kafka.producer.TaskStatusProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest extends PostgresContainer {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskStatusProducer taskStatusProducer;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_ShouldSaveAndReturnDto() {
        String title = "Test";
        String description = "Desc";
        TaskDto inputDto = new TaskDto(null, title, description, null, null);
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(TaskStatus.NEW);

        Task savedTask = new Task();
        savedTask.setId(1L);
        savedTask.setTitle(title);
        savedTask.setDescription(description);
        savedTask.setStatus(TaskStatus.NEW);

        TaskDto expectedDto = new TaskDto(1L, title, description, null, TaskStatus.NEW);

        when(taskMapper.fromDto(inputDto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(savedTask);
        when(taskMapper.toDto(savedTask)).thenReturn(expectedDto);

        TaskDto result = taskService.createTask(inputDto);

        assertEquals(1L, result.taskId());
        verify(taskRepository).save(task);
    }

    @Test
    void getTask_WhenExists_ShouldReturnTaskDto() {
        Long taskId = 1L;
        Task task = new Task(1L, "Test", "Desc", 1L,TaskStatus.NEW);
        task.setId(taskId);
        TaskDto expectedDto = new TaskDto(null, "Test", "Desc", null, null);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(expectedDto);

        TaskDto result = taskService.getTask(taskId);

        assertNotNull(result);
        assertEquals(taskId, result.taskId());
    }

    @Test
    void getTask_WhenNotExists_ShouldThrowException() {
        Long taskId = 99L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());


        assertThrows(RuntimeException.class, () -> taskService.getTask(taskId));
    }

    @Test
    void updateTask_ShouldUpdateAndSendStatusEvent() {
        Long taskId = 1L;
        Task existingTask = new Task(taskId, "Old", "Desc", 1L,TaskStatus.NEW);
        existingTask.setId(taskId);
        existingTask.setStatus(TaskStatus.IN_PROGRESS);

        TaskDto updateDto = new TaskDto(taskId, "New", "New Desc",1L, TaskStatus.COMPLETED);
        Task updatedTask = new Task(2L, "Test", "Desc", 1L,TaskStatus.NEW);
        updatedTask.setId(taskId);
        updatedTask.setStatus(TaskStatus.COMPLETED);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(updatedTask);
        when(taskMapper.toDto(updatedTask)).thenReturn(updateDto);

        TaskDto result = taskService.updateTask(taskId, updateDto);

        assertEquals(TaskStatus.COMPLETED, result.status());
        verify(taskStatusProducer).sendStatusUpdate(taskId, TaskStatus.COMPLETED.toString());
    }

    @Test
    void deleteTask_WhenExists_ShouldDelete() {
        Long taskId = 1L;
        when(taskRepository.existsById(taskId)).thenReturn(true);

        taskService.deleteTask(taskId);

        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        Task task1 = new Task(1L, "First", "Desc", 1L,TaskStatus.NEW);
        Task task2 = new Task(2L, "Second", "Desc", 1L,TaskStatus.NEW);
        List<Task> tasks = List.of(task1, task2);

        TaskDto dto1 = new TaskDto(1L, "New", "New Desc",1L, TaskStatus.COMPLETED);
        TaskDto dto2 = new TaskDto(1L, "New", "New Desc",1L, TaskStatus.COMPLETED);

        when(taskRepository.findAll()).thenReturn(tasks);
        when(taskMapper.toDto(task1)).thenReturn(dto1);
        when(taskMapper.toDto(task2)).thenReturn(dto2);

        List<TaskDto> result = taskService.getAllTasks();

        assertEquals(2, result.size());
    }
}
