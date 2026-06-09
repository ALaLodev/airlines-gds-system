package com.gds.airline.booking_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "FLIGHT-SERVICE")
public interface FlightClient {

    // Suponiendo que el Flight Service devuelve un DTO con info del vuelo
    @GetMapping("/api/flights/{id}")
    Map<String, Object> getFlightInfo(@PathVariable("id") Long id);
}
