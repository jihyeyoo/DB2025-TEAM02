package DB2025Team02GUI;

import javax.swing.*;
import DB2025Team02DAO.SignUpDAO;

import java.awt.*;
/**
 *  회원 가입을 위한 화면을 구성하는 클래스입니다.
 */
public class SignUp extends JFrame {

    public SignUp() {
        setTitle("회원가입");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Font font = new Font("맑은 고딕", Font.PLAIN, 16);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("아이디:");
        idLabel.setFont(font);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(idLabel, gbc);

        JTextField idField = new JTextField(15);
        idField.setFont(font);
        gbc.gridx = 1;
        panel.add(idField, gbc);

        JLabel nameLabel = new JLabel("이름:");
        nameLabel.setFont(font);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(15);
        nameField.setFont(font);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        JLabel pwLabel = new JLabel("비밀번호:");
        pwLabel.setFont(font);
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(pwLabel, gbc);

        JPasswordField pwField = new JPasswordField(15);
        pwField.setFont(font);
        gbc.gridx = 1;
        panel.add(pwField, gbc);

        JButton signupButton = new JButton("회원가입");
        signupButton.setPreferredSize(new Dimension(180, 40));
        signupButton.setFont(font);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(signupButton, gbc);

        signupButton.addActionListener(e -> {
            String loginId = idField.getText();
            String userName = nameField.getText();
            String password = new String(pwField.getPassword());

            if (loginId.isEmpty() || userName.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "모든 항목을 입력해주세요.");
                return;
            }

            try {
                SignUpDAO dao = new SignUpDAO();
                if (dao.isDuplicateLoginId(loginId)) {
                    JOptionPane.showMessageDialog(null, "이미 사용 중인 아이디입니다.");
                } else {
                    boolean success = dao.registerUser(loginId, userName, password);
                    if (success) {
                        JOptionPane.showMessageDialog(null, "회원가입 성공!");
                        dispose();
                        new Login();
                    } else {
                        JOptionPane.showMessageDialog(null, "회원가입 실패. 다시 시도하세요.");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "DB 오류 발생: " + ex.getMessage());
            }
        });

        add(panel);
        setVisible(true);
    }
}
