package org.sikawofie.projecttracker.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sikawofie.projecttracker.dto.DeveloperRequestDTO;
import org.sikawofie.projecttracker.dto.DeveloperResponseDTO;
import org.sikawofie.projecttracker.entity.Developer;
import org.sikawofie.projecttracker.exception.ResourceNotFoundException;
import org.sikawofie.projecttracker.repository.DeveloperRepository;
import org.sikawofie.projecttracker.service.impl.AuditLogService;
import org.sikawofie.projecttracker.service.impl.DeveloperServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeveloperServiceImplTest {

    @Mock
    private DeveloperRepository developerRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private DeveloperServiceImpl developerService;

    private Developer developer;
    private DeveloperRequestDTO requestDTO;
    private DeveloperResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        developer = new Developer();
        developer.setId(1L);
        developer.setName("John Doe");
        developer.setEmail("john@example.com");
        developer.setSkills(List.of("Java", "Spring"));

        requestDTO = new DeveloperRequestDTO();
        requestDTO.setName("John Doe");
        requestDTO.setEmail("john@example.com");
        requestDTO.setSkills(List.of("Java", "Spring"));

        responseDTO = new DeveloperResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("John Doe");
        responseDTO.setEmail("john@example.com");
        responseDTO.setSkills(List.of("Java", "Spring"));
    }

    @Test
    void createDeveloper_Success() {
        when(developerRepository.save(any(Developer.class))).thenReturn(developer);

        DeveloperResponseDTO result = developerService.createDeveloper(requestDTO);

        assertNotNull(result);
        assertEquals(developer.getId(), result.getId());
        verify(developerRepository, times(1)).save(any(Developer.class));
        verify(auditLogService, times(1)).logAction(
                eq("CREATE"),
                eq("Developer"),
                anyString(),
                any(Developer.class),
                eq("SYSTEM"));
    }

    @Test
    void updateDeveloper_Success() {
        when(developerRepository.findById(1L)).thenReturn(Optional.of(developer));
        when(developerRepository.save(any(Developer.class))).thenReturn(developer);

        DeveloperResponseDTO result = developerService.updateDeveloper(1L, requestDTO);

        assertNotNull(result);
        assertEquals(developer.getId(), result.getId());
        verify(developerRepository, times(1)).save(developer);
        verify(auditLogService, times(1)).logAction(
                eq("UPDATE"),
                eq("Developer"),
                anyString(),
                any(Developer.class),
                eq("SYSTEM"));
    }

    @Test
    void updateDeveloper_NotFound() {
        when(developerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            developerService.updateDeveloper(1L, requestDTO);
        });

        verify(developerRepository, never()).save(any(Developer.class));
    }

    @Test
    void deleteDeveloper_Success() {
        when(developerRepository.findById(1L)).thenReturn(Optional.of(developer));
        doNothing().when(developerRepository).delete(developer);

        developerService.deleteDeveloper(1L);

        verify(developerRepository, times(1)).delete(developer);
        verify(auditLogService, times(1)).logAction(
                eq("DELETE"),
                eq("Developer"),
                anyString(),
                any(Developer.class),
                eq("SYSTEM"));
    }

    @Test
    void deleteDeveloper_NotFound() {
        when(developerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            developerService.deleteDeveloper(1L);
        });

        verify(developerRepository, never()).delete(any(Developer.class));
    }

    @Test
    void getAllDevelopers_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        when(developerRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(developer)));

        Page<DeveloperResponseDTO> result = developerService.getAllDevelopers(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(developer.getId(), result.getContent().get(0).getId());
    }

    @Test
    void getAllDevelopers_Empty() {
        Pageable pageable = PageRequest.of(0, 10);
        when(developerRepository.findAll(pageable)).thenReturn(Page.empty());

        Page<DeveloperResponseDTO> result = developerService.getAllDevelopers(pageable);

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void getDeveloperById_Success() {
        when(developerRepository.findById(1L)).thenReturn(Optional.of(developer));

        DeveloperResponseDTO result = developerService.getDeveloperById(1L);

        assertNotNull(result);
        assertEquals(developer.getId(), result.getId());
        assertEquals(developer.getName(), result.getName());
    }

    @Test
    void getDeveloperById_NotFound() {
        when(developerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            developerService.getDeveloperById(1L);
        });
    }

    @Test
    void mapToDTO_Success() {
        DeveloperResponseDTO dto = developerService.mapToDTO(developer);

        assertNotNull(dto);
        assertEquals(developer.getId(), dto.getId());
        assertEquals(developer.getName(), dto.getName());
        assertEquals(developer.getEmail(), dto.getEmail());
        assertEquals(developer.getSkills(), dto.getSkills());
    }

    @Test
    void mapToEntity_Success() {
        Developer entity = developerService.mapToEntity(requestDTO);

        assertNotNull(entity);
        assertEquals(requestDTO.getName(), entity.getName());
        assertEquals(requestDTO.getEmail(), entity.getEmail());
        assertEquals(requestDTO.getSkills(), entity.getSkills());
    }
}