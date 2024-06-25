package com.tddcleanarchi.api.service;

import com.tddcleanarchi.api.controller.dto.LectureSlotDTO;
import com.tddcleanarchi.api.controller.dto.LectureDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LectureServiceStubTest {

    private final LectureDTO lectureData = LectureDTO.builder()
            .id(1L)
            .name("Easy TDD From Mr. heo")
            .lectureTime(LocalDateTime.of(2021, 7, 1, 10, 0))
            .capacity(30)
            .registered(4)
            .enrolles(List.of(1L, 2L, 3L, 4L))
            .build();

    public List<LectureSlotDTO> getAvailableLectureApplications() {
        return IntStream.range(0, lectureData.registered())
                .mapToObj(i -> LectureSlotDTO.builder()
                        .id((long) (i + 1))
                        .lectureId(lectureData.id())
                        .build())
                .collect(Collectors.toList());
    }
}