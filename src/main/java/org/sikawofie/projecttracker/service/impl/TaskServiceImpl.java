package org.sikawofie.projecttracker.service.impl;

import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.dto.TaskRequestDTO;
import org.sikawofie.projecttracker.dto.TaskResponseDTO;
import org.sikawofie.projecttracker.entity.Developer;
import org.sikawofie.projecttracker.entity.Project;
import org.sikawofie.projecttracker.entity.Task;
import org.sikawofie.projecttracker.entity.User;
import org.sikawofie.projecttracker.enums.TaskStatus;
import org.sikawofie.projecttracker.exception.ResourceNotFoundException;
import org.sikawofie.projecttracker.repository.DeveloperRepository;
import org.sikawofie.projecttracker.repository.ProjectRepository;
import org.sikawofie.projecttracker.repository.TaskRepository;
import org.sikawofie.projecttracker.repository.UserRepo;
import org.sikawofie.projecttracker.service.TaskService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"tasks"})
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final DeveloperRepository developerRepository;
    private final ProjectRepository projectRepository;
    private final AuditLogService auditLogService;
    private final UserRepo userRepo;

    @Override
    @Cacheable("allTasks")
    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @CacheEvict(value = {"allTasks", "developerTasks", "projectTasks"}, allEntries = true)
    public TaskResponseDTO createTask(TaskRequestDTO dto) {
        Task task = mapToEntity(dto);
        Task savedTask = taskRepository.save(task);

        auditLogService.logAction("CREATE", "Task", savedTask.getId().toString(), savedTask, "SYSTEM");

        return mapToDTO(savedTask);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"allTasks", "developerTasks"}, allEntries = true)
    public TaskResponseDTO assignTask(Long taskId, Long developerId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + taskId));

        Developer developer = developerRepository.findById(developerId)
                .orElseThrow(() -> new ResourceNotFoundException("Developer not found with id " + developerId));

        task.setDeveloper(developer);
        Task updated = taskRepository.save(task);

        auditLogService.logAction("UPDATE", "Task", updated.getId().toString(), updated, "SYSTEM");

        return mapToDTO(updated);
    }

    @Override
    @CacheEvict(value = {"allTasks", "developerTasks", "projectTasks"}, allEntries = true)
    public void deleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + taskId));

        taskRepository.delete(task);

        auditLogService.logAction("DELETE", "Task", task.getId().toString(), task, "SYSTEM");
    }

    @Override
    @Cacheable(value = "projectTasks", key = "#projectId")
    public List<TaskResponseDTO> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Cacheable(value = "developerTasks", key = "#root.methodName")
    public List<TaskResponseDTO> getMyTasks() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepo.findByUsername(username);
        return taskRepository.findByDeveloper_Id(currentUser.getId())
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Cacheable(value = "developerTasks", key = "#developerId")
    public List<TaskResponseDTO> getTasksByDeveloperId(Long developerId) {
        return taskRepository.findByDeveloper_Id(developerId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Cacheable("overdueTasks")
    public List<TaskResponseDTO> getOverdueTasks() {
        return taskRepository.findByDueDateBeforeAndStatusNot(LocalDate.now(), TaskStatus.DONE)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Cacheable(value = "sortedTasks", key = "#sortBy")
    public List<TaskResponseDTO> getTasksSorted(String sortBy) {
        return taskRepository.findAll(Sort.by(Sort.Direction.ASC, sortBy))
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(value = {"allTasks", "developerTasks", "projectTasks"}, allEntries = true)
    public TaskResponseDTO updateTask(long id, TaskRequestDTO taskRequestDTO) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with " + id + " not found"));

        if (taskRequestDTO.getTitle() != null) task.setTitle(taskRequestDTO.getTitle());
        if (taskRequestDTO.getDescription() != null) task.setDescription(taskRequestDTO.getDescription());
        if (taskRequestDTO.getStatus() != null) task.setStatus(taskRequestDTO.getStatus());

        return mapToDTO(taskRepository.save(task));
    }

    // Helper mapping methods
    private TaskResponseDTO mapToDTO(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .status(task.getStatus())
                .projectId(task.getProject().getId())
                .developerId(task.getDeveloper() != null ? task.getDeveloper().getId() : null)
                .build();
    }

    private Task mapToEntity(TaskRequestDTO dto) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setStatus(dto.getStatus());

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + dto.getProjectId()));
        task.setProject(project);

        if (dto.getDeveloperId() != null) {
            Developer developer = developerRepository.findById(dto.getDeveloperId())
                    .orElseThrow(() -> new ResourceNotFoundException("Developer not found with id " + dto.getDeveloperId()));
            task.setDeveloper(developer);
        }

        return task;
    }
}
