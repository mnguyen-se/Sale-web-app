// com.example.Web_sale_app.dto.OrderSummaryDTO
package com.example.Web_sale_app.dto;

import com.example.Web_sale_app.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OrderSummaryDTO(
        Long id,
        OrderStatus status,  
        BigDecimal totalAmount,
        OffsetDateTime createdAt
) {}
