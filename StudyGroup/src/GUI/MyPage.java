package GUI;

import javax.swing.*;
import DTO.UserDTO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.*;

import main.AppMain;

public class MyPage extends JFrame {

    private JLabel nameLabel, pointLabel;

    public MyPage(UserDTO user) {
        setTitle("마이페이지");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel welcomeLabel = new JLabel("\uD83D\uDC4B 마이페이지");
        welcomeLabel.setBounds(140, 20, 200, 25);
        add(welcomeLabel);

        nameLabel = new JLabel("이름: " + user.getUserName());
        nameLabel.setBounds(50, 60, 300, 25);
        add(nameLabel);

        pointLabel = new JLabel("보유 포인트: " + user.getPoints() + "점");
        pointLabel.setBounds(50, 90, 300, 25);
        add(pointLabel);

        // 주요 기능 버튼
        JButton studyButton = new JButton("내 스터디 보기");
        studyButton.setBounds(50, 140, 140, 30);
        add(studyButton);

        JButton listButton = new JButton("스터디 목록");
        listButton.setBounds(210, 140, 140, 30);
        add(listButton);

        JButton chargeButton = new JButton("포인트 충전");
        chargeButton.setBounds(50, 190, 140, 30);
        add(chargeButton);

        JButton refundButton = new JButton("환급 정보");
        refundButton.setBounds(210, 190, 140, 30);
        add(refundButton);

        // 로그아웃
        JButton logoutButton = new JButton("로그아웃");
        logoutButton.setBounds(130, 240, 120, 30);
        add(logoutButton);

        // 이벤트 연결
        studyButton.addActionListener(e -> {
            setVisible(false);               // 창을 닫지 않고 숨기기
            new MyStudyPage(user, this);     // 현재 MyPage 인스턴스를 넘겨줌
            dispose();
            new MyStudyPage(user, this);
        });

        listButton.addActionListener(e -> {
            dispose();
            new StudyList(user);
        });

        chargeButton.addActionListener(e -> {
            dispose();
            new ChargePoint(user);
        });

        refundButton.addActionListener(e -> new RefundInfo(user));

        logoutButton.addActionListener(e -> {
            dispose();
            new Login();
        });

        setVisible(true);
    }
}
