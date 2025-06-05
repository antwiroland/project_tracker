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

    @Override
    @CacheEvict(value = "developers", allEntries = true)
    public DeveloperResponseDTO createDeveloper(DeveloperRequestDTO dto) {
        Developer developer = mapToEntity(dto);
        return mapToDTO(developerRepository.save(developer));
    }

    @Override
    @CacheEvict(value = "developers", key = "#id")
    public DeveloperResponseDTO updateDeveloper(Long id, DeveloperRequestDTO updatedDTO) {
        Developer existing = developerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Developer not found with ID: " + id));

        existing.setName(updatedDTO.getName());
        existing.setEmail(updatedDTO.getEmail());
        existing.setSkills(updatedDTO.getSkills());

        return mapToDTO(developerRepository.save(existing));
    }

    @Override
    @CacheEvict(value = "developers", key = "#id")
    public void deleteDeveloper(Long id) {
        if (!developerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Developer not found with ID: " + id);
        }
        developerRepository.deleteById(id);
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
    private DeveloperResponseDTO mapToDTO(Developer developer) {
        DeveloperResponseDTO dto = new DeveloperResponseDTO();
        dto.setId(developer.getId());
        dto.setName(developer.getName());
        dto.setEmail(developer.getEmail());
        dto.setSkills(developer.getSkills());
        return dto;
    }

    private Developer mapToEntity(DeveloperRequestDTO dto) {
        Developer developer = new Developer();
        developer.setName(dto.getName());
        developer.setEmail(dto.getEmail());
        developer.setSkills(dto.getSkills());
        return developer;
    }
}
