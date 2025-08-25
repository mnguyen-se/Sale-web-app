package com.example.Web_sale_app.service;

import com.example.Web_sale_app.dto.Req.AddToCartRequest;
import com.example.Web_sale_app.dto.Res.AddToCartResponse;

public interface CartService {
    AddToCartResponse addItem(AddToCartRequest req);
}
