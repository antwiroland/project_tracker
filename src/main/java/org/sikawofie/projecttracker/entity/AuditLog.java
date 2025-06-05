
package org.sikawofie.projecttracker.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "audit_logs")
@Builder
public class AuditLog {
    @Id
    private String id; // MongoDB ObjectId as a string

    private String actionType;   // e.g. CREATE, UPDATE, DELETE
    private String entityType;   // e.g. "Project", "Developer"
    private String entityId;     // Typically the ID of the target entity (String allows UUIDs or DB-generated IDs)
    private String actorName;    // Who performed the action (could be a username or system)
    private String payload;      // Optional: JSON payload for what changed (e.g. old/new values)
    private Instant timestamp;   // Time the action occurred
}
