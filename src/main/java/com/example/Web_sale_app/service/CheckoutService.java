package com.example.Web_sale_app.service;

import com.example.Web_sale_app.dto.Req.CheckoutRequest;
import com.example.Web_sale_app.dto.Res.CheckoutResponse;

import java.math.BigDecimal;


public interface CheckoutService {
    String generateQrImageBase64(BigDecimal amount);
    CheckoutResponse checkout(CheckoutRequest req, Long userId);
}
