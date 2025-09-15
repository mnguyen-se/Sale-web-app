package com.example.Web_sale_app.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class HmacSigner {
    private HmacSigner(){}

    public static String sign(String raw, String secretKey) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Cannot sign HMAC", e);
        }
    }

    /** Tạo rawSignature cho /create theo docs MoMo v2 */
    public static String rawCreateSignature(Map<String, String> params) {
        // Phải đúng thứ tự key này
        String[] order = {"accessKey","amount","extraData","ipnUrl","orderId","orderInfo",
                "partnerCode","redirectUrl","requestId","requestType"};
        Map<String,String> ordered = new LinkedHashMap<>();
        for (String k : order) ordered.put(k, params.getOrDefault(k,""));
        return ordered.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
    }

    /** Raw signature cho IPN (notify). Một số trường có thể thiếu; giữ đúng thứ tự dưới đây. */
    public static String rawIpnSignature(Map<String, String> params) {
        String[] order = {"accessKey","amount","extraData","message","orderId","orderInfo",
                "orderType","partnerCode","payType","requestId",
                "responseTime","resultCode","transId"};
        Map<String,String> ordered = new LinkedHashMap<>();
        for (String k : order) if (params.containsKey(k)) ordered.put(k, params.get(k));
        return ordered.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
    }
}
