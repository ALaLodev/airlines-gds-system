package com.gds.airline.auth_service.controller;

import com.gds.airline.auth_service.dto.AuthResponse;
import com.gds.airline.auth_service.dto.LoginRequest;
import com.gds.airline.auth_service.dto.RegisterRequest;
import com.gds.airline.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public  ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/users/{id}/email")
    public ResponseEntity<String> getUserEmailById(@PathVariable Long id) {
        // Necesitarás crear este método en tu AuthService para buscar en BD
        String email = authService.getUserEmailById(id);
        return ResponseEntity.ok(email);
    }
}
