package com.spring.ollama.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
//todo use database
    private Map<String, String> users = new HashMap<>();

    public void register(String username, String password) {
        users.put(username, password);
    }

    public boolean login(String username, String password) {
        return password.equals(users.get(username));
    }
}
