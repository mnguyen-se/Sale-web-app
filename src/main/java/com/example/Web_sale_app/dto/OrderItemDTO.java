package com.example.Web_sale_app.dto;


import java.math.BigDecimal;

public record OrderItemDTO(
        Long productId,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal lineTotal
) {}
