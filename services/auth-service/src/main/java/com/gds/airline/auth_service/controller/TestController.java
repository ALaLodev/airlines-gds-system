package com.gds.airline.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test") // Fíjate que esta ruta ya NO es /api/auth
public class TestController {

    @GetMapping("/secret")
    public ResponseEntity<String> secretEndpoint() {
        return ResponseEntity.ok("¡Bienvenido a la zona VIP del GDS! Tu token funciona a la perfección.");
    }
}