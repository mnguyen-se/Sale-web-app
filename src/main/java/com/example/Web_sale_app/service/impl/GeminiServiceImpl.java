package com.example.Web_sale_app.service.impl;

import com.example.Web_sale_app.service.GeminiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class GeminiServiceImpl implements GeminiService {
    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=";
    @Override
    public String generateContent(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        // Request body
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        Map<String, Object> item = new HashMap<>();
        item.put("parts", Collections.singletonList(part));

        content.put("contents", Collections.singletonList(item));

        // Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(content, headers);

        // Call API
        ResponseEntity<Map> response = restTemplate.exchange(
                GEMINI_URL + apiKey,
                HttpMethod.POST,
                entity,
                Map.class
        );

        // Parse response
        if (response.getBody() != null) {
            try {
                List candidates = (List) response.getBody().get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map firstCandidate = (Map) candidates.get(0);
                    Map contentMap = (Map) firstCandidate.get("content");
                    List parts = (List) contentMap.get("parts");
                    Map firstPart = (Map) parts.get(0);
                    return firstPart.get("text").toString();
                }
            } catch (Exception e) {
                return "Error parsing response: " + e.getMessage();
            }
        }
        return "No response from Gemini API";
    }
}
