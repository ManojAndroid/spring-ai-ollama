package com.spring.ollama.service;
import com.spring.ollama.dto.ResumeVector;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryVectorStore {

    private final List<ResumeVector> store = new ArrayList<>();

    public void save(ResumeVector vector) {
        store.add(vector);
    }

    public List<ResumeVector> getAll() {
        return store;
    }
}
