package org.sikawofie.projecttracker.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.dto.ApiResponseDTO;
import org.sikawofie.projecttracker.dto.TaskRequestDTO;
import org.sikawofie.projecttracker.dto.TaskResponseDTO;
import org.sikawofie.projecttracker.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Get all task", description = "Returns a list of tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of tasks"),
            @ApiResponse(responseCode = "200", description = "[]")
    })
    public ResponseEntity<ApiResponseDTO<List<TaskResponseDTO>>> getAllTasks() {
        List<TaskResponseDTO> allTasks = taskService.getAllTasks();

        ApiResponseDTO<List<TaskResponseDTO>> response = ApiResponseDTO.<List<TaskResponseDTO>>builder().status(200).message("Tasks successfully fetched").data(allTasks).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Create a new task", description = "Creates a new task using the provided TaskDTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<ApiResponseDTO<TaskResponseDTO>> createTask(@Valid @RequestBody TaskRequestDTO taskDTO) {
        TaskResponseDTO savedTask = taskService.createTask(taskDTO);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedTask.getId())
                .toUri();

        ApiResponseDTO<TaskResponseDTO> response = ApiResponseDTO.<TaskResponseDTO>builder()
                .status(201)
                .message("Task created successfully")
                .data(savedTask)
                .build();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('DEVELOPER')")
    @Operation(summary = "Get current developer's tasks", description = "Retrieves all tasks assigned to the currently logged-in developer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks fetched successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not a developer")
    })
    public ResponseEntity<ApiResponseDTO<List<TaskResponseDTO>>> getMyTasks() {
        List<TaskResponseDTO> tasks = taskService.getMyTasks();

        ApiResponseDTO<List<TaskResponseDTO>> response = ApiResponseDTO.<List<TaskResponseDTO>>builder()
                .status(200)
                .message("Tasks fetched successfully")
                .data(tasks)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{taskId}/assign/{developerId}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    @Operation(summary = "Assign a task to a developer", description = "Assigns the task with the given ID to the developer with the given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task assigned successfully"),
            @ApiResponse(responseCode = "404", description = "Invalid task or developer ID")
    })
    public ResponseEntity<ApiResponseDTO<TaskResponseDTO>> assignTask(
            @PathVariable Long taskId,
            @PathVariable Long developerId) {
        TaskResponseDTO updatedTask = taskService.assignTask(taskId, developerId);

        ApiResponseDTO<TaskResponseDTO> response = ApiResponseDTO.<TaskResponseDTO>builder()
                .status(200)
                .message("Task assigned successfully")
                .data(updatedTask)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('DEVELOPER') and @securityUtil.isTaskOwner(authentication.name, #id))")
    @Operation(summary = "Update a task", description = "Only task owner or admin can update a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task successfully updated"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not task owner or not authorized"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<ApiResponseDTO<TaskResponseDTO>> updateTask(
            @PathVariable long id,
            @Valid @RequestBody TaskRequestDTO taskRequestDTO) {
        TaskResponseDTO taskResponseDTO = taskService.updateTask(id, taskRequestDTO);

        ApiResponseDTO<TaskResponseDTO> response = ApiResponseDTO.<TaskResponseDTO>builder()
                .status(200)
                .message("Task successfully updated")
                .data(taskResponseDTO)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a task", description = "Deletes the task with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get tasks by project ID", description = "Retrieves all tasks associated with the specified project ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found")
    })
    public ResponseEntity<ApiResponseDTO<List<TaskResponseDTO>>> getTasksByProject(@PathVariable Long projectId) {
        List<TaskResponseDTO> tasks = taskService.getTasksByProjectId(projectId);

        ApiResponseDTO<List<TaskResponseDTO>> response = ApiResponseDTO.<List<TaskResponseDTO>>builder()
                .status(200)
                .message("Tasks fetched successfully")
                .data(tasks)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/developer/{developerId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get tasks by developer ID", description = "Retrieves all tasks assigned to the specified developer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Developer not found")
    })
    public ResponseEntity<ApiResponseDTO<List<TaskResponseDTO>>> getTasksByDeveloper(@PathVariable Long developerId) {
        List<TaskResponseDTO> tasks = taskService.getTasksByDeveloperId(developerId);

        ApiResponseDTO<List<TaskResponseDTO>> response = ApiResponseDTO.<List<TaskResponseDTO>>builder()
                .status(200)
                .message("Tasks fetched successfully")
                .data(tasks)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get overdue tasks", description = "Retrieves all tasks that are overdue and not completed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Overdue tasks fetched")
    })
    public ResponseEntity<ApiResponseDTO<List<TaskResponseDTO>>> getOverdueTasks() {
        List<TaskResponseDTO> overdueTasks = taskService.getOverdueTasks();

        ApiResponseDTO<List<TaskResponseDTO>> response = ApiResponseDTO.<List<TaskResponseDTO>>builder()
                .status(200)
                .message("Overdue tasks fetched")
                .data(overdueTasks)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/sorted")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get tasks sorted by field", description = "Retrieves all tasks sorted by the specified field")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks fetched sorted by field"),
            @ApiResponse(responseCode = "400", description = "Invalid sort parameter")
    })
    public ResponseEntity<ApiResponseDTO<List<TaskResponseDTO>>> getTasksSorted(
            @RequestParam
            @Pattern(regexp = "title|status|dueDate|project.id|developer.id", message = "Invalid sort parameter")
            String sortBy) {
        List<TaskResponseDTO> sortedTasks = taskService.getTasksSorted(sortBy);

        ApiResponseDTO<List<TaskResponseDTO>> response = ApiResponseDTO.<List<TaskResponseDTO>>builder()
                .status(200)
                .message("Tasks fetched sorted by " + sortBy)
                .data(sortedTasks)
                .build();

        return ResponseEntity.ok(response);
    }
}