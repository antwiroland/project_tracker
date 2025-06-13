package org.sikawofie.projecttracker.service;

import org.sikawofie.projecttracker.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    ProjectResponseDTO createProject(ProjectRequestDTO projectDTO);
    ProjectResponseDTO updateProject(Long id, ProjectRequestDTO updatedDTO);
    void deleteProject(Long id);
    ProjectResponseDTO getProjectById(Long id);
    Page<ProjectResponseDTO> getAllProjects(Pageable pageable);

    interface UserService {
        UserResponseDTO addUser(UserRequestDTO userRequestDTO);
        AuthResponseDTO verify(UserRequestDTO userRequestDTO);
    }
}
