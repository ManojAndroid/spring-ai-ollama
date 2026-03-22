package com.spring.ollama.controller;

import com.spring.ollama.service.ResumeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class DashboardController {

    private final ResumeService service;

    public DashboardController(ResumeService service) {
        this.service = service;
    }

    /*@GetMapping("/")
    public String dashboard() {
        return "dashboard";
    }*/

    @PostMapping("/upload")
    public String upload(@RequestParam("files") List<MultipartFile> files) throws Exception {

        for (MultipartFile file : files) {
            String text = new BufferedReader(new InputStreamReader(file.getInputStream()))
                    .lines().collect(Collectors.joining(" "));
            service.storeResume(file.getOriginalFilename(), text);
        }

        return "redirect:/";
    }

    @PostMapping("/filter")
    public String filter(@RequestParam String jd, Model model) {

        Map<String, Double> results = service.filterResumes(jd);

        model.addAttribute("results", results);

        return "results";
    }
}
