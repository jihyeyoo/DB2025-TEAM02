package DB2025Team02GUI;

import DB2025Team02DAO.RefundDAO;
import DB2025Team02DTO.RefundInfoDTO;
import DB2025Team02DTO.UserDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
/**
 * 환급 정보를 표시하는 화면을 구성하는 클래스입니다.
 */
public class RefundInfo extends JFrame {

    public RefundInfo(UserDTO user) {
        setTitle("환급 정보");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("환급 정보 조회", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        String[] columnNames = {"스터디명", "보증금", "환급 금액", "환급일"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        List<RefundInfoDTO> refundList = new RefundDAO().getRefundInfoList(user.getUserId());
        for (RefundInfoDTO dto : refundList) {
            model.addRow(new Object[]{
                dto.getStudyName(),
                dto.getDeposit() + "P",
                dto.getRefundAmount() + "P",
                dto.getRefundDate()
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(28);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton closeBtn = new JButton("닫기");
        closeBtn.addActionListener(e -> dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }
}