package org.sikawofie.projecttracker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private RoleName name;

    public enum RoleName {
        ROLE_ADMIN, ROLE_MANAGER, ROLE_DEVELOPER, ROLE_CONTRACTOR
    }

    public Role(RoleName name) {
        this.name = name;
    }
}
