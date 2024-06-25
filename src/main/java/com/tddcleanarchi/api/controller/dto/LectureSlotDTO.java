package com.tddcleanarchi.api.controller.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public record LectureSlotDTO(Long id,  Long lectureId) {
    @Builder
    public LectureSlotDTO {
    }
}