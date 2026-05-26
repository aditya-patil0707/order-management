package com.example.ordermanagement.order.repository;

import com.example.ordermanagement.order.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    List<OrderItemEntity> findByOrderOrderIdAndActiveTrue(Long orderId);
}