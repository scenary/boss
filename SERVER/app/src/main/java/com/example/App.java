package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class App {

    @GetMapping("/")
    public String hello() {
        return "Spring Boot 서버가 실행 중입니다!";
    }

    @GetMapping("/api/status")
    public String status() {
        return "{\"status\": \"running\"}";
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
