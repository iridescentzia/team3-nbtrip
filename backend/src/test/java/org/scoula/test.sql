USE nbtrip;

SELECT * FROM trip_member tm
                  JOIN trip t ON tm.trip_id = t.trip_id
WHERE t.trip_status = 'CLOSED';

-- 추가 예시
INSERT INTO trip (owner_id, trip_name, start_date, end_date, budget, trip_status)
VALUES (2, '서울 가을 여행', '2025-09-01', '2025-09-03', 1200000, 'READY');

-- trip_id=4로 가정하여 멤버로 추가
INSERT INTO trip_member (user_id, trip_id, member_status) VALUES
    (2, 4, 'JOINED');

SELECT * FROM trip_member WHERE user_id = 5;

SELECT tm.user_id, tm.trip_id, tm.member_status, t.trip_status, t.trip_name
FROM trip_member tm
         JOIN trip t ON tm.trip_id = t.trip_id
WHERE tm.user_id = 5;