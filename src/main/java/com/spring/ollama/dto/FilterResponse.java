package com.spring.ollama.dto;

import java.util.List;

public class FilterResponse {
    private List<CandidateScore> candidates;

    public FilterResponse(List<CandidateScore> candidates) {
        this.candidates = candidates;
    }

    public List<CandidateScore> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<CandidateScore> candidates) {
        this.candidates = candidates;
    }
}
