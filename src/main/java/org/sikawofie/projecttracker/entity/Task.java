package org.sikawofie.projecttracker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.sikawofie.projecttracker.enums.TaskStatus;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToMany
    @JoinTable(
            name = "task_developers",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "developer_id")
    )
    private Set<Developer> developers = new HashSet<>();


    public void addDeveloper(Developer developer) {
        this.developers.add(developer);
        developer.getTasks().add(this);
    }

    public void removeDeveloper(Developer developer) {
        this.developers.remove(developer);
        developer.getTasks().remove(this);
    }
}
