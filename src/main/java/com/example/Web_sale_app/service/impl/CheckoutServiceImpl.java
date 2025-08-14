package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.dto.CheckoutRequest;
import com.example.Web_sale_app.dto.CheckoutResponse;
import com.example.Web_sale_app.entity.*;
import com.example.Web_sale_app.enums.PaymentMethod;
import com.example.Web_sale_app.repository.*;
import com.example.Web_sale_app.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CheckoutServiceImpl implements CheckoutService {

    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;

    @Override
    public CheckoutResponse checkout(CheckoutRequest req, Long userId) {
        // 1) Nạp giỏ + items
        Cart cart = cartRepo.findById(req.cartId())
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));

        List<CartItem> items = cartItemRepo.findByCart_Id(cart.getId());
        if (items.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        // 2) Kiểm tra tồn kho + tính tổng
        BigDecimal total = BigDecimal.ZERO;
        List<CheckoutResponse.OrderLine> lines = new ArrayList<>();

        for (CartItem ci : items) {
            Product p = productRepo.findById(ci.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + ci.getProduct().getId()));
            if (p.getStock() < ci.getQuantity()) {
                throw new IllegalStateException("Sản phẩm \"" + p.getName() + "\" không đủ tồn kho");
            }
            BigDecimal lineTotal = p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
            total = total.add(lineTotal);
            lines.add(new CheckoutResponse.OrderLine(p.getId(), p.getName(), ci.getQuantity(), p.getPrice()));
        }

        // 3) Tạo Order
        Order order = new Order();
        if (userId != null) {
            User u = new User(); u.setId(userId);
            order.setUser(u);
        }
        order.setTotalAmount(total);
        order.setStatus("PENDING"); // chờ thanh toán/xác nhận tùy luồng
        order.setCreatedAt(OffsetDateTime.now());
        order = orderRepo.save(order);

        // 4) Tạo OrderItem + trừ tồn kho
        for (CartItem ci : items) {
            Product p = productRepo.findById(ci.getProduct().getId()).orElseThrow();
            p.setStock(p.getStock() - ci.getQuantity());
            productRepo.save(p);

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(p);
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(p.getPrice());
            orderItemRepo.save(oi);
        }

        // 5) XÓA GIỎ HẲN (không cần status)
        cartItemRepo.deleteByCart_Id(cart.getId());
        cartRepo.delete(cart);

        // 6) Payment URL (nếu ONLINE)
        String paymentUrl = (req.paymentMethod() == PaymentMethod.ONLINE)
                ? "https://pay.example.com/" + order.getId()
                : null;

        return new CheckoutResponse(order.getId(), order.getTotalAmount(), order.getStatus(), lines, paymentUrl);
    }
}



