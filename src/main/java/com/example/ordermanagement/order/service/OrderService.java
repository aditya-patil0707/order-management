package com.example.ordermanagement.order.service;

import com.example.ordermanagement.order.dto.request.OrderRequest;
import com.example.ordermanagement.util.CommonResponse;

public interface OrderService {

    CommonResponse saveOrUpdateOrder(OrderRequest request);

    CommonResponse shipOrder(Long orderId);

    CommonResponse cancelOrder(Long orderId);

    CommonResponse getAllOrders();

    CommonResponse getOrderById(Long orderId);
}