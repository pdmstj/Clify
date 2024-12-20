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
import java.util.HashMap;
import java.util.Map;

public class WritePostDialog extends JDialog {

    private JTextField titleField;
    private JTextArea contentArea;
    private JButton saveButton;
    private JButton tempSaveButton;
    private JButton deleteButton;

    private DefaultListModel<String> postListModel;
    private static Map<Integer, Map<String, String>> tempStorage = new HashMap<>();
    private int userId;

    public WritePostDialog(JFrame parentFrame, DefaultListModel<String> postListModel, int userId) {
        super(parentFrame, "글쓰기", true);
        this.postListModel = postListModel;
        this.userId = userId;
        setSize(800, 600);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());

        getContentPane().setBackground(new Color(255, 240, 245));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(255, 240, 245));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("제목");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(titleLabel, gbc);

        titleField = new JTextField(30);
        titleField.setFont(new Font("SansSerif", Font.PLAIN, 16));
        titleField.setBackground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(titleField, gbc);

        JLabel contentLabel = new JLabel("본문");
        contentLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(contentLabel, gbc);

        contentArea = new JTextArea(10, 50);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        contentArea.setBackground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        inputPanel.add(new JScrollPane(contentArea), gbc);

        add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(255, 240, 245));

        saveButton = new JButton("등록");
        saveButton.setBackground(new Color(204, 153, 255));
        saveButton.setForeground(Color.WHITE);

        tempSaveButton = new JButton("임시저장");
        tempSaveButton.setBackground(new Color(204, 153, 255));
        tempSaveButton.setForeground(Color.WHITE);

        deleteButton = new JButton("삭제");
        deleteButton.setBackground(new Color(204, 153, 255));
        deleteButton.setForeground(Color.WHITE);

        buttonPanel.add(deleteButton);
        buttonPanel.add(tempSaveButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);

        deleteButton.addActionListener(e -> {
            String title = titleField.getText();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "삭제할 글이 없습니다.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int postId = getPostIdByTitle(title); // 해당 글의 ID를 가져오는 메서드 필요
            if (postId != -1) {
                PostRepository postRepository = new PostRepository();
                postRepository.deletePostById(postId); // 데이터베이스에서 삭제
                titleField.setText("");
                contentArea.setText("");
                JOptionPane.showMessageDialog(this, "글이 삭제되었습니다.", "정보", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        tempSaveButton.addActionListener(e -> {
            String title = titleField.getText();
            String content = contentArea.getText();

            if (!title.isEmpty() && !content.isEmpty()) {
                tempStorage.putIfAbsent(userId, new HashMap<>());
                tempStorage.get(userId).put(title, content);
                JOptionPane.showMessageDialog(this, "글이 임시 저장되었습니다.", "임시저장", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "제목과 본문을 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            }
        });

        saveButton.addActionListener(e -> {
            String title = titleField.getText();
            String content = contentArea.getText();

            if (!title.isEmpty() && !content.isEmpty()) {
                try {
                    if (!postListModel.contains(title)) { // 중복 방지
                        PostRepository postRepository = new PostRepository();
                        postRepository.savePost(title, content, userId); // 데이터베이스에 저장
                        postListModel.addElement(title); // 제목을 리스트에 추가
                    }
                    dispose(); // 저장 후 다이얼로그 닫기
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "글 저장 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "제목과 본문을 입력하세요.", "경고", JOptionPane.WARNING_MESSAGE);
            }
        });

        // 임시 저장된 글 불러오기 (생성 시)
        if (tempStorage.containsKey(userId) && !tempStorage.get(userId).isEmpty()) {
            Map<String, String> userTempStorage = tempStorage.get(userId);
            String tempTitle = userTempStorage.keySet().iterator().next();
            String tempContent = userTempStorage.get(tempTitle);
            titleField.setText(tempTitle);
            contentArea.setText(tempContent);
        }
    }

    public String getTitleText() {
        return titleField.getText();
    }

    public String getContentText() {
        return contentArea.getText();
    }

    // 글 제목을 기반으로 글 ID를 가져오는 메서드 추가
    private int getPostIdByTitle(String title) {
        String sql = "SELECT id FROM posts WHERE title = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, title);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // 글 ID가 없을 경우 -1 반환
    }
}
