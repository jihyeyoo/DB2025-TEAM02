package GUI;

import DAO.MyStudyDAO;
import DTO.MyStudyDTO;
import DTO.UserDTO;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class MyStudyPage extends JFrame {

    public MyStudyPage(UserDTO user) {
        setTitle("자기 스터디 조회 페이지");
        setSize(900, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        List<MyStudyDTO> studyList = new MyStudyDAO().getMyStudies(user);

        String[] columnNames = {"스터디명", "스터디장", "시작일", "정보", "탈퇴"};
        Object[][] data = new Object[studyList.size()][5];

        for (int i = 0; i < studyList.size(); i++) {
            MyStudyDTO dto = studyList.get(i);
            data[i][0] = dto.getStudyName();
            data[i][1] = dto.getLeaderName();
            data[i][2] = dto.getStartDate();
            data[i][3] = "정보 보기";
            data[i][4] = "탈퇴";
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getColumn("정보").setCellRenderer(new InfoButtonRenderer());
        table.getColumn("정보").setCellEditor(new InfoButtonEditor(new JCheckBox(), studyList, user));
        table.getColumn("탈퇴").setCellRenderer(new WithdrawButtonRenderer());
        table.getColumn("탈퇴").setCellEditor(new WithdrawButtonEditor(new JCheckBox(), studyList, user));

        add(new JScrollPane(table));
        setVisible(true);
    }

    // 정보 버튼 렌더러
    class InfoButtonRenderer extends JButton implements TableCellRenderer {
        public InfoButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText("정보 보기");
            return this;
        }
    }

    // 탈퇴 버튼 렌더러
    class WithdrawButtonRenderer extends JButton implements TableCellRenderer {
        public WithdrawButtonRenderer() {
            setOpaque(true);
            setForeground(Color.WHITE);
            setBackground(Color.RED);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText("탈퇴");
            return this;
        }
    }

    // 정보 보기 버튼 클릭 이벤트
    class InfoButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private List<MyStudyDTO> list;
        private int currentRow;
        private UserDTO user;  // ✅ UserDTO 추가

        public InfoButtonEditor(JCheckBox checkBox, List<MyStudyDTO> list, UserDTO user) {
            super(checkBox);
            this.list = list;
            this.user = user;
            button = new JButton("정보 보기");
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            clicked = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) {
                MyStudyDTO selected = list.get(currentRow);
                // ✅ UserDTO 함께 전달
                new MyStudyDetailPage(selected.getStudyId(), user);
            }
            clicked = false;
            return "정보 보기";
        }

        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }


    // 탈퇴 버튼 클릭 이벤트
    class WithdrawButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private List<MyStudyDTO> list;
        private int currentRow;
        private UserDTO user;

        public WithdrawButtonEditor(JCheckBox checkBox, List<MyStudyDTO> list, UserDTO user) {
            super(checkBox);
            this.list = list;
            this.user = user;
            button = new JButton("탈퇴");
            button.setForeground(Color.WHITE);
            button.setBackground(Color.RED);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            clicked = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) {
                MyStudyDTO selected = list.get(currentRow);
                int confirm = JOptionPane.showConfirmDialog(button,
                        "정말로 " + selected.getStudyName() + " 스터디에서 탈퇴하시겠습니까?",
                        "탈퇴 확인", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean result = new MyStudyDAO().withdrawFromStudy(selected.getStudyId(), user);
                    if (result) {
                        JOptionPane.showMessageDialog(button, "탈퇴가 완료되었습니다.");
                        dispose();
                        new MyStudyPage(user); // 새로고침
                    } else {
                        JOptionPane.showMessageDialog(button, "탈퇴에 실패했습니다.");
                    }
                }
            }
            clicked = false;
            return "탈퇴";
        }

        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }
}

