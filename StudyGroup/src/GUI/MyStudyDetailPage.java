package GUI;

import DAO.MyStudyDetailDAO;
import DTO.MyStudyDetailDTO;

import javax.swing.*;
import java.awt.*;

public class MyStudyDetailPage extends JFrame {

    public MyStudyDetailPage(int studyId) {
        setTitle("📝 마이스터디 상세 페이지");
        setSize(400, 300);
        setLocationRelativeTo(null);

        // 🔽 studyId 기반 상세 정보 조회
        MyStudyDetailDTO detail = new MyStudyDetailDAO().getDetailById(studyId);

        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));
        add(contentPane);

        JLabel titleLabel = new JLabel("📌 마이스터디 정보");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        contentPane.add(titleLabel);

        contentPane.add(makeLabel("스터디명: " + detail.getStudyName()));
        contentPane.add(makeLabel("스터디장: " + detail.getLeaderName()));
        contentPane.add(makeLabel("참여인원 수: " + detail.getMemberCount()));
        contentPane.add(makeLabel("총 벌금: " + detail.getTotalFine()));
        contentPane.add(makeLabel("최근 수정일: " +
                (detail.getLastModified() != null ? detail.getLastModified().toString() : "없음")));

        setVisible(true);
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        label.setBorder(new javax.swing.border.EmptyBorder(5, 0, 5, 0));
        return label;
    }
}
