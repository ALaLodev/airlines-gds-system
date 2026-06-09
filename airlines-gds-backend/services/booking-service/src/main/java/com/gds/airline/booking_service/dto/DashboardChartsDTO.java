package com.gds.airline.booking_service.dto;

import java.util.Map;

public record DashboardChartsDTO(
        Map<String, Long> statusDistribution, // Para el gráfico Doughnut
        Map<String, Long> salesPerDay         // Para el gráfico de barras (Volumen)
) {}
