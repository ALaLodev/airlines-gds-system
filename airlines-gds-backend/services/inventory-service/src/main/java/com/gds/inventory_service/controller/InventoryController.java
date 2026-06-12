package com.gds.inventory_service.controller;

import com.gds.inventory_service.dto.*;
import com.gds.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<PaginatedResponse<InventoryResponse>> getInventories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(inventoryService.getInventories(page, size, search, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> getInventoryById(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    @GetMapping("/metrics")
    public ResponseEntity<InventoryMetricsResponse> getMetrics() {
        return ResponseEntity.ok(inventoryService.getMetrics());
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<InventoryAlertResponse>> getAlerts() {
        return ResponseEntity.ok(inventoryService.getAlerts());
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponse> updateInventory(
            @PathVariable Long id,
            @RequestBody InventoryUpdateRequest request) {
        return ResponseEntity.ok(inventoryService.updateInventory(id, request));
    }
}
