package org.sikawofie.projecttracker.controller;

import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.dto.TaskDTO;
import org.sikawofie.projecttracker.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        TaskDTO savedTask = taskService.createTask(taskDTO);
        return ResponseEntity.ok(savedTask);
    }

    @PutMapping("/{taskId}/assign/{developerId}")
    public ResponseEntity<TaskDTO> assignTask(
            @PathVariable Long taskId,
            @PathVariable Long developerId) {
        TaskDTO updatedTask = taskService.assignTask(taskId, developerId);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskDTO>> getTasksByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getTasksByProjectId(projectId));
    }

    @GetMapping("/developer/{developerId}")
    public ResponseEntity<List<TaskDTO>> getTasksByDeveloper(@PathVariable Long developerId) {
        return ResponseEntity.ok(taskService.getTasksByDeveloperId(developerId));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TaskDTO>> getOverdueTasks() {
        return ResponseEntity.ok(taskService.getOverdueTasks());
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<TaskDTO>> getTasksSorted(@RequestParam String sortBy) {
        return ResponseEntity.ok(taskService.getTasksSorted(sortBy));
    }
}
