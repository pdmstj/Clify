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
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class SignUpDialog extends JDialog {

    private boolean isPasswordVisible = false; // 비밀번호 표시 상태

    public SignUpDialog(JFrame parentFrame) {
        super(parentFrame, "회원가입", true); // 모달 다이얼로그 설정
        setSize(600, 500); // 창 크기 설정
        setLocationRelativeTo(parentFrame); // 부모 창 중앙에 배치
        setLayout(new BorderLayout());

        // 배경 색상 설정
        getContentPane().setBackground(new Color(229, 204, 255)); // 연한 보라색 배경

        // 상단 레이블 (가입정보)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(229, 204, 255)); // 배경색 통일
        JLabel headerLabel = new JLabel("가입정보");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLabel.setHorizontalAlignment(SwingConstants.LEFT);
        headerLabel.setForeground(new Color(153, 102, 255)); // 보라색 글씨
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 여백 추가
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // 입력 필드 패널
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10)); // 4행 2열 그리드
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));
        inputPanel.setBackground(new Color(255, 240, 255)); // 연한 보라 핑크 배경

        // 아이디 입력
        JLabel idLabel = new JLabel("아이디:");
        idLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        idLabel.setForeground(new Color(102, 51, 153)); // 진한 보라색
        JTextField idField = new JTextField(20);
        idField.setFont(new Font("SansSerif", Font.PLAIN, 14)); // 글씨 크기 조정
        addPlaceholderText(idField, "3~10글자 이내로 입력해주세요");

        // 이메일 입력
        JLabel emailLabel = new JLabel("이메일:");
        emailLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        emailLabel.setForeground(new Color(102, 51, 153)); // 진한 보라색
        JTextField emailField = new JTextField(20);
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 14)); // 글씨 크기 조정
        addPlaceholderText(emailField, "이메일 주소를 입력해주세요");

        // 비밀번호 입력
        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        passwordLabel.setForeground(new Color(102, 51, 153)); // 진한 보라색
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14)); // 글씨 크기 조정
        addPasswordPlaceholder(passwordField, "6~20글자 이내로 입력해주세요");

        // 눈 모양 버튼 추가 (비밀번호 표시/숨기기 기능)
        JButton showPasswordButton = new JButton("👁");
        showPasswordButton.setFocusPainted(false);
        showPasswordButton.setPreferredSize(new Dimension(40, 20));

        showPasswordButton.addActionListener(e -> {
            if (isPasswordVisible) {
                passwordField.setEchoChar('●'); // 비밀번호 숨기기
                isPasswordVisible = false;
            } else {
                passwordField.setEchoChar((char) 0); // 비밀번호 보이기
                isPasswordVisible = true;
            }
        });

        JPanel passwordPanel = new JPanel(new BorderLayout());
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        passwordPanel.add(showPasswordButton, BorderLayout.EAST);

        // 비밀번호 확인 입력
        JLabel confirmPasswordLabel = new JLabel("비밀번호 확인:");
        confirmPasswordLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        confirmPasswordLabel.setForeground(new Color(102, 51, 153)); // 진한 보라색
        JPasswordField confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 14)); // 글씨 크기 조정
        addPasswordPlaceholder(confirmPasswordField, "비밀번호 확인");

        inputPanel.add(idLabel);
        inputPanel.add(idField);
        inputPanel.add(emailLabel);
        inputPanel.add(emailField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordPanel); // 비밀번호 패널에 눈 모양 버튼 포함
        inputPanel.add(confirmPasswordLabel);
        inputPanel.add(confirmPasswordField);

        add(inputPanel, BorderLayout.CENTER);

        // 하단 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        buttonPanel.setBackground(new Color(229, 204, 255)); // 배경색 설정

        JButton signUpButton = new JButton("회원가입");
        signUpButton.setBackground(new Color(153, 102, 255));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFocusPainted(false);
        signUpButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        JButton cancelButton = new JButton("취소");
        cancelButton.setBackground(new Color(153, 102, 255));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        // 회원가입 버튼 액션
        signUpButton.addActionListener(e -> {
            String id = idField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (id.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 필드를 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            } else if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(this, "유효한 이메일 주소를 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            } else if (isUsernameTaken(id)) {
                JOptionPane.showMessageDialog(this, "이미 존재하는 아이디입니다.", "경고", JOptionPane.WARNING_MESSAGE);
            } else if (isEmailTaken(email)) {
                JOptionPane.showMessageDialog(this, "이미 존재하는 이메일입니다.", "경고", JOptionPane.WARNING_MESSAGE);
            } else if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "비밀번호와 비밀번호 확인이 일치하지 않습니다.", "경고", JOptionPane.WARNING_MESSAGE);
            } else if (!isValidPassword(password)) {
                JOptionPane.showMessageDialog(this, "비밀번호는 특수문자, 숫자, 영어를 포함하고 6글자 이상이어야 합니다.", "경고", JOptionPane.WARNING_MESSAGE);
            } else {
                saveUserToDatabase(id, email, password);
                JOptionPane.showMessageDialog(this, "회원가입 완료!", "정보", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // 성공 시 창 닫기
            }
        });

        // 취소 버튼 액션
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(signUpButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // 아이디 중복 확인 메서드
    private boolean isUsernameTaken(String id) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            if (conn != null) {
                String query = "SELECT COUNT(*) AS count FROM users WHERE username = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, id);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return rs.getInt("count") > 0;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터베이스 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // 이메일 중복 확인 메서드
    private boolean isEmailTaken(String email) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            if (conn != null) {
                String query = "SELECT COUNT(*) AS count FROM users WHERE email = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, email);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return rs.getInt("count") > 0;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터베이스 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // 사용자 정보 데이터베이스에 저장하는 메서드
    private void saveUserToDatabase(String id, String email, String password) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            if (conn != null) {
                String query = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, id);
                    stmt.setString(2, email);
                    stmt.setString(3, password);
                    stmt.executeUpdate();
                    System.out.println("사용자 정보 저장 성공!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "데이터베이스 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 이메일 검증 메서드 (@ 포함 여부)
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // 비밀번호 검증 메서드 (특수문자, 숫자, 영어 포함 여부, 6글자 이상)
    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,20}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    // 플레이스홀더 추가 메소드 (일반 텍스트 필드용)
    public static void addPlaceholderText(JTextComponent textComponent, String placeholder) {
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

    // 비밀번호 필드용 플레이스홀더 메서드
    public static void addPasswordPlaceholder(JPasswordField passwordField, String placeholder) {
        // 기본적으로 플레이스홀더 표시
        passwordField.setEchoChar((char) 0);
        passwordField.setForeground(Color.GRAY);
        passwordField.setText(placeholder);

        // 포커스를 얻었을 때 플레이스홀더를 제거하고 입력을 받도록 설정
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(passwordField.getPassword()).equals(placeholder)) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                    passwordField.setEchoChar('●'); // 입력을 동그라미로 변환
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (passwordField.getPassword().length == 0) {
                    passwordField.setForeground(Color.GRAY);
                    passwordField.setText(placeholder);
                    passwordField.setEchoChar((char) 0); // 플레이스홀더 상태일 때 일반 텍스트로 표시
                }
            }
        });
    }
}