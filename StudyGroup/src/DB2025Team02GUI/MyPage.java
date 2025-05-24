package DB2025Team02GUI;

import javax.swing.*;
import DB2025Team02DTO.UserDTO;
import java.awt.*;

public class MyPage extends JFrame {

    private JLabel nameLabel, pointLabel;

    public MyPage(UserDTO user) {
        setTitle("마이페이지");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        Font font = new Font("맑은 고딕", Font.PLAIN, 18);
        Font titleFont = new Font("맑은 고딕", Font.BOLD, 32);

        JLabel welcomeLabel = new JLabel("마이페이지", SwingConstants.CENTER);
        welcomeLabel.setFont(titleFont);
        welcomeLabel.setBounds(300, 40, 400, 50);
        add(welcomeLabel);

        nameLabel = new JLabel("이름: " + user.getUserName(), SwingConstants.CENTER);
        nameLabel.setFont(font);
        nameLabel.setBounds(350, 110, 300, 30);
        add(nameLabel);

        pointLabel = new JLabel("보유 포인트: " + user.getPoints() + "점", SwingConstants.CENTER);
        pointLabel.setFont(font);
        pointLabel.setBounds(350, 150, 300, 30);
        add(pointLabel);

        JButton studyButton = new JButton("내 스터디 보기");
        studyButton.setFont(font);
        studyButton.setBounds(380, 210, 220, 50);
        add(studyButton);

        JButton listButton = new JButton("스터디 목록");
        listButton.setFont(font);
        listButton.setBounds(380, 270, 220, 50);
        add(listButton);

        JButton chargeButton = new JButton("포인트 충전");
        chargeButton.setFont(font);
        chargeButton.setBounds(380, 330, 220, 50);
        add(chargeButton);

        JButton refundButton = new JButton("환급 정보");
        refundButton.setFont(font);
        refundButton.setBounds(380, 390, 220, 50);
        add(refundButton);

        JButton logoutButton = new JButton("로그아웃");
        logoutButton.setFont(font);
        logoutButton.setBounds(380, 450, 220, 50);
        add(logoutButton);

        studyButton.addActionListener(e -> {
            setVisible(false);
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
