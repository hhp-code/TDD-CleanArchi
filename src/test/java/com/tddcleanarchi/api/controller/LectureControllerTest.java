package com.tddcleanarchi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tddcleanarchi.api.controller.dto.LectureDTO;
import com.tddcleanarchi.api.controller.dto.LectureSlotDTO;
import com.tddcleanarchi.api.service.LectureService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * - **ERD 작성**
 * - **특강 신청 API**
 * - **특강 신청 완료 여부 조회 API**
 */

/**
 * ## API Specs
 *
 * 1️⃣**(핵심)** 특강 신청 **API `POST /lectures/applyAndSearchAndReturnsHttpMessage`**
 *
 * - 특정 userId 로 선착순으로 제공되는 특강을 신청하는 API 를 작성합니다.
 * - 동일한 신청자는 한 번의 수강 신청만 성공할 수 있습니다.
 * - 특강은 `4월 20일 토요일 1시` 에 열리며, 선착순 30명만 신청 가능합니다.
 * - 이미 신청자가 30명이 초과되면 이후 신청자는 요청을 실패합니다.
 * - 어떤 유저가 특강을 신청했는지 히스토리를 저장해야한다.
 *
 *
 * 1.유효한 사용자가 특강 신청을 한다.
 * 2. 특강신청을 하면 LectureService의 현재 상태를 조회하는 메서드가 실행된다.
 * 3. LectureService의 현재 상태를 조회하는 메서드는 현재 신청자 수를 반환한다.
 * 4. 현재 신청자 수가 30명 미만이면 LectureService의 특강 신청 메서드가 실행된다.
 * 5. 특강 신청 메서드는 현재 신청자 수를 1 증가시킨다.
 * 6. 특강 신청 메서드는 현재 신청자 수를 반환한다.
 * 7. 특강 신청 메서드는 특강 신청자 목록에 사용자를 추가한다.
 * 8. 특강 신청 메서드는 특강 신청 성공 메시지를 반환한다.
 * 구조
 * 신청 -> 조회 -> 발리데이션 확인 -> 저장 -> 결과반환
 *
 * **2️⃣(기본)** 특강 신청 완료 여부 조회 API **`GET /lectures/application/{userId}`**
 *
 * - 특정 userId 로 특강 신청 완료 여부를 조회하는 API 를 작성합니다.
 * - 특강 신청에 성공한 사용자는 성공했음을, 특강 등록자 명단에 없는 사용자는 실패했음을 반환합니다. (true, false)
 */
@ExtendWith(MockitoExtension.class)
@WebMvcTest(LectureController.class)
class LectureControllerTest {
    @MockBean
    private LectureService lectureService;

    @Autowired
    MockMvc mockMvc;

    long userId =1L;
    String lectureName ="특강";
    @Test
    void 특강신청_요청처리의_정상() throws Exception {
        // given
        LectureSlotDTO lectureDTO = LectureSlotDTO.builder()
                .id(userId)
                .lectureId(1L)
                .build();
        // when
        when(lectureService.applyAndSearchAndReturnsHttpMessage(any(LectureSlotDTO.class)))
                .thenReturn(ResponseEntity.ok(lectureDTO));

        // then
        mockMvc.perform(post("/lectures/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(lectureDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(lectureDTO)));

        verify(lectureService).applyAndSearchAndReturnsHttpMessage(any(LectureSlotDTO.class));
    }
    @Test
    void 특강신청_조회처리의_정상(){
        // given
        //when 신청 가능한 특강이 있을때
        List<LectureDTO> somethingList = List.of();
        when(lectureService.getAvailableLectures()).thenReturn(somethingList);
        try {
            mockMvc.perform(get("/lectures/application/{userId}", userId))
                    .andExpect(status().isOk())
                    .andExpect(content().json(new ObjectMapper().writeValueAsString(somethingList)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        verify(lectureService).getAvailableLectures();

    }




}