package com.tddcleanarchi.api.service;

import com.tddcleanarchi.api.controller.LectureMapper;
import com.tddcleanarchi.api.controller.ResultMessage;
import com.tddcleanarchi.api.domain.Lecture;
import com.tddcleanarchi.api.domain.LectureSlot;
import com.tddcleanarchi.api.controller.dto.LectureSlotDTO;
import com.tddcleanarchi.api.controller.dto.LectureDTO;
import com.tddcleanarchi.api.repository.LectureSlotRepository;
import com.tddcleanarchi.api.repository.LectureRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LectureService {
    private final LectureRepository lectureRepository;
    private final LectureSlotRepository lectureSlotRepository;
    private final LectureMapper lectureMapper = new LectureMapper();
    private static final int MAX_CAPACITY = 30;


    public LectureService(LectureRepository lectureRepository, LectureSlotRepository lectureSlotRepository) {
        this.lectureRepository = lectureRepository;
        this.lectureSlotRepository = lectureSlotRepository;
    }

    @Transactional
    public ResponseEntity<LectureSlotDTO> applySequence(LectureSlotDTO lectureSlotDTO) {
        //유효한 강의신청인지 검증
        Lecture lecture = lectureValidation(lectureSlotDTO);
        //신청가능한 강의 확인
        lectureFiltering(lectureSlotDTO);
        LectureSlot application = new LectureSlot(lectureSlotDTO.userId(), lecture);
        LectureSlot savedApplication = lectureSlotRepository.save(application);
        LectureSlotDTO lectureSlot = lectureMapper.convertToLectureSlotDto(savedApplication);

        return ResponseEntity.ok(lectureSlot);
    }

    private void lectureFiltering(LectureSlotDTO lectureSlotDTO) {
        List<LectureDTO> filteredFinal = getFilteredLectures(lectureSlotDTO);
        if (filteredFinal.isEmpty()) {
            log.error("강의 신청이 불가능합니다.");
            throw new IllegalStateException("강의 신청이 불가능합니다.");
        }
    }

    private Lecture lectureValidation(LectureSlotDTO lectureSlotDTO) {
        Lecture lecture = lectureRepository.findById(lectureSlotDTO.lectureId())
                .orElseThrow(() -> new EntityNotFoundException("강의를 찾을 수 없습니다."));
        if (isLectureFull(lecture)) {
            log.error("강의가 꽉 찼습니다.");
            throw new IllegalStateException("강의가 꽉 찼습니다.");
        }
        if (hasAlreadyApplied(lecture.getLectureId(), lectureSlotDTO.userId())) {
            log.error("이미 신청한 강의입니다.");
            throw new IllegalStateException("이미 신청한 강의입니다.");
        }
        return lecture;
    }

    private List<LectureDTO> getFilteredLectures(LectureSlotDTO lectureSlotDTO) {

        List<LectureDTO> mappedLectureDTO = lectureRepository.findAll().stream()
                .map(lectureMapper::convertToLectureDTO)
                .toList();
        List<LectureDTO> filteredByDates = filterLectureByDateAndTime(mappedLectureDTO, LocalDateTime.now());
        List<LectureDTO> filteredByPerson = filterLectureByLessThan30People(filteredByDates);
        return filterLectureNotApplied(filteredByPerson, lectureSlotDTO.userId());
    }

    private List<LectureDTO> filterLectureNotApplied(List<LectureDTO> lectureDTO, Long id) {
        return lectureDTO.stream()
                .filter(lecture -> !lecture.enrolles().contains(id))
                .collect(Collectors.toList());

    }

    public List<LectureDTO> getAvailableLectures() {
        List<Lecture> lectures = lectureRepository.findAll();
        List<LectureDTO> lectureDTO = new ArrayList<>();
        for (Lecture lecture : lectures) {
            lectureDTO.add(lectureMapper.convertToLectureDTO(lecture));
        }
        return filterLectureByDateAndTime(lectureDTO, LocalDateTime.now());
    }

    public List<LectureDTO> filterLectureByDateAndTime(List<LectureDTO> lectures, LocalDateTime date) {
        log.info("filterLectureByDateAndTime");
        return lectures.stream()
                .filter(lecture -> lecture.lectureTime().isAfter(date))
                .collect(Collectors.toList());
    }

    public List<LectureDTO> filterLectureByLessThan30People(List<LectureDTO> lectures) {
        return lectures.stream()
                .filter(lecture -> lecture.registered() < MAX_CAPACITY)
                .collect(Collectors.toList());
    }

    boolean hasAlreadyApplied(long lectureId, long userId) {
        return lectureRepository.findEnrolleeByLectureIdAndUserId(lectureId, userId).isPresent();
    }

    boolean isLectureFull(Lecture lecture) {
        return lecture.getEnrollee().size() >= lecture.getCapacity();
    }

    public ResultMessage isSuccessfullyApplied(long userId) {
        return lectureSlotRepository.findByUserId(userId)
                .map(application ->
                        new ResultMessage(application.getLecture().getName(), application.getLecture().getLectureTime(), "success", "강의 신청이 완료되었습니다."))
                .orElseGet(() -> new ResultMessage("", LocalDateTime.now(), "fail", "강의 신청이 실패했습니다."));
    }
}