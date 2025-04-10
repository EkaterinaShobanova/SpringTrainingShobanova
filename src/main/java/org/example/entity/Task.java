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
    @Enumerated(EnumType.STRING)
    private TaskStatus status;


}
