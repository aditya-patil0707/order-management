package com.example.ordermanagement.inventory.dto.response;

import com.example.ordermanagement.inventory.enums.InventoryTransactionType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransactionResponse {

    private Long transactionId;
    private Long inventoryId;
    private InventoryTransactionType transactionType;

    private Long orderId;
    private String orderNumber;

    private Integer availableBeforeQuantity;
    private Integer availableAfterQuantity;

    private Integer reservedBeforeQuantity;
    private Integer reservedAfterQuantity;

    private Integer quantity;
    private String remarks;
}