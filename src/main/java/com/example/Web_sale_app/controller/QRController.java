package com.example.Web_sale_app.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/qr")
public class QRController {
    private final RestClient restClient = RestClient.create();

    @GetMapping("/code")
    public ResponseEntity<?> getQRCode(
            @RequestParam long amount
    ) {
        String url = String.format("https://img.vietqr.io/image/MB-0984515950-qr_only.png?amount=%d",
                amount);
        byte[] png = restClient.get().uri(url).retrieve().body(byte[].class);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(png);
    }


}
