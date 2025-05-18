package GUI;

import javax.swing.*;

import DTO.UserDTO;

public class UserInfo extends JFrame {
    public UserInfo(UserDTO user) {
        setTitle("내 정보");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel label = new JLabel("내 정보 페이지는 준비 중입니다.");
        label.setBounds(70, 50, 200, 30);
        add(label);

        setVisible(true);
    }
}
