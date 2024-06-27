package com.tddcleanarchi.api.service;

import com.tddcleanarchi.api.domain.Lecture;
import com.tddcleanarchi.api.domain.LectureSlot;
import com.tddcleanarchi.api.service.repository.LectureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LectureServiceUnitTest {
    @Mock
    private LectureRepository lectureRepository;
    @Mock
    private Lecture lecture;

    @InjectMocks
    private LectureService lectureService;

    private Long userId;
    private Long lectureId;


    @BeforeEach
    void setUp() {
        userId = 1L;
        lectureId = 1L;
    }

    @Test
    void 메서드_가능한_특강강의가있는걸_조회할때_성공() {
        //given
        when(lectureRepository.findAll()).thenReturn(List.of(lecture));

        //when
        List<Lecture> result = lectureService.getLectures();

        //then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(lectureRepository).findAll();
    }


    @Test
    void 오류_강의신청을할때_30명이넘어버리면() {
        //given
        LectureCommand.Create create = new LectureCommand.Create(userId, lectureId);
        LectureSlot dummyLectureSlot = new LectureSlot(userId, lecture);
        Set<LectureSlot> dummySet = new HashSet<>();
        for(int i = 0; i < 30; i++) {
            dummySet.add(dummyLectureSlot);
        }
        Lecture fullLecture = new Lecture(lectureId, "Full Lecture", LocalDateTime.now(), 30, dummySet);
        given(lectureRepository.findById(lectureId)).willReturn(Optional.of(fullLecture));

        //when
       try{
           lectureService.create(create);
       }catch(Exception e) {
           //then
           System.out.println(e.getMessage());
           assertTrue(e.getMessage().contains("유효한 강의를 찾을 수 없습니다."));
       }


    }


    @Test
    void 오류_강의신청을했는데_강의를찾을수없을때() {
        //given
        LectureCommand.Create applicationDTO = new LectureCommand.Create(userId, lectureId);
        given(lectureRepository.findById(lectureId)).willReturn(Optional.empty());

        //when
        try{
            lectureService.create(applicationDTO);
        }catch (Exception e){
            //then
            assertTrue(e.getMessage().contains("강의를 찾을 수 없습니다."));
        }
    }
    @Test
    void 메소드_강의신청이_성공했을때_알려주는것() {
        //given
        LectureCommand.Search search = new LectureCommand.Search(userId);
        Lecture lecture = new Lecture(lectureId, "Test Lecture", LocalDateTime.now().plusDays(1), 30, new HashSet<>());

        given(lectureRepository.findById(userId)).willReturn(Optional.of(lecture));

        //when
        boolean result = lectureService.status(search);

        //then
        assert result;
    }

    @Test
    void 메소드_강의신청이_실패했는지_알려줄때() {
        //given
        LectureCommand.Search search = new LectureCommand.Search(userId);
        given(lectureRepository.findById(userId)).willReturn(Optional.empty());

        //when
        try{
            lectureService.status(search);
        }catch (NoSuchElementException e){
            //then
            assertThat(e.getMessage()).contains("No value present");
        }

        //then
        verify(lectureRepository).findById(userId);
    }

}