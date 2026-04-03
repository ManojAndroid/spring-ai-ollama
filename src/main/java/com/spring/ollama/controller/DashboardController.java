package com.spring.ollama.controller;

import com.spring.ollama.dto.CandidateScore;
import com.spring.ollama.dto.FilterResponse;
import com.spring.ollama.dto.ResumeVector;
import com.spring.ollama.service.EmbeddingService;
import com.spring.ollama.service.InMemoryVectorStore;
import com.spring.ollama.service.ResumeFilterService;
import com.spring.ollama.service.ResumeService;
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
    private final ResumeService resumeService;

    public DashboardController(EmbeddingService embeddingService,
                               InMemoryVectorStore vectorStore,
                               ResumeFilterService filterResumes, ResumeService resumeService) {
        this.embeddingService = embeddingService;
        this.vectorStore = vectorStore;
        this.filterResumes = filterResumes;
        this.resumeService = resumeService;
    }
    // ✅ Upload Resumes
    @PostMapping("/upload")
    public String upload(@RequestParam("files") List<MultipartFile> files) {
        resumeService.uploadResumes(files);
        return "redirect:/";
    }

    // ✅ Filter (Semantic Search)
    @PostMapping("/filter")
    @ResponseBody
    public FilterResponse filter(@RequestBody Map<String, String> request) {
        List<CandidateScore> topCandidates = filterResumes.filterResumes(request.get("jd"));
        return new FilterResponse(topCandidates);
    }
}