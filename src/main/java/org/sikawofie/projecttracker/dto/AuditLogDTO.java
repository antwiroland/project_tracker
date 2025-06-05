package org.sikawofie.projecttracker.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogDTO {
    private String id;
    private String action;
    private String entityType;
    private String entityId;
    private String actionType;
    private String actorName;
    private String payload;
    private LocalDateTime timestamp;
}

