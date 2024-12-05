package com.example.swing_project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentRepository {

    // 댓글 저장 메서드
    public void saveComment(String comment, String parentId, String author, int userId) {
        String sql = "INSERT INTO comments (content, parent_id, author, user_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, comment);
            if (parentId == null) {
                pstmt.setNull(2, Types.VARCHAR);
            } else {
                pstmt.setString(2, parentId);
            }
            pstmt.setString(3, author);
            pstmt.setInt(4, userId);
            pstmt.executeUpdate();
            System.out.println("댓글 저장 성공: " + comment);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 댓글 불러오기 메서드
    public List<Comment> loadComments(String parentId) {
        String sql;
        if (parentId == null) {
            sql = "SELECT id, content, author, user_id FROM comments WHERE parent_id IS NULL";
        } else {
            sql = "SELECT id, content, author, user_id FROM comments WHERE parent_id = ?";
        }
        List<Comment> comments = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (parentId != null) {
                pstmt.setString(1, parentId);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                comments.add(new Comment(
                        rs.getString("id"),
                        rs.getString("content"),
                        rs.getString("author"),
                        rs.getInt("user_id")
                ));
            }
            System.out.println("댓글 불러오기 성공. 총 " + comments.size() + "개의 댓글");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    // 사용자 댓글 필터링 메서드
    public List<Comment> fetchCommentsByUser(int userId) {
        String sql = "SELECT id, content, author FROM comments WHERE user_id = ?";
        List<Comment> userComments = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                userComments.add(new Comment(
                        rs.getString("id"),
                        rs.getString("content"),
                        rs.getString("author"),
                        userId
                ));
            }
            System.out.println("사용자 댓글 불러오기 성공. 총 " + userComments.size() + "개의 댓글");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userComments;
    }
}
