package com.tddcleanarchi.api.service;

import com.tddcleanarchi.api.controller.dto.LectureSlotDTO;
import com.tddcleanarchi.api.domain.Lecture;
import com.tddcleanarchi.api.repository.LectureRepository;
import com.tddcleanarchi.api.repository.LectureSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LectureServiceTest {

    private RestClient restClient;


    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private LectureSlotRepository lectureSlotRepository;

    @BeforeEach
    void setUp() {
        String baseUrl = "http://localhost:" + port;
        lectureRepository.deleteAll();
        lectureSlotRepository.deleteAll();
        restClient = RestClient.create(baseUrl);
    }

    @Test
    void testApplyForLecture() {
        // 강의 생성
        Lecture lecture = Lecture.builder().name("Test Lecture").lectureTime(LocalDateTime.now().plusDays(1)).capacity(30).build();
        lectureRepository.save(lecture);

        LectureSlotDTO lectureSlotDTO = new LectureSlotDTO(1L, lecture.getLectureId());


        // 강의 신청
        ResponseEntity<LectureSlotDTO> response =restClient.post().uri("/lectures/apply").contentType(MediaType.APPLICATION_JSON)
                .body(lectureSlotDTO)
                .retrieve().toEntity(LectureSlotDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody().id()).isEqualTo(1L);
        assertThat(response.getBody().lectureId()).isEqualTo(lecture.getLectureId());

        // 동일 사용자 중복 신청
        response =restClient.post().uri("/lectures/apply").contentType(MediaType.APPLICATION_JSON)
                .body(lectureSlotDTO)
                .retrieve().toEntity(LectureSlotDTO.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        // 정원 초과 테스트
        for (long i = 2; i <= 31; i++) {
            response = restClient.post().uri("/lectures/apply").contentType(MediaType.APPLICATION_JSON)
                    .body(new LectureSlotDTO(i, lecture.getLectureId()))
                    .retrieve().toEntity(LectureSlotDTO.class);
        }
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertThat(response.getBody()).isNull();
    }

    @Test
    void testGetLectures() {
        // 강의 여러 개 생성
        Lecture lecture1 = Lecture.builder().name("Lecture 1").lectureTime(LocalDateTime.now().plusDays(1)).capacity(30).build();
        Lecture lecture2 = Lecture.builder().name("Lecture 2").lectureTime(LocalDateTime.now().plusDays(2)).capacity(30).build();
        lectureRepository.saveAll(List.of(lecture1, lecture2));

        // 강의 목록 조회
        ResponseEntity<List<Lecture>> response = restClient
                .get().uri("/lectures").accept(MediaType.APPLICATION_JSON).retrieve().toEntity(new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testCheckApplicationStatus() {
        // 강의 생성 및 신청
        Lecture lecture = Lecture.builder().name("Test Lecture").lectureTime(LocalDateTime.now().plusDays(1)).capacity(30).build();
        lectureRepository.save(lecture);
        restClient.post().uri("/lectures/apply").contentType(MediaType.APPLICATION_JSON)
                .body(new LectureSlotDTO(1L, lecture.getLectureId()))
                .retrieve().toEntity(LectureSlotDTO.class);

        // 신청 상태 확인
        ResponseEntity<Boolean> response = restTemplate.getForEntity("/lectures/application/1", Boolean.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.TRUE, response.getBody());

        // 미신청 사용자 상태 확인
        response = restTemplate.getForEntity("/lectures/application/2", Boolean.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotEquals(Boolean.TRUE, response.getBody());
    }
}