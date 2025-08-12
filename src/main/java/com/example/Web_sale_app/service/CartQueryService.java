package com.example.Web_sale_app.service;

import com.example.Web_sale_app.dto.CartSummaryDTO;

public interface CartQueryService {
    CartSummaryDTO getCartSummary(Long cartId, String currentVoucherCodeOrNull);
}
