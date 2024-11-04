package com.example.swing_project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class PostRepository {

    // 글 저장 메서드
    public void savePost(String title, String content, int userId) {
        String sql = "INSERT INTO posts (title, content, user_id) VALUES (?, ?, ?)";
        Connection connection = null;

        try {
            connection = DatabaseConnector.getConnection();
            if (connection != null) {
                connection.setAutoCommit(false); // 자동 커밋 비활성화

                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, title);
                    preparedStatement.setString(2, content);
                    preparedStatement.setInt(3, userId);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Post saved successfully!");
                    }

                    connection.commit(); // 트랜잭션 커밋
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback(); // 오류 시 롤백
                    System.out.println("Transaction rolled back.");
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true); // 커밋 모드 되돌리기
                    connection.close(); // 연결 닫기
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    // 특정 사용자의 모든 글 삭제 메서드
    public void deletePostsByUser(int userId) {
        String sql = "DELETE FROM posts WHERE user_id = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Posts deleted successfully for user ID: " + userId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 특정 사용자의 모든 글 조회 메서드
    public void getPostsByUser(int userId) {
        String sql = "SELECT * FROM posts WHERE user_id = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String title = resultSet.getString("title");
                    String content = resultSet.getString("content");
                    System.out.println("Title: " + title);
                    System.out.println("Content: " + content);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
