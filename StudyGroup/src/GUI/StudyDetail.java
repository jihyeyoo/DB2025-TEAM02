package GUI;

import javax.swing.*;
import java.awt.*;

/*


목적: StudyList에서 사용자가 선택한 스터디의 상세 정보 교시함.
create.sql의 StudyGroups 참고.
현재는 DB 연동 없는 스탠드 얼론 버전 - 더미 데이터 만들어 둠. 추후 DAO/DTO 연동 시 메소드 넣어서 연결할 것 (TODO)
DB2022 프로젝트 페이지 디자인(ProductDetail?) 참고함.


TO DO:
- DTO랑 DAO 생성 시 연결 필요함. 이것 때문에 아직 Detail과 StudyList랑 연결을 못 해뒀음.... (일단은 테스트할 수 있게 팝업만.)
- 리더 이름, 참여 인원 등은 추후 JOIN 또는 추가 쿼리로 확장할 수 있도록....


*/


public class StudyDetail extends JFrame {

    // 각 항목을 표시할 라벨
    private JLabel nameLabel;
    private JTextArea descriptionArea;
    private JLabel startDateLabel;
    private JLabel endDateLabel;
    private JLabel certMethodLabel;
    private JLabel depositLabel;

    public StudyDetail() {
        setTitle("스터디 상세 정보");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);
        setResizable(false);
        setLocationRelativeTo(null);
        Container contentPane = getContentPane();
        contentPane.setLayout(null);
        contentPane.setBackground(Color.WHITE);

        Font titleFont = new Font("맑은 고딕", Font.BOLD, 30);
        Font labelFont = new Font("맑은 고딕", Font.BOLD, 18);
        Font contentFont = new Font("맑은 고딕", Font.PLAIN, 16);

        // 제목 라벨
        JLabel titleLabel = new JLabel("스터디 상세 정보");
        titleLabel.setBounds(400, 30, 300, 40);
        titleLabel.setFont(titleFont);
        contentPane.add(titleLabel);

        // 항목 라벨 및 값 위치 설정
        int labelX = 250, labelWidth = 200, fieldX = 480, fieldWidth = 300, height = 30;
        int y = 100, spacing = 60;

        // 스터디 이름
        JLabel nameText = new JLabel("스터디 이름:");
        nameText.setBounds(labelX, y, labelWidth, height);
        nameText.setFont(labelFont);
        contentPane.add(nameText);

        nameLabel = new JLabel("스터디 이름 예시");
        nameLabel.setBounds(fieldX, y, fieldWidth, height);
        nameLabel.setFont(contentFont);
        contentPane.add(nameLabel);

        // 스터디 소개
        y += spacing;
        JLabel descText = new JLabel("스터디 소개:");
        descText.setBounds(labelX, y, labelWidth, height);
        descText.setFont(labelFont);
        contentPane.add(descText);

        descriptionArea = new JTextArea("이곳에 스터디 설명을 표시합니다.\n여러 줄도 가능합니다.");
        descriptionArea.setFont(contentFont);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBounds(fieldX, y, fieldWidth, 80);
        contentPane.add(scrollPane);

        // 시작일
        y += spacing + 40;
        JLabel startLabel = new JLabel("시작일:");
        startLabel.setBounds(labelX, y, labelWidth, height);
        startLabel.setFont(labelFont);
        contentPane.add(startLabel);

        startDateLabel = new JLabel("2025-01-01");
        startDateLabel.setBounds(fieldX, y, fieldWidth, height);
        startDateLabel.setFont(contentFont);
        contentPane.add(startDateLabel);

        // 종료일
        y += spacing;
        JLabel endLabel = new JLabel("종료일:");
        endLabel.setBounds(labelX, y, labelWidth, height);
        endLabel.setFont(labelFont);
        contentPane.add(endLabel);

        endDateLabel = new JLabel("2025-12-31");
        endDateLabel.setBounds(fieldX, y, fieldWidth, height);
        endDateLabel.setFont(contentFont);
        contentPane.add(endDateLabel);

        // 인증 방식
        y += spacing;
        JLabel certLabel = new JLabel("인증 방식:");
        certLabel.setBounds(labelX, y, labelWidth, height);
        certLabel.setFont(labelFont);
        contentPane.add(certLabel);

        certMethodLabel = new JLabel("사진(예시)");
        certMethodLabel.setBounds(fieldX, y, fieldWidth, height);
        certMethodLabel.setFont(contentFont);
        contentPane.add(certMethodLabel);

        // 보증금
        y += spacing;
        JLabel depositLabelText = new JLabel("보증금:");
        depositLabelText.setBounds(labelX, y, labelWidth, height);
        depositLabelText.setFont(labelFont);
        contentPane.add(depositLabelText);

        depositLabel = new JLabel("5000 원");
        depositLabel.setBounds(fieldX, y, fieldWidth, height);
        depositLabel.setFont(contentFont);
        contentPane.add(depositLabel);

        // 닫기 버튼
        JButton closeBtn = new JButton("닫기");
        closeBtn.setFont(labelFont);
        closeBtn.setBounds(430, y + 70, 120, 40);
        closeBtn.setBackground(new Color(0xFF6472));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.addActionListener(e -> dispose());
        contentPane.add(closeBtn);

        setVisible(true);
    }

    // TO DO: 추후 DTO를 받아 라벨에 표시하는 생성자 추가 예정
    // public StudyDetail(StudyDTO dto) { ... }
} 
