package GUI;

import javax.swing.*;

public class MyStudyGroups extends JFrame {
    public MyStudyGroups(String loginId) {
        setTitle("내 스터디 보기");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel label = new JLabel("내 스터디 보기 페이지는 준비 중입니다.");
        label.setBounds(30, 50, 240, 30);
        add(label);

        setVisible(true);
    }
}
