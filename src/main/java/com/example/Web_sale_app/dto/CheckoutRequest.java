package com.example.Web_sale_app.dto;

import com.example.Web_sale_app.enums.PaymentMethod;

// com.example.Web_sale_app.dto.CheckoutRequest
public record CheckoutRequest(
        Long cartId,
        String fullName,
        String phone,
        String email,
        String addressLine1,
        String addressLine2,
        String ward,
        String district,
        String province,
        String postalCode,
        PaymentMethod paymentMethod // COD / ONLINE
) {}

