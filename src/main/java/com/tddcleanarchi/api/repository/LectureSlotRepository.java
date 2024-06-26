package com.tddcleanarchi.api.repository;

import com.tddcleanarchi.api.domain.LectureSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LectureSlotRepository extends JpaRepository<LectureSlot, Long> {

    Optional<LectureSlot> findByUserId(Long userId);

}