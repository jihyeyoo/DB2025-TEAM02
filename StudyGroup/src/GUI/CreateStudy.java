package GUI;
import DAO.CreateStudyDAO;
import DTO.CreateStudyDTO;
import DTO.UserDTO;

import javax.swing.*;
import java.awt.*;


/*

WHAT: 스터디 개설 페이지 GUI
WHO: 담당자 - 공세영
TODO: 

*/


public class CreateStudy extends JFrame {
    private JTextField nameField, startField, endField, depositField;
    private JTextArea descriptionArea;

    
    public CreateStudy(UserDTO user) {
        setTitle("스터디 개설");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);

        // 01. 제목
        JLabel titleLabel = new JLabel("스터디 개설", SwingConstants.CENTER);
        titleLabel.setBounds(400, 30, 200, 40);
        getContentPane().add(titleLabel);
        
     // 02. 각 입력 필드 라벨 및 컴포넌트 배치
        JLabel nameLabel = new JLabel("스터디 이름");
        nameLabel.setBounds(150, 100, 100, 30);
        getContentPane().add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(320, 100, 400, 30);
        getContentPane().add(nameField);

        JLabel descLabel = new JLabel("스터디 소개");
        descLabel.setBounds(150, 150, 100, 30);
        getContentPane().add(descLabel);

        descriptionArea = new JTextArea();
        descriptionArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBounds(320, 150, 400, 100);
        getContentPane().add(descScroll);

        JLabel startLabel = new JLabel("시작일 (YYYY-MM-DD)");
        startLabel.setBounds(150, 270, 150, 30);
        getContentPane().add(startLabel);

        startField = new JTextField();
        startField.setBounds(320, 270, 400, 30);
        getContentPane().add(startField);

        JLabel endLabel = new JLabel("종료일 (YYYY-MM-DD)");
        endLabel.setBounds(150, 320, 150, 30);
        getContentPane().add(endLabel);

        endField = new JTextField();
        endField.setBounds(320, 320, 400, 30);
        getContentPane().add(endField);

        JLabel depositLabel = new JLabel("보증금");
        depositLabel.setBounds(150, 370, 100, 30);
        getContentPane().add(depositLabel);

        depositField = new JTextField();
        depositField.setBounds(320, 370, 400, 30);
        getContentPane().add(depositField);

        // 03. 버튼 배치
        JButton createBtn = new JButton("개설");
        createBtn.setBounds(320, 450, 150, 40);
        getContentPane().add(createBtn);

        JButton cancelBtn = new JButton("취소");
        cancelBtn.setBounds(500, 450, 150, 40);
        getContentPane().add(cancelBtn);
        

        // 이벤트
        cancelBtn.addActionListener(e -> {
            dispose();
            new StudyList(user);
        });

        createBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String desc = descriptionArea.getText().trim();
                String start = startField.getText().trim();
                String end = endField.getText().trim();
                int deposit = Integer.parseInt(depositField.getText().trim());

                if (name.isEmpty() || desc.isEmpty() || start.isEmpty() || end.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "모든 항목을 입력하세요.");
                    return;
                }

                // 인증 방식은 임시로 "글"로 고정
                CreateStudyDTO dto = new CreateStudyDTO(name, user.getUserId(), desc, start, end, "글", deposit);
                boolean ok = new CreateStudyDAO().createStudyGroup(dto);

                if (ok) {
                    JOptionPane.showMessageDialog(this, "스터디 개설 성공!");
                    dispose();
                    new StudyList(user);
                } else {
                    JOptionPane.showMessageDialog(this, "개설 실패. 다시 시도하세요.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "입력값 오류: " + ex.getMessage());
            }
        });


        setVisible(true);
    }

}
