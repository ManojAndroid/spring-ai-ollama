package com.spring.ollama.service;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Common;
import io.qdrant.client.grpc.JsonWithInt;
import io.qdrant.client.grpc.Points;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeService {

    private final QdrantClient qdrantClient;
    private final OllamaEmbeddingModel embeddingModel;

    private static final int CHUNK_SIZE = 300;
    private static final int BATCH_SIZE = 20;

    public void uploadResumes(List<MultipartFile> files) {

        files.parallelStream().forEach(file -> {
            try {
                String text = extractText(file);
                List<String> chunks = chunkText(text);

                // 🔥 Batch embedding (faster than per-call)
                List<float[]> embeddings = embeddingModel.embed(chunks);

                List<Points.PointStruct> points = new ArrayList<>();

                for (int i = 0; i < chunks.size(); i++) {
                    points.add(buildPoint(file.getOriginalFilename(), chunks.get(i), toFloatList(embeddings.get(i))));
                }

                // 🔥 Batch insert to Qdrant
                for (int i = 0; i < points.size(); i += BATCH_SIZE) {
                    int end = Math.min(i + BATCH_SIZE, points.size());
                    List<Points.PointStruct> batch = points.subList(i, end);
                    log.info("Batch size: " + batch.size());
                    log.info("Vector size: " + batch.get(0).getVectors().getVector().getDataCount());
                    qdrantClient.upsertAsync("test", batch).get();
                }

            } catch (Exception e) {
                log.error("Error processing file: " + file.getOriginalFilename(), e);
                throw new RuntimeException("Error processing file: " + file.getOriginalFilename(), e);
            }
        });
    }

    // 🔥 Build Qdrant point
    private Points.PointStruct buildPoint(String fileName, String chunk, List<Float> vector) {

        long id = Math.abs(UUID.randomUUID().getMostSignificantBits());

        // ✅ DO NOT use Map.of here
        Map<String, JsonWithInt.Value> payload = new HashMap<>();
        payload.put("fileName", toValue(fileName));
        payload.put("content", toValue(chunk));

        return Points.PointStruct.newBuilder()
                .setId(Common.PointId.newBuilder().setNum(id).build())
                .setVectors(Points.Vectors.newBuilder().setVector(
                                Points.Vector.newBuilder().addAllData(vector).build())
                        .build()
                )
                .putAllPayload(payload) // ✅ NOW THIS WORKS
                .build();
    }

    private JsonWithInt.Value toValue(String val) {
        return JsonWithInt.Value.newBuilder()
                .setStringValue(val)
                .build();
    }

    private String extractText(MultipartFile file) throws Exception {
        try (InputStream is = file.getInputStream()) {
            return new String(is.readAllBytes());
        }
    }

    private List<String> chunkText(String text) {

        String[] words = text.split("\\s+");
        List<String> chunks = new ArrayList<>();

        StringBuilder chunk = new StringBuilder();

        for (String word : words) {
            if (chunk.length() + word.length() > CHUNK_SIZE) {
                chunks.add(chunk.toString());
                chunk = new StringBuilder();
            }
            chunk.append(word).append(" ");
        }

        if (!chunk.isEmpty()) {
            chunks.add(chunk.toString());
        }

        return chunks;
    }

    private List<Float> toFloatList(float[] embedding) {
        List<Float> list = new ArrayList<>(embedding.length);
        for (float v : embedding) {
            list.add(v);
        }
        return list;
    }
}