package com.example.ordermanagement.payment.model.response;

import com.example.ordermanagement.payment.enums.PaymentMode;
import com.example.ordermanagement.payment.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long paymentId;
    private String paymentNumber;
    private Long orderId;
    private String orderNumber;
    private String customerName;
    private BigDecimal amount;
    private PaymentMode paymentMode;
    private PaymentStatus paymentStatus;
    private String remark;
}