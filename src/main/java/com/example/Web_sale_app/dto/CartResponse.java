package com.example.Web_sale_app.dto;

import java.util.List;

public record CartResponse(
        Long cartId,
        List<CartItemDTO> items,
        CartSummaryDTO summary,
        boolean adjusted,  // true nếu có điều chỉnh do tồn/giới hạn
        String message     // giải thích (nếu có)
) {}
