package GUI;

import main.AppMain;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import DAO.DailyCertsDAO;
import DTO.DailyCertsDTO;

import java.awt.*;
import java.sql.DriverManager;
import java.util.List;


/*

WHAT: 인증 제출 후 || 마이페이지 - 인증 내역 버튼으로 이어주기. 인증 기록 띄워주는 페이지.
WHO: 담당자 - 공세영
TODO: 마이페이지랑 연결하기, 인증 기록 DB랑 연결해서 테이블에 띄워주는 작업까지.

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

        // 01. 제목
        JLabel titleLabel = new JLabel("내 인증 내역", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBounds(400, 30, 200, 40);
        getContentPane().add(titleLabel);

        // 02. 테이블 설정
        String[] columns = {"날짜", "내용", "승인 여부"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        certTable = new JTable(tableModel);
        certTable.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(certTable);
        scrollPane.setBounds(50, 100, 880, 450); // 중앙 테이블 배치
        getContentPane().add(scrollPane);

        // 03. 닫기 버튼
        JButton closeButton = new JButton("닫기");
        closeButton.setBounds(430, 580, 120, 40);
        getContentPane().add(closeButton);

        closeButton.addActionListener(e -> dispose());

        // 04. 테이블 데이터 로드
        loadCertHistory(studyId, userId);

        setVisible(true);
    }

    private void loadCertHistory(int studyId, int userId) {
        DailyCertsDAO dao = new DailyCertsDAO();
        List<DailyCertsDTO> list = dao.getCertificationsForUser(studyId, userId);

        for (DailyCertsDTO dto : list) {
            String approvalText = dto.isApproved() ? "승인됨" : "대기중";
            tableModel.addRow(new Object[]{
                dto.getCertDate().toString(),
                dto.getContent(),
                approvalText
            });
        }

        // 가운데 정렬
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < certTable.getColumnCount(); i++) {
            certTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }


    // 테스트용 main()

}
