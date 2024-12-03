package com.example.swing_project;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDialog extends JDialog {

    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signUpButton;
    private JCheckBox autoLoginCheck;
    private boolean isLoggedIn = false;
    private String username;

    public LoginDialog(JFrame parentFrame) {
        super(parentFrame, "로그인", true);
        setSize(400, 300);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(255, 240, 245));

        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(new Color(255, 228, 255)); // 연한 보라 핑크 배경
        loginPanel.setLayout(new GridLayout(5, 1, 10, 10)); // 그리드 레이아웃
        loginPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 패딩 추가
        add(loginPanel, BorderLayout.CENTER);

        // ID 필드에 플레이스홀더 추가
        idField = new JTextField(10);
        addPlaceholderText(idField, "ID");

        // PASSWORD 필드에 플레이스홀더 추가
        passwordField = new JPasswordField(10);
        addPlaceholderText(passwordField, "PASSWORD");

        autoLoginCheck = new JCheckBox("자동 로그인");
        loginButton = new JButton("로그인");
        signUpButton = new JButton("회원가입");

        loginButton.setBackground(new Color(153, 102, 255));
        loginButton.setForeground(Color.WHITE);
        signUpButton.setBackground(new Color(204, 153, 255));
        signUpButton.setForeground(Color.WHITE);

        loginPanel.add(idField);
        loginPanel.add(passwordField);
        loginPanel.add(autoLoginCheck);
        loginPanel.add(loginButton);
        loginPanel.add(signUpButton);

        // 회원가입 버튼 액션 추가
        signUpButton.addActionListener(e -> {
            SignUpDialog signUpDialog = new SignUpDialog(parentFrame); // 회원가입 창 호출
            signUpDialog.setVisible(true); // 회원가입 창 띄우기
        });

        // 로그인 버튼 액션 추가
        loginButton.addActionListener(e -> {
            username = idField.getText();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "아이디와 비밀번호를 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            } else if (authenticateUser(username, password)) {
                JOptionPane.showMessageDialog(parentFrame, "로그인 성공!", "정보", JOptionPane.INFORMATION_MESSAGE);
                isLoggedIn = true; // 로그인 상태로 변경
                dispose(); // 로그인 창 닫기
            } else {
                JOptionPane.showMessageDialog(parentFrame, "로그인 실패: 아이디 또는 비밀번호가 잘못되었습니다.", "경고", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    // 플레이스홀더 추가 메소드
    private void addPlaceholderText(JTextComponent textComponent, String placeholder) {
        textComponent.setForeground(Color.GRAY);
        textComponent.setText(placeholder);

        textComponent.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textComponent.getText().equals(placeholder)) {
                    textComponent.setText("");
                    textComponent.setForeground(Color.BLACK); // 입력 시 검은색 글씨로 변경
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textComponent.getText().isEmpty()) {
                    textComponent.setForeground(Color.GRAY); // 포커스를 잃으면 다시 회색 플레이스홀더
                    textComponent.setText(placeholder);
                }
            }
        });
    }

    // 사용자 인증 메소드
    private boolean authenticateUser(String username, String password) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            if (conn != null) {
                String query = "SELECT COUNT(*) AS count FROM users WHERE username = ? AND password = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return rs.getInt("count") > 0;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "데이터베이스 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public String getUsername() {
        return username;
    }
}
