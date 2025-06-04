package org.sikawofie.projecttracker.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLogDTO {
    private String action;
    private String entityType;
    private String entityId;
    private String actorName;
    private LocalDateTime timestamp;
}

