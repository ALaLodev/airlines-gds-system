package com.gds.airlines.payment_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCreatedEvent {
    private String pnr;
    private Long userId;
    private Long scheduleId; // ¡El dato clave!
    private BigDecimal totalAmount;
}
