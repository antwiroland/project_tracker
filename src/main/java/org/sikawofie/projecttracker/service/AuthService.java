package org.sikawofie.projecttracker.service;

import org.sikawofie.projecttracker.dto.AuthResponseDTO;
import org.sikawofie.projecttracker.dto.UserRequestDTO;
import org.sikawofie.projecttracker.dto.UserResponseDTO;

public interface AuthService {
    UserResponseDTO addUser(UserRequestDTO userRequestDTO);
    AuthResponseDTO verify(UserRequestDTO userRequestDTO);
}
