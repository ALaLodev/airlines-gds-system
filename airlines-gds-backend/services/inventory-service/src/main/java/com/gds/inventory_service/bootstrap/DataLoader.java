package com.gds.inventory_service.bootstrap;

import com.gds.inventory_service.model.Inventory;
import com.gds.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) throws Exception {
        // Verificamos si la BD ya tiene info
        if (inventoryRepository.count() == 0) {
            log.info("⚙️ Base de datos vacía detectada. Iniciando carga de inventario simulado...");

            // Creamos 100 asientos para el vuelo con scheduleId = 1
            inventoryRepository.save(Inventory.builder()
                    .scheduleId(1L)
                    .availableSeats(100)
                    .build());

            // Por si acaso probamos con otros vuelos, metemos dos más
            inventoryRepository.save(Inventory.builder()
                    .scheduleId(2L)
                    .availableSeats(50)
                    .build());

            inventoryRepository.save(Inventory.builder()
                    .scheduleId(3L)
                    .availableSeats(200)
                    .build());

            log.info("✅ Carga inicial completada. Asientos listos para ser reservados.");
        } else {
            log.info("♻️ El inventario ya contiene datos. Omitiendo la carga inicial.");
        }
    }
}

