package org.example.controller;

import org.example.aspect.TaskNotFoundException;
import org.example.dto.TaskDto;
import org.example.entity.TaskStatus;
import org.example.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    void createTask_ShouldReturnCreatedTask() throws Exception {

        TaskDto inputDto = new TaskDto(null, "New Task", "Description", null, null);
        TaskDto outputDto = new TaskDto(1L, "New Task", "Description", null, TaskStatus.NEW);

        given(taskService.createTask(any(TaskDto.class))).willReturn(outputDto);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(1L)) // Исправлено на taskId
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.status").value("NEW"));
    }

    @Test
    void getTask_ShouldReturnTask() throws Exception {

        TaskDto taskDto = new TaskDto(1L, "Test Task", "Description", 1L, TaskStatus.IN_PROGRESS);
        given(taskService.getTask(1L)).willReturn(taskDto);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(1L))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void updateTask_ShouldReturnUpdatedTask() throws Exception {
        TaskDto updateDto = new TaskDto(1L, "Updated", "New Desc", 1L, TaskStatus.COMPLETED);
        given(taskService.updateTask(eq(1L), any(TaskDto.class))).willReturn(updateDto);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void deleteTask_ShouldReturnOk() throws Exception {
        willDoNothing().given(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllTasks_ShouldReturnTaskList() throws Exception {
        List<TaskDto> tasks = List.of(
                new TaskDto(1L, "Task 1", "Desc 1", 1L, TaskStatus.NEW),
                new TaskDto(2L, "Task 2", "Desc 2", 1L, TaskStatus.IN_PROGRESS)
        );
        given(taskService.getAllTasks()).willReturn(tasks);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].status").value("NEW"))
                .andExpect(jsonPath("$[1].status").value("IN_PROGRESS"));
    }

    @Test
    void getTask_WhenNotExists_ShouldReturnNotFound() throws Exception {
        given(taskService.getTask(99L))
                .willThrow(new TaskNotFoundException(99L));

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound());
    }


}