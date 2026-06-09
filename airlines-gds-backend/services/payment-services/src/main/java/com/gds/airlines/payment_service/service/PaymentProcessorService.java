package com.gds.airlines.payment_service.service;

import com.gds.airlines.payment_service.event.BookingCreatedEvent;
import com.gds.airlines.payment_service.model.Payment;
import com.gds.airlines.payment_service.event.PaymentResultEvent;
import com.gds.airlines.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentProcessorService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void processPayment(BookingCreatedEvent event){
        log.info("🏦 Conectando con pasarela de pagos para el PNR: {}", event.getPnr());

        // Simulador de pasarela: Falla si el userId es 99, acierta para los demás
        boolean isSuccess = event.getUserId() != 99L;
        String status = isSuccess ? "SUCCESS" : "FAILED";

        // Generamos un ID de transacción falso simulando la respuesta de Stripe
        String stripeTxId = "txn_" + UUID.randomUUID().toString().replace("-", "").substring(0, 15);

        // Construimos el recibo
        Payment payment = Payment.builder()
                .pnr(event.getPnr())
                .amount(event.getTotalAmount())
                .paymentStatus(status)
                .transactionId(stripeTxId)
                .createdAt(LocalDateTime.now())
                .build();

        // Persistimos en MySQL
        paymentRepository.save(payment);

        if (isSuccess) {
            log.info("✅ Pago APROBADO. Transacción: {}. Avisando al GDS...", stripeTxId);
        } else {
            log.error("❌ Pago RECHAZADO (Fondos insuficientes). Transacción: {}. Avisando al GDS...", stripeTxId);
        }

        // Creamos el evento de respuesta
        PaymentResultEvent resultEvent = PaymentResultEvent.builder()
                .pnr(event.getPnr())
                .paymentStatus(status)
                .scheduleId(event.getScheduleId())
                .build();

        // Lo lanzamos al nuevo tópico 'payment-events'
        kafkaTemplate.send("payment-events", resultEvent);
        log.info("📢 Evento PaymentResultEvent publicado para el PNR: {}", event.getPnr());
    }
}
