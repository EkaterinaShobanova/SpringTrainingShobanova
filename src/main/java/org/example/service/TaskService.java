package org.example.service;

import org.example.dto.TaskDto;
import org.example.entity.Task;
import org.example.mapping.TaskMapper;
import org.example.repository.TaskRepository;
import org.example.service.kafka.producer.TaskStatusProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskStatusProducer taskStatusProducer;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, TaskStatusProducer taskStatusProducer, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskStatusProducer = taskStatusProducer;
        this.taskMapper = taskMapper;
    }

    public TaskDto createTask(TaskDto taskDto) {
        Task task = taskMapper.fromDto(taskDto);
        Task savedTask = taskRepository.save(task);
        return taskMapper.toDto(savedTask);
    }

    @Transactional(readOnly = true)
    public TaskDto getTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        return taskMapper.toDto(task);
    }

    public TaskDto updateTask(Long id, TaskDto taskDto) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(RuntimeException::new);

        taskMapper.updateFromDto(taskDto, existingTask);

        if (taskDto.status() != null && !taskDto.status().equals(existingTask.getStatus())) {
            taskStatusProducer.sendStatusUpdate(id, taskDto.status().toString());
        }

        Task updatedTask = taskRepository.save(existingTask);
        return taskMapper.toDto(updatedTask);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
    }
}