package com.example.swing_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class MyPostsDialog extends JDialog {
    private DefaultListModel<String> myPostsModel;
    private JList<String> myPostsList;
    private DefaultListModel<String> myCommentsModel;
    private JList<String> myCommentsList;

    private DefaultListModel<String> mainListModel; // 메인 UI의 리스트 모델
    private String loggedInUsername; // 로그인한 사용자의 아이디
    private AtomicReference<String> nickname; // 로그인한 사용자의 닉네임
    private boolean isNicknameFixed; // 고정 닉네임 여부
    private int currentUserId;

    private final UserRepository userRepository = new UserRepository(); // UserRepository 참조
    private final PostRepository postRepository = new PostRepository(); // PostRepository 참조
    private final CommentRepository commentRepository = new CommentRepository(DatabaseConnector.getConnection()); // CommentRepository 참조

    private static final String NICKNAME_STATUS_FILE = "nickname_status.txt"; // 닉네임 상태 파일

    public MyPostsDialog(List<post> userPosts, DefaultListModel<String> mainListModel, String loggedInUsername, int currentUserId) {
        setTitle("내가 작성한 글과 댓글");
        setSize(600, 600);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(255, 240, 245)); // 연한 핑크 배경
        setLocationRelativeTo(null);

        // 메인 UI 데이터 참조
        this.mainListModel = mainListModel;
        this.loggedInUsername = loggedInUsername;
        this.currentUserId = currentUserId;

        // 닉네임과 고정 상태 로드
        loadNicknameStatus();

        // 초기 닉네임 설정
        this.nickname = new AtomicReference<>(userRepository.getNicknameByUserId(currentUserId));

        // 상단 패널 (닉네임과 타이틀)
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(new Color(204, 153, 255)); // 연한 보라색 배경
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 패딩 추가

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setBackground(new Color(204, 153, 255));

        // 닉네임 레이블
        JLabel nicknameLabel = new JLabel("닉네임: " + nickname.get() + (isNicknameFixed ? " (고정)" : " (유동)"));
        nicknameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        nicknameLabel.setForeground(Color.WHITE);
        topPanel.add(nicknameLabel, BorderLayout.NORTH);

        // 닉네임 변경 버튼
        JButton changeNicknameButton = createButton("닉네임 변경", e -> {
            if (!isNicknameFixed) {
                String newNickname = generateRandomNickname();
                updateNickname(newNickname, nicknameLabel);
            }
        });
        buttonPanel.add(changeNicknameButton);

        // 닉네임 고정 버튼
        JButton fixNicknameButton = createButton(isNicknameFixed ? "닉네임 고정 해제" : "고정 닉네임", null);
        fixNicknameButton.addActionListener(e -> {
            isNicknameFixed = !isNicknameFixed;
            updateNicknameLabel(nicknameLabel, fixNicknameButton);
            saveNicknameStatus(); // 닉네임 상태 저장
        });
        buttonPanel.add(fixNicknameButton);

        // 닉네임 설정 버튼
        JButton setNicknameButton = createButton("닉네임 설정", e -> {
            String newNickname = JOptionPane.showInputDialog(this, "새 닉네임을 입력하세요:");
            if (newNickname != null && !newNickname.trim().isEmpty()) {
                if (!userRepository.isNicknameDuplicate(newNickname)) {
                    updateNickname(newNickname, nicknameLabel);
                } else {
                    showMessage("닉네임이 중복되었습니다. 다른 닉네임을 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonPanel.add(setNicknameButton);

        topPanel.add(buttonPanel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel("내가 작성한 글과 댓글 목록", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // 탭 패널 생성
        JTabbedPane tabbedPane = new JTabbedPane();

        // 작성한 글 탭
        myPostsModel = new DefaultListModel<>();
        userPosts.forEach(post -> myPostsModel.addElement(post.getTitle()));

        myPostsList = new JList<>(myPostsModel);
        myPostsList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        myPostsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myPostsList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        myPostsList.setBackground(Color.WHITE);

        myPostsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showSelectedPostDetail(userPosts);
                }
            }
        });

        JScrollPane postsScrollPane = new JScrollPane(myPostsList);
        postsScrollPane.setPreferredSize(new Dimension(580, 300));
        tabbedPane.addTab("작성한 글", postsScrollPane);

        // 작성한 댓글 탭
        myCommentsModel = new DefaultListModel<>();
        List<Comment> userComments = commentRepository.fetchCommentsByUser(currentUserId);
        userComments.forEach(comment -> myCommentsModel.addElement(comment.getContent()));

        myCommentsList = new JList<>(myCommentsModel);
        myCommentsList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        myCommentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myCommentsList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        myCommentsList.setBackground(Color.WHITE);

        myCommentsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showSelectedCommentDetail(userComments);
                }
            }
        });

        JScrollPane commentsScrollPane = new JScrollPane(myCommentsList);
        commentsScrollPane.setPreferredSize(new Dimension(580, 300));
        tabbedPane.addTab("작성한 댓글", commentsScrollPane);

        add(tabbedPane, BorderLayout.CENTER);

        // 하단 패널 (삭제 버튼과 닫기 버튼)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(new Color(255, 240, 245));

        JButton deleteButton = createButton("삭제", e -> {
            if (tabbedPane.getSelectedIndex() == 0) {
                deleteSelectedPost(userPosts);
            } else if (tabbedPane.getSelectedIndex() == 1) {
                deleteSelectedComment(userComments);
            }
        });
        deleteButton.setEnabled(false);

        JButton closeButton = createButton("닫기", e -> dispose());

        // 글 또는 댓글 선택 시 삭제 버튼 활성화
        myPostsList.addListSelectionListener(e -> deleteButton.setEnabled(myPostsList.getSelectedIndex() != -1));
        myCommentsList.addListSelectionListener(e -> deleteButton.setEnabled(myCommentsList.getSelectedIndex() != -1));

        bottomPanel.add(deleteButton);
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // 닉네임 상태 저장 메서드
    private void saveNicknameStatus() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NICKNAME_STATUS_FILE))) {
            writer.write(nickname.get() + "\n");
            writer.write(isNicknameFixed ? "true" : "false");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 닉네임 상태 로드 메서드
    private void loadNicknameStatus() {
        try (BufferedReader reader = new BufferedReader(new FileReader(NICKNAME_STATUS_FILE))) {
            nickname = new AtomicReference<>(reader.readLine());
            isNicknameFixed = Boolean.parseBoolean(reader.readLine());
        } catch (IOException e) {
            nickname = new AtomicReference<>(generateRandomNickname());
            isNicknameFixed = false;
        }
    }

    // 닉네임 업데이트 메서드
    private void updateNickname(String newNickname, JLabel nicknameLabel) {
        nickname.set(newNickname);
        userRepository.updateNicknameByUserId(currentUserId, newNickname);
        nicknameLabel.setText("닉네임: " + nickname.get() + (isNicknameFixed ? " (고정)" : " (유동)"));
        saveNicknameStatus(); // 닉네임 상태 저장
    }

    // 닉네임 라벨 업데이트
    private void updateNicknameLabel(JLabel nicknameLabel, JButton fixNicknameButton) {
        if (isNicknameFixed) {
            fixNicknameButton.setText("닉네임 고정 해제");
            nicknameLabel.setText("닉네임: " + nickname.get() + " (고정)");
        } else {
            fixNicknameButton.setText("고정 닉네임");
            nicknameLabel.setText("닉네임: " + nickname.get() + " (유동)");
        }
        saveNicknameStatus(); // 닉네임 상태 저장
    }

    // 랜덤 닉네임 생성 메서드
    private String generateRandomNickname() {
        String[] randomNicknames = {"하늘을 달리는 호랑이", "웃는 바람", "고요한 바다", "날아오르는 새", "작은 별빛", "달리는 귤", "춤추는 펭귄"};
        Random random = new Random();
        return randomNicknames[random.nextInt(randomNicknames.length)];
    }

    // 공통 버튼 생성 메서드
    private JButton createButton(String text, java.awt.event.ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 30)); // 버튼 크기 조정
        button.setBackground(new Color(153, 102, 255));
        button.setForeground(Color.WHITE);
        if (actionListener != null) {
            button.addActionListener(actionListener);
        }
        return button;
    }

    // 공통 메시지 표시 메서드
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    // 글 내용 상세보기 다이얼로그
    private void showSelectedPostDetail(List<post> userPosts) {
        String selectedTitle = myPostsList.getSelectedValue();
        post selectedPost = userPosts.stream()
                .filter(post -> post.getTitle().equals(selectedTitle))
                .findFirst()
                .orElse(null);

        if (selectedPost != null) {
            showDetailDialog(selectedPost.getTitle(), selectedPost.getContent());
        }
    }

    // 댓글 내용 상세보기 다이얼로그
    private void showSelectedCommentDetail(List<Comment> userComments) {
        String selectedContent = myCommentsList.getSelectedValue();
        Comment selectedComment = userComments.stream()
                .filter(comment -> comment.getContent().equals(selectedContent))
                .findFirst()
                .orElse(null);

        if (selectedComment != null) {
            showDetailDialog("댓글", selectedComment.getContent());
        }
    }

    // 공통 상세보기 다이얼로그
    private void showDetailDialog(String title, String content) {
        JDialog detailDialog = new JDialog(this, title, true);
        detailDialog.setSize(450, 350);
        detailDialog.setLayout(new BorderLayout(10, 10));
        detailDialog.setLocationRelativeTo(this);

        JTextArea contentArea = new JTextArea(content);
        contentArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);

        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setPreferredSize(new Dimension(400, 250));
        detailDialog.add(contentScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> detailDialog.dispose());
        buttonPanel.add(closeButton);

        detailDialog.add(buttonPanel, BorderLayout.SOUTH);
        detailDialog.setVisible(true);
    }

    // 글 삭제 메서드
    private void deleteSelectedPost(List<post> userPosts) {
        String selectedTitle = myPostsList.getSelectedValue();
        if (selectedTitle != null) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "정말로 선택한 글을 삭제하시겠습니까?",
                    "삭제 확인",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                post selectedPost = userPosts.stream()
                        .filter(post -> post.getTitle().equals(selectedTitle))
                        .findFirst()
                        .orElse(null);

                if (selectedPost != null) {
                    postRepository.deletePostById(selectedPost.getId());
                    userPosts.remove(selectedPost);
                    myPostsModel.removeElement(selectedTitle);
                    showMessage("글이 삭제되었습니다.", "정보", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    // 댓글 삭제 메서드
    private void deleteSelectedComment(List<Comment> userComments) {
        String selectedComment = myCommentsList.getSelectedValue();
        if (selectedComment != null) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "정말로 선택한 댓글을 삭제하시겠습니까?",
                    "삭제 확인",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                Comment selectedCommentObj = userComments.stream()
                        .filter(comment -> comment.getContent().equals(selectedComment))
                        .findFirst()
                        .orElse(null);

                if (selectedCommentObj != null) {
                    commentRepository.deleteCommentById(selectedCommentObj.getId());
                    userComments.remove(selectedCommentObj);
                    myCommentsModel.removeElement(selectedComment);
                    showMessage("댓글이 삭제되었습니다.", "정보", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }
}
