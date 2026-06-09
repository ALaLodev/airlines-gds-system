package com.gds.airline.flight_service.repository;

import com.gds.airline.flight_service.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    // Spring Data crea la consulta SQL automáticamente solo con leer el nombre del método
    List<Flight> findByOriginAndDestination(String origin, String destination);
}
