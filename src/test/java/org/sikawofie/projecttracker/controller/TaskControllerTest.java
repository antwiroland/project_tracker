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
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        TaskResponseDTO response = TaskResponseDTO.builder()
                .id(1L)
                .title("Test Task")
                .description("Task Description")
                .dueDate(request.getDueDate())
                .status(request.getStatus())
                .projectId(1L)
                .build();

        when(taskService.createTask(request)).thenReturn(response);

        // Perform POST request using MockMvc and check response
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/tasks/1"))
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.title").value(response.getTitle()))
                .andExpect(jsonPath("$.description").value(response.getDescription()))
                .andExpect(jsonPath("$.dueDate").value(response.getDueDate().toString()))
                .andExpect(jsonPath("$.status").value(response.getStatus().toString()))
                .andExpect(jsonPath("$.projectId").value(response.getProjectId()));

        verify(taskService).createTask(request);
    }

    @Test
    public void testAssignTask() {
        Long taskId = 1L;
        Long developerId = 2L;

        TaskResponseDTO response = TaskResponseDTO.builder()
                .id(taskId)
                .developerId(developerId)
                .build();

        when(taskService.assignTask(taskId, developerId)).thenReturn(response);

        ResponseEntity<TaskResponseDTO> result = taskController.assignTask(taskId, developerId);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody().getDeveloperId()).isEqualTo(developerId);
    }

    @Test
    public void testDeleteTask() {
        Long id = 1L;

        doNothing().when(taskService).deleteTask(id);

        ResponseEntity<Void> result = taskController.deleteTask(id);

        assertThat(result.getStatusCodeValue()).isEqualTo(204);
        verify(taskService).deleteTask(id);
    }

    @Test
    public void testGetTasksByProject() {
        Long projectId = 1L;
        TaskResponseDTO task = TaskResponseDTO.builder().id(1L).projectId(projectId).build();
        when(taskService.getTasksByProjectId(projectId)).thenReturn(List.of(task));

        ResponseEntity<List<TaskResponseDTO>> result = taskController.getTasksByProject(projectId);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    public void testGetTasksByDeveloper() {
        Long developerId = 2L;
        TaskResponseDTO task = TaskResponseDTO.builder().id(1L).developerId(developerId).build();
        when(taskService.getTasksByDeveloperId(developerId)).thenReturn(List.of(task));

        ResponseEntity<List<TaskResponseDTO>> result = taskController.getTasksByDeveloper(developerId);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody().get(0).getDeveloperId()).isEqualTo(developerId);
    }

    @Test
    public void testGetOverdueTasks() {
        TaskResponseDTO task = TaskResponseDTO.builder().id(1L).status(TaskStatus.TODO).build();
        when(taskService.getOverdueTasks()).thenReturn(List.of(task));

        ResponseEntity<List<TaskResponseDTO>> result = taskController.getOverdueTasks();

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).hasSize(1);
    }

    @Test
    public void testGetTasksSorted() {
        String sortBy = "status";
        TaskResponseDTO task = TaskResponseDTO.builder().id(1L).status(TaskStatus.IN_PROGRESS).build();
        when(taskService.getTasksSorted(sortBy)).thenReturn(List.of(task));

        ResponseEntity<List<TaskResponseDTO>> result = taskController.getTasksSorted(sortBy);

        assertThat(result.getStatusCodeValue()).isEqualTo(200);
        assertThat(result.getBody()).hasSize(1);
    }
}
