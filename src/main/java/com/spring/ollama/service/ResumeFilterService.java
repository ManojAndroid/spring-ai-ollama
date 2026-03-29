package com.spring.ollama.service;

import com.spring.ollama.dto.CandidateScore;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class ResumeFilterService {
    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public ResumeFilterService(VectorStore vectorStore, ChatClient chatClient) {
        this.vectorStore = vectorStore;
        this.chatClient = chatClient;
    }

    public List<CandidateScore> filterResumes(String jobDescription) {

        if (jobDescription == null || jobDescription.isBlank()) {
            return Collections.emptyList();
        }

        // 🔥 1️⃣ Get top matches from Chroma
        List<Document> docs = vectorStore.similaritySearch(jobDescription);

        List<CandidateScore> resultList = new ArrayList<>();

        for (Document doc : docs) {

            String fileName = (String) doc.getMetadata().get("fileName");
            String resumeId = (String) doc.getMetadata().get("resumeId");
            String content = doc.getText();

            // 🔥 2️⃣ LLM scoring (keep your logic)
            double finalScore;
            try {
                String prompt = """
                        You are an HR assistant.
                        Rate this resume against the following job description on a scale of 0-100:
                        
                        Job Description: %s
                        
                        Resume: %s
                        
                        Return only the numeric score.
                        """.formatted(jobDescription, content);

                String response = chatClient.prompt(prompt).call().content();
                double llmScore = parseScore(response) / 100.0;

                finalScore = llmScore;

            } catch (Exception e) {
                // fallback score if LLM fails
                finalScore = 0.5;
            }

            // clamp
            finalScore = Math.max(0, Math.min(1, finalScore));
            String downloadUrl = "/download/" + resumeId;
            resultList.add(new CandidateScore(fileName, finalScore, downloadUrl));
        }
        // 🔥 3️⃣ Sort & return top 5
        return resultList.stream()
                .sorted(Comparator.comparingDouble(CandidateScore::getScore).reversed())
                .limit(5)
                .toList();
    }

    private double parseScore(String response) {
        try {
            return Double.parseDouble(response.replaceAll("[^0-9.]", ""));
        } catch (Exception e) {
            return 0.0;
        }
    }
}