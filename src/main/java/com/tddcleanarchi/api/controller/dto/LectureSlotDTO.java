package com.tddcleanarchi.api.controller.dto;

import lombok.Builder;

public record LectureSlotDTO(Long userId, Long lectureId) {
    @Builder
    public LectureSlotDTO {
    }
}