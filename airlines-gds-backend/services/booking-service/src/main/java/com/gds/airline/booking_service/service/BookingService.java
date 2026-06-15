package com.gds.airline.booking_service.service;

import com.gds.airline.booking_service.dto.SeatMapResponse;
import com.gds.airline.booking_service.event.BookingCreatedEvent;
import com.gds.airline.booking_service.model.PaymentStatus;
import com.gds.airline.booking_service.model.Reservation;
import com.gds.airline.booking_service.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j // Anotación de Lombok para poder usar logs
public class BookingService {

    private final ReservationRepository reservationRepository;
    private final KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate;

    public Reservation createReservation(Reservation request) {

        String pnr = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        request.setPnr(pnr);
        request.setStatus(PaymentStatus.PENDING);

        Reservation savedReservation = reservationRepository.save(request);

        // Construimos el mensaje para Kafka
        BookingCreatedEvent event = BookingCreatedEvent.builder()
                .pnr(savedReservation.getPnr())
                .userId(savedReservation.getUserId())
                .scheduleId(savedReservation.getScheduleId())
                .totalAmount(savedReservation.getTotalAmount())
                .build();

        // Publicamos el evento en el tópico "booking-events"
        log.info("Publicando evento BookingCreatedEvent para el PNR: {}, Asiento: {}, Cabina: {}",
                pnr, savedReservation.getSeatNumber(), savedReservation.getCabinClass());
        kafkaTemplate.send("booking-events", event);

        return savedReservation;
    }

    /**
     * Devuelve la lista de asientos ocupados (PENDING o COMPLETED) para un vuelo dado.
     * Solo expone el número de asiento y la cabina, sin datos personales del pasajero.
     */
    public List<SeatMapResponse> getBookedSeats(Long flightId) {
        return reservationRepository.findActiveReservationsByScheduleId(flightId)
                .stream()
                .filter(r -> r.getSeatNumber() != null) // ignorar reservas legacy sin asiento
                .map(r -> new SeatMapResponse(
                        r.getSeatNumber(),
                        r.getCabinClass(),
                        r.getStatus().name()
                ))
                .collect(Collectors.toList());
    }
}