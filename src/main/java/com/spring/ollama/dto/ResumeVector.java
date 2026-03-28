package com.spring.ollama.dto;
import java.util.List;

public class ResumeVector {
    private String fileName;
    private String content;          // full resume text
    private List<float[]> embedding;

    public ResumeVector(String fileName, String content, List<float[]> embedding) {
        this.fileName = fileName;
        this.content = content;
        this.embedding = embedding;
    }

    public String getFileName() { return fileName; }
    public String getContent() { return content; }
    public List<float[]> getEmbedding() { return embedding; }

    /**
     * Returns a short snippet of the resume for LLM scoring
     */
    public String getTextSnippet() {
        if (content == null || content.isBlank()) return "";
        int snippetLength = 500; // take first 500 characters
        return content.length() <= snippetLength
                ? content
                : content.substring(0, snippetLength) + "...";
    }
}
