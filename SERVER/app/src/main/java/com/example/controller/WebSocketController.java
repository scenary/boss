package com.example.controller;

import com.example.service.WebSocketConnectionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * WebSocket 메시지 처리 컨트롤러
 */
@Controller
public class WebSocketController {
    
    @Autowired
    private WebSocketConnectionService connectionService;
    
    /**
     * 레이드 방 접속 알림
     */
    @MessageMapping("/raid-room/connect")
    public void connectToRoom(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getSessionId();
            Object roomIdObj = payload.get("roomId");
            Object userIdObj = payload.get("userId");
            
            // 세션에서 사용자 ID 가져오기 (payload에 없으면)
            if (userIdObj == null) {
                HttpSession session = (HttpSession) headerAccessor.getSessionAttributes().get("HTTP.SESSION");
                if (session != null) {
                    userIdObj = session.getAttribute("userId");
                }
            }
            
            if (roomIdObj != null && userIdObj != null) {
                Long roomId = convertToLong(roomIdObj);
                Long userId = convertToLong(userIdObj);
                
                if (roomId != null && userId != null && sessionId != null) {
                    System.out.println("레이드 방 접속 알림 수신: sessionId=" + sessionId + ", userId=" + userId + ", roomId=" + roomId);
                    connectionService.onUserConnect(sessionId, userId, roomId);
                } else {
                    System.out.println("레이드 방 접속 알림 실패: roomId=" + roomId + ", userId=" + userId + ", sessionId=" + sessionId);
                }
            } else {
                System.out.println("레이드 방 접속 알림 실패: roomIdObj=" + roomIdObj + ", userIdObj=" + userIdObj);
            }
        } catch (Exception e) {
            System.err.println("레이드 방 접속 알림 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 레이드 방 접속 해제 알림
     */
    @MessageMapping("/raid-room/disconnect")
    public void disconnectFromRoom(SimpMessageHeaderAccessor headerAccessor) {
        try {
            String sessionId = headerAccessor.getSessionId();
            if (sessionId != null) {
                connectionService.onUserDisconnect(sessionId);
            }
        } catch (Exception e) {
            // 에러 무시 (로그만)
        }
    }
    
    private Long convertToLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Long) return (Long) obj;
        if (obj instanceof Number) return ((Number) obj).longValue();
        if (obj instanceof String) {
            try {
                return Long.parseLong((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}

