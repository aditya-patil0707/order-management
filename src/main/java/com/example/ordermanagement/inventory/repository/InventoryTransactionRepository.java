package com.example.ordermanagement.inventory.repository;

import com.example.ordermanagement.inventory.entity.InventoryTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransactionEntity, Long> {

    List<InventoryTransactionEntity> findByInventoryInventoryIdAndActiveTrueOrderByCreatedDateDesc(Long inventoryId);
}