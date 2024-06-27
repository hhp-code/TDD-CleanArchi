package com.tddcleanarchi.api.service;

import com.tddcleanarchi.api.domain.Lecture;
import com.tddcleanarchi.api.domain.LectureSlot;
import com.tddcleanarchi.api.service.repository.LectureRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@Transactional
@SpringBootTest
@ActiveProfiles("local")
class LectureServiceConcurrencyTest {
    @Autowired
    private LectureService lectureService;

    @Autowired
    private LectureRepository lectureRepository;


    @Test
    void 신청자가29명일때_2명이동시에요청할경우(){
        //given
        long lectureId =4L;
        //사용자들을 29명으로 설정
        for(long i=0; i<29; i++){
            lectureService.create(new LectureCommand.Create(i, lectureId));
        }
        //when

        //30번째 사용자가 2명시 동시에 요청
        try{
            CompletableFuture.allOf(
                    CompletableFuture.runAsync(() -> lectureService.create(new LectureCommand.Create(30L, lectureId))),
                    CompletableFuture.runAsync(() -> lectureService.create(new LectureCommand.Create(31L, lectureId)))
            ).join();
        } catch(Exception e){
            Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
            assertThat(lecture.getEnrollee().size()).isEqualTo(29);
            assertThat(e.getMessage()).contains("강의가 꽉 찼습니다.");
        }
    }
    @Test
    void 신청자가_동시에_31명이_들어온경우_낙관적락_실패_reenterentlock으로_변경후_성공(){
        //given
        long lectureId = 4L;
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        //when
        for (long i = 0; i < 31; i++) {
            final long applicantId = i;  // 람다에서 사용하기 위해 final 변수로 선언
            futures.add(CompletableFuture.runAsync(() ->
                    lectureService.create(new LectureCommand.Create(applicantId, lectureId))
            ));
        }

        try{
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        //then -> 30명만 들어오세요
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        assertThat(lecture.getEnrollee().size()).isEqualTo(30);
        //결과 -> 30명..
    }



}