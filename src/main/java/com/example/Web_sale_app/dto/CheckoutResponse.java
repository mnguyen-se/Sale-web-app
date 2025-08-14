package com.example.Web_sale_app.dto;

// com.example.Web_sale_app.dto.CheckoutResponse
import java.math.BigDecimal;
import java.util.List;

public record CheckoutResponse(
        Long orderId,
        BigDecimal totalAmount,
        String status,             // ví dụ "PENDING"
        List<OrderLine> items,
        String paymentRedirectUrl  // null nếu COD
) {
    public record OrderLine(Long productId, String productName, int quantity, java.math.BigDecimal price) {}
}
