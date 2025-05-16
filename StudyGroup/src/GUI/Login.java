package GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import DAO.LoginDAO;

public class Login extends JFrame {

    public Login() {
        setTitle("로그인");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JLabel idLabel = new JLabel("아이디:");
        idLabel.setBounds(50, 50, 80, 25);
        JTextField idField = new JTextField();
        idField.setBounds(140, 50, 180, 25);

        JLabel pwLabel = new JLabel("비밀번호:");
        pwLabel.setBounds(50, 100, 80, 25);
        JPasswordField pwField = new JPasswordField();
        pwField.setBounds(140, 100, 180, 25);

        JButton loginButton = new JButton("로그인");
        loginButton.setBounds(80, 160, 100, 30);
        add(loginButton);

        JButton signupButton = new JButton("회원가입");
        signupButton.setBounds(200, 160, 100, 30);
        add(signupButton);

        add(idLabel);
        add(idField);
        add(pwLabel);
        add(pwField);

        // 로그인 버튼 클릭 이벤트
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String loginId = idField.getText();
                String password = new String(pwField.getPassword());

                if (loginId.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "아이디와 비밀번호를 모두 입력해주세요.");
                    return;
                }

                try {
                    LoginDAO dao = new LoginDAO();
                    boolean success = dao.checkLogin(loginId, password);

                    if (success) {
                        JOptionPane.showMessageDialog(null, "로그인 성공!");
                        dispose();  // 로그인 창 닫기
                        new MyPage(loginId);  // 마이페이지로 이동
                    } else {
                        JOptionPane.showMessageDialog(null, "로그인 실패. 아이디 또는 비밀번호 확인.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "DB 연결 오류: " + ex.getMessage());
                }
            }
        });

        // 회원가입 버튼 클릭 이벤트
        signupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();        // 로그인 창 닫기
                new SignUp();     // 회원가입 창 열기
            }
        });

        setVisible(true);
    }
}
