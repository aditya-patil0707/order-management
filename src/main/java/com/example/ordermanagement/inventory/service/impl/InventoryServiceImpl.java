package com.example.ordermanagement.inventory.service.impl;

import com.example.ordermanagement.inventory.dto.request.InventoryAdjustRequest;
import com.example.ordermanagement.inventory.dto.response.InventoryResponse;
import com.example.ordermanagement.inventory.dto.response.InventoryTransactionResponse;
import com.example.ordermanagement.inventory.entity.InventoryEntity;
import com.example.ordermanagement.inventory.entity.InventoryTransactionEntity;
import com.example.ordermanagement.inventory.enums.InventoryTransactionType;
import com.example.ordermanagement.inventory.repository.InventoryRepository;
import com.example.ordermanagement.inventory.repository.InventoryTransactionRepository;
import com.example.ordermanagement.inventory.service.InventoryService;
import com.example.ordermanagement.product.entity.ProductEntity;
import com.example.ordermanagement.security.SecurityUtil;
import com.example.ordermanagement.util.CommonResponse;
import com.example.ordermanagement.util.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final CommonResponseUtil commonResponseUtil;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public void createInventoryForProduct(ProductEntity productEntity) {

        InventoryEntity inventoryEntity = new InventoryEntity();
        inventoryEntity.setProduct(productEntity);
        inventoryEntity.setAvailableQuantity(0);
        inventoryEntity.setReservedQuantity(0);
        inventoryEntity.setActive(true);
        inventoryEntity.setCreatedBy(currentUserId());
        inventoryEntity.setCreatedDate(LocalDateTime.now());

        inventoryRepository.save(inventoryEntity);
    }

    @Override
    @Transactional
    public CommonResponse adjustStock(InventoryAdjustRequest request) {

        InventoryEntity inventoryEntity = getInventoryByProductIdEntity(request.getProductId());

        Integer previousAvailableQuantity = inventoryEntity.getAvailableQuantity();
        Integer previousReservedQuantity = inventoryEntity.getReservedQuantity();

        Integer newAvailableQuantity;
        Integer newReservedQuantity = previousReservedQuantity;
        Integer transactionQuantity;

        switch (request.getTransactionType()) {

            case STOCK_IN -> {
                validateQuantity(request.getQuantity());
                transactionQuantity = request.getQuantity();
                newAvailableQuantity = previousAvailableQuantity + request.getQuantity();
            }

            case STOCK_OUT -> {
                validateQuantity(request.getQuantity());

                if (previousAvailableQuantity < request.getQuantity()) {
                    throw new RuntimeException("Insufficient available stock");
                }

                transactionQuantity = request.getQuantity();
                newAvailableQuantity = previousAvailableQuantity - request.getQuantity();
            }

            case STOCK_ADJUSTMENT -> {
                if (request.getAvailableQuantity() == null || request.getReservedQuantity() == null) {
                    throw new RuntimeException("Available quantity and reserved quantity are required");
                }

                if (request.getAvailableQuantity() < 0 || request.getReservedQuantity() < 0) {
                    throw new RuntimeException("Quantity cannot be negative");
                }

                newAvailableQuantity = request.getAvailableQuantity();
                newReservedQuantity = request.getReservedQuantity();

                transactionQuantity = Math.abs(
                        (newAvailableQuantity + newReservedQuantity)
                                - (previousAvailableQuantity + previousReservedQuantity)
                );

                if (transactionQuantity == 0) {
                    transactionQuantity = 1;
                }
            }

            default -> throw new RuntimeException("Invalid transaction type for manual stock adjustment");
        }

        updateInventory(inventoryEntity, newAvailableQuantity, newReservedQuantity);

        saveTransaction(
                inventoryEntity,
                request.getTransactionType(),
                transactionQuantity,
                previousAvailableQuantity,
                newAvailableQuantity,
                previousReservedQuantity,
                newReservedQuantity,
                request.getRemarks(),
                null,
                null
        );

        return commonResponseUtil.success("Inventory updated successfully", null);
    }

    @Override
    @Transactional
    public void reserveStock(Long productId, Integer quantity, Long orderId, String orderNumber) {

        validateQuantity(quantity);

        InventoryEntity inventoryEntity = getInventoryByProductIdEntity(productId);

        Integer previousAvailableQuantity = inventoryEntity.getAvailableQuantity();
        Integer previousReservedQuantity = inventoryEntity.getReservedQuantity();

        if (previousAvailableQuantity < quantity) {
            throw new RuntimeException("Insufficient available stock");
        }

        Integer newAvailableQuantity = previousAvailableQuantity - quantity;
        Integer newReservedQuantity = previousReservedQuantity + quantity;

        updateInventory(inventoryEntity, newAvailableQuantity, newReservedQuantity);

        saveTransaction(
                inventoryEntity,
                InventoryTransactionType.STOCK_RESERVED,
                quantity,
                previousAvailableQuantity,
                newAvailableQuantity,
                previousReservedQuantity,
                newReservedQuantity,
                "Stock reserved for order",
                orderId,
                orderNumber
        );
    }

    @Override
    @Transactional
    public void confirmStock(Long productId, Integer quantity, Long orderId, String orderNumber) {

        validateQuantity(quantity);

        InventoryEntity inventoryEntity = getInventoryByProductIdEntity(productId);

        Integer previousAvailableQuantity = inventoryEntity.getAvailableQuantity();
        Integer previousReservedQuantity = inventoryEntity.getReservedQuantity();

        if (previousReservedQuantity < quantity) {
            throw new RuntimeException("Insufficient reserved stock");
        }

        Integer newAvailableQuantity = previousAvailableQuantity;
        Integer newReservedQuantity = previousReservedQuantity - quantity;

        updateInventory(inventoryEntity, newAvailableQuantity, newReservedQuantity);

        saveTransaction(
                inventoryEntity,
                InventoryTransactionType.STOCK_CONFIRMED,
                quantity,
                previousAvailableQuantity,
                newAvailableQuantity,
                previousReservedQuantity,
                newReservedQuantity,
                "Reserved stock confirmed/shipped",
                orderId,
                orderNumber
        );
    }

    @Override
    @Transactional
    public void releaseStock(Long productId, Integer quantity, Long orderId, String orderNumber) {

        validateQuantity(quantity);

        InventoryEntity inventoryEntity = getInventoryByProductIdEntity(productId);

        Integer previousAvailableQuantity = inventoryEntity.getAvailableQuantity();
        Integer previousReservedQuantity = inventoryEntity.getReservedQuantity();

        if (previousReservedQuantity < quantity) {
            throw new RuntimeException("Insufficient reserved stock");
        }

        Integer newAvailableQuantity = previousAvailableQuantity + quantity;
        Integer newReservedQuantity = previousReservedQuantity - quantity;

        updateInventory(inventoryEntity, newAvailableQuantity, newReservedQuantity);

        saveTransaction(
                inventoryEntity,
                InventoryTransactionType.STOCK_RELEASED,
                quantity,
                previousAvailableQuantity,
                newAvailableQuantity,
                previousReservedQuantity,
                newReservedQuantity,
                "Reserved stock released after order cancellation",
                orderId,
                orderNumber
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CommonResponse getInventoryByProductId(Long productId) {

        InventoryEntity inventoryEntity = getInventoryByProductIdEntity(productId);

        InventoryResponse response = mapToInventoryResponse(inventoryEntity);

        return commonResponseUtil.success("Inventory fetched successfully", response);
    }

    @Override
    @Transactional(readOnly = true)
    public CommonResponse getTransactionsByProductId(Long productId) {

        InventoryEntity inventoryEntity = getInventoryByProductIdEntity(productId);

        List<InventoryTransactionResponse> responses =
                transactionRepository
                        .findByInventoryInventoryIdAndActiveTrueOrderByCreatedDateDesc(
                                inventoryEntity.getInventoryId()
                        )
                        .stream()
                        .map(this::mapToTransactionResponse)
                        .toList();

        return commonResponseUtil.success("Inventory transactions fetched successfully", responses);
    }

    private InventoryEntity getInventoryByProductIdEntity(Long productId) {
        return inventoryRepository.findByProductProductIdAndActiveTrue(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product"));
    }

    private void updateInventory(
            InventoryEntity inventoryEntity,
            Integer availableQuantity,
            Integer reservedQuantity
    ) {
        inventoryEntity.setAvailableQuantity(availableQuantity);
        inventoryEntity.setReservedQuantity(reservedQuantity);
        inventoryEntity.setUpdatedBy(currentUserId());
        inventoryEntity.setUpdatedDate(LocalDateTime.now());

        inventoryRepository.save(inventoryEntity);
    }

    private void saveTransaction(
            InventoryEntity inventoryEntity,
            InventoryTransactionType transactionType,
            Integer quantity,
            Integer previousAvailableQuantity,
            Integer newAvailableQuantity,
            Integer previousReservedQuantity,
            Integer newReservedQuantity,
            String remarks,
            Long orderId,
            String orderNumber
    ) {
        InventoryTransactionEntity transactionEntity = new InventoryTransactionEntity();

        transactionEntity.setInventory(inventoryEntity);
        transactionEntity.setTransactionType(transactionType);
        transactionEntity.setOrderId(orderId);
        transactionEntity.setOrderNumber(orderNumber);
        transactionEntity.setQuantity(quantity);

        transactionEntity.setAvailableBeforeQuantity(previousAvailableQuantity);
        transactionEntity.setAvailableAfterQuantity(newAvailableQuantity);
        transactionEntity.setReservedBeforeQuantity(previousReservedQuantity);
        transactionEntity.setReservedAfterQuantity(newReservedQuantity);

        transactionEntity.setRemarks(remarks);
        transactionEntity.setActive(true);
        transactionEntity.setCreatedBy(currentUserId());
        transactionEntity.setCreatedDate(LocalDateTime.now());

        transactionRepository.save(transactionEntity);
    }

    private InventoryResponse mapToInventoryResponse(InventoryEntity inventoryEntity) {

        return new InventoryResponse(
                inventoryEntity.getInventoryId(),
                inventoryEntity.getProduct().getProductId(),
                inventoryEntity.getProduct().getProductName(),
                inventoryEntity.getAvailableQuantity(),
                inventoryEntity.getReservedQuantity()
        );
    }

    private InventoryTransactionResponse mapToTransactionResponse(
            InventoryTransactionEntity transactionEntity
    ) {
        return new InventoryTransactionResponse(
                transactionEntity.getTransactionId(),
                transactionEntity.getInventory().getInventoryId(),
                transactionEntity.getTransactionType(),
                transactionEntity.getOrderId(),
                transactionEntity.getOrderNumber(),
                transactionEntity.getAvailableBeforeQuantity(),
                transactionEntity.getAvailableAfterQuantity(),
                transactionEntity.getReservedBeforeQuantity(),
                transactionEntity.getReservedAfterQuantity(),
                transactionEntity.getQuantity(),
                transactionEntity.getRemarks()
        );
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than zero");
        }
    }

    private String currentUserId() {
        return securityUtil.getCurrentUserId();
    }
}