-- 테스트용 users 테이블 생성
CREATE TABLE IF NOT EXISTS users (
                                     user_id INT AUTO_INCREMENT PRIMARY KEY,
                                     email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100) UNIQUE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fcm_token VARCHAR(255),
    name VARCHAR(30) NOT NULL,
    phone_number VARCHAR(30) NOT NULL
    );
