package com.spring.ollama.controller;

import com.spring.ollama.agent.MultiAgentService;
import com.spring.ollama.dto.ChatRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/chat")
public class ChatControllerv1 {

    private final MultiAgentService agentService;

    public ChatControllerv1(MultiAgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody ChatRequest req) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            String response = agentService.process(username, req.message());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("AI service error: " + e.getMessage());
        }
    }
}
