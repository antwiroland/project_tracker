package org.sikawofie.projecttracker.service.impl;

import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.dto.DeveloperRequestDTO;
import org.sikawofie.projecttracker.dto.DeveloperResponseDTO;
import org.sikawofie.projecttracker.entity.Developer;
import org.sikawofie.projecttracker.exception.ResourceNotFoundException;
import org.sikawofie.projecttracker.repository.DeveloperRepository;
import org.sikawofie.projecttracker.service.DeveloperService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeveloperServiceImpl implements DeveloperService {

    private final DeveloperRepository developerRepository;
    private final AuditLogService auditLogService;

    @Override
    @CacheEvict(value = "developers", allEntries = true)
    public DeveloperResponseDTO createDeveloper(DeveloperRequestDTO dto) {
        Developer developer = mapToEntity(dto);
        Developer saved = developerRepository.save(developer);

        auditLogService.logAction(
                "CREATE",
                "Developer",
                saved.getId().toString(),
                saved,
                "SYSTEM"
        );

        return mapToDTO(saved);
    }

    @Override
    @CacheEvict(value = "developers", key = "#id")
    public DeveloperResponseDTO updateDeveloper(Long id, DeveloperRequestDTO updatedDTO) {
        Developer existing = developerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Developer not found with ID: " + id));

        existing.setName(updatedDTO.getName());
        existing.setEmail(updatedDTO.getEmail());
        existing.setSkills(updatedDTO.getSkills());

        Developer updated = developerRepository.save(existing);

        auditLogService.logAction(
                "UPDATE",
                "Developer",
                updated.getId().toString(),
                updated,
                "SYSTEM"
        );

        return mapToDTO(updated);
    }

    @Override
    @CacheEvict(value = "developers", key = "#id")
    public void deleteDeveloper(Long id) {
        Developer developer = developerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Developer not found with ID: " + id));

        developerRepository.delete(developer);

        auditLogService.logAction(
                "DELETE",
                "Developer",
                developer.getId().toString(),
                developer,
                "SYSTEM"
        );
    }

    @Override
    @Cacheable(value = "developers")
    public Page<DeveloperResponseDTO> getAllDevelopers(Pageable pageable) {
        return developerRepository.findAll(pageable).map(this::mapToDTO);
    }

    @Override
    @Cacheable(value = "developers", key = "#id")
    public DeveloperResponseDTO getDeveloperById(Long id) {
        Developer developer = developerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Developer not found with ID: " + id));
        return mapToDTO(developer);
    }

    // Helper methods
    public DeveloperResponseDTO mapToDTO(Developer developer) {
        DeveloperResponseDTO dto = new DeveloperResponseDTO();
        dto.setId(developer.getId());
        dto.setName(developer.getName());
        dto.setEmail(developer.getEmail());
        dto.setSkills(developer.getSkills());
        return dto;
    }

    public Developer mapToEntity(DeveloperRequestDTO dto) {
        Developer developer = new Developer();
        developer.setName(dto.getName());
        developer.setEmail(dto.getEmail());
        developer.setSkills(dto.getSkills());
        return developer;
    }
}
