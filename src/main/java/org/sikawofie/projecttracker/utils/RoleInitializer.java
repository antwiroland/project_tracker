package org.sikawofie.projecttracker.utils;

import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.entity.Role;
import org.sikawofie.projecttracker.repository.RoleRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements CommandLineRunner {
    private final RoleRepo roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByName(Role.RoleName.ROLE_CONTRACTOR).isEmpty()) {
            roleRepository.save(new Role(Role.RoleName.ROLE_CONTRACTOR));
        }

        if (roleRepository.findByName(Role.RoleName.ROLE_MANAGER).isEmpty()) {
            roleRepository.save(new Role(Role.RoleName.ROLE_MANAGER));
        }

        if (roleRepository.findByName(Role.RoleName.ROLE_DEVELOPER).isEmpty()) {
            roleRepository.save(new Role(Role.RoleName.ROLE_DEVELOPER));
        }

        if (roleRepository.findByName(Role.RoleName.ROLE_ADMIN).isEmpty()) {
            roleRepository.save(new Role(Role.RoleName.ROLE_ADMIN));
        }
    }
}
