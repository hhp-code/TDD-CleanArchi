package com.tddcleanarchi.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * ## API Specs
 *
 * 1️⃣**(핵심)** 특강 신청 **API `POST /lectures/applyAndSearchAndReturnsHttpMessage`**
 *
 * - 특정 userId 로 선착순으로 제공되는 특강을 신청하는 API 를 작성합니다.
 * - 동일한 신청자는 한 번의 수강 신청만 성공할 수 있습니다.
 * - 특강은 `4월 20일 토요일 1시` 에 열리며, 선착순 30명만 신청 가능합니다.
 * - 이미 신청자가 30명이 초과되면 이후 신청자는 요청을 실패합니다.
 * - 어떤 유저가 특강을 신청했는지 히스토리를 저장해야한다.
 *
 *
 * 1.유효한 사용자가 특강 신청을 한다.
 * 2. 특강신청을 하면 LectureService의 현재 상태를 조회하는 메서드가 실행된다.
 * 3. LectureService의 현재 상태를 조회하는 메서드는 현재 신청자 수를 반환한다.
 * 4. 현재 신청자 수가 30명 미만이면 LectureService의 특강 신청 메서드가 실행된다.
 * 5. 특강 신청 메서드는 현재 신청자 수를 1 증가시킨다.
 * 6. 특강 신청 메서드는 현재 신청자 수를 반환한다.
 * 7. 특강 신청 메서드는 특강 신청자 목록에 사용자를 추가한다.
 * 8. 특강 신청 메서드는 특강 신청 성공 메시지를 반환한다.
 * 구조
 * 신청 -> 조회 -> 발리데이션 확인 -> 저장 -> 결과반환
 *  * **2️⃣(기본)** 특강 신청 완료 여부 조회 API **`GET /lectures/application/{userId}`**
 *  *
 *  * - 특정 userId 로 특강 신청 완료 여부를 조회하는 API 를 작성합니다.
 *  * - 특강 신청에 성공한 사용자는 성공했음을, 특강 등록자 명단에 없는 사용자는 실패했음을 반환합니다. (true, false)
 *  */

class LectureServiceTest {
    LectureService lectureService = new LectureService();
    String userId;
    String lectureId;

    private LectureServiceStubTest dummyData;
    @BeforeEach
    void setUp(){
        userId = "test";
        lectureId = "testLecture";
    }

    @Test
    void 특강이_열린것들을_조회하기(){
        //given
        userId = "searchLectureAllOpened";

        //when
        lectureService.searchLectureAllOpened();

    }
    @Test
    void 특강이_열리지않은것을_조회하기_그라믄_실패(){

    }
    @Test
    void 특강을_신청하기_성공(){

    }
    @Test
    void 특강을_신청하기_실패(){

    }
    @Test
    void 특강을_신청하기_실패_특강이_없을때(){

    }
    @Test
    void 특강을_신청하기_실패_인원수가_다찼을때(){

    }

}