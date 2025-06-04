package org.sikawofie.projecttracker.service;

import org.sikawofie.projecttracker.dto.DeveloperDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeveloperService {
    DeveloperDTO createDeveloper(DeveloperDTO developerDTO);
    DeveloperDTO updateDeveloper(Long id, DeveloperDTO updated);
    void deleteDeveloper(Long id);
    Page<DeveloperDTO> getAllDevelopers(Pageable pageable);
    DeveloperDTO getDeveloperById(Long id);
}
