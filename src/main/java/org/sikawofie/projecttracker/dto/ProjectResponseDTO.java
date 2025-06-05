package org.sikawofie.projecttracker.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import org.sikawofie.projecttracker.enums.ProjectStatus;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponseDTO {

    private Long id;
    private String name;
    private String description;
    private LocalDate deadline;
    private ProjectStatus status;
}
