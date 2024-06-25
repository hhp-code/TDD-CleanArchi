package com.tddcleanarchi.api.controller;

import lombok.Builder;

import java.time.LocalDateTime;

public record ResultMessage(String lectureName, LocalDateTime lectureTime, String status, String message) {
    @Builder
    public ResultMessage {
    }

}