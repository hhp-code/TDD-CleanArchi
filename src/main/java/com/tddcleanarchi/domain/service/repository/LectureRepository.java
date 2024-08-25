package com.tddcleanarchi.domain.service.repository;

import com.tddcleanarchi.domain.Lecture;

import java.util.List;
import java.util.Optional;
public interface LectureRepository {

    Optional<Lecture> findById(long lectureId);

    void save(Lecture lecture);

    List<Lecture> findAll();

    void deleteAll();
}
