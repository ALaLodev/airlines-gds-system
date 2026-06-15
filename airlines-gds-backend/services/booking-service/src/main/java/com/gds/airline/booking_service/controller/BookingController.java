package com.gds.airline.booking_service.controller;

import com.gds.airline.booking_service.dto.SeatMapResponse;
import com.gds.airline.booking_service.model.Reservation;
import com.gds.airline.booking_service.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /**
     * POST /api/bookings — Crea una nueva reserva con asiento asignado
     */
    @PostMapping
    public ResponseEntity<Reservation> bookFlight(@RequestBody Reservation request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createReservation(request));
    }

    /**
     * GET /api/bookings/flight/{flightId}/seats — Devuelve los asientos ocupados de un vuelo
     * Usado por el frontend para renderizar el mapa interactivo de asientos.
     */
    @GetMapping("/flight/{flightId}/seats")
    public ResponseEntity<List<SeatMapResponse>> getBookedSeatsByFlight(@PathVariable Long flightId) {
        return ResponseEntity.ok(bookingService.getBookedSeats(flightId));
    }
}
