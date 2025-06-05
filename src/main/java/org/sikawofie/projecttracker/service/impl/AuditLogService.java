package org.sikawofie.projecttracker.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.entity.AuditLog;
import org.sikawofie.projecttracker.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public void logAction(String actionType, String entityType, String entityId, Object entity, String actorName) {
        try {
            String payload = objectMapper.writeValueAsString(entity);

            AuditLog auditLog = AuditLog.builder()
                    .actionType(actionType)
                    .entityType(entityType)
                    .entityId(entityId)
                    .timestamp(Instant.now())
                    .actorName(actorName)
                    .payload(payload)
                    .build();

            auditLogRepository.save(auditLog);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize audit log payload for entityId {}: {}", entityId, e.getMessage(), e);
        }
    }
}
