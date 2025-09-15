package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.service.VNPayService;
import com.example.Web_sale_app.util.VNPayUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayServiceImpl implements VNPayService {

    @Value("${vnpay.tmnCode}")
    private String tmnCode;

    @Value("${vnpay.secretKey}")
    private String secretKey;

    @Value("${vnpay.payUrl}")
    private String payUrl;

    @Value("${vnpay.returnUrl}")
    private String returnUrl;

    @Override
    public String createPaymentUrl(String orderId, long amount, String bankCode) {
        try {
            Map<String, String> vnpParams = new HashMap<>();
            vnpParams.put("vnp_Version", "2.1.0");
            vnpParams.put("vnp_Command", "pay");
            vnpParams.put("vnp_TmnCode", tmnCode);
            vnpParams.put("vnp_Amount", String.valueOf(amount * 100)); // nhân 100
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_TxnRef", orderId);
            vnpParams.put("vnp_OrderInfo", "Thanh toan don hang " + orderId);
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_ReturnUrl", returnUrl);
            vnpParams.put("vnp_IpAddr", "127.0.0.1");

            // Thời gian tạo và hết hạn
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String createDate = formatter.format(cal.getTime());
            vnpParams.put("vnp_CreateDate", createDate);

            cal.add(Calendar.MINUTE, 15);
            String expireDate = formatter.format(cal.getTime());
            vnpParams.put("vnp_ExpireDate", expireDate);

            // Thêm bankCode nếu có
            if (bankCode != null && !bankCode.isEmpty()) {
                vnpParams.put("vnp_BankCode", bankCode);
            }

            // Sort params theo alphabet
            List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (String name : fieldNames) {
                String value = vnpParams.get(name);
                if (hashData.length() > 0) {
                    hashData.append('&');
                    query.append('&');
                }
                // hashData giữ nguyên (raw)
                hashData.append(name).append('=').append(value);
                // query phải encode
                query.append(URLEncoder.encode(name, StandardCharsets.UTF_8))
                        .append('=')
                        .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
            }

            String secureHash = VNPayUtil.hmacSHA512(secretKey, hashData.toString());
            query.append("&vnp_SecureHash=").append(secureHash);

            System.out.println("HashData: " + hashData);
            System.out.println("SecureHash: " + secureHash);

            return payUrl + "?" + query;

        } catch (Exception e) {
            throw new RuntimeException("Error creating payment url", e);
        }
    }
}
