package DB2025Team02GUI;

import DB2025Team02DAO.DailyCertsDAO;
import DB2025Team02DAO.StudyListDAO;
import DB2025Team02DTO.StudyListDTO;
import DB2025Team02DTO.UserDTO;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmitCert extends JFrame {
    private JTextArea contentArea;
    private JLabel dateLabel;
    private int studyId;
    private UserDTO user;

    private JComboBox<String> studyCombo;
    private Map<String, Integer> studyNameToIdMap = new HashMap<>();

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

        studyCombo = new JComboBox<>();
        studyCombo.setBounds(300, 130, 300, 30);
        getContentPane().add(studyCombo);

        // DB에서 사용자 가입 스터디 불러오기
        StudyListDAO dao = new StudyListDAO();
        List<StudyListDTO> studies = dao.getStudiesByMember(user.getUserId());
        for (StudyListDTO dto : studies) {
            String name = dto.getName();
            studyCombo.addItem(name);
            studyNameToIdMap.put(name, dto.getStudyId());
        }

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
            String dateStr = dateLabel.getText();
            String selectedStudy = (String) studyCombo.getSelectedItem();

            if (description.isEmpty()) {
                JOptionPane.showMessageDialog(this, "인증 내용을 입력해주세요.");
                return;
            }

            int selectedStudyId = studyNameToIdMap.get(selectedStudy);
            DailyCertsDAO certDao = new DailyCertsDAO();

            // 인증 주기 범위 계산
            LocalDate today = LocalDate.now();
            int currentWeekNo = certDao.calculateWeekNo(selectedStudyId, today);

            // 이번 주차 인증 중복 여부 확인
            boolean alreadyCertified = certDao.hasCertifiedWeek(user.getUserId(), selectedStudyId, currentWeekNo);
            if (alreadyCertified) {
                JOptionPane.showMessageDialog(this, "이미 이번 주차에 인증한 기록이 있어요!");
                return;
            }

            // 스터디 시작일 이전인지 확인
            LocalDate studyStartDate = certDao.getStudyStartDate(selectedStudyId);
            if (today.isBefore(studyStartDate)) {
                JOptionPane.showMessageDialog(this, "스터디 시작일 이전에는 인증할 수 없습니다!");
                return;
            }




            boolean result = certDao.submitCertification(user.getUserId(), selectedStudyId, dateStr, description, "pending");

            if (result) {
                JOptionPane.showMessageDialog(this, "제출 완료!");
                dispose();
                new MyPage(user);
                new ReviewCertHistory(selectedStudyId, user.getUserId());
            } else {
                JOptionPane.showMessageDialog(this, "제출 실패. 다시 시도해주세요.");
            }
        });


        // 08. 취소 시 마이페이지로
        cancelBtn.addActionListener(e -> {
            dispose();
            new MyPage(user);
        });

        setVisible(true);
    }
}
