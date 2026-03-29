/*
package com.spring.ollama.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QdrantConfig {

    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {

        return QdrantVectorStore.builder()
                .host("localhost")
                .port(6333)
                .collectionName("resumes")
                .embeddingModel(embeddingModel)
                .initializeSchema(true)
                .build();
    }
}

*/
