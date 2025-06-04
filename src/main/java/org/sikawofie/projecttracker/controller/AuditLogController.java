package org.sikawofie.projecttracker.controller;

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
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @GetMapping
    public ResponseEntity<List<AuditLogDTO>> getLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String actorName) {

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
    public ResponseEntity<Page<AuditLogDTO>> getLogsPaged(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String actorName,
            Pageable pageable) {

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