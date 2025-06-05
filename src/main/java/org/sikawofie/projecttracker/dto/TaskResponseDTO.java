package org.sikawofie.projecttracker.dto;

import lombok.Builder;
import lombok.Data;
import org.sikawofie.projecttracker.enums.TaskStatus;

import java.time.LocalDate;

@Data
@Builder
public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
    private Long projectId;
    private Long developerId;
}
