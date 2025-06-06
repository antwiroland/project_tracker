package org.sikawofie.projecttracker.controller;

import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuditLogController.class)
@Import(AuditLogControllerTest.TestConfig.class)
public class AuditLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AuditLogMapper auditLogMapper;

    private AuditLog mockAuditLog;
    private AuditLogDTO mockAuditLogDTO;

    @BeforeEach
    void setUp() {
        Instant fixedInstant = Instant.parse("2024-01-01T12:00:00Z");
        LocalDateTime fixedDateTime = LocalDateTime.ofInstant(fixedInstant, ZoneId.systemDefault());

        mockAuditLog = AuditLog.builder()
                .id("1")
                .entityType("Project")
                .entityId("42")
                .actionType("CREATE")
                .actorName("John Doe")
                .payload("{\"key\":\"value\"}")
                .timestamp(fixedInstant)
                .build();

        mockAuditLogDTO = AuditLogDTO.builder()
                .id("1")
                .entityType("Project")
                .entityId("42")
                .actionType("CREATE")
                .actorName("John Doe")
                .payload("{\"key\":\"value\"}")
                .timestamp(fixedDateTime)
                .build();
    }

    @Test
    void getLogs_ShouldReturnAllLogs() throws Exception {
        when(auditLogRepository.findAll()).thenReturn(List.of(mockAuditLog));
        when(auditLogMapper.toDTO(any(AuditLog.class))).thenReturn(mockAuditLogDTO);

        mockMvc.perform(get("/api/logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].entityType").value("Project"))
                .andExpect(jsonPath("$[0].actorName").value("John Doe"));
    }

    @Test
    void getLogsPaged_ShouldReturnPagedLogs() throws Exception {
        Page<AuditLog> page = new PageImpl<>(List.of(mockAuditLog));
        when(auditLogRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(auditLogMapper.toDTO(any(AuditLog.class))).thenReturn(mockAuditLogDTO);

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
