package com.gds.airline.agency_service.service;

import com.gds.airline.agency_service.dto.AgencyMetricsResponse;
import com.gds.airline.agency_service.dto.AgencyRequest;
import com.gds.airline.agency_service.dto.AgencyResponse;
import com.gds.airline.agency_service.dto.PaginatedResponse;
import com.gds.airline.agency_service.entity.Agency;
import com.gds.airline.agency_service.entity.AgencyStatus;
import com.gds.airline.agency_service.entity.Region;
import com.gds.airline.agency_service.repository.AgencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgencyService {

    private final AgencyRepository agencyRepository;

    public PaginatedResponse<AgencyResponse> getAgencies(int page, int size, String search, String status, String region) {
        Pageable pageable = PageRequest.of(page, size);
        AgencyStatus agencyStatus = status != null && !status.isEmpty() && !status.equals("All Statuses") ? AgencyStatus.valueOf(status.toUpperCase()) : null;
        Region agencyRegion = region != null && !region.isEmpty() && !region.equals("Global Region") ? Region.valueOf(region.toUpperCase()) : null;

        Page<Agency> agencyPage = agencyRepository.searchAgencies(search, agencyStatus, agencyRegion, pageable);

        List<AgencyResponse> responses = agencyPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PaginatedResponse.<AgencyResponse>builder()
                .data(responses)
                .currentPage(agencyPage.getNumber())
                .pageSize(agencyPage.getSize())
                .totalElements(agencyPage.getTotalElements())
                .totalPages(agencyPage.getTotalPages())
                .isLast(agencyPage.isLast())
                .build();
    }

    public AgencyResponse getAgencyById(Long id) {
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agency not found"));
        return mapToResponse(agency);
    }

    public AgencyResponse createAgency(AgencyRequest request) {
        Agency agency = Agency.builder()
                .agencyName(request.getAgencyName())
                .iataCode(request.getIataCode())
                .city(request.getCity())
                .country(request.getCountry())
                .region(request.getRegion())
                .contactName(request.getContactName())
                .contactEmail(request.getContactEmail())
                .status(request.getStatus() != null ? request.getStatus() : AgencyStatus.ACTIVE)
                .bookings30d(0)
                .complianceRate(100.0)
                .build();

        Agency savedAgency = agencyRepository.save(agency);
        return mapToResponse(savedAgency);
    }

    public AgencyResponse updateAgency(Long id, AgencyRequest request) {
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agency not found"));

        agency.setAgencyName(request.getAgencyName());
        agency.setIataCode(request.getIataCode());
        agency.setCity(request.getCity());
        agency.setCountry(request.getCountry());
        agency.setRegion(request.getRegion());
        agency.setContactName(request.getContactName());
        agency.setContactEmail(request.getContactEmail());
        
        if(request.getStatus() != null) {
            agency.setStatus(request.getStatus());
        }

        Agency updatedAgency = agencyRepository.save(agency);
        return mapToResponse(updatedAgency);
    }

    public void deleteAgency(Long id) {
        agencyRepository.deleteById(id);
    }

    public AgencyMetricsResponse getMetrics() {
        long totalAgencies = agencyRepository.count();
        Integer activeBookings = agencyRepository.getTotalActiveBookings();
        Double complianceRate = agencyRepository.getAverageComplianceRate();

        return AgencyMetricsResponse.builder()
                .totalAgencies(totalAgencies)
                .totalAgenciesChange(12.0) // Mocked for UI
                .activeBookings(activeBookings != null ? activeBookings : 0)
                .activeBookingsChange(5.4) // Mocked for UI
                .revenueMtd(3200000.0) // Mocked for UI
                .revenueMtdChange(-2.1) // Mocked for UI
                .complianceRate(complianceRate != null ? Math.round(complianceRate * 10.0) / 10.0 : 100.0)
                .build();
    }

    public List<AgencyResponse> getTopPerformers() {
        return agencyRepository.findTopPerformers(4).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private AgencyResponse mapToResponse(Agency agency) {
        return AgencyResponse.builder()
                .id(agency.getId())
                .agencyName(agency.getAgencyName())
                .iataCode(agency.getIataCode())
                .city(agency.getCity())
                .country(agency.getCountry())
                .region(agency.getRegion())
                .contactName(agency.getContactName())
                .contactEmail(agency.getContactEmail())
                .status(agency.getStatus())
                .bookings30d(agency.getBookings30d())
                .complianceRate(agency.getComplianceRate())
                .createdAt(agency.getCreatedAt())
                .updatedAt(agency.getUpdatedAt())
                .build();
    }
}
