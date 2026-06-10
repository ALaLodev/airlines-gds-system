package com.gds.airline.agency_service.repository;

import com.gds.airline.agency_service.entity.Agency;
import com.gds.airline.agency_service.entity.AgencyStatus;
import com.gds.airline.agency_service.entity.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgencyRepository extends JpaRepository<Agency, Long> {

    @Query("SELECT a FROM Agency a WHERE " +
           "(:search IS NULL OR LOWER(a.agencyName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.iataCode) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.city) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:region IS NULL OR a.region = :region)")
    Page<Agency> searchAgencies(@Param("search") String search, 
                               @Param("status") AgencyStatus status, 
                               @Param("region") Region region, 
                               Pageable pageable);

    @Query("SELECT a FROM Agency a ORDER BY a.bookings30d DESC LIMIT :limit")
    List<Agency> findTopPerformers(@Param("limit") int limit);

    @Query("SELECT SUM(a.bookings30d) FROM Agency a")
    Integer getTotalActiveBookings();
    
    @Query("SELECT AVG(a.complianceRate) FROM Agency a")
    Double getAverageComplianceRate();
}
