package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.dto.Req.RefundRequest;
import com.example.Web_sale_app.dto.Res.RefundResponse;
import com.example.Web_sale_app.util.HmacUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
@Service
public class MomoRefundServiceImpl {
    @Value("${momo.partnerCode}") private String partnerCode;
    @Value("${momo.accessKey}") private String accessKey;
    @Value("${momo.secretKey}") private String secretKey;
    @Value("${momo.refundUrl}") private String refundUrl;

    private RestTemplate rest = new RestTemplate();

    public RefundResponse refund(RefundRequest req) {
        String raw = String.format("accessKey=%s&amount=%d&description=%s&orderId=%s&partnerCode=%s&requestId=%s&transId=%d",
                accessKey, req.getAmount(), req.getDescription(), req.getOrderId(), partnerCode, req.getRequestId(), req.getTransId());

        String signature = HmacUtil.hmacSHA256(raw, secretKey);

        Map<String,Object> body = new HashMap<>();
        body.put("partnerCode", partnerCode);
        body.put("requestId", req.getRequestId());
        body.put("orderId", req.getOrderId());
        body.put("transId", req.getTransId());
        body.put("amount", req.getAmount());
        body.put("description", req.getDescription());
        body.put("lang", req.getLang());
        body.put("signature", signature);

        ResponseEntity<RefundResponse> resp = rest.postForEntity(refundUrl, body, RefundResponse.class);
        return resp.getBody();
    }
}
