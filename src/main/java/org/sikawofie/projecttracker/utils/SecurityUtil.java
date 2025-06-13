//package org.sikawofie.projecttracker.utils;
//
//import lombok.RequiredArgsConstructor;
//import org.sikawofie.projecttracker.entity.Task;
//import org.sikawofie.projecttracker.repository.TaskRepository;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class SecurityUtil {
//    private TaskRepository taskRepository;
//
//    public boolean isTaskOwner(String username, Long taskId) {
//        Task task = taskRepository.findById(taskId).orElse(null);
//        return task != null && task.getAssignedTo().getEmail().equals(username);
//    }
//}
