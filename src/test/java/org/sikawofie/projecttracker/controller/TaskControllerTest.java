package org.sikawofie.projecttracker.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sikawofie.projecttracker.dto.TaskRequestDTO;
import org.sikawofie.projecttracker.dto.TaskResponseDTO;
import org.sikawofie.projecttracker.entity.Developer;
import org.sikawofie.projecttracker.entity.Project;
import org.sikawofie.projecttracker.entity.Task;
import org.sikawofie.projecttracker.enums.TaskStatus;
import org.sikawofie.projecttracker.exception.ResourceNotFoundException;
import org.sikawofie.projecttracker.repository.DeveloperRepository;
import org.sikawofie.projecttracker.repository.ProjectRepository;
import org.sikawofie.projecttracker.repository.TaskRepository;
import org.sikawofie.projecttracker.service.impl.AuditLogService;
import org.sikawofie.projecttracker.service.impl.TaskServiceImpl;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private DeveloperRepository developerRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Task task;
    private TaskRequestDTO taskRequestDTO;
    private Developer developer;
    private Project project;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setName("Test Project");

        developer = new Developer();
        developer.setId(1L);
        developer.setName("John Doe");

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setDescription("Test Description");
        task.setDueDate(LocalDate.now().plusDays(7));
        task.setStatus(TaskStatus.TODO);
        task.setProject(project);
        task.setDeveloper(developer);

        taskRequestDTO = TaskRequestDTO.builder()
                .title("Test Task")
                .description("Test Description")
                .dueDate(LocalDate.now().plusDays(7))
                .status(TaskStatus.TODO)
                .projectId(1L)
                .developerId(1L)
                .build();
    }

    @Test
    void createTask_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(developerRepository.findById(1L)).thenReturn(Optional.of(developer));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponseDTO result = taskService.createTask(taskRequestDTO);

        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
        assertEquals(task.getTitle(), result.getTitle());
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(auditLogService, times(1)).logAction(
                eq("CREATE"),
                eq("Task"),
                anyString(),
                any(Task.class),
                eq("SYSTEM"));
    }

    @Test
    void createTask_ProjectNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.createTask(taskRequestDTO);
        });

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void assignTask_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(developerRepository.findById(1L)).thenReturn(Optional.of(developer));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponseDTO result = taskService.assignTask(1L, 1L);

        assertNotNull(result);
        assertEquals(developer.getId(), result.getDeveloperId());
        verify(taskRepository, times(1)).save(task);
        verify(auditLogService, times(1)).logAction(
                eq("UPDATE"),
                eq("Task"),
                anyString(),
                any(Task.class),
                eq("SYSTEM"));
    }

    @Test
    void assignTask_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.assignTask(1L, 1L);
        });

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_Success() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).delete(task);
        verify(auditLogService, times(1)).logAction(
                eq("DELETE"),
                eq("Task"),
                anyString(),
                any(Task.class),
                eq("SYSTEM"));
    }

    @Test
    void deleteTask_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.deleteTask(1L);
        });

        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void getTasksByProjectId_Success() {
        when(taskRepository.findByProjectId(1L)).thenReturn(List.of(task));

        List<TaskResponseDTO> results = taskService.getTasksByProjectId(1L);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(task.getId(), results.get(0).getId());
    }

    @Test
    void getTasksByDeveloperId_Success() {
        when(taskRepository.findByDeveloper_Id(1L)).thenReturn(List.of(task));

        List<TaskResponseDTO> results = taskService.getTasksByDeveloperId(1L);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(task.getId(), results.get(0).getId());
    }

    @Test
    void getTasksSorted_Success() {
        when(taskRepository.findAll(Sort.by(Sort.Direction.ASC, "title")))
                .thenReturn(List.of(task));

        List<TaskResponseDTO> results = taskService.getTasksSorted("title");

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(task.getId(), results.get(0).getId());
    }

    @Test
    void updateTask_Success() {
        TaskRequestDTO updateDTO = TaskRequestDTO.builder()
                .title("Updated Title")
                .description("Updated Description")
                .status(TaskStatus.IN_PROGRESS)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponseDTO result = taskService.updateTask(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    void updateTask_TaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.updateTask(1L, taskRequestDTO);
        });

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTask_PartialUpdate() {
        TaskRequestDTO partialUpdateDTO = TaskRequestDTO.builder()
                .title("Only Title Updated")
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskResponseDTO result = taskService.updateTask(1L, partialUpdateDTO);

        assertNotNull(result);
        assertEquals("Only Title Updated", result.getTitle());
        // Original description should remain
        assertEquals("Test Description", result.getDescription());
    }
}