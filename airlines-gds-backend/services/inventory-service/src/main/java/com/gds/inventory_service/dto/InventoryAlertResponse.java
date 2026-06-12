package com.gds.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAlertResponse {
    private String type;    // CRITICAL, WARNING, INFO
    private String title;
    private String message;
    private String icon;
}
