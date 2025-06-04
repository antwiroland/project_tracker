package org.sikawofie.projecttracker.service.impl;// --- AuditLogService.java ---

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.entity.AuditLog;
import org.sikawofie.projecttracker.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public void logAction(String actionType, String entityType, String entityId, Object entity, String actorName) {
        try {
            String payload = objectMapper.writeValueAsString(entity);
            AuditLog auditLog = new AuditLog();
            auditLog.setActionType(actionType);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setTimestamp(Instant.now());
            auditLog.setActorName(actorName);
            auditLog.setPayload(payload);

            auditLogRepository.save(auditLog);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
