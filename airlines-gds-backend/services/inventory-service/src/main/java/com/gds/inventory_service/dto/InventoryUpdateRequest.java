package com.gds.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryUpdateRequest {
    private Integer economyBooked;
    private Integer premiumBooked;
    private Integer businessBooked;
    private Integer firstBooked;
    private String status;
}
