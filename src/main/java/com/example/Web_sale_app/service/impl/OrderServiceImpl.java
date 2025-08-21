// com.example.Web_sale_app.service.impl.OrderServiceImpl
package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.dto.*;
import com.example.Web_sale_app.entity.Order;
import com.example.Web_sale_app.entity.OrderItem;
import com.example.Web_sale_app.repository.OrderItemRepository;
import com.example.Web_sale_app.repository.OrderRepository;
import com.example.Web_sale_app.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;

    @Override
    public Page<OrderSummaryDTO> listMyOrders(Long userId, int page, int size, String status) {
        Pageable pageable = PageRequest.of(Math.max(page,0), Math.min(Math.max(size,1), 100));
        Page<Order> pageData = (status == null || status.isBlank())
                ? orderRepo.findByUser_IdOrderByCreatedAtDesc(userId, pageable)
                : orderRepo.findByUser_IdAndStatusOrderByCreatedAtDesc(userId, status, pageable);

        return pageData.map(o -> new OrderSummaryDTO(
                o.getId(),
                o.getStatus(),
                o.getTotalAmount(),
                o.getCreatedAt()
        ));
    }

    @Override
    public OrderDetailDTO getMyOrderDetail(Long userId, Long orderId) {
        Order o = orderRepo.findByIdAndUser_Id(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hoặc bạn không có quyền"));

        List<OrderItem> items = orderItemRepo.findByOrder_Id(o.getId());
        List<OrderItemLine> lines = items.stream().map(oi ->
                new OrderItemLine(
                        oi.getProduct().getId(),
                        oi.getProduct().getName(),
                        oi.getQuantity(),
                        oi.getPrice(),
                        oi.getPrice().multiply(java.math.BigDecimal.valueOf(oi.getQuantity()))
                )
        ).toList();

        return new OrderDetailDTO(
                o.getId(),
                o.getStatus(),
                o.getTotalAmount(),
                o.getCreatedAt(),
                o.getRecipientEmail(),
                o.getRecipientPhone(),
                lines
        );
    }

    @Override
    public OrderDetailDTO guestLookup(Long orderId, String email, String phone) {
        if ((email == null || email.isBlank()) && (phone == null || phone.isBlank())) {
            throw new IllegalArgumentException("Cần cung cấp email hoặc số điện thoại để tra cứu vãng lai");
        }

        Order o = (email != null && !email.isBlank())
                ? orderRepo.findByIdAndRecipientEmailIgnoreCase(orderId, email)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn theo email"))
                : orderRepo.findByIdAndRecipientPhone(orderId, phone)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn theo số điện thoại"));

        List<OrderItem> items = orderItemRepo.findByOrder_Id(o.getId());
        List<OrderItemLine> lines = items.stream().map(oi ->
                new OrderItemLine(
                        oi.getProduct().getId(),
                        oi.getProduct().getName(),
                        oi.getQuantity(),
                        oi.getPrice(),
                        oi.getPrice().multiply(java.math.BigDecimal.valueOf(oi.getQuantity()))
                )
        ).toList();

        return new OrderDetailDTO(
                o.getId(),
                o.getStatus(),
                o.getTotalAmount(),
                o.getCreatedAt(),
                o.getRecipientEmail(),
                o.getRecipientPhone(),
                lines
        );
    }
}
