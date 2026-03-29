package com.spring.ollama.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateScore {
    private String fileName;
    private double score;
    private String downloadUrl;

    public CandidateScore(String fileName, double score) {
        this.fileName = fileName;
        this.score = score;
    }
}
