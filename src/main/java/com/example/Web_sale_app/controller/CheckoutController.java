package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.dto.CheckoutRequest;
import com.example.Web_sale_app.dto.CheckoutResponse;
import com.example.Web_sale_app.service.CheckoutService;
import com.example.Web_sale_app.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@Tag(name = "Checkout")
@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final RestClient restClient = RestClient.create();
    @Operation
    @PostMapping
    public ResponseEntity<CheckoutResponse> checkout(@RequestBody @Valid CheckoutRequest req) {
        Long userId = SecurityUtils.currentUserIdOrNull(); // null nếu khách vãng lai
        CheckoutResponse res = checkoutService.checkout(req, userId);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/code")
    public ResponseEntity<?> getQRCode(
            @RequestParam long amount
    ) {
        String url = String.format("https://img.vietqr.io/image/MB-0984515950-qr_only.png?amount=%d",
                amount);
        byte[] png = restClient.get().uri(url).retrieve().body(byte[].class);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(png);
    }

}
