package com.gds.airline.booking_service.consumer;

import com.gds.airline.booking_service.event.PaymentResultEvent;
import com.gds.airline.booking_service.model.PaymentStatus;
import com.gds.airline.booking_service.model.Reservation;
import com.gds.airline.booking_service.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentResultConsumer {

    private final ReservationRepository reservationRepository;

    @KafkaListener(topics = "payment-events", groupId = "booking-group")
    public void consumePaymentResult(PaymentResultEvent event) {
        log.info("🔔 Resultado de pago recibido para el PNR: {} - Estado: {}", event.getPnr(), event.getPaymentStatus());

        Optional<Reservation> reservationOpt = reservationRepository.findByPnr(event.getPnr());

        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();

            // Evaluamos la respuesta de la Saga
            if ("SUCCESS".equals(event.getPaymentStatus())) {
                reservation.setStatus(PaymentStatus.valueOf("COMPLETED"));
                log.info("✅ Reserva {} CONFIRMADA con éxito en la Base de Datos.", event.getPnr());
            } else {
                reservation.setStatus(PaymentStatus.valueOf("CANCELLED"));
                log.info("❌ Reserva {} CANCELADA debido a fallo en el pago (Transacción Compensatoria Ejecutada).", event.getPnr());
            }

            // Guardamos el nuevo estado definitivo
            reservationRepository.save(reservation);

        } else {
            log.warn("⚠️ Alerta de Inconsistencia: No se encontró la reserva con PNR: {}", event.getPnr());
        }
    }
}