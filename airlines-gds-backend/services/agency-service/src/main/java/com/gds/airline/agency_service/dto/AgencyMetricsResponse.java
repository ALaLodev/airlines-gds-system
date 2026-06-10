package com.gds.airline.agency_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgencyMetricsResponse {
    private long totalAgencies;
    private double totalAgenciesChange;
    private int activeBookings;
    private double activeBookingsChange;
    private double revenueMtd;
    private double revenueMtdChange;
    private double complianceRate;
}
