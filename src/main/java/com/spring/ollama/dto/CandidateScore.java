package com.spring.ollama.dto;

public class CandidateScore {
    private String fileName;
    private double score;

    public CandidateScore(String fileName, double score) {
        this.fileName = fileName;
        this.score = score;
    }

    public String getFileName() {
        return fileName;
    }

    public double getScore() {
        return score;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
