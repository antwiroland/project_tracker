package org.sikawofie.projecttracker.service;

import org.sikawofie.projecttracker.dto.DeveloperRequestDTO;
import org.sikawofie.projecttracker.dto.DeveloperResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeveloperService {
    DeveloperResponseDTO createDeveloper(DeveloperRequestDTO developerDTO);
    DeveloperResponseDTO updateDeveloper(Long id, DeveloperRequestDTO updated);
    void deleteDeveloper(Long id);
    Page<DeveloperResponseDTO> getAllDevelopers(Pageable pageable);
    DeveloperResponseDTO getDeveloperById(Long id);
}
