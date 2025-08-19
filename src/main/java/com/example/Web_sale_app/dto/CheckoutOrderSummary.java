package com.example.Web_sale_app.dto;

import java.math.BigDecimal;
import java.util.List;

public record CheckoutOrderSummary(
        Long orderId,
        Long sellerId,
        BigDecimal subtotal,
        BigDecimal shippingFee,
        BigDecimal total,
        String status,
        List<OrderItemDTO> items
) {}
