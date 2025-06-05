package org.sikawofie.projecttracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sikawofie.projecttracker.dto.DeveloperRequestDTO;
import org.sikawofie.projecttracker.dto.DeveloperResponseDTO;
import org.sikawofie.projecttracker.service.DeveloperService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeveloperController.class)
@Import(DeveloperControllerTest.MockConfig.class)
class DeveloperControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeveloperService developerService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public DeveloperService developerService() {
            return Mockito.mock(DeveloperService.class);
        }
    }

    @Test
    void createDeveloper_ShouldReturnCreatedDeveloper() throws Exception {
        DeveloperRequestDTO request = new DeveloperRequestDTO(
                "Alice",
                "alice@example.com",
                List.of("Java", "Spring")
        );

        DeveloperResponseDTO response = new DeveloperResponseDTO();
        response.setId(1L);
        response.setName("Alice");
        response.setEmail("alice@example.com");
        response.setSkills(List.of("Java", "Spring"));

        Mockito.when(developerService.createDeveloper(any())).thenReturn(response);

        mockMvc.perform(post("/api/developers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.skills[0]").value("Java"))
                .andExpect(jsonPath("$.skills[1]").value("Spring"));
    }

    @Test
    void updateDeveloper_ShouldReturnUpdatedDeveloper() throws Exception {
        DeveloperRequestDTO request = new DeveloperRequestDTO(
                "Bob",
                "bob@example.com",
                List.of("Python", "Django")
        );

        DeveloperResponseDTO response = new DeveloperResponseDTO();
        response.setId(2L);
        response.setName("Bob");
        response.setEmail("bob@example.com");
        response.setSkills(List.of("Python", "Django"));

        Mockito.when(developerService.updateDeveloper(eq(2L), any())).thenReturn(response);

        mockMvc.perform(put("/api/developers/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Bob"))
                .andExpect(jsonPath("$.email").value("bob@example.com"))
                .andExpect(jsonPath("$.skills[0]").value("Python"))
                .andExpect(jsonPath("$.skills[1]").value("Django"));
    }

    @Test
    void deleteDeveloper_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/developers/3"))
                .andExpect(status().isNoContent());

        Mockito.verify(developerService).deleteDeveloper(3L);
    }

    @Test
    void getDeveloperById_ShouldReturnDeveloper() throws Exception {
        DeveloperResponseDTO response = new DeveloperResponseDTO();
        response.setId(4L);
        response.setName("Charlie");
        response.setEmail("charlie@example.com");
        response.setSkills(List.of("JavaScript", "React"));

        Mockito.when(developerService.getDeveloperById(4L)).thenReturn(response);

        mockMvc.perform(get("/api/developers/4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4L))
                .andExpect(jsonPath("$.name").value("Charlie"))
                .andExpect(jsonPath("$.email").value("charlie@example.com"))
                .andExpect(jsonPath("$.skills[0]").value("JavaScript"))
                .andExpect(jsonPath("$.skills[1]").value("React"));
    }

    @Test
    void getAllDevelopers_ShouldReturnPaginatedList() throws Exception {
        DeveloperResponseDTO dev1 = new DeveloperResponseDTO();
        dev1.setId(1L);
        dev1.setName("Alice");
        dev1.setEmail("alice@example.com");
        dev1.setSkills(List.of("Java", "Spring"));

        DeveloperResponseDTO dev2 = new DeveloperResponseDTO();
        dev2.setId(2L);
        dev2.setName("Bob");
        dev2.setEmail("bob@example.com");
        dev2.setSkills(List.of("Python", "Django"));

        Page<DeveloperResponseDTO> page = new PageImpl<>(List.of(dev1, dev2));

        Mockito.when(developerService.getAllDevelopers(any())).thenReturn(page);

        mockMvc.perform(get("/api/developers?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Alice"))
                .andExpect(jsonPath("$.content[0].email").value("alice@example.com"))
                .andExpect(jsonPath("$.content[0].skills[0]").value("Java"))
                .andExpect(jsonPath("$.content[0].skills[1]").value("Spring"))
                .andExpect(jsonPath("$.content[1].name").value("Bob"))
                .andExpect(jsonPath("$.content[1].email").value("bob@example.com"))
                .andExpect(jsonPath("$.content[1].skills[0]").value("Python"))
                .andExpect(jsonPath("$.content[1].skills[1]").value("Django"));
    }
}
