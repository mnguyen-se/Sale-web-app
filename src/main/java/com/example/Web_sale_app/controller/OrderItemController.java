package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.repository.UserRepository;
import com.example.Web_sale_app.service.OrderItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orderItem")
public class OrderItemController {
    private final OrderItemService orderItemService;
    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @GetMapping("/canReturnItem")
    public ResponseEntity<?> canReturnItem(@RequestParam Long orderItemId){
        boolean result = orderItemService.checkStatus(orderItemId);
        if(result){
            return ResponseEntity.ok("Can return item successfully!");
        }else{
            throw new RuntimeException("Can't return item!");
        }
    }
}
