package org.sikawofie.projecttracker.logger;
import org.sikawofie.projecttracker.dto.AuditLogDTO;
import org.sikawofie.projecttracker.entity.AuditLog;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class AuditLogMapper {
    public AuditLogDTO toDTO(AuditLog log) {
        return AuditLogDTO.builder()
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .action(log.getActionType())
                .actorName(log.getActorName())
                .timestamp(log.getTimestamp().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .build();
    }
}
