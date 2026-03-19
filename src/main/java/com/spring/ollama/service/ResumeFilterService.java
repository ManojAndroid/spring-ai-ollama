package com.spring.ollama.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ResumeFilterService {

    private final ChatClient chatClient;

    public ResumeFilterService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * Returns a map of resume filename -> score (0-100)
     */
    public Map<String, Double> scoreResumes(String jobDescription, Map<String, String> resumes) {
        Map<String, Double> scores = new HashMap<>();

        for (Map.Entry<String, String> entry : resumes.entrySet()) {
            String resumeName = entry.getKey();
            String resumeText = entry.getValue();

            // Build prompt for LLM
            String prompt = """
                    You are an HR assistant.
                    Rate this resume against the following job description on a scale of 0-100:
                    
                    Job Description: %s
                    
                    Resume: %s
                    
                    Return only the numeric score.
                    """.formatted(jobDescription, resumeText);

            // Call Ollama via Spring AI
            String response = chatClient.prompt(prompt).call().content();

            // Parse numeric score
            double score = parseScore(response);
            scores.put(resumeName, score);
        }

        return scores;
    }

    private double parseScore(String response) {
        try {
            return Double.parseDouble(response.replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            return 0.0;
        }
    }
}
