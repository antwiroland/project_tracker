package org.sikawofie.projecttracker.service.impl;

import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.dto.TaskDTO;
import org.sikawofie.projecttracker.entity.Developer;
import org.sikawofie.projecttracker.entity.Project;
import org.sikawofie.projecttracker.entity.Task;
import org.sikawofie.projecttracker.enums.TaskStatus;
import org.sikawofie.projecttracker.exception.ResourceNotFoundException;
import org.sikawofie.projecttracker.repository.DeveloperRepository;
import org.sikawofie.projecttracker.repository.ProjectRepository;
import org.sikawofie.projecttracker.repository.TaskRepository;
import org.sikawofie.projecttracker.service.TaskService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final DeveloperRepository developerRepository;
    private final ProjectRepository projectRepository;

    @Override
    public TaskDTO createTask(TaskDTO dto) {
        Task task = mapToEntity(dto);
        Task savedTask = taskRepository.save(task);
        return mapToDTO(savedTask);
    }

    @Override
    @Transactional
    public TaskDTO assignTask(Long taskId, Long developerId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + taskId));

        Developer developer = developerRepository.findById(developerId)
                .orElseThrow(() -> new ResourceNotFoundException("Developer not found with id " + developerId));

        task.addDeveloper(developer);
        Task updated = taskRepository.save(task);
        return mapToDTO(updated);
    }

    @Override
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with id " + taskId);
        }
        taskRepository.deleteById(taskId);
    }

    @Override
    public List<TaskDTO> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getTasksByDeveloperId(Long developerId) {
        return taskRepository.findByDevelopers_Id(developerId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getOverdueTasks() {
        return taskRepository.findByDueDateBeforeAndStatusNot(LocalDate.now(), TaskStatus.DONE)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> getTasksSorted(String sortBy) {
        return taskRepository.findAll(Sort.by(Sort.Direction.ASC, sortBy))
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Mapping Methods

    private TaskDTO mapToDTO(Task task) {
        Set<Long> developerIds = task.getDevelopers().stream()
                .map(Developer::getId).collect(Collectors.toSet());

        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .dueDate(task.getDueDate())
                .status(task.getStatus())
                .projectId(task.getProject().getId())
                .developerIds(developerIds)
                .build();
    }

    private Task mapToEntity(TaskDTO dto) {
        Task task = new Task();

        task.setId(dto.getId());
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setStatus(dto.getStatus());

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id " + dto.getProjectId()));
        task.setProject(project);

        if (dto.getDeveloperIds() != null && !dto.getDeveloperIds().isEmpty()) {
            Set<Developer> developers = dto.getDeveloperIds().stream()
                    .map(id -> developerRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Developer not found with id " + id)))
                    .collect(Collectors.toSet());
            task.setDevelopers(developers);
        }

        return task;
    }
}
