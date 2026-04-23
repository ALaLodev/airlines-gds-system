package com.gds.airline.booking_service.repository;

import com.gds.airline.booking_service.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    Optional<Reservation> findByPnr(String pnr);
}
