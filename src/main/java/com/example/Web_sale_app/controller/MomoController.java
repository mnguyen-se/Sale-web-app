package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.service.impl.MomoServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/momo")
public class MomoController {

    private final MomoServiceImpl momoService;

    public MomoController(MomoServiceImpl momoService) {
        this.momoService = momoService;
    }

    @PostMapping("/create")
    public String createPayment(@RequestParam String orderId, @RequestParam String amount) throws Exception {
        return momoService.createPayment(orderId, amount);
    }
}
