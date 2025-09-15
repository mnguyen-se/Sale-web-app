package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.service.impl.VNPayRefundServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class VNPayRefundController {
    private final VNPayRefundServiceImpl refundService;

    @PostMapping("/refund")
    public ResponseEntity<?> refund(
            @RequestParam String txnRef,
            @RequestParam String amount,
            @RequestParam String transDate,
            @RequestParam String createBy,
            @RequestParam String transactionNo) {
        String result = refundService.refund(txnRef, amount, transDate, createBy, transactionNo);
        return ResponseEntity.ok(result);


    }

    @GetMapping("/refund")
    public String refundTest() {
        // ví dụ giả sử txnRef = "123456", amount = "10000", transDate = "20250904120000", transactionNo = "123456789"
        return refundService.refund("123456", "10000", "20250904120000", "Admin", "123456789");
    }
}
