package com.example.Web_sale_app.service.payment;

public class MomoGateway implements paymentGateway{
    @Override
    public boolean refund(Long orderId, double amount, String paymentMethod) {
        return false;
    }
    @Override
    public boolean charge(Long orderId, double amount, String paymentMethod) {
        return false;
    }
}
