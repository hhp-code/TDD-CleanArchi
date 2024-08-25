package com.tddcleanarchi.infra;

import com.tddcleanarchi.domain.LectureSlot;
import com.tddcleanarchi.domain.service.repository.LectureSlotRepository;
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
