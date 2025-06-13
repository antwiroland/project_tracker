package org.sikawofie.projecttracker.entity;

import jakarta.persistence.*;

@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleName name;

    public enum RoleName {
        ROLE_ADMIN, ROLE_MANAGER, ROLE_DEVELOPER, ROLE_CONTRACTOR
    }
}
