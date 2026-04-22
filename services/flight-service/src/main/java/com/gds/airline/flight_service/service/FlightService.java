package com.gds.airline.flight_service.service;

import com.gds.airline.flight_service.model.Flight;
import com.gds.airline.flight_service.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;

    public Flight saveFlight(Flight flight) {
        return flightRepository.save(flight);
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public List<Flight> searchFlight(String origin, String destination) {
        return flightRepository.findByOriginAndDestination(origin, destination);
    }

}
