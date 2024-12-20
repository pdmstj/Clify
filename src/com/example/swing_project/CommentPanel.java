package com.example.swing_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentPanel extends JPanel {

    private DefaultListModel<String> commentListModel; // 댓글 리스트 모델
    private Map<String, DefaultListModel<String>> replyMap; // 댓글에 대한 대댓글 리스트를 관리하는 맵
    private Map<String, JScrollPane> visibleReplyLists; // 현재 보여지는 대댓글 리스트를 저장
    private CommentRepository commentRepository;
    private int postId; // 현재 게시글의 ID

    // 생성자에 게시글 ID를 전달받도록 수정
    public CommentPanel(int postId) {
        this.postId = postId; // 게시글 ID 저장

        setLayout(new BorderLayout());
        setBackground(new Color(255, 240, 245));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

        // 대댓글 맵 초기화
        replyMap = new HashMap<>();
        visibleReplyLists = new HashMap<>();

        // CommentRepository 초기화
        commentRepository = new CommentRepository(DatabaseConnector.getConnection());

        // 댓글 클릭 이벤트 처리 (대댓글 보이기/숨기기)
        commentList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { // 댓글을 클릭 시
                    String selectedComment = commentList.getSelectedValue();
                    if (selectedComment != null) {
                        toggleReplyVisibility(selectedComment); // 대댓글 보이기/숨기기 토글
                    }
                }
            }
        });

        // 댓글 입력 필드와 버튼
        JTextField commentField = new JTextField(30);
        commentField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JButton commentButton = new JButton("댓글 달기");
        commentButton.setBackground(new Color(204, 153, 255));
        commentButton.setForeground(Color.WHITE);
        commentButton.setFocusPainted(false);

        // 댓글 달기 버튼 클릭 시 댓글 저장 및 UI 갱신
        commentButton.addActionListener(e -> {
            // 로그인 상태 확인
            if (!ClifyMainUI.getIsLoggedIn()) { // 메서드를 사용하여 로그인 상태 확인
                JOptionPane.showMessageDialog(this, "로그인 후에 댓글을 작성할 수 있습니다.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String commentText = commentField.getText().trim();
            if (!commentText.isEmpty()) {
                int userId = ClifyMainUI.getCurrentUserId(); // 현재 로그인한 사용자 ID 가져오기
                String currentUserNickname = ClifyMainUI.getCurrentNickname(); // 로그인된 사용자의 닉네임 가져오기
                commentRepository.saveComment(postId, commentText, currentUserNickname, userId); // 게시글 ID와 함께 댓글 DB 저장
                commentField.setText(""); // 입력 필드 초기화
                loadComments(); // 댓글 저장 후 댓글 목록 갱신
            } else {
                JOptionPane.showMessageDialog(this, "댓글 내용을 입력하세요.", "경고", JOptionPane.WARNING_MESSAGE);
            }
        });

        JPanel commentInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        commentInputPanel.setBackground(new Color(255, 240, 245));
        commentInputPanel.add(commentField);
        commentInputPanel.add(commentButton);
        add(commentInputPanel, BorderLayout.SOUTH);

        // 댓글 초기화 (데이터베이스에서 댓글 및 대댓글 불러오기)
        loadComments();
    }

    // 댓글 및 대댓글 불러오기
    private void loadComments() {
        commentListModel.clear(); // 기존 댓글 삭제 후 갱신
        List<Comment> comments = commentRepository.loadCommentsByPost(postId); // 해당 게시글에 대한 댓글 (최상위 댓글들)
        for (Comment comment : comments) {
            commentListModel.addElement(comment.getContent() + " - " + comment.getAuthor()); // 댓글 리스트에 추가, 작성자 표시
            replyMap.put(comment.getContent(), new DefaultListModel<>()); // 대댓글 리스트 초기화

            // 대댓글 불러오기
            List<Comment> replies = commentRepository.loadRepliesByCommentId(comment.getId()); // 해당 댓글에 대한 대댓글 불러오기
            DefaultListModel<String> replyListModel = replyMap.get(comment.getContent());
            for (Comment reply : replies) {
                replyListModel.addElement(reply.getContent() + " - " + reply.getAuthor()); // 대댓글 리스트에 추가, 작성자 표시
            }
        }
    }

    // 대댓글 보이기/숨기기 토글
    private void toggleReplyVisibility(String comment) {
        DefaultListModel<String> replies = replyMap.get(comment); // 해당 댓글에 대한 대댓글 리스트 가져오기
        if (replies == null || replies.isEmpty()) {
            JOptionPane.showMessageDialog(this, "대댓글이 없습니다.");
            return;
        }

        if (visibleReplyLists.containsKey(comment)) {
            // 대댓글이 이미 보이고 있다면 숨기기
            JScrollPane replyScrollPane = visibleReplyLists.remove(comment);
            remove(replyScrollPane);
        } else {
            // 대댓글이 보이지 않으면 새로 추가
            JList<String> replyList = new JList<>(replies);
            replyList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            replyList.setFont(new Font("SansSerif", Font.PLAIN, 12));
            JScrollPane replyScrollPane = new JScrollPane(replyList);
            replyScrollPane.setPreferredSize(new Dimension(200, 100));

            // 댓글 밑에 대댓글 리스트 추가
            add(replyScrollPane, BorderLayout.SOUTH);
            visibleReplyLists.put(comment, replyScrollPane);
        }

        // 레이아웃 업데이트
        revalidate();
        repaint();
    }

    // 대댓글 입력 창을 표시하는 메서드
    private void showReplyDialog(String comment) {
        if (comment == null || comment.isEmpty()) return;

        JDialog replyDialog = new JDialog((Frame) null, "답글 달기", true);
        replyDialog.setSize(400, 200);
        replyDialog.setLayout(new BorderLayout());
        replyDialog.setLocationRelativeTo(this);

        // 대댓글 입력 필드
        JTextField replyField = new JTextField(30);
        JPanel replyPanel = new JPanel(new BorderLayout());
        replyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        replyPanel.add(new JLabel("대댓글 입력: "), BorderLayout.NORTH);
        replyPanel.add(replyField, BorderLayout.CENTER);
        replyDialog.add(replyPanel, BorderLayout.CENTER);

        // 대댓글 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton submitButton = new JButton("등록");
        JButton cancelButton = new JButton("취소");

        // 대댓글 등록 버튼 클릭 시
        submitButton.addActionListener(e -> {
            // 로그인 상태 확인
            if (!ClifyMainUI.getIsLoggedIn()) { // 메서드를 사용하여 로그인 상태 확인
                JOptionPane.showMessageDialog(this, "로그인 후에 대댓글을 작성할 수 있습니다.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String replyText = replyField.getText().trim();
            if (!replyText.isEmpty()) {
                int userId = ClifyMainUI.getCurrentUserId(); // 현재 로그인한 사용자 ID 가져오기
                String currentUserNickname = ClifyMainUI.getCurrentNickname(); // 로그인된 사용자의 닉네임 가져오기
                commentRepository.saveReply(comment, replyText, currentUserNickname, userId); // 대댓글 DB 저장

                DefaultListModel<String> replies = replyMap.get(comment);
                if (replies == null) {
                    replies = new DefaultListModel<>();
                    replyMap.put(comment, replies); // 대댓글 리스트가 없을 경우 초기화
                }
                replies.addElement(replyText + " - " + currentUserNickname); // 대댓글 추가
                JOptionPane.showMessageDialog(this, "대댓글이 등록되었습니다.");
                replyDialog.dispose();
                loadComments(); // 대댓글 등록 후 댓글 목록 갱신
            } else {
                JOptionPane.showMessageDialog(this, "대댓글 내용을 입력하세요.", "경고", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> replyDialog.dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        replyDialog.add(buttonPanel, BorderLayout.SOUTH);

        replyDialog.setVisible(true);
    }
}
