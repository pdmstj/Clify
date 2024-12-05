package com.example.swing_project;

import java.sql.Timestamp;

public class Comment {
    private String id;          // 댓글 ID
    private String content;     // 댓글 내용
    private String author;      // 작성자 (닉네임)
    private int userId;         // 작성자 ID
    private Timestamp createdAt; // 댓글 작성 시간

    // 생성자
    public Comment(String id, String content, String author, int userId, Timestamp createdAt) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    // Getter 및 Setter 메서드
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // toString 메서드 - UI 또는 로그 출력 시 가독성 향상
    @Override
    public String toString() {
        return String.format("%s - %s (%s)", content, author, createdAt.toString());
    }
}
