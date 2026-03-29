package com.spring.ollama.dto;
import lombok.Data;

import java.util.List;
@Data
public class ResumeVector {
    private String fileName;
    private String content;          // full resume text
    private List<float[]> embedding;
    private Long id;

    public ResumeVector(String fileName, String content,
                        List<float[]> embedding,
                        Long id) {
        this.fileName = fileName;
        this.content = content;
        this.embedding = embedding;
        this.id=id;

    }
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
