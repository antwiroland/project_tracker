package org.sikawofie.projecttracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String email;
    private String password;
    boolean oauthUser;


    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles;
}
