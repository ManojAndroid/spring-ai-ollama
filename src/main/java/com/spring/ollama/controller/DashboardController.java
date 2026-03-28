package com.spring.ollama.controller;

import com.spring.ollama.dto.CandidateScore;
import com.spring.ollama.dto.FilterResponse;
import com.spring.ollama.dto.ResumeVector;
import com.spring.ollama.service.EmbeddingService;
import com.spring.ollama.service.InMemoryVectorStore;
import com.spring.ollama.service.ResumeFilterService;
import com.spring.ollama.utils.SimilarityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class DashboardController {

    private final EmbeddingService embeddingService;
    private final InMemoryVectorStore vectorStore;
    private final ResumeFilterService filterResumes;

    public DashboardController(EmbeddingService embeddingService,
                               InMemoryVectorStore vectorStore,
                               ResumeFilterService filterResumes) {
        this.embeddingService = embeddingService;
        this.vectorStore = vectorStore;
        this.filterResumes=filterResumes;
    }

  /*  // ✅ Dashboard Page
    @GetMapping("/")
    public String dashboard() {
        return "dashboard";
    }*/

    // ✅ Upload Resumes
    @PostMapping("/upload")
    public String upload(@RequestParam("files") List<MultipartFile> files) {

        for (MultipartFile file : files) {

            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(file.getInputStream()))) {

                String text = reader.lines().collect(Collectors.joining(" "));

                List<float []> embedding = embeddingService.generateEmbeddings(text);

                ResumeVector vector = new ResumeVector(
                        file.getOriginalFilename(),
                        text,
                        embedding
                );

                vectorStore.save(vector);

            } catch (Exception e) {
                throw new RuntimeException("Error processing file", e);
            }
        }

        return "redirect:/";
    }

    // ✅ Filter (Semantic Search)
    @PostMapping("/filter")
    @ResponseBody
    public FilterResponse filter(@RequestBody Map<String, String> request) {

        String jd = request.get("jd");
        List<ResumeVector> allResumes = vectorStore.getAll();

        List<CandidateScore> topCandidates = filterResumes.filterResumes(jd, allResumes);

        return new FilterResponse(topCandidates);
    }
}