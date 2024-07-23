package com.tddcleanarchi.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "lectures")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @Builder.Default
    private Set<LectureSlot> enrollee = new HashSet<>();

    public Lecture(Long lectureId, String name, LocalDateTime localDateTime, int capacity, Set<LectureSlot> enrollee) {
        this.lectureId = lectureId;
        this.name = name;
        this.lectureTime = localDateTime;
        this.capacity = capacity;
        this.enrollee = enrollee;
    }

    public void addEnrollee(LectureSlot lectureSlot) {
        enrollee.add(lectureSlot);
        lectureSlot.setLecture(this);
    }

}
