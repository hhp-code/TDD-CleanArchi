package com.tddcleanarchi.api.controller.dto;

import com.tddcleanarchi.domain.Lecture;
import com.tddcleanarchi.domain.LectureSlot;
import com.tddcleanarchi.domain.service.LectureCommand;

import java.util.List;

public class LectureDTOMapper {
    public static LectureCommand.Create toCommand(LectureDTO.CreateApplyRequest request) {
        return new LectureCommand.Create(request.userId(), request.lectureId());
    }
    public static LectureCommand.Search toSearch(LectureDTO.CreateStatusRequest request) {
        return new LectureCommand.Search(request.userId());
    }
    public static LectureDTO.CreateApplyResponse toResponse(LectureSlot lectureSlot) {
        return new LectureDTO.CreateApplyResponse(lectureSlot.getUserId(), lectureSlot.getLecture().getLectureId());
    }

    public static LectureDTO.CreateListResponse toResponse(List<Lecture> lecture){
        return new LectureDTO.CreateListResponse(lecture);
    }
    public static LectureDTO.CreateStatusResponse toResponse(boolean applied) {
        return new LectureDTO.CreateStatusResponse(applied);
    }


}
