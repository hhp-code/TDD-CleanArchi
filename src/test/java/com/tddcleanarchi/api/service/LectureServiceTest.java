package com.tddcleanarchi.api.service;

import com.tddcleanarchi.api.controller.ResultMessage;
import com.tddcleanarchi.api.controller.dto.LectureSlotDTO;
import com.tddcleanarchi.api.domain.Lecture;
import com.tddcleanarchi.api.repository.LectureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.util.List;

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
        LectureSlotDTO lectureSlotDTO = new LectureSlotDTO(1L, lecture.getLectureId());

        // 강의 신청
        ResponseEntity<LectureSlotDTO> response = restClient.post()
                .uri("/lectures/apply").contentType(MediaType.APPLICATION_JSON)
                .body(lectureSlotDTO)
                .retrieve().toEntity(LectureSlotDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody().userId()).isEqualTo(1L);
        assertThat(response.getBody().lectureId()).isEqualTo(lecture.getLectureId());

        // 동일 사용자 중복 신청
        try {
            restClient.post().uri("/lectures/apply").contentType(MediaType.APPLICATION_JSON)
                    .body(lectureSlotDTO)
                    .retrieve().toBodilessEntity();
            fail("Expected HttpClientErrorException.BadRequest to be thrown");
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("400 Bad Request");
        }


        // 정원 초과 테스트
        try {
            for (long i = 2; i <= 31; i++) {
                restClient.post().uri("/lectures/apply").contentType(MediaType.APPLICATION_JSON)
                        .body(new LectureSlotDTO(i, lecture.getLectureId()))
                        .retrieve().toBodilessEntity();
            }
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("400 Bad Request");
        }
    }

    @Test
    void 강의목록이_정상적으로_조회되는가() {
        // 강의 여러 개 생성 테스트에서는 3개 생성되어있습니다.
        int TestLectureCount = 3;
        // 강의 목록 조회
        ResponseEntity<List<Lecture>> response = restClient
                .get().uri("/lectures").accept(MediaType.APPLICATION_JSON).retrieve().toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(TestLectureCount, response.getBody().size());
    }

    @Test
    void 신청상태를_신청해보고_성공하는것과_실패하는것_비교() {
        //given
        String sucessMessage = "success";
        String failMessage = "fail";

        // 강의 신청
        Lecture lecture = lectureRepository.findById(2L).orElseThrow();
        restClient.post().uri("/lectures/apply").contentType(MediaType.APPLICATION_JSON)
                .body(new LectureSlotDTO(2L, lecture.getLectureId()))
                .retrieve().toEntity(LectureSlotDTO.class);

        // 신청 상태 확인
        ResponseEntity<ResultMessage> acceptedResponse = restClient.get()
                .uri("/lectures/application/2").retrieve()
                .toEntity(ResultMessage.class);

        assertEquals(HttpStatus.OK, acceptedResponse.getStatusCode());
        assertEquals(sucessMessage, acceptedResponse.getBody().status());

        // 미신청 사용자 상태 확인
        ResponseEntity<ResultMessage>  deniedResponse = restClient.get()
                .uri("/lectures/application/3").retrieve()
                .toEntity(ResultMessage.class);

        assertEquals(HttpStatus.OK, deniedResponse.getStatusCode());
        assertEquals(failMessage, deniedResponse.getBody().status());
    }
}