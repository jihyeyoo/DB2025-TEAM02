package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.*;

import main.AppMain;

public class MyPage extends JFrame {

    private JLabel nameLabel, pointLabel;

    public MyPage(String loginId) {
        setTitle("마이페이지");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel welcomeLabel = new JLabel("마이페이지");
        welcomeLabel.setBounds(150, 20, 100, 25);
        add(welcomeLabel);

        nameLabel = new JLabel("이름: ");
        nameLabel.setBounds(50, 70, 300, 25);
        add(nameLabel);

        pointLabel = new JLabel("보유 포인트: ");
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
                dispose(); // 마이페이지 닫고
                new ChargePoint(loginId); // 다음 단계에서 만들 예정
            }
        });
        
        // 연결만
        studyButton.addActionListener(e -> new MyStudyGroups(loginId));
        refundButton.addActionListener(e -> new RefundInfo(loginId));
        infoButton.addActionListener(e -> new UserInfo(loginId));

        // 사용자 정보 불러오기
        loadUserInfo(loginId);

        setVisible(true);
    }

    private void loadUserInfo(String loginId) {
        String sql = "SELECT user_name, points FROM Users WHERE login_id = ?";
        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setString(1, loginId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("user_name");
                int points = rs.getInt("points");

                nameLabel.setText("이름: " + name);
                pointLabel.setText("보유 포인트: " + points + "P");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
