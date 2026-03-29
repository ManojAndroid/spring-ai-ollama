package com.spring.ollama.service;

import com.spring.ollama.entity.ResumeEntity;
import com.spring.ollama.repository.ResumeRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class ResumeService {

    private final ChatClient chatClient;

    @Autowired
    private ResumeRepository repository;

    private final Map<String, String> resumes = new HashMap<>();

    public ResumeService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public void storeResume(String name, String content) {
        resumes.put(name, content);
    }

    public Map<String, Double> filterResumes(String jd) {

        StringBuilder sb = new StringBuilder();

        for (var entry : resumes.entrySet()) {
            sb.append("Resume: ").append(entry.getKey())
                    .append("\n")
                    .append(entry.getValue())
                    .append("\n\n");
        }

        String prompt = """
                You are an HR system.
                Score these resumes (0-100).
                
                Job Description:
                %s
                
                Resumes:
                %s
                
                Return JSON:
                { "resume1.pdf": 90 }
                """.formatted(jd, sb.toString());

        String response = chatClient.prompt(prompt).call().content();

        return parse(response);
    }

    private Map<String, Double> parse(String response) {
        Map<String, Double> map = new HashMap<>();
        response = response.replaceAll("[{}\"]", "");
        String[] entries = response.split(",");

        for (String e : entries) {
            String[] kv = e.split(":");
            if (kv.length == 2) {
                map.put(kv[0].trim(), Double.parseDouble(kv[1].trim()));
            }
        }
        return map;
    }

    // Save resume in DB
    @Async
    public Long save(MultipartFile file) throws IOException {
        ResumeEntity resume = new ResumeEntity();
        resume.setFileName(file.getOriginalFilename());
        resume.setContent(file.getBytes());
        resume.setContentType(file.getContentType());
        return repository.save(resume).getId();
    }

    // Get all resumes
    public List<ResumeEntity> getAll() {
        return repository.findAll();
    }

    // Get resume by filename
    public ResumeEntity getByFileName(String fileName) {
        return repository.findByFileName(fileName);
    }


    public ResumeEntity getById(Long id) {
        return repository.findById(id).get();
    }
}
