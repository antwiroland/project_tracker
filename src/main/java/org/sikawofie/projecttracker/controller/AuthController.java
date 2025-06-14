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

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller", description = "Handles registration, login, and OAuth2 authentication")
public class AuthController {

    private final AuthServiceImpl userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new local user with ROLE_DEVELOPER or specified role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or email already exists")
    })
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO userResponseDTO = userService.addUser(userRequestDTO);
        ApiResponseDTO<UserResponseDTO> response = ApiResponseDTO.<UserResponseDTO>builder()
                .message("User created successfully")
                .status(201)
                .data(userResponseDTO)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user and return JWT", description = "Authenticates user with email and password, returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, JWT returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - wrong credentials")
    })
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        AuthResponseDTO response = userService.verify(userRequestDTO);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/oauth2/success")
    public ResponseEntity<AuthResponseDTO> oauthSuccess(@RequestParam("token") String token) {
        AuthResponseDTO response = AuthResponseDTO.builder().status(200).message("Authentication successful").token(token).build();
        return ResponseEntity.ok(response);
    }

}
