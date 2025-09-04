package com.example.Web_sale_app.entity;

import com.example.Web_sale_app.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "products",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "manufacturer", "category_id"})
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status", nullable = false)
    private ProductStatus status = ProductStatus.DRAFT;

    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold;

    // getters & setters
    public Integer getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(Integer lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }
}
