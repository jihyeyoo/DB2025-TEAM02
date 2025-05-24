package DB2025Team02GUI;

import javax.swing.*;
import java.awt.*;

import DB2025Team02DAO.LoginDAO;
import DB2025Team02DTO.LoginResultDTO;
import DB2025Team02DTO.LoginResultDTO.LoginStatus;
import DB2025Team02DTO.UserDTO;

public class Login extends JFrame {

    public Login() {
        setTitle("로그인");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        Font font = new Font("맑은 고딕", Font.PLAIN, 18);
        ImageIcon icon = new ImageIcon("res/bannerimage.png");
        Image image = icon.getImage();

        int frameWidth = 1000;
        int originalWidth = image.getWidth(null);
        int originalHeight = image.getHeight(null);

        double ratio = (double) frameWidth / originalWidth;
        int scaledHeight = (int) (originalHeight * ratio);

        Image scaledImage = image.getScaledInstance(frameWidth, scaledHeight, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        JLabel bannerLabel = new JLabel(scaledIcon);
        bannerLabel.setBounds(0, 0, frameWidth, scaledHeight);

        add(bannerLabel);

        JLabel idLabel = new JLabel("아이디:");
        idLabel.setBounds(320, 370, 100, 40);
        idLabel.setFont(font);
        JTextField idField = new JTextField();
        idField.setBounds(430, 370, 250, 40);
        idField.setFont(font);

        JLabel pwLabel = new JLabel("비밀번호:");
        pwLabel.setBounds(320, 430, 100, 40);
        pwLabel.setFont(font);
        JPasswordField pwField = new JPasswordField();
        pwField.setBounds(430, 430, 250, 40);
        pwField.setFont(font);

        JButton loginButton = new JButton("로그인");
        loginButton.setBounds(380, 510, 120, 50);
        loginButton.setFont(font);
        JButton signupButton = new JButton("회원가입");
        signupButton.setBounds(530, 510, 120, 50);
        signupButton.setFont(font);

        add(idLabel);
        add(idField);
        add(pwLabel);
        add(pwField);
        add(loginButton);
        add(signupButton);

        loginButton.addActionListener(e -> {
            String loginId = idField.getText();
            String password = new String(pwField.getPassword());

            if (loginId.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "아이디와 비밀번호를 모두 입력해주세요.");
                return;
            }

            try {
                LoginDAO dao = new LoginDAO();
                LoginResultDTO result = dao.login(loginId, password);

                if (result.getStatus() == LoginStatus.SUCCESS) {
                    UserDTO user = result.getUser();
                    JOptionPane.showMessageDialog(null, user.getUserName() + "님, 환영합니다!");
                    dispose();
                    new StudyList(user);
                } else if (result.getStatus() == LoginStatus.INVALID_PASSWORD) {
                    JOptionPane.showMessageDialog(null, "비밀번호가 일치하지 않습니다.");
                } else if (result.getStatus() == LoginStatus.ID_NOT_FOUND) {
                    JOptionPane.showMessageDialog(null, "존재하지 않는 아이디입니다.");
                } else {
                    JOptionPane.showMessageDialog(null, "로그인 중 오류 발생.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "DB 연결 오류: " + ex.getMessage());
            }
        });

        signupButton.addActionListener(e -> {
            dispose();
            new SignUp();
        });

        setVisible(true);
    }
}
