package com.gds.airline.agency_service.dto;

import com.gds.airline.agency_service.entity.AgencyStatus;
import com.gds.airline.agency_service.entity.Region;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgencyResponse {
    private Long id;
    private String agencyName;
    private String iataCode;
    private String city;
    private String country;
    private Region region;
    private String contactName;
    private String contactEmail;
    private AgencyStatus status;
    private Integer bookings30d;
    private Double complianceRate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
