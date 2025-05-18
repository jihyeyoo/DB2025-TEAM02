package GUI;

import DAO.StudyListDAO;
import DTO.StudyListDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class StudyList extends JFrame {

    // 테이블 구성에 필요한 컴포넌트들을 이곳에 선언
    private JTable table;                       // 스터디 데이터를 보여주는 테이블
    private DefaultTableModel tableModel;       // 테이블에 표시할 데이터를 관리하는 모델
    private String loginId;                     // 현재 로그인한 사용자의 login_id를 기록 - 마이페이지와 연결
    public StudyList(String loginId) {
        this.loginId = loginId;  				// 생성자로 전달받은 로그인 ID를 저장

        // 제목 프레임 설정
        setTitle("스터디 목록");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        getContentPane().setLayout(null);  // 절대 위치 사용
        getContentPane().setBackground(Color.WHITE);

        // 타이틀 라벨 생성
        JLabel titleLabel = new JLabel("스터디 목록");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        titleLabel.setBounds(420, 30, 200, 40);
        getContentPane().add(titleLabel);

        // 우상단에 마이페이지 버튼 생성
        JButton myPageBtn = new JButton("마이페이지");
        myPageBtn.setBounds(870, 20, 100, 30);
        myPageBtn.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        getContentPane().add(myPageBtn);
        
        // 마이페이지 버튼 이벤트 처리
        myPageBtn.addActionListener(e -> {
            // 클릭 시 마이페이지 창으로 이동 - 이때, loginId 전달!!!
            dispose();                   // 현재 창 없애고 마이페이지 호출
            new MyPage(loginId);
        });

        // 테이블 초기 설정 필요
        String[] columnNames = {"ID", "번호", "이름", "시작일", "종료일", "인증방식", "보증금"};  // 컬럼은 이렇게
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false; // 셀 수정은 일단 막았음. 수정 페이지에서 고칠 수 있도록 할 것.
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("맑은 고딕", Font.PLAIN, 14));

        // DB상에 기록된 ID는 사용자에게 보이지 않도록 숨기고 이후 그냥 카운팅해서 1,2,3...
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // 스크롤 가능하게 함.
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 100, 880, 400);
        getContentPane().add(scrollPane);

        // 하단 버튼 두 가지 - 새로고침, 스터디 개설
        JButton refreshBtn = new JButton("새로고침");
        JButton createBtn = new JButton("스터디 개설");

        refreshBtn.setBounds(320, 520, 150, 40);
        createBtn.setBounds(500, 520, 150, 40);

        // 버튼 디자인 설정 - 2022년도 프로젝트 디자인 참고함.
        Font btnFont = new Font("맑은 고딕", Font.BOLD, 14);
        Color btnColor = new Color(0xFF6472);
        refreshBtn.setFont(btnFont);
        createBtn.setFont(btnFont);
        refreshBtn.setBackground(btnColor);
        refreshBtn.setForeground(Color.WHITE);
        createBtn.setBackground(btnColor);
        createBtn.setForeground(Color.WHITE);

        getContentPane().add(refreshBtn);
        getContentPane().add(createBtn);

        // 새로고침 버튼 이벤트 - 테이블 데이터 다시 불러오기
        refreshBtn.addActionListener(e -> loadStudyData());

        // 개설 버튼 이벤트 - CreateStudy 화면으로 이동. 이때 loginId 전달하면서 넘어감.
        createBtn.addActionListener(e -> {
            dispose();                    	// 현재 창 닫고 새로운 창으로 이동!
            new CreateStudy(loginId);
        });

        // 테이블 행 더블 클릭 시 해당 스터디 상세 정보 페이지로 이동하도록 설정. 아니면 따로 버튼 만들어도?
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { 				// 더블 클릭으로 설정
                    int row = table.getSelectedRow(); 		// 선택한 행 번호를 기록하는... 라인
                    if (row != -1) {
                        int studyId = (int) tableModel.getValueAt(row, 0); 	// 숨긴 DB상의 진짜 ID 값을 가져온다.
                        dispose();  						// 현재 창 닫고 이동.
                        new StudyDetail(studyId, loginId); 
                    }
                }
            }
        });

        // 화면 로드 시 초기 데이터 표시 - 리프레시
        loadStudyData();
        setVisible(true);
    }

    // DAO에서 스터디 목록을 받아 테이블에 표시하는 함수
    private void loadStudyData() {
        tableModel.setRowCount(0);  					// 기존 데이터 초기화
        StudyListDAO dao = new StudyListDAO();  		// DAO 호출
        List<StudyListDTO> list = dao.getAllStudies();  // 전체 목록 불러오기

        int index = 1;  								// 번호 열에 표시할 번호 - 1부터 시작.
        for (StudyListDTO dto : list) {
            tableModel.addRow(new Object[]{
                dto.getStudyId(),        // DB의 스터디 고유 ID (보호를 위해 숨김 열)
                index++,                 // 이건 사용자에게 표시되는 번호. 1부터 시작합니다.
                dto.getName(),           // 스터디 이름
                dto.getStartDate(),      // 시작일
                dto.getEndDate(),        // 종료일
                dto.getCertMethod(),     // 인증 방식 (글, 사진, 영상 등)
                dto.getDeposit()         // 보증금
            });
        }
    }
}
