package org.sikawofie.projecttracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sikawofie.projecttracker.dto.TaskRequestDTO;
import org.sikawofie.projecttracker.dto.TaskResponseDTO;
import org.sikawofie.projecttracker.enums.TaskStatus;
import org.sikawofie.projecttracker.service.TaskService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    public void testCreateTask() throws Exception {
        TaskRequestDTO request = TaskRequestDTO.builder()
                .title("Test Task")
                .description("Task Description")
                .dueDate(LocalDate.now().plusDays(3))
                .status(TaskStatus.TODO)
                .projectId(1L)
                .build();

        TaskResponseDTO responseData = TaskResponseDTO.builder()
                .id(1L)
                .title("Test Task")
                .description("Task Description")
                .dueDate(request.getDueDate())
                .status(request.getStatus())
                .projectId(1L)
                .build();

        when(taskService.createTask(any(TaskRequestDTO.class))).thenReturn(responseData);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/tasks/1"))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Task created successfully"))
                .andExpect(jsonPath("$.data.id").value(responseData.getId().intValue()))
                .andExpect(jsonPath("$.data.title").value(responseData.getTitle()))
                .andExpect(jsonPath("$.data.description").value(responseData.getDescription()))
                .andExpect(jsonPath("$.data.dueDate").value(responseData.getDueDate().toString()))
                .andExpect(jsonPath("$.data.status").value(responseData.getStatus().toString()))
                .andExpect(jsonPath("$.data.projectId").value(responseData.getProjectId().intValue()));

        verify(taskService).createTask(any(TaskRequestDTO.class));
    }

    @Test
    public void testAssignTask() throws Exception {
        Long taskId = 1L;
        Long developerId = 2L;

        TaskResponseDTO responseData = TaskResponseDTO.builder()
                .id(taskId)
                .developerId(developerId)
                .build();

        when(taskService.assignTask(taskId, developerId)).thenReturn(responseData);

        mockMvc.perform(post("/api/tasks/{taskId}/assign/{developerId}", taskId, developerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Task assigned successfully"))
                .andExpect(jsonPath("$.data.id").value(taskId.intValue()))
                .andExpect(jsonPath("$.data.developerId").value(developerId.intValue()));

        verify(taskService).assignTask(taskId, developerId);
    }

    @Test
    public void testDeleteTask() throws Exception {
        Long id = 1L;

        doNothing().when(taskService).deleteTask(id);

        mockMvc.perform(delete("/api/tasks/{id}", id))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(id);
    }

    @Test
    public void testGetTasksByProject() throws Exception {
        Long projectId = 1L;
        TaskResponseDTO task1 = TaskResponseDTO.builder().id(1L).projectId(projectId).build();
        TaskResponseDTO task2 = TaskResponseDTO.builder().id(2L).projectId(projectId).build();

        List<TaskResponseDTO> tasks = List.of(task1, task2);

        when(taskService.getTasksByProjectId(projectId)).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks/project/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Tasks fetched successfully"))
                .andExpect(jsonPath("$.data.length()").value(tasks.size()))
                .andExpect(jsonPath("$.data[0].projectId").value(projectId.intValue()))
                .andExpect(jsonPath("$.data[1].projectId").value(projectId.intValue()));

        verify(taskService).getTasksByProjectId(projectId);
    }

    @Test
    public void testGetTasksByDeveloper() throws Exception {
        Long developerId = 2L;
        TaskResponseDTO task = TaskResponseDTO.builder().id(1L).developerId(developerId).build();

        List<TaskResponseDTO> tasks = List.of(task);

        when(taskService.getTasksByDeveloperId(developerId)).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks/developer/{developerId}", developerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Tasks fetched successfully"))
                .andExpect(jsonPath("$.data[0].developerId").value(developerId.intValue()));

        verify(taskService).getTasksByDeveloperId(developerId);
    }

    @Test
    public void testGetOverdueTasks() throws Exception {
        TaskResponseDTO task = TaskResponseDTO.builder().id(1L).status(TaskStatus.TODO).build();

        List<TaskResponseDTO> tasks = List.of(task);

        when(taskService.getOverdueTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Overdue tasks fetched"))
                .andExpect(jsonPath("$.data.length()").value(tasks.size()));

        verify(taskService).getOverdueTasks();
    }

    @Test
    public void testGetTasksSorted() throws Exception {
        String sortBy = "status";

        TaskResponseDTO task = TaskResponseDTO.builder().id(1L).status(TaskStatus.IN_PROGRESS).build();
        List<TaskResponseDTO> tasks = List.of(task);

        when(taskService.getTasksSorted(sortBy)).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks/sorted")
                        .param("sortBy", sortBy))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Tasks fetched sorted by " + sortBy))
                .andExpect(jsonPath("$.data.length()").value(tasks.size()));

        verify(taskService).getTasksSorted(sortBy);
    }
}
