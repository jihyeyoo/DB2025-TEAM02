package GUI;

import DTO.DailyCertsDTO;
import DAO.DailyCertsDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ManageCertsPage extends JFrame {
    private DailyCertsDAO dao;
    private DefaultTableModel model;
    private JTable table;
    private int studyId;

    public ManageCertsPage(int studyId) {
        this.studyId = studyId;
        dao = new DailyCertsDAO();

        setTitle("인증 승인 관리");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        model = new DefaultTableModel(new String[]{"인증 ID", "사용자 ID", "날짜", "내용", "승인", "반려"}, 0);
        table = new JTable(model);
        table.setRowHeight(30);

        table.getColumn("승인").setCellRenderer(new ButtonRenderer("승인"));
        table.getColumn("승인").setCellEditor(new ButtonEditor(new JCheckBox(), true));

        table.getColumn("반려").setCellRenderer(new ButtonRenderer("반려"));
        table.getColumn("반려").setCellEditor(new ButtonEditor(new JCheckBox(), false));

        loadTableData();

        JButton backButton = new JButton("뒤로가기");
        backButton.addActionListener(e -> dispose());

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }

    private void loadTableData() {
        model.setRowCount(0);
        List<DailyCertsDTO> list = dao.getPendingCertsForStudy(studyId);
        for (DailyCertsDTO cert : list) {
            model.addRow(new Object[]{
                    cert.getCertId(),
                    cert.getUserId(),
                    cert.getCertDate(),
                    cert.getContent(),
                    "승인", "반려"
            });
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String label) {
            setText(label);
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private int row;
        private boolean approve;

        public ButtonEditor(JCheckBox checkBox, boolean approve) {
            super(checkBox);
            this.approve = approve;
            button = new JButton(approve ? "승인" : "반려");
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.row = row;
            this.clicked = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) {
                int certId = (int) table.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(button,
                        approve ? "해당 인증을 승인하시겠습니까?" : "해당 인증을 반려하시겠습니까?",
                        "확인", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean result = dao.updateCertificationStatus(certId, approve);
                    if (result) {
                        JOptionPane.showMessageDialog(button, "처리가 완료되었습니다.");
                        loadTableData();
                    } else {
                        JOptionPane.showMessageDialog(button, "처리에 실패했습니다.");
                    }
                }
            }
            clicked = false;
            return approve ? "승인" : "반려";
        }

        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }
}
