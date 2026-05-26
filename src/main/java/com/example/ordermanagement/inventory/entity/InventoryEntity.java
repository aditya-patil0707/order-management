package com.example.ordermanagement.inventory.entity;

import com.example.ordermanagement.product.entity.ProductEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "inventory")
public class InventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inventory_seq_gen")
    @SequenceGenerator(
            name = "inventory_seq_gen",
            sequenceName = "inventory_seq",
            allocationSize = 1
    )
    @Column(name = "inventory_id", nullable = false)
    private Long inventoryId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private ProductEntity product;

    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity = 0;

    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity = 0;

    @OneToMany(mappedBy = "inventory", fetch = FetchType.LAZY)
    private List<InventoryTransactionEntity> transactions;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}