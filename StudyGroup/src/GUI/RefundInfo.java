package GUI;

import javax.swing.*;

public class RefundInfo extends JFrame {
    public RefundInfo(String loginId) {
        setTitle("환급 정보");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel label = new JLabel("환급 정보 페이지는 준비 중입니다.");
        label.setBounds(50, 50, 200, 30);
        add(label);

        setVisible(true);
    }
}
