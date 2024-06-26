package com.tddcleanarchi.api.controller;

import com.tddcleanarchi.api.controller.dto.LectureDTO;
import com.tddcleanarchi.api.controller.dto.LectureSlotDTO;
import com.tddcleanarchi.api.domain.Lecture;
import com.tddcleanarchi.api.domain.LectureSlot;

import java.util.stream.Collectors;

public class LectureMapper {
    public LectureDTO convertToLectureDTO(Lecture lecture) {
        return new LectureDTO(
                lecture.getLectureId(),
                lecture.getName(),
                lecture.getLectureTime(),
                lecture.getCapacity(),
                lecture.getEnrollee().size(),
                lecture.getEnrollee().stream()
                        .map(LectureSlot::getUserId)
                        .collect(Collectors.toList())
        );
    }

    public LectureSlotDTO convertToLectureSlotDto(LectureSlot application) {
        return new LectureSlotDTO(
                application.getUserId(),
                application.getLecture().getLectureId()
        );
    }
}
