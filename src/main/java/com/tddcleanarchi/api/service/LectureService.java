package com.tddcleanarchi.api.service;

import com.tddcleanarchi.api.controller.ResultMessage;
import com.tddcleanarchi.api.domain.Lecture;
import com.tddcleanarchi.api.domain.LectureSlot;
import com.tddcleanarchi.api.controller.dto.LectureSlotDTO;
import com.tddcleanarchi.api.controller.dto.LectureDTO;
import com.tddcleanarchi.api.repository.LectureSlotRepository;
import com.tddcleanarchi.api.repository.LectureRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LectureService {
    private final LectureRepository lectureRepository;
    private final LectureSlotRepository lectureSlotRepository;

    public LectureService(LectureRepository lectureRepository, LectureSlotRepository lectureSlotRepository) {
        this.lectureRepository = lectureRepository;
        this.lectureSlotRepository = lectureSlotRepository;
    }

    @Transactional
    public ResponseEntity<LectureSlotDTO> applyAndSearchAndReturnsHttpMessage(LectureSlotDTO lectureSlotDTO) {
        try {
            List<LectureDTO> filteredFinal = getFilteredLectures(lectureSlotDTO);
            if (filteredFinal.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            Lecture lecture = lectureRepository.findById(lectureSlotDTO.lectureId())
                    .orElseThrow(() -> new EntityNotFoundException("강의를 찾을 수 없습니다."));
            LectureSlot application = new LectureSlot(lectureSlotDTO.id(), lecture);
            LectureSlot savedApplication = lectureSlotRepository.save(application);

            return ResponseEntity.ok(convertToLectureApplicationDTO(savedApplication));
        }catch (EntityNotFoundException e){
            return ResponseEntity.badRequest().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private List<LectureDTO> getFilteredLectures(LectureSlotDTO lectureSlotDTO) {
        List<LectureDTO> all = lectureRepository.findAll().stream()
                .map(this::convertToLectureDTO)
                .toList();
        List<LectureDTO> lectureDTOS = filterLectureByDateAndTime(all, LocalDateTime.now());
        List<LectureDTO> filteredLectures = filterLectureByLessThan30People(lectureDTOS);
        return filterLectureNotApplied(filteredLectures, lectureSlotDTO.id());
    }

    private List<LectureDTO> filterLectureNotApplied(List<LectureDTO> all, Long id) {
        return all.stream()
                .filter(lecture -> !lecture.enrolles().contains(id))
                .collect(Collectors.toList());

    }

    public List<LectureDTO> getAvailableLectures() {
        List<Lecture> lectures = lectureRepository.findAll();
        List<LectureDTO> lectureDTO = new ArrayList<>();
        for (Lecture lecture : lectures) {
            lectureDTO.add(convertToLectureDTO(lecture));
        }
        return filterLectureByDateAndTime(lectureDTO, LocalDateTime.now());
    }

    public List<LectureDTO> filterLectureByDateAndTime(List<LectureDTO> lectures, LocalDateTime date) {
        return lectures.stream()
                .filter(lecture -> lecture.lectureTime() == date)
                .collect(Collectors.toList());
    }

    private static final int MAX_CAPACITY = 30;
    public List<LectureDTO> filterLectureByLessThan30People(List<LectureDTO> lectures) {
        return lectures.stream()
                .filter(lecture -> lecture.registered() < MAX_CAPACITY)
                .collect(Collectors.toList());
    }
    @Transactional
    public Map<Long, ResultMessage> applyLecture(LectureDTO lectureDTO, long userId) {
        Lecture lecture = lectureRepository.findById(lectureDTO.id())
                .orElseThrow(() -> new EntityNotFoundException("강의를 찾을 수 없습니다."));
        if (isLectureFull(lecture)) {
            return createFailureResult(userId, lectureDTO, "강의 신청이 불가능합니다.");
        }
        if (hasAlreadyApplied(userId)) {
            return createFailureResult(userId, lectureDTO, "이미 신청한 강의입니다.");
        }

        return saveSlot(lectureDTO, userId, lecture);
    }

    private Map<Long, ResultMessage> saveSlot(LectureDTO lectureDTO, long userId, Lecture lecture) {
        LectureSlot application = new LectureSlot(userId, lecture);

        try {
            lectureSlotRepository.save(application);
            return Map.of(userId, new ResultMessage(lectureDTO.name(), lectureDTO.lectureTime(), "success", "강의 신청이 완료되었습니다."));
        } catch (Exception e) {
            return createFailureResult(userId, lectureDTO, "강의 신청중 오류가 발생했습니다.");
        }
    }

    private boolean hasAlreadyApplied(long userId) {
        return lectureSlotRepository.existsByUserId(userId);
    }

    private Map<Long, ResultMessage> createFailureResult(long userId, LectureDTO lectureDTO, String message) {
        return Map.of(userId, new ResultMessage(lectureDTO.name(), lectureDTO.lectureTime(), "fail", message));
    }

    private boolean isLectureFull(Lecture lecture) {
        return lecture.getEnrollee().size() >= lecture.getCapacity();
    }

    private LectureDTO convertToLectureDTO(Lecture lecture) {
        return new LectureDTO(
                lecture.getLectureId(),
                lecture.getName(),
                lecture.getLectureTime(),
                lecture.getCapacity(),
                lecture.getEnrollee().size(),
                lecture.getEnrollee().stream()
                        .map(LectureSlot::getUserId)
                        .collect(Collectors.toList())
        );
    }

    private LectureSlotDTO convertToLectureApplicationDTO(LectureSlot application) {
        return new LectureSlotDTO(
                application.getId(),
                application.getLecture().getLectureId()
        );
    }

    public ResultMessage isSuccessfullyApplied(long userId) {
        return lectureSlotRepository.findById(userId)
                .map(application -> new ResultMessage(application.getLecture().getName(), application.getLecture().getLectureTime(), "success", "강의 신청이 완료되었습니다."))
                .orElseGet(() -> new ResultMessage("", LocalDateTime.now(), "fail", "강의 신청이 불가능합니다."));
    }
}