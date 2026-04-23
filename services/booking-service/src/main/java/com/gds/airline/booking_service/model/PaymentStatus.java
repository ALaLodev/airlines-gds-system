package com.gds.airline.booking_service.model;

public enum PaymentStatus {
    PENDING,    // Al crear la reserva
    COMPLETED,  // Cuando el Payment Service confirme el cobro
    FAILED,     // Si la tarjeta no tiene fondos
    CANCELLED   // Si el usuario cancela
}
