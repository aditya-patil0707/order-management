package com.example.ordermanagement.inventory.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {

    private Long inventoryId;

    private Long productId;

    private String productName;

    private Integer availableQuantity;

    private Integer reservedQuantity;
}