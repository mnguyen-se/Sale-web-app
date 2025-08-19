package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.service.CartCommandService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CartPricingPolicyImpl implements CartCommandService.CartPricingPolicy {
    private static final BigDecimal FREE_SHIP_THRESHOLD = new BigDecimal("500000"); // >=500k miễn ship
    private static final BigDecimal FLAT_SHIP = new BigDecimal("30000");
    @Override
    public BigDecimal estimateShipping(BigDecimal subtotal) {
        if (subtotal == null) return FLAT_SHIP;
        return subtotal.compareTo(FREE_SHIP_THRESHOLD) >= 0 ? BigDecimal.ZERO : FLAT_SHIP;
    }

    @Override
    public BigDecimal computeDiscount(BigDecimal subtotal, String voucherCode) {
        if (voucherCode == null || voucherCode.isBlank()) return BigDecimal.ZERO;
        // RULE TẠM: SAVE10 => giảm 10% tối đa 100k, SAVE50K => giảm 50k cố định
        if ("SAVE10".equalsIgnoreCase(voucherCode)) {
            BigDecimal tenPercent = subtotal.multiply(new BigDecimal("0.10"));
            BigDecimal cap = new BigDecimal("100000");
            return tenPercent.min(cap);
        }
        if ("SAVE50K".equalsIgnoreCase(voucherCode)) {
            return new BigDecimal("50000");
        }
        return BigDecimal.ZERO; // không hợp lệ
    }
}
