package com.example.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class DataSourceConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);
    
    private final Environment environment;
    
    @Value("${spring.datasource.url:NOT_SET}")
    private String datasourceUrl;
    
    @Value("${spring.datasource.username:NOT_SET}")
    private String datasourceUsername;
    
    @Value("${spring.datasource.password:NOT_SET}")
    private String datasourcePassword;
    
    public DataSourceConfig(Environment environment) {
        this.environment = environment;
    }
    
    @PostConstruct
    public void logDataSourceConfig() {
        String[] activeProfiles = environment.getActiveProfiles();
        logger.info("=== Database Configuration Debug (After Spring Boot Start) ===");
        logger.info("Active Profiles: {}", activeProfiles.length > 0 ? String.join(", ", activeProfiles) : "default");
        logger.info("DATABASE_URL env: {}", System.getenv("DATABASE_URL") != null ? "SET" : "NOT SET");
        logger.info("DATABASE_USERNAME env: {}", System.getenv("DATABASE_USERNAME") != null ? "SET" : "NOT SET");
        logger.info("DATABASE_PASSWORD env: {}", System.getenv("DATABASE_PASSWORD") != null ? "SET" : "NOT SET");
        logger.info("spring.datasource.url: {}", datasourceUrl != null && !datasourceUrl.equals("NOT_SET") ? datasourceUrl.substring(0, Math.min(50, datasourceUrl.length())) + "..." : datasourceUrl);
        logger.info("spring.datasource.username: {}", datasourceUsername);
        logger.info("spring.datasource.password: {}", datasourcePassword != null && !datasourcePassword.equals("NOT_SET") ? "***" : datasourcePassword);
        logger.info("=============================================================");
    }
}

