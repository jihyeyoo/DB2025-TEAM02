package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;



/*


목적: 현재까지 생성한 모든 스터디 목록을 표시 - 핵심 정보인 study_id, name, description을 보여줌.

create.sql의 StudyGroups 참고.
현재는 DB 연동 없는 스탠드 얼론 버전 - 더미 데이터 만들어 둠. 추후 DAO/DTO 연동 시 메소드 넣어서 연결할 것 (TODO)
DB2022 프로젝트 페이지 디자인 참고함.

이외 리더의 이름이나 종료일, 보증금 금액은 세부 정보에서 확인할 수 있도록 함. (피드백에 따라 정보를 해당 페이지에 올리도록 추후 수정할 수 있음.)


TO DO : DAO랑 DTO 상황 보고 Detail과 추후 연결 할 수 있도록 만들기.

 */



public class StudyList extends JFrame {

    // 테이블&데이터 모델 선언
    private JTable table;                         // 실제로 스터디 목록을 화면에 뿌려줄 테이블
    private DefaultTableModel tableModel;         // 테이블 데이터 저장 및 관리하는 역할


    // 창 구성 및 이벤트를 이곳에서 설정
    public StudyList() {
    	// 아래로는 프레임 속성을 정의
        setTitle("스터디 목록");                             		// 창의 최상단 제목
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    	 	// 닫기 시 프로그램을 종료
        setSize(1000, 700);                               	  	// 창 크기를 고정 (TO DO: 창 사이즈 고정/표준화)
        setLocationRelativeTo(null);                       	 	// 화면을 중앙에 띄우는 것으로 배치
        setLayout(null);                                   	 	// 절대 좌표를 이용해 배치하는 것으로 설정
        getContentPane().setBackground(Color.WHITE);        	// 배경색은 흰색 (DB 2022 프로젝트 참고함. 테마색 결정 시 이후 수정.)

        // 폰트를 지정 - DB2022 프로젝트 참고함.
        Font titleFont = new Font("맑은 고딕", Font.BOLD, 30);		// 제목
        Font tableFont = new Font("맑은 고딕", Font.PLAIN, 15);   // 테이블
        Font headerFont = new Font("맑은 고딕", Font.BOLD, 15);   // 테이블 헤더

        // 제목 라벨 설정
        JLabel titleLabel = new JLabel("스터디 목록");
        titleLabel.setFont(titleFont);
        titleLabel.setBounds(400, 30, 300, 40); 				// x, y, width, height 순서
        add(titleLabel);

        // 테이블 생성
        String[] columnNames = {"번호", "스터디 이름", "설명"};		// 여기서 column의 이름을 지정
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;  									// 사용자가 셀을 직접 수정하지 못하게 막음.
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);                 				// 행 높이
        table.setFont(tableFont);               				// 테이블 text 폰트
        table.setBackground(new Color(0xFFF8DC)); 				// 밝은 노란색 배경
        table.getTableHeader().setFont(headerFont);  			// 헤더로 굵게 설정

        JScrollPane scrollPane = new JScrollPane(table);  		// 스크롤 기능 넣음
        scrollPane.setBounds(100, 100, 800, 450);         		// 중앙 배치
        add(scrollPane);

        // 버튼
        JButton refreshBtn = new JButton("새로고침");				// DB 변동 사항 생기면 반영할 수 있도록.
        JButton createBtn = new JButton("스터디 개설하기");			// CreateStudy로 연결.

        refreshBtn.setBounds(350, 570, 130, 50);
        createBtn.setBounds(520, 570, 160, 50);

        Font btnFont = new Font("맑은 고딕", Font.BOLD, 16);
        Color btnColor = new Color(0xFF6472); 					// 분홍빛 컬러 (DB2022참고)

        refreshBtn.setFont(btnFont);
        createBtn.setFont(btnFont);

        createBtn.setBackground(btnColor);
        createBtn.setForeground(Color.WHITE);

        add(refreshBtn);
        add(createBtn);

     // 테이블 더블 클릭 시 Detail 페이지로 연결
     // 현재는 DTO 전달 없이 디자인 테스트만 가능하도록 연결
     // TO DO: 추후 StudyDetail(dto)와 같은 방식으로 데이터 전달 추가
     table.addMouseListener(new MouseAdapter() {
         public void mouseClicked(MouseEvent e) {
             if (e.getClickCount() == 2) {
                 new StudyDetail().setVisible(true);  // 기본 생성자 호출만 진행 (디자인 확인용)
             }
         }
     });


        // 개설 버튼 - CreateStudy 페이지로 이동
        createBtn.addActionListener(e -> {
            new CreateStudy().setVisible(true);  // 새 창 띄우기
            dispose();                           // 현재 창 닫기
        });

        // 새로고침 버튼 - DB에서 데이터 불러오기 메소드...인데 현재는 그냥 더미데이터를 뿌려줍니다.
        refreshBtn.addActionListener(e -> loadStudyData());

        // 프로그램 시작하는 시점에서 데이터 자동으로 불러올 수 있도록 호출
        loadStudyData();
        setVisible(true);
    }

    
    
/*

스터디 목록을 테이블에 표시해 주는 함수
아직 DB 연동을 못하는 상태 - 더미 데이터로 테스트 중. 

*/
    
    
    
    private void loadStudyData() {
        tableModel.setRowCount(0);  // 테이블 내용 초기화

        // name, description에 해당하는 테스트 데이터 
        String[][] dummyData = {
            {"스터디 이름 01", "이곳에는 스터디 설명을 적습니다."},
            {"스터디 이름 02", "가나다라마바사 테스트를 진행합니다. 길어지면 표시가 어떻게 되는지도 테스트. 잘리는데 Detail에서 표시 되니까 상관 없나?"},
            {"Study Set 03", "ABCEFGHELLO"},
            {"Study Set 04", "ABCEFGHELLO"},
            {"Study Set 05", "."},
            {"Study Set 00", ""}
        };

        // study_id에 무관하게 인덱스 줄 수 있도록 새로운 배열을 생성
        int index = 1;
        for (String[] row : dummyData) {
            tableModel.addRow(new Object[]{ index++, row[0], row[1] });
        }
    }

    // 테스트 하려고 메인을 여기에 뒀는데 나중에 수정하셔도 됩니다.
    public static void main(String[] args) {
        EventQueue.invokeLater(StudyList::new);
    }
}
