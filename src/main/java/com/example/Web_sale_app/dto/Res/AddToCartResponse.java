package com.example.Web_sale_app.dto.Res;

import java.util.List;

import com.example.Web_sale_app.dto.CartItemDTO;

public record AddToCartResponse(
        Long cartId,
        boolean adjusted,   // true nếu hệ thống đã điều chỉnh số lượng
        String message,     // giải thích lý do điều chỉnh (nếu có)
        List<CartItemDTO> items
) {}
