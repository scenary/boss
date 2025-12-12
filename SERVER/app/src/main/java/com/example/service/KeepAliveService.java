package com.example.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 서버 Keep-Alive 서비스
 * 
 * 서버가 sleep되지 않도록 주기적으로 health check를 수행합니다.
 * 클라이언트가 없어도 서버가 계속 활성 상태를 유지합니다.
 */
@Service
public class KeepAliveService {
    
    private static final Logger logger = LoggerFactory.getLogger(KeepAliveService.class);
    
    @Value("${server.port:8080}")
    private int serverPort;
    
    private final RestTemplate restTemplate;
    
    public KeepAliveService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * 10분마다 서버의 health check 엔드포인트를 호출하여 서버를 활성 상태로 유지합니다.
     * fixedRate = 600000ms = 10분
     */
    @Scheduled(fixedRate = 600000)
    public void keepAlive() {
        try {
            String healthUrl = "http://localhost:" + serverPort + "/api/auth/health";
            restTemplate.getForObject(healthUrl, String.class);
            logger.debug("Keep-alive health check SUCCESS");
        } catch (Exception e) {
            logger.warn("Keep-alive health check FAILED: {}", e.getMessage());
        }
    }
}
