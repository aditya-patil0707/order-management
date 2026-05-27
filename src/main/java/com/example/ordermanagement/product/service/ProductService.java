package com.example.ordermanagement.product.service;

import com.example.ordermanagement.product.dto.request.ProductRequest;
import com.example.ordermanagement.util.CommonResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;


public interface ProductService {

    CommonResponse saveOrUpdateProduct(ProductRequest request);

    CommonResponse getProductById(Long productId);

    CommonResponse getAllProducts(Integer page, Integer size);

    CommonResponse deleteProduct(Long productId);
}