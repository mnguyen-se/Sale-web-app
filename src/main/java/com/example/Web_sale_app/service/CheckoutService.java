package com.example.Web_sale_app.service;

import com.example.Web_sale_app.dto.CheckoutRequest;
import com.example.Web_sale_app.dto.CheckoutResponse;


public interface CheckoutService {
    CheckoutResponse checkout(CheckoutRequest req, Long userId);
}
