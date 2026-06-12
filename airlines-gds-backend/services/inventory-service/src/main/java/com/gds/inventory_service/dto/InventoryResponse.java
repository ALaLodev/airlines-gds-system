package com.gds.inventory_service.dto;

import com.gds.inventory_service.model.InventoryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private Long id;
    private Long scheduleId;
    private String flightNumber;
    private String origin;
    private String destination;
    private String aircraftType;
    private LocalDateTime departureTime;
    private Integer economyTotal;
    private Integer economyBooked;
    private Integer premiumTotal;
    private Integer premiumBooked;
    private Integer businessTotal;
    private Integer businessBooked;
    private Integer firstTotal;
    private Integer firstBooked;
    private InventoryStatus status;
    private Double baseFare;
    private Integer availableSeats;
}
