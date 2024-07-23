package com.tddcleanarchi.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lecture_slots")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LectureSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id")
    private Long userId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    public LectureSlot(Long userId, Lecture lecture) {
        this.userId = userId;
        this.lecture = lecture;
    }

}