package com.tddcleanarchi.api.controller;

import com.tddcleanarchi.api.service.LectureService;
import com.tddcleanarchi.api.controller.dto.LectureSlotDTO;
import com.tddcleanarchi.api.controller.dto.LectureDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lectures")
public class LectureController {
    private final LectureService lectureService;

    public LectureController(LectureService lectureService) {
        this.lectureService = lectureService;
    }
    @GetMapping
    public ResponseEntity<List<LectureDTO>> getLectures() {
        List<LectureDTO> availableClasses = lectureService.getAvailableLectures();
        return ResponseEntity.ok(availableClasses);
    }

    @PostMapping("/apply")
    public ResponseEntity<LectureSlotDTO> apply(LectureSlotDTO lectureSlotDTO) {
        return lectureService.applyAndSearchAndReturnsHttpMessage(lectureSlotDTO);
    }
    @GetMapping("/application/{userId}")
    public ResponseEntity<ResultMessage> application(@PathVariable  long userId) {
        ResultMessage resultMessage = lectureService.isSuccessfullyApplied(userId);
        return ResponseEntity.ok(resultMessage);
    }

}
