package com.example.Web_sale_app.service;

public interface VNPayService {
    String createPaymentUrl(String orderId, long amount, String bankCode);
}
