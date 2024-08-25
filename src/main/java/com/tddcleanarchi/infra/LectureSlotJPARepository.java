package com.tddcleanarchi.infra;

import com.tddcleanarchi.domain.LectureSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureSlotJPARepository extends JpaRepository<LectureSlot, Long> {

}