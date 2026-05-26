package com.example.ordermanagement.product.repository;

import com.example.ordermanagement.product.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findByProductIdAndActiveTrue(Long productId);

    boolean existsBySkuIgnoreCaseAndActiveTrue(String sku);

    Page<ProductEntity> findAllByActiveTrue(Pageable pageable);

}