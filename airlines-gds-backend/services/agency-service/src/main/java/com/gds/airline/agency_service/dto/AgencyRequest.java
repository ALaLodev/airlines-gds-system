package com.gds.airline.agency_service.dto;

import com.gds.airline.agency_service.entity.AgencyStatus;
import com.gds.airline.agency_service.entity.Region;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgencyRequest {
    private String agencyName;
    private String iataCode;
    private String city;
    private String country;
    private Region region;
    private String contactName;
    private String contactEmail;
    private AgencyStatus status;
}
