package com.tddcleanarchi.infra;

import com.tddcleanarchi.domain.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureJPARepository extends JpaRepository<Lecture, Long> {

}

