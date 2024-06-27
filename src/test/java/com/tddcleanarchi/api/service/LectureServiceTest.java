package com.tddcleanarchi.api.service;

import com.tddcleanarchi.api.domain.Lecture;
import com.tddcleanarchi.api.service.repository.LectureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LectureServiceTest {

    private RestClient restClient;

    @LocalServerPort
    private int port;

    @Autowired
    private LectureRepository lectureRepository;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port;
        restClient = RestClient.create(baseUrl);
    }

    @Test
    void 강의신청_및_예외검증_시나리오() {
        // 강의 생성
        Lecture lecture = lectureRepository.findById(1L).orElseThrow();
        LectureCommand.Create lectureSlotDTO = new LectureCommand.Create(1L, lecture.getLectureId());

        // 강의 신청
        ResponseEntity<LectureCommand.Create> response = restClient.post()
                .uri("/lectures/apply").contentType(MediaType.APPLICATION_JSON)
                .body(lectureSlotDTO)
                .retrieve().toEntity(LectureCommand.Create.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody().userId()).isEqualTo(1L);
        assertThat(response.getBody().lectureId()).isEqualTo(lecture.getLectureId());

        // 동일 사용자 중복 신청
        try {
            restClient.post().uri("/lectures/apply").contentType(MediaType.APPLICATION_JSON)
                    .body(lectureSlotDTO)
                    .retrieve().toBodilessEntity();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertThat(e.getMessage()).contains("404 Not Found");
        }

        // 정원 초과 테스트
        try {
            for (long i = 2; i <= 31; i++) {
                restClient.post().uri("/lectures/apply").contentType(MediaType.APPLICATION_JSON)
                        .body(new LectureCommand.Create(i, lecture.getLectureId()))
                        .retrieve().toBodilessEntity();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertThat(e.getMessage()).contains("404 Not Found");
        }
    }

    @Test
    void 강의목록이_정상적으로_조회되는가() {
        //given
        lectureRepository.deleteAll();
        lectureRepository.save(new Lecture(1L, "강의1", LocalDateTime.now(), 30, null));
        lectureRepository.save(new Lecture(2L, "강의2", LocalDateTime.now(), 30, null));
        // 강의 목록 조회
        //when
        String response = restClient
                .get().uri("/lectures").retrieve().body(String.class);
        System.out.println(response);
        //then
        assertThat(response).contains("강의1");
        assertThat(response).contains("강의2");
    }


}