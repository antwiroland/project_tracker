package org.sikawofie.projecttracker.utils;

import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.entity.Task;
import org.sikawofie.projecttracker.repository.TaskRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {
    private final TaskRepository taskRepository;

    public boolean isTaskOwner(String username, Long taskId) {
        Task task = taskRepository.findById(taskId).orElse(null);
        return task != null && task.getDeveloper() != null &&
                task.getDeveloper().getEmail().equals(username);
    }
}

