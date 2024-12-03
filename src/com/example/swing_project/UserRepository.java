package com.example.swing_project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class UserRepository {

    // 사용자 등록 메서드
    public void registerUser(String username, String password, String email) {
        String sql = "INSERT INTO users (username, password, email, nickname) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, generateRandomNickname()); // 랜덤 닉네임 설정

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User registered successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 사용자 이름을 기반으로 사용자 ID를 가져오는 메서드
    public int getUserIdByUsername(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // 사용자 ID가 없을 경우 -1 반환
    }

    // 사용자 ID를 기반으로 닉네임을 가져오는 메서드
    public String getNicknameByUserId(int userId) {
        String sql = "SELECT nickname FROM users WHERE id = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("nickname");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 닉네임을 찾지 못할 경우 null 반환
    }

    // 사용자 ID를 기반으로 닉네임을 업데이트하는 메서드
    public void updateNicknameByUserId(int userId, String newNickname) {
        String sql = "UPDATE users SET nickname = ? WHERE id = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, newNickname);
            preparedStatement.setInt(2, userId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Nickname updated successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 랜덤 닉네임을 생성하는 메서드
    private String generateRandomNickname() {
        String[] randomNicknames = {"하늘을 달리는 호랑이", "웃는 바람", "고요한 바다", "날아오르는 새", "작은 별빛", "달리는 귤", "춤추는 펭귄"};
        Random random = new Random();
        return randomNicknames[random.nextInt(randomNicknames.length)];
    }

    // 사용자 닉네임이 중복되는지 확인하는 메서드
    public boolean isNicknameDuplicate(String nickname) {
        String sql = "SELECT COUNT(*) AS count FROM users WHERE nickname = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, nickname);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt("count") > 0) {
                    return true; // 중복되는 닉네임이 있을 경우 true 반환
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // 중복되는 닉네임이 없을 경우 false 반환
    }
}
