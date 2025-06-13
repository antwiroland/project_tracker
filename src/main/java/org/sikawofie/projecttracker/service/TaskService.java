package org.sikawofie.projecttracker.service;

import org.sikawofie.projecttracker.dto.TaskRequestDTO;
import org.sikawofie.projecttracker.dto.TaskResponseDTO;

import java.util.List;

public interface TaskService {
    List<TaskResponseDTO> getAllTasks();
    TaskResponseDTO createTask(TaskRequestDTO taskDTO);
    TaskResponseDTO assignTask(Long taskId, Long developerId);
    void deleteTask(Long taskId);
    List<TaskResponseDTO> getTasksByProjectId(Long projectId);
    List<TaskResponseDTO> getTasksByDeveloperId(Long developerId);
    List<TaskResponseDTO> getOverdueTasks();
    List<TaskResponseDTO> getTasksSorted(String sortBy);

    TaskResponseDTO updateTask(long id, TaskRequestDTO taskRequestDTO);
}
