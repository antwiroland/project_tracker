package org.sikawofie.projecttracker.controller;

import org.junit.jupiter.api.Test;
import org.sikawofie.projecttracker.dto.AuditLogDTO;
import org.sikawofie.projecttracker.entity.AuditLog;
import org.sikawofie.projecttracker.logger.AuditLogMapper;
import org.sikawofie.projecttracker.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuditLogController.class)
@Import(AuditLogControllerTest.TestConfig.class)
public class AuditLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AuditLogMapper auditLogMapper;

    private final AuditLog mockAuditLog = AuditLog.builder()
            .id("1")
            .entityType("Project")
            .entityId("42")
            .actionType("CREATE")
            .actorName("John Doe")
            .payload("{\"key\":\"value\"}")
            .timestamp(Instant.now())
            .build();

    private final AuditLogDTO mockAuditLogDTO = AuditLogDTO.builder()
            .id("1")
            .entityType("Project")
            .entityId("42")
            .actionType("CREATE")
            .actorName("John Doe")
            .payload("{\"key\":\"value\"}")
            .timestamp(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()))
            .build();

    @Test
    void getLogs_ShouldReturnAllLogs() throws Exception {
        when(auditLogRepository.findAll()).thenReturn(singletonList(mockAuditLog));
        when(auditLogMapper.toDTO(any())).thenReturn(mockAuditLogDTO);

        mockMvc.perform(get("/api/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].entityType").value("Project"))
                .andExpect(jsonPath("$[0].actorName").value("John Doe"));
    }

    @Test
    void getLogsPaged_ShouldReturnPagedLogs() throws Exception {
        Page<AuditLog> page = new PageImpl<>(List.of(mockAuditLog));
        when(auditLogRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(auditLogMapper.toDTO(any())).thenReturn(mockAuditLogDTO);

        mockMvc.perform(get("/api/logs/paged?page=0&size=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].entityType").value("Project"))
                .andExpect(jsonPath("$.content[0].actorName").value("John Doe"));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AuditLogRepository auditLogRepository() {
            return mock(AuditLogRepository.class);
        }

        @Bean
        public AuditLogMapper auditLogMapper() {
            return mock(AuditLogMapper.class);
        }
    }
}
