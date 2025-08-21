// com.example.Web_sale_app.controller.OrderController
package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.dto.*;
import com.example.Web_sale_app.service.OrderService;
import com.example.Web_sale_app.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Orders")
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/me")
    public ResponseEntity<Page<OrderSummaryDTO>> listMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status
    ) {
        Long userId = SecurityUtils.currentUserIdOrNull();
        if (userId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(orderService.listMyOrders(userId, page, size, status));
    }

    @GetMapping("/me/{orderId}")
    public ResponseEntity<OrderDetailDTO> myOrderDetail(@PathVariable Long orderId) {
        Long userId = SecurityUtils.currentUserIdOrNull();
        if (userId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(orderService.getMyOrderDetail(userId, orderId));
    }

    @PostMapping("/guest/lookup")
    public ResponseEntity<OrderDetailDTO> guestLookup(@RequestBody GuestOrderLookupRequest req) {
        return ResponseEntity.ok(orderService.guestLookup(req.orderId(), req.email(), req.phone()));
    }
}
