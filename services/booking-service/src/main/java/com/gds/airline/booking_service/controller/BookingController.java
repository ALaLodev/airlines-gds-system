package com.gds.airline.booking_service.controller;

import com.gds.airline.booking_service.model.Reservation;
import com.gds.airline.booking_service.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private  final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Reservation> bookFlight(@RequestBody Reservation request){
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createReservation(request));
    }
}
