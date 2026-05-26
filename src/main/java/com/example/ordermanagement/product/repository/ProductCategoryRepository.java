package com.example.ordermanagement.product.repository;

import com.example.ordermanagement.product.entity.ProductCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategoryEntity, Long> {

    Optional<ProductCategoryEntity> findByCategoryIdAndActiveTrue(Long categoryId);

    List<ProductCategoryEntity> findAllByActiveTrue();

    boolean existsByCategoryNameIgnoreCaseAndActiveTrue(String categoryName);
}