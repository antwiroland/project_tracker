package org.sikawofie.projecttracker.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.dto.ApiResponseDTO;
import org.sikawofie.projecttracker.dto.AuthResponseDTO;
import org.sikawofie.projecttracker.dto.UserRequestDTO;
import org.sikawofie.projecttracker.dto.UserResponseDTO;
import org.sikawofie.projecttracker.service.impl.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO userResponseDTO = userService.addUser(userRequestDTO);
        ApiResponseDTO<UserResponseDTO> response =  ApiResponseDTO.<UserResponseDTO>builder().message("User created successfully").status(201).data(userResponseDTO).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        AuthResponseDTO response = userService.verify(userRequestDTO);
        return ResponseEntity.ok(response);
    }
}
