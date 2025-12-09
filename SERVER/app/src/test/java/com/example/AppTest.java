package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppTest {
    @Test 
    void appHasHelloEndpoint() {
        App app = new App();
        assertNotNull(app, "App should not be null");
        
        // hello() 메서드가 올바른 문자열을 반환하는지 확인
        String result = app.hello();
        assertNotNull(result, "hello() should return a non-null string");
        assertTrue(result.contains("Spring Boot"), "hello() should contain 'Spring Boot'");
    }
    
    @Test
    void appHasStatusEndpoint() {
        App app = new App();
        String result = app.status();
        assertNotNull(result, "status() should return a non-null string");
        assertTrue(result.contains("running"), "status() should contain 'running'");
    }
}
