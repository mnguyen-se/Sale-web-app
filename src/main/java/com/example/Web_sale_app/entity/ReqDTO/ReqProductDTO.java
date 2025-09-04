package com.example.Web_sale_app.entity.ReqDTO;

import com.example.Web_sale_app.entity.Category;
import com.example.Web_sale_app.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqProductDTO {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private String manufacturer;


    @Column(nullable = false)
    private String imageUrl;

    private long categoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status", nullable = false)
    private ProductStatus status = ProductStatus.DRAFT;

    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold;

}
