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

import java.time.LocalDateTime; // THÊM nếu chưa import

@Service
@RequiredArgsConstructor
@Transactional
public class CheckoutServiceImpl implements CheckoutService {

    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final ProductRepository productRepo;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final VoucherRepository voucherRepo;

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
        BigDecimal subtotal = BigDecimal.ZERO;
        List<CheckoutResponse.OrderLine> lines = new ArrayList<>();

        for (CartItem ci : items) {
            Product p = productRepo.findById(ci.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + ci.getProduct().getId()));
            if (p.getStock() < ci.getQuantity()) {
                throw new IllegalStateException("Sản phẩm \"" + p.getName() + "\" không đủ tồn kho");
            }
            BigDecimal lineTotal = p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
            subtotal = subtotal.add(lineTotal);
            lines.add(new CheckoutResponse.OrderLine(p.getId(), p.getName(), ci.getQuantity(), p.getPrice()));
        }

        // 3) Áp dụng voucher nếu có
        BigDecimal discount = BigDecimal.ZERO;
        Voucher appliedVoucher = null;

        if (req.voucher() != null && !req.voucher().isBlank()) {
            Voucher voucher = voucherRepo.findByCodeIgnoreCase(req.voucher())
                    .orElseThrow(() -> new IllegalArgumentException("Voucher không tồn tại"));

            if (!voucher.isActive()) {
                throw new IllegalStateException("Voucher không hợp lệ hoặc đã bị vô hiệu hóa");
            }

            LocalDateTime now = LocalDateTime.now();
            if (voucher.getStartDate().isAfter(now) || voucher.getEndDate().isBefore(now)) {
                throw new IllegalStateException("Voucher đã hết hạn sử dụng");
            }

            if (subtotal.compareTo(voucher.getMinimumOrderAmount()) < 0) {
                throw new IllegalStateException("Đơn hàng chưa đủ điều kiện áp dụng voucher");
            }

            if (voucher.isPercentage()) {
                discount = subtotal.multiply(voucher.getDiscountPercentage()).divide(BigDecimal.valueOf(100));
            } else {
                discount = voucher.getDiscountAmount();
            }

            // Giới hạn giảm tối đa không vượt quá tổng
            if (discount.compareTo(subtotal) > 0) {
                discount = subtotal;
            }

            appliedVoucher = voucher;
        }

        BigDecimal total = subtotal.subtract(discount);

        // 4) Tạo Order
        Order order = new Order();
        if (userId != null) {
            User u = new User();
            u.setId(userId);
            order.setUser(u);
        }
        order.setTotalAmount(total);
        order.setStatus("PENDING");
        order.setCreatedAt(OffsetDateTime.now());
        if (appliedVoucher != null) {
            order.setVoucher(appliedVoucher); // cần thêm field voucher trong entity Order
        }
        order = orderRepo.save(order);

        // 5) Tạo OrderItem + trừ tồn kho
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

        // 6) Xóa giỏ
        cartItemRepo.deleteByCart_Id(cart.getId());
        cartRepo.delete(cart);

        // 7) Tạo URL thanh toán nếu cần
        String paymentUrl = (req.paymentMethod() == PaymentMethod.ONLINE)
                ? "https://pay.example.com/" + order.getId()
                : null;

        return new CheckoutResponse(order.getId(), total, order.getStatus(), lines, paymentUrl);
    }
}




