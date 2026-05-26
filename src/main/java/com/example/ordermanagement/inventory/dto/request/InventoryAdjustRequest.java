package com.example.ordermanagement.inventory.dto.request;

import com.example.ordermanagement.inventory.enums.InventoryTransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAdjustRequest {

    @NotNull(message = "productId is required")
    private Long productId;

    @NotNull(message = "transactionType is required")
    private InventoryTransactionType transactionType;

    private Integer quantity;

    private Integer availableQuantity;

    private Integer reservedQuantity;

    private String remarks;
}