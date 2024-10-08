package com.tddcleanarchi.domain.service;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LectureCommand {
    public record Create(long userId, long lectureId) { }
    public record Search(long userId) { }
}
