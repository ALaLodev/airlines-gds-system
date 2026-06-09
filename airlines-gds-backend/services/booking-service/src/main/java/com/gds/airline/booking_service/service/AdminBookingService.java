package com.gds.airline.booking_service.service;

import com.gds.airline.booking_service.client.AuthClient;
import com.gds.airline.booking_service.client.FlightClient;
import com.gds.airline.booking_service.dto.AdminBookingDTO;
import com.gds.airline.booking_service.dto.DashboardChartsDTO;
import com.gds.airline.booking_service.dto.DashboardKpiDTO;
import com.gds.airline.booking_service.dto.PaginatedResponse;
import com.gds.airline.booking_service.model.PaymentStatus;
import com.gds.airline.booking_service.model.Reservation;
import com.gds.airline.booking_service.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminBookingService {

    private final ReservationRepository reservationRepository;
    private final AuthClient authClient;
    private final FlightClient flightClient;

    public PaginatedResponse<AdminBookingDTO> getRecentBookings(int page, int size, String pnr, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        PaymentStatus paymentStatus = null;
        if (status != null && !status.isBlank()) {
            paymentStatus = PaymentStatus.valueOf(status.toUpperCase());
        }
        String pnrFilter = (pnr != null && !pnr.isBlank()) ? pnr.trim().toUpperCase() : null;

        Page<Reservation> reservationPage = (pnrFilter == null && paymentStatus == null)
                ? reservationRepository.findAll(pageable)
                : reservationRepository.findByFilters(pnrFilter, paymentStatus, pageable);

        // 2. Transformamos cada Reservation en un AdminBookingDTO cruzando los datos
        List<AdminBookingDTO> dtoList = reservationPage.getContent().stream().map(reservation -> {

            String email = "Email no disponible";
            String origin = "N/A";
            String destination = "N/A";

            try {
                email = authClient.getUserEmailById(reservation.getUserId());
            } catch (Exception e) {
                log.warn("No se pudo obtener el email para el usuario ID: {}", reservation.getUserId());
            }

            // Llamada al Flight Service
            try {
                Map<String, Object> flightInfo = flightClient.getFlightInfo(reservation.getScheduleId());
                origin = (String) flightInfo.get("origin");
                destination = (String) flightInfo.get("destination");
            } catch (Exception e) {
                log.warn("No se pudo obtener el vuelo para el ID: {}", reservation.getScheduleId());
            }

            // Ensamblamos el DTO final
            return new AdminBookingDTO(
                    reservation.getPnr(),
                    email,
                    origin,
                    destination,
                    reservation.getTotalAmount(),
                    reservation.getStatus().name(),
                    reservation.getCreatedAt()
            );
        }).collect(Collectors.toList());

        // Devolvemos el JSON paginado
        return new PaginatedResponse<>(
                dtoList,
                reservationPage.getNumber(),
                reservationPage.getSize(),
                reservationPage.getTotalElements(),
                reservationPage.getTotalPages(),
                reservationPage.isLast()
        );
    }

    public DashboardKpiDTO getDashboardKpis() {
        // 1. Contamos todas las reservas absolutas (El metod count() nos lo regala JpaRepository)
        long totalBookings = reservationRepository.count();

        // 2. Contamos solo las reservas exitosas
        long successfulBookings = reservationRepository.countByStatus(PaymentStatus.COMPLETED);

        // 3. Calculamos los ingresos totales delegando la suma a MySQL
        BigDecimal totalRevenue = reservationRepository.sumTotalAmountByStatus(PaymentStatus.COMPLETED);

        // 4. Calculamos la tasa de éxito (evitando dividir por cero si la BD está vacía)
        double successRate = 0.0;
        if (totalBookings > 0) {
            successRate = ((double) successfulBookings / totalBookings) * 100;
            // Redondeamos a 2 decimales para que el JSON quede limpio (ej. 98.24)
            successRate = Math.round(successRate * 100.0) / 100.0;
        }

        // 5. Ensamblamos y devolvemos el Record
        return new DashboardKpiDTO(
                totalRevenue,
                totalBookings,
                successfulBookings,
                successRate
        );
    }

    public DashboardChartsDTO getDashboardCharts() {
        // 1. Procesamos la distribución de estados
        Map<String, Long> statusDistribution = new LinkedHashMap<>();

        // TRUCO FRONTEND: Inicializamos todos los estados posibles a 0
        // (Asegúrate de importar tu Enum PaymentStatus)
        for (PaymentStatus status : PaymentStatus.values()) {
            statusDistribution.put(status.name(), 0L);
        }

        // Ahora pedimos los datos reales a la base de datos
        List<Object[]> statusResults = reservationRepository.countReservationsByStatus();

        for (Object[] row : statusResults) {
            String status = row[0].toString();
            Long count = ((Number) row[1]).longValue();
            // Sobrescribimos el 0 con la cantidad real
            statusDistribution.put(status, count);
        }

        // 2. Procesamos el volumen de reservas por día
        List<Object[]> dateResults = reservationRepository.countReservationsByDate();
        Map<String, Long> salesPerDay = new LinkedHashMap<>();

        for (Object[] row : dateResults) {
            String date = row[0].toString();
            Long count = ((Number) row[1]).longValue();
            salesPerDay.put(date, count);
        }

        // 3. Devolvemos el JSON empaquetado
        return new DashboardChartsDTO(statusDistribution, salesPerDay);
    }
}
