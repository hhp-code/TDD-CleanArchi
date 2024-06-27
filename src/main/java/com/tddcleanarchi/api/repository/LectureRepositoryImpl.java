package com.tddcleanarchi.api.repository;

import com.tddcleanarchi.api.domain.Lecture;
import com.tddcleanarchi.api.service.repository.LectureRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class LectureRepositoryImpl implements LectureRepository {
    private final LectureJPARepository lectureJpaRepository;

    public LectureRepositoryImpl(LectureJPARepository lectureJpaRepository) {
        this.lectureJpaRepository = lectureJpaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Lecture> findById(long lectureId) {
        return lectureJpaRepository.findById(lectureId);
    }

    @Override
    @Transactional
    public void save(Lecture lecture) {
        lectureJpaRepository.save(lecture);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Lecture> findAll() {
        return lectureJpaRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteAll() {
        lectureJpaRepository.deleteAll();
    }


}
