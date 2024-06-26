package com.tddcleanarchi.api.repository;

import com.tddcleanarchi.api.domain.Lecture;
import com.tddcleanarchi.api.domain.LectureSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LectureRepository extends JpaRepository<Lecture, Long> {

    // 특정 강의의 특정 신청자 조회
    @Query("SELECT ls FROM Lecture l JOIN l.enrollee ls WHERE l.lectureId = :lectureId AND ls.userId = :userId")
    Optional<LectureSlot> findEnrolleeByLectureIdAndUserId(@Param("lectureId") Long lectureId, @Param("userId") Long userId);

}

