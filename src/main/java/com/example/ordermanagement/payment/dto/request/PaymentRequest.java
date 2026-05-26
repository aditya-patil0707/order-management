package com.example.ordermanagement.payment.model.request;

import com.example.ordermanagement.payment.enums.PaymentMode;
import com.example.ordermanagement.payment.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    private Long paymentId;

    @NotNull(message = "Order id is required")
    private Long orderId;

    @NotNull(message = "Payment mode is required")
    private PaymentMode paymentMode;

    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;

    private String remark;
}