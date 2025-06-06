package org.sikawofie.projecttracker.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperResponseDTO {

    private Long id;
    private String name;
    private String email;
    private List<String> skills;
}
