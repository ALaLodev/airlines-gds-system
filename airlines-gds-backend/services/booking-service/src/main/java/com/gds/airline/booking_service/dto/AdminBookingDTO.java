package com.gds.airline.booking_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AdminBookingDTO(
        String pnr,
        String userEmail,
        String origin,
        String destination,
        BigDecimal totalAmount,
        String status,
        LocalDateTime bookingDate,
        String seatNumber,
        String cabinClass
) {}

