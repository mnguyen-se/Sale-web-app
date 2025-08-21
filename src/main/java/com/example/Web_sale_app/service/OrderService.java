package com.example.Web_sale_app.service;

import com.example.Web_sale_app.dto.OrderDetailDTO;
import com.example.Web_sale_app.dto.OrderSummaryDTO;
import org.springframework.data.domain.Page;

public interface OrderService {
    Page<OrderSummaryDTO> listMyOrders(Long userId, int page, int size, String status);
    OrderDetailDTO getMyOrderDetail(Long userId, Long orderId);

    // Guest:
    OrderDetailDTO guestLookup(Long orderId, String email, String phone);
}
