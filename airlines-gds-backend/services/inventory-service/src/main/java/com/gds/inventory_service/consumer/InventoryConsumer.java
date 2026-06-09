package com.gds.inventory_service.consumer;

import com.gds.inventory_service.event.PaymentResultEvent;
import com.gds.inventory_service.model.Inventory;
import com.gds.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryConsumer {

    private final InventoryRepository inventoryRepository;

    @KafkaListener(topics = "payment-events", groupId = "inventory-group")
    public void updateInventory(PaymentResultEvent event) {

        if ("SUCCESS".equals(event.getPaymentStatus())) {
            log.info("🪑 Reduciendo inventario para el vuelo: {}", event.getScheduleId());

            // Buscamos el vuelo en la base de datos
            Optional<Inventory> inventoryOpt = inventoryRepository.findByScheduleId(event.getScheduleId());

            if (inventoryOpt.isPresent()) {
                Inventory inventory = inventoryOpt.get();

                // Le restamos 1 asiento al total
                inventory.setAvailableSeats(inventory.getAvailableSeats() - 1);
                inventoryRepository.save(inventory);

                // ¡AQUÍ ESTÁ TU LOG PERSONALIZADO!
                log.info("✅ Asiento restado con éxito. Quedan {} asientos disponibles para el vuelo {}.",
                        inventory.getAvailableSeats(), event.getScheduleId());
            } else {
                log.warn("⚠️ No se encontró el inventario para el vuelo: {}", event.getScheduleId());
            }

        } else {
            // Si el pago falla, dejamos un log para saber que lo hemos ignorado
            log.info("⚠️ El pago falló para el PNR {}. No se restan asientos.", event.getPnr());
        }
    }
}

