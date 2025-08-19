package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.dto.CartItemDTO;
import com.example.Web_sale_app.dto.CartSummaryDTO;
import com.example.Web_sale_app.entity.CartItem;
import com.example.Web_sale_app.repository.CartItemRepository;
import com.example.Web_sale_app.service.CartCommandService;
import com.example.Web_sale_app.service.CartQueryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional(readOnly = true)
public class CartQueryServiceImpl implements CartQueryService {

    private final CartItemRepository cartItemRepository;
    private final CartCommandService.CartPricingPolicy pricing;

    public CartQueryServiceImpl(CartItemRepository cartItemRepository, CartCommandService.CartPricingPolicy pricing) {
        this.cartItemRepository = cartItemRepository;
        this.pricing = pricing;
    }

    @Override
    public CartSummaryDTO getCartSummary(Long cartId, String voucherCode) {
        var items = cartItemRepository.findByCart_Id(cartId).stream()
                .map(this::toDTO)
                .toList();

        BigDecimal subtotal = items.stream()
                .map(CartItemDTO::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = pricing.computeDiscount(subtotal, voucherCode);
        BigDecimal shipping = pricing.estimateShipping(subtotal.subtract(discount));
        BigDecimal grand = subtotal.add(shipping).subtract(discount);

        return new CartSummaryDTO(
                cartId,
                items,
                subtotal,
                shipping,
                discount,
                grand,
                discount.signum() > 0 ? voucherCode : null
        );
    }

    private CartItemDTO toDTO(CartItem ci) {
        var p = ci.getProduct();
        var price = p.getPrice(); // lấy đơn giá hiện tại
        return new CartItemDTO(
                ci.getId(),
                p.getId(),
                p.getName(),
                ci.getQuantity(),
                price,
                price.multiply(BigDecimal.valueOf(ci.getQuantity()))
        );
    }
}
