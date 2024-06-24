package com.tddcleanarchi.api;

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

    @PostMapping("/apply")
    public ResponseEntity<UserApplicationDTO> apply(UserApplicationDTO lectureDTO) {
        return lectureService.applyAndSearchAndReturnsHttpMessage(lectureDTO);
    }
    @GetMapping("/application/{userId}")
    public ResponseEntity<List<LectureDTO>> getLecture(@PathVariable  long userId) {
        List<LectureDTO> availableClasses = lectureService.getAvailableClasses(userId);
        return ResponseEntity.ok(availableClasses);
    }

}
