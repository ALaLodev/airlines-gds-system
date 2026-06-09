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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(adminBookingService.getRecentBookings(page, size));
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
