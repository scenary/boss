package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableCaching
@org.springframework.scheduling.annotation.EnableScheduling
@org.springframework.scheduling.annotation.EnableAsync
public class App {
    
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    @GetMapping("/status")
    public String status() {
        return "{\"status\": \"running\"}";
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        
        // 환경 변수 디버깅 (애플리케이션 시작 전)
        logger.info("=== Environment Check (Before Spring Boot Start) ===");
        String activeProfile = System.getProperty("spring.profiles.active");
        if (activeProfile == null || activeProfile.isEmpty()) {
            activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
        }
        
        // 프로필이 명시적으로 설정되지 않았을 때, 파일 존재 여부로 자동 선택
        // application-dev.properties가 있으면 dev, 없으면 prod 사용
        if (activeProfile == null || activeProfile.isEmpty()) {
            boolean devFileExists = App.class.getResource("/application-dev.properties") != null;
            boolean prodFileExists = App.class.getResource("/application-prod.properties") != null;
            
            if (devFileExists) {
                activeProfile = "dev";
                app.setAdditionalProfiles("dev");
                logger.info("No profile explicitly set, found application-dev.properties, using 'dev' profile");
            } else if (prodFileExists) {
                activeProfile = "prod";
                app.setAdditionalProfiles("prod");
                logger.info("No profile explicitly set, application-dev.properties not found, using 'prod' profile");
            } else {
                // 둘 다 없으면 dev를 기본값으로 (로컬 개발 환경 가정)
                activeProfile = "dev";
                app.setAdditionalProfiles("dev");
                logger.warn("No profile files found, defaulting to 'dev' profile");
            }
        }
        
        logger.info("Active Profile: {}", activeProfile);
        logger.info("DATABASE_URL: {}", System.getenv("DATABASE_URL") != null ? "SET" : "NOT SET");
        logger.info("DATABASE_USERNAME: {}", System.getenv("DATABASE_USERNAME") != null ? "SET" : "NOT SET");
        logger.info("DATABASE_PASSWORD: {}", System.getenv("DATABASE_PASSWORD") != null ? "SET" : "NOT SET");
        logger.info("=====================================================");
        
        app.run(args);
    }
}
