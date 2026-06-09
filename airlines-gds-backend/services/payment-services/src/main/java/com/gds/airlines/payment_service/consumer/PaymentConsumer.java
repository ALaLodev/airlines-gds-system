package com.gds.airlines.payment_service.consumer;

// ¡FÍJATE EN ESTE IMPORT! Ahora apunta al payment_service
import com.gds.airlines.payment_service.event.BookingCreatedEvent;
import com.gds.airlines.payment_service.service.PaymentProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final PaymentProcessorService paymentProcessorService;

    @KafkaListener(topics = "booking-events", groupId = "payment-group")
    public void consumeBookingEvent(BookingCreatedEvent event) {
        // Ahora el log nos chivará si el ID del vuelo llega correctamente
        log.info("💳 ¡Nuevo evento recibido! PNR: {} - Vuelo: {}", event.getPnr(), event.getScheduleId());

        paymentProcessorService.processPayment(event);
    }
}