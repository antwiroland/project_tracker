package org.sikawofie.projecttracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.sikawofie.projecttracker.enums.TaskStatus;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private LocalDate dueDate;

    @NotNull(message = "Task status is required")
    private TaskStatus status;

    @NotNull(message = "Project id is required")
    private Long projectId;

    private Set<Long> developerIds;
}
