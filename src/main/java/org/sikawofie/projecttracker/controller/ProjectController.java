package org.sikawofie.projecttracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.dto.ProjectDTO;
import org.sikawofie.projecttracker.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Project Controller",description = "CRUD operation for project")
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "Create a Project", description = "Create a project using projectDTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data input")
    })
    @PostMapping
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectDTO) {
        return ResponseEntity.ok(projectService.createProject(projectDTO));
    }

    @Operation(summary = "Update a Project", description = "Update a project using projectDTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project update successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data input"),
            @ApiResponse(responseCode = "404", description = "Project with id provided not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectDTO projectDTO) {
        return ResponseEntity.ok(projectService.updateProject(id, projectDTO));
    }

    @Operation(summary = "Delete a Project", description = "Delete a project using projectID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Project with id provided not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Return a Project", description = "Return a projectDTO using projectID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ProjectDTO"),
            @ApiResponse(responseCode = "404", description = "Project with id provided not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @Operation(summary = "Return list of ProjectDTO", description = "Return a list projectDTO using")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List<ProjectDTO>"),
    })
    @GetMapping
    public ResponseEntity<Page<ProjectDTO>> getAllProjects(Pageable pageable) {
        return ResponseEntity.ok(projectService.getAllProjects(pageable));
    }
}
