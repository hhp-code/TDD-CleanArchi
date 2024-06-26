package com.tddcleanarchi.api.service;

import com.tddcleanarchi.api.controller.LectureMapper;
import com.tddcleanarchi.api.controller.ResultMessage;
import com.tddcleanarchi.api.domain.Lecture;
import com.tddcleanarchi.api.domain.LectureSlot;
import com.tddcleanarchi.api.controller.dto.LectureSlotDTO;
import com.tddcleanarchi.api.controller.dto.LectureDTO;
import com.tddcleanarchi.api.repository.LectureSlotRepository;
import com.tddcleanarchi.api.repository.LectureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LectureServiceUnitTest {
    @Mock
    private LectureRepository lectureRepository;

    private final LectureMapper lectureMapper = new LectureMapper();
    @Mock
    private LectureSlotRepository lectureSlotRepository;

    @InjectMocks
    private LectureService lectureService;

    private Long userId;
    private Long lectureId;
    private Lecture lecture;
    private LectureDTO testLectureDTO;

    @BeforeEach
    void setUp() {
        userId = 1L;
        lectureId = 1L;
        lecture = new Lecture(lectureId, "Test Lecture", LocalDateTime.now().plusDays(1), 30, List.of());
        testLectureDTO = new LectureDTO(lectureId, "Test Lecture", LocalDateTime.now().plusDays(1), 30, 0, List.of());
    }


    @Test
    void getAvailableLectures_성공() {
        when(lectureRepository.findAll()).thenReturn(List.of(lecture));

        List<LectureDTO> result = lectureService.getAvailableLectures();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(lectureRepository).findAll();
    }

    @Test
    void filterLectureByDate_AndTime_성공() {
        List<LectureDTO> lectures = List.of(testLectureDTO);
        LocalDateTime date = LocalDateTime.now();

        List<LectureDTO> result = lectureService.filterLectureByDateAndTime(lectures, date);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }


    @Test
    void filterLectureByLessThan30People_성공() {
        List<LectureDTO> lectures = List.of(testLectureDTO);

        List<LectureDTO> result = lectureService.filterLectureByLessThan30People(lectures);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void applyAndSearchAndReturnsHttpMessage_강의가_꽉_찼을_때() {
        LectureSlotDTO applicationDTO = new LectureSlotDTO(userId, lectureId);

        LectureSlot dummyLectureSlot = new LectureSlot(userId, lecture);
        List<LectureSlot> dummyList = new ArrayList<>();
        for(int i = 0; i < 30; i++) {
            dummyList.add(dummyLectureSlot);
        }
        Lecture fullLecture = new Lecture(lectureId, "Full Lecture", LocalDateTime.now(), 30, dummyList);
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(fullLecture));

        ResponseEntity<?> response = lectureService.applyAndSearchAndReturnsHttpMessage(applicationDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void applyAndSearchAndReturnsHttpMessage_이미_신청한_강의일_때() {
        LectureSlotDTO applicationDTO = new LectureSlotDTO(userId, lectureId);
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(lectureRepository.findEnrolleeByLectureIdAndUserId(lectureId, userId)).thenReturn(Optional.of(new LectureSlot(userId,lecture)));

        ResponseEntity<?> response = lectureService.applyAndSearchAndReturnsHttpMessage(applicationDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void applyAndSearchAndReturnsHttpMessage_강의를_찾을_수_없을_때() {
        LectureSlotDTO applicationDTO = new LectureSlotDTO(userId, lectureId);
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.empty());

        ResponseEntity<?> response = lectureService.applyAndSearchAndReturnsHttpMessage(applicationDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
    void convertToLectureSlotDto_성공() {
        LectureSlot lectureSlot = new LectureSlot(userId, lecture);

        LectureSlotDTO result = lectureMapper.convertToLectureSlotDto(lectureSlot);

        assertEquals(userId, result.userId());
        assertEquals(lectureId, result.lectureId());
    }
    @Test
    void isSuccessfullyApplied_성공() {
        LectureSlot lectureSlot = new LectureSlot(userId, lecture);
        when(lectureSlotRepository.findByUserId(userId)).thenReturn(Optional.of(lectureSlot));

        ResultMessage result = lectureService.isSuccessfullyApplied(userId);

        assertEquals("success", result.status());
        assertEquals("강의 신청이 완료되었습니다.", result.message());
        assertEquals(lecture.getName(), result.lectureName());
        assertEquals(lecture.getLectureTime(), result.lectureTime());
    }

    @Test
    void isSuccessfullyApplied_실패() {
        when(lectureSlotRepository.findByUserId(userId)).thenReturn(Optional.empty());

        ResultMessage result = lectureService.isSuccessfullyApplied(userId);

        assertEquals("fail", result.status());
        assertEquals("강의 신청이 실패했습니다.", result.message());
        assertEquals("", result.lectureName());
        assertNotNull(result.lectureTime());
    }


}