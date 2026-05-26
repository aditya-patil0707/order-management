package com.example.ordermanagement.payment.service.impl;

import com.example.ordermanagement.security.SecurityUtil;
import com.example.ordermanagement.order.entity.OrderEntity;
import com.example.ordermanagement.order.repository.OrderRepository;
import com.example.ordermanagement.payment.entity.PaymentEntity;
import com.example.ordermanagement.payment.model.request.PaymentRequest;
import com.example.ordermanagement.payment.model.response.PaymentResponse;
import com.example.ordermanagement.payment.repository.PaymentRepository;
import com.example.ordermanagement.payment.service.PaymentService;
import com.example.ordermanagement.util.CommonResponse;
import com.example.ordermanagement.util.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CommonResponseUtil commonResponseUtil;
    private final SecurityUtil securityUtil;

    @Override
    public CommonResponse saveOrUpdatePayment(PaymentRequest request) {

        log.info("Save/Update payment request received");

        OrderEntity orderEntity = orderRepository.findByOrderIdAndActiveTrue(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));

        PaymentEntity paymentEntity;
        boolean isCreate = request.getPaymentId() == null;

        if (isCreate) {

            if (paymentRepository.existsByOrderOrderIdAndActiveTrue(request.getOrderId())) {
                throw new RuntimeException("Payment already exists for this order");
            }

            paymentEntity = new PaymentEntity();
            paymentEntity.setOrder(orderEntity);
            paymentEntity.setPaymentNumber(generatePaymentNumber());
            paymentEntity.setCustomerName(orderEntity.getCustomerName());
            paymentEntity.setAmount(orderEntity.getTotalAmount());
            paymentEntity.setPaymentMode(request.getPaymentMode());
            paymentEntity.setPaymentStatus(request.getPaymentStatus());
            paymentEntity.setRemark(request.getRemark());
            paymentEntity.setActive(true);
            paymentEntity.setCreatedBy(currentUserId());
            paymentEntity.setCreatedDate(LocalDateTime.now());

        } else {

            paymentEntity = paymentRepository.findByPaymentIdAndActiveTrue(request.getPaymentId())
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            if (!paymentEntity.getOrder().getOrderId().equals(request.getOrderId())) {
                throw new RuntimeException("Payment order cannot be changed");
            }

            paymentEntity.setPaymentMode(request.getPaymentMode());
            paymentEntity.setPaymentStatus(request.getPaymentStatus());
            paymentEntity.setRemark(request.getRemark());
            paymentEntity.setUpdatedBy(currentUserId());
            paymentEntity.setUpdatedDate(LocalDateTime.now());
        }

        paymentRepository.save(paymentEntity);

        return commonResponseUtil.success(
                isCreate ? "Payment Created Successfully" : "Payment Updated Successfully",
                null
        );
    }

    @Override
    public CommonResponse getPaymentById(Long paymentId) {

        log.info("Fetching payment by id {}", paymentId);

        PaymentEntity paymentEntity = paymentRepository.findByPaymentIdAndActiveTrue(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return commonResponseUtil.success(
                "Payment fetched successfully",
                mapToResponse(paymentEntity)
        );
    }

    @Override
    public CommonResponse getPaymentByOrderId(Long orderId) {

        log.info("Fetching payment by order id {}", orderId);

        PaymentEntity paymentEntity = paymentRepository.findByOrderOrderIdAndActiveTrue(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order"));

        return commonResponseUtil.success(
                "Payment fetched successfully",
                mapToResponse(paymentEntity)
        );
    }

    private PaymentResponse mapToResponse(PaymentEntity paymentEntity) {

        return new PaymentResponse(
                paymentEntity.getPaymentId(),
                paymentEntity.getPaymentNumber(),
                paymentEntity.getOrder().getOrderId(),
                paymentEntity.getOrder().getOrderNumber(),
                paymentEntity.getCustomerName(),
                paymentEntity.getAmount(),
                paymentEntity.getPaymentMode(),
                paymentEntity.getPaymentStatus(),
                paymentEntity.getRemark()
        );
    }

    private String generatePaymentNumber() {
        return "PAY-" + System.currentTimeMillis();
    }

    private String currentUserId() {
        return securityUtil.getCurrentUserId();
    }
}