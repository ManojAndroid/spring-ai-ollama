package com.spring.ollama.tools;

import com.spring.ollama.entity.Candidate;
import com.spring.ollama.repository.CandidateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DataTool {
    private final CandidateRepository repository;

    public DataTool(CandidateRepository repository) {
        this.repository = repository;
    }

    @Tool(description = "Return all candidates in the database with full details")
    public String getAllCandidates() {
        log.info("Fetching all candidates from database");

        List<Candidate> list = repository.findAll();

        if (list.isEmpty()) {
            return "No candidates found in the database.";
        }

        // Return nicely formatted string
        return list.stream()
                .map(c -> "ID: " + c.getId() +
                        " | Name: " + c.getName() +
                        " | Email: " + c.getEmail() +
                        " | Skills: " + c.getSkills() +
                        " | Experience: " + c.getExperience() +
                        " yrs" +
                        " | Status: " + c.getStatus())
                .collect(Collectors.joining("\n"));
    }
}
