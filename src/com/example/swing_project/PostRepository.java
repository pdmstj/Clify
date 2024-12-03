package com.example.swing_project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostRepository {

    // 글 저장 메서드
    public void savePost(String title, String content, int userId) {
        String sql = "INSERT INTO posts (title, content, user_id) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, title);
            preparedStatement.setString(2, content);
            preparedStatement.setInt(3, userId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Post saved successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
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

    // 특정 사용자가 작성한 글 목록 조회 메서드
    public List<post> getPostsByUser(int userId) {
        String sql = "SELECT * FROM posts WHERE user_id = ?";
        List<post> userPosts = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String title = resultSet.getString("title");
                    String content = resultSet.getString("content");
                    userPosts.add(new post(id, title, content));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userPosts;
    }

    // 특정 글 ID로 삭제
    public void deletePostById(int postId) {
        String sql = "DELETE FROM posts WHERE id = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, postId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Post deleted successfully with ID: " + postId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 모든 글 조회 메서드
    public List<post> getAllPosts() {
        String sql = "SELECT * FROM posts";
        List<post> allPosts = new ArrayList<>();

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String content = resultSet.getString("content");
                allPosts.add(new post(id, title, content));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allPosts;
    }
}
