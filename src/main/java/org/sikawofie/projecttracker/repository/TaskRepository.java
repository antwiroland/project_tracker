package org.sikawofie.projecttracker.repository;

import org.sikawofie.projecttracker.entity.Task;
import org.sikawofie.projecttracker.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {


    List<Task> findByProjectId(Long projectId);


    List<Task> findByDevelopers_Id(Long developerId);


    List<Task> findByDueDateBeforeAndStatusNot(LocalDate date, TaskStatus status);
}
