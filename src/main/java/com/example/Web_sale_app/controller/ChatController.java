package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.service.impl.GeminiServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final GeminiServiceImpl geminiServiceImpl;

    public ChatController(GeminiServiceImpl geminiServiceImpl) {
        this.geminiServiceImpl = geminiServiceImpl;
    }

    @GetMapping
    public String chat(@RequestParam String message) {
        return geminiServiceImpl.generateContent(message);
    }
}
