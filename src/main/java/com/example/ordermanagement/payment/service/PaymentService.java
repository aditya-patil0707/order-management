package com.example.ordermanagement.payment.service;

import com.example.ordermanagement.payment.model.request.PaymentRequest;
import com.example.ordermanagement.util.CommonResponse;

public interface PaymentService {

    CommonResponse saveOrUpdatePayment(PaymentRequest request);

    CommonResponse getPaymentById(Long paymentId);

    CommonResponse getPaymentByOrderId(Long orderId);
}