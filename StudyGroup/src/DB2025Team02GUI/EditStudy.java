package DB2025Team02GUI;

import DB2025Team02DAO.StudyEditDAO;
import DB2025Team02DTO.StudyEditDTO;
import DB2025Team02DTO.UserDTO;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
/**
 * 스터디 정보 수정을 위한 화면을 구성하는 클래스입니다.
 */
public class EditStudy extends JFrame {
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField endDateField;  // yyyy-MM-dd
    private JTextField certMethodField;
    private JComboBox<String> certMethodCombo;
    private JTextField settleCycleField; // 정산 주기 직접 입력

    private StudyEditDTO study;
    private UserDTO user;
    private JFrame previousPage;

    public EditStudy(StudyEditDTO study, UserDTO user, JFrame previousPage) {
        this.study = (study != null) ? study : new StudyEditDTO();
        this.user = user;
        this.previousPage = previousPage;

        setTitle("스터디 정보 수정");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        nameField = new JTextField(study.getName() != null ? study.getName() : "");
        descriptionArea = new JTextArea(study.getDescription() != null ? study.getDescription() : "", 3, 20);
        endDateField = new JTextField(study.getEndDate() != null ? study.getEndDate().toString() : "");
        certMethodField = new JTextField(study.getCertMethod() != null ? study.getCertMethod().toString() : "");
        settleCycleField = new JTextField(String.valueOf(study.getSettlementCycle()));

        panel.add(new JLabel("스터디 이름"));
        panel.add(nameField);

        panel.add(new JLabel("설명"));
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        panel.add(scrollPane);

        panel.add(new JLabel("종료일 (yyyy-MM-dd)"));
        panel.add(endDateField);

        panel.add(new JLabel("인증 방식"));
        panel.add(certMethodField);

        panel.add(new JLabel("정산 주기 (일)"));
        panel.add(settleCycleField);

        JButton saveButton = new JButton("수정 저장");
        panel.add(new JLabel());
        panel.add(saveButton);

        add(panel, BorderLayout.CENTER);

        saveButton.addActionListener(e -> {
            if (updateStudyInfo()) {
                JOptionPane.showMessageDialog(this, "스터디 정보가 수정되었습니다.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "수정에 실패했습니다.");
            }
        });

        JPanel backPanel = new JPanel();
        add(backPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private boolean updateStudyInfo() {
        try {
            study.setName(nameField.getText().trim());
            study.setDescription(descriptionArea.getText().trim());

            String endDateText = endDateField.getText().trim();
            if (!endDateText.isEmpty()) {
                study.setEndDate(Date.valueOf(endDateText));
            }

            String certMethod = certMethodField.getText().trim();
            if (!endDateText.isEmpty()) {
                study.setCertMethod(certMethod);
            }

            String cycleText = settleCycleField.getText().trim();
            if (!cycleText.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "정산 주기는 숫자만 입력해주세요.");
                return false;
            }
            int cycle = Integer.parseInt(cycleText);
            study.setSettlementCycle(cycle);

            return new StudyEditDAO().updateStudyInfo(study);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
