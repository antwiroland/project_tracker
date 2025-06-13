package org.sikawofie.projecttracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.dto.ApiResponseDTO;
import org.sikawofie.projecttracker.dto.AuthResponseDTO;
import org.sikawofie.projecttracker.dto.UserRequestDTO;
import org.sikawofie.projecttracker.dto.UserResponseDTO;
import org.sikawofie.projecttracker.service.impl.AuthServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthServiceImpl userService;

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

    @GetMapping("/oauth2/success")
    public String handleSuccess(Principal principal) {
        return principal.getName();
    }
}
