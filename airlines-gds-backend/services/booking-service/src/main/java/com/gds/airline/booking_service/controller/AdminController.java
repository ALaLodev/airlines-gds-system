package com.gds.airline.booking_service.controller;

import com.gds.airline.booking_service.dto.AdminBookingDTO;
import com.gds.airline.booking_service.dto.DashboardChartsDTO;
import com.gds.airline.booking_service.dto.DashboardKpiDTO;
import com.gds.airline.booking_service.dto.PaginatedResponse;
import com.gds.airline.booking_service.service.AdminBookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings/admin") // Ruta exclusiva para el panel de administración
@RequiredArgsConstructor
public class AdminController {

    private final AdminBookingService adminBookingService;

    @GetMapping("/dashboard/recent")
    public ResponseEntity<PaginatedResponse<AdminBookingDTO>> getRecentBookings(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "pnr", required = false) String pnr,
            @RequestParam(value = "status", required = false) String status) {

        return ResponseEntity.ok(adminBookingService.getRecentBookings(page, size, pnr, status));
    }

    @GetMapping("/dashboard/kpis")
    public ResponseEntity<DashboardKpiDTO> getDashboardKpi() {
        return ResponseEntity.ok(adminBookingService.getDashboardKpis());
    }

    @GetMapping("/dashboard/charts")
    public ResponseEntity<DashboardChartsDTO> getDashboardCharts() {
        return ResponseEntity.ok(adminBookingService.getDashboardCharts());
    }
}
