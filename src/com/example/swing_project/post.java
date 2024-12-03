package com.example.swing_project;

public class post {
    private int id;
    private String title;
    private String content;

    // 세 개의 매개변수를 받는 생성자
    public post(int id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    // 두 개의 매개변수를 받는 생성자 (id는 초기값으로 설정)
    public post(String title, String content) {
        this.title = title;
        this.content = content;
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

    // 제목을 설정하는 메소드
    public void setTitle(String title) {
        this.title = title;
    }

    // 내용을 설정하는 메소드
    public void setContent(String content) {
        this.content = content;
    }
}
