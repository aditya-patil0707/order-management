package com.example.ordermanagement.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {

    private Long orderId;

    @NotBlank(message = "customerName is required")
    private String customerName;

    @Valid
    @NotEmpty(message = "orderItems are required")
    private List<OrderItemRequest> orderItems;
}