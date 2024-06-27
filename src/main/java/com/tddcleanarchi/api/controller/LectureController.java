package com.tddcleanarchi.api.controller;

import com.tddcleanarchi.api.controller.dto.LectureDTO;
import com.tddcleanarchi.api.controller.dto.LectureDTOMapper;
import com.tddcleanarchi.api.service.LectureService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lectures")
public class LectureController {
    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }
    @GetMapping
    public LectureDTO.CreateListResponse getLectures() {
        return LectureDTOMapper.toResponse(
                lectureService.getLectures()
        );
    }

    @PostMapping("/apply")
    public LectureDTO.CreateApplyResponse apply(@RequestBody LectureDTO.CreateApplyRequest request) {
        request.validate();
        return LectureDTOMapper.toResponse(
                lectureService.create(LectureDTOMapper.toCommand(request))
        );
    }
    @GetMapping("/application/{userId}")
    public LectureDTO.CreateStatusResponse application(@PathVariable Long userId) {
        LectureDTO.CreateStatusRequest request = new LectureDTO.CreateStatusRequest(userId);
        request.validate();
        return LectureDTOMapper.toResponse(
                lectureService.status(LectureDTOMapper.toSearch(request))
        );
    }

}
