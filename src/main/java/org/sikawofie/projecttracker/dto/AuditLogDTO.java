package org.sikawofie.projecttracker.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

