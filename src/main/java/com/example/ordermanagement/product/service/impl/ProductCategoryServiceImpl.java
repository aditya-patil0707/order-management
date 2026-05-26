package com.example.ordermanagement.product.service.impl;

import com.example.ordermanagement.product.dto.request.ProductCategoryRequest;
import com.example.ordermanagement.product.dto.response.ProductCategoryResponse;
import com.example.ordermanagement.product.entity.ProductCategoryEntity;
import com.example.ordermanagement.product.repository.ProductCategoryRepository;
import com.example.ordermanagement.product.repository.ProductRepository;
import com.example.ordermanagement.product.service.ProductCategoryService;
import com.example.ordermanagement.security.SecurityUtil;
import com.example.ordermanagement.util.CommonResponse;
import com.example.ordermanagement.util.CommonResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CommonResponseUtil commonResponseUtil;
    private final SecurityUtil securityUtil;

    @Override
    public CommonResponse saveOrUpdateCategory(ProductCategoryRequest request) {

        log.info("Save/Update Product Category request received");

        ProductCategoryEntity categoryEntity;

        if (request.getCategoryId() != null) {

            categoryEntity = categoryRepository.findByCategoryIdAndActiveTrue(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Product category not found"));

            categoryEntity.setCategoryName(request.getCategoryName());
            categoryEntity.setDescription(request.getDescription());
            categoryEntity.setUpdatedBy(currentUserId());
            categoryEntity.setUpdatedDate(LocalDateTime.now());

        } else {

            if (categoryRepository.existsByCategoryNameIgnoreCaseAndActiveTrue(request.getCategoryName())) {
                throw new RuntimeException("Product category already exists");
            }

            categoryEntity = new ProductCategoryEntity();
            categoryEntity.setCategoryName(request.getCategoryName());
            categoryEntity.setDescription(request.getDescription());
            categoryEntity.setActive(true);
            categoryEntity.setCreatedBy(currentUserId());
            categoryEntity.setCreatedDate(LocalDateTime.now());
        }

        categoryRepository.save(categoryEntity);

        log.info("Product Category saved successfully");

        return commonResponseUtil.success(
                request.getCategoryId() == null
                        ? "Product Category Created Successfully"
                        : "Product Category Updated Successfully",
                null
        );
    }

    @Override
    public CommonResponse getAllCategories() {

        log.info("Fetching all product categories");

        List<ProductCategoryEntity> categoryList =
                categoryRepository.findAllByActiveTrue();

        List<ProductCategoryResponse> responseList = categoryList
                .stream()
                .map(categoryEntity -> new ProductCategoryResponse(
                        categoryEntity.getCategoryId(),
                        categoryEntity.getCategoryName(),
                        categoryEntity.getDescription()
                ))
                .toList();

        return commonResponseUtil.success(
                "Product Categories fetched successfully",
                responseList
        );
    }

    @Override
    public CommonResponse getCategoryById(Long categoryId) {

        log.info("Fetching product category by id {}", categoryId);

        ProductCategoryEntity categoryEntity = categoryRepository.findByCategoryIdAndActiveTrue(categoryId)
                .orElseThrow(() -> new RuntimeException("Product category not found"));

        ProductCategoryResponse response = new ProductCategoryResponse(
                categoryEntity.getCategoryId(),
                categoryEntity.getCategoryName(),
                categoryEntity.getDescription()
        );

        return commonResponseUtil.success("Product Category fetched successfully", response);
    }

    private String currentUserId() {
        return securityUtil.getCurrentUserId();
    }
}