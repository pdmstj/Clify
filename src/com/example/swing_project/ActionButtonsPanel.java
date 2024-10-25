package com.example.swing_project;

import javax.swing.*;
import java.awt.*;

public class ActionButtonsPanel extends JPanel {

    public ActionButtonsPanel(JDialog parentDialog) {
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        setBackground(new Color(255, 240, 245)); // 배경색 설정

        JButton replyButton = new JButton("답장");
        replyButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        replyButton.setBackground(new Color(204, 153, 255));
        replyButton.setForeground(Color.WHITE);
        replyButton.setFocusPainted(false);

        JButton reportButton = new JButton("신고");
        reportButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        reportButton.setBackground(new Color(204, 153, 255));
        reportButton.setForeground(Color.WHITE);
        reportButton.setFocusPainted(false);

        JButton bookmarkButton = new JButton("북마크");
        bookmarkButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        bookmarkButton.setBackground(new Color(204, 153, 255));
        bookmarkButton.setForeground(Color.WHITE);
        bookmarkButton.setFocusPainted(false);

        JButton closeButton = new JButton("닫기");
        closeButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        closeButton.setBackground(new Color(204, 153, 255));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);

        closeButton.addActionListener(e -> parentDialog.dispose());

        add(replyButton);
        add(reportButton);
        add(bookmarkButton);
        add(closeButton);
    }
}
