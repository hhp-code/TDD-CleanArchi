package com.tddcleanarchi.api.repository;

import com.tddcleanarchi.api.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureJPARepository extends JpaRepository<Lecture, Long> {

}

