package com.example.swing_project;

public class post {
    private int id;
    private String title;
    private String content;
    private int likes; // 좋아요 수를 저장하는 필드 추가

    // 네 개의 매개변수를 받는 생성자
    public post(int id, String title, String content, int likes) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.likes = likes;
    }

    // 세 개의 매개변수를 받는 생성자 (id는 초기값으로 설정)
    public post(String title, String content, int likes) {
        this.title = title;
        this.content = content;
        this.likes = likes;
        this.id = -1; // 임시 ID 값으로 설정 (데이터베이스에서 생성된 후 설정 가능)
    }

    // 제목을 반환하는 메소드
    public String getTitle() {
        return title;
    }

    // 내용을 반환하는 메소드
    public String getContent() {
        return content;
    }

    // ID를 반환하는 메소드
    public int getId() {
        return id;
    }

    // 좋아요 수를 반환하는 메소드
    public int getLikes() {
        return likes;
    }

    // 제목을 설정하는 메소드
    public void setTitle(String title) {
        this.title = title;
    }

    // 내용을 설정하는 메소드
    public void setContent(String content) {
        this.content = content;
    }

    // 좋아요 수를 설정하는 메소드
    public void setLikes(int likes) {
        this.likes = likes;
    }

    // 좋아요 수를 증가시키는 메소드
    public void increaseLikes() {
        this.likes++;
    }
}
