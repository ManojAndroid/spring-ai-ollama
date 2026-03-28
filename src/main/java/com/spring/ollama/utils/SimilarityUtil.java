package com.spring.ollama.utils;
import java.util.List;

public class SimilarityUtil {

    public static float cosineSimilarity(float[] a, float[] b) {

        float dot = 0.0f;
        float normA = 0.0f;
        float normB = 0.0f;

        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        return (float) (dot / (Math.sqrt(normA) * Math.sqrt(normB)));
    }
}
