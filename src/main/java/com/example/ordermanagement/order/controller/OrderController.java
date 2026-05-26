package com.example.ordermanagement.order.controller;

import com.example.ordermanagement.order.dto.request.OrderRequest;
import com.example.ordermanagement.order.service.OrderService;
import com.example.ordermanagement.util.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/save-or-update")
    public ResponseEntity<CommonResponse> saveOrUpdateOrder(
            @Valid @RequestBody OrderRequest request
    ) {
        return new ResponseEntity<>(
                orderService.saveOrUpdateOrder(request),
                request.getOrderId() == null ? HttpStatus.CREATED : HttpStatus.OK
        );
    }

    @PatchMapping("/{orderId}/ship")
    public ResponseEntity<CommonResponse> shipOrder(@PathVariable Long orderId) {
        return new ResponseEntity<>(orderService.shipOrder(orderId), HttpStatus.OK);
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<CommonResponse> cancelOrder(@PathVariable Long orderId) {
        return new ResponseEntity<>(orderService.cancelOrder(orderId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<CommonResponse> getAllOrders() {
        return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<CommonResponse> getOrderById(@PathVariable Long orderId) {
        return new ResponseEntity<>(orderService.getOrderById(orderId), HttpStatus.OK);
    }
}