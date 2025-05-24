package GUI;

import DAO.DailyCertsDAO;
import DTO.DailyCertsDTO;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ManageCertsPage extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private DailyCertsDAO dao;
    private int studyId;

    private JRadioButton pendingRadio;
    private JRadioButton approvedRadio;

    public ManageCertsPage(int studyId) {
        this.studyId = studyId;
        this.dao = new DailyCertsDAO();

        setTitle("인증 승인 관리");
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(new String[]{"ID", "유저", "날짜", "내용", "승인", "반려"}, 0);
        table = new JTable(model);
        table.setRowHeight(30);

        table.getColumn("승인").setCellRenderer(new ButtonRenderer("승인"));
        table.getColumn("승인").setCellEditor(new ButtonEditor(new JCheckBox(), "approved"));

        table.getColumn("반려").setCellRenderer(new ButtonRenderer("반려"));
        table.getColumn("반려").setCellEditor(new ButtonEditor(new JCheckBox(), "rejected"));

        pendingRadio = new JRadioButton("대기중", true);
        approvedRadio = new JRadioButton("승인됨");

        ButtonGroup group = new ButtonGroup();
        group.add(pendingRadio);
        group.add(approvedRadio);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(pendingRadio);
        topPanel.add(approvedRadio);

        pendingRadio.addActionListener(e -> loadTable("pending"));
        approvedRadio.addActionListener(e -> loadTable("approved"));

        JButton backBtn = new JButton("뒤로가기");
        backBtn.addActionListener(e -> dispose());

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backBtn);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadTable("pending");
        setVisible(true);
    }

    private void loadTable(String status) {
        model.setRowCount(0);

        TableColumnModel columnModel = table.getColumnModel();

        if (status.equals("approved")) {
            if (columnModel.getColumnCount() > 4) {
                columnModel.removeColumn(columnModel.getColumn(5)); // 반려
                columnModel.removeColumn(columnModel.getColumn(4)); // 승인
            }
        } else {
            if (columnModel.getColumnCount() == 4) {
                TableColumn approveCol = new TableColumn(4);
                approveCol.setHeaderValue("승인");
                approveCol.setCellRenderer(new ButtonRenderer("승인"));
                approveCol.setCellEditor(new ButtonEditor(new JCheckBox(), "approved"));
                columnModel.addColumn(approveCol);

                TableColumn rejectCol = new TableColumn(5);
                rejectCol.setHeaderValue("반려");
                rejectCol.setCellRenderer(new ButtonRenderer("반려"));
                rejectCol.setCellEditor(new ButtonEditor(new JCheckBox(), "rejected"));
                columnModel.addColumn(rejectCol);
            }
        }

        List<DailyCertsDTO> list = dao.getCertsByStatus(studyId, status);
        for (DailyCertsDTO dto : list) {
            if (status.equals("pending")) {
                model.addRow(new Object[]{
                        dto.getCertId(),
                        dto.getUserId(),
                        dto.getCertDate(),
                        dto.getContent(),
                        "승인",
                        "반려"
                });
            } else {
                model.addRow(new Object[]{
                        dto.getCertId(),
                        dto.getUserId(),
                        dto.getCertDate(),
                        dto.getContent()
                });
            }
        }
    }


    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String label) {
            setText(label);
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String newStatus;
        private int selectedRow;
        private boolean clicked;

        public ButtonEditor(JCheckBox checkBox, String newStatus) {
            super(checkBox);
            this.newStatus = newStatus;
            this.button = new JButton(newStatus.equals("approved") ? "승인" : "반려");
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.selectedRow = row;
            this.clicked = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) {
                if (selectedRow >= table.getRowCount()) return button.getText();

                int certId = (int) table.getValueAt(selectedRow, 0);
                String actionText = newStatus.equals("approved") ? "승인" : "반려";

                int confirm = JOptionPane.showConfirmDialog(button,
                        "해당 인증을 " + actionText + "하시겠습니까?",
                        "확인", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    boolean success = dao.updateCertificationStatus(certId, newStatus);
                    if (success) {
                        JOptionPane.showMessageDialog(button, "처리 완료되었습니다.");

                        // 🔥 테이블 새로고침은 다음 이벤트 루프에서 실행
                        SwingUtilities.invokeLater(() -> {
                            loadTable(pendingRadio.isSelected() ? "pending" : "approved");
                        });

                    } else {
                        JOptionPane.showMessageDialog(button, "처리에 실패했습니다.");
                    }
                }
            }
            clicked = false;
            return button.getText();
        }


        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }
}
