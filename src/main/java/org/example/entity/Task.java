package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.dto.TaskDto;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String status;

    public static Task fromDto(TaskDto dto) {
        Task task = new Task();
        task.setId(dto.taskId());
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setUserId(dto.userid());
        task.setStatus(dto.status());
        return task;
    }

    public TaskDto toDto() {
        return new TaskDto(
                this.id,
                this.title,
                this.description,
                this.userId,
                this.status
        );
    }
}
