package com.tddcleanarchi.api.service;

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


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
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
    private LectureDTO testLectureDTO;

    @BeforeEach
    void setUp() {
        userId = 1L;
        lectureId = 1L;
        lecture = new Lecture(lectureId, "Test Lecture", LocalDateTime.now().plusDays(1), 30, List.of());
        testLectureDTO = new LectureDTO(lectureId, "Test Lecture", LocalDateTime.now().plusDays(1), 30, 0, List.of());
    }


    @Test
    void 메서드_가능한_특강강의가있는걸_조회할때_성공() {
        //given
        when(lectureRepository.findAll()).thenReturn(List.of(lecture));

        //when
        List<LectureDTO> result = lectureService.getAvailableLectures();

        //then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(lectureRepository).findAll();
    }

    @Test
    void 메서드_날짜와시간으로필터링하는_성공() {
        //given
        List<LectureDTO> lectures = List.of(testLectureDTO);
        LocalDateTime date = LocalDateTime.now();

        //when
        List<LectureDTO> result = lectureService.filterLectureByDateAndTime(lectures, date);

        //then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }


    @Test
    void 메서드_삼십명이하인지확인_성공() {
        //given
        List<LectureDTO> lectures = List.of(testLectureDTO);
        //when
        List<LectureDTO> result = lectureService.filterLectureByLessThan30People(lectures);

        //then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void 오류_강의신청을할때_30명이넘어버리면() {
        //given
        LectureSlotDTO applicationDTO = new LectureSlotDTO(userId, lectureId);
        LectureSlot dummyLectureSlot = new LectureSlot(userId, lecture);
        List<LectureSlot> dummyList = new ArrayList<>();
        for(int i = 0; i < 30; i++) {
            dummyList.add(dummyLectureSlot);
        }
        Lecture fullLecture = new Lecture(lectureId, "Full Lecture", LocalDateTime.now(), 30, dummyList);
        given(lectureRepository.findById(lectureId)).willReturn(Optional.of(fullLecture));

        //when
       try{
           lectureService.applySequence(applicationDTO);
       }catch(Exception e) {
           //then
           assertTrue(e.getMessage().contains("강의가 꽉 찼습니다."));
       }


    }

    @Test
    void 오류_강의신청을했는데_똑같은강의를한번더신청할때() {
        //given
        LectureSlotDTO applicationDTO = new LectureSlotDTO(userId, lectureId);
        given(lectureRepository.findById(lectureId)).willReturn(Optional.of(lecture));
        given(lectureRepository.findEnrolleeByLectureIdAndUserId(lectureId, userId))
                .willReturn(Optional.of(new LectureSlot(userId,lecture)));

        //when
        try{
            lectureService.applySequence(applicationDTO);
        }catch(Exception e){
            //then
            assertTrue(e.getMessage().contains("이미 신청한 강의입니다."));
        }
    }

    @Test
    void 오류_강의신청을했는데_강의를찾을수없을때() {
        //given
        LectureSlotDTO applicationDTO = new LectureSlotDTO(userId, lectureId);
        given(lectureRepository.findById(lectureId)).willReturn(Optional.empty());

        //when
        try{
            lectureService.applySequence(applicationDTO);
        }catch (Exception e){
            //then
            assertTrue(e.getMessage().contains("강의를 찾을 수 없습니다."));
        }
    }
    @Test
    void 메소드_강의신청이_성공했을때_알려주는것() {
        //given
        LectureSlot lectureSlot = new LectureSlot(userId, lecture);
        when(lectureSlotRepository.findByUserId(userId)).thenReturn(Optional.of(lectureSlot));

        //when
        ResultMessage result = lectureService.isSuccessfullyApplied(userId);

        //then
        assertEquals("success", result.status());
        assertEquals("강의 신청이 완료되었습니다.", result.message());
        assertEquals(lecture.getName(), result.lectureName());
        assertEquals(lecture.getLectureTime(), result.lectureTime());
    }

    @Test
    void 메소드_강의신청이_실패했는지_알려줄때() {
        //given
        when(lectureSlotRepository.findByUserId(userId)).thenReturn(Optional.empty());

        //when
        ResultMessage result = lectureService.isSuccessfullyApplied(userId);

        //then
        assertEquals("fail", result.status());
        assertEquals("강의 신청이 실패했습니다.", result.message());
        assertEquals("", result.lectureName());
        assertNotNull(result.lectureTime());
    }

    @Test
    void 메소드_강의가꽉찼는지_알려주는() {
        // given
        Lecture fullLecture = new Lecture(lectureId, "Full Lecture", LocalDateTime.now(), 30, new ArrayList<>());
        for (int i = 0; i < 30; i++) {
            fullLecture.getEnrollee().add(new LectureSlot(userId, fullLecture));
        }
        // when
        boolean result = lectureService.isLectureFull(fullLecture);
        // then
        assertTrue(result);
    }
    @Test
    void 메서드_강의가_이미신청했는지_확인(){
        //given
        LectureSlot lectureSlot = new LectureSlot(userId, lecture);
        given(lectureRepository.findEnrolleeByLectureIdAndUserId(lectureId, userId)).willReturn(Optional.of(lectureSlot));

        //when
        boolean result = lectureService.hasAlreadyApplied(lectureId, userId);

        //then
        assertTrue(result);
    }


}