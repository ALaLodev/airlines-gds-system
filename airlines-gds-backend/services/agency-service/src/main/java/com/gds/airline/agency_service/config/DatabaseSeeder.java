package com.gds.airline.agency_service.config;

import com.gds.airline.agency_service.entity.Agency;
import com.gds.airline.agency_service.entity.AgencyStatus;
import com.gds.airline.agency_service.entity.Region;
import com.gds.airline.agency_service.repository.AgencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final AgencyRepository agencyRepository;

    @Override
    public void run(String... args) throws Exception {
        if (agencyRepository.count() == 0) {
            log.info("Seeding agencies database...");
            
            // EMEA (3 agencies)
            agencyRepository.save(Agency.builder()
                    .agencyName("GlobeTrotter Travel EMEA")
                    .iataCode("1234567")
                    .city("Madrid")
                    .country("Spain")
                    .region(Region.EMEA)
                    .contactName("Sofia Gomez")
                    .contactEmail("sofia@globetrotter.com")
                    .status(AgencyStatus.ACTIVE)
                    .bookings30d(1250)
                    .complianceRate(98.5)
                    .build());

            agencyRepository.save(Agency.builder()
                    .agencyName("EuroVoyage Link")
                    .iataCode("2345678")
                    .city("Paris")
                    .country("France")
                    .region(Region.EMEA)
                    .contactName("Pierre Dubois")
                    .contactEmail("pierre@eurovoyage.fr")
                    .status(AgencyStatus.ACTIVE)
                    .bookings30d(820)
                    .complianceRate(94.2)
                    .build());

            agencyRepository.save(Agency.builder()
                    .agencyName("Alps & Med Travel")
                    .iataCode("3456789")
                    .city("Rome")
                    .country("Italy")
                    .region(Region.EMEA)
                    .contactName("Matteo Rossi")
                    .contactEmail("matteo@alpsmed.it")
                    .status(AgencyStatus.SUSPENDED)
                    .bookings30d(150)
                    .complianceRate(85.0)
                    .build());

            // APAC (1 agency)
            agencyRepository.save(Agency.builder()
                    .agencyName("Pacific Horizon Tours")
                    .iataCode("4567890")
                    .city("Tokyo")
                    .country("Japan")
                    .region(Region.APAC)
                    .contactName("Yuki Tanaka")
                    .contactEmail("yuki@pacifichorizon.jp")
                    .status(AgencyStatus.ACTIVE)
                    .bookings30d(2100)
                    .complianceRate(99.1)
                    .build());

            // AMER (1 agency)
            agencyRepository.save(Agency.builder()
                    .agencyName("Liberty Express Travel")
                    .iataCode("5678901")
                    .city("New York")
                    .country("United States")
                    .region(Region.AMER)
                    .contactName("John Smith")
                    .contactEmail("john@libertyexpress.us")
                    .status(AgencyStatus.ACTIVE)
                    .bookings30d(3400)
                    .complianceRate(97.8)
                    .build());

            log.info("Agencies database seeded successfully!");
        }
    }
}
