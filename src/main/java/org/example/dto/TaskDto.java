package org.example.dto;

import org.example.entity.TaskStatus;

public record TaskDto(
        Long taskId,
        String title,
        String description,
        Long userid,
        TaskStatus status
) {}