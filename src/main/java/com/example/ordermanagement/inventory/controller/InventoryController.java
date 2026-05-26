package com.example.ordermanagement.inventory.controller;

import com.example.ordermanagement.inventory.dto.request.InventoryAdjustRequest;
import com.example.ordermanagement.inventory.service.InventoryService;
import com.example.ordermanagement.util.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PutMapping("/adjust-stock")
    public ResponseEntity<CommonResponse> adjustStock(
            @Valid @RequestBody InventoryAdjustRequest request
    ) {
        return new ResponseEntity<>(
                inventoryService.adjustStock(request),
                HttpStatus.OK
        );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<CommonResponse> getInventoryByProductId(
            @PathVariable Long productId
    ) {
        return new ResponseEntity<>(
                inventoryService.getInventoryByProductId(productId),
                HttpStatus.OK
        );
    }

    @GetMapping("/product/{productId}/transactions")
    public ResponseEntity<CommonResponse> getTransactionsByProductId(
            @PathVariable Long productId
    ) {
        return new ResponseEntity<>(
                inventoryService.getTransactionsByProductId(productId),
                HttpStatus.OK
        );
    }
}