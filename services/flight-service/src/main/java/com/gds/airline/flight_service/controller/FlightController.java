package com.gds.airline.flight_service.controller;


import com.gds.airline.flight_service.model.Flight;
import com.gds.airline.flight_service.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @PostMapping
    public ResponseEntity<Flight> createFlight(@RequestBody Flight flight) {
        return ResponseEntity.status(HttpStatus.CREATED).body(flightService.saveFlight(flight));
    }

    @GetMapping
    public ResponseEntity<List<Flight>> getAllFlight() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Flight>> searchFlights(@RequestParam String origin, @RequestParam String destination) {
        return ResponseEntity.ok(flightService.searchFlight(origin, destination));
    }
}
