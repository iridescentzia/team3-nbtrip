-- 테스트용 초기 데이터 삽입
INSERT INTO users (user_id, email, password, nickname, name, phone_number, fcm_token) VALUES
            (1, 'test@example.com', 'encodedPassword123', 'testuser', '테스트유저', '010-1234-5678', 'test_fcm_token'),
            (2, 'existing@example.com', 'encodedPassword456', 'existinguser', '기존유저', '010-9876-5432', 'existing_fcm_token');
