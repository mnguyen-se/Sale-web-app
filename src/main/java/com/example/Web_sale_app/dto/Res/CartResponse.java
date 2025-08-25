package com.example.Web_sale_app.dto.Res;

import java.util.List;

import com.example.Web_sale_app.dto.CartItemDTO;
import com.example.Web_sale_app.dto.CartSummaryDTO;

public record CartResponse(
        Long cartId,
        List<CartItemDTO> items,
        CartSummaryDTO summary,
        boolean adjusted,  // true nếu có điều chỉnh do tồn/giới hạn
        String message     // giải thích (nếu có)
) {}
