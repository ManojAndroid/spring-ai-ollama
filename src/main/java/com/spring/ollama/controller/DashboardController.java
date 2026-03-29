package com.spring.ollama.controller;

import com.spring.ollama.dto.CandidateScore;
import com.spring.ollama.dto.FilterResponse;
import com.spring.ollama.entity.ResumeEntity;
import com.spring.ollama.service.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping
public class DashboardController {

    private final ResumeProcessingService processingService;
    private final ResumeFilterService filterResumes;
    private final ResumeService resumeService;

    public DashboardController(ResumeFilterService filterResumes,
                               ResumeService resumeService,
                               ResumeProcessingService processingService) {
        this.filterResumes = filterResumes;
        this.resumeService = resumeService;
        this.processingService = processingService;
    }

    // ✅ Upload Resumes
    @PostMapping("/upload")
    public String upload(@RequestParam("files") List<MultipartFile> files) throws IOException {
        processingService.processAndStore(files);
        return "redirect:/";
    }

    // ✅ Filter (Semantic Search)
    @PostMapping("/filter")
    @ResponseBody
    public FilterResponse filter(@RequestBody Map<String, String> request) {
        List<CandidateScore> topCandidates = filterResumes.filterResumes(request.get("jd"));
        return new FilterResponse(topCandidates);
    }

    /* ✅ Download resume */
    @GetMapping("/download/{id}")
    @ResponseBody
    public ResponseEntity<?> download(@PathVariable Long id) {
        // ResumeEntity resume = resumeService.getByFileName(fileName);
        ResumeEntity resume = resumeService.getById(id);
        if (resume == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resume.getFileName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, resume.getContentType())
                .body(new ByteArrayResource(resume.getContent()));
    }
}