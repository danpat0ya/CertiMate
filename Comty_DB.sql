-- 1. 데이터베이스 통째로 삭제 및 다시 생성
DROP DATABASE IF EXISTS CertiMateDB;
CREATE DATABASE CertiMateDB DEFAULT CHARACTER SET utf8mb4;
USE CertiMateDB;

-- 2. 임시 USER 테이블
CREATE TABLE IF NOT EXISTS USER (
    user_id INT AUTO_INCREMENT PRIMARY KEY, -- 기본키(PK) 및 자동 증가 설정
    email VARCHAR(255),
    name VARCHAR(100),
    major VARCHAR(100),
    phone_number VARCHAR(20),
    kakao_id VARCHAR(100),
    interest VARCHAR(255),
    noti_consent BOOLEAN
);

-- 3. CertiMate Community DB Schema
CREATE TABLE IF NOT EXISTS community_post (
    post_id INT AUTO_INCREMENT PRIMARY KEY,                  -- 고유 식별자 (PK)
    category VARCHAR(50) NOT NULL DEFAULT '자유게시판',        -- 분류 (자유게시판, 궁금해요, 취업·면접후기, 과외/스터디 모집)
    nickname INT NULL,                                       -- 작성 유저 ID (INT, FK -> USER.user_id)
    title VARCHAR(255) NOT NULL,                             -- 제목
    content TEXT NOT NULL,                                   -- 내용
    image_url VARCHAR(500) NULL,                             -- 첨부 사진 경로/URL
    views INT DEFAULT 0,                                     -- 조회수
    recommendations INT DEFAULT 0,                           -- 추천수
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 작성일시
    user_id INT NULL,                                        -- 작성 유저 외래키 (선택)
    FOREIGN KEY (nickname) REFERENCES USER(user_id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES USER(user_id) ON DELETE SET NULL
);

-- 4. 댓글 테이블 (comments)
CREATE TABLE IF NOT EXISTS comments (
    comment_id INT AUTO_INCREMENT PRIMARY KEY,               -- 댓글 고유 식별자 (PK)
    post_id INT NOT NULL,                                    -- 게시글 외래키 (FK)
    writer VARCHAR(50) NOT NULL DEFAULT 'NV_51630***',       -- 댓글 작성자 별명
    content TEXT NOT NULL,                                   -- 댓글 내용
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 댓글 작성일시
    user_id INT NULL,                                        -- 작성 유저 외래키 (선택)
    FOREIGN KEY (post_id) REFERENCES community_post(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES USER(user_id) ON DELETE SET NULL
);
