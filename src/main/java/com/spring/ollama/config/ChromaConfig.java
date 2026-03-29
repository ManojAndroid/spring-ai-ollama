package com.spring.ollama.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chroma.vectorstore.ChromaApi;
import org.springframework.ai.chroma.vectorstore.ChromaVectorStore;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ChromaConfig {

   /* @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {

        RestClient.Builder restClientBuilder = RestClient.builder();
        ObjectMapper objectMapper = new ObjectMapper();

        ChromaApi chromaApi = new ChromaApi(
                "http://localhost:8000",
                restClientBuilder,
                objectMapper
        );

        return ChromaVectorStore.builder(chromaApi, embeddingModel)
                .collectionName("resumes")
                .initializeSchema(true)
                .build();
    }*/

   /* @Bean
    public ChromaVectorStore vectorStore(EmbeddingModel embeddingModel) {
        return ChromaVectorStore.builder(embeddingModel).build();
    }*/

}