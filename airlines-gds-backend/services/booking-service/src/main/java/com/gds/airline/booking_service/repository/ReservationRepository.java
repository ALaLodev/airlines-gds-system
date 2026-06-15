package com.gds.airline.booking_service.repository;

import com.gds.airline.booking_service.model.PaymentStatus;
import com.gds.airline.booking_service.model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    Optional<Reservation> findByPnr(String pnr);

    // 1. Contar reservas filtrando por estado (Spring hace la query automática por el nombre)
    long countByStatus(PaymentStatus status);

    // 2. Sumar el total de dinero solo de las reservas con un estado concreto
    // Usamos COALESCE para que, si no hay ventas, devuelva 0 en lugar de un error NULL
    @Query("SELECT COALESCE(SUM(r.totalAmount), 0) FROM Reservation r WHERE r.status = :status")
    BigDecimal sumTotalAmountByStatus(PaymentStatus status);

    @Query("SELECT r FROM Reservation r WHERE " +
           "(:pnr IS NULL OR :pnr = '' OR r.pnr LIKE CONCAT('%', :pnr, '%')) AND " +
           "(:status IS NULL OR r.status = :status)")
    Page<Reservation> findByFilters(@org.springframework.data.repository.query.Param("pnr") String pnr, @org.springframework.data.repository.query.Param("status") PaymentStatus status, Pageable pageable);

    // 1. Agrupación para el Doughnut (JPQL)
    @Query("SELECT r.status, COUNT(r) FROM Reservation r GROUP BY r.status")
    List<Object[]> countReservationsByStatus();

    // 2. Agrupación para el gráfico de barras (SQL Nativo de MySQL)
    // Agrupa por fecha de creación y saca los últimos 7 días con actividad
    @Query(value = "SELECT DATE(created_at) as date, COUNT(*) as count FROM reservations GROUP BY DATE(created_at) ORDER BY date DESC LIMIT 7", nativeQuery = true)
    List<Object[]> countReservationsByDate();

    // 3. Obtener todas las reservas para un vuelo concreto (para el mapa de asientos)
    // Solo devuelve reservas activas (PENDING o COMPLETED), excluyendo CANCELLED y FAILED
    @Query("SELECT r FROM Reservation r WHERE r.scheduleId = :scheduleId AND r.status IN (com.gds.airline.booking_service.model.PaymentStatus.PENDING, com.gds.airline.booking_service.model.PaymentStatus.COMPLETED)")
    List<Reservation> findActiveReservationsByScheduleId(@org.springframework.data.repository.query.Param("scheduleId") Long scheduleId);
}

