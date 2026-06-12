package com.gds.inventory_service.service;

import com.gds.inventory_service.dto.*;
import com.gds.inventory_service.model.Inventory;
import com.gds.inventory_service.model.InventoryStatus;
import com.gds.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public PaginatedResponse<InventoryResponse> getInventories(int page, int size, String search, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("departureTime").ascending());
        Page<Inventory> inventoryPage;

        boolean hasSearch = search != null && !search.trim().isEmpty();
        boolean hasStatus = status != null && !status.trim().isEmpty();

        if (hasSearch && hasStatus) {
            InventoryStatus inventoryStatus = InventoryStatus.valueOf(status.toUpperCase());
            inventoryPage = inventoryRepository.searchByKeywordAndStatus(search.trim(), inventoryStatus, pageable);
        } else if (hasSearch) {
            inventoryPage = inventoryRepository.searchByKeyword(search.trim(), pageable);
        } else if (hasStatus) {
            InventoryStatus inventoryStatus = InventoryStatus.valueOf(status.toUpperCase());
            inventoryPage = inventoryRepository.findByStatus(inventoryStatus, pageable);
        } else {
            inventoryPage = inventoryRepository.findAll(pageable);
        }

        List<InventoryResponse> data = inventoryPage.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return PaginatedResponse.<InventoryResponse>builder()
                .data(data)
                .currentPage(inventoryPage.getNumber())
                .pageSize(inventoryPage.getSize())
                .totalElements(inventoryPage.getTotalElements())
                .totalPages(inventoryPage.getTotalPages())
                .isLast(inventoryPage.isLast())
                .build();
    }

    public InventoryResponse getInventoryById(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));
        return toResponse(inventory);
    }

    public InventoryMetricsResponse getMetrics() {
        List<Inventory> allInventories = inventoryRepository.findAll();
        long activeFlights = allInventories.stream()
                .filter(i -> i.getStatus() != InventoryStatus.CLOSED)
                .count();

        double avgLoadFactor = 0;
        double unsoldValue = 0;
        if (!allInventories.isEmpty()) {
            double totalLoad = 0;
            int count = 0;
            for (Inventory inv : allInventories) {
                int totalSeats = inv.getEconomyTotal() + inv.getPremiumTotal() + inv.getBusinessTotal()
                        + (inv.getFirstTotal() != null ? inv.getFirstTotal() : 0);
                int bookedSeats = inv.getEconomyBooked() + inv.getPremiumBooked() + inv.getBusinessBooked()
                        + (inv.getFirstBooked() != null ? inv.getFirstBooked() : 0);
                if (totalSeats > 0) {
                    totalLoad += ((double) bookedSeats / totalSeats) * 100;
                    count++;
                }
                int unsold = totalSeats - bookedSeats;
                unsoldValue += unsold * inv.getBaseFare();
            }
            avgLoadFactor = count > 0 ? Math.round(totalLoad / count * 10.0) / 10.0 : 0;
        }

        return InventoryMetricsResponse.builder()
                .activeFlights(activeFlights)
                .activeFlightsChange(12.0)
                .avgLoadFactor(avgLoadFactor)
                .unsoldInventoryValue(Math.round(unsoldValue * 100.0) / 100.0)
                .build();
    }

    public List<InventoryAlertResponse> getAlerts() {
        List<InventoryAlertResponse> alerts = new ArrayList<>();
        List<Inventory> allInventories = inventoryRepository.findAll();

        for (Inventory inv : allInventories) {
            // Check for overbooking (booked > total)
            if (inv.getPremiumBooked() > inv.getPremiumTotal() ||
                inv.getEconomyBooked() > inv.getEconomyTotal() ||
                inv.getBusinessBooked() > inv.getBusinessTotal()) {
                alerts.add(InventoryAlertResponse.builder()
                        .type("CRITICAL")
                        .title("Over-allocated: " + inv.getFlightNumber())
                        .message("A cabin class is overbooked. Immediate action required.")
                        .icon("priority_high")
                        .build());
            }

            // Check for critical capacity (>90% booked on any class)
            int totalSeats = inv.getEconomyTotal() + inv.getPremiumTotal() + inv.getBusinessTotal();
            int bookedSeats = inv.getEconomyBooked() + inv.getPremiumBooked() + inv.getBusinessBooked();
            if (totalSeats > 0 && ((double) bookedSeats / totalSeats) > 0.90) {
                alerts.add(InventoryAlertResponse.builder()
                        .type("WARNING")
                        .title("High demand: " + inv.getOrigin() + " → " + inv.getDestination())
                        .message("Load factor above 90%. Consider price surge recommendation.")
                        .icon("trending_up")
                        .build());
            }
        }

        // Add a general system info alert
        if (alerts.isEmpty()) {
            alerts.add(InventoryAlertResponse.builder()
                    .type("INFO")
                    .title("System Operational")
                    .message("All inventory levels are within normal parameters.")
                    .icon("notifications_active")
                    .build());
        }

        return alerts;
    }

    public InventoryResponse updateInventory(Long id, InventoryUpdateRequest request) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found with id: " + id));

        if (request.getEconomyBooked() != null) inventory.setEconomyBooked(request.getEconomyBooked());
        if (request.getPremiumBooked() != null) inventory.setPremiumBooked(request.getPremiumBooked());
        if (request.getBusinessBooked() != null) inventory.setBusinessBooked(request.getBusinessBooked());
        if (request.getFirstBooked() != null) inventory.setFirstBooked(request.getFirstBooked());
        if (request.getStatus() != null) {
            inventory.setStatus(InventoryStatus.valueOf(request.getStatus().toUpperCase()));
        }

        // Recalculate availableSeats
        int totalSeats = inventory.getEconomyTotal() + inventory.getPremiumTotal() + inventory.getBusinessTotal()
                + (inventory.getFirstTotal() != null ? inventory.getFirstTotal() : 0);
        int bookedSeats = inventory.getEconomyBooked() + inventory.getPremiumBooked() + inventory.getBusinessBooked()
                + (inventory.getFirstBooked() != null ? inventory.getFirstBooked() : 0);
        inventory.setAvailableSeats(totalSeats - bookedSeats);

        Inventory saved = inventoryRepository.save(inventory);
        log.info("✅ Inventory updated for flight {} (id={})", saved.getFlightNumber(), saved.getId());
        return toResponse(saved);
    }

    private InventoryResponse toResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .scheduleId(inventory.getScheduleId())
                .flightNumber(inventory.getFlightNumber())
                .origin(inventory.getOrigin())
                .destination(inventory.getDestination())
                .aircraftType(inventory.getAircraftType())
                .departureTime(inventory.getDepartureTime())
                .economyTotal(inventory.getEconomyTotal())
                .economyBooked(inventory.getEconomyBooked())
                .premiumTotal(inventory.getPremiumTotal())
                .premiumBooked(inventory.getPremiumBooked())
                .businessTotal(inventory.getBusinessTotal())
                .businessBooked(inventory.getBusinessBooked())
                .firstTotal(inventory.getFirstTotal())
                .firstBooked(inventory.getFirstBooked())
                .status(inventory.getStatus())
                .baseFare(inventory.getBaseFare())
                .availableSeats(inventory.getAvailableSeats())
                .build();
    }
}
