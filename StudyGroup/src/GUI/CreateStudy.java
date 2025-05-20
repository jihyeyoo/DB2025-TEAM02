package GUI;

import DAO.CreateStudyDAO;
import DTO.CreateStudyDTO;
import main.AppMain;

import javax.swing.*;
import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CreateStudy extends JFrame {

    // 입력 필드 선언
    private JTextField nameField, startDateField, endDateField, depositField;
    private JTextArea descriptionArea;
    private JComboBox<String> certMethodBox;
    private final String[] certMethods = {"글", "사진", "영상"};  // 인증 방식 옵션

    private String loginId;  // 로그인한 사용자의 ID (study 생성 시 user_id 조회용)

    // 생성자
    public CreateStudy(String loginId) {
        this.loginId = loginId;  // 전달받은 로그인 ID 저장

        // 기본 프레임 설정
        setTitle("스터디 개설");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        // 폰트 설정
        Font titleFont = new Font("맑은 고딕", Font.BOLD, 26);
        Font labelFont = new Font("맑은 고딕", Font.BOLD, 16);
        Font inputFont = new Font("맑은 고딕", Font.PLAIN, 15);

        // 타이틀 라벨
        JLabel title = new JLabel("스터디 개설");
        title.setFont(titleFont);
        title.setBounds(400, 30, 300, 40);
        getContentPane().add(title);

        JLabel notice = new JLabel("* 모든 항목을 입력해주세요.");
        notice.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        notice.setBounds(400, 70, 300, 20);
        getContentPane().add(notice);

        // 입력 필드 위치 설정용 좌표 변수
        int labelX = 200, inputX = 550, width = 300, height = 30;
        int baseY = 110;
        int spacing = 70;
        int yOffset = 0;

        // 스터디 이름 입력
        JLabel nameLabel = new JLabel("스터디 이름:");
        nameLabel.setFont(labelFont);
        nameLabel.setBounds(labelX, baseY + yOffset, 150, height);
        getContentPane().add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(inputX, baseY + yOffset, width, height);
        nameField.setFont(inputFont);
        getContentPane().add(nameField);
        yOffset += spacing;

        // 스터디 소개 입력 (여러 줄 가능)
        JLabel descLabel = new JLabel("스터디 소개:");
        descLabel.setFont(labelFont);
        descLabel.setBounds(labelX, baseY + yOffset, 150, height);
        getContentPane().add(descLabel);

        descriptionArea = new JTextArea();
        descriptionArea.setFont(inputFont);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBounds(inputX, baseY + yOffset, width, 80);
        getContentPane().add(scrollPane);
        yOffset += spacing + 40;

        // 시작일
        JLabel startLabel = new JLabel("시작일 (YYYY-MM-DD):");
        startLabel.setFont(labelFont);
        startLabel.setBounds(labelX, baseY + yOffset, 200, height);
        getContentPane().add(startLabel);

        startDateField = new JTextField();
        startDateField.setBounds(inputX, baseY + yOffset, width, height);
        startDateField.setFont(inputFont);
        getContentPane().add(startDateField);
        yOffset += spacing;

        // 종료일
        JLabel endLabel = new JLabel("종료일 (YYYY-MM-DD):");
        endLabel.setFont(labelFont);
        endLabel.setBounds(labelX, baseY + yOffset, 200, height);
        getContentPane().add(endLabel);

        endDateField = new JTextField();
        endDateField.setBounds(inputX, baseY + yOffset, width, height);
        endDateField.setFont(inputFont);
        getContentPane().add(endDateField);
        yOffset += spacing;

        // 인증 방식 선택 (ComboBox)
        JLabel certLabel = new JLabel("인증 방식:");
        certLabel.setFont(labelFont);
        certLabel.setBounds(labelX, baseY + yOffset, 200, height);
        getContentPane().add(certLabel);

        certMethodBox = new JComboBox<>(certMethods);
        certMethodBox.setBounds(inputX, baseY + yOffset, width, height);
        certMethodBox.setFont(inputFont);
        getContentPane().add(certMethodBox);
        yOffset += spacing;

        // 보증금 입력
        JLabel depositLabel = new JLabel("보증금 (원):");
        depositLabel.setFont(labelFont);
        depositLabel.setBounds(labelX, baseY + yOffset, 200, height);
        getContentPane().add(depositLabel);

        depositField = new JTextField();
        depositField.setBounds(inputX, baseY + yOffset, width, height);
        depositField.setFont(inputFont);
        getContentPane().add(depositField);

        // 버튼 생성
        JButton createBtn = new JButton("개설");
        JButton cancelBtn = new JButton("뒤로가기");

        createBtn.setFont(labelFont);
        cancelBtn.setFont(labelFont);

        createBtn.setBackground(new Color(0xFF6472));
        createBtn.setForeground(Color.WHITE);
        cancelBtn.setBackground(Color.LIGHT_GRAY);

        createBtn.setBounds(400, 570, 130, 40);
        cancelBtn.setBounds(550, 570, 130, 40);

        getContentPane().add(createBtn);
        getContentPane().add(cancelBtn);

        // 스터디 개설 버튼 이벤트
        createBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String desc = descriptionArea.getText().trim();
            String start = startDateField.getText().trim();
            String end = endDateField.getText().trim();
            String cert = (String) certMethodBox.getSelectedItem();
            String depositStr = depositField.getText().trim();

            // 입력 유효성 검사
            if (name.isEmpty() || desc.isEmpty() || start.isEmpty() || end.isEmpty() || depositStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 항목을 입력해주세요.");
                return;
            }

            try {
                int deposit = Integer.parseInt(depositStr); // 숫자인지 확인
                int userId = getUserIdByLoginId(loginId);   // loginId → user_id 조회

                if (userId == -1) {
                    JOptionPane.showMessageDialog(this, "사용자 정보를 찾을 수 없습니다.");
                    return;
                }

                // DTO 생성 후 DAO로 삽입 요청
                CreateStudyDTO dto = new CreateStudyDTO(name, userId, desc, start, end, cert, deposit);
                boolean success = new CreateStudyDAO().insertStudy(dto);

                if (success) {
                    JOptionPane.showMessageDialog(this, "스터디가 성공적으로 개설되었습니다.");
                    dispose();
                    new StudyList(loginId);  // 성공 시 목록으로 복귀
                } else {
                    JOptionPane.showMessageDialog(this, "개설 실패! (DB 확인)");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "입력 형식을 다시 확인해주세요.");
            }
        });

        // 뒤로가기 버튼 이벤트
        cancelBtn.addActionListener(e -> {
            dispose();
            new StudyList(loginId);  // 목록으로 돌아감
        });

        setVisible(true);
    }

    // login_id로 user_id를 조회하는 메서드
    private int getUserIdByLoginId(String loginId) {
        String sql = "SELECT user_id FROM Users WHERE login_id = ?";
        try (PreparedStatement ps = AppMain.conn.prepareStatement(sql)) {
            ps.setString(1, loginId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;  // 조회 실패 시 -1 반환
    }
}
