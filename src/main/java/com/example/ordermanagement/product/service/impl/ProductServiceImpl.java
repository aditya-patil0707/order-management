package com.example.ordermanagement.product.service.impl;

import com.example.ordermanagement.inventory.service.InventoryService;
import com.example.ordermanagement.product.dto.request.ProductRequest;
import com.example.ordermanagement.product.dto.response.ProductResponse;
import com.example.ordermanagement.product.entity.ProductCategoryEntity;
import com.example.ordermanagement.product.entity.ProductEntity;
import com.example.ordermanagement.product.repository.ProductCategoryRepository;
import com.example.ordermanagement.product.repository.ProductRepository;
import com.example.ordermanagement.product.service.ProductService;
import com.example.ordermanagement.security.SecurityUtil;
import com.example.ordermanagement.util.CommonResponse;
import com.example.ordermanagement.util.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final CommonResponseUtil commonResponseUtil;
    private final InventoryService inventoryService;
    private final SecurityUtil securityUtil;

    // ================= SAVE OR UPDATE =================
    @Override
    public CommonResponse saveOrUpdateProduct(ProductRequest request) {

        log.info("Save/Update Product request received");

        ProductCategoryEntity categoryEntity = categoryRepository
                .findByCategoryIdAndActiveTrue(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Product category not found"));

        ProductEntity productEntity;

        // UPDATE
        if (request.getProductId() != null) {

            productEntity = productRepository
                    .findByProductIdAndActiveTrue(request.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            productEntity.setProductName(request.getProductName());
            productEntity.setDescription(request.getDescription());
            productEntity.setPrice(request.getPrice());
            productEntity.setCategory(categoryEntity);
            productEntity.setUpdatedBy(currentUserId());
            productEntity.setUpdatedDate(LocalDateTime.now());

        }

        // CREATE
        else {

            if (productRepository.existsBySkuIgnoreCaseAndActiveTrue(request.getSku())) {
                throw new RuntimeException("Product SKU already exists");
            }

            productEntity = new ProductEntity();

            productEntity.setProductName(request.getProductName());
            productEntity.setSku(request.getSku());
            productEntity.setDescription(request.getDescription());
            productEntity.setPrice(request.getPrice());
            productEntity.setCategory(categoryEntity);
            productEntity.setActive(true);
            productEntity.setCreatedBy(currentUserId());
            productEntity.setCreatedDate(LocalDateTime.now());
        }

        ProductEntity savedProduct = productRepository.save(productEntity);

        // Create inventory only for new product
        if (request.getProductId() == null) {
            inventoryService.createInventoryForProduct(savedProduct);
        }

        log.info("Product saved successfully");

        return commonResponseUtil.success(
                request.getProductId() == null
                        ? "Product Created Successfully"
                        : "Product Updated Successfully",
                null
        );
    }

    // ================= GET BY ID =================
    @Override
    public CommonResponse getProductById(Long productId) {

        log.info("Fetching product by id {}", productId);

        ProductEntity productEntity = productRepository
                .findByProductIdAndActiveTrue(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductResponse response = mapToResponse(productEntity);

        return commonResponseUtil.success(
                "Product fetched successfully",
                response
        );
    }

    // ================= GET ALL =================
    @Override
    public CommonResponse getAllProducts(Integer page, Integer size) {

        log.info("Get all products request received");

        Pageable pageable = (page == null || size == null)
                ? Pageable.unpaged()
                : PageRequest.of(page, size, Sort.by("productId").ascending());

        Page<ProductEntity> pageResult =
                productRepository.findAllByActiveTrue(pageable);

        List<ProductResponse> responseList = pageResult.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return commonResponseUtil.success(
                "Products fetched successfully",
                responseList,
                page,
                size,
                pageResult.getTotalElements()
        );
    }

    // ================= DELETE =================
    @Override
    public CommonResponse deleteProduct(Long productId) {

        log.info("Deleting product by id {}", productId);

        ProductEntity productEntity = productRepository
                .findByProductIdAndActiveTrue(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productEntity.setActive(false);
        productEntity.setUpdatedBy(currentUserId());
        productEntity.setUpdatedDate(LocalDateTime.now());

        productRepository.save(productEntity);

        return commonResponseUtil.success(
                "Product deleted successfully",
                null
        );
    }

    // ================= MAPPER =================
    private ProductResponse mapToResponse(ProductEntity productEntity) {

        return new ProductResponse(
                productEntity.getProductId(),
                productEntity.getProductName(),
                productEntity.getSku(),
                productEntity.getDescription(),
                productEntity.getPrice(),
                productEntity.getCategory().getCategoryId(),
                productEntity.getCategory().getCategoryName()
        );
    }

    // ================= CURRENT USER =================
    private String currentUserId() {
        return securityUtil.getCurrentUserId();
    }
}