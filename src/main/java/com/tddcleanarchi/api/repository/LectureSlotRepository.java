package com.tddcleanarchi.api.repository;

import com.tddcleanarchi.api.domain.LectureSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureSlotRepository extends JpaRepository<LectureSlot, Long> {

    // 특정 사용자의 특정 강의 신청 여부 확인
    boolean existsByUserIdAndLecture_LectureId(Long userId, Long lectureId);

    // 특정 강의의 모든 신청 내역 조회
    List<LectureSlot> findByLecture_LectureId(Long lectureId);

    boolean existsByUserId(Long userId);

}