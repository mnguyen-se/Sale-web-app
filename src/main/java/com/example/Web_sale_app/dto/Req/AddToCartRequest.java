package com.example.Web_sale_app.dto.Req;

public record AddToCartRequest(
        Long cartId,       // null nếu khách vãng lai chưa có giỏ
        Long productId,
        Integer quantity
) {}
