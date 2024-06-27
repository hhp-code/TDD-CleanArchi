package com.tddcleanarchi.api.repository;

import com.tddcleanarchi.api.domain.LectureSlot;
import com.tddcleanarchi.api.service.repository.LectureSlotRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class LectureSlotRepositoryImpl implements LectureSlotRepository {

    private final LectureSlotJPARepository lectureSlotJPARepository;

    public LectureSlotRepositoryImpl(LectureSlotJPARepository lectureSlotJPARepository) {
        this.lectureSlotJPARepository = lectureSlotJPARepository;
    }

    @Override
    @Transactional
    public LectureSlot save(LectureSlot application) {
        return lectureSlotJPARepository.save(application);
    }

}
