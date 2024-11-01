package com.example.swing_project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PostRepository {

    public void savePost(String title, String content, int userId) {
        String sql = "INSERT INTO posts (title, content, user_id) VALUES (?, ?, ?)";
        Connection connection = null;

        try {
            connection = DatabaseConnector.getConnection();
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
}
