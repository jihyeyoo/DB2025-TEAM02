package GUI;

import DAO.MyStudyDetailDAO;
import DTO.MyStudyDetailDTO;
import DTO.StudyMemberDTO;
import DTO.RuleDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MyStudyDetailPage extends JFrame {

    private MyStudyDetailDAO dao = new MyStudyDetailDAO();

    public MyStudyDetailPage(int studyId) {
        setTitle("📘 마이스터디 상세 페이지");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. 데이터 불러오기
        MyStudyDetailDTO summary = dao.getStudySummary(studyId);
        List<StudyMemberDTO> members = dao.getMemberList(studyId);
        RuleDTO rule = dao.getRuleInfo(studyId);
        boolean isLeader = dao.isLeader("지혜", studyId); // ← 로그인 사용자 이름

        // 2. 상단: 통계 요약
        JPanel topPanel = new JPanel(new GridLayout(0, 1));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        topPanel.add(new JLabel("📌 스터디명: " + summary.getStudyName()));
        topPanel.add(new JLabel("👥 참여 인원: " + summary.getMemberCount()));
        topPanel.add(new JLabel("💸 총 벌금: " + summary.getTotalFine() + "원"));
        topPanel.add(new JLabel("🛠 최근 규칙 수정일: " +
                (summary.getLastModified() != null ? summary.getLastModified().toString() : "없음")));
        add(topPanel, BorderLayout.NORTH);

        // 3. 중단: 참여자 목록 테이블
        String[] cols = isLeader ?
                new String[]{"이름", "누적 벌금", "관리"} :
                new String[]{"이름", "누적 벌금"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);

        for (StudyMemberDTO m : members) {
            if (isLeader) {
                JButton kickBtn = new JButton("강퇴");
                kickBtn.setForeground(Color.RED);
                kickBtn.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            m.getUserName() + "님을 정말 강퇴하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        // TODO: 실제 강퇴 로직 작성
                        JOptionPane.showMessageDialog(this, m.getUserName() + " 강퇴 완료 (가상)");
                    }
                });
                model.addRow(new Object[]{m.getUserName(), m.getAccumulatedFine(), kickBtn});
            } else {
                model.addRow(new Object[]{m.getUserName(), m.getAccumulatedFine()});
            }
        }

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 4. 하단: 규칙 정보
        JPanel rulePanel = new JPanel(new GridLayout(0, 1));
        rulePanel.setBorder(BorderFactory.createTitledBorder("📋 스터디 규칙"));

        if (rule != null) {
            rulePanel.add(new JLabel("인증 마감 시각: " + rule.getCertDeadline()));
            rulePanel.add(new JLabel("인증 주기: " + rule.getCertCycle() + "일"));
            rulePanel.add(new JLabel("유예 기간: " + rule.getGracePeriod() + "일"));
            rulePanel.add(new JLabel("지각 벌금: " + rule.getFineLate() + "원"));
            rulePanel.add(new JLabel("미인증 벌금: " + rule.getFineAbsent() + "원"));
            rulePanel.add(new JLabel("보증금 정산 주기: " + rule.getPtSettleCycle() + "일"));
        } else {
            rulePanel.add(new JLabel("규칙 정보 없음."));
        }

        add(rulePanel, BorderLayout.SOUTH);
        setVisible(true);
    }
}
