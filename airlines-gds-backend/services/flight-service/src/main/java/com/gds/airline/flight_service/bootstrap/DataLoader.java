package com.gds.airline.flight_service.bootstrap;

import com.gds.airline.flight_service.model.Flight;
import com.gds.airline.flight_service.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final FlightRepository flightRepository;

    @Override
    public void run(String... args) throws Exception {
        if (flightRepository.count() == 0) {
            log.info("⚙️ flight-service: Base de datos vacía detectada. Iniciando carga de vuelos iniciales...");

            // Rutas de inventory-service
            // Flight 1: LHR → JFK
            flightRepository.save(Flight.builder()
                    .flightNumber("SL-402")
                    .origin("LHR")
                    .destination("JFK")
                    .departureTime(LocalDateTime.now().plusHours(6))
                    .arrivalTime(LocalDateTime.now().plusHours(14))
                    .price(new BigDecimal("450.00"))
                    .availableSeats(116)
                    .build());

            // Flight 2: DXB → SYD
            flightRepository.save(Flight.builder()
                    .flightNumber("SL-118")
                    .origin("DXB")
                    .destination("SYD")
                    .departureTime(LocalDateTime.now().plusHours(3))
                    .arrivalTime(LocalDateTime.now().plusHours(17))
                    .price(new BigDecimal("890.00"))
                    .availableSeats(30)
                    .build());

            // Flight 3: HND → CDG
            flightRepository.save(Flight.builder()
                    .flightNumber("SL-952")
                    .origin("HND")
                    .destination("CDG")
                    .departureTime(LocalDateTime.now().plusDays(5))
                    .arrivalTime(LocalDateTime.now().plusDays(5).plusHours(12))
                    .price(new BigDecimal("720.00"))
                    .availableSeats(183)
                    .build());

            // Flight 4: MAD → MIA
            flightRepository.save(Flight.builder()
                    .flightNumber("SL-615")
                    .origin("MAD")
                    .destination("MIA")
                    .departureTime(LocalDateTime.now().plusHours(12))
                    .arrivalTime(LocalDateTime.now().plusHours(21))
                    .price(new BigDecimal("520.00"))
                    .availableSeats(167)
                    .build());

            // Flight 5: SIN → LAX
            flightRepository.save(Flight.builder()
                    .flightNumber("SL-788")
                    .origin("SIN")
                    .destination("LAX")
                    .departureTime(LocalDateTime.now().plusHours(1))
                    .arrivalTime(LocalDateTime.now().plusHours(18))
                    .price(new BigDecimal("680.00"))
                    .availableSeats(43)
                    .build());

            // Flight 6: FRA → NRT
            flightRepository.save(Flight.builder()
                    .flightNumber("SL-330")
                    .origin("FRA")
                    .destination("NRT")
                    .departureTime(LocalDateTime.now().plusHours(18))
                    .arrivalTime(LocalDateTime.now().plusHours(29))
                    .price(new BigDecimal("610.00"))
                    .availableSeats(160)
                    .build());

            // --- NUEVAS RUTAS SOLICITADAS POR EL USUARIO ---

            // Barcelona - París (BCN - CDG)
            flightRepository.save(Flight.builder()
                    .flightNumber("SK-401")
                    .origin("BCN")
                    .destination("CDG")
                    .departureTime(LocalDateTime.now().plusHours(8))
                    .arrivalTime(LocalDateTime.now().plusHours(10))
                    .price(new BigDecimal("119.99"))
                    .availableSeats(150)
                    .build());

            // París - Barcelona (CDG - BCN)
            flightRepository.save(Flight.builder()
                    .flightNumber("SK-402")
                    .origin("CDG")
                    .destination("BCN")
                    .departureTime(LocalDateTime.now().plusHours(15))
                    .arrivalTime(LocalDateTime.now().plusHours(17))
                    .price(new BigDecimal("119.99"))
                    .availableSeats(150)
                    .build());

            // Barcelona - Madrid (BCN - MAD)
            flightRepository.save(Flight.builder()
                    .flightNumber("SK-101")
                    .origin("BCN")
                    .destination("MAD")
                    .departureTime(LocalDateTime.now().plusHours(2))
                    .arrivalTime(LocalDateTime.now().plusHours(3).plusMinutes(15))
                    .price(new BigDecimal("69.99"))
                    .availableSeats(180)
                    .build());

            // Madrid - Barcelona (MAD - BCN)
            flightRepository.save(Flight.builder()
                    .flightNumber("SK-102")
                    .origin("MAD")
                    .destination("BCN")
                    .departureTime(LocalDateTime.now().plusHours(6))
                    .arrivalTime(LocalDateTime.now().plusHours(7).plusMinutes(15))
                    .price(new BigDecimal("69.99"))
                    .availableSeats(180)
                    .build());

            // Barcelona - Roma (BCN - FCO)
            flightRepository.save(Flight.builder()
                    .flightNumber("SK-501")
                    .origin("BCN")
                    .destination("FCO")
                    .departureTime(LocalDateTime.now().plusHours(4))
                    .arrivalTime(LocalDateTime.now().plusHours(6))
                    .price(new BigDecimal("89.99"))
                    .availableSeats(120)
                    .build());

            // Roma - Barcelona (FCO - BCN)
            flightRepository.save(Flight.builder()
                    .flightNumber("SK-502")
                    .origin("FCO")
                    .destination("BCN")
                    .departureTime(LocalDateTime.now().plusHours(10))
                    .arrivalTime(LocalDateTime.now().plusHours(12))
                    .price(new BigDecimal("89.99"))
                    .availableSeats(120)
                    .build());

            // Madrid - Tokio Narita (MAD - NRT)
            flightRepository.save(Flight.builder()
                    .flightNumber("SK-701")
                    .origin("MAD")
                    .destination("NRT")
                    .departureTime(LocalDateTime.now().plusHours(20))
                    .arrivalTime(LocalDateTime.now().plusHours(34))
                    .price(new BigDecimal("899.00"))
                    .availableSeats(250)
                    .build());

            // Barcelona - Tokio Narita (BCN - NRT)
            flightRepository.save(Flight.builder()
                    .flightNumber("SK-702")
                    .origin("BCN")
                    .destination("NRT")
                    .departureTime(LocalDateTime.now().plusDays(1).plusHours(2))
                    .arrivalTime(LocalDateTime.now().plusDays(1).plusHours(16))
                    .price(new BigDecimal("919.00"))
                    .availableSeats(250)
                    .build());

            // Barcelona - Zúrich (BCN - ZRH) - Alpes Suizos
            flightRepository.save(Flight.builder()
                    .flightNumber("SK-801")
                    .origin("BCN")
                    .destination("ZRH")
                    .departureTime(LocalDateTime.now().plusHours(9))
                    .arrivalTime(LocalDateTime.now().plusHours(11))
                    .price(new BigDecimal("149.99"))
                    .availableSeats(100)
                    .build());

            // Zúrich - Barcelona (ZRH - BCN) - Alpes Suizos
            flightRepository.save(Flight.builder()
                    .flightNumber("SK-802")
                    .origin("ZRH")
                    .destination("BCN")
                    .departureTime(LocalDateTime.now().plusHours(18))
                    .arrivalTime(LocalDateTime.now().plusHours(20))
                    .price(new BigDecimal("149.99"))
                    .availableSeats(100)
                    .build());

            log.info("✅ flight-service: Carga inicial de vuelos completada. 14 vuelos guardados.");
        } else {
            log.info("♻️ flight-service: La base de datos ya contiene vuelos. Omitiendo carga inicial.");
        }
    }
}
