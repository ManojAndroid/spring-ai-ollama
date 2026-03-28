package com.spring.ollama.service;

import com.spring.ollama.dto.CandidateScore;
import com.spring.ollama.dto.ResumeVector;
import com.spring.ollama.utils.SimilarityUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ResumeFilterService {

    private final ChatClient chatClient;

    public ResumeFilterService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * Filter and score resumes given a job description.
     */
    public List<CandidateScore> filterResumes(String jobDescription, List<ResumeVector> resumes) {

        if (jobDescription == null || jobDescription.isBlank() || resumes.isEmpty()) {
            return Collections.emptyList();
        }

        List<CandidateScore> resultList = new ArrayList<>();
        final int TOP_K = 5;
        final float MIN_SCORE_THRESHOLD = 0.3f;

        for (ResumeVector vector : resumes) {

            // 1️⃣ Cosine similarity scoring
            PriorityQueue<Float> topScores = new PriorityQueue<>(TOP_K);
            for (float[] jdVec : vector.getEmbedding()) {
                for (float[] resumeVec : vector.getEmbedding()) {
                    float score = SimilarityUtil.cosineSimilarity(jdVec, resumeVec);
                    if (!Float.isNaN(score) && score > MIN_SCORE_THRESHOLD) {
                        if (topScores.size() < TOP_K) topScores.offer(score);
                        else if (score > topScores.peek()) {
                            topScores.poll();
                            topScores.offer(score);
                        }
                    }
                }
            }

            if (topScores.isEmpty()) continue;

            float sum = 0f;
            for (float s : topScores) sum += s;
            float avgScore = sum / topScores.size();
            double normalized = (avgScore + 1) / 2;
            double boosted = Math.pow(normalized, 0.5); // embeddings score fallback

            // 2️⃣ Ollama LLM scoring with fallback
            double finalScore = boosted; // default fallback
            try {
                String prompt = """
                        You are an HR assistant.
                        Rate this resume against the following job description on a scale of 0-100:

                        Job Description: %s

                        Resume: %s

                        Return only the numeric score.
                        """.formatted(jobDescription, vector.getTextSnippet());

                String response = chatClient.prompt(prompt).call().content();

                double llmScore = parseScore(response) / 100.0; // normalize 0-1

                // Combine LLM + embeddings
                finalScore = 0.6 * llmScore + 0.4 * boosted;

            } catch (Exception e) {
                // fallback to embeddings score only
                finalScore = boosted;
            }

            // clamp to [0,1]
            finalScore = Math.max(0, Math.min(1, finalScore));

            resultList.add(new CandidateScore(vector.getFileName(), finalScore));
        }

        // 3️⃣ Sort top 5 candidates
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