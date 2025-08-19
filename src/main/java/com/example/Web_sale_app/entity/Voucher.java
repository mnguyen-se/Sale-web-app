package com.example.Web_sale_app.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "vouchers")
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;              // Ví dụ: SAVE10, FREESHIP
    private String description;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;// Hoặc discountPercentage
    private boolean isPercentage;     // true = %, false = số tiền
    private BigDecimal minimumOrderAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean active;

    // Có thể thêm: số lần sử dụng, người dùng áp dụng, sản phẩm áp dụng,...
}

