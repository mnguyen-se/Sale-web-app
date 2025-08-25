// com.example.Web_sale_app.dto.OrderDetailDTO
package com.example.Web_sale_app.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import com.example.Web_sale_app.enums.OrderStatus;

public record OrderDetailDTO(
        Long id,
        OrderStatus status,
        BigDecimal totalAmount,
        OffsetDateTime createdAt,
        String recipientEmail,
        String recipientPhone,
        List<OrderItemLine> items
) {}
