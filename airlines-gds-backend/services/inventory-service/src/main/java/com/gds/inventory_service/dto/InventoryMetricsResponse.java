package com.gds.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMetricsResponse {
    private long activeFlights;
    private double activeFlightsChange;
    private double avgLoadFactor;
    private double unsoldInventoryValue;
}
