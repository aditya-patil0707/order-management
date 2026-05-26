package com.example.ordermanagement.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long productId;
    private String productName;
    private String sku;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private String categoryName;
}