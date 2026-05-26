package com.example.ordermanagement.inventory.entity;

import com.example.ordermanagement.inventory.enums.InventoryTransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "inventory_transaction")
public class InventoryTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inventory_transaction_seq_gen")
    @SequenceGenerator(
            name = "inventory_transaction_seq_gen",
            sequenceName = "inventory_transaction_seq",
            allocationSize = 1
    )
    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private InventoryEntity inventory;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private InventoryTransactionType transactionType;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "available_before_quantity", nullable = false)
    private Integer availableBeforeQuantity;

    @Column(name = "available_after_quantity", nullable = false)
    private Integer availableAfterQuantity;

    @Column(name = "reserved_before_quantity", nullable = false)
    private Integer reservedBeforeQuantity;

    @Column(name = "reserved_after_quantity", nullable = false)
    private Integer reservedAfterQuantity;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "remarks", length = 500)
    private String remarks;

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
