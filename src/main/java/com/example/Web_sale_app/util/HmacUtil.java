package com.example.Web_sale_app.util;

import jakarta.xml.bind.DatatypeConverter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class HmacUtil {
    public static String hmacSHA256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256"));
            byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return DatatypeConverter.printHexBinary(hash).toLowerCase();
        } catch(Exception e){ throw new RuntimeException(e); }
    }
}
