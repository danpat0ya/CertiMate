-- CertiMate Community DB Schema
CREATE TABLE IF NOT EXISTS community_post (
    post_id INT AUTO_INCREMENT PRIMARY KEY,                  -- 고유 식별자 (PK)
    category VARCHAR(50) NOT NULL DEFAULT '자유게시판',        -- 분류 (자유게시판, 궁금해요, 취업·면접후기, 과외/스터디 모집)
    nickname VARCHAR(50) NOT NULL DEFAULT 'NV_51630***',      -- 작성자 별명
    title VARCHAR(255) NOT NULL,                             -- 제목
    content TEXT NOT NULL,                                   -- 내용
    image_url VARCHAR(500) NULL,                             -- 첨부 사진 경로/URL
    views INT DEFAULT 0,                                     -- 조회수
    recommendations INT DEFAULT 0,                           -- 추천수
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 작성일시
    user_id INT NULL,                                        -- 작성 유저 외래키 (선택)
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE SET NULL
);

-- 댓글 테이블 (comments)
CREATE TABLE IF NOT EXISTS comments (
    comment_id INT AUTO_INCREMENT PRIMARY KEY,               -- 댓글 고유 식별자 (PK)
    post_id INT NOT NULL,                                    -- 게시글 외래키 (FK)
    writer VARCHAR(50) NOT NULL DEFAULT 'NV_51630***',       -- 댓글 작성자 별명
    content TEXT NOT NULL,                                   -- 댓글 내용
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 댓글 작성일시
    user_id INT NULL,                                        -- 작성 유저 외래키 (선택)
    FOREIGN KEY (post_id) REFERENCES community_post(post_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE SET NULL
);

-- 1. 데이터베이스 통째로 삭제
DROP DATABASE IF EXISTS CertiMateDB;
-- 2. 데이터베이스 다시 생성
CREATE DATABASE CertiMateDB DEFAULT CHARACTER SET utf8mb4;
-- 3. 다시 사용할 DB 선택
USE CertiMateDB;