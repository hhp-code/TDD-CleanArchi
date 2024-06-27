package com.tddcleanarchi.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tddcleanarchi.api.domain.Lecture;
import com.tddcleanarchi.api.domain.LectureSlot;
import com.tddcleanarchi.api.service.LectureCommand;
import com.tddcleanarchi.api.service.LectureService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.HashSet;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(LectureController.class)
class LectureControllerTest {
    @MockBean
    private LectureService lectureService;

    @Autowired
    MockMvc mockMvc;

    @Test
    void 특강신청_요청_정상() throws Exception {
        // given
        Lecture dummyLecture = Lecture.builder()
                .lectureId(1L)
                .name("wow")
                .capacity(30)
                .build();
        LectureSlot lectureSlot = LectureSlot.builder()
                .userId(1L)
                .lecture(dummyLecture)
                .build();
        String Json= """
                {
                  "userId": 1,
                  "lectureId": 1
                }
                """;
        // when
        when(lectureService.create(any(LectureCommand.Create.class)))
                .thenReturn(lectureSlot);

        // then
        mockMvc.perform(post("/lectures/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Json))
                .andExpect(status().isOk())
                .andExpect(content().json(Json));

        verify(lectureService).create(any(LectureCommand.Create.class));
    }
    @Test
    void 특강_리스트조회_성공() throws Exception {
        // given
        Lecture lecture1 = new Lecture(1L, "wow", null, 30, new HashSet<>());
        Lecture lecture2 = new Lecture(2L, "wow2", null, 30, new HashSet<>());
        when(lectureService.getLectures()).thenReturn(List.of(lecture2, lecture1));

        String Json = """
                {
                  "CreateResponseList": [
                    {
                      "lectureId": 2,
                      "name": "wow2",
                      "lectureTime": null,
                      "capacity": 30,
                      "enrollee": []
                    },
                    {
                      "lectureId": 1,
                      "name": "wow",
                      "lectureTime": null,
                      "capacity": 30,
                      "enrollee": []
                    }
                  ]
                }
                """;
        // when & then
        mockMvc.perform(get("/lectures"))
                .andExpect(status().isOk())
                        .andExpect(content().json(Json));

        verify(lectureService).getLectures();
    }

    @Test
    void 특강신청후_조회처리의_성공(){
        // given
        long search = 1L;
        LectureCommand.Search searchCommand = new LectureCommand.Search(search);
        when(lectureService.status(searchCommand)).thenReturn(true);
        String Json = """
                {
                  "applied": true
                }
                """;
        // when
        try {
            mockMvc.perform(get("/lectures/application/{userId}", search))
                    .andExpect(status().isOk())
                    .andExpect(content().json(Json));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // then
        verify(lectureService).status(searchCommand);
    }
    @ParameterizedTest
    @ValueSource(longs = {-1L, 0L})
    void 신청조회에서_음수또는_영으로들어올때(long userId){
        // given
        // when & then
        try {
            mockMvc.perform(get("/lectures/application/{userId}", userId))
                    .andExpect(status().is(400))
                    .andDo(print())
                    .andExpect(content().json("{ \"message\": \"Invalid userId\" }"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void 신청조회에서_널로들어올때(){
        //given
        String userId ="";

        //when & then
        try {
            mockMvc.perform(get("/lectures/application/{userId}", userId))
                    .andExpect(status().is(400))
                    .andDo(print())
                    .andExpect(content().json("{ \"message\": \"No static resource lectures/application.\" }"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




}