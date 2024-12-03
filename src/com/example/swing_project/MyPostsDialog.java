package com.example.swing_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class MyPostsDialog extends JDialog {
    private DefaultListModel<String> myPostsModel;
    private JList<String> myPostsList;

    private DefaultListModel<String> mainListModel; // 메인 UI의 리스트 모델
    private HashMap<String, String> mainPostMap; // 메인 UI의 포스트 맵
    private String loggedInUsername; // 로그인한 사용자의 아이디
    private AtomicReference<String> nickname; // 로그인한 사용자의 닉네임
    private boolean isNicknameFixed = false; // 고정 닉네임 여부
    private int currentUserId;

    public MyPostsDialog(List<post> userPosts, DefaultListModel<String> mainListModel, HashMap<String, String> mainPostMap, String loggedInUsername, int currentUserId) {
        setTitle("내가 작성한 글");
        setSize(600, 600);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(255, 240, 245)); // 연한 핑크 배경
        setLocationRelativeTo(null);

        // 메인 UI 데이터 참조
        this.mainListModel = mainListModel;
        this.mainPostMap = mainPostMap;
        this.loggedInUsername = loggedInUsername;
        this.currentUserId = currentUserId;

        UserRepository userRepository = new UserRepository();
        this.nickname = new AtomicReference<>(userRepository.getNicknameByUserId(currentUserId));

        // 상단 패널 (닉네임과 타이틀)
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(new Color(204, 153, 255)); // 연한 보라색 배경
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 패딩 추가

        // 닉네임 및 설정 버튼 패널
        JPanel nicknamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        nicknamePanel.setBackground(new Color(204, 153, 255));
        JLabel nicknameLabel = new JLabel("닉네임: " + nickname.get() + " (유동)");
        nicknameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        nicknameLabel.setForeground(Color.WHITE);
        nicknamePanel.add(nicknameLabel);

        JButton changeNicknameButton = new JButton("닉네임 변경");
        changeNicknameButton.setBackground(new Color(153, 102, 255));
        changeNicknameButton.setForeground(Color.WHITE);
        changeNicknameButton.addActionListener(e -> {
            if (!isNicknameFixed) {
                String newNickname = generateRandomNickname();
                if (!userRepository.isNicknameDuplicate(newNickname)) {
                    nickname.set(newNickname);
                    userRepository.updateNicknameByUserId(currentUserId, newNickname);
                    nicknameLabel.setText("닉네임: " + nickname.get());
                } else {
                    JOptionPane.showMessageDialog(this, "중복된 닉네임이 생성되었습니다. 다시 시도해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        nicknamePanel.add(changeNicknameButton);

        JButton fixNicknameButton = new JButton("고정 닉네임");
        fixNicknameButton.setBackground(new Color(153, 102, 255));
        fixNicknameButton.setForeground(Color.WHITE);
        fixNicknameButton.addActionListener(e -> {
            isNicknameFixed = !isNicknameFixed;
            if (isNicknameFixed) {
                fixNicknameButton.setText("닉네임 고정 해제");
                nicknameLabel.setText("닉네임: " + nickname.get() + " (고정)");
            } else {
                fixNicknameButton.setText("고정 닉네임");
                nicknameLabel.setText("닉네임: " + nickname.get() + " (유동)");
            }
        });
        nicknamePanel.add(fixNicknameButton);

        JButton setNicknameButton = new JButton("닉네임 설정");
        setNicknameButton.setBackground(new Color(153, 102, 255));
        setNicknameButton.setForeground(Color.WHITE);
        setNicknameButton.addActionListener(e -> {
            String newNickname = JOptionPane.showInputDialog(this, "새 닉네임을 입력하세요:");
            if (newNickname != null && !newNickname.trim().isEmpty() && !userRepository.isNicknameDuplicate(newNickname)) {
                nickname.set(newNickname);
                userRepository.updateNicknameByUserId(currentUserId, newNickname);
                nicknameLabel.setText("닉네임: " + nickname.get() + (isNicknameFixed ? " (고정)" : " (유동)"));
            } else {
                JOptionPane.showMessageDialog(this, "닉네임이 중복되었거나 유효하지 않습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });
        nicknamePanel.add(setNicknameButton);

        topPanel.add(nicknamePanel, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel("내가 작성한 글 목록", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // 글 목록 리스트 모델 및 리스트 구성
        myPostsModel = new DefaultListModel<>();
        for (post post : userPosts) {
            myPostsModel.addElement(post.getTitle());
        }

        myPostsList = new JList<>(myPostsModel);
        myPostsList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        myPostsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myPostsList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // 경계선 추가
        myPostsList.setBackground(Color.WHITE);

        // 리스트 클릭 시 글 내용 보여주기
        myPostsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedTitle = myPostsList.getSelectedValue();
                    post selectedPost = userPosts.stream()
                            .filter(post -> post.getTitle().equals(selectedTitle))
                            .findFirst()
                            .orElse(null);

                    if (selectedPost != null) {
                        showPostDetailDialog(selectedPost);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(myPostsList);
        scrollPane.setPreferredSize(new Dimension(580, 300));
        add(scrollPane, BorderLayout.CENTER);

        // 하단 패널 (삭제 버튼과 닫기 버튼)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(new Color(255, 240, 245)); // 하단 배경

        JButton deleteButton = new JButton("삭제");
        deleteButton.setBackground(new Color(204, 102, 102));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setEnabled(false); // 초기에는 비활성화
        deleteButton.addActionListener(e -> deleteSelectedPost(userPosts));

        JButton closeButton = new JButton("닫기");
        closeButton.setBackground(new Color(204, 153, 255));
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> dispose());

        // 글 선택 시 삭제 버튼 활성화
        myPostsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && myPostsList.getSelectedIndex() != -1) {
                deleteButton.setEnabled(true);
            } else {
                deleteButton.setEnabled(false);
            }
        });

        bottomPanel.add(deleteButton);
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // 글 삭제 메서드
    private void deleteSelectedPost(List<post> userPosts) {
        String selectedTitle = myPostsList.getSelectedValue();
        if (selectedTitle != null) {
            int confirm = JOptionPane.showConfirmDialog(
                    MyPostsDialog.this,
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
                    // 데이터베이스에서 글 삭제
                    PostRepository postRepository = new PostRepository();
                    postRepository.deletePostById(selectedPost.getId());

                    // UI에서 글 삭제
                    userPosts.remove(selectedPost);
                    myPostsModel.removeElement(selectedTitle);

                    // 메인 UI에서도 글 삭제
                    if (mainListModel.contains(selectedTitle)) {
                        mainListModel.removeElement(selectedTitle);
                        mainPostMap.remove(selectedTitle);
                    }

                    JOptionPane.showMessageDialog(MyPostsDialog.this, "글이 삭제되었습니다.", "정보", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    // 글 내용 상세보기 다이얼로그
    private void showPostDetailDialog(post post) {
        JDialog postDetailDialog = new JDialog(this, post.getTitle(), true);
        postDetailDialog.setSize(450, 350);
        postDetailDialog.setLayout(new BorderLayout(10, 10));
        postDetailDialog.setLocationRelativeTo(this);

        JTextArea contentArea = new JTextArea(post.getContent());
        contentArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        contentArea.setLineWrap(true); // 줄 바꿈 설정
        contentArea.setWrapStyleWord(true); // 단어 단위로 줄 바꿈
        contentArea.setEditable(false); // 수정 불가

        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setPreferredSize(new Dimension(400, 250));
        postDetailDialog.add(contentScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> postDetailDialog.dispose());
        buttonPanel.add(closeButton);

        postDetailDialog.add(buttonPanel, BorderLayout.SOUTH);
        postDetailDialog.setVisible(true);
    }

    // 랜덤 닉네임 생성 메서드
    private String generateRandomNickname() {
        String[] randomNicknames = {"하늘을 달리는 호랑이", "웃는 바람", "고요한 바다", "날아오르는 새", "작은 별빛", "달리는 귤", "춤추는 펭귄"};
        Random random = new Random();
        return randomNicknames[random.nextInt(randomNicknames.length)];
    }

    // 닉네임 중복 검사 메서드
    private boolean isNicknameDuplicate(String newNickname) {
        UserRepository userRepository = new UserRepository();
        return userRepository.isNicknameDuplicate(newNickname);
    }
}
