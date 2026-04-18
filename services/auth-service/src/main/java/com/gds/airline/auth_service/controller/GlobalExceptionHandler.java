package com.gds.airline.auth_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException ex){

        // Creamos un JSON personalizado para devolver
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Acceso denegado");
        errorResponse.put("message", "El email o la contraseña son incorrectos.");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    // Atrapamos errores generales
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeErrors(RuntimeException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Petición inválida");
        errorResponse.put("message", ex.getMessage());

        // Devolver un HTTP 400 (Bad Request)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
