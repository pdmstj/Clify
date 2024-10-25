package com.example.swing_project;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class CommentPanel extends JPanel {

    private DefaultListModel<String> commentListModel;
    private HashMap<Integer, DefaultListModel<String>> replyMap; // 댓글 ID에 답글 리스트 저장

    public CommentPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 240, 245));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        replyMap = new HashMap<>();

        // 댓글 레이블
        JLabel commentLabel = new JLabel("댓글");
        commentLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        commentLabel.setForeground(new Color(153, 102, 255));
        add(commentLabel, BorderLayout.NORTH);

        // 댓글 리스트
        commentListModel = new DefaultListModel<>();
        JList<String> commentList = new JList<>(commentListModel);
        commentList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        commentList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane commentScrollPane = new JScrollPane(commentList);
        add(commentScrollPane, BorderLayout.CENTER);

        // 댓글 입력 필드와 버튼
        JTextField commentField = new JTextField(30);
        commentField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JButton commentButton = new JButton("댓글 달기");
        commentButton.setBackground(new Color(204, 153, 255));
        commentButton.setForeground(Color.WHITE);
        commentButton.setFocusPainted(false);

        // 댓글 추가 동작
        commentButton.addActionListener(e -> {
            String commentText = commentField.getText();
            if (!commentText.isEmpty()) {
                int commentIndex = commentListModel.size(); // 댓글 ID로 사용
                commentListModel.addElement("댓글 " + (commentIndex + 1) + ": " + commentText); // 댓글 추가
                replyMap.put(commentIndex, new DefaultListModel<>()); // 댓글에 대한 답글 리스트 생성
                commentField.setText(""); // 입력 필드 초기화
            }
        });

        JPanel commentInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        commentInputPanel.setBackground(new Color(255, 240, 245));
        commentInputPanel.add(commentField);
        commentInputPanel.add(commentButton);
        add(commentInputPanel, BorderLayout.SOUTH);

        // 댓글 클릭 시 답글 및 기능 추가
        commentList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int selectedCommentIndex = commentList.getSelectedIndex();
                    if (selectedCommentIndex != -1) {
                        showReplyDialog(selectedCommentIndex);
                    }
                }
            }
        });
    }

    // 답글 창을 띄우는 메서드
    private void showReplyDialog(int commentIndex) {
        JDialog parentDialog = (JDialog) SwingUtilities.getWindowAncestor(this);
        JDialog replyDialog = new JDialog(parentDialog, "답글 달기", true);
        replyDialog.setSize(400, 200);
        replyDialog.setLayout(new BorderLayout());
        replyDialog.setLocationRelativeTo(this);

        // 답글 입력 필드
        JTextArea replyArea = new JTextArea(5, 30);
        replyArea.setLineWrap(true);
        replyArea.setWrapStyleWord(true);
        replyArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane replyScrollPane = new JScrollPane(replyArea);
        replyDialog.add(replyScrollPane, BorderLayout.CENTER);

        // 하단 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveReplyButton = new JButton("저장");
        saveReplyButton.setBackground(new Color(204, 153, 255));
        saveReplyButton.setForeground(Color.WHITE);

        JButton cancelReplyButton = new JButton("취소");
        cancelReplyButton.setBackground(new Color(204, 153, 255));
        cancelReplyButton.setForeground(Color.WHITE);

        // 답글 저장 기능
        saveReplyButton.addActionListener(e -> {
            String replyText = replyArea.getText();
            if (!replyText.isEmpty()) {
                DefaultListModel<String> replies = replyMap.get(commentIndex);
                replies.addElement("ㄴ 답글: " + replyText); // 답글 추가
                commentListModel.addElement("    " + replyText); // 댓글 리스트에도 표시
                replyDialog.dispose();
            }
        });

        // 취소 기능
        cancelReplyButton.addActionListener(e -> replyDialog.dispose());

        buttonPanel.add(saveReplyButton);
        buttonPanel.add(cancelReplyButton);
        replyDialog.add(buttonPanel, BorderLayout.SOUTH);

        replyDialog.setVisible(true);
    }
}
