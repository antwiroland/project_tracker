package org.sikawofie.projecttracker.repository;

import org.sikawofie.projecttracker.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    List<AuditLog> findByEntityType(String entityType);
    List<AuditLog> findByActorName(String actorName);
    List<AuditLog> findByEntityTypeAndActorName(String entityType, String actorName);

    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);
    Page<AuditLog> findByActorName(String actorName, Pageable pageable);
    Page<AuditLog> findByEntityTypeAndActorName(String entityType, String actorName, Pageable pageable);
}
