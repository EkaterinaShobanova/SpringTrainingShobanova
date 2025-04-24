package org.example.dto;

public record TaskStatusUpdateDto(
        Long taskId,
        String newStatus
) {}
