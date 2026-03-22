package com.spring.ollama.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // login.html
    }

    @GetMapping("/")
    public String dashboard() {
        return "dashboard"; // your HR page
    }
}
