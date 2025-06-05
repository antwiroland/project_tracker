package org.sikawofie.projecttracker.service.impl;

import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.dto.ProjectRequestDTO;
import org.sikawofie.projecttracker.dto.ProjectResponseDTO;
import org.sikawofie.projecttracker.entity.Project;
import org.sikawofie.projecttracker.exception.ResourceNotFoundException;
import org.sikawofie.projecttracker.repository.ProjectRepository;
import org.sikawofie.projecttracker.repository.TaskRepository;
import org.sikawofie.projecttracker.service.ProjectService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    @Override
    @CacheEvict(value = "projects", allEntries = true)
    public ProjectResponseDTO createProject(ProjectRequestDTO dto) {
        Project project = mapToEntity(dto);
        return mapToDTO(projectRepository.save(project));
    }

    @Override
    @Transactional
    @CacheEvict(value = "projects", key = "#id")
    public ProjectResponseDTO updateProject(Long id, ProjectRequestDTO dto) {
        Project existing = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + id));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setDeadline(dto.getDeadline());
        existing.setStatus(dto.getStatus());

        return mapToDTO(projectRepository.save(existing));
    }

    @Override
    @Transactional
    @CacheEvict(value = "projects", key = "#id")
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + id));
        taskRepository.deleteAll(project.getTasks());
        projectRepository.delete(project);
    }

    @Override
    @Cacheable(value = "projects", key = "#id")
    public ProjectResponseDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + id));
        return mapToDTO(project);
    }

    @Override
    public Page<ProjectResponseDTO> getAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable).map(this::mapToDTO);
    }

    private ProjectResponseDTO mapToDTO(Project project) {
        return ProjectResponseDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .deadline(project.getDeadline())
                .status(project.getStatus())
                .build();
    }

    private Project mapToEntity(ProjectRequestDTO dto) {
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        project.setDeadline(dto.getDeadline());
        project.setStatus(dto.getStatus());
        return project;
    }
}
