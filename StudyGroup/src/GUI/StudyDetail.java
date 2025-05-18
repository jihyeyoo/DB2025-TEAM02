package GUI;

import DAO.StudyDetailDAO;
import DTO.StudyDetailDTO;

import javax.swing.*;
import java.awt.*;

public class StudyDetail extends JFrame {

    public StudyDetail(int studyId) {
        setTitle("스터디 상세 정보");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        JLabel title = new JLabel("스터디 상세 정보");
        title.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        title.setBounds(230, 30, 300, 40);
        add(title);

        StudyDetailDAO dao = new StudyDetailDAO();
        StudyDetailDTO dto = dao.getStudyDetail(studyId);

        if (dto == null) {
            JOptionPane.showMessageDialog(this, "해당 스터디 정보를 불러올 수 없습니다.");
            dispose();
            new StudyList();
            return;
        }

        int y = 100, spacing = 60;
        Font labelFont = new Font("맑은 고딕", Font.BOLD, 16);
        Font valueFont = new Font("맑은 고딕", Font.PLAIN, 15);

        JLabel[] labels = {
            new JLabel("이름:"), new JLabel("소개:"), new JLabel("시작일:"),
            new JLabel("종료일:"), new JLabel("인증 방식:"), new JLabel("보증금:")
        };

        String[] values = {
            dto.getName(), dto.getDescription(), dto.getStartDate(),
            dto.getEndDate(), dto.getCertMethod(), dto.getDeposit() + " 원"
        };

        for (int i = 0; i < labels.length; i++) {
            labels[i].setFont(labelFont);
            labels[i].setBounds(100, y, 150, 30);
            add(labels[i]);

            JTextArea value = new JTextArea(values[i]);
            value.setFont(valueFont);
            value.setEditable(false);
            value.setLineWrap(true);
            JScrollPane scroll = new JScrollPane(value);
            scroll.setBounds(250, y, 350, (i == 1) ? 80 : 30);
            add(scroll);

            y += (i == 1) ? spacing + 30 : spacing;
        }

        JButton backBtn = new JButton("뒤로가기");
        backBtn.setBounds(280, y + 30, 120, 40);
        backBtn.setFont(labelFont);
        backBtn.addActionListener(e -> {
            dispose();
            new StudyList();
        });
        add(backBtn);

        setVisible(true);
    }
}
