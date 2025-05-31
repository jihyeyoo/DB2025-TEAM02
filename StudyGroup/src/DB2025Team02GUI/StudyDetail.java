package DB2025Team02GUI;
import DB2025Team02DAO.StudyDetailDAO;
import DB2025Team02DTO.StudyDetailDTO;
import DB2025Team02DTO.UserDTO;
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

        JButton joinBtn = new JButton("가입하기");
        joinBtn.setBounds(580, y + 30, 120, 40);
        joinBtn.addActionListener(e -> {
            if ("closed".equalsIgnoreCase(dto.getStatus())) {
                JOptionPane.showMessageDialog(this, "해당 스터디는 종료되어 가입할 수 없습니다.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "\"" + dto.getName() + "\" 스터디에 가입하시겠습니까?",
                    "가입 확인",
                    JOptionPane.YES_NO_OPTION
            );


            if (confirm == JOptionPane.YES_OPTION) {
                if (dao.isAlreadyJoined(studyId, user.getUserId())) {
                    JOptionPane.showMessageDialog(this, "이미 해당 스터디에 가입되어 있습니다.");
                    return;
                }

                if(dao.iswWthdrawnUser(studyId, user.getUserId())) {
                    JOptionPane.showMessageDialog(this, "탈퇴한 스터디에는 재가입할 수 없습니다");
                    return;
                }

                if (!dao.hasEnoughPoints(user.getUserId(), studyId)) {
                    JOptionPane.showMessageDialog(this, "보유 포인트가 부족하여 가입할 수 없습니다.");
                    return;
                }

                boolean success = dao.joinStudy(studyId, user.getUserId());
                if (success) {
                    JOptionPane.showMessageDialog(this, "스터디 가입이 완료되었습니다.");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "가입에 실패했습니다. 다시 시도해주세요.");
                }
            }
        });


        add(joinBtn);


        setVisible(true);
    }
}
