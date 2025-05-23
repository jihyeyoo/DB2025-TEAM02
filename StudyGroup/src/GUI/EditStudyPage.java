package GUI;

import DAO.StudyEditDAO;
import DTO.StudyEditDTO;
import DTO.UserDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;

public class EditStudyPage extends JFrame {
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField endDateField;  // yyyy-MM-dd
    private JComboBox<String> certMethodCombo;
    private JComboBox<String> cycleCombo;

    private StudyEditDTO study;
    private UserDTO user;
    private JFrame previousPage;

    public EditStudyPage(StudyEditDTO study, UserDTO user, JFrame previousPage) {
        this.study = (study != null) ? study : new StudyEditDTO();
        this.user = user;
        this.previousPage = previousPage;
        // study가 null일 경우 기본 객체 생성
        if (study == null) {
            study = new StudyEditDTO();
        }
        this.study = study;

        setTitle("스터디 정보 수정");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // null-safe 값 세팅
        nameField = new JTextField(study.getName() != null ? study.getName() : "");
        descriptionArea = new JTextArea(study.getDescription() != null ? study.getDescription() : "", 3, 20);
        endDateField = new JTextField(
            study.getEndDate() != null ? study.getEndDate().toString() : ""
        );

        certMethodCombo = new JComboBox<>(new String[]{"사진 인증", "출석 체크", "퀴즈 제출"});
        certMethodCombo.setSelectedItem(
            study.getCertMethod() != null ? study.getCertMethod() : "사진 인증"
        );

        cycleCombo = new JComboBox<>(new String[]{"매주", "매월"});
        cycleCombo.setSelectedItem(
            study.getSettlementCycle() == 30 ? "매월" : "매주"
        );

        // 배치
        panel.add(new JLabel("스터디 이름"));
        panel.add(nameField);

        panel.add(new JLabel("설명"));
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        panel.add(scrollPane);

        panel.add(new JLabel("종료일 (yyyy-MM-dd)"));
        panel.add(endDateField);

        panel.add(new JLabel("인증 방식"));
        panel.add(certMethodCombo);

        panel.add(new JLabel("정산 주기"));
        panel.add(cycleCombo);

        JButton saveButton = new JButton("수정 저장");
        panel.add(new JLabel());  // 빈칸
        panel.add(saveButton);

        add(panel, BorderLayout.CENTER);

        // 저장 이벤트
        saveButton.addActionListener(e -> {
            if (updateStudyInfo()) {
                JOptionPane.showMessageDialog(this, "스터디 정보가 수정되었습니다.");
                dispose();  // 수정 완료 후 닫기
            } else {
                JOptionPane.showMessageDialog(this, "수정에 실패했습니다.");
            }
        });
        
     // 아래에 뒤로가기 버튼 추가
        JPanel backPanel = new JPanel();
        JButton backButton = new JButton("← 뒤로 가기");
        backButton.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        backButton.setFocusPainted(false);
        backButton.setBackground(Color.LIGHT_GRAY);
        backButton.setForeground(Color.BLACK);
        backPanel.add(backButton);
        add(backPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            dispose(); // 현재 창 닫기
            new MyStudyPage(user, previousPage); // 다시 스터디 목록 창 열기
        });

        
        setVisible(true);
    }

    private boolean updateStudyInfo() {
        try {
            // 값 세팅
            study.setName(nameField.getText().trim());
            study.setDescription(descriptionArea.getText().trim());

            String endDateText = endDateField.getText().trim();
            if (!endDateText.isEmpty()) {
                study.setEndDate(Date.valueOf(endDateText));  // yyyy-MM-dd
            }

            String certMethod = (String) certMethodCombo.getSelectedItem();
            String cycleStr = (String) cycleCombo.getSelectedItem();
            int cycle = cycleStr.equals("매주") ? 7 : 30;

            study.setCertMethod(certMethod);
            study.setSettlementCycle(cycle);

            // DAO 호출
            return new StudyEditDAO().updateStudyInfo(study);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
