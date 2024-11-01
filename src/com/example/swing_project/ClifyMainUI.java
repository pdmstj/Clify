package com.example.swing_project;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

public class ClifyMainUI {

    private static DefaultListModel<String> listModel;
    private static HashMap<String, String> postMap = new HashMap<>(); // 제목과 본문을 저장하는 맵

    public static void main(String[] args) {
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
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(204, 153, 255)); // 연한 보라색 배경
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 패딩 추가
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel("Clify");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel);

        // 검색 필드에 플레이스홀더 추가
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        addPlaceholderText(searchField, "검색어를 입력하세요...");
        JButton searchButton = new JButton("검색");
        searchButton.setBackground(new Color(153, 102, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        topPanel.add(searchField);
        topPanel.add(searchButton);

        // 로그인 패널 (우측)
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(new Color(255, 228, 255)); // 연한 보라 핑크 배경
        loginPanel.setLayout(new GridLayout(5, 1, 10, 10)); // 그리드 레이아웃
        loginPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 패딩 추가
        mainPanel.add(loginPanel, BorderLayout.EAST);

        // ID 필드에 플레이스홀더 추가
        JTextField idField = new JTextField(10);
        addPlaceholderText(idField, "ID");

        // PASSWORD 필드에 플레이스홀더 추가
        JPasswordField passwordField = new JPasswordField(10);
        addPlaceholderText(passwordField, "PASSWORD");

        JCheckBox autoLoginCheck = new JCheckBox("자동 로그인");
        JButton loginButton = new JButton("로그인");
        JButton signUpButton = new JButton("회원가입");

        loginButton.setBackground(new Color(153, 102, 255));
        loginButton.setForeground(Color.WHITE);
        signUpButton.setBackground(new Color(204, 153, 255));
        signUpButton.setForeground(Color.WHITE);

        loginPanel.add(idField);
        loginPanel.add(passwordField);
        loginPanel.add(autoLoginCheck);
        loginPanel.add(loginButton);
        loginPanel.add(signUpButton);

        // 회원가입 버튼 액션 추가
        signUpButton.addActionListener(e -> {
            SignUpDialog signUpDialog = new SignUpDialog(frame); // 회원가입 창 호출
            signUpDialog.setVisible(true); // 회원가입 창 띄우기
        });

        // 리스트 패널 (중앙)
        JPanel listPanel = new JPanel();
        listModel = new DefaultListModel<>(); // 리스트 모델 생성
        JList<String> itemList = new JList<>(listModel);
        itemList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // 경계선 추가
        itemList.setBackground(Color.WHITE);
        itemList.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // 리스트 아이템 클릭 시 글 내용 보여주기
        itemList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // 더블 클릭 시
                    String selectedTitle = itemList.getSelectedValue();
                    String content = postMap.get(selectedTitle); // 제목에 해당하는 본문 찾기
                    if (content != null) {
                        // 새로운 창에 글 내용을 표시
                        PostDetailDialog postDetailDialog = new PostDetailDialog(frame, selectedTitle, content);
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
        JButton writeButton = new JButton("글쓰기");
        writeButton.setBackground(new Color(204, 153, 255));
        writeButton.setForeground(Color.WHITE);

        // 글쓰기 버튼 클릭 시 글쓰기 창 호출
        writeButton.addActionListener(e -> {
            WritePostDialog writePostDialog = new WritePostDialog(frame, listModel);
            writePostDialog.setVisible(true);

            // 글쓰기 창에서 입력된 값을 리스트에 추가
            String title = writePostDialog.getTitleText();
            String content = writePostDialog.getContentText();
            if (!title.isEmpty() && !content.isEmpty()) {
                listModel.addElement(title); // 제목을 리스트에 추가
                postMap.put(title, content); // 제목과 본문을 맵에 저장
            }
        });

        bottomPanel.add(writeButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // 프레임 표시
        frame.setVisible(true);
    }

    // 플레이스홀더 추가 메소드
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
                    textComponent.setForeground(Color.GRAY); // 포커스를 잃으면 다시 회색 플레이스홀더
                    textComponent.setText(placeholder);
                }
            }
        });
    }
}
