package org.sikawofie.projecttracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.dto.AuditLogDTO;
import org.sikawofie.projecttracker.entity.AuditLog;
import org.sikawofie.projecttracker.logger.AuditLogMapper;
import org.sikawofie.projecttracker.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Tag(name = "Audit Log", description = "Operations related to audit logging and log retrieval")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @GetMapping
    @Operation(
            summary = "Get audit logs (non-paged)",
            description = "Retrieve a list of audit logs filtered by optional parameters: entityType and actorName"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of audit logs")
    })
    public ResponseEntity<List<AuditLogDTO>> getLogs(
            @Parameter(description = "Entity type to filter logs by") @RequestParam(required = false) String entityType,
            @Parameter(description = "Actor name to filter logs by") @RequestParam(required = false) String actorName) {

        List<AuditLog> logs;

        if (entityType != null && actorName != null) {
            logs = auditLogRepository.findByEntityTypeAndActorName(entityType, actorName);
        } else if (entityType != null) {
            logs = auditLogRepository.findByEntityType(entityType);
        } else if (actorName != null) {
            logs = auditLogRepository.findByActorName(actorName);
        } else {
            logs = auditLogRepository.findAll();
        }

        List<AuditLogDTO> dtos = logs.stream()
                .map(auditLogMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/paged")
    @Operation(
            summary = "Get audit logs (paged)",
            description = "Retrieve a paginated list of audit logs with optional filters for entityType and actorName"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved paginated audit logs")
    })
    public ResponseEntity<Page<AuditLogDTO>> getLogsPaged(
            @Parameter(description = "Entity type to filter logs by") @RequestParam(required = false) String entityType,
            @Parameter(description = "Actor name to filter logs by") @RequestParam(required = false) String actorName,
            @Parameter(description = "Pagination and sorting information") Pageable pageable) {

        Page<AuditLog> logs;

        if (entityType != null && actorName != null) {
            logs = auditLogRepository.findByEntityTypeAndActorName(entityType, actorName, pageable);
        } else if (entityType != null) {
            logs = auditLogRepository.findByEntityType(entityType, pageable);
        } else if (actorName != null) {
            logs = auditLogRepository.findByActorName(actorName, pageable);
        } else {
            logs = auditLogRepository.findAll(pageable);
        }

        return ResponseEntity.ok(logs.map(auditLogMapper::toDTO));
    }
}
