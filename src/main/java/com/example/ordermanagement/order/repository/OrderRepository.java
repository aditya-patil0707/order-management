package com.example.ordermanagement.order.repository;

import com.example.ordermanagement.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByOrderIdAndActiveTrue(Long orderId);

    boolean existsByOrderNumber(String orderNumber);
}