package org.sikawofie.projecttracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.dto.DeveloperRequestDTO;
import org.sikawofie.projecttracker.dto.DeveloperResponseDTO;
import org.sikawofie.projecttracker.service.DeveloperService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/developers")
@RequiredArgsConstructor
@Tag(name = "Developer Controller", description = "CRUD operations for developers")
public class DeveloperController {

    private final DeveloperService developerService;

    @Operation(summary = "Create a developer", description = "Create a new developer using the provided DeveloperDTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Developer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<DeveloperResponseDTO> createDeveloper(@Valid @RequestBody DeveloperRequestDTO developerDTO) {
        DeveloperResponseDTO createdDeveloper = developerService.createDeveloper(developerDTO);
        return ResponseEntity.status(201).body(createdDeveloper);
    }

    @Operation(summary = "Update a developer", description = "Update an existing developer identified by ID using the provided DeveloperDTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Developer updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Developer not found with given ID")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DeveloperResponseDTO> updateDeveloper(
            @PathVariable Long id,
            @Valid @RequestBody DeveloperRequestDTO developerDTO) {
        DeveloperResponseDTO updatedDeveloper = developerService.updateDeveloper(id, developerDTO);
        return ResponseEntity.ok(updatedDeveloper);
    }

    @Operation(summary = "Delete a developer", description = "Delete the developer with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Developer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Developer not found with given ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeveloper(@PathVariable Long id) {
        developerService.deleteDeveloper(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all developers", description = "Retrieve a paginated list of all developers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of developers retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<Page<DeveloperResponseDTO>> getAllDevelopers(Pageable pageable) {
        Page<DeveloperResponseDTO> developers = developerService.getAllDevelopers(pageable);
        return ResponseEntity.ok(developers);
    }

    @Operation(summary = "Get developer by ID", description = "Retrieve a developer using the provided ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Developer retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Developer not found with given ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DeveloperResponseDTO> getDeveloperById(@PathVariable Long id) {
        DeveloperResponseDTO developerDTO = developerService.getDeveloperById(id);
        return ResponseEntity.ok(developerDTO);
    }
}
