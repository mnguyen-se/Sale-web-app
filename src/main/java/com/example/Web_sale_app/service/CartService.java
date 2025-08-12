package com.example.Web_sale_app.service;

import com.example.Web_sale_app.dto.AddToCartRequest;
import com.example.Web_sale_app.dto.AddToCartResponse;

public interface CartService {
    AddToCartResponse addItem(AddToCartRequest req);
}
