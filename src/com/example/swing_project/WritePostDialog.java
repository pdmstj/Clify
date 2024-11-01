package com.example.swing_project;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class WritePostDialog extends JDialog {

    private JTextField titleField;
    private JTextArea contentArea;
    private JButton saveButton;
    private JButton tempSaveButton;
    private JButton deleteButton;

    private DefaultListModel<String> postListModel;
    private static Map<String, String> tempStorage = new HashMap<>();

    public WritePostDialog(JFrame parentFrame, DefaultListModel<String> postListModel) {
        super(parentFrame, "글쓰기", true);
        this.postListModel = postListModel;
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
            titleField.setText("");
            contentArea.setText("");
        });

        tempSaveButton.addActionListener(e -> {
            String title = titleField.getText();
            String content = contentArea.getText();
            if (!title.isEmpty() && !content.isEmpty()) {
                tempStorage.put(title, content);
                JOptionPane.showMessageDialog(this, "글이 임시 저장되었습니다.", "임시저장", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "제목과 본문을 입력해주세요.", "경고", JOptionPane.WARNING_MESSAGE);
            }
        });

        saveButton.addActionListener(e -> {
            String title = titleField.getText();
            String content = contentArea.getText();
            int userId = 1; // 예시로 사용자 ID를 1로 설정합니다. 실제로는 로그인한 사용자 ID를 사용해야 합니다.

            if (!title.isEmpty() && !content.isEmpty()) {
                PostRepository postRepository = new PostRepository();
                postRepository.savePost(title, content, userId); // 데이터베이스에 저장
                postListModel.addElement(title);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "제목과 본문을 입력하세요.", "경고", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    public String getTitleText() {
        return titleField.getText();
    }

    public String getContentText() {
        return contentArea.getText();
    }
}
