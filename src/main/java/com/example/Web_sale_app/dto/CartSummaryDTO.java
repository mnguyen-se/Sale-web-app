package com.example.Web_sale_app.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartSummaryDTO(
        Long cartId,
        List<CartItemDTO> items,
        BigDecimal subtotal,        // tổng tiền hàng
        BigDecimal shippingFee,     // phí ship dự kiến
        BigDecimal discountAmount,  // giảm giá (nếu có)
        BigDecimal grandTotal,      // tổng phải trả = subtotal + shippingFee - discountAmount
        String appliedVoucher       // mã voucher đã áp (nếu có)
) {}
