package com.tddcleanarchi.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LectureService {
    public ResponseEntity<UserApplicationDTO> applyAndSearchAndReturnsHttpMessage(UserApplicationDTO lectureDTO) {
        return ResponseEntity.ok(lectureDTO);

    }

    public List<LectureDTO> getAvailableClasses(long userId) {
        return List.of();
    }

    public void searchLectureAllOpened() {
    }
}
