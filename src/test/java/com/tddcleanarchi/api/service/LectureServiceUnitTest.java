package com.tddcleanarchi.api.service;

import com.tddcleanarchi.api.controller.ResultMessage;
import com.tddcleanarchi.api.domain.Lecture;
import com.tddcleanarchi.api.domain.LectureSlot;
import com.tddcleanarchi.api.controller.dto.LectureSlotDTO;
import com.tddcleanarchi.api.controller.dto.LectureDTO;
import com.tddcleanarchi.api.repository.LectureSlotRepository;
import com.tddcleanarchi.api.repository.LectureRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LectureServiceUnitTest {
    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private LectureSlotRepository lectureSlotRepository;

    @InjectMocks
    private LectureService lectureService;

    private Long userId;
    private Long lectureId;
    private Lecture lecture;
    private LectureDTO lectureDTO;

    @BeforeEach
    void setUp() {
        userId = 1L;
        lectureId = 1L;
        lecture = new Lecture(lectureId, "Test Lecture", LocalDateTime.now(), 30, List.of());
        lectureDTO = new LectureDTO(lectureId, "Test Lecture", LocalDateTime.now(), 30, 0, List.of());
    }

    @Test
    void applyAndSearchAndReturnsHttpMessage_성공() {
        LectureSlotDTO applicationDTO = new LectureSlotDTO(userId,lectureId);
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(lectureSlotRepository.save(any())).thenReturn(new LectureSlot(userId,lecture));

        ResponseEntity<LectureSlotDTO> response = lectureService.applyAndSearchAndReturnsHttpMessage(applicationDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userId, response.getBody().id());
        assertEquals(lectureId, response.getBody().lectureId());
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
        List<LectureDTO> lectures = List.of(lectureDTO);
        LocalDateTime date = LocalDateTime.now();

        List<LectureDTO> result = lectureService.filterLectureByDateAndTime(lectures, date);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }


    @Test
    void filterLectureByLessThan30People_성공() {
        List<LectureDTO> lectures = List.of(lectureDTO);

        List<LectureDTO> result = lectureService.filterLectureByLessThan30People(lectures);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void applyLecture_성공() {
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(lectureSlotRepository.existsByUserId(userId)).thenReturn(false);

        Map<Long, ResultMessage> result = lectureService.applyLecture(lectureDTO, userId);

        assertTrue(result.containsKey(userId));
        assertEquals("success", result.get(userId).status());
        verify(lectureSlotRepository).save(any(LectureSlot.class));
    }

    @Test
    void applyLecture_실패_이미_신청한_경우() {
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(lectureSlotRepository.existsByUserId(lectureId)).thenReturn(true);

        Map<Long, ResultMessage> result = lectureService.applyLecture(lectureDTO,userId);

        assertTrue(result.containsKey(userId));
        assertEquals("fail", result.get(userId).status());
        verify(lectureSlotRepository, never()).save(any(LectureSlot.class));
    }

    @Test
    void applyLecture_실패_강의가_없는_경우() {
        //given
        //when
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.empty());
        //then
        assertThrows(EntityNotFoundException.class, () -> lectureService.applyLecture(lectureDTO, userId));
        verify(lectureSlotRepository, never()).save(any(LectureSlot.class));
    }
}