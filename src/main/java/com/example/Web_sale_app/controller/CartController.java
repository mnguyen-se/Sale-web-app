package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.dto.AddToCartRequest;
import com.example.Web_sale_app.dto.AddToCartResponse;
import com.example.Web_sale_app.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/items")
    public ResponseEntity<AddToCartResponse> addItem(@RequestBody AddToCartRequest req) {
        try {
            AddToCartResponse res = cartService.addItem(req);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // hoặc trả message chi tiết
        } catch (IllegalStateException e) {
            // Hết hàng, lỗi tồn kho...
            return ResponseEntity.status(409).body(
                    new AddToCartResponse(
                            req.cartId(),
                            true,
                            e.getMessage(),
                            java.util.List.of()
                    )
            );
        }
    }
}
