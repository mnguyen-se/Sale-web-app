package com.example.Web_sale_app.entity.ReqDTO;


public class MomoCreateRequest {
    public String partnerCode;
    public String accessKey;
    public String requestId;
    public String amount;
    public String orderId;
    public String orderInfo;
    public String redirectUrl;
    public String ipnUrl;        // notifyUrl
    public String extraData;     // có thể để rỗng
    public String requestType;   // captureWallet
    public String signature;
    public String lang;          // vi | en
}

