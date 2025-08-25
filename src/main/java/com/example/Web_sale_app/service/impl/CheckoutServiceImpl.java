// com.example.Web_sale_app.service.impl.CheckoutServiceImpl
package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.dto.Req.CheckoutRequest;
import com.example.Web_sale_app.dto.Res.CheckoutResponse;
import com.example.Web_sale_app.entity.*;
import com.example.Web_sale_app.enums.OrderStatus;
import com.example.Web_sale_app.enums.PaymentMethod;
import com.example.Web_sale_app.repository.*;
import com.example.Web_sale_app.service.CheckoutService;
import com.example.Web_sale_app.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
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
    private final VoucherRepository voucherRepo;
    private final EmailService emailService;

    // Có thể tái sử dụng RestClient (Spring 6+)
    private final RestClient restClient = RestClient.create();

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
            var now = java.time.LocalDateTime.now();
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
            if (discount.compareTo(subtotal) > 0) discount = subtotal;
            appliedVoucher = voucher;
        }

        BigDecimal total = subtotal.subtract(discount);

        // 4) Tạo Order
        Order order = new Order();
        if (userId != null) {
            User u = new User(); u.setId(userId);
            order.setUser(u);
        }
        order.setTotalAmount(total);
        order.setStatus(OrderStatus.PENDING);  // ✅ ĐÚNG - sử dụng enum
        order.setCreatedAt(OffsetDateTime.now());
        order.setPaymentMethod(req.paymentMethod()); // Thêm payment method
        if (appliedVoucher != null) {
            order.setVoucher(appliedVoucher);
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

        // 6) XÓA GIỎ (không dùng status)
        cartItemRepo.deleteByCart_Id(cart.getId());
        cartRepo.delete(cart);

        // 7) Nếu ONLINE: tạo payment URL + QR URL để hiển thị
        String qrImageBase64 = null;
        if (req.paymentMethod() == PaymentMethod.ONLINE) {
            qrImageBase64 = generateQrImageBase64(order.getTotalAmount());
        }

// Gửi email thông báo (không chặn luồng nếu gửi thất bại)
        try {
            // Lấy email người nhận: ưu tiên email trong request, nếu không có thì dùng từ user
            String recipient = req.email();
            if ((recipient == null || recipient.isBlank()) && order.getUser() != null) {
                try {
                    recipient = order.getUser().getEmail();
                } catch (Exception ignored) {}
            }

            if (recipient != null && !recipient.isBlank()) {
                String subject = "[Web Sale] Xác nhận đặt hàng #" + order.getId();

                DecimalFormat formatter = new DecimalFormat("#,###");

                StringBuilder body = new StringBuilder();
                body.append("Xin chào,<br><br>")
                        .append("Đơn hàng của bạn đã được tạo thành công.<br>")
                        .append("Mã đơn: #").append(order.getId()).append("<br>")
                        .append("Trạng thái: ").append(order.getStatus()).append("<br>")
                        .append("Tổng tiền: ").append(formatter.format(order.getTotalAmount())).append(" VND<br><br>")
                        .append("Chi tiết sản phẩm:<br><ul>");

                for (CheckoutResponse.OrderLine line : lines) {
                    String formattedPrice = formatter.format(line.price());
                    body.append("<li>")
                            .append(line.productName()).append(" x").append(line.quantity())
                            .append(" @ ").append(formattedPrice).append(" VND")
                            .append("</li>");
                }
                body.append("</ul>");

                if (req.paymentMethod() == PaymentMethod.ONLINE) {
                    long amount = order.getTotalAmount().setScale(0, RoundingMode.DOWN).longValue();
                    String qrUrl = "https://img.vietqr.io/image/MB-0984515950-qr_only.png?amount=" + amount;

                    body.append("<br>Thanh toán online: Quét mã QR sau để thanh toán:<br>")
                            .append("<img src=\"").append(qrUrl).append("\" alt=\"QR code\" style=\"width:200px;\"/><br>");
                } else {
                    body.append("<br>Phương thức thanh toán: COD (thanh toán khi nhận hàng)<br>");
                }

                body.append("<br>Cảm ơn bạn đã mua sắm tại Web Sale!");

                emailService.sendMail(recipient, subject, body.toString());
            }
        } catch (Exception ignored) {
            // Ghi log nếu cần, không nên bỏ trống nếu dùng trong thực tế
            // Logger.warn("Send email failed for order {}", order.getId(), ignored);
        }
// 8) Trả về DTO đúng format
        return new CheckoutResponse(
                order.getId(),
                total,
                order.getStatus(),  // ✅ ĐÚNG - truyền OrderStatus enum
                lines,
                qrImageBase64
        );
    }

    public String generateQrImageBase64(BigDecimal amount) {
        try {
            String url = "https://img.vietqr.io/image/MB-0984515950-qr_only.png?amount=" + amount;
            byte[] imageBytes = new URL(url).openStream().readAllBytes();
            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            return "data:image/png;base64," + base64;
        } catch (IOException e) {
            return null;
        }
    }

}
