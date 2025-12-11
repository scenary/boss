package com.example.service;

import com.example.entity.User;
import com.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 연결 추적 및 레이드 방별 접속 사용자 관리 서비스
 */
@Service
public class WebSocketConnectionService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConnectionService.class);
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    // 레이드 방별 접속 세션 목록 (roomId -> Set<sessionId>)
    private final Map<Long, Set<String>> roomSessions = new ConcurrentHashMap<>();
    
    // 세션별 사용자 정보 (sessionId -> userId)
    private final Map<String, Long> sessionUsers = new ConcurrentHashMap<>();
    
    // 세션별 레이드 방 정보 (sessionId -> roomId)
    private final Map<String, Long> sessionRooms = new ConcurrentHashMap<>();
    
    /**
     * 사용자가 레이드 방에 접속
     * 접속 시점에 참가 기록을 생성 (한 번이라도 접속했던 사용자 추적)
     */
    public void onUserConnect(String sessionId, Long userId, Long roomId) {
        try {
            // 기존 접속이 있으면 먼저 해제 (같은 세션이 다른 방에 접속한 경우)
            onUserDisconnect(sessionId);
            
            sessionUsers.put(sessionId, userId);
            sessionRooms.put(sessionId, roomId);
            
            // 세션 기반으로 접속 추적 (같은 userId라도 다른 세션이면 별도로 추적)
            roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
            
            logger.info("사용자 접속: sessionId={}, userId={}, roomId={}", sessionId, userId, roomId);
            
            // 접속 사용자 목록 브로드캐스트
            broadcastConnectedUsers(roomId);
        } catch (Exception e) {
            logger.error("사용자 접속 처리 중 오류: sessionId={}, userId={}, roomId={}", sessionId, userId, roomId, e);
        }
    }
    
    /**
     * 사용자가 레이드 방에서 접속 해제
     */
    public void onUserDisconnect(String sessionId) {
        try {
            Long userId = sessionUsers.remove(sessionId);
            Long roomId = sessionRooms.remove(sessionId);
            
            if (roomId != null) {
                Set<String> sessions = roomSessions.get(roomId);
                if (sessions != null) {
                    sessions.remove(sessionId);
                    if (sessions.isEmpty()) {
                        roomSessions.remove(roomId);
                    }
                }
                
                logger.info("사용자 접속 해제: sessionId={}, userId={}, roomId={}", sessionId, userId, roomId);
                
                // 사용자가 레이드 방을 나갈 때 이동중 상태 제거
                if (userId != null) {
                    try {
                        // 순환 참조 방지를 위해 ApplicationContextProvider 사용
                        com.example.service.RaidRoomService raidRoomService = com.example.config.ApplicationContextProvider
                            .getApplicationContext()
                            .getBean(com.example.service.RaidRoomService.class);
                        raidRoomService.clearUserMovingStatus(roomId, userId);
                        logger.info("사용자 이동중 상태 제거: userId={}, roomId={}", userId, roomId);
                    } catch (Exception e) {
                        logger.warn("사용자 이동중 상태 제거 실패: userId={}, roomId={}", userId, roomId, e);
                    }
                }
                
                // 접속 사용자 목록 브로드캐스트
                broadcastConnectedUsers(roomId);
            }
        } catch (Exception e) {
            logger.error("사용자 접속 해제 처리 중 오류: sessionId={}", sessionId, e);
        }
    }
    
    /**
     * 레이드 방의 접속 사용자 목록 조회
     * 세션 기반으로 추적하여 같은 userId라도 다른 세션이면 별도로 표시
     */
    public List<Map<String, Object>> getConnectedUsers(Long roomId) {
        Set<String> sessionIds = roomSessions.getOrDefault(roomId, Collections.emptySet());
        List<Map<String, Object>> users = new ArrayList<>();
        Set<Long> addedUserIds = new HashSet<>(); // 중복 제거용 (같은 userId는 한 번만 표시)
        
        for (String sessionId : sessionIds) {
            try {
                Long userId = sessionUsers.get(sessionId);
                if (userId != null && !addedUserIds.contains(userId)) {
                    Optional<User> userOpt = userRepository.findById(userId);
                    if (userOpt.isPresent()) {
                        User user = userOpt.get();
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("userId", user.getId());
                        userData.put("username", user.getUsername() != null ? user.getUsername() : "");
                        userData.put("displayName", user.getDisplayName() != null ? user.getDisplayName() : "");
                        userData.put("avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : "");
                        users.add(userData);
                        addedUserIds.add(userId);
                    }
                }
            } catch (Exception e) {
                logger.warn("사용자 정보 조회 실패: sessionId={}", sessionId, e);
            }
        }
        
        return users;
    }
    
    /**
     * 레이드 방의 접속 사용자 ID 목록 조회 (중복 제거)
     */
    public Set<Long> getConnectedUserIds(Long roomId) {
        Set<String> sessionIds = roomSessions.getOrDefault(roomId, Collections.emptySet());
        Set<Long> userIds = new HashSet<>();
        
        for (String sessionId : sessionIds) {
            Long userId = sessionUsers.get(sessionId);
            if (userId != null) {
                userIds.add(userId);
            }
        }
        
        return userIds;
    }
    
    /**
     * 접속 사용자 목록 브로드캐스트
     */
    private void broadcastConnectedUsers(Long roomId) {
        try {
            List<Map<String, Object>> connectedUsers = getConnectedUsers(roomId);
            Map<String, Object> message = new HashMap<>();
            message.put("type", "connected_users");
            message.put("users", connectedUsers);
            message.put("_timestamp", System.currentTimeMillis());
            
            messagingTemplate.convertAndSend("/topic/raid-room/" + roomId + "/users", message);
            logger.debug("접속 사용자 목록 브로드캐스트: roomId={}, users={}", roomId, connectedUsers.size());
        } catch (Exception e) {
            logger.error("접속 사용자 목록 브로드캐스트 중 오류: roomId={}", roomId, e);
        }
    }
}

