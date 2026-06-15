package com.gds.airline.booking_service.dto;

/**
 * DTO lightweight para el mapa de asientos.
 * Solo expone el número de asiento y la cabina, sin datos personales del pasajero.
 */
public record SeatMapResponse(
        String seatNumber,
        String cabinClass,
        String status  // "PENDING" o "COMPLETED" — útil para diferenciar bloqueos de agente vs. vendidos
) {}
