package com.gds.inventory_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long scheduleId;

    @Column(nullable = false)
    private Integer availableSeats;

    // Flight metadata (denormalized)
    @Column(nullable = false)
    private String flightNumber;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    private String aircraftType;

    private LocalDateTime departureTime;

    // Economy class
    @Column(nullable = false)
    private Integer economyTotal;

    @Builder.Default
    @Column(nullable = false)
    private Integer economyBooked = 0;

    // Premium class
    @Column(nullable = false)
    private Integer premiumTotal;

    @Builder.Default
    @Column(nullable = false)
    private Integer premiumBooked = 0;

    // Business class
    @Column(nullable = false)
    private Integer businessTotal;

    @Builder.Default
    @Column(nullable = false)
    private Integer businessBooked = 0;

    // First class (nullable - not all aircraft have First)
    private Integer firstTotal;

    @Builder.Default
    private Integer firstBooked = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InventoryStatus status;

    @Column(nullable = false)
    private Double baseFare;
}
