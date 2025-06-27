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
    @CacheEvict(value = {"developerById", "developersPage"}, allEntries = true)
    public DeveloperResponseDTO createDeveloper(DeveloperRequestDTO dto) {
        Developer developer = mapToEntity(dto);
        Developer saved = developerRepository.save(developer);

        auditLogService.logAction("CREATE", "Developer", saved.getId().toString(), saved, "SYSTEM");

        return mapToDTO(saved);
    }

    @Override
    @CacheEvict(value = {"developerById", "developersPage"}, key = "#id", allEntries = true)
    public DeveloperResponseDTO updateDeveloper(Long id, DeveloperRequestDTO updatedDTO) {
        Developer existing = developerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Developer not found with ID: " + id));

        existing.setName(updatedDTO.getName());
        existing.setEmail(updatedDTO.getEmail());
        existing.setSkills(updatedDTO.getSkills());

        Developer updated = developerRepository.save(existing);

        auditLogService.logAction("UPDATE", "Developer", updated.getId().toString(), updated, "SYSTEM");

        return mapToDTO(updated);
    }

    @Override
    @CacheEvict(value = {"developerById", "developersPage"}, key = "#id", allEntries = true)
    public void deleteDeveloper(Long id) {
        Developer developer = developerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Developer not found with ID: " + id));

        developerRepository.delete(developer);

        auditLogService.logAction("DELETE", "Developer", developer.getId().toString(), developer, "SYSTEM");
    }

    @Override
    @Cacheable(value = "developersPage", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<DeveloperResponseDTO> getAllDevelopers(Pageable pageable) {
        return developerRepository.findAll(pageable).map(this::mapToDTO);
    }

    @Override
    @Cacheable(value = "developerById", key = "#id")
    public DeveloperResponseDTO getDeveloperById(Long id) {
        Developer developer = developerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Developer not found with ID: " + id));
        return mapToDTO(developer);
    }

    // Mapping helpers
    public DeveloperResponseDTO mapToDTO(Developer developer) {
        return DeveloperResponseDTO.builder()
                .id(developer.getId())
                .name(developer.getName())
                .email(developer.getEmail())
                .skills(developer.getSkills())
                .build();
    }

    public Developer mapToEntity(DeveloperRequestDTO dto) {
        Developer developer = new Developer();
        developer.setName(dto.getName());
        developer.setEmail(dto.getEmail());
        developer.setSkills(dto.getSkills());
        return developer;
    }
}
