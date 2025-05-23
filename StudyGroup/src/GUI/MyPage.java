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
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel welcomeLabel = new JLabel("마이페이지");
        welcomeLabel.setBounds(150, 20, 100, 25);
        add(welcomeLabel);

        nameLabel = new JLabel("이름: " +user.getUserName());
        nameLabel.setBounds(50, 70, 300, 25);
        add(nameLabel);

        pointLabel = new JLabel("보유 포인트: " + user.getPoints());
        pointLabel.setBounds(50, 110, 300, 25);
        add(pointLabel);

        JButton chargeButton = new JButton("포인트 충전");
        chargeButton.setBounds(140, 150, 120, 30);
        add(chargeButton);

        // 내 스터디 보기 버튼 (연결만) 
        JButton studyButton = new JButton("내 스터디 보기");
        studyButton.setBounds(50, 200, 140, 30);
        add(studyButton);
        
        // 환급 정보 버튼 (연결만)
        JButton refundButton = new JButton("환급 정보");
        refundButton.setBounds(210, 200, 140, 30);
        add(refundButton);
        
        // 내 정보 버튼 (연결만)
        JButton infoButton = new JButton("내 정보");
        infoButton.setBounds(130, 250, 140, 30);
        add(infoButton);

        // 포인트 충전 화면으로 이동
        chargeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new ChargePoint(user);  // UserDTO 전달
            }
        });

        studyButton.addActionListener(e -> {
            setVisible(false);               // 창을 닫지 않고 숨기기
            new MyStudyPage(user, this);     // 현재 MyPage 인스턴스를 넘겨줌
        });

        refundButton.addActionListener(e -> new RefundInfo(user));
        infoButton.addActionListener(e -> new UserInfo(user));

        setVisible(true);
    }
}
