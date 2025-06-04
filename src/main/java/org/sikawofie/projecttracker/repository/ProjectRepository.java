package org.sikawofie.projecttracker.repository;

import org.sikawofie.projecttracker.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {

}
