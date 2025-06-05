package org.sikawofie.projecttracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.dto.ProjectRequestDTO;
import org.sikawofie.projecttracker.dto.ProjectResponseDTO;
import org.sikawofie.projecttracker.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Project Controller", description = "CRUD operations for projects")
@Validated
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "Create a project", description = "Create a new project using the provided ProjectDTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Project created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody ProjectRequestDTO projectDTO) {
        ProjectResponseDTO createdProject = projectService.createProject(projectDTO);
        System.out.println("Project created: " + createdProject);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdProject.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdProject);
    }

    @Operation(summary = "Update a project", description = "Update an existing project identified by ID using the provided ProjectDTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Project not found with provided ID")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequestDTO projectDTO) {
        ProjectResponseDTO updatedProject = projectService.updateProject(id, projectDTO);
        return ResponseEntity.ok(updatedProject);
    }

    @Operation(summary = "Delete a project", description = "Delete the project with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found with provided ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get a project by ID", description = "Retrieve a project using the provided ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found with provided ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable Long id) {
        ProjectResponseDTO projectDTO = projectService.getProjectById(id);
        return ResponseEntity.ok(projectDTO);
    }

    @Operation(summary = "Get all projects", description = "Retrieve a paginated list of all projects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of projects retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<Page<ProjectResponseDTO>> getAllProjects(Pageable pageable) {
        Page<ProjectResponseDTO> projects = projectService.getAllProjects(pageable);
        return ResponseEntity.ok(projects);
    }
}
