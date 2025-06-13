package org.sikawofie.projecttracker.security.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.sikawofie.projecttracker.dto.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@RestControllerAdvice
public class SecurityExceptionHandler extends ResponseEntityExceptionHandler
        implements AuthenticationEntryPoint, AccessDeniedHandler {


    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ApiResponseDTO<?> handleUsernameExists(UsernameAlreadyExistsException ex) {
        return ApiResponseDTO.builder()
                .status(HttpStatus.CONFLICT.value())
                .message(ex.getMessage())
                .data(null)
                .build();
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ApiResponseDTO<?> handleInvalidToken(InvalidTokenException ex) {
        return ApiResponseDTO.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage())
                .data(null)
                .build();
    }

    @ExceptionHandler(OAuth2AuthenticationProcessingException.class)
    public ApiResponseDTO<?> handleOAuth2Error(OAuth2AuthenticationProcessingException ex) {
        return ApiResponseDTO.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message(ex.getMessage())
                .data(null)
                .build();
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(
                ApiResponseDTO.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message("Authentication failed: " + authException.getMessage())
                        .data(null)
                        .build().toString()
        );
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(
                ApiResponseDTO.builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .message("Access denied: " + accessDeniedException.getMessage())
                        .data(null)
                        .build().toString()
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ApiResponseDTO<?> handleBadCredentials(BadCredentialsException ex) {
        return ApiResponseDTO.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("Invalid username or password")
                .data(null)
                .build();
    }


    @ExceptionHandler(Exception.class)
    public ApiResponseDTO<?> handleAllExceptions(Exception ex) {
        return ApiResponseDTO.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred: " + ex.getMessage())
                .data(null)
                .build();
    }
}