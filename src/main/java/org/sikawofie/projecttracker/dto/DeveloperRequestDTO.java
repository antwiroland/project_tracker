package org.sikawofie.projecttracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperRequestDTO {
    @NotBlank(message = "Developer name is required")
    private String name;

    @Email(message = "Must be a valid email")
    private String email;

    private List<String> skills;
}
