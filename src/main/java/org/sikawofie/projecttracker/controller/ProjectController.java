package org.sikawofie.projecttracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.dto.ApiResponseDTO;
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

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Project Controller", description = "CRUD operations for projects")
@Validated
public class ProjectController {

    private final ProjectService projectService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a project", description = "Create a new project using the provided ProjectDTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Project created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<ProjectResponseDTO>> createProject(@Valid @RequestBody ProjectRequestDTO projectDTO) {
        ProjectResponseDTO createdProject = projectService.createProject(projectDTO);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdProject.getId())
                .toUri();

        ApiResponseDTO<ProjectResponseDTO> response = ApiResponseDTO.<ProjectResponseDTO>builder()
                .status(201)
                .message("Project created successfully")
                .data(createdProject)
                .build();

        return ResponseEntity.created(location).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update a project", description = "Update an existing project identified by ID using the provided ProjectDTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Project not found with provided ID")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ProjectResponseDTO>> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequestDTO projectDTO) {
        ProjectResponseDTO updatedProject = projectService.updateProject(id, projectDTO);

        ApiResponseDTO<ProjectResponseDTO> response = ApiResponseDTO.<ProjectResponseDTO>builder()
                .status(200)
                .message("Project updated successfully")
                .data(updatedProject)
                .build();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a project", description = "Delete the project with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Project deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found with provided ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);

        ApiResponseDTO<Void> response = ApiResponseDTO.<Void>builder()
                .status(204)
                .message("Project deleted successfully")
                .build();

        return ResponseEntity.status(204).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DEVELOPER', 'CONTRACTOR')")
    @Operation(summary = "Get a project by ID", description = "Retrieve a project using the provided ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found with provided ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ProjectResponseDTO>> getProjectById(@PathVariable Long id) {
        ProjectResponseDTO projectDTO = projectService.getProjectById(id);

        ApiResponseDTO<ProjectResponseDTO> response = ApiResponseDTO.<ProjectResponseDTO>builder()
                .status(200)
                .message("Project retrieved successfully")
                .data(projectDTO)
                .build();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'DEVELOPER', 'CONTRACTOR')")
    @Operation(summary = "Get all projects", description = "Retrieve a paginated list of all projects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of projects retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<ProjectResponseDTO>>> getAllProjects(Pageable pageable) {
        Page<ProjectResponseDTO> projects = projectService.getAllProjects(pageable);

        ApiResponseDTO<Page<ProjectResponseDTO>> response = ApiResponseDTO.<Page<ProjectResponseDTO>>builder()
                .status(200)
                .message("Projects retrieved successfully")
                .data(projects)
                .build();

        return ResponseEntity.ok(response);
    }
}
