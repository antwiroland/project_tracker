package org.sikawofie.projecttracker.service.impl;

import lombok.RequiredArgsConstructor;
import org.sikawofie.projecttracker.dto.AuthResponseDTO;
import org.sikawofie.projecttracker.dto.UserRequestDTO;
import org.sikawofie.projecttracker.dto.UserResponseDTO;
import org.sikawofie.projecttracker.entity.Role;
import org.sikawofie.projecttracker.entity.User;
import org.sikawofie.projecttracker.repository.RoleRepo;
import org.sikawofie.projecttracker.repository.UserRepo;
import org.sikawofie.projecttracker.security.JwtService;
import org.sikawofie.projecttracker.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepository;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;


    public UserResponseDTO addUser(UserRequestDTO userRequestDTO) {
        User user = mapUserRequestDTOToUser(userRequestDTO);

        Role defaultRole = roleRepository.findByName(Role.RoleName.ROLE_DEVELOPER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        user.setRoles(Set.of(defaultRole));
        User savedUser = userRepo.save(user);
        return mapUserToUserResponseDTO(savedUser);
    }

    public AuthResponseDTO verify(UserRequestDTO userRequestDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userRequestDTO.getUsername(),
                        userRequestDTO.getPassword()
                )
        );

        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(userRequestDTO.getUsername());
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            return AuthResponseDTO.builder()
                    .message("Authentication successful")
                    .status(200)
                    .token(token)
                    .roles(roles)
                    .build();
        }

        return AuthResponseDTO.builder()
                .message("Authentication failed")
                .status(404)
                .token(null)
                .build();
    }

    private User mapUserRequestDTOToUser(UserRequestDTO userRequestDTO) {
        String encodedPassword = bCryptPasswordEncoder.encode(userRequestDTO.getPassword());
        return User.builder()
                .username(userRequestDTO.getUsername())
                .password(encodedPassword)
                .email(userRequestDTO.getEmail())
                .oauthUser(false)
                .build();
    }

    private UserResponseDTO mapUserToUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}