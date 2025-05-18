package GUI;

import DAO.StudyListDAO;
import DTO.StudyListDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class StudyList extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;

    public StudyList() {
        setTitle("스터디 목록");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("스터디 목록");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setBounds(420, 30, 200, 40);
        getContentPane().add(titleLabel);

        // 번호, 이름, 시작일, 종료일, 인증방식, 보증금
        String[] columnNames = {"번호", "이름", "시작일", "종료일", "인증방식", "보증금"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 100, 880, 400);
        getContentPane().add(scrollPane);

        JButton refreshBtn = new JButton("새로고침");
        refreshBtn.setBounds(320, 520, 150, 40);
        refreshBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        refreshBtn.setBackground(new Color(0xFF6472));
        refreshBtn.setForeground(Color.WHITE);
        getContentPane().add(refreshBtn);

        JButton createBtn = new JButton("스터디 개설");
        createBtn.setBounds(500, 520, 150, 40);
        createBtn.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        createBtn.setBackground(new Color(0xFF6472));
        createBtn.setForeground(Color.WHITE);
        getContentPane().add(createBtn);

        refreshBtn.addActionListener(e -> loadStudyData());
        createBtn.addActionListener(e -> {
            dispose();
            new CreateStudy();
        });
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    int studyId = (int) tableModel.getValueAt(row, 0); // 실제 ID 사용
                    dispose();
                    new StudyDetail(studyId);
                }
            }
        });

        loadStudyData();
        setVisible(true);
    }

    private void loadStudyData() {
        tableModel.setRowCount(0);
        StudyListDAO dao = new StudyListDAO();
        List<StudyListDTO> list = dao.getAllStudies();

        int index = 1;
        for (StudyListDTO dto : list) {
            tableModel.addRow(new Object[]{
                index++,                        // ✅ 사용자용 번호
                dto.getName(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getCertMethod(),
                dto.getDeposit()
            });
        }
    }

    public static void main(String[] args) {
        new StudyList();
    }
}
