package com.example.ordermanagement.inventory.service;

import com.example.ordermanagement.inventory.dto.request.InventoryAdjustRequest;
import com.example.ordermanagement.product.entity.ProductEntity;
import com.example.ordermanagement.util.CommonResponse;

public interface InventoryService {

    void createInventoryForProduct(ProductEntity productEntity);

    CommonResponse getInventoryByProductId(Long productId);

    CommonResponse getTransactionsByProductId(Long productId);

    CommonResponse adjustStock(InventoryAdjustRequest request);

    void reserveStock(Long productId, Integer quantity, Long orderId, String orderNumber);

    void confirmStock(Long productId, Integer quantity, Long orderId, String orderNumber);

    void releaseStock(Long productId, Integer quantity, Long orderId, String orderNumber);
}