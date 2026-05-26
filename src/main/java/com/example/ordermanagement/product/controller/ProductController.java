package com.example.ordermanagement.product.controller;

import com.example.ordermanagement.product.dto.request.ProductCategoryRequest;
import com.example.ordermanagement.product.dto.request.ProductRequest;
import com.example.ordermanagement.product.service.ProductCategoryService;
import com.example.ordermanagement.product.service.ProductService;
import com.example.ordermanagement.util.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;
    private final ProductCategoryService productCategoryService;

    // ================= PRODUCT CATEGORY =================

    // SAVE OR UPDATE CATEGORY
    @PostMapping("/categories")
    public ResponseEntity<CommonResponse> saveOrUpdateCategory(
            @RequestBody @Valid ProductCategoryRequest request
    ) {

        log.info("Save/Update category request received");

        return ResponseEntity.ok(
                productCategoryService.saveOrUpdateCategory(request)
        );
    }

    // GET CATEGORY BY ID
    @GetMapping("/categories/{categoryId}")
    public ResponseEntity<CommonResponse> getCategoryById(
            @PathVariable Long categoryId
    ) {

        log.info("Get category by id request received. categoryId={}", categoryId);

        return ResponseEntity.ok(
                productCategoryService.getCategoryById(categoryId)
        );
    }

    // GET ALL CATEGORIES
    @GetMapping("/categories")
    public ResponseEntity<CommonResponse> getAllCategories() {

        log.info("Get all categories request received");
        return ResponseEntity.ok(productCategoryService.getAllCategories());
    }


    // ================= PRODUCT =================

    // SAVE OR UPDATE PRODUCT
    @PostMapping
    public ResponseEntity<CommonResponse> saveOrUpdateProduct(
            @RequestBody @Valid ProductRequest request
    ) {

        log.info("Save/Update product request received");

        return ResponseEntity.ok(
                productService.saveOrUpdateProduct(request)
        );
    }

    // GET PRODUCT BY ID
    @GetMapping("/{productId}")
    public ResponseEntity<CommonResponse> getProductById(
            @PathVariable Long productId
    ) {

        log.info("Get product by id request received. productId={}", productId);

        return ResponseEntity.ok(
                productService.getProductById(productId)
        );
    }

    // GET ALL PRODUCTS
    @GetMapping
    public ResponseEntity<CommonResponse> getAllProducts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {

        log.info("Get all products request received");

        return ResponseEntity.ok(
                productService.getAllProducts(page, size)
        );
    }

    // DELETE PRODUCT
    @DeleteMapping("/{productId}")
    public ResponseEntity<CommonResponse> deleteProduct(
            @PathVariable Long productId
    ) {

        log.info("Delete product request received. productId={}", productId);

        return ResponseEntity.ok(
                productService.deleteProduct(productId)
        );
    }
}