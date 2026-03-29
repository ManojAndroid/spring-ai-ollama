package com.spring.ollama.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class ResumeProcessingService {

    private final VectorStore vectorStore;
    private final ResumeService resumeService;

    public ResumeProcessingService(VectorStore vectorStore, ResumeService resumeService) {
        this.vectorStore = vectorStore;
        this.resumeService = resumeService;
    }

    public void processAndStore(List<MultipartFile> files) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = files.stream()
                    .map(file -> executor.submit(() -> {
                        try {
                            // 1️⃣ Save file in DB
                            Long id = resumeService.save(file);
                            // 2️⃣ Read text
                            String text;
                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                                text = reader.lines().collect(Collectors.joining(" "));
                            }
                            // 3️⃣ Store in Chroma
                            vectorStore.add(List.of(new Document(text, Map.of("resumeId", id.toString(), "fileName", file.getOriginalFilename()))));

                        } catch (Exception e) {
                            throw new RuntimeException("Error processing file: " + file.getOriginalFilename(), e);
                        }
                    })).toList();
            // Wait for completion
            for (var future : futures) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e.getCause());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
