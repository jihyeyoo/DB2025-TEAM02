package DB2025Team02GUI;
import DB2025Team02DAO.CreateStudyDAO;
import DB2025Team02DTO.CreateStudyDTO;
import DB2025Team02DTO.RuleDTO;
import DB2025Team02DTO.UserDTO;

import javax.swing.*;
import java.awt.*;
import java.sql.Time;
import java.sql.Date;

public class CreateStudy extends JFrame {
    private JTextField nameField, startField, endField, certMethodField, depositField;
    private JTextArea descriptionArea;
    private JTextField certDeadlineField, certCycleField, gracePeriodField, fineLateField, fineAbsentField, settleCycleField;

    public CreateStudy(UserDTO user) {
        setTitle("스터디 개설");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(null);
        panel.setPreferredSize(new Dimension(1000, 1000));

        JLabel titleLabel = new JLabel("스터디 개설", SwingConstants.CENTER);
        titleLabel.setBounds(400, 30, 200, 40);
        panel.add(titleLabel);

        JLabel nameLabel = new JLabel("스터디 이름");
        nameLabel.setBounds(150, 100, 100, 30);
        panel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(320, 100, 400, 30);
        panel.add(nameField);

        JLabel descLabel = new JLabel("스터디 소개");
        descLabel.setBounds(150, 150, 100, 30);
        panel.add(descLabel);

        descriptionArea = new JTextArea();
        descriptionArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBounds(320, 150, 400, 100);
        panel.add(descScroll);

        JLabel startLabel = new JLabel("시작일 (YYYY-MM-DD)");
        startLabel.setBounds(150, 270, 150, 30);
        panel.add(startLabel);

        startField = new JTextField();
        startField.setBounds(320, 270, 400, 30);
        panel.add(startField);

        JLabel endLabel = new JLabel("종료일 (YYYY-MM-DD)");
        endLabel.setBounds(150, 320, 150, 30);
        panel.add(endLabel);



        endField = new JTextField();
        endField.setBounds(320, 320, 400, 30);
        panel.add(endField);


        JLabel certMethodLabel = new JLabel("인증 방식");
        certMethodLabel.setBounds(150, 370, 100, 30);
        panel.add(certMethodLabel);

        certMethodField = new JTextField();
        certMethodField.setBounds(320, 370, 400, 30);
        panel.add(certMethodField);

        JLabel depositLabel = new JLabel("보증금");
        depositLabel.setBounds(150, 420, 100, 30);
        panel.add(depositLabel);

        depositField = new JTextField();
        depositField.setBounds(320, 420, 400, 30);
        panel.add(depositField);

        JLabel certDeadlineLabel = new JLabel("인증 마감시각 (HH:MM:SS)");
        certDeadlineLabel.setBounds(150, 470, 200, 30);
        panel.add(certDeadlineLabel);

        certDeadlineField = new JTextField("23:00:00");
        certDeadlineField.setBounds(370, 470, 350, 30);
        panel.add(certDeadlineField);

        JLabel certCycleLabel = new JLabel("인증 주기 (일)");
        certCycleLabel.setBounds(150, 510, 200, 30);
        panel.add(certCycleLabel);

        certCycleField = new JTextField("7");
        certCycleField.setBounds(370, 510, 350, 30);
        panel.add(certCycleField);

        JLabel graceLabel = new JLabel("유예 기간 (일)");
        graceLabel.setBounds(150, 550, 200, 30);
        panel.add(graceLabel);

        gracePeriodField = new JTextField("1");
        gracePeriodField.setBounds(370, 550, 350, 30);
        panel.add(gracePeriodField);

        JLabel fineLateLabel = new JLabel("지각 벌금");
        fineLateLabel.setBounds(150, 590, 200, 30);
        panel.add(fineLateLabel);

        fineLateField = new JTextField("500");
        fineLateField.setBounds(370, 590, 350, 30);
        panel.add(fineLateField);

        JLabel fineAbsentLabel = new JLabel("미인증 벌금");
        fineAbsentLabel.setBounds(150, 630, 200, 30);
        panel.add(fineAbsentLabel);

        fineAbsentField = new JTextField("1000");
        fineAbsentField.setBounds(370, 630, 350, 30);
        panel.add(fineAbsentField);

        JLabel settleCycleLabel = new JLabel("보증금 정산 주기 (일)");
        settleCycleLabel.setBounds(150, 670, 200, 30);
        panel.add(settleCycleLabel);

        settleCycleField = new JTextField("7");
        settleCycleField.setBounds(370, 670, 350, 30);
        panel.add(settleCycleField);

        JButton createBtn = new JButton("개설");
        createBtn.setBounds(320, 720, 150, 40);
        panel.add(createBtn);

        JButton cancelBtn = new JButton("취소");
        cancelBtn.setBounds(500, 720, 150, 40);
        panel.add(cancelBtn);

        cancelBtn.addActionListener(e -> {
            dispose();
            new StudyList(user);
        });

        createBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String desc = descriptionArea.getText().trim();
                String startStr = startField.getText().trim();
                String endStr = endField.getText().trim();
                String certMethod = certMethodField.getText().trim();
                String depositStr = depositField.getText().trim();

                if (name.isEmpty() || desc.isEmpty() || startStr.isEmpty() || endStr.isEmpty()
                        || certMethod.isEmpty() || depositStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "모든 필드를 빠짐없이 입력해주세요.");
                    return;
                }

                java.sql.Date startDate;
                java.sql.Date endDate;
                try {
                    startDate = java.sql.Date.valueOf(startStr);
                    endDate = java.sql.Date.valueOf(endStr);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, "날짜 형식이 유효하지 않습니다. (예: 2025-12-31)");
                    return;
                }

                if (!startDate.before(endDate)) {
                    JOptionPane.showMessageDialog(this, "시작일은 종료일보다 이전이어야 합니다.");
                    return;
                }

                java.util.Date now = new java.util.Date();

                if (endDate.before(new java.sql.Date(now.getTime()))) {
                    JOptionPane.showMessageDialog(this, "종료일은 오늘 이후여야 합니다.");
                    return;
                }

                int deposit = Integer.parseInt(depositStr);

                RuleDTO rule = new RuleDTO(
                        Time.valueOf(certDeadlineField.getText().trim()),
                        Integer.parseInt(certCycleField.getText().trim()),
                        Integer.parseInt(gracePeriodField.getText().trim()),
                        Integer.parseInt(fineLateField.getText().trim()),
                        Integer.parseInt(fineAbsentField.getText().trim()),
                        Integer.parseInt(settleCycleField.getText().trim()),
                        new Date(System.currentTimeMillis())
                );

                CreateStudyDTO dto = new CreateStudyDTO(name, user.getUserId(), desc,
                        startStr, endStr, certMethod, deposit, rule);

                boolean ok = new CreateStudyDAO().createStudyGroup(dto);

                if (ok) {
                    JOptionPane.showMessageDialog(this, "스터디 개설 성공!");
                    dispose();
                    new StudyList(user);
                } else {
                    JOptionPane.showMessageDialog(this, "개설 실패. 다시 시도하세요.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "보증금 및 규칙 관련 필드에 숫자만 입력해주세요.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "시간 형식이 잘못되었습니다. (예: 23:00:00)");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "오류 발생: " + ex.getMessage());
            }
        });


        JScrollPane scrollPane = new JScrollPane(panel);
        setContentPane(scrollPane);
        setVisible(true);
    }
}
