package GUI;

import DTO.UserDTO;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;


/*

WHAT: 마이페이지 - 내 스터디 목록 - 스터디 디테일(생략 가능) - 인증하기 << 인증 내용 적는 페이지
WHO: 담당자 - 공세영
TODO: 마이페이지랑 연결, DB에서 사용자가 가입한 스터디만 불러와서 옵션으로 넣기

*/


public class SubmitCert extends JFrame {
    private JTextArea contentArea;
    private JLabel dateLabel;
    private int studyId;
    private UserDTO user;

    // 예시용 스터디 목록
    private String[] studyOptions = {"스터디 A", "스터디 B", "스터디 C"}; // TODO: DB에서 사용자별로 불러오기

    public SubmitCert(int studyId, UserDTO user) {
        this.studyId = studyId;
        this.user = user;

        setTitle("스터디 인증 제출");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        // 01. 제목
        JLabel header = new JLabel("인증 제출", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 24));
        header.setBounds(400, 30, 200, 40);
        getContentPane().add(header);

        // 02. 유저 이름
        JLabel nameLabel = new JLabel(user.getUserName() + " 님");
        nameLabel.setBounds(200, 90, 300, 30);
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        getContentPane().add(nameLabel);

        // 03. 스터디 선택
        JLabel studyLabel = new JLabel("스터디 선택:");
        studyLabel.setBounds(200, 130, 100, 30);
        getContentPane().add(studyLabel);

        JComboBox<String> studyCombo = new JComboBox<>(studyOptions);
        studyCombo.setBounds(300, 130, 300, 30);
        getContentPane().add(studyCombo);

        // 04. 날짜
        JLabel dateTextLabel = new JLabel("날짜:");
        dateTextLabel.setBounds(200, 180, 100, 30);
        getContentPane().add(dateTextLabel);

        dateLabel = new JLabel(LocalDate.now().toString());
        dateLabel.setBounds(300, 180, 300, 30);
        getContentPane().add(dateLabel);

        // 05. 인증 내용
        JLabel contentLabel = new JLabel("인증 내용:");
        contentLabel.setBounds(200, 230, 100, 30);
        getContentPane().add(contentLabel);

        contentArea = new JTextArea();
        contentArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBounds(300, 230, 500, 300);
        getContentPane().add(scrollPane);

        // 06. 버튼
        JButton submitBtn = new JButton("제출");
        submitBtn.setBounds(350, 550, 120, 40);
        getContentPane().add(submitBtn);

        JButton cancelBtn = new JButton("취소");
        cancelBtn.setBounds(530, 550, 120, 40);
        getContentPane().add(cancelBtn);

        // 07. 제출 이벤트
        submitBtn.addActionListener(e -> {
            String description = contentArea.getText().trim();
            String date = dateLabel.getText();
            String selectedStudy = (String) studyCombo.getSelectedItem();

            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "인증 내용을 입력해주세요.");
                return;
            }

            System.out.println("===== 인증 제출 =====");
            System.out.println("Study: " + selectedStudy);
            System.out.println("User ID: " + user.getUserId());
            System.out.println("날짜: " + date);
            System.out.println("내용: " + description);

            JOptionPane.showMessageDialog(this, "제출 완료!");
            dispose();
            new MyPage(user);
            new ReviewCertHistory(1, user.getUserId());
        });

        // 08. 취소 시 마이페이지로
        cancelBtn.addActionListener(e -> {
            dispose();
            new MyPage(user);
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new SubmitCert(0, null); // 예시: 스터디 ID 1, 유저 ID 2
    }
}
