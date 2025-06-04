package org.sikawofie.projecttracker.service;

import org.sikawofie.projecttracker.dto.ProjectDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    ProjectDTO createProject(ProjectDTO projectDTO);
    ProjectDTO updateProject(Long id, ProjectDTO updatedDTO);
    void deleteProject(Long id);
    ProjectDTO getProjectById(Long id);
    Page<ProjectDTO> getAllProjects(Pageable pageable);
}
