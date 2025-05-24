package DB2025Team02GUI;

import DB2025Team02DAO.MyStudyDetailDAO;
import DB2025Team02DTO.MyStudyDetailDTO;
import DB2025Team02DTO.StudyMemberDTO;
import DB2025Team02DTO.RuleDTO;
import DB2025Team02DTO.UserDTO;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class MyStudyDetailPage extends JFrame {

    private MyStudyDetailDAO dao = new MyStudyDetailDAO();

    private JFrame previousPage;

    public MyStudyDetailPage(int studyId, UserDTO user, JFrame previousPage) {
        this.previousPage = previousPage;

        setTitle("📘 마이스터디 상세 페이지");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        MyStudyDetailDTO summary = dao.getStudySummary(studyId);
        List<StudyMemberDTO> members = dao.getMemberList(studyId);
        RuleDTO rule = dao.getRuleInfo(studyId);
        boolean isLeader = dao.isLeader(user, studyId);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 5)); // 좌측 정렬, 간격 추가
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        Font baseFont = UIManager.getFont("Label.font");
        Font largeFont = baseFont.deriveFont(baseFont.getSize() + 4.0f);

        JLabel nameLabel = new JLabel("📌 스터디명: " + summary.getStudyName());
        nameLabel.setFont(largeFont);
        topPanel.add(nameLabel);

        JLabel memberLabel = new JLabel("👥 참여 인원: " + summary.getMemberCount());
        memberLabel.setFont(largeFont);
        topPanel.add(memberLabel);

        JLabel fineLabel = new JLabel("💸 총 벌금: " + summary.getTotalFine() + "원");
        fineLabel.setFont(largeFont);
        topPanel.add(fineLabel);

        add(topPanel, BorderLayout.NORTH);


        String[] cols = isLeader ? new String[]{"스터디원", "누적 벌금", "관리"} : new String[]{"이름", "누적 벌금"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) {
                return isLeader && column == 2;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(40);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(header.getWidth(), 50));
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 16));

        for (StudyMemberDTO m : members) {
            if (isLeader) {
                if (m.getUserId() == user.getUserId()) {
                    JButton dummyBtn = new JButton("강퇴");
                    dummyBtn.setEnabled(false);
                    model.addRow(new Object[]{m.getUserName(), m.getAccumulatedFine(), dummyBtn});
                }else{
                    JButton kickBtn = new JButton("강퇴");
                    kickBtn.setForeground(Color.RED);
                    kickBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
                    kickBtn.addActionListener(e -> {
                        int confirm = JOptionPane.showConfirmDialog(this,
                                m.getUserName() + "님을 강퇴하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            boolean success = dao.kickMember(studyId, m.getUserId());
                            if (success) {
                                JOptionPane.showMessageDialog(this, m.getUserName() + " 강퇴 완료");
                                dispose();  // 현재 페이지 닫고
                                new MyStudyDetailPage(studyId, user, previousPage); // 새로고침
                            } else {
                                JOptionPane.showMessageDialog(this, "강퇴 실패. 다시 시도해주세요.");
                            }
                        }
                    });

                    model.addRow(new Object[]{m.getUserName(), m.getAccumulatedFine(), kickBtn});
                }


            } else {
                model.addRow(new Object[]{m.getUserName(), m.getAccumulatedFine()});
            }
        }


        if (isLeader) {
            table.getColumn("관리").setCellRenderer(new ButtonRenderer());
            table.getColumn("관리").setCellEditor(new ButtonEditor(new JCheckBox()));
        }

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel rulePanel = new JPanel(new GridLayout(0, 1));
        Font titleFont = new Font("맑은 고딕", Font.BOLD, 20);
        TitledBorder titleBorder = BorderFactory.createTitledBorder("스터디 규칙");
        titleBorder.setTitleFont(titleFont);
        titleBorder.setTitleJustification(TitledBorder.LEFT); // 왼쪽 정렬
        titleBorder.setTitlePosition(TitledBorder.TOP);
        rulePanel.setBorder(titleBorder);

        Font ruleFont = new Font("맑은 고딕", Font.PLAIN, 16);

        if (rule != null) {
            JLabel label1 = new JLabel("인증 마감 시각: " + rule.getCertDeadline());
            label1.setFont(ruleFont);
            rulePanel.add(label1);

            JLabel label2 = new JLabel("인증 주기: " + rule.getCertCycle() + "일");
            label2.setFont(ruleFont);
            rulePanel.add(label2);

            JLabel label3 = new JLabel("유예 기간: " + rule.getGracePeriod() + "일");
            label3.setFont(ruleFont);
            rulePanel.add(label3);

            JLabel label4 = new JLabel("지각 벌금: " + rule.getFineLate() + "원");
            label4.setFont(ruleFont);
            rulePanel.add(label4);

            JLabel label5 = new JLabel("미인증 벌금: " + rule.getFineAbsent() + "원");
            label5.setFont(ruleFont);
            rulePanel.add(label5);

            JLabel label6 = new JLabel("보증금 정산 주기: " + rule.getPtSettleCycle() + "일");
            label6.setFont(ruleFont);
            rulePanel.add(label6);
        } else {
            JLabel noRuleLabel = new JLabel("규칙 정보 없음.");
            noRuleLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 18)); // 강조용
            rulePanel.add(noRuleLabel);
        }


        rulePanel.setBorder(BorderFactory.createCompoundBorder(
                titleBorder,
                BorderFactory.createEmptyBorder(10, 20, 20, 20) // 내부 여백: 상, 좌, 하, 우
        ));


        bottomPanel.add(rulePanel, BorderLayout.CENTER);

        JPanel backPanel = new JPanel();
        JButton backButton = new JButton("← 뒤로 가기");
        backButton.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        backButton.setFocusPainted(false);
        backButton.setBackground(Color.LIGHT_GRAY);
        backButton.setForeground(Color.BLACK);
        backPanel.add(backButton);
        bottomPanel.add(backPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> {
            dispose();
            previousPage.setVisible(true);
        });

        add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 0, 30));


        setVisible(true);
    }

    class ButtonRenderer implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return (Component) value;
        }
    }

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
