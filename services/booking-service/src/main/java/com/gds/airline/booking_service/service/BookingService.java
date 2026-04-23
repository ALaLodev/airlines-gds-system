package com.gds.airline.booking_service.service;

import com.gds.airline.booking_service.event.BookingCreatedEvent;
import com.gds.airline.booking_service.model.PaymentStatus;
import com.gds.airline.booking_service.model.Reservation;
import com.gds.airline.booking_service.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j // Anotación de Lombok para poder usar logs
public class BookingService {

    private final ReservationRepository reservationRepository;

    // Inyectamos la herramienta de Spring para hablar con Kafka
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
        log.info("Publicando evento BookingCreatedEvent para el PNR: {}", pnr);
        kafkaTemplate.send("booking-events", event);

        return savedReservation;
    }
}