##  TDD + Clean Architecture 

### API 요구사항

1. **특강 신청 API** (`POST /lectures/apply`)
  - 선착순 30명 제한
  - 중복 신청 불가
  - 신청 히스토리 저장

2. **특강 목록 API** (`GET /lectures`)
  - 날짜별 특강 조회 가능
  - 정원 30명 고정 (변동 가능성 고려)

3. **특강 신청 완료 여부 조회 API** (`GET /lectures/application/{userId}`)
  - 사용자별 신청 성공 여부 반환

### 구현 사항

1. **특강 신청 API**
  - 컨트롤러 유닛 테스트 및 MockAPI 구현
  - ReentrantLock을 활용한 동시성 문제 해결

2. **특강 목록 API**
  - 컨트롤러 유닛 테스트 및 MockAPI 구현
  - 전체 강의 조회 기능

3. **특강 신청 완료 여부 조회 API**
  - 컨트롤러 유닛 테스트 및 MockAPI 구현
  - Record Class를 활용한 요청/응답 객체 검증
  - 강의와 강의신청 도메인 분리

### 주요 기술 스택
- Spring Boot
- TDD (Test-Driven Development)
- Clean Architecture
- ReentrantLock (동시성 제어)
- Record Class (Java 16+)

### 학습 포인트
- TDD 기반 개발 프로세스
- Clean Architecture 설계
- 동시성 문제 해결 방법
- API 설계 및 구현
- 도메인 분리 및 모델링

