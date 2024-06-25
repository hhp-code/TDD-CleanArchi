package com.tddcleanarchi.api.repository;

import com.tddcleanarchi.api.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LectureRepository extends JpaRepository<Lecture, Long> {


    // 특정 날짜 이후의 특강 목록 조회
    List<Lecture> findByLectureTimeAfter(@Param("date") LocalDateTime date);

    // 특정 강의의 현재 신청자 수 조회
    int countEnrollmentsByLectureId(Long userId);
}

