package com.gds.airline.booking_service.dto;

import java.math.BigDecimal;

public record DashboardKpiDTO(
        BigDecimal totalRevenue,      // Ingresos totales de reservas completadas
        long totalBookings,           // Número total de reservas intentadas
        long successfulBookings,      // Número de reservas COMPLETED
        double successRate            // Porcentaje de éxito (ej. 98.2)
) {}
