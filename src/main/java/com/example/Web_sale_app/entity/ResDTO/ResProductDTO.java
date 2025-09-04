package com.example.Web_sale_app.entity.ResDTO;

import com.example.Web_sale_app.entity.Category;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResProductDTO {
    private String name;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private String manufacturer;

    private String imageUrl;

    private String categoryName;
}
