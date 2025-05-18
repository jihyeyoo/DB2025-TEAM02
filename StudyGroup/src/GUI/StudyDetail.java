package GUI;

import DAO.StudyDetailDAO;
import DTO.StudyDetailDTO;

import javax.swing.*;
import java.awt.*;

public class StudyDetail extends JFrame {

    private JLabel nameLabel, startDateLabel, endDateLabel, certMethodLabel, depositLabel;
    private JTextArea descriptionArea;

    private int studyId;
    private String loginId;

    public StudyDetail(int studyId, String loginId) {
        this.studyId = studyId;
        this.loginId = loginId;

        setTitle("스터디 상세 정보");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        Font titleFont = new Font("맑은 고딕", Font.BOLD, 26);
        Font labelFont = new Font("맑은 고딕", Font.BOLD, 18);
        Font contentFont = new Font("맑은 고딕", Font.PLAIN, 16);

        JLabel title = new JLabel("스터디 상세 정보");
        title.setFont(titleFont);
        title.setBounds(220, 30, 300, 40);
        getContentPane().add(title);

        int labelX = 100, labelWidth = 150, fieldX = 260, width = 300, height = 30;
        int y = 100, spacing = 60;

        JLabel nameText = new JLabel("스터디 이름:");
        nameText.setFont(labelFont);
        nameText.setBounds(labelX, y, labelWidth, height);
        getContentPane().add(nameText);

        nameLabel = new JLabel();
        nameLabel.setFont(contentFont);
        nameLabel.setBounds(fieldX, y, width, height);
        getContentPane().add(nameLabel);

        y += spacing;
        JLabel descText = new JLabel("스터디 소개:");
        descText.setFont(labelFont);
        descText.setBounds(labelX, y, labelWidth, height);
        getContentPane().add(descText);

        descriptionArea = new JTextArea();
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBounds(fieldX, y, width, 80);
        getContentPane().add(descScroll);

        y += spacing + 40;
        JLabel startText = new JLabel("시작일:");
        startText.setFont(labelFont);
        startText.setBounds(labelX, y, labelWidth, height);
        getContentPane().add(startText);

        startDateLabel = new JLabel();
        startDateLabel.setFont(contentFont);
        startDateLabel.setBounds(fieldX, y, width, height);
        getContentPane().add(startDateLabel);

        y += spacing;
        JLabel endText = new JLabel("종료일:");
        endText.setFont(labelFont);
        endText.setBounds(labelX, y, labelWidth, height);
        getContentPane().add(endText);

        endDateLabel = new JLabel();
        endDateLabel.setFont(contentFont);
        endDateLabel.setBounds(fieldX, y, width, height);
        getContentPane().add(endDateLabel);

        y += spacing;
        JLabel certText = new JLabel("인증 방식:");
        certText.setFont(labelFont);
        certText.setBounds(labelX, y, labelWidth, height);
        getContentPane().add(certText);

        certMethodLabel = new JLabel();
        certMethodLabel.setFont(contentFont);
        certMethodLabel.setBounds(fieldX, y, width, height);
        getContentPane().add(certMethodLabel);

        y += spacing;
        JLabel depositText = new JLabel("보증금:");
        depositText.setFont(labelFont);
        depositText.setBounds(labelX, y, labelWidth, height);
        getContentPane().add(depositText);

        depositLabel = new JLabel();
        depositLabel.setFont(contentFont);
        depositLabel.setBounds(fieldX, y, width, height);
        getContentPane().add(depositLabel);

        JButton backBtn = new JButton("뒤로가기");
        backBtn.setFont(labelFont);
        backBtn.setBounds(280, y + 60, 120, 40);
        getContentPane().add(backBtn);

        backBtn.addActionListener(e -> {
            dispose();
            new StudyList(loginId);  // ✅ loginId로 StudyList로 복귀
        });

        loadStudyData(studyId);  // ✅ 정보 불러오기
        setVisible(true);
    }

    private void loadStudyData(int studyId) {
        StudyDetailDAO dao = new StudyDetailDAO();
        StudyDetailDTO dto = dao.getStudyDetailById(studyId);

        if (dto != null) {
            nameLabel.setText(dto.getName());
            descriptionArea.setText(dto.getDescription());
            startDateLabel.setText(dto.getStartDate());
            endDateLabel.setText(dto.getEndDate());
            certMethodLabel.setText(dto.getCertMethod());
            depositLabel.setText(dto.getDeposit() + " 원");
        } else {
            JOptionPane.showMessageDialog(this, "스터디 정보를 불러오지 못했습니다.");
        }
    }
}
