package com.example.Web_sale_app.dto.Req;

import lombok.Data;

@Data
public class RefundRequest {
    long transId;
    String orderId;
    String requestId;
    long amount;
    String description;
    String lang = "en";
}
