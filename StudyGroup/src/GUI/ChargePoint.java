package GUI;

import javax.swing.*;

import DAO.MyPageDAO;
import DTO.UserDTO;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.*;

import main.AppMain;

public class ChargePoint extends JFrame {

    public ChargePoint(UserDTO user) {
        setTitle("포인트 충전");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel infoLabel = new JLabel("충전할 포인트를 입력하세요:");
        infoLabel.setBounds(50, 50, 300, 25);
        add(infoLabel);

        JTextField pointField = new JTextField();
        pointField.setBounds(50, 90, 280, 30);
        add(pointField);

        JButton chargeButton = new JButton("충전");
        chargeButton.setBounds(130, 140, 100, 30);
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
