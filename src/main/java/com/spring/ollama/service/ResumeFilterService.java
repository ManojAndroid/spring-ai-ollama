package com.spring.ollama.service;

import com.spring.ollama.dto.CandidateScore;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Points;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeFilterService {

    private final ChatClient chatClient;
    private final QdrantClient qdrantClient;
    private final OllamaEmbeddingModel embeddingModel;

    private static final int SEARCH_LIMIT = 200;
    private static final int TOP_RESULTS = 5;
    private static final double LLM_WEIGHT = 0.6;
    private static final double VECTOR_WEIGHT = 0.4;

    public List<CandidateScore> filterResumes(String jobDescription) {

        if (jobDescription == null || jobDescription.isBlank()) {
            log.warn("Job description is empty");
            return Collections.emptyList();
        }

        try {
            // 1️⃣ Convert JD → embedding
            float[] embedding = embeddingModel.embed(List.of(jobDescription)).get(0);
            List<Float> vectorList = toFloatList(embedding);

            // 2️⃣ Search Qdrant
            Points.SearchPoints searchRequest = Points.SearchPoints.newBuilder()
                    .setCollectionName("test")
                    .addAllVector(vectorList)
                    .setLimit(SEARCH_LIMIT)
                    .setWithPayload(Points.WithPayloadSelector.newBuilder().setEnable(true).build())
                    .build();

            List<Points.ScoredPoint> points = qdrantClient.searchAsync(searchRequest).get();
            if (points.isEmpty()) return Collections.emptyList();

            // 3️⃣ Aggregate max vector score per resume
            Map<String, Double> maxScoreMap = new HashMap<>();
            Map<String, String> contentMap = new HashMap<>();

            for (Points.ScoredPoint point : points) {
                var payload = point.getPayloadMap();
                if (!payload.containsKey("fileName") || !payload.containsKey("content")) continue;

                String fileName = payload.get("fileName").getStringValue();
                String content = payload.get("content").getStringValue();

                maxScoreMap.merge(fileName, (double) point.getScore(), Math::max);
                contentMap.putIfAbsent(fileName, content);
            }

            // 4️⃣ Pick top vector candidates
            List<Map.Entry<String, Double>> topVectorCandidates = maxScoreMap.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(TOP_RESULTS)
                    .toList();

            // 5️⃣ Run LLM scoring in parallel using CompletableFuture
            List<CompletableFuture<CandidateScore>> futures = topVectorCandidates.stream()
                    .map(entry -> CompletableFuture.supplyAsync(() -> {
                        String fileName = entry.getKey();
                        double vectorScore = entry.getValue();
                        double llmScore = 0.0;

                        try {
                            String prompt = """
                                    You are an HR assistant.
                                    Rate this resume against the following job description on a scale of 0-100:

                                    Job Description: %s

                                    Resume: %s

                                    Return only the numeric score.
                                    """.formatted(jobDescription, contentMap.get(fileName));

                            String responseText = chatClient.prompt(prompt).call().content();
                            llmScore = parseScore(responseText) / 100.0;

                        } catch (Exception e) {
                            log.warn("LLM scoring failed for file: {}", fileName);
                        }

                        double finalScore = (llmScore > 0) ? LLM_WEIGHT * llmScore + VECTOR_WEIGHT * vectorScore : vectorScore;
                        finalScore = Math.max(0, Math.min(1, finalScore));

                        log.info("File: {}, VectorScore: {}, LLMScore: {}, FinalScore: {}",
                                fileName, vectorScore, llmScore, finalScore);

                        return new CandidateScore(fileName, finalScore);
                    }))
                    .collect(Collectors.toList());

            // 6️⃣ Wait for all LLM scoring to finish
            List<CandidateScore> results = futures.stream()
                    .map(CompletableFuture::join)
                    .sorted(Comparator.comparingDouble(CandidateScore::getScore).reversed())
                    .toList();

            return results;

        } catch (InterruptedException | ExecutionException e) {
            log.error("Error filtering resumes", e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to filter resumes", e);
        } catch (Exception e) {
            log.error("Unexpected error filtering resumes", e);
            throw new RuntimeException("Failed to filter resumes", e);
        }
    }

    private List<Float> toFloatList(float[] embedding) {
        List<Float> list = new ArrayList<>(embedding.length);
        for (float v : embedding) list.add(v);
        return list;
    }

    private double parseScore(String response) {
        try {
            String cleaned = response.replaceAll("[^0-9.]", "");
            if (cleaned.isEmpty()) return 0;
            double score = Double.parseDouble(cleaned);
            return Math.max(0, Math.min(score, 100));
        } catch (Exception e) {
            return 0.0;
        }
    }
}