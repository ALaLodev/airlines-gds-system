package com.gds.airline.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/booking")
    public ResponseEntity<Map<String, Object>> bookingFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "TEMPORALY_UNAVAILABLE");
        response.put("message", "El servicio de reserva está experimentando problemas o una alta demanda. Por favor, inténtelo de nuevo en unos segundos.");

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @RequestMapping("/flights")
    public ResponseEntity<Map<String, Object>> flightsFallback() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SEARCH_UNAVAILABLE");
        response.put("message", "Nuestro buscador de vuelos está en mantenimiento. Mostrando resultados cacheados u operando en modo degradado.");
        response.put("data", new java.util.ArrayList<>()); // Devolvemos una lista vacía para que el frontend no rompa

        return ResponseEntity.ok(response);
    }

}
