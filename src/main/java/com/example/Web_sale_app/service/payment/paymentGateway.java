package com.example.Web_sale_app.service.payment;

public interface paymentGateway {
    boolean refund(Long orderId, double amount, String paymentMethod);
    boolean charge(Long orderId, double amount, String paymentMethod);
}
