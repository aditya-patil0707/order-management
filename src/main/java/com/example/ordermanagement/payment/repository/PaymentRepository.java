package com.example.ordermanagement.payment.repository;

import com.example.ordermanagement.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {

    Optional<PaymentEntity> findByPaymentIdAndActiveTrue(Long paymentId);

    Optional<PaymentEntity> findByOrderOrderIdAndActiveTrue(Long orderId);

    boolean existsByOrderOrderIdAndActiveTrue(Long orderId);

    boolean existsByPaymentNumber(String paymentNumber);
}