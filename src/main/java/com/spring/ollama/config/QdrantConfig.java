package com.spring.ollama.config;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QdrantConfig {

    @Value("${qdrant.host}")
    private String host;

    @Value("${qdrant.port}")
    private int port;

    @Value("${qdrant.api-key:}")
    private String apiKey;

    @Bean
    public QdrantClient qdrantClient() {

        if (apiKey != null && !apiKey.isEmpty()) {
            return new QdrantClient(
                    QdrantGrpcClient.newBuilder(host, port, true)
                            .withApiKey(apiKey)
                            .build()
            );
        }

        return new QdrantClient(
                QdrantGrpcClient.newBuilder(host, port, false).build()
        );
    }
}
