package org.example.dto;

public record TaskDto(
        Long taskId,
        String title,
        String description,
        Long userid,
        String status
) {}