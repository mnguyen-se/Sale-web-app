package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.dto.CartSummaryDTO;
import com.example.Web_sale_app.dto.Req.ApplyVoucherRequest;
import com.example.Web_sale_app.dto.Req.UpdateCartItemRequest;
import com.example.Web_sale_app.dto.Res.ApplyVoucherResponse;
import com.example.Web_sale_app.entity.CartItem;
import com.example.Web_sale_app.entity.Product;
import com.example.Web_sale_app.repository.CartItemRepository;
import com.example.Web_sale_app.repository.ProductRepository;
import com.example.Web_sale_app.service.CartCommandService;
import com.example.Web_sale_app.service.CartQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartCommandServiceImpl implements CartCommandService {

    private static final int MAX_PER_ITEM = 10;

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartQueryService cartQueryService;

    public CartCommandServiceImpl(CartItemRepository cartItemRepository,
                                  ProductRepository productRepository,
                                  CartQueryService cartQueryService) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.cartQueryService = cartQueryService;
    }

    @Override
    public CartSummaryDTO updateItem(Long cartId, Long itemId, UpdateCartItemRequest req) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item không tồn tại"));

        if (!item.getCart().getId().equals(cartId)) {
            throw new IllegalArgumentException("CartId không khớp với item");
        }

        int newQty = req.quantity() == null ? 0 : Math.max(req.quantity(), 0);

        if (newQty == 0) {
            cartItemRepository.delete(item);
            return cartQueryService.getCartSummary(cartId, null);
        }

        // kiểm tra tồn & giới hạn
        Product p = productRepository.findById(item.getProduct().getId())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));

        int available = p.getStock() != null ? p.getStock() : 0;
        int cappedByMax = Math.min(newQty, MAX_PER_ITEM);
        int finalQty = Math.min(cappedByMax, available);

        if (finalQty <= 0) {
            cartItemRepository.delete(item);
            return cartQueryService.getCartSummary(cartId, null);
        }

        item.setQuantity(finalQty);
        cartItemRepository.save(item);

        return cartQueryService.getCartSummary(cartId, null);
    }

    @Override
    public CartSummaryDTO deleteItem(Long cartId, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item không tồn tại"));

        if (!item.getCart().getId().equals(cartId)) {
            throw new IllegalArgumentException("CartId không khớp với item");
        }

        cartItemRepository.delete(item);
        return cartQueryService.getCartSummary(cartId, null);
    }

    @Override
    public ApplyVoucherResponse applyVoucher(Long cartId, ApplyVoucherRequest req) {
        // Với rule tạm: tính discount “tại thời điểm này”, không lưu DB
        var summaryBefore = cartQueryService.getCartSummary(cartId, req.code());
        boolean applied = summaryBefore.discountAmount().signum() > 0;

        return new ApplyVoucherResponse(
                cartId,
                req.code(),
                applied,
                applied ? "Áp mã thành công" : "Mã không hợp lệ hoặc không đủ điều kiện",
                summaryBefore.discountAmount()
        );
    }
}
