package com.tddcleanarchi.api.repository;

import com.tddcleanarchi.api.domain.LectureSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureSlotJPARepository extends JpaRepository<LectureSlot, Long> {

}