package com.spring.ollama.controller;

import com.spring.ollama.dto.ChatRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient chatClient;
    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody ChatRequest req) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String message = req.message();
        String prompt = "User: " + username + "\n" + message;
        try {
            String response = chatClient.prompt(prompt)
                    .user(username)
                    .call()
                    .content();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("AI service error: " + e.getMessage());
        }
    }

}
