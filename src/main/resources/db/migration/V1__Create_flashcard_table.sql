CREATE TABLE flashcard (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    front_content VARCHAR(255) NOT NULL,
    back_content VARCHAR(255) NOT NULL,
    folder_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- サンプルデータの挿入
INSERT INTO flashcard (front_content, back_content) VALUES
('What is Spring Boot?', 'Spring Bootは、Springフレームワークのアプリケーション開発を簡素化するためのフレームワークです。'),
('What is JPA?', 'JPAは、Javaアプリケーションでリレーショナルデータを管理するためのAPI仕様です。'); 