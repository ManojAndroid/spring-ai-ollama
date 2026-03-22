package com.spring.ollama.controller;

import com.spring.ollama.dto.LoginRequest;
import com.spring.ollama.service.UserService;
import com.spring.ollama.utils.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody LoginRequest req) {
        userService.register(req.username(), req.password());
        return ResponseEntity.ok("SUCCESS");
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest req) {

        if (userService.login(req.username(), req.password())) {
            return jwtUtil.generateToken(req.username());
        }

        throw new RuntimeException("Invalid credentials");
    }
}
