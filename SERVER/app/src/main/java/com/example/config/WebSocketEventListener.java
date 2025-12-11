package com.example.config;

import com.example.service.WebSocketConnectionService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Map;

/**
 * WebSocket 이벤트 리스너
 * 연결/해제 이벤트를 감지하여 접속 사용자 목록 관리
 */
@Component
public class WebSocketEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    
    @Autowired
    private WebSocketConnectionService connectionService;
    
    /**
     * WebSocket 연결 이벤트
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.debug("WebSocket 연결: {}", event.getMessage());
    }
    
    /**
     * WebSocket 구독 이벤트 (레이드 방 구독 시)
     */
    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        try {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
            String destination = headerAccessor.getDestination();
            
            // /topic/raid-room/{roomId} 구독 시 (users로 끝나지 않는 경우만)
            if (destination != null && destination.startsWith("/topic/raid-room/") && !destination.endsWith("/users")) {
                String roomIdStr = destination.replace("/topic/raid-room/", "");
                // 슬래시나 추가 경로가 있으면 제거 (예: "12/users" -> "12")
                if (roomIdStr.contains("/")) {
                    roomIdStr = roomIdStr.substring(0, roomIdStr.indexOf("/"));
                }
                try {
                    Long roomId = Long.parseLong(roomIdStr);
                    String sessionId = headerAccessor.getSessionId();
                    
                    // 세션에서 사용자 ID 가져오기
                    Long userId = getUserIdFromSession(headerAccessor);
                    
                    if (userId != null && roomId != null) {
                        connectionService.onUserConnect(sessionId, userId, roomId);
                        logger.info("레이드 방 구독: sessionId={}, userId={}, roomId={}", sessionId, userId, roomId);
                    } else {
                        logger.warn("사용자 ID를 찾을 수 없음: sessionId={}, roomId={}", sessionId, roomId);
                    }
                } catch (NumberFormatException e) {
                    logger.warn("잘못된 roomId 형식: {}", roomIdStr);
                }
            }
        } catch (Exception e) {
            logger.error("WebSocket 구독 이벤트 처리 중 오류", e);
        }
    }
    
    /**
     * WebSocket 연결 해제 이벤트
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        try {
            StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
            String sessionId = headerAccessor.getSessionId();
            
            if (sessionId != null) {
                connectionService.onUserDisconnect(sessionId);
                logger.info("WebSocket 연결 해제: sessionId={}", sessionId);
            }
        } catch (Exception e) {
            logger.error("WebSocket 연결 해제 이벤트 처리 중 오류", e);
        }
    }
    
    /**
     * 세션에서 사용자 ID 가져오기
     */
    private Long getUserIdFromSession(StompHeaderAccessor headerAccessor) {
        try {
            Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
            if (sessionAttributes != null) {
                // WebSocket 세션 속성에서 가져오기
                Object userIdObj = sessionAttributes.get("userId");
                if (userIdObj != null) {
                    if (userIdObj instanceof Long) {
                        return (Long) userIdObj;
                    } else if (userIdObj instanceof Number) {
                        return ((Number) userIdObj).longValue();
                    }
                }
                
                // HTTP 세션에서 가져오기
                HttpSession httpSession = (HttpSession) sessionAttributes.get("HTTP.SESSION");
                if (httpSession != null) {
                    Object userIdObj2 = httpSession.getAttribute("userId");
                    if (userIdObj2 != null) {
                        if (userIdObj2 instanceof Long) {
                            return (Long) userIdObj2;
                        } else if (userIdObj2 instanceof Number) {
                            return ((Number) userIdObj2).longValue();
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("세션에서 사용자 ID 가져오기 실패", e);
        }
        return null;
    }
}

