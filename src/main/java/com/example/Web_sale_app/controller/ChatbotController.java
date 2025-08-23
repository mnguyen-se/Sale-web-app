package com.example.Web_sale_app.controller;

import com.example.Web_sale_app.service.ChatbotService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {
    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/chat")
    public String chat(@RequestBody String question) {
        return chatbotService.askChatbot(question);
    }
}
