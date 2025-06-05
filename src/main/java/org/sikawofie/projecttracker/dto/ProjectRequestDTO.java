package org.sikawofie.projecttracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import org.sikawofie.projecttracker.enums.ProjectStatus;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRequestDTO {

    @NotBlank(message = "Project name is required")
    private String name;

    private String description;

    @NotNull(message = "Deadline is required")
    private LocalDate deadline;

    private ProjectStatus status;
}
