package GUI;

import javax.swing.*;
import DAO.SignUpDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignUp extends JFrame {

    public SignUp() {
        setTitle("회원가입");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel idLabel = new JLabel("아이디:");
        idLabel.setBounds(50, 50, 100, 30);
        JTextField idField = new JTextField();
        idField.setBounds(150, 50, 150, 30);

        JLabel nameLabel = new JLabel("이름:");
        nameLabel.setBounds(50, 100, 100, 30);
        JTextField nameField = new JTextField();
        nameField.setBounds(150, 100, 150, 30);

        JLabel pwLabel = new JLabel("비밀번호:");
        pwLabel.setBounds(50, 150, 100, 30);
        JPasswordField pwField = new JPasswordField();
        pwField.setBounds(150, 150, 150, 30);

        JButton signupButton = new JButton("회원가입");
        signupButton.setBounds(130, 200, 120, 40);

        add(idLabel);
        add(idField);
        add(nameLabel);
        add(nameField);
        add(pwLabel);
        add(pwField);
        add(signupButton);

        // 버튼 클릭 이벤트 처리
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                            dispose();       // 회원가입 창 닫기
                            new Login();     // 로그인 창으로 이동
                        } else {
                            JOptionPane.showMessageDialog(null, "회원가입 실패. 다시 시도하세요.");
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "DB 오류 발생: " + ex.getMessage());
                }
            }
        });

        setVisible(true);
    }
}
