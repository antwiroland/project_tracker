package org.sikawofie.projecttracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sikawofie.projecttracker.dto.ProjectRequestDTO;
import org.sikawofie.projecttracker.dto.ProjectResponseDTO;
import org.sikawofie.projecttracker.enums.ProjectStatus;
import org.sikawofie.projecttracker.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@Import(ProjectControllerTest.TestConfig.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ProjectService projectService() {
            return Mockito.mock(ProjectService.class);
        }
    }

    @Test
    void createProject_ShouldReturnCreatedProject() throws Exception {
        ProjectRequestDTO request = ProjectRequestDTO.builder()
                .name("New Project")
                .description("Project Description")
                .deadline(LocalDate.now().plusDays(30))
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        ProjectResponseDTO response = ProjectResponseDTO.builder()
                .id(1L)
                .name("New Project")
                .description("Project Description")
                .deadline(LocalDate.now().plusDays(30))
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        Mockito.when(projectService.createProject(any(ProjectRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("New Project"))
                .andExpect(jsonPath("$.data.description").value("Project Description"));
    }

    @Test
    void updateProject_ShouldReturnUpdatedProject() throws Exception {
        ProjectRequestDTO request = ProjectRequestDTO.builder()
                .name("Updated Project")
                .description("Updated Desc")
                .deadline(LocalDate.now())
                .status(ProjectStatus.COMPLETED)
                .build();

        ProjectResponseDTO response = ProjectResponseDTO.builder()
                .id(1L)
                .name("Updated Project")
                .description("Updated Desc")
                .deadline(LocalDate.now())
                .status(ProjectStatus.COMPLETED)
                .build();

        Mockito.when(projectService.updateProject(eq(1L), any(ProjectRequestDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/projects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Updated Project"))
                .andExpect(jsonPath("$.data.description").value("Updated Desc"));
    }

    @Test
    void deleteProject_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/projects/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(projectService).deleteProject(1L);
    }

    @Test
    void getProjectById_ShouldReturnProject() throws Exception {
        ProjectResponseDTO response = ProjectResponseDTO.builder()
                .id(1L)
                .name("Demo Project")
                .description("Desc")
                .deadline(LocalDate.now())
                .status(ProjectStatus.ON_HOLD)
                .build();

        Mockito.when(projectService.getProjectById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Demo Project"))
                .andExpect(jsonPath("$.data.description").value("Desc"));
    }

    @Test
    void getAllProjects_ShouldReturnPaginatedProjects() throws Exception {
        ProjectResponseDTO project1 = ProjectResponseDTO.builder()
                .id(1L)
                .name("Project A")
                .description("Desc A")
                .deadline(LocalDate.now())
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        ProjectResponseDTO project2 = ProjectResponseDTO.builder()
                .id(2L)
                .name("Project B")
                .description("Desc B")
                .deadline(LocalDate.now().plusDays(10))
                .status(ProjectStatus.COMPLETED)
                .build();

        Page<ProjectResponseDTO> page = new PageImpl<>(List.of(project1, project2));

        Mockito.when(projectService.getAllProjects(any())).thenReturn(page);

        mockMvc.perform(get("/api/projects?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[0].name").value("Project A"))
                .andExpect(jsonPath("$.data.content[1].name").value("Project B"));
    }
}
