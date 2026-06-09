package com.gds.airline.booking_service.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResultEvent {
    private String pnr;
    private String paymentStatus; // "SUCCESS" o "FAILED"
    private Long scheduleId;
}