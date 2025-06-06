package org.sikawofie.projecttracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.dto.ApiResponseDTO;
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
    public ResponseEntity<ApiResponseDTO<DeveloperResponseDTO>> createDeveloper(@Valid @RequestBody DeveloperRequestDTO developerDTO) {
        DeveloperResponseDTO createdDeveloper = developerService.createDeveloper(developerDTO);

        ApiResponseDTO<DeveloperResponseDTO> response = ApiResponseDTO.<DeveloperResponseDTO>builder()
                .status(201)
                .message("Developer created successfully")
                .data(createdDeveloper)
                .build();

        return ResponseEntity.status(201).body(response);
    }

    @Operation(summary = "Update a developer", description = "Update an existing developer identified by ID using the provided DeveloperDTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Developer updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Developer not found with given ID")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<DeveloperResponseDTO>> updateDeveloper(
            @PathVariable Long id,
            @Valid @RequestBody DeveloperRequestDTO developerDTO) {
        DeveloperResponseDTO updatedDeveloper = developerService.updateDeveloper(id, developerDTO);

        ApiResponseDTO<DeveloperResponseDTO> response = ApiResponseDTO.<DeveloperResponseDTO>builder()
                .status(200)
                .message("Developer updated successfully")
                .data(updatedDeveloper)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a developer", description = "Delete the developer with the specified ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Developer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Developer not found with given ID")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteDeveloper(@PathVariable Long id) {
        developerService.deleteDeveloper(id);

        ApiResponseDTO<Void> response = ApiResponseDTO.<Void>builder()
                .status(204)
                .message("Developer deleted successfully")
                .build();

        return ResponseEntity.status(204).body(response);
    }

    @Operation(summary = "Get all developers", description = "Retrieve a paginated list of all developers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of developers retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<DeveloperResponseDTO>>> getAllDevelopers(Pageable pageable) {
        Page<DeveloperResponseDTO> developers = developerService.getAllDevelopers(pageable);

        ApiResponseDTO<Page<DeveloperResponseDTO>> response = ApiResponseDTO.<Page<DeveloperResponseDTO>>builder()
                .status(200)
                .message("Developers retrieved successfully")
                .data(developers)
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get developer by ID", description = "Retrieve a developer using the provided ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Developer retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Developer not found with given ID")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<DeveloperResponseDTO>> getDeveloperById(@PathVariable Long id) {
        DeveloperResponseDTO developerDTO = developerService.getDeveloperById(id);

        ApiResponseDTO<DeveloperResponseDTO> response = ApiResponseDTO.<DeveloperResponseDTO>builder()
                .status(200)
                .message("Developer retrieved successfully")
                .data(developerDTO)
                .build();

        return ResponseEntity.ok(response);
    }
}
