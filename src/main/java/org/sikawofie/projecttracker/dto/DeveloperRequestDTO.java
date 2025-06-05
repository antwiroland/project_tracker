package org.sikawofie.projecttracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeveloperRequestDTO {
    @NotBlank(message = "Developer name is required")
    private String name;

    @Email(message = "Must be a valid email")
    private String email;

    private List<String> skills;
}
