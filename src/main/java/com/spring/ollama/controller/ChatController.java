package com.spring.ollama.controller;

import com.spring.ollama.service.MemoryService;
import org.apache.coyote.Response;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ChatController {
    /*private ChatClient chatClient;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @RequestMapping("/chat")
    public ResponseEntity<String> chat(@RequestParam(value = "q") String query) {
       String response=this.chatClient.prompt(query).call().content();
        return  ResponseEntity.ok(response);
    }*/


    private final ChatClient chatClient;
    private final MemoryService memory;
    //private final ChatMemoryRepository memory;

    public ChatController(ChatClient chatClient, MemoryService memory) {
        this.chatClient = chatClient;
        this.memory = memory;
    }

    @GetMapping("/chat")
    public ResponseEntity<String> chat(@RequestParam String q,
                                       @RequestParam(defaultValue = "user1") String user) {

        // Append user message to memory
        memory.remember(user, "User: " + q);

        // Prepare prompt by combining conversation history + current user input
        //String prompt = String.join("\n", memory.getHistory(user)) + "\nUser: " + q;

        // Only take last 10 messages to limit prompt size
        String prompt = String.join("\n", memory.getRecentHistory(user, 10)) + "\nUser: " + q;

        // Call ChatClient and get AI response
        String response = chatClient.prompt(prompt).call().content();

        // Store AI response in memory
        memory.remember(user, "AI: " + response);

        return ResponseEntity.ok(response);
    }

}
