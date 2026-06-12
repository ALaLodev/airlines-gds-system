package com.gds.inventory_service.repository;

import com.gds.inventory_service.model.Inventory;
import com.gds.inventory_service.model.InventoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByScheduleId(Long scheduleId);

    Page<Inventory> findByStatus(InventoryStatus status, Pageable pageable);

    @Query("SELECT i FROM Inventory i WHERE " +
           "LOWER(i.flightNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.origin) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.destination) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Inventory> searchByKeyword(@Param("search") String search, Pageable pageable);

    @Query("SELECT i FROM Inventory i WHERE " +
           "(LOWER(i.flightNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.origin) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(i.destination) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "i.status = :status")
    Page<Inventory> searchByKeywordAndStatus(@Param("search") String search, @Param("status") InventoryStatus status, Pageable pageable);

    List<Inventory> findByStatusNot(InventoryStatus status);
}
