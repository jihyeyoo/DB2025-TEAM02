package GUI;

import DAO.MyStudyDetailDAO;
import DTO.MyStudyDetailDTO;
import DTO.StudyMemberDTO;
import DTO.RuleDTO;
import DTO.UserDTO;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class MyStudyDetailPage extends JFrame {

    private MyStudyDetailDAO dao = new MyStudyDetailDAO();

    public MyStudyDetailPage(int studyId, UserDTO user) {
        setTitle("📘 마이스터디 상세 페이지");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. 데이터 불러오기
        MyStudyDetailDTO summary = dao.getStudySummary(studyId);
        List<StudyMemberDTO> members = dao.getMemberList(studyId);
        RuleDTO rule = dao.getRuleInfo(studyId);
        boolean isLeader = dao.isLeader(user, studyId); 

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
        String[] cols = isLeader ? new String[]{"이름", "누적 벌금", "관리"} : new String[]{"이름", "누적 벌금"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) {
                return isLeader && column == 2;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);

        for (StudyMemberDTO m : members) {
            if (isLeader) {
                JButton kickBtn = new JButton("강퇴");
                kickBtn.setForeground(Color.RED);
                kickBtn.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            m.getUserName() + "님을 강퇴하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        // TODO: 강퇴 로직
                        JOptionPane.showMessageDialog(this, m.getUserName() + " 강퇴 완료 (가상)");
                    }
                });
                model.addRow(new Object[]{m.getUserName(), m.getAccumulatedFine(), kickBtn});
            } else {
                model.addRow(new Object[]{m.getUserName(), m.getAccumulatedFine()});
            }
        }

        // 버튼 정상 출력 렌더러/에디터 설정
        if (isLeader) {
            table.getColumn("관리").setCellRenderer(new ButtonRenderer());
            table.getColumn("관리").setCellEditor(new ButtonEditor(new JCheckBox()));
        }

        add(new JScrollPane(table), BorderLayout.CENTER);

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

    // 버튼 렌더러
    class ButtonRenderer implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return (Component) value;
        }
    }

    // 버튼 에디터
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            button = (JButton) value;
            return button;
        }

        public Object getCellEditorValue() {
            return button;
        }
    }
}
