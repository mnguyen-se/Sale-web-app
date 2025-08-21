// com.example.Web_sale_app.dto.OrderSummaryDTO
package com.example.Web_sale_app.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OrderSummaryDTO(
        Long id,
        String status,
        BigDecimal totalAmount,
        OffsetDateTime createdAt
) {}
