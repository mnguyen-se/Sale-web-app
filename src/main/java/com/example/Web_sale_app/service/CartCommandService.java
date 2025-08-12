package com.example.Web_sale_app.service;

import com.example.Web_sale_app.dto.ApplyVoucherRequest;
import com.example.Web_sale_app.dto.ApplyVoucherResponse;
import com.example.Web_sale_app.dto.CartSummaryDTO;
import com.example.Web_sale_app.dto.UpdateCartItemRequest;

public interface CartCommandService {
    CartSummaryDTO updateItem(Long cartId, Long itemId, UpdateCartItemRequest req);
    CartSummaryDTO deleteItem(Long cartId, Long itemId);
    ApplyVoucherResponse applyVoucher(Long cartId, ApplyVoucherRequest req);
}
