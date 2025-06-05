package org.sikawofie.projecttracker.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.dto.TaskRequestDTO;
import org.sikawofie.projecttracker.dto.TaskResponseDTO;
import org.sikawofie.projecttracker.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Controller", description = "CRUD operations for tasks")
@Validated
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(
            summary = "Create a new task",
            description = "Creates a new task using the provided TaskDTO"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO taskDTO) {
        TaskResponseDTO savedTask = taskService.createTask(taskDTO);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedTask.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedTask);
    }

    @PutMapping("/{taskId}/assign/{developerId}")
    @Operation(
            summary = "Assign a task to a developer",
            description = "Assigns the task with the given ID to the developer with the given ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task assigned successfully"),
            @ApiResponse(responseCode = "404", description = "Invalid task or developer ID")
    })
    public ResponseEntity<TaskResponseDTO> assignTask(
            @PathVariable Long taskId,
            @PathVariable Long developerId) {
        TaskResponseDTO updatedTask = taskService.assignTask(taskId, developerId);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a task",
            description = "Deletes the task with the specified ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/project/{projectId}")
    @Operation(
            summary = "Get tasks by project ID",
            description = "Retrieves all tasks associated with the specified project ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of tasks retrieved"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<List<TaskResponseDTO>> getTasksByProject(@PathVariable Long projectId) {
        List<TaskResponseDTO> tasks = taskService.getTasksByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/developer/{developerId}")
    @Operation(
            summary = "Get tasks by developer ID",
            description = "Retrieves all tasks assigned to the specified developer"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of tasks retrieved"),
            @ApiResponse(responseCode = "404", description = "Developer not found")
    })
    public ResponseEntity<List<TaskResponseDTO>> getTasksByDeveloper(@PathVariable Long developerId) {
        List<TaskResponseDTO> tasks = taskService.getTasksByDeveloperId(developerId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/overdue")
    @Operation(
            summary = "Get overdue tasks",
            description = "Retrieves all tasks that are overdue and not completed"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of overdue tasks retrieved")
    })
    public ResponseEntity<List<TaskResponseDTO>> getOverdueTasks() {
        List<TaskResponseDTO> overdueTasks = taskService.getOverdueTasks();
        return ResponseEntity.ok(overdueTasks);
    }

    @GetMapping("/sort")
    @Operation(
            summary = "Get tasks sorted by field",
            description = "Retrieves all tasks sorted by the specified field"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of sorted tasks retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid sort parameter")
    })
    public ResponseEntity<List<TaskResponseDTO>> getTasksSorted(
            @RequestParam
            @Pattern(regexp = "title|status|dueDate|project.id|developer.id", message = "Invalid sort parameter")
            String sortBy) {
        List<TaskResponseDTO> sortedTasks = taskService.getTasksSorted(sortBy);
        return ResponseEntity.ok(sortedTasks);
    }
}
