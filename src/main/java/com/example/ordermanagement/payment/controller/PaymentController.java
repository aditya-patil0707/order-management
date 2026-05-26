package com.example.ordermanagement.payment.controller;

import com.example.ordermanagement.payment.model.request.PaymentRequest;
import com.example.ordermanagement.payment.service.PaymentService;
import com.example.ordermanagement.util.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<CommonResponse> saveOrUpdatePayment(
            @RequestBody @Valid PaymentRequest request
    ) {
        log.info("Save/Update payment request received");
        return ResponseEntity.ok(paymentService.saveOrUpdatePayment(request));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<CommonResponse> getPaymentById(@PathVariable Long paymentId) {
        log.info("Get payment by id request received. paymentId={}", paymentId);
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<CommonResponse> getPaymentByOrderId(@PathVariable Long orderId) {
        log.info("Get payment by order id request received. orderId={}", orderId);
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }
}