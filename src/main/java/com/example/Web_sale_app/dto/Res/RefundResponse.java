package com.example.Web_sale_app.dto.Res;

import lombok.Data;

@Data
public class RefundResponse {
    String partnerCode;
    String orderId;
    String requestId;
    long amount;
    long transId;
    int resultCode;
    String message;
    long responseTime;
}
