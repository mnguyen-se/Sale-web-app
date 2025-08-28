package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.entity.OrderReturn;
import com.example.Web_sale_app.enums.ReturnStatus;
import com.example.Web_sale_app.repository.OrderReturnRepository;
import com.example.Web_sale_app.service.OrderReturnService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orderReturn")
public class OrderReturnController {
    public final OrderReturnService orderReturnService;
    public final OrderReturnRepository orderReturnRepository;

    public OrderReturnController(OrderReturnService orderReturnService, OrderReturnRepository orderReturnRepository) {
        this.orderReturnService = orderReturnService;
        this.orderReturnRepository = orderReturnRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrderReturn(@RequestParam Long orderId,
                                               @RequestParam Long orderItemId,
                                               @RequestParam Long customerId,
                                               @RequestParam String reason,
                                               @RequestParam List<String> evidenceUrls){
        OrderReturn orderReturn = orderReturnService.createOrderReturnRequest(orderId,orderItemId,customerId,reason,evidenceUrls);
        if(orderReturn != null){
            return ResponseEntity.ok("Order return successfully!");
        }else{
            throw new RuntimeException("Can't create order return!");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateReturnStatus(Long orderReturnId, ReturnStatus status){
        OrderReturn orderReturn = orderReturnRepository.findById(orderReturnId).orElseThrow();
        if(orderReturnId != null){
            orderReturn.setStatus(status);
            orderReturnRepository.save(orderReturn);
            return ResponseEntity.ok("Update order return successfully!");
        }else{
            throw new RuntimeException("Can't update order return!");
        }
    }


}
