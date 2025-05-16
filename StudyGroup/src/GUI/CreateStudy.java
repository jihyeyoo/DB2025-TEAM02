package GUI;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;



/*


목적: 새로운 스터디를 생성
StudyGroups 테이블의 데이터를 입력 받는 구조를 GUI로 구성.

현재는 DB 연동 없는 스탠드 얼론 버전 - 더미 데이터 만들어 둠. 추후 DAO/DTO 연동 시 메소드 넣어서 연결할 것 (TODO)
DB2022 프로젝트의 ProductRegister 페이지 디자인 참고함.

입력 값은 [[이름, 설명, 시작일, 종료일, 인증 방식, 보증금]]


*/



public class CreateStudy extends JFrame {

    // 입력 필드 선언 - 아래 6개의 필드는 사용자가 직접 작성하게 될 항목들
    private JTextField nameField;               // 스터디 이름
    private JTextArea descriptionArea;          // 스터디 설명(길게 쓸 수 있게 해둠)
    private JTextField startDateField;          // 시작일
    private JTextField endDateField;            // 종료일
    private JComboBox<String> certMethodBox;    // 인증 방식 선택 콤보박스
    private JTextField depositField;            // 보증금 입력칸

    // 인증 방식 종류 - 일단 단순한 3개 정도만 선택지로 구성
    private final String[] certMethods = {"글", "사진", "영상"};

    // 창 구성 및 이벤트를 이곳에서 설정
    public CreateStudy() {
        setTitle("스터디 개설");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);                     // 창 크기를 고정 (TO DO: 창 사이즈 고정/표준화)
        setResizable(false);
        setLocationRelativeTo(null);            // 화면을 중앙에 띄우는 것으로 배치
        Container c = getContentPane();
        c.setLayout(null);                      // 절대 좌표를 이용해 배치하는 것으로 설정
        c.setBackground(Color.WHITE);           
        
     // 폰트를 지정 - DB2022 프로젝트 참고함.
        Font titleFont = new Font("맑은 고딕", Font.BOLD, 30);     // 페이지 상단 제목
        Font labelFont = new Font("맑은 고딕", Font.BOLD, 20);     // 라벨 텍스트
        Font inputFont = new Font("맑은 고딕", Font.PLAIN, 18);    // 입력창 내부 폰트

        // 제목 라벨 설정
        JLabel title = new JLabel("스터디 개설");
        title.setFont(titleFont);
        title.setBounds(400, 30, 400, 40);  // 위치 및 크기 지정
        c.add(title);

        // 제목 아래에 작게 입력 안내
        JLabel notice = new JLabel("* 모든 항목을 입력해주세요.");
        notice.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        notice.setBounds(400, 70, 300, 20);
        c.add(notice);

        // 라벨 및 필드 위치를 위한 좌표값 설정
        int labelX = 200, inputX = 550, width = 300, height = 40;
        int baseY = 100, spacing = 70, yOffset = 0;

        // 각 항목명 및 해당 입력 필드를 구성
        String[] labels = {"스터디 이름", "스터디 소개", "시작일 (YYYY-MM-DD)", "종료일 (YYYY-MM-DD)", "인증 방식", "보증금 (원)"};
        JComponent[] inputs = {
            nameField = new JTextField(),
            new JScrollPane(descriptionArea = new JTextArea(4, 20)),
            startDateField = new JTextField(),
            endDateField = new JTextField(),
            certMethodBox = new JComboBox<>(certMethods),
            depositField = new JTextField()
        };

        // 라벨 및 필드를 화면에 배치 (줄마다 라벨/입력칸 쌍으로 추가)
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(labelFont);
            int y = baseY + i * spacing + yOffset;
            lbl.setBounds(labelX, y, 300, height);
            c.add(lbl);

            JComponent input = inputs[i];
            input.setFont(inputFont);

            if (input instanceof JScrollPane scroll) {
                scroll.setBounds(inputX, y, width, 80);  // 설명칸은 높이 크게
                yOffset += 40;                           // 다음 줄 간격 확보 (Description이랑 붙어서 둠.)
                c.add(scroll);
            } else {
                input.setBounds(inputX, y, width, height);
                c.add(input);
            }
        }

        // 버튼
        JButton createBtn = new JButton("개설");
        JButton cancelBtn = new JButton("뒤로가기");		// 이건 StudyList랑 연결함.

        createBtn.setFont(labelFont);
        cancelBtn.setFont(labelFont);

        Color buttonColor = new Color(0xFF6472);  // 컬러는 DB2022 프로젝트 참고.
        createBtn.setBackground(buttonColor);
        createBtn.setForeground(Color.WHITE);
        cancelBtn.setBackground(Color.LIGHT_GRAY);

        createBtn.setBounds(400, 550, 130, 50);
        cancelBtn.setBounds(550, 550, 130, 50);
        c.add(createBtn);
        c.add(cancelBtn);

        // 개설 버튼 클릭 시 이벤트 - 유효성 검사
        createBtn.addActionListener(e -> {
            try {
                // 입력값 읽기
                String name = nameField.getText().trim();
                String desc = descriptionArea.getText().trim();
                String start = startDateField.getText().trim();
                String end = endDateField.getText().trim();
                String cert = (String) certMethodBox.getSelectedItem();
                String depositStr = depositField.getText().trim();

                // 빈칸 검사
                if (name.isEmpty() || desc.isEmpty() || start.isEmpty() || end.isEmpty() || depositStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "모든 항목을 입력해주세요.");
                    return;
                }

                // 날짜 형식 검사 - 실패 시 DateTimeParseException 발생
                LocalDate startDate = LocalDate.parse(start);
                LocalDate endDate = LocalDate.parse(end);

                // 보증금 숫자 검사 - 실패 시 NumberFormatException 발생
                int deposit = Integer.parseInt(depositStr);
                if (deposit < 0) throw new NumberFormatException();  // 음수 방지

                // 현재는 DB 연동 없고 팝업으로 올려만 둠.
                JOptionPane.showMessageDialog(this, "스터디가 개설되었습니다! (임시)");

                // 목록 페이지로 이동
                new StudyList().setVisible(true);
                dispose();

            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this, "날짜는 YYYY-MM-DD 형식으로 입력해주세요.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "보증금은 0 이상의 숫자로 입력해주세요.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "입력 처리 중 오류가 발생했습니다.");
            }
        });

        // 뒤로가기 버튼 클릭 시 - StudyList 
        cancelBtn.addActionListener(e -> {
            new StudyList().setVisible(true);
            dispose();
        });
    }
}
