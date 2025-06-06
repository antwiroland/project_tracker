package org.sikawofie.projecttracker.dto;

import lombok.*;
import org.sikawofie.projecttracker.enums.TaskStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
    private Long projectId;
    private Long developerId;
}
