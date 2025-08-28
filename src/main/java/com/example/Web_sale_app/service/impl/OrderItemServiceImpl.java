package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.entity.OrderItem;
import com.example.Web_sale_app.repository.OrderItemRepository;
import com.example.Web_sale_app.service.OrderItemService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class OrderItemServiceImpl implements OrderItemService {
    private final OrderItemRepository orderItemRepository;
    public OrderItemServiceImpl(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }
    @Override
    public boolean checkStatus(Long orderItemId) {
        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElse(null);
        if (orderItem != null) {
            long dayBetween = ChronoUnit.DAYS.between(orderItem.getCreatedAt(), LocalDateTime.now());
            if(dayBetween < 14) {
                return true;
            }
        }
        return false;
    }

}
