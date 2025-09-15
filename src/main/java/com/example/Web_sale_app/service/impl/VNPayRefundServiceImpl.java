package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.util.HmacSHA512;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayRefundServiceImpl {

    @Value("${vnpay.tmnCode}") private String tmnCode;
    @Value("${vnpay.secretKey}") private String secretKey;
    @Value("${vnpay.refundUrl}") private String refundUrl;

    public String refund(String txnRef, String amount, String transDate, String createBy, String transactionNo) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("vnp_RequestId", String.valueOf(System.currentTimeMillis()));
            params.put("vnp_Version", "2.1.0");
            params.put("vnp_Command", "refund");
            params.put("vnp_TmnCode", tmnCode);
            params.put("vnp_TransactionType", "02"); // 02 = full refund, 03 = partial
            params.put("vnp_TxnRef", txnRef);
            long amountValue = Long.parseLong(amount);
            params.put("vnp_Amount", String.valueOf(amountValue * 100)); // nhân 100
            params.put("vnp_OrderInfo", "Refund order " + txnRef);
            params.put("vnp_TransactionNo", transactionNo); // transaction gốc
            params.put("vnp_TransactionDate", transDate);   // yyyyMMddHHmmss của giao dịch gốc
            params.put("vnp_CreateBy", createBy);
            params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            params.put("vnp_IpAddr", "127.0.0.1");

            // ✅ Build data string: key=value&key=value...
            List<String> fieldNames = new ArrayList<>(params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            for (int i = 0; i < fieldNames.size(); i++) {
                String fieldName = fieldNames.get(i);
                String fieldValue = params.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    hashData.append(fieldName)
                            .append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (i < fieldNames.size() - 1) {
                        hashData.append('&');
                    }
                }
            }

            // ✅ Generate secure hash
            String secureHash = HmacSHA512.sign(hashData.toString(), secretKey);
            params.put("vnp_SecureHash", secureHash);

            System.out.println("🔑 SecureHash: " + secureHash);

            // ✅ Send POST request (JSON)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(refundUrl, HttpMethod.POST, request, String.class);

            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Refund failed", e);
        }
    }
}
