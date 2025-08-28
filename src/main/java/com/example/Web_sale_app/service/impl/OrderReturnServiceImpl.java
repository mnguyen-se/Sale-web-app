package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.entity.Order;
import com.example.Web_sale_app.entity.OrderItem;
import com.example.Web_sale_app.entity.OrderReturn;
import com.example.Web_sale_app.enums.OrderStatus;
import com.example.Web_sale_app.enums.ReturnStatus;
import com.example.Web_sale_app.repository.OrderItemRepository;
import com.example.Web_sale_app.repository.OrderRepository;
import com.example.Web_sale_app.repository.OrderReturnRepository;
import com.example.Web_sale_app.service.OrderReturnService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class OrderReturnServiceImpl implements OrderReturnService {
    public final OrderReturnRepository orderReturnRepository;
    public final OrderRepository orderRepository;
    public final OrderItemRepository orderItemRepository;
    public OrderReturnServiceImpl(OrderReturnRepository orderReturnRepository, OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderReturnRepository = orderReturnRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }


    @Override
    public OrderReturn createOrderReturnRequest(Long orderId, Long orderItemId, Long customerId, String reason, List<String> evidenceUrls) {

        OrderReturn orderReturn = new OrderReturn();
        orderReturn.setEvidenceUrls(evidenceUrls);
        orderReturn.setCreatedAt(LocalDateTime.now());
        orderReturn.setOrderItemId(orderItemId);
        orderReturn.setOrderId(orderId);
        orderReturn.setCustomerId(customerId);

        return orderReturnRepository.save(orderReturn);

    }

    @Override
    public String updateReturnStatus(Long orderReturnId, ReturnStatus status) {
        OrderReturn orderReturn = orderReturnRepository.getReferenceById(orderReturnId);
        orderReturn.setStatus(status);
        orderReturnRepository.save(orderReturn);
        return "Đã cập nhật trạng thái trả lại đơn hàng thành : " + status.toString();
    }

    @Override
    public void processRefund(Long orderReturnId) {
        OrderReturn orderReturn = orderReturnRepository.getReferenceById(orderReturnId);
        if(orderReturn.getStatus() != ReturnStatus.RETURNED){
            throw new IllegalStateException("Yêu cầu không đủ điều kiện hoàn tiền!!");
        }
        OrderItem orderItem = orderItemRepository.findById(orderReturn.getOrderItemId()).orElseThrow();
        BigDecimal refundAmount = orderItem.getPrice();

    }

    @Override
    public void processExchange(Long orderReturnId, OrderItem newOrderItem) {

    }
}
