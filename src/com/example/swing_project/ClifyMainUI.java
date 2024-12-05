package com.example.swing_project;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ClifyMainUI {

    private static DefaultListModel<String> listModel;
    private static JList<String> itemList; // itemList를 클래스 필드로 선언하여 다른 메서드에서 접근 가능하게 변경
    private static HashMap<String, String> postMap = new HashMap<>(); // 제목과 본문을 저장하는 맵
    private static HashMap<String, DefaultListModel<String>> commentMap = new HashMap<>(); // 각 글에 대한 댓글을 저장하는 맵
    private static boolean isLoggedIn = false; // 로그인 상태 확인
    private static String loggedInUsername = ""; // 로그인한 사용자의 아이디 저장
    private static String nickname = ""; // 로그인한 사용자의 닉네임 저장
    private static int currentUserId = -1; // 현재 로그인한 사용자의 ID 저장
    private static JButton myPageButton; // myPageButton을 인스턴스 변수로 선언
    private static boolean isNicknameFixed = false; // 닉네임 고정 여부
    private static final String NICKNAME_FILE = "nickname_status.txt"; // 닉네임과 상태를 저장하는 파일 경로

    public static void main(String[] args) {
        // 닉네임 상태 복원
        loadNicknameStatus();

        // 메인 프레임 생성
        JFrame frame = new JFrame("Clify");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // 사이즈 조정
        frame.setLayout(new BorderLayout());

        // 배경 패널
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 240, 245)); // 연한 핑크 배경
        frame.add(mainPanel, BorderLayout.CENTER);

        // 상단 패널 (타이틀 + 검색창)
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(new Color(204, 153, 255)); // 연한 보라색 배경
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 패딩 추가
        mainPanel.add(topPanel, BorderLayout.NORTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // 타이틀 라벨 추가
        JLabel titleLabel = new JLabel("Clify");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        topPanel.add(titleLabel, gbc);

        // 검색 필드 추가
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        addPlaceholderText(searchField, "검색어를 입력하세요...");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(searchField, gbc);

        // 검색 버튼 추가
        JButton searchButton = createButton("검색", e -> {
            String searchTerm = searchField.getText().trim();
            if (postMap.containsKey(searchTerm)) {
                itemList.setSelectedValue(searchTerm, true);
            } else {
                showMessage(frame, "검색 결과가 없습니다.", "정보", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        topPanel.add(searchButton, gbc);

        // 빈 공간 추가
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(Box.createHorizontalStrut(20), gbc);

        // 로그인 버튼 추가
        JButton loginButton = createButton("로그인", e -> {
            LoginDialog loginDialog = new LoginDialog(frame); // 로그인 창 호출
            loginDialog.setVisible(true); // 로그인 창 표시

            // 로그인 후 결과 확인
            if (loginDialog.isLoggedIn()) {
                showMessage(frame, "로그인 성공!", "정보", JOptionPane.INFORMATION_MESSAGE);
                isLoggedIn = true; // 로그인 상태로 변경
                loggedInUsername = loginDialog.getUsername(); // 로그인한 사용자 아이디 저장

                UserRepository userRepository = new UserRepository();
                int userId = userRepository.getUserIdByUsername(loggedInUsername); // 사용자의 ID 가져오기
                if (userId != -1) {
                    currentUserId = userId; // 로그인한 사용자의 ID 저장
                }

                // 닉네임이 고정되지 않은 경우 랜덤 닉네임 생성
                if (!isNicknameFixed) {
                    nickname = generateRandomNickname();
                }

                // 마이 페이지 버튼 활성화
                myPageButton.setEnabled(true);

                // 닉네임 상태 저장
                saveNicknameStatus();
            } else {
                showMessage(frame, "로그인 실패: 아이디 또는 비밀번호가 잘못되었습니다.", "경고", JOptionPane.WARNING_MESSAGE);
            }
        });
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 0;
        topPanel.add(loginButton, gbc);

        // "마이 페이지" 버튼 추가 (초기에는 비활성화 상태)
        myPageButton = createButton("마이 페이지", e -> {
            if (getIsLoggedIn()) {
                PostRepository postRepository = new PostRepository();
                CommentRepository commentRepository = new CommentRepository(DatabaseConnector.getConnection()); // 댓글 리포지토리 추가

                List<post> userPosts = postRepository.getPostsByUser(getCurrentUserId()); // 사용자가 작성한 글 불러오기
                List<Comment> userComments = commentRepository.fetchCommentsByUser(getCurrentUserId()); // 사용자가 작성한 댓글 불러오기

                if (userPosts.isEmpty() && userComments.isEmpty()) {
                    showMessage(frame, "작성한 글과 댓글이 없습니다.", "정보", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    MyPostsDialog myPostsDialog = new MyPostsDialog(userPosts, listModel, loggedInUsername, currentUserId);
                    myPostsDialog.setVisible(true);
                }
            }
        });
        myPageButton.setEnabled(isLoggedIn); // 로그인 상태에 따른 초기 활성화 상태 설정
        gbc.gridx = 5;
        gbc.gridy = 0;
        topPanel.add(myPageButton, gbc);

        // 리스트 패널 (중앙)
        JPanel listPanel = new JPanel();
        listModel = new DefaultListModel<>(); // 리스트 모델 생성
        itemList = new JList<>(listModel);
        itemList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // 경계선 추가
        itemList.setBackground(Color.WHITE);
        itemList.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // 프로그램 시작 시 데이터베이스에서 기존 글 및 댓글 불러오기
        loadPostsAndCommentsFromDatabase();

        // 리스트 아이템 클릭 시 글 내용 보여주기
        itemList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 더블 클릭 시
                    String selectedTitle = itemList.getSelectedValue();
                    String content = postMap.get(selectedTitle); // 제목에 해당하는 본문 찾기
                    if (content != null) {
                        // PostRepository 인스턴스 생성
                        PostRepository postRepository = new PostRepository();

                        // postId 가져오기
                        int postId = postRepository.getPostIdByTitle(selectedTitle);

                        // DatabaseConnector를 통해 연결 가져오기
                        Connection connection = DatabaseConnector.getConnection();

                        // PostDetailDialog 생성 및 표시
                        PostDetailDialog postDetailDialog = new PostDetailDialog(frame, selectedTitle, content, postId, getCurrentUser(), getCurrentUserId(), connection);
                        postDetailDialog.setVisible(true);
                    }
                }
            }
        });



        // 스크롤 패널로 리스트 감싸기
        JScrollPane listScrollPane = new JScrollPane(itemList);
        mainPanel.add(listScrollPane, BorderLayout.CENTER);

        // 하단 패널 (글쓰기 버튼)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(new Color(255, 240, 245)); // 하단 배경
        JButton writeButton = createButton("글쓰기", e -> {
            if (!getIsLoggedIn()) {
                showMessage(frame, "로그인 후에 글을 작성할 수 있습니다.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }
            WritePostDialog writePostDialog = new WritePostDialog(frame, listModel, getCurrentUserId());
            writePostDialog.setVisible(true);

            String title = writePostDialog.getTitleText();
            String content = writePostDialog.getContentText();
            if (title != null && !title.isEmpty() && content != null && !content.isEmpty()) {
                if (!listModel.contains(title)) { // 중복 방지
                    listModel.addElement(title); // 제목을 리스트에 추가
                    postMap.put(title, content); // 제목과 본문을 맵에 저장

                    // 댓글 리스트 초기화
                    commentMap.put(title, new DefaultListModel<>());

                    // 데이터베이스에 글 저장
                    savePostToDatabase(title, content, getCurrentUserId());
                }
            }
        });

        bottomPanel.add(writeButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // 프레임 표시
        frame.setVisible(true);
    }

    // 프로그램 시작 시 데이터베이스에서 기존 글 및 댓글 불러오기
    private static void loadPostsAndCommentsFromDatabase() {
        PostRepository postRepository = new PostRepository();
        CommentRepository commentRepository = new CommentRepository(DatabaseConnector.getConnection());

        // 모든 글 불러오기
        List<post> allPosts = postRepository.getAllPosts();

        for (post post : allPosts) {
            if (!listModel.contains(post.getTitle())) {
                listModel.addElement(post.getTitle());
                postMap.put(post.getTitle(), post.getContent());

                // 댓글 불러오기
                List<Comment> comments = commentRepository.loadCommentsByPost(post.getId()); // getId() 메서드가 post ID를 반환한다고 가정
                DefaultListModel<String> commentListModel = new DefaultListModel<>();
                for (Comment comment : comments) {
                    commentListModel.addElement(comment.getContent());
                }
                commentMap.put(post.getTitle(), commentListModel);
            }
        }
    }

    // 로그인 상태를 확인하는 메서드
    public static boolean getIsLoggedIn() {
        return isLoggedIn;
    }

    // 현재 로그인한 사용자 ID를 반환하는 메서드
    public static int getCurrentUserId() {
        return currentUserId;
    }

    // 로그인된 사용자 이름을 반환하는 함수
    public static String getCurrentUser() {
        return loggedInUsername;
    }

    // 플레이스홀더 추가 메서드
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
                    textComponent.setForeground(Color.GRAY); // 포커스를 잃으면 다시 회색 플레이스호드
                    textComponent.setText(placeholder);
                }
            }
        });
    }

    // 데이터베이스에 글을 저장하는 메서드
    private static void savePostToDatabase(String title, String content, int userId) {
        PostRepository postRepository = new PostRepository();
        postRepository.savePost(title, content, userId);
    }

    // 리스트에서 특정 글을 삭제하는 메서드
    public static void removePostFromList(String title) {
        if (listModel.contains(title)) {
            listModel.removeElement(title);
            postMap.remove(title);
            commentMap.remove(title); // 해당 글의 댓글도 제거
        }
    }

    // 공통 메시지 표시 메서드
    private static void showMessage(Component parent, String message, String title, int messageType) {
        JOptionPane.showMessageDialog(parent, message, title, messageType);
    }

    // 공통 버튼 생성 메서드
    private static JButton createButton(String text, java.awt.event.ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setBackground(new Color(153, 102, 255));
        button.setForeground(Color.WHITE);
        button.addActionListener(actionListener);
        return button;
    }

    // 랜덤 닉네임 생성 메서드 추가
    private static String generateRandomNickname() {
        String[] randomNicknames = {"하늘을 달리는 호랑이", "웃는 바람", "고요한 바다", "내리오르는 사이", "작은 별빛", "달리는 규", "춤춤는 폴"};
        Random random = new Random();
        return randomNicknames[random.nextInt(randomNicknames.length)];
    }

    // 닉네임 상태 저장 메서드
    private static void saveNicknameStatus() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NICKNAME_FILE))) {
            writer.write(nickname + "\n");
            writer.write(isNicknameFixed ? "true" : "false");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 닉네임 상태 로드 메서드
    private static void loadNicknameStatus() {
        try (BufferedReader reader = new BufferedReader(new FileReader(NICKNAME_FILE))) {
            nickname = reader.readLine();
            isNicknameFixed = Boolean.parseBoolean(reader.readLine());
        } catch (IOException e) {
            nickname = generateRandomNickname();
            isNicknameFixed = false;
        }
    }
}
