package com.example.swing_project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    // 데이터베이스 URL, 사용자명, 비밀번호 설정
    private static final String URL = "jdbc:mysql://localhost:3306/clifyDB"; // 데이터베이스 URL
    private static final String USER = "root"; // MySQL 사용자 이름
    private static final String PASSWORD = "111111"; // MySQL 비밀번호

    // 데이터베이스 연결 메소드
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // MySQL 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("데이터베이스 연결 성공!");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL 드라이버 로드 실패: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("데이터베이스 연결 실패: " + e.getMessage());
        }
        return connection;

    }

}
