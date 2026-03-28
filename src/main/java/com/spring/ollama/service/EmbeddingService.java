package com.spring.ollama.service;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    // ✅ Main method
    public List<float[]> generateEmbeddings(String text) {
        List<String> chunks = splitText(text, 2000);
        return chunks.parallelStream()
                .map(embeddingModel::embed)
                .collect(Collectors.toList());
    }

    // ✅ ADD THIS METHOD (this was missing)
    private List<String> splitText(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();

        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            chunks.add(text.substring(start, end));
            start = end;
        }

        return chunks;
    }
}
