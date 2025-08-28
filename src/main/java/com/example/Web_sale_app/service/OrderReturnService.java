package com.example.Web_sale_app.service;

import com.example.Web_sale_app.entity.OrderItem;
import com.example.Web_sale_app.entity.OrderReturn;
import com.example.Web_sale_app.enums.ReturnStatus;
import org.springframework.stereotype.Service;

import java.util.List;

public interface OrderReturnService {
    OrderReturn createOrderReturnRequest(Long orderId, Long orderItemId, Long customerId, String reason, List<String> evidenceUrls);
    String updateReturnStatus(Long orderReturnId, ReturnStatus status);
    void processRefund(Long orderReturnId);
    void processExchange(Long orderReturnId,OrderItem newOrderItem);

}
