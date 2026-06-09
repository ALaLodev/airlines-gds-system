package com.gds.inventory_service.event;

import lombok.Data;

@Data
public class PaymentResultEvent {
    private String pnr;
    private String paymentStatus;
    private Long scheduleId;
}
