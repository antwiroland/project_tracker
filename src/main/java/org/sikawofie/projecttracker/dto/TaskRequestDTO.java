package org.sikawofie.projecttracker.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.sikawofie.projecttracker.enums.TaskStatus;

import java.time.LocalDate;

@Data
@Builder
public class TaskRequestDTO {
    @NotNull(message = "Task title is required")
    @Size(min = 2, max = 100, message = "Task title name must be between 2 and 100 characters")
    private String title;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
    private Long projectId;
    private Long developerId;
}
