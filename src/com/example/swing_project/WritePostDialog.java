package com.example.swing_project;

import javax.swing.*;
import java.awt.*;

public class WritePostDialog extends JDialog {

    private JTextField titleField;
    private JTextArea contentArea;
    private JButton saveButton;
    private JButton tempSaveButton;
    private JButton cancelButton;

    public WritePostDialog(JFrame parentFrame) {
        super(parentFrame, "글쓰기", true); // 모달 다이얼로그 설정
        setSize(800, 600); // 창 사이즈 조정
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());

        // 배경 색상
        getContentPane().setBackground(new Color(255, 240, 245)); // 연한 핑크 배경

        // 제목과 본문 필드 생성 - GridBagLayout 사용
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(255, 240, 245)); // 배경색 설정
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // 컴포넌트 간 여백

        // 제목 레이블
        JLabel titleLabel = new JLabel("제목");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(titleLabel, gbc);

        // 제목 입력 필드 (작은 네모박스)
        titleField = new JTextField(30); // 작은 텍스트 필드
        titleField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        titleField.setBackground(new Color(255, 255, 255));
        titleField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; // 수평으로 확장
        inputPanel.add(titleField, gbc);

        // 본문 레이블
        JLabel contentLabel = new JLabel("본문");
        contentLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(contentLabel, gbc);

        // 본문 입력 필드 (큰 네모박스)
        contentArea = new JTextArea(10, 50); // 큰 텍스트 영역
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        contentArea.setBackground(new Color(255, 255, 255));
        contentArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH; // 수평, 수직으로 확장
        gbc.weightx = 1.0; // 수평 공간 채우기
        gbc.weighty = 1.0; // 수직 공간 채우기
        inputPanel.add(new JScrollPane(contentArea), gbc);

        add(inputPanel, BorderLayout.CENTER);

        // 하단 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(255, 240, 245)); // 배경색 설정

        saveButton = new JButton("등록");
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveButton.setBackground(new Color(204, 153, 255)); // 연한 보라색
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);

        tempSaveButton = new JButton("임시저장");
        tempSaveButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        tempSaveButton.setBackground(new Color(204, 153, 255));
        tempSaveButton.setForeground(Color.WHITE);
        tempSaveButton.setFocusPainted(false);

        cancelButton = new JButton("삭제");
        cancelButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        cancelButton.setBackground(new Color(204, 153, 255)); // 연한 보라색
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        buttonPanel.add(cancelButton);
        buttonPanel.add(tempSaveButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // 버튼 동작 설정
        cancelButton.addActionListener(e -> dispose()); // 취소하면 창 닫기

        saveButton.addActionListener(e -> {
            // 등록 버튼을 클릭하면 입력된 내용 처리
            String title = titleField.getText();
            String content = contentArea.getText();

            // 제목이나 내용이 비어있을 경우 경고창 띄우기
            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "제목과 본문을 입력하세요.", "경고", JOptionPane.WARNING_MESSAGE);
            } else {
                // 메인 프레임과 연동하여 글 저장 처리
                dispose(); // 처리 후 창 닫기
            }
        });
    }

    // Getter 메서드들 (필요할 경우)
    public String getTitleText() {
        return titleField.getText();
    }

    public String getContentText() {
        return contentArea.getText();
    }
}
