package com.example.ordermanagement.order.service.impl;

import com.example.ordermanagement.inventory.service.InventoryService;
import com.example.ordermanagement.order.dto.request.OrderItemRequest;
import com.example.ordermanagement.order.dto.request.OrderRequest;
import com.example.ordermanagement.order.entity.OrderEntity;
import com.example.ordermanagement.order.entity.OrderItemEntity;
import com.example.ordermanagement.order.enums.OrderStatus;
import com.example.ordermanagement.order.repository.OrderItemRepository;
import com.example.ordermanagement.order.repository.OrderRepository;
import com.example.ordermanagement.order.service.OrderService;
import com.example.ordermanagement.product.entity.ProductEntity;
import com.example.ordermanagement.product.repository.ProductRepository;
import com.example.ordermanagement.security.SecurityUtil;
import com.example.ordermanagement.util.CommonResponse;
import com.example.ordermanagement.util.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final CommonResponseUtil commonResponseUtil;
    private final SecurityUtil securityUtil;

    // Create new order or update existing pending order.
    @Override
    @Transactional
    public CommonResponse saveOrUpdateOrder(OrderRequest request) {

        if (request.getOrderId() == null) {
            createOrder(request);
            return commonResponseUtil.success("Order created successfully", null);
        }

        updateOrder(request);
        return commonResponseUtil.success("Order updated successfully", null);
    }

    // Creates order and reserves stock for all order items.
    private void createOrder(OrderRequest request) {

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderNumber(generateOrderNumber());
        orderEntity.setOrderStatus(OrderStatus.PENDING);
        orderEntity.setCustomerName(request.getCustomerName());
        orderEntity.setTotalAmount(BigDecimal.ZERO);
        orderEntity.setActive(true);
        orderEntity.setCreatedBy(currentUserId());
        orderEntity.setCreatedDate(LocalDateTime.now());

        OrderEntity savedOrder = orderRepository.save(orderEntity);

        BigDecimal totalAmount = saveOrderItemsAndReserveStock(savedOrder, request.getOrderItems());

        savedOrder.setTotalAmount(totalAmount);
        orderRepository.save(savedOrder);
    }

    // Updates order items by reserving/releasing only quantity difference.
    private void updateOrder(OrderRequest request) {

        OrderEntity orderEntity = getActiveOrder(request.getOrderId());

        if (OrderStatus.SHIPPED.equals(orderEntity.getOrderStatus())) {
            throw new RuntimeException("Shipped order cannot be updated");
        }

        if (OrderStatus.CANCELLED.equals(orderEntity.getOrderStatus())) {
            throw new RuntimeException("Cancelled order cannot be updated");
        }

        orderEntity.setCustomerName(request.getCustomerName());
        orderEntity.setUpdatedBy(currentUserId());
        orderEntity.setUpdatedDate(LocalDateTime.now());

        BigDecimal totalAmount = updateOrderItemsAndAdjustStock(orderEntity, request.getOrderItems());

        orderEntity.setTotalAmount(totalAmount);
        orderRepository.save(orderEntity);
    }

    // Saves new order items and reserves stock during order creation.
    private BigDecimal saveOrderItemsAndReserveStock(
            OrderEntity orderEntity,
            List<OrderItemRequest> itemRequests
    ) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : itemRequests) {

            ProductEntity productEntity = getActiveProduct(itemRequest.getProductId());

            OrderItemEntity orderItemEntity = createOrderItem(
                    orderEntity,
                    productEntity,
                    itemRequest.getQuantity()
            );

            orderItemRepository.save(orderItemEntity);

            inventoryService.reserveStock(
                    productEntity.getProductId(),
                    itemRequest.getQuantity(),
                    orderEntity.getOrderId(),
                    orderEntity.getOrderNumber()
            );

            totalAmount = totalAmount.add(orderItemEntity.getTotalPrice());
        }

        return totalAmount;
    }

    // Updates existing items, adds new items, and releases removed items.
    private BigDecimal updateOrderItemsAndAdjustStock(
            OrderEntity orderEntity,
            List<OrderItemRequest> newItemRequests
    ) {
        List<OrderItemEntity> oldItems =
                orderItemRepository.findByOrderOrderIdAndActiveTrue(orderEntity.getOrderId());

        Map<Long, OrderItemEntity> oldItemMap = oldItems.stream()
                .collect(Collectors.toMap(
                        item -> item.getProduct().getProductId(),
                        item -> item
                ));

        Map<Long, OrderItemRequest> newItemMap = newItemRequests.stream()
                .collect(Collectors.toMap(
                        OrderItemRequest::getProductId,
                        item -> item
                ));

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest newItemRequest : newItemRequests) {

            Long productId = newItemRequest.getProductId();
            Integer newQuantity = newItemRequest.getQuantity();

            ProductEntity productEntity = getActiveProduct(productId);

            OrderItemEntity oldItem = oldItemMap.get(productId);

            if (oldItem == null) {

                OrderItemEntity newOrderItem = createOrderItem(
                        orderEntity,
                        productEntity,
                        newQuantity
                );

                orderItemRepository.save(newOrderItem);

                inventoryService.reserveStock(
                        productId,
                        newQuantity,
                        orderEntity.getOrderId(),
                        orderEntity.getOrderNumber()
                );

                totalAmount = totalAmount.add(newOrderItem.getTotalPrice());

            } else {

                int oldQuantity = oldItem.getQuantity();

                if (newQuantity > oldQuantity) {

                    int extraQuantity = newQuantity - oldQuantity;

                    inventoryService.reserveStock(
                            productId,
                            extraQuantity,
                            orderEntity.getOrderId(),
                            orderEntity.getOrderNumber()
                    );

                } else if (newQuantity < oldQuantity) {

                    int reducedQuantity = oldQuantity - newQuantity;

                    inventoryService.releaseStock(
                            productId,
                            reducedQuantity,
                            orderEntity.getOrderId(),
                            orderEntity.getOrderNumber()
                    );
                }

                BigDecimal totalPrice = productEntity.getPrice()
                        .multiply(BigDecimal.valueOf(newQuantity));

                oldItem.setQuantity(newQuantity);
                oldItem.setUnitPrice(productEntity.getPrice());
                oldItem.setTotalPrice(totalPrice);
                oldItem.setProductName(productEntity.getProductName());
                oldItem.setSku(productEntity.getSku());
                oldItem.setUpdatedBy(currentUserId());
                oldItem.setUpdatedDate(LocalDateTime.now());

                orderItemRepository.save(oldItem);

                totalAmount = totalAmount.add(totalPrice);
            }
        }

        for (OrderItemEntity oldItem : oldItems) {

            Long oldProductId = oldItem.getProduct().getProductId();

            if (!newItemMap.containsKey(oldProductId)) {

                inventoryService.releaseStock(
                        oldProductId,
                        oldItem.getQuantity(),
                        orderEntity.getOrderId(),
                        orderEntity.getOrderNumber()
                );

                oldItem.setActive(false);
                oldItem.setUpdatedBy(currentUserId());
                oldItem.setUpdatedDate(LocalDateTime.now());

                orderItemRepository.save(oldItem);
            }
        }

        return totalAmount;
    }

    // Confirms reserved stock and marks order as shipped.
    @Override
    @Transactional
    public CommonResponse shipOrder(Long orderId) {

        OrderEntity orderEntity = getActiveOrder(orderId);

        if (OrderStatus.SHIPPED.equals(orderEntity.getOrderStatus())) {
            throw new RuntimeException("Order already shipped");
        }

        if (OrderStatus.CANCELLED.equals(orderEntity.getOrderStatus())) {
            throw new RuntimeException("Cancelled order cannot be shipped");
        }

        List<OrderItemEntity> orderItems =
                orderItemRepository.findByOrderOrderIdAndActiveTrue(orderId);

        for (OrderItemEntity orderItem : orderItems) {
            inventoryService.confirmStock(
                    orderItem.getProduct().getProductId(),
                    orderItem.getQuantity(),
                    orderEntity.getOrderId(),
                    orderEntity.getOrderNumber()
            );
        }

        orderEntity.setOrderStatus(OrderStatus.SHIPPED);
        orderEntity.setUpdatedBy(currentUserId());
        orderEntity.setUpdatedDate(LocalDateTime.now());

        orderRepository.save(orderEntity);

        return commonResponseUtil.success("Order shipped successfully", null);
    }

    // Releases reserved stock and marks order as cancelled.
    @Override
    @Transactional
    public CommonResponse cancelOrder(Long orderId) {

        OrderEntity orderEntity = getActiveOrder(orderId);

        if (OrderStatus.CANCELLED.equals(orderEntity.getOrderStatus())) {
            throw new RuntimeException("Order already cancelled");
        }

        if (OrderStatus.SHIPPED.equals(orderEntity.getOrderStatus())) {
            throw new RuntimeException("Shipped order cannot be cancelled");
        }

        List<OrderItemEntity> orderItems =
                orderItemRepository.findByOrderOrderIdAndActiveTrue(orderId);

        for (OrderItemEntity orderItem : orderItems) {
            inventoryService.releaseStock(
                    orderItem.getProduct().getProductId(),
                    orderItem.getQuantity(),
                    orderEntity.getOrderId(),
                    orderEntity.getOrderNumber()
            );
        }

        orderEntity.setOrderStatus(OrderStatus.CANCELLED);
        orderEntity.setUpdatedBy(currentUserId());
        orderEntity.setUpdatedDate(LocalDateTime.now());

        orderRepository.save(orderEntity);

        return commonResponseUtil.success("Order cancelled successfully", null);
    }

    // Fetches all active orders.
    @Override
    @Transactional(readOnly = true)
    public CommonResponse getAllOrders() {

        List<OrderEntity> orders = orderRepository.findAll()
                .stream()
                .filter(order -> Boolean.TRUE.equals(order.getActive()))
                .toList();

        return commonResponseUtil.success("Orders fetched successfully", orders);
    }

    // Fetches active order by id.
    @Override
    @Transactional(readOnly = true)
    public CommonResponse getOrderById(Long orderId) {

        OrderEntity orderEntity = getActiveOrder(orderId);

        return commonResponseUtil.success("Order fetched successfully", orderEntity);
    }

    private OrderItemEntity createOrderItem(
            OrderEntity orderEntity,
            ProductEntity productEntity,
            Integer quantity
    ) {
        BigDecimal totalPrice = productEntity.getPrice()
                .multiply(BigDecimal.valueOf(quantity));

        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrder(orderEntity);
        orderItemEntity.setProduct(productEntity);
        orderItemEntity.setProductName(productEntity.getProductName());
        orderItemEntity.setSku(productEntity.getSku());
        orderItemEntity.setQuantity(quantity);
        orderItemEntity.setUnitPrice(productEntity.getPrice());
        orderItemEntity.setTotalPrice(totalPrice);
        orderItemEntity.setActive(true);
        orderItemEntity.setCreatedBy(currentUserId());
        orderItemEntity.setCreatedDate(LocalDateTime.now());

        return orderItemEntity;
    }

    private ProductEntity getActiveProduct(Long productId) {
        return productRepository.findByProductIdAndActiveTrue(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    private OrderEntity getActiveOrder(Long orderId) {
        return orderRepository.findByOrderIdAndActiveTrue(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }

    private String currentUserId() {
        return securityUtil.getCurrentUserId();
    }
}