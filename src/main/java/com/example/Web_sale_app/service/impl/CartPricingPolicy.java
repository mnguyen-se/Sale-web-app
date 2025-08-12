package com.example.Web_sale_app.service;

import java.math.BigDecimal;

public interface CartPricingPolicy {
    BigDecimal estimateShipping(BigDecimal subtotal);
    BigDecimal computeDiscount(BigDecimal subtotal, String voucherCode); // tạm tính theo rule
}
