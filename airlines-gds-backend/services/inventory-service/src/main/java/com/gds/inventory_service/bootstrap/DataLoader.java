package com.gds.inventory_service.bootstrap;

import com.gds.inventory_service.model.Inventory;
import com.gds.inventory_service.model.InventoryStatus;
import com.gds.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final InventoryRepository inventoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (inventoryRepository.count() == 0) {
            log.info("⚙️ Base de datos vacía detectada. Iniciando carga de inventario simulado...");

            // Flight 1: LHR → JFK (high demand on Premium)
            inventoryRepository.save(Inventory.builder()
                    .scheduleId(1L)
                    .flightNumber("SL-402")
                    .origin("LHR")
                    .destination("JFK")
                    .aircraftType("A350-1000")
                    .departureTime(LocalDateTime.now().plusHours(6))
                    .economyTotal(220)
                    .economyBooked(142)
                    .premiumTotal(40)
                    .premiumBooked(32)
                    .businessTotal(36)
                    .businessBooked(12)
                    .firstTotal(8)
                    .firstBooked(2)
                    .status(InventoryStatus.ON_SALE)
                    .baseFare(450.0)
                    .availableSeats(116)
                    .build());

            // Flight 2: DXB → SYD (almost full)
            inventoryRepository.save(Inventory.builder()
                    .scheduleId(2L)
                    .flightNumber("SL-118")
                    .origin("DXB")
                    .destination("SYD")
                    .aircraftType("B787-9")
                    .departureTime(LocalDateTime.now().plusHours(3))
                    .economyTotal(210)
                    .economyBooked(190)
                    .premiumTotal(40)
                    .premiumBooked(38)
                    .businessTotal(32)
                    .businessBooked(24)
                    .firstTotal(6)
                    .firstBooked(6)
                    .status(InventoryStatus.LIMITED)
                    .baseFare(890.0)
                    .availableSeats(30)
                    .build());

            // Flight 3: HND → CDG (early booking, low fill)
            inventoryRepository.save(Inventory.builder()
                    .scheduleId(3L)
                    .flightNumber("SL-952")
                    .origin("HND")
                    .destination("CDG")
                    .aircraftType("A350-900")
                    .departureTime(LocalDateTime.now().plusDays(5))
                    .economyTotal(180)
                    .economyBooked(45)
                    .premiumTotal(30)
                    .premiumBooked(4)
                    .businessTotal(24)
                    .businessBooked(2)
                    .firstTotal(null)
                    .firstBooked(null)
                    .status(InventoryStatus.EARLY_BOOKING)
                    .baseFare(720.0)
                    .availableSeats(183)
                    .build());

            // Flight 4: MAD → MIA (moderate)
            inventoryRepository.save(Inventory.builder()
                    .scheduleId(4L)
                    .flightNumber("SL-615")
                    .origin("MAD")
                    .destination("MIA")
                    .aircraftType("B777-300ER")
                    .departureTime(LocalDateTime.now().plusHours(12))
                    .economyTotal(280)
                    .economyBooked(168)
                    .premiumTotal(48)
                    .premiumBooked(22)
                    .businessTotal(42)
                    .businessBooked(18)
                    .firstTotal(8)
                    .firstBooked(3)
                    .status(InventoryStatus.ON_SALE)
                    .baseFare(520.0)
                    .availableSeats(167)
                    .build());

            // Flight 5: SIN → LAX (high fill)
            inventoryRepository.save(Inventory.builder()
                    .scheduleId(5L)
                    .flightNumber("SL-788")
                    .origin("SIN")
                    .destination("LAX")
                    .aircraftType("A380-800")
                    .departureTime(LocalDateTime.now().plusHours(1))
                    .economyTotal(340)
                    .economyBooked(312)
                    .premiumTotal(60)
                    .premiumBooked(55)
                    .businessTotal(56)
                    .businessBooked(48)
                    .firstTotal(14)
                    .firstBooked(12)
                    .status(InventoryStatus.LIMITED)
                    .baseFare(680.0)
                    .availableSeats(43)
                    .build());

            // Flight 6: FRA → NRT (balanced)
            inventoryRepository.save(Inventory.builder()
                    .scheduleId(6L)
                    .flightNumber("SL-330")
                    .origin("FRA")
                    .destination("NRT")
                    .aircraftType("B787-10")
                    .departureTime(LocalDateTime.now().plusHours(18))
                    .economyTotal(240)
                    .economyBooked(120)
                    .premiumTotal(36)
                    .premiumBooked(14)
                    .businessTotal(28)
                    .businessBooked(10)
                    .firstTotal(null)
                    .firstBooked(null)
                    .status(InventoryStatus.ON_SALE)
                    .baseFare(610.0)
                    .availableSeats(160)
                    .build());

            log.info("✅ Carga inicial completada. 6 registros de inventario creados.");
        } else {
            log.info("♻️ El inventario ya contiene datos. Omitiendo la carga inicial.");
        }
    }
}
