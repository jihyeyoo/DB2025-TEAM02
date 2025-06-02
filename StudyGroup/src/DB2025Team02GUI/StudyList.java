package DB2025Team02GUI;
import DB2025Team02DAO.ChargePointDAO;
import DB2025Team02DAO.StudyListDAO;
import DB2025Team02DTO.StudyListDTO;
import DB2025Team02DTO.UserDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;


/**
 * 전체 스터디 목록을 조회하고 스터디를 검색하기 위한 화면을 구성하는 클래스입니다.
 */

public class StudyList extends JFrame {
    private JTable table;									// 보여주기 위한 컴포넌트 
    private DefaultTableModel tableModel;					// 데이터 모델용
    

    // UserDTO를 통해 유저 정보를 받아옴 - 이후 마이페이지랑 연결하기 위함.
    public StudyList(UserDTO user) {
        setTitle("스터디 목록");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);									// 화면 사이즈를 고정했습니다.
        setLocationRelativeTo(null);						// TODO: 프로그램 통일성을 위해 모든 GUI에 해당 줄 추가하기 - 윈도우 중앙에 창을 띄워줍니다.
        getContentPane().setLayout(null);					// setBounds(요소 절대 위치 배치) 사용하기 위함.

        // 01. 제목 라벨 - "스터디 목록"
        JLabel titleLabel = new JLabel("스터디 목록", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setBounds(400, 30, 200, 40); 			// 창 상부 중앙 배치 - 절대 위치 기준이라 창 크기 바뀌어도 적용 안 됩니다.
        getContentPane().add(titleLabel);					// 레이아웃에 컴파운드 넣어줌.		
        
        // 02. 마이페이지 이동 - 버튼, "마이페이지"
        JButton myPageBtn = new JButton(user.getUserName() + "의 마이페이지");	// 로그인 정보 받아와서 '이름'의 마이페이지로 버튼 띄워줌.
        myPageBtn.setBounds(800, 20, 160, 30);				// 절대 위치라 유저 이름이 길면 잘립니다.
        getContentPane().add(myPageBtn);					// 레이아웃에 컴파운드 넣어줌.

        // 02E. 마이페이지 버튼 클릭 이벤트 - 현재 StudyList창 끄고 MyPage 생성
        myPageBtn.addActionListener(e -> {
            ChargePointDAO myPageDAO = new ChargePointDAO();
            int updatedPoints = myPageDAO.getUserPoints(user.getUserId());
            user.setPoints(updatedPoints);
            dispose();
            new MyPage(user); 								// UserDTO 사용해서 넘겨주기.
        });
        
        
        // 02-1. 검색창 추가
        JTextField searchField = new JTextField();
        searchField.setBounds(50, 90, 300, 30);		// 위치: 제목 라벨 아래
        getContentPane().add(searchField);

        JButton searchBtn = new JButton("검색");
        searchBtn.setBounds(360, 90, 80, 30);
        getContentPane().add(searchBtn);

        // 02-2. 검색 버튼 클릭 시 이벤트
        searchBtn.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                loadStudyData(); // 비어있으면 전체 목록 불러오기
            } else {
                loadStudyData(keyword);
            }
        });
        
        searchField.addActionListener(e -> {
            String keyword = searchField.getText().trim();
            if (keyword.isEmpty()) {
                loadStudyData();
            } else {
                loadStudyData(keyword);
            }
        });
        
        
        
        
        String[] sortFields = {"이름", "시작일", "종료일", "보증금"};
        String[] sortOrders = {"오름차순", "내림차순"};

        JComboBox<String> sortFieldCombo = new JComboBox<>(sortFields);
        sortFieldCombo.setBounds(600, 90, 120, 30);
        getContentPane().add(sortFieldCombo);

        JComboBox<String> sortOrderCombo = new JComboBox<>(sortOrders);
        sortOrderCombo.setBounds(730, 90, 120, 30);
        getContentPane().add(sortOrderCombo);

        JButton sortBtn = new JButton("정렬");
        sortBtn.setBounds(860, 90, 70, 30);
        getContentPane().add(sortBtn);
        
        sortBtn.addActionListener(e -> {
            String field = switch (sortFieldCombo.getSelectedItem().toString()) {
                case "이름" -> "name";
                case "시작일" -> "start_date";
                case "종료일" -> "end_date";
                case "보증금" -> "deposit";
                default -> "name";
            };
            String order = sortOrderCombo.getSelectedItem().toString().equals("오름차순") ? "ASC" : "DESC";
            String keyword = searchField.getText().trim();

            if (keyword.isEmpty()) {
                loadStudyDataSorted(field, order);
            } else {
                loadStudyDataFilteredAndSorted(keyword, field, order);
            }
        });

        
        // 03. DB - 테이블
        String[] columnNames = {"번호", "이름", "시작일", "종료일", "인증방식", "보증금", "스터디ID", "상태"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setRowHeight(30);
        
        // 스터디ID 컬럼 숨김
        table.getColumnModel().getColumn(6).setMinWidth(0);
        table.getColumnModel().getColumn(6).setMaxWidth(0);
        table.getColumnModel().getColumn(6).setWidth(0);

        
        // 테이블, 새로고침, 스터디 개설 버튼 생성 + 이벤트 처리
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(50, 140, 880, 400);
        getContentPane().add(scrollPane);

        JButton refreshBtn = new JButton("새로고침");
        refreshBtn.setBounds(320, 560, 150, 40);
        getContentPane().add(refreshBtn);

        JButton createBtn = new JButton("스터디 개설");
        createBtn.setBounds(500, 560, 150, 40);
        getContentPane().add(createBtn);

        refreshBtn.addActionListener(e -> loadStudyData());

        createBtn.addActionListener(e -> {
        	dispose();
            new CreateStudy(user);
        });

        
        
        // 06. StudyDetail로 넘어가기 위한 이벤트 핸들링
        // 마우스로 더블클릭하면 해당 스터디의 세부사항 페이지로 넘어가게 해둠.
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {				
                    int row = table.getSelectedRow();
                    int studyId = (int) tableModel.getValueAt(row, 6);
                    new StudyDetail(studyId, user);	// TODO: 해당 studyId 부분은 인덱스 사용시 수정 필요할 수도 있음.
                }
            }
        });

        loadStudyData();
        setVisible(true);
    }

    // 07. DB상 순서나 PK가 아닌 표시를 위한 순서 인덱스 출력. 무조건 1, 2, 3...으로 뜨게.
    // DB에서 사용하는 PK는 숨겨둡니다.
    // 기본 전체 목록 로딩
    private void loadStudyData() {
        tableModel.setRowCount(0);
        StudyListDAO dao = new StudyListDAO();
        List<StudyListDTO> list = dao.getAllStudies();
        populateTable(list);
    }

    // 검색어로 목록 로딩
    private void loadStudyData(String keyword) {
        tableModel.setRowCount(0);
        StudyListDAO dao = new StudyListDAO();
        List<StudyListDTO> list = dao.searchStudiesByName(keyword);
        populateTable(list);
    }
    
    private void loadStudyDataSorted(String field, String order) {
        tableModel.setRowCount(0);
        StudyListDAO dao = new StudyListDAO();
        List<StudyListDTO> list = dao.getSortedStudies(field, order);
        populateTable(list);
    }
    
    private void loadStudyDataFilteredAndSorted(String keyword, String field, String order) {
        StudyListDAO dao = new StudyListDAO();
        populateTable(dao.searchAndSortStudies(keyword, field, order));
    }



    // 테이블 채우는 공통 로직
    // 테이블 로딩 메서드
    private void populateTable(List<StudyListDTO> list) {
        tableModel.setRowCount(0);
        int index = 1;
        for (StudyListDTO dto : list) {
			tableModel.addRow(new Object[]{
				index++,
                dto.getName(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getCertMethod(),
                dto.getDeposit(),
            	dto.getStudyId(),
                dto.getStatus()
            });
        }
    }

}
