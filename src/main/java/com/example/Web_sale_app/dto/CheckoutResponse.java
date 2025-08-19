package com.example.Web_sale_app.dto;

// com.example.Web_sale_app.dto.CheckoutResponse
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

public record CheckoutResponse(
        Long orderId,
        BigDecimal totalAmount,
        String status,
        List<OrderLine> items,
        @Schema(description = "Hình ảnh QR code thanh toán (base64)",
                type = "string",
                format = "byte",
                example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...")
        String qrImageBase64        // ← ví dụ: "/api/payments/{orderId}/qr"
) {
    public record OrderLine(Long productId, String productName, int quantity, java.math.BigDecimal price) {}
}
