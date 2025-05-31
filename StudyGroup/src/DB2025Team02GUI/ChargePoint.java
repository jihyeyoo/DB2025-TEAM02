package DB2025Team02GUI;

import javax.swing.*;

import DB2025Team02DAO.MyPageDAO;
import DB2025Team02DTO.UserDTO;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChargePoint extends JFrame {

    public ChargePoint(UserDTO user, JFrame previousPage) {
        setTitle("포인트 충전");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 중앙 패널 (라벨 + 입력 필드만)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(100, 200, 50, 200));

        JLabel infoLabel = new JLabel("충전할 포인트를 입력하세요:");
        infoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(infoLabel);

        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        JTextField pointField = new JTextField();
        pointField.setMaximumSize(new Dimension(300, 50));
        pointField.setFont(new Font("맑은 고딕", Font.PLAIN, 20));
        pointField.setHorizontalAlignment(JTextField.CENTER);
        pointField.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(pointField);

        add(centerPanel, BorderLayout.CENTER);

        // 하단 패널 (충전 버튼 + 뒤로가기 버튼 나란히 배치)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 20));  // 가운데 정렬 + 간격
        JButton chargeButton = new JButton("충전");
        chargeButton.setFont(new Font("맑은 고딕", Font.BOLD, 20));

        JButton backButton = new JButton("뒤로가기");
        backButton.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);

        bottomPanel.add(chargeButton);
        bottomPanel.add(backButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // 이벤트: 뒤로가기
        backButton.addActionListener(e -> {
            dispose();
            if (previousPage != null) previousPage.setVisible(true);
        });

        // 이벤트: 충전
        chargeButton.addActionListener(e -> {
            String input = pointField.getText();

            try {
                int chargeAmount = Integer.parseInt(input);

                if (chargeAmount <= 0) {
                    JOptionPane.showMessageDialog(this, "1 이상의 금액만 입력 가능합니다.");
                    return;
                }

                MyPageDAO dao = new MyPageDAO();
                boolean success = dao.chargePoints(user.getUserId(), chargeAmount);

                if (success) {
                    JOptionPane.showMessageDialog(this, "포인트 충전 완료!");
                    dispose();
                    user.setPoints(user.getPoints() + chargeAmount);
                    new MyPage(user);
                } else {
                    JOptionPane.showMessageDialog(this, "충전에 실패했습니다.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "숫자만 입력해주세요.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "오류 발생: " + ex.getMessage());
            }
        });

        setVisible(true);
    }
}
