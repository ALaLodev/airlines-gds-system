package com.gds.inventory_service.repository;

import com.gds.inventory_service.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository  extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByScheduleId(Long scheduleId);
}
