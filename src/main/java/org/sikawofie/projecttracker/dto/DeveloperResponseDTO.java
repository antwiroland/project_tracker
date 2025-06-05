package org.sikawofie.projecttracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeveloperResponseDTO {

    private Long id;
    private String name;
    private String email;

    private List<String> skills;
}
