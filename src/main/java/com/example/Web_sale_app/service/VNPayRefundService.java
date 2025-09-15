package com.example.Web_sale_app.service;

public interface VNPayRefundService {
    String refund(String txnRef, String amount, String transDate, String createBy, String transactionNo);
}
