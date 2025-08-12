package com.example.Web_sale_app.dto;

import java.math.BigDecimal;

public record CartItemDTO(
        Long itemId,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal price,      // đơn giá hiện tại
        BigDecimal lineTotal   // price * quantity
) {}
