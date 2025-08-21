package com.example.Web_sale_app.dto;

import java.math.BigDecimal;

public record ProductDTO(
        Long id,
        String name,
        String imageUrl,
        String description,
        BigDecimal price,
        Integer stock,
        String categoryName,
        String manufacturer
) {}
