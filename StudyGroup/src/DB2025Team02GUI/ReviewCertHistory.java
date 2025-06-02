package DB2025Team02GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import DB2025Team02DAO.DailyCertsDAO;
import DB2025Team02DTO.DailyCertsDTO;

import java.awt.*;
import java.util.List;


/**
 * 내 인증내역의 날짜, 내용 , 승인 여부를 조회하는 화면을 구성하는 클래스입니다.
 */

public class ReviewCertHistory extends JFrame {
    private JTable certTable;
    private DefaultTableModel tableModel;

    public ReviewCertHistory(int studyId, int userId) {
        setTitle("내 인증 내역");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null); // 절대 위치 배치

        JLabel titleLabel = new JLabel("내 인증 내역", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBounds(400, 30, 200, 40);
        getContentPane().add(titleLabel);

        String[] columns = {"날짜", "내용", "승인 여부"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        certTable = new JTable(tableModel);
        certTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(certTable);
        scrollPane.setBounds(50, 100, 880, 450);
        getContentPane().add(scrollPane);
        JButton closeButton = new JButton("닫기");
        closeButton.setBounds(430, 580, 120, 40);
        getContentPane().add(closeButton);

        closeButton.addActionListener(e -> dispose());

        loadCertHistory(studyId, userId);

        setVisible(true);
    }

    private void loadCertHistory(int studyId, int userId) {
        DailyCertsDAO dao = new DailyCertsDAO();
        List<DailyCertsDTO> list = dao.getCertificationsForUser(studyId, userId);

        for (DailyCertsDTO dto : list) {
            String statusKor = switch (dto.getApprovalStatus()) {
                case "approved" -> "승인됨";
                case "rejected" -> "반려됨";
                case "pending" -> "대기중";
                default -> "알 수 없음";
            };

            tableModel.addRow(new Object[]{
                    dto.getCertDate().toString(),
                    dto.getContent(),
                    statusKor
            });
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < certTable.getColumnCount(); i++) {
            certTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

}
