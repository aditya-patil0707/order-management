package com.example.ordermanagement.product.service;

import com.example.ordermanagement.product.dto.request.ProductCategoryRequest;
import com.example.ordermanagement.util.CommonResponse;


public interface ProductCategoryService {

    CommonResponse saveOrUpdateCategory(ProductCategoryRequest request);

    CommonResponse getCategoryById(Long categoryId);
    CommonResponse getAllCategories();

}