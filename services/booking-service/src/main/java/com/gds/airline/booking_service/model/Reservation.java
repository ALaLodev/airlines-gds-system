package com.gds.airline.booking_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // PNR: Código alfanumérico único de 6 caracteres que se da al pasajero (ej: "X7B9Q2")
    @Column(nullable = false, unique = true, length = 6)
    private String pnr;

    // Guardamos el ID del usuario, no la entidad entera
    @Column(nullable = false)
    private Long userId;

    // Guardamos el ID del vuelo/horario
    @Column(nullable = false)
    private Long scheduleId;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
}
