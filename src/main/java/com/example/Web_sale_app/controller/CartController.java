package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.dto.*;
import com.example.Web_sale_app.dto.Req.AddToCartRequest;
import com.example.Web_sale_app.dto.Req.ApplyVoucherRequest;
import com.example.Web_sale_app.dto.Req.UpdateCartItemRequest;
import com.example.Web_sale_app.dto.Res.AddToCartResponse;
import com.example.Web_sale_app.dto.Res.ApplyVoucherResponse;
import com.example.Web_sale_app.service.CartCommandService;
import com.example.Web_sale_app.service.CartQueryService;
import com.example.Web_sale_app.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartCommandService cartCommandService;
    private final CartQueryService cartQueryService;
    private final CartService cartService;

    public CartController(CartCommandService cartCommandService,
                          CartQueryService cartQueryService,
                         CartService cartService) {
                this.cartCommandService = cartCommandService;
                this.cartQueryService = cartQueryService;
               this.cartService = cartService;
           }

    // UC5.1: Xem giỏ
    @GetMapping("/{cartId}")
    public ResponseEntity<CartSummaryDTO> getCart(@PathVariable Long cartId,
                                                  @RequestParam(required = false) String voucher) {
        return ResponseEntity.ok(cartQueryService.getCartSummary(cartId, voucher));
    }

    // UC5.2: Cập nhật số lượng (quantity=0 => xóa)
    @PatchMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<CartSummaryDTO> updateItem(@PathVariable Long cartId,
                                                     @PathVariable Long itemId,
                                                     @RequestBody UpdateCartItemRequest req) {
        try {
            return ResponseEntity.ok(cartCommandService.updateItem(cartId, itemId, req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // UC5.3: Xóa mục hàng
    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<CartSummaryDTO> deleteItem(@PathVariable Long cartId,
                                                     @PathVariable Long itemId) {
        try {
            return ResponseEntity.ok(cartCommandService.deleteItem(cartId, itemId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // UC5.4: Áp mã giảm giá (tạm thời tính tức thời, không lưu)
    @PostMapping("/{cartId}/apply-voucher")
    public ResponseEntity<ApplyVoucherResponse> applyVoucher(@PathVariable Long cartId,
                                                             @RequestBody ApplyVoucherRequest req) {
        return ResponseEntity.ok(cartCommandService.applyVoucher(cartId, req));
    }

    @PostMapping("/add")
    public ResponseEntity<AddToCartResponse> addToCart(@RequestBody @Valid AddToCartRequest req) {
        // req: { cartId?, productId, quantity }
        // BE sẽ:
        // - Nếu cartId == null: tạo giỏ mới, thêm hàng vào, trả về cartId mới
        // - Nếu cartId != null: thêm/cộng dồn vào giỏ hiện có
        AddToCartResponse res = cartService.addItem(req);
        return ResponseEntity.ok(res);
    }



}
