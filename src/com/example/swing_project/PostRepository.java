package com.example.swing_project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;

public class PostRepository {

    private Connection connection;

    // 글 저장 메서드
    public void savePost(String title, String content, int userId) {
        String sql = "INSERT INTO posts (title, content, user_id, likes) VALUES (?, ?, ?, 0)";
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

    // 생성자에서 DatabaseConnector를 사용하여 connection 초기화
    public PostRepository() {
        this.connection = DatabaseConnector.getConnection();
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
                    int likes = resultSet.getInt("likes");
                    userPosts.add(new post(id, title, content, likes));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userPosts;
    }

    // 좋아요 수 증가 메서드
    public void increaseLikeCount(int postId) {
        String sql = "UPDATE posts SET likes = likes + 1 WHERE id = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, postId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Like count updated successfully for post ID: " + postId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 좋아요 여부 확인 메서드
    public boolean hasUserLikedPost(int userId, int postId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE user_id = ? AND post_id = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, postId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // 좋아요 추가 메서드
    public void addLike(int userId, int postId) {
        String sql = "INSERT INTO likes (user_id, post_id) VALUES (?, ?)";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, postId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Like added successfully for post ID: " + postId);
                increaseLikeCount(postId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<post> getBookmarkedPostsByUser(int userId) {
        List<post> bookmarkedPosts = new ArrayList<>();
        String sql = "SELECT p.id, p.title, p.content FROM posts p " +
                "JOIN bookmarks b ON p.id = b.post_id " +
                "WHERE b.user_id = ?";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String title = resultSet.getString("title");
                    String content = resultSet.getString("content");
                    bookmarkedPosts.add(new post(id, title, content));
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bookmarkedPosts;
    }

    // 특정 사용자의 북마크를 삭제하는 메소드
    public void deleteBookmarkByUser(int userId, int postId) {
        String sql = "DELETE FROM bookmarks WHERE user_id = ? AND post_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, postId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("북마크 삭제 중 오류가 발생했습니다.");
        }
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
                int likes = resultSet.getInt("likes");
                allPosts.add(new post(id, title, content, likes));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allPosts;
    }

    // 제목으로 글 ID 조회 메서드 추가
    public int getPostIdByTitle(String title) {
        String sql = "SELECT id FROM posts WHERE title = ?";
        int postId = -1;

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, title);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    postId = resultSet.getInt("id");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return postId;
    }
}
