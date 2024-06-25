package com.tddcleanarchi.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "lectures")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lectureId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime lectureTime;

    @Column(nullable = false)
    private int capacity;

    @OneToMany(mappedBy = "lecture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LectureSlot> enrollee = new ArrayList<>();


    public boolean isAvailableForApplication() {
        return enrollee.size() < capacity;
    }

    public void addApplication(LectureSlot application) {
        if (isAvailableForApplication()) {
            throw new IllegalStateException("강의 정원이 초과되었습니다.");
        }
        enrollee.add(application);
        application.setLecture(this);
    }
}
