package org.sikawofie.projecttracker.service;

import jakarta.validation.constraints.Positive;
import org.sikawofie.projecttracker.dto.TaskDTO;

import java.util.List;

public interface TaskService {
    TaskDTO createTask(TaskDTO taskDTO);
    TaskDTO assignTask(Long taskId, Long developerId);
    void deleteTask(Long taskId);
    List<TaskDTO> getTasksByProjectId(Long projectId);
    List<TaskDTO> getTasksByDeveloperId(Long developerId);
    List<TaskDTO> getOverdueTasks();
    List<TaskDTO> getTasksSorted(String sortBy);
}
