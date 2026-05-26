package com.example.ordermanagement.inventory.repository;

import com.example.ordermanagement.inventory.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {

    Optional<InventoryEntity> findByProductProductIdAndActiveTrue(Long productId);
}