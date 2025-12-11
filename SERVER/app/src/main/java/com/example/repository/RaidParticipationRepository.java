package com.example.repository;

import com.example.entity.RaidParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RaidParticipationRepository extends JpaRepository<RaidParticipation, Long> {
    
    /**
     * 특정 레이드 방의 모든 참가자 조회
     */
    List<RaidParticipation> findByRaidRoomId(Long raidRoomId);
    
    /**
     * 특정 사용자가 특정 레이드 방에 참가했는지 확인
     */
    Optional<RaidParticipation> findByUserIdAndRaidRoomId(Long userId, Long raidRoomId);
    
    /**
     * 특정 사용자의 모든 참가 기록 조회
     */
    List<RaidParticipation> findByUserId(Long userId);
}

