package GUI;

import DAO.DailyCertsDAO;
import DAO.MyPageDAO;
import DAO.MyStudyDAO;
import DTO.UserDTO;

import javax.swing.*;
import java.awt.*;

public class UserInfo extends JFrame {

    public UserInfo(UserDTO user) {
        setTitle("내 정보");
        setSize(400, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel title = new JLabel("내 정보 보기", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        title.setBounds(100, 20, 200, 30);
        add(title);

        // 기본 정보
        JLabel nameLabel = new JLabel("이름: " + user.getUserName());
        nameLabel.setBounds(50, 80, 300, 25);
        add(nameLabel);

        JLabel idLabel = new JLabel("아이디: " + user.getLoginId());
        idLabel.setBounds(50, 110, 300, 25);
        add(idLabel);

        JLabel pointLabel = new JLabel("보유 포인트: " + user.getPoints() + "P");
        pointLabel.setBounds(50, 140, 300, 25);
        add(pointLabel);

        // 활동 요약 정보
        int studyCount = new MyStudyDAO().getStudyCountByUser(user.getUserId());
        int refundCount = new MyPageDAO().getRefundedDepositsByUser(user.getUserId()).size();

        JLabel studyLabel = new JLabel("참여한 스터디 수: " + studyCount);
        studyLabel.setBounds(50, 190, 300, 25);
        add(studyLabel);

        JLabel refundLabel = new JLabel("환급 받은 횟수: " + refundCount);
        refundLabel.setBounds(50, 220, 300, 25);
        add(refundLabel);

        JButton closeBtn = new JButton("닫기");
        closeBtn.setBounds(140, 300, 100, 30);
        closeBtn.addActionListener(e -> dispose());
        add(closeBtn);

        setVisible(true);
    }
}