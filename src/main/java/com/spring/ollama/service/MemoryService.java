package com.spring.ollama.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MemoryService {

    private final Map<String, List<String>> memory = new ConcurrentHashMap<>();

    public void remember(String userId, String message) {
        memory.computeIfAbsent(userId, k -> new ArrayList<>()).add(message);
    }

    public List<String> getHistory(String userId) {
        return memory.getOrDefault(userId, List.of());
    }
    public List<String> getRecentHistory(String user, int maxMessages) {
        List<String> history = memory.getOrDefault(user, new ArrayList<>());
        int fromIndex = Math.max(history.size() - maxMessages, 0);
        return history.subList(fromIndex, history.size());
    }
}
