package DB2025Team02GUI;

import javax.swing.*;

import DB2025Team02DAO.MyPageDAO;
import DB2025Team02DTO.UserDTO;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChargePoint extends JFrame {

    public ChargePoint(UserDTO user) {
        setTitle("포인트 충전");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        int centerX = 1000 / 2;
        int startY = 200;

        JLabel infoLabel = new JLabel("충전할 포인트를 입력하세요:");
        infoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setBounds(centerX - 250, startY, 500, 40);
        add(infoLabel);

        JTextField pointField = new JTextField();
        pointField.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        pointField.setHorizontalAlignment(JTextField.CENTER);
        pointField.setBounds(centerX - 150, startY + 60, 300, 50);
        add(pointField);

        JButton chargeButton = new JButton("충전");
        chargeButton.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        chargeButton.setBounds(centerX - 75, startY + 140, 150, 50);
        add(chargeButton);

        chargeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String input = pointField.getText();

                try {
                    int chargeAmount = Integer.parseInt(input);

                    if (chargeAmount <= 0) {
                        JOptionPane.showMessageDialog(null, "1 이상의 금액만 입력 가능합니다.");
                        return;
                    }

                    MyPageDAO dao = new MyPageDAO();
                    boolean success = dao.chargePoints(user.getUserId(), chargeAmount);

                    if (success) {
                        JOptionPane.showMessageDialog(null, "포인트 충전 완료!");
                        dispose();
                        user.setPoints(user.getPoints() + chargeAmount);
                        new MyPage(user);
                    } else {
                        JOptionPane.showMessageDialog(null, "충전에 실패했습니다.");
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "숫자만 입력해주세요.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "오류 발생: " + ex.getMessage());
                }
            }
        });

        setVisible(true);
    }
}
