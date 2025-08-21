// com.example.Web_sale_app.dto.OrderItemLine
package com.example.Web_sale_app.dto;

import java.math.BigDecimal;

public record OrderItemLine(
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal price,
        BigDecimal lineTotal
) {}
