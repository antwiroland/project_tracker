package org.sikawofie.projecttracker.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sikawofie.projecttracker.dto.ProjectRequestDTO;
import org.sikawofie.projecttracker.dto.ProjectResponseDTO;
import org.sikawofie.projecttracker.entity.Project;
import org.sikawofie.projecttracker.entity.Task;
import org.sikawofie.projecttracker.enums.ProjectStatus;
import org.sikawofie.projecttracker.exception.ResourceNotFoundException;
import org.sikawofie.projecttracker.repository.ProjectRepository;
import org.sikawofie.projecttracker.repository.TaskRepository;
import org.sikawofie.projecttracker.service.impl.AuditLogService;
import org.sikawofie.projecttracker.service.impl.ProjectServiceImpl;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AuditLogService auditLogService;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project project;
    private ProjectRequestDTO projectRequestDTO;
    private Task task;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setName("Test Project");
        project.setDescription("Test Description");
        project.setDeadline(LocalDate.now().plusDays(30));
        project.setStatus(ProjectStatus.IN_PROGRESS);

        task = new Task();
        task.setId(1L);
        task.setProject(project);

        projectRequestDTO = ProjectRequestDTO.builder()
                .name("Test Project")
                .description("Test Description")
                .deadline(LocalDate.now().plusDays(30))
                .status(ProjectStatus.IN_PROGRESS)
                .build();
    }

    @Test
    void createProject_Success() {
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponseDTO result = projectService.createProject(projectRequestDTO);

        assertNotNull(result);
        assertEquals(project.getId(), result.getId());
        assertEquals(project.getName(), result.getName());
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(auditLogService, times(1)).logAction(
                eq("CREATE"),
                eq("Project"),
                anyString(),
                any(Project.class),
                eq("SYSTEM"));
    }

    @Test
    void updateProject_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponseDTO result = projectService.updateProject(1L, projectRequestDTO);

        assertNotNull(result);
        assertEquals(project.getId(), result.getId());
        verify(projectRepository, times(1)).save(project);
        verify(auditLogService, times(1)).logAction(
                eq("UPDATE"),
                eq("Project"),
                anyString(),
                any(Project.class),
                eq("SYSTEM"));
    }

    @Test
    void updateProject_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            projectService.updateProject(1L, projectRequestDTO);
        });

        verify(projectRepository, never()).save(any(Project.class));
    }


    @Test
    void deleteProject_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            projectService.deleteProject(1L);
        });

        verify(projectRepository, never()).delete(any(Project.class));
    }

    @Test
    void getProjectById_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        ProjectResponseDTO result = projectService.getProjectById(1L);

        assertNotNull(result);
        assertEquals(project.getId(), result.getId());
        assertEquals(project.getName(), result.getName());
    }

    @Test
    void getProjectById_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            projectService.getProjectById(1L);
        });
    }

    @Test
    void getAllProjects_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        when(projectRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(project)));

        Page<ProjectResponseDTO> result = projectService.getAllProjects(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(project.getId(), result.getContent().get(0).getId());
    }

    @Test
    void getAllProjects_Empty() {
        Pageable pageable = PageRequest.of(0, 10);
        when(projectRepository.findAll(pageable)).thenReturn(Page.empty());

        Page<ProjectResponseDTO> result = projectService.getAllProjects(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void mapToDTO_Success() {
        ProjectResponseDTO dto = projectService.mapToDTO(project);

        assertNotNull(dto);
        assertEquals(project.getId(), dto.getId());
        assertEquals(project.getName(), dto.getName());
        assertEquals(project.getDescription(), dto.getDescription());
        assertEquals(project.getDeadline(), dto.getDeadline());
        assertEquals(project.getStatus(), dto.getStatus());
    }

    @Test
    void mapToEntity_Success() {
        Project entity = projectService.mapToEntity(projectRequestDTO);

        assertNotNull(entity);
        assertEquals(projectRequestDTO.getName(), entity.getName());
        assertEquals(projectRequestDTO.getDescription(), entity.getDescription());
        assertEquals(projectRequestDTO.getDeadline(), entity.getDeadline());
        assertEquals(projectRequestDTO.getStatus(), entity.getStatus());
    }
}