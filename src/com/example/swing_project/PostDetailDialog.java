package com.example.swing_project;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class PostDetailDialog extends JDialog {

    private DefaultListModel<String> commentListModel;
    private HashMap<Integer, DefaultListModel<String>> replyMap; // 댓글에 대한 답글 저장

    public PostDetailDialog(JFrame parentFrame, String title, String content) {
        super(parentFrame, title, true); // 모달 다이얼로그 설정
        setSize(800, 600); // 창 크기 설정
        setLocationRelativeTo(parentFrame); // 부모 창 중앙에 배치
        setLayout(new BorderLayout());

        replyMap = new HashMap<>();

        // 배경 색상 설정
        getContentPane().setBackground(new Color(255, 240, 245)); // 연한 핑크 배경

        // 상단 제목 표시
        JLabel titleLabel = new JLabel("창작");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT); // 왼쪽 정렬
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // 여백 추가
        titleLabel.setForeground(new Color(153, 102, 255)); // 보라색 글씨
        add(titleLabel, BorderLayout.NORTH);

        // 본문 패널 (작성자 글)
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(255, 240, 245)); // 연한 핑크 배경
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel contentTitle = new JLabel(title);
        contentTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        contentTitle.setForeground(new Color(153, 102, 255)); // 보라색 글씨
        contentTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea contentArea = new JTextArea(content);
        contentArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createLineBorder(new Color(204, 153, 255))); // 보라색 테두리

        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBorder(BorderFactory.createLineBorder(new Color(204, 153, 255), 1)); // 보라색 테두리

        contentPanel.add(contentTitle, BorderLayout.NORTH);
        contentPanel.add(contentScrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.NORTH);

        // 댓글 패널
        CommentPanel commentPanel = new CommentPanel();
        add(commentPanel, BorderLayout.CENTER);

        // 하단 버튼 패널 추가
        ActionButtonsPanel actionButtonsPanel = new ActionButtonsPanel(this);
        add(actionButtonsPanel, BorderLayout.SOUTH);
    }
}
