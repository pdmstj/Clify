package com.example.swing_project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentRepository {

    private Connection connection; // 데이터베이스 연결 객체

    // Connection 객체를 받는 생성자 추가
    public CommentRepository(Connection connection) {
        this.connection = connection;
    }

    // 댓글 저장 메서드
    public String saveComment(int postId, String content, String author, int userId) {
        String sql = "INSERT INTO comments (post_id, content, parent_id, author, user_id, created_at) VALUES (?, ?, NULL, ?, ?, NOW())";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, postId);
            pstmt.setString(2, content);
            pstmt.setString(3, author); // 작성자의 닉네임 또는 사용자 이름을 설정
            pstmt.setInt(4, userId);
            pstmt.executeUpdate();

            // 생성된 댓글 ID 반환
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getString(1); // 생성된 ID 반환
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 실패 시 null 반환
    }

    // 대댓글 저장 메서드
    public String saveReply(String parentId, String replyContent, String author, int userId) {
        String sql = "INSERT INTO comments (post_id, content, parent_id, author, user_id, created_at) " +
                "VALUES ((SELECT post_id FROM comments WHERE id = ?), ?, ?, ?, ?, NOW())";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, parentId);
            pstmt.setString(2, replyContent);
            pstmt.setString(3, parentId);
            pstmt.setString(4, author); // 작성자의 닉네임 또는 사용자 이름을 설정
            pstmt.setInt(5, userId);
            pstmt.executeUpdate();

            // 생성된 대댓글 ID 반환
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getString(1); // 생성된 ID 반환
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 실패 시 null 반환
    }

    // 특정 게시글에 대한 댓글 불러오기 메서드
    public List<Comment> loadCommentsByPost(int postId) {
        String sql = "SELECT id, content, author, user_id, created_at FROM comments WHERE post_id = ? AND parent_id IS NULL ORDER BY created_at ASC";
        List<Comment> comments = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    comments.add(new Comment(
                            rs.getString("id"),
                            rs.getString("content"),
                            rs.getString("author"),
                            rs.getInt("user_id"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }
            System.out.println("댓글 불러오기 성공. 총 " + comments.size() + "개의 댓글");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    // 대댓글 불러오기 메서드
    public List<Comment> loadRepliesByCommentId(String commentId) {
        String sql = "SELECT id, content, author, user_id, created_at FROM comments WHERE parent_id = ? ORDER BY created_at ASC";
        List<Comment> replies = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, commentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    replies.add(new Comment(
                            rs.getString("id"),
                            rs.getString("content"),
                            rs.getString("author"),
                            rs.getInt("user_id"),
                            rs.getTimestamp("created_at")
                    ));
                }
            }
            System.out.println("대댓글 불러오기 성공. 총 " + replies.size() + "개의 대댓글");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return replies;
    }

    // 사용자 댓글 불러오기 메서드
    public List<Comment> fetchCommentsByUser(int userId) {
        String sql = "SELECT id, post_id, content, author, created_at FROM comments WHERE user_id = ? ORDER BY created_at ASC";
        List<Comment> userComments = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    userComments.add(new Comment(
                            rs.getString("id"),
                            rs.getString("content"),
                            rs.getString("author"),
                            userId,
                            rs.getTimestamp("created_at")
                    ));
                }
            }
            System.out.println("사용자 댓글 불러오기 성공. 총 " + userComments.size() + "개의 댓글");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userComments;
    }

    // 댓글 삭제 메서드
    public void deleteCommentById(String commentId) {
        String sql = "DELETE FROM comments WHERE id = ? OR parent_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, commentId);
            pstmt.setString(2, commentId);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("댓글 삭제 성공: " + commentId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
