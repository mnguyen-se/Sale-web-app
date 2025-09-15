package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.service.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class VNPayController {

    @Autowired
    private VNPayService vnPayService;

    @GetMapping("/create")
    public String createPayment(@RequestParam String orderId,
                                @RequestParam long amount,
                                @RequestParam(required = false) String bankCode) {
        return vnPayService.createPaymentUrl(orderId, amount, bankCode);
    }

    @GetMapping("/vnpay-return")
    public String vnpayReturn(@RequestParam Map<String, String> params) {
        // TODO: verify checksum tại đây
        return "Thanh toán thành công với dữ liệu: " + params.toString();
    }
}
