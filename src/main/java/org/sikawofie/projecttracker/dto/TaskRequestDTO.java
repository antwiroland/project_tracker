package org.sikawofie.projecttracker.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.sikawofie.projecttracker.enums.TaskStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDTO {
    @NotNull(message = "Task title is required")
    @Size(min = 2, max = 100, message = "Task title must be between 2 and 100 characters")
    private String title;

    private String description;

    private LocalDate dueDate;

    private TaskStatus status;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    private Long developerId;
}
