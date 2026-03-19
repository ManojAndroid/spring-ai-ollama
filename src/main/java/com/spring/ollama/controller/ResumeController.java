package com.spring.ollama.controller;
import com.spring.ollama.service.ResumeFilterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/resume")
public class ResumeController {

    private final ResumeFilterService filterService;

    public ResumeController(ResumeFilterService filterService) {
        this.filterService = filterService;
    }

    @PostMapping("/filter")
    public ResponseEntity<Map<String, Double>> filterResumes(
            @RequestParam String jobDescription,
            @RequestParam List<MultipartFile> resumes) throws Exception {

        Map<String, String> resumeTexts = new HashMap<>();

        // Convert uploaded resumes to text
        for (MultipartFile file : resumes) {
            String text = new BufferedReader(new InputStreamReader(file.getInputStream()))
                    .lines().collect(Collectors.joining(" "));
            resumeTexts.put(file.getOriginalFilename(), text);
        }

        // Call ResumeFilterService using Ollama LLM
        Map<String, Double> scores = filterService.scoreResumes(jobDescription, resumeTexts);

        // Sort descending by score
        Map<String, Double> sorted = scores.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return ResponseEntity.ok(sorted);
    }
}
