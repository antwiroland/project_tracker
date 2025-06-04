package org.sikawofie.projecttracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeveloperDTO {
    private Long id;

    @NotBlank(message = "Developer name is required")
    private String name;

    @Email(message = "Must be a valid email")
    private String email;

    private List<String> skills;
}
