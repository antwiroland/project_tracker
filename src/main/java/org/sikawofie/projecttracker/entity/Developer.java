package org.sikawofie.projecttracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "developers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Developer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Developer name is required")
    private String name;

    @Email(message = "Must be a valid email")
    @Column(unique = true)
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "developer_skills", joinColumns = @JoinColumn(name = "developer_id"))
    @Column(name = "skill")
    private List<String> skills = new ArrayList<>();

    @ManyToMany(mappedBy = "developers", fetch = FetchType.LAZY)
    private Set<Task> tasks = new HashSet<>();


    public void addTask(Task task) {
        this.tasks.add(task);
        task.getDevelopers().add(this);
    }

    public void removeTask(Task task) {
        this.tasks.remove(task);
        task.getDevelopers().remove(this);
    }
}
