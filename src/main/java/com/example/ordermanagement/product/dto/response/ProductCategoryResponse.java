package com.example.ordermanagement.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryResponse {

    private Long categoryId;
    private String categoryName;
    private String description;
}