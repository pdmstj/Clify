package com.example.swing_project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LikeRepository {

    private Connection connection;

    public LikeRepository(Connection connection) {
        this.connection = connection;
    }

    // 좋아요 추가 메서드
    public boolean addLike(int postId, int userId) {
        String sql = "INSERT INTO likes (post_id, user_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            // 이미 좋아요를 눌렀을 경우 UNIQUE 제약조건에 의해 오류가 발생할 수 있음
            if (e.getErrorCode() == 1062) {
                System.out.println("이미 좋아요를 누른 게시글입니다.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    // 특정 사용자가 특정 게시글에 좋아요를 눌렀는지 확인하는 메서드
    public boolean hasLiked(int postId, int userId) {
        String sql = "SELECT * FROM likes WHERE post_id = ? AND user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            pstmt.setInt(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
