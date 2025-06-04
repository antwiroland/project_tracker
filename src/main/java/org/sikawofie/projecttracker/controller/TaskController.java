package org.sikawofie.projecttracker.controller;

import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.dto.TaskDTO;
import org.sikawofie.projecttracker.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;


import jakarta.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Controller", description = "CRUD operations for tasks")
public class TaskController {

    private final TaskService taskService;


    @PostMapping
    @Operation(
            summary = "Create a new task",
            description = "Creates a task using the provided TaskDTO object"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        TaskDTO savedTask = taskService.createTask(taskDTO);
        return ResponseEntity.ok(savedTask);
    }


    @Operation(
            summary = "Update an existing task",
            description = "Update a task using the provide TaskDTO object"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{taskId}/assign/{developerId}")
    public ResponseEntity<TaskDTO> assignTask(
            @PathVariable Long taskId,
            @PathVariable Long developerId) {
        TaskDTO updatedTask = taskService.assignTask(taskId, developerId);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(
            summary = "Delete a task",
            description = "Delete a task with the provided id of type long"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task with provided id not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Returns a taskDTO",
            description = "Returns a taskDTO with the provided id of type long"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TaskDTO Object"),
            @ApiResponse(responseCode = "404", description = "Task with provided project id not found")
    })
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskDTO>> getTasksByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getTasksByProjectId(projectId));
    }

    @Operation(
            summary = "Returns a taskDTO",
            description = "Returns a taskDTO with the provided developer of type long"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TaskDTO Object"),
            @ApiResponse(responseCode = "404", description = "Task with developer project id not found")
    })
    @GetMapping("/developer/{developerId}")
    public ResponseEntity<List<TaskDTO>> getTasksByDeveloper(@PathVariable Long developerId) {
        return ResponseEntity.ok(taskService.getTasksByDeveloperId(developerId));
    }

    @Operation(
            summary = "Returns taskDTO objects",
            description = "Returns a taskDTO objects with overdue date"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TaskDTO Objects"),
    })
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDTO>> getOverdueTasks() {
        return ResponseEntity.ok(taskService.getOverdueTasks());
    }

    @Operation(
            summary = "Returns taskDTO objects",
            description = "Returns a sorted taskDTO objects"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "TaskDTO Objects"),
    })
    @GetMapping("/sorted")
    public ResponseEntity<List<TaskDTO>> getTasksSorted(@RequestParam String sortBy) {
        return ResponseEntity.ok(taskService.getTasksSorted(sortBy));
    }
}
