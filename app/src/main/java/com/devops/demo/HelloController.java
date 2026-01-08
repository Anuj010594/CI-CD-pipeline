package com.devops.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "Hello DevOps 🚀 Application deployed via Jenkins + Docker + Kubernetes!";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}

