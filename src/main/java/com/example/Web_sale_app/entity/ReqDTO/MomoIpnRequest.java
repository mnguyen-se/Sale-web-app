package com.example.Web_sale_app.entity.ReqDTO;

public class MomoIpnRequest {
    public String partnerCode;
    public String orderId;
    public String requestId;
    public long amount;
    public String orderInfo;
    public String orderType;
    public long transId;
    public int resultCode;      // 0 = success
    public String message;
    public String payType;
    public long responseTime;
    public String extraData;
    public String signature;
}
