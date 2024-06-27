package com.tddcleanarchi.api.service;

import com.tddcleanarchi.api.domain.Lecture;
import com.tddcleanarchi.api.domain.LectureSlot;
import com.tddcleanarchi.api.service.repository.LectureRepository;
import com.tddcleanarchi.api.service.repository.LectureSlotRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class LectureService {
    private final LectureRepository lectureRepository;
    private final LectureSlotRepository lectureSlotRepository;
    private static final int MAX_CAPACITY = 30;
    private int currentEnrollment = 0;
    private final ReentrantLock lock = new ReentrantLock(true);

    public LectureService(LectureRepository lectureRepository, LectureSlotRepository lectureSlotRepository) {
        this.lectureRepository = lectureRepository;
        this.lectureSlotRepository = lectureSlotRepository;
    }

    @Transactional
    public LectureSlot create(LectureCommand.Create lectureSlot) {
        lock.lock();
        try {
            if (currentEnrollment >= MAX_CAPACITY) {throw new IllegalStateException("강의가 꽉 찼습니다.");}
            Lecture lecture = lectureValidation(lectureSlot);
            LectureSlot application = new LectureSlot(lectureSlot.userId(), lecture);
            lecture.addEnrollee(application);
            LectureSlot savedSlot = lectureSlotRepository.save(application);
            lectureRepository.save(lecture);
            currentEnrollment++;
            return savedSlot;
        } finally {
            lock.unlock();
        }
    }

    Lecture lectureValidation(LectureCommand.Create lectureSlotDTO) {
        return lectureRepository.findById(lectureSlotDTO.lectureId())
                .filter(lecture -> lecture.getLectureTime().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new EntityNotFoundException("유효한 강의를 찾을 수 없습니다."));
    }


    @Transactional(readOnly = true)
    public List<Lecture> getLectures() {
        return lectureRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Boolean status(LectureCommand.Search userId) {
        return lectureRepository.findById(userId.userId()).isPresent();
    }


}