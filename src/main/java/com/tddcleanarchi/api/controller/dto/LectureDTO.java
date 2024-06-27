package com.tddcleanarchi.api.controller.dto;

import com.tddcleanarchi.api.domain.Lecture;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class LectureDTO {
    public record CreateApplyRequest(long userId, long lectureId) {
        public void validate() {
            if(userId <= 0 || lectureId <= 0) throw new IllegalArgumentException("Invalid userId or lectureId");
        }
    }

    public record CreateApplyResponse(long userId, long lectureId) {
    }

    public record CreateListResponse(List<Lecture> CreateResponseList) {
    }

    public record CreateStatusResponse(boolean applied) {
    }

    public record CreateStatusRequest(long userId) {
        public void validate() {
            if(userId <= 0) throw new IllegalArgumentException("Invalid userId");
        }
    }
}
