package DB2025Team02GUI;

import DB2025Team02DAO.DailyCertsDAO;
import DB2025Team02DAO.MyStudyDAO;
import DB2025Team02DTO.MyStudyDTO;
import DB2025Team02DTO.UserDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class MyCertPage extends JFrame {

    private List<MyStudyDTO> studyList;

    public MyCertPage(UserDTO user) {
        setTitle("스터디 인증 관리");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        studyList = new MyStudyDAO().getMyStudiesWithCertDate(user);

        String[] columnNames = {"스터디명", "다음 인증 마감일", "제출 여부", "내 인증 내역"};
        Object[][] data = new Object[studyList.size()][4];
        
        
        DailyCertsDAO certDao = new DailyCertsDAO();

        for (int i = 0; i < studyList.size(); i++) {
            MyStudyDTO dto = studyList.get(i);

            // [1] 스터디명
            data[i][0] = dto.getStudyName();

            // [2] 다음 인증 마감일 (null-safe)
            String deadlineStr;
            if (dto.getNextCertDate() != null) {
                deadlineStr = dto.getNextCertDate().toString();  // yyyy-MM-dd
                if (dto.getCertDeadline() != null) {
                    String timeStr = dto.getCertDeadline().toString().substring(0, 5); // HH:mm
                    deadlineStr += " " + timeStr;
                }
            } else {
                deadlineStr = "미설정";
            }
            data[i][1] = deadlineStr;

            // [3] 제출 여부 (next_cert_date가 있을 때만 확인)
            if (dto.getNextCertDate() != null) {
                boolean hasCertified = certDao.hasCertifiedBeforeDeadline(
                    user.getUserId(),
                    dto.getStudyId(),
                    dto.getNextCertDate()
                );
                data[i][2] = hasCertified ? "O" : "X";
            } else {
                data[i][2] = "-";
            }

            // [4] 인증 내역 버튼
            data[i][3] = "내역 보기";
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return column == 3;  // "내역 보기"만 클릭 가능
            }
        };


        JTable table = new JTable(model);
        table.setRowHeight(40);
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(header.getWidth(), 50));
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 16));

        table.getColumn("내 인증 내역").setCellRenderer(new HistoryButtonRenderer());
        table.getColumn("내 인증 내역").setCellEditor(new HistoryButtonEditor(new JCheckBox(), studyList, user));

        add(new JScrollPane(table), BorderLayout.CENTER);

        // 인증 제출 버튼 (하단)
        JButton submitButton = new JButton("인증 제출");
        submitButton.setFont(new Font("맑은 고딕", Font.PLAIN, 18));
        submitButton.setBackground(Color.LIGHT_GRAY);

        submitButton.addActionListener(e -> {
            if (studyList.isEmpty()) {
                JOptionPane.showMessageDialog(this, "가입한 스터디가 없습니다.");
            } else {
                // 첫 번째 스터디 기준으로 인증 제출 페이지 열기
                MyStudyDTO dto = studyList.get(0);
                new SubmitCert(dto.getStudyId(), user);
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(submitButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // "내역 보기" 버튼 렌더러
    class HistoryButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public HistoryButtonRenderer() {
            setOpaque(true);
            setText("내역 보기");
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // "내역 보기" 버튼 에디터
    class HistoryButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private int currentRow;
        private List<MyStudyDTO> list;
        private UserDTO user;

        public HistoryButtonEditor(JCheckBox checkBox, List<MyStudyDTO> list, UserDTO user) {
            super(checkBox);
            this.list = list;
            this.user = user;
            button = new JButton("내역 보기");
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            clicked = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) {
                int studyId = list.get(currentRow).getStudyId();
                new ReviewCertHistory(studyId, user.getUserId());
            }
            clicked = false;
            return "내역 보기";
        }

        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }
}
