package com.tddcleanarchi.api.controller.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public record LectureDTO(Long id, String name, LocalDateTime lectureTime, int capacity, int registered,
                         List<Long> enrolles) {
    @Builder
    public LectureDTO {
    }
}