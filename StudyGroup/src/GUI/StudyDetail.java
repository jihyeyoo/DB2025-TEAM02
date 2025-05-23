package GUI;
import DAO.StudyDetailDAO;
import DTO.StudyDetailDTO;
import DTO.UserDTO;
import javax.swing.*;
import java.awt.*;


/*

WHAT: 스터디목록에서 선택한 스터디의 세부 정보를 띄워주는 페이지 GUI
WHO: 담당자 - 공세영
TODO: 

*/


public class StudyDetail extends JFrame {
	// studyId를 이용해 선택한 스터디 정보를 DB로부터 뿌려줄 수 있게 함.
    public StudyDetail(int studyId, UserDTO user) {
        setTitle("스터디 상세 정보");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 700);								
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);					

        JLabel title = new JLabel("스터디 상세 정보", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setBounds(400, 30, 200, 40);					
        add(title);

        StudyDetailDAO dao = new StudyDetailDAO();
        StudyDetailDTO dto = dao.getStudyDetail(studyId);

        if (dto == null) {
            JOptionPane.showMessageDialog(this, "해당 스터디 정보를 불러올 수 없습니다.");
            dispose();
            new StudyList(null);
            return;
        }

        int y = 100, spacing = 70; 

        JLabel[] labels = {
            new JLabel("이름:"), new JLabel("소개:"), new JLabel("시작일:"),
            new JLabel("종료일:"), new JLabel("인증 방식:"), new JLabel("보증금:")
        };

        String[] values = {
            dto.getName(), dto.getDescription(), dto.getStartDate(),
            dto.getEndDate(), dto.getCertMethod(), dto.getDeposit() + " 원"
        };

        for (int i = 0; i < labels.length; i++) {
            labels[i].setBounds(220, y, 100, 30);				
            add(labels[i]);

            JTextArea value = new JTextArea(values[i]);
            value.setEditable(false);
            value.setLineWrap(true);

            JScrollPane scroll = new JScrollPane(value);
            scroll.setBounds(340, y, 420, (i == 1) ? 80 : 30);	
            add(scroll);

            y += (i == 1) ? spacing + 30 : spacing;
        }

        JButton closeBtn = new JButton("닫기");
        closeBtn.setBounds(440, y + 30, 120, 40);				
        closeBtn.addActionListener(e -> {
            dispose();
        });
        add(closeBtn);

        setVisible(true);
    }
}
