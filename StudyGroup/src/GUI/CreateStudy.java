package GUI;

import DAO.CreateStudyDAO;
import DTO.CreateStudyDTO;

import javax.swing.*;
import java.awt.*;

public class CreateStudy extends JFrame {

    private JTextField nameField, startField, endField, depositField;
    private JTextArea descriptionArea;
    private JComboBox<String> certMethodBox;

    private final String[] certOptions = {"글", "사진", "영상"};

    public CreateStudy() {
        setTitle("스터디 개설");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        Font labelFont = new Font("맑은 고딕", Font.BOLD, 18);
        Font inputFont = new Font("맑은 고딕", Font.PLAIN, 16);

        JLabel title = new JLabel("스터디 개설");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 28));
        title.setBounds(400, 30, 300, 40);
        getContentPane().add(title);

        String[] labels = {"스터디 이름", "스터디 소개", "시작일 (YYYY-MM-DD)", "종료일 (YYYY-MM-DD)", "인증 방식", "보증금"};
        int y = 100;
        int spacing = 70;

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(labelFont);
            lbl.setBounds(200, y + i * spacing, 250, 30);
            getContentPane().add(lbl);
        }

        nameField = new JTextField();
        nameField.setBounds(500, 100, 300, 35);
        getContentPane().add(nameField);

        descriptionArea = new JTextArea();
        descriptionArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBounds(500, 170, 300, 70);
        getContentPane().add(scrollPane);

        startField = new JTextField();
        startField.setBounds(500, 250, 300, 35);
        getContentPane().add(startField);

        endField = new JTextField();
        endField.setBounds(500, 320, 300, 35);
        getContentPane().add(endField);

        certMethodBox = new JComboBox<>(certOptions);
        certMethodBox.setFont(inputFont);
        certMethodBox.setBounds(500, 390, 300, 35);
        getContentPane().add(certMethodBox);

        depositField = new JTextField();
        depositField.setBounds(500, 460, 300, 35);
        getContentPane().add(depositField);

        JButton createBtn = new JButton("개설");
        createBtn.setBounds(350, 550, 130, 45);
        createBtn.setFont(labelFont);
        createBtn.setBackground(new Color(0xFF6472));
        createBtn.setForeground(Color.WHITE);
        getContentPane().add(createBtn);

        JButton cancelBtn = new JButton("취소");
        cancelBtn.setBounds(500, 550, 130, 45);
        cancelBtn.setFont(labelFont);
        cancelBtn.setBackground(Color.LIGHT_GRAY);
        getContentPane().add(cancelBtn);

        cancelBtn.addActionListener(e -> {
            dispose();        // 창 닫고
            new StudyList();  // 목록으로 돌아감
        });

        createBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String desc = descriptionArea.getText().trim();
                String start = startField.getText().trim();
                String end = endField.getText().trim();
                String cert = (String) certMethodBox.getSelectedItem();
                int deposit = Integer.parseInt(depositField.getText().trim());

                if (name.isEmpty() || desc.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "모든 항목을 입력하세요.");
                    return;
                }

                CreateStudyDTO dto = new CreateStudyDTO(name, 1, desc, start, end, cert, deposit);  // leader_id = 1
                boolean success = new CreateStudyDAO().insertStudy(dto);

                if (success) {
                    JOptionPane.showMessageDialog(this, "스터디 개설 성공!");
                    dispose();
                    new StudyList();
                } else {
                    JOptionPane.showMessageDialog(this, "개설 실패. 다시 시도하세요.");
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "보증금은 숫자로 입력해야 합니다.");
            }
        });

        setVisible(true);
    }

    public static void main(String[] args) {
        new CreateStudy();
    }
}
