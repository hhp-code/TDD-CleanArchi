CREATE TABLE IF NOT EXISTS lectures
(
    lecture_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    lecture_time TIMESTAMP    NOT NULL,
    version      BIGINT DEFAULT 0,
    capacity     INT          NOT NULL
);

CREATE TABLE IF NOT EXISTS lecture_slots
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT,
    lecture_id BIGINT,
    FOREIGN KEY (lecture_id) REFERENCES lectures (lecture_id)
);
INSERT INTO lectures (name, lecture_time, capacity)
VALUES ('Spring Boot 기초', '2024-07-01 10:00:00', 30),
       ('JPA 심화 과정', '2024-07-02 14:00:00', 30),
       ('RESTful API 설계', '2024-07-03 09:00:00', 30),
       ('TDD Is NOT FOR YOU', '2024-07-04 13:00:00', 30);

INSERT INTO lecture_slots (user_id, lecture_id)
VALUES (101, 1),
       (102, 1),
       (103, 1),
       (104, 2),
       (105, 2),
       (106, 3),
       (107, 3),
       (108, 3);