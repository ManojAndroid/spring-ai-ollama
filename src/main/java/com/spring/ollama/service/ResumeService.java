package com.spring.ollama.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ResumeService {

    private final ChatClient chatClient;

    private final Map<String, String> resumes = new HashMap<>();

    public ResumeService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public void storeResume(String name, String content) {
        resumes.put(name, content);
    }

    public Map<String, Double> filterResumes(String jd) {

        StringBuilder sb = new StringBuilder();

        for (var entry : resumes.entrySet()) {
            sb.append("Resume: ").append(entry.getKey())
                    .append("\n")
                    .append(entry.getValue())
                    .append("\n\n");
        }

        String prompt = """
                You are an HR system.
                Score these resumes (0-100).
                
                Job Description:
                %s
                
                Resumes:
                %s
                
                Return JSON:
                { "resume1.pdf": 90 }
                """.formatted(jd, sb.toString());

        String response = chatClient.prompt(prompt).call().content();

        return parse(response);
    }

    private Map<String, Double> parse(String response) {
        Map<String, Double> map = new HashMap<>();
        response = response.replaceAll("[{}\"]", "");
        String[] entries = response.split(",");

        for (String e : entries) {
            String[] kv = e.split(":");
            if (kv.length == 2) {
                map.put(kv[0].trim(), Double.parseDouble(kv[1].trim()));
            }
        }
        return map;
    }
}
