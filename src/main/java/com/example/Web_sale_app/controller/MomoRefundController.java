package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.dto.Req.RefundRequest;
import com.example.Web_sale_app.dto.Res.RefundResponse;
import com.example.Web_sale_app.service.MomoRefundService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class MomoRefundController {
    @Autowired
    private MomoRefundService momoRefundService;

    @PostMapping
    public ResponseEntity<RefundResponse> create(@RequestBody @Valid RefundRequest req) {
        RefundResponse resp = momoRefundService.refund(req);
        return ResponseEntity.ok(resp);
    }
}
