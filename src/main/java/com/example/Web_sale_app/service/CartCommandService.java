package com.example.Web_sale_app.service;

import com.example.Web_sale_app.dto.CartSummaryDTO;
import com.example.Web_sale_app.dto.Req.ApplyVoucherRequest;
import com.example.Web_sale_app.dto.Req.UpdateCartItemRequest;
import com.example.Web_sale_app.dto.Res.ApplyVoucherResponse;

import java.math.BigDecimal;

public interface CartCommandService {
    CartSummaryDTO updateItem(Long cartId, Long itemId, UpdateCartItemRequest req);
    CartSummaryDTO deleteItem(Long cartId, Long itemId);
    ApplyVoucherResponse applyVoucher(Long cartId, ApplyVoucherRequest req);

    interface CartPricingPolicy {
        BigDecimal estimateShipping(BigDecimal subtotal);
        BigDecimal computeDiscount(BigDecimal subtotal, String voucherCode); // tạm tính theo rule
    }
}
