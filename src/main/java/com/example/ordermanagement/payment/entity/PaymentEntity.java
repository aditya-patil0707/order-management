package com.example.ordermanagement.payment.entity;

import com.example.ordermanagement.order.entity.OrderEntity;
import com.example.ordermanagement.payment.enums.PaymentMode;
import com.example.ordermanagement.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payments_seq_gen")
    @SequenceGenerator(
            name = "payments_seq_gen",
            sequenceName = "payments_seq",
            allocationSize = 1
    )
    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private OrderEntity order;

    @Column(name = "payment_number", nullable = false, unique = true)
    private String paymentNumber;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", nullable = false)
    private PaymentMode paymentMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "remark")
    private String remark;

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