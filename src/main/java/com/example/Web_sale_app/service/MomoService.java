package com.example.Web_sale_app.service;

import net.minidev.json.JSONObject;

public interface MomoService {
    String createPayment(String orderId, String amount) throws Exception;

}
