package com.example.swing_project;

public class Comment {
    private String id;
    private String content;
    private String author;
    private int userId; // userId 필드 추가

    public Comment(String id, String content, String author, int userId) {
        this.id = id;
        this.content = content;
        this.author = author;
        this.userId = userId; // userId 초기화
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getAuthor() {
        return author;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return content + " - " + author;
    }
}
