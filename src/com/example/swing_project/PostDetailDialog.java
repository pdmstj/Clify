package com.example.swing_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

public class PostDetailDialog extends JDialog {

    private DefaultListModel<String> commentListModel;
    private HashMap<Integer, DefaultListModel<String>> replyMap; // 댓글에 대한 답글 저장
    private int postId; // 현재 게시글 ID
    private String currentUserName; // 현재 사용자 이름
    private int currentUserId; // 현재 사용자 ID
    private String nickname; // 현재 사용자 닉네임
    private CommentRepository commentRepository; // 댓글 데이터베이스 처리

    public PostDetailDialog(JFrame parentFrame, String title, String content, int postId, String currentUserName, int currentUserId, String currentNickname, Connection connection) {
        super(parentFrame, title, true); // 모달 다이얼로그 설정
        this.postId = postId;
        this.currentUserName = currentUserName;
        this.currentUserId = currentUserId;
        this.nickname = currentNickname;
        this.commentRepository = new CommentRepository(connection);

        setSize(800, 600);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());

        replyMap = new HashMap<>();

        // 배경 색상 설정
        getContentPane().setBackground(new Color(255, 240, 245));

        // 상단 제목 표시
        JLabel titleLabel = new JLabel("게시글 상세");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        titleLabel.setForeground(new Color(153, 102, 255));
        add(titleLabel, BorderLayout.NORTH);

        // 본문 패널
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(255, 240, 245));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel contentTitle = new JLabel(title);
        contentTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        contentTitle.setForeground(new Color(153, 102, 255));

        JTextArea contentArea = new JTextArea(content);
        contentArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createLineBorder(new Color(204, 153, 255)));

        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBorder(BorderFactory.createLineBorder(new Color(204, 153, 255), 1));

        contentPanel.add(contentTitle, BorderLayout.NORTH);
        contentPanel.add(contentScrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.NORTH);

        // 댓글 패널
        JPanel commentPanel = new JPanel(new BorderLayout());
        commentPanel.setBackground(new Color(255, 240, 245));
        commentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel commentLabel = new JLabel("댓글");
        commentLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        commentLabel.setForeground(new Color(153, 102, 255));
        commentPanel.add(commentLabel, BorderLayout.NORTH);

        // 댓글 리스트
        commentListModel = new DefaultListModel<>();
        loadComments(); // 초기 댓글 불러오기
        JList<String> commentList = new JList<>(commentListModel);
        commentList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        commentList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        commentList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = commentList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        showReplyDialog(index);
                    }
                }
            }
        });
        JScrollPane commentScrollPane = new JScrollPane(commentList);
        commentPanel.add(commentScrollPane, BorderLayout.CENTER);

        // 댓글 입력 필드
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(new Color(255, 240, 245));

        JTextField commentInputField = new JTextField();
        inputPanel.add(commentInputField, BorderLayout.CENTER);

        JButton addCommentButton = new JButton("댓글 달기");
        addCommentButton.setBackground(new Color(204, 153, 255));
        addCommentButton.setForeground(Color.WHITE);
        addCommentButton.addActionListener(e -> {
            String comment = commentInputField.getText().trim();
            if (!comment.isEmpty()) {
                saveComment(comment); // 댓글 저장 시 로그인된 사용자의 닉네임 사용
                commentInputField.setText("");
            } else {
                JOptionPane.showMessageDialog(PostDetailDialog.this, "댓글 내용을 입력하세요.", "경고", JOptionPane.WARNING_MESSAGE);
            }
        });

        inputPanel.add(addCommentButton, BorderLayout.EAST);

        commentPanel.add(inputPanel, BorderLayout.SOUTH);

        add(commentPanel, BorderLayout.CENTER);

        // 하단 버튼 패널 추가
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionButtonsPanel.setBackground(new Color(255, 240, 245));

        JButton replyButton = new JButton("답장");
        JButton reportButton = new JButton("신고");
        JButton bookmarkButton = new JButton("북마크");
        JButton closeButton = new JButton("닫기");

        replyButton.setBackground(new Color(204, 153, 255));
        reportButton.setBackground(new Color(204, 153, 255));
        bookmarkButton.setBackground(new Color(204, 153, 255));
        closeButton.setBackground(new Color(204, 153, 255));

        replyButton.setForeground(Color.WHITE);
        reportButton.setForeground(Color.WHITE);
        bookmarkButton.setForeground(Color.WHITE);
        closeButton.setForeground(Color.WHITE);

        closeButton.addActionListener(e -> dispose());

        actionButtonsPanel.add(replyButton);
        actionButtonsPanel.add(reportButton);
        actionButtonsPanel.add(bookmarkButton);
        actionButtonsPanel.add(closeButton);

        add(actionButtonsPanel, BorderLayout.SOUTH);
    }

    private void loadComments() {
        try {
            List<Comment> comments = commentRepository.loadCommentsByPost(postId);
            commentListModel.clear();
            for (Comment comment : comments) {
                // nickname을 사용하여 댓글을 포맷팅
                String formattedComment = String.format("%s - %s (%s)", comment.getContent(), comment.getAuthor(), comment.getCreatedAt().toString());
                commentListModel.addElement(formattedComment);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "댓글을 불러오는 중 오류가 발생했습니다.");
        }
    }

    private void saveComment(String content) {
        try {
            // author를 현재 사용자 닉네임으로 지정합니다.
            commentRepository.saveComment(postId, content, nickname, currentUserId);
            loadComments(); // 댓글 저장 후 새로고침
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "댓글 저장 중 오류가 발생했습니다.");
        }
    }

    private void showReplyDialog(int commentIndex) {
        JDialog replyDialog = new JDialog(this, "답글 달기", true);
        replyDialog.setSize(400, 200);
        replyDialog.setLayout(new BorderLayout());
        replyDialog.setLocationRelativeTo(this);

        JTextField replyField = new JTextField();
        replyDialog.add(replyField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton submitButton = new JButton("등록");
        JButton cancelButton = new JButton("취소");

        submitButton.addActionListener(e -> {
            String replyText = replyField.getText().trim();
            if (!replyText.isEmpty()) {
                // 댓글 저장 로직 추가 필요
                JOptionPane.showMessageDialog(this, "답글이 등록되었습니다.");
                replyDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "답글 내용을 입력하세요.", "경고", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> replyDialog.dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        replyDialog.add(buttonPanel, BorderLayout.SOUTH);

        replyDialog.setVisible(true);
    }
}
