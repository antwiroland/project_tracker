package org.sikawofie.projecttracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.dto.DeveloperDTO;
import org.sikawofie.projecttracker.service.DeveloperService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/developers")
@RequiredArgsConstructor
@Tag(name = "Developer", description = "CRUD operation for developer")
public class DeveloperController {

    private final DeveloperService developerService;

    @Operation(summary = "Create Developer", description = "Create a developer using a developerDTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Developer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data input")
    })
    @PostMapping
    public ResponseEntity<DeveloperDTO> createDeveloper(@Valid @RequestBody DeveloperDTO developerDTO) {
        return ResponseEntity.ok(developerService.createDeveloper(developerDTO));
    }

    @Operation(summary = "Update Developer", description = "Update a developer using a developerDTO and id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Developer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Developer with given id not found"),
            @ApiResponse(responseCode = "400", description = "Invalid data input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DeveloperDTO> updateDeveloper(
            @PathVariable Long id,
            @Valid @RequestBody DeveloperDTO developerDTO) {
        return ResponseEntity.ok(developerService.updateDeveloper(id, developerDTO));
    }

    @Operation(summary = "Delete Developer", description = "Delete a developer using developer id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deleting developer successful"),
            @ApiResponse(responseCode = "404", description = "Developer with given id not found"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeveloper(@PathVariable Long id) {
        developerService.deleteDeveloper(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Return List DeveloperDTO", description = "Return a List of developers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List<DeveloperDTO>"),
    })
    @GetMapping
    public ResponseEntity<Page<DeveloperDTO>> getAllDevelopers(Pageable pageable) {
        return ResponseEntity.ok(developerService.getAllDevelopers(pageable));
    }

    @Operation(summary = "Return DeveloperDTO", description = "Return a developer using developer id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "DeveloperDTO"),
            @ApiResponse(responseCode = "404", description = "Developer with given id not found"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<DeveloperDTO> getDeveloperById(@PathVariable Long id) {
        return ResponseEntity.ok(developerService.getDeveloperById(id));
    }
}
