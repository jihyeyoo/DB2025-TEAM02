package GUI;

import DAO.MyStudyDAO;
import DAO.StudyEditDAO;
import DTO.StudyEditDTO;
import DTO.MyStudyDTO;
import DTO.UserDTO;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class MyStudyPage extends JFrame {
    private JFrame previousPage;

    public MyStudyPage(UserDTO user, JFrame previousPage) {
        this.previousPage = previousPage;
        setTitle("자기 스터디 조회 페이지");
        setSize(1000, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        List<MyStudyDTO> studyList = new MyStudyDAO().getMyStudies(user);

        String[] columnNames = {"스터디명", "스터디장", "시작일", "정보", "수정", "탈퇴", "인증 관리"};
        Object[][] data = new Object[studyList.size()][7];

        for (int i = 0; i < studyList.size(); i++) {
            MyStudyDTO dto = studyList.get(i);
            data[i][0] = dto.getStudyName();
            data[i][1] = dto.getLeaderName();
            data[i][2] = dto.getStartDate();
            data[i][3] = "정보 보기";
            data[i][4] = (dto.getLeaderId() == user.getUserId()) ? "수정" : "";  // 개설자인 경우만 "수정"
            data[i][5] = "탈퇴";
            data[i][6] = (dto.getLeaderId() == user.getUserId()) ? "인증 관리" : "";
        }


        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
        	public boolean isCellEditable(int row, int column) {

        	    if (column == 4) {  // 수정 버튼 컬럼일 때만 특별히 검사
        	        MyStudyDTO dto = studyList.get(row);
        	        return dto.getLeaderId() == user.getUserId();  // 개설자인 경우만 편집 가능 (즉, 수정 버튼만 동작)
        	    }
        	    if (column == 3) return true; // 정보 보기 버튼
                if (column == 5) return true; // ✅ 탈퇴 버튼도 모두 클릭 가능하게 추가
                if (column == 6) return true; // 인증 관리 버튼
                return false;


        	}

        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getColumn("정보").setCellRenderer(new InfoButtonRenderer());
        table.getColumn("정보").setCellEditor(new InfoButtonEditor(new JCheckBox(), studyList, user));
        table.getColumn("탈퇴").setCellRenderer(new WithdrawButtonRenderer());
        table.getColumn("탈퇴").setCellEditor(new WithdrawButtonEditor(new JCheckBox(), studyList, user));
        table.getColumn("수정").setCellRenderer(new EditButtonRenderer());
        table.getColumn("수정").setCellEditor(new EditButtonEditor(new JCheckBox(), studyList, user));
        table.getColumn("인증 관리").setCellRenderer(new CertManageButtonRenderer());
        table.getColumn("인증 관리").setCellEditor(new CertManageButtonEditor(new JCheckBox(), studyList));

        add(new JScrollPane(table), BorderLayout.CENTER);

        // ✅ 아래 부분만 추가/수정됨 (기본 텍스트 뒤로 가기 버튼)
        JPanel bottomPanel = new JPanel();
        JButton backButton = new JButton("← 뒤로 가기");
        backButton.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        backButton.setFocusPainted(false);
        backButton.setBackground(Color.LIGHT_GRAY);
        backButton.setForeground(Color.BLACK);

        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
        setVisible(true);

        backButton.addActionListener(e -> {
            dispose(); //현재 창 닫기
            previousPage.setVisible(true); // MyPage 다시 보이기
        });
    }

    class CertManageButtonRenderer extends JButton implements TableCellRenderer {
        public CertManageButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "");
            return this;
        }
    }

    class CertManageButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private List<MyStudyDTO> list;
        private int currentRow;

        public CertManageButtonEditor(JCheckBox checkBox, List<MyStudyDTO> list) {
            super(checkBox);
            this.list = list;
            button = new JButton("인증 관리");
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            clicked = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) {
                MyStudyDTO selected = list.get(currentRow);
                new ManageCertsPage(selected.getStudyId());
            }
            clicked = false;
            return "인증 관리";
        }

        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }

    // 정보 버튼 렌더러
    class InfoButtonRenderer extends JButton implements TableCellRenderer {
        public InfoButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText("정보 보기");
            return this;
        }
    }

    // 탈퇴 버튼 렌더러
    class WithdrawButtonRenderer extends JButton implements TableCellRenderer {
        public WithdrawButtonRenderer() {
            setOpaque(true);
            setForeground(Color.WHITE);
            setBackground(Color.RED);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText("탈퇴");
            return this;
        }
    }

    // 정보 보기 버튼 클릭 이벤트
    class InfoButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private List<MyStudyDTO> list;
        private int currentRow;
        private UserDTO user;

        public InfoButtonEditor(JCheckBox checkBox, List<MyStudyDTO> list, UserDTO user) {
            super(checkBox);
            this.list = list;
            this.user = user;
            button = new JButton("정보 보기");
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            clicked = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) {
                MyStudyDTO selected = list.get(currentRow);
                JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(button); // 현재 창 참조
                currentFrame.dispose(); // 현재 MyStudyPage 창 닫기
                new MyStudyDetailPage(selected.getStudyId(), user, currentFrame); // 이전 창도 넘기기
            }
            clicked = false;
            return "정보 보기";
        }

        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }

    class WithdrawButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private List<MyStudyDTO> list;
        private int currentRow;
        private UserDTO user;

        public WithdrawButtonEditor(JCheckBox checkBox, List<MyStudyDTO> list, UserDTO user) {
            super(checkBox);
            this.list = list;
            this.user = user;
            button = new JButton("탈퇴");
            button.setForeground(Color.WHITE);
            button.setBackground(Color.RED);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            clicked = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) {
                MyStudyDTO selected = list.get(currentRow);
                int confirm = JOptionPane.showConfirmDialog(button,
                        "정말로 " + selected.getStudyName() + " 스터디에서 탈퇴하시겠습니까?",
                        "탈퇴 확인", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean result = new MyStudyDAO().withdrawFromStudy(selected.getStudyId(), user);
                    if (result) {
                        JOptionPane.showMessageDialog(button, "탈퇴가 완료되었습니다.");
                        // ✅ 정확한 현재 프레임 닫고 새로 띄우기
                        JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(button);
                        currentFrame.dispose();
                        new MyStudyPage(user, previousPage);
                    } else {
                        JOptionPane.showMessageDialog(button, "탈퇴에 실패했습니다.");
                    }
                }
            }
            clicked = false;
            return "탈퇴";
        }


        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }
    
    class EditButtonRenderer extends JButton implements TableCellRenderer {
        public EditButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "");
            return this;
        }
    }
    
    class EditButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private List<MyStudyDTO> list;
        private int currentRow;
        private UserDTO user;

        public EditButtonEditor(JCheckBox checkBox, List<MyStudyDTO> list, UserDTO user) {
            super(checkBox);
            this.list = list;
            this.user = user;
            button = new JButton("수정");
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            currentRow = row;
            clicked = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (clicked) {
                MyStudyDTO selected = list.get(currentRow);
                System.out.println("selected study id" + selected.getStudyId());
                if (selected.getLeaderId() == user.getUserId()) {
                    StudyEditDTO dto = new StudyEditDAO().getStudyById(selected.getStudyId());  // 여기에서 studyId 가져오기
                    JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(button);     // 이전 창 전달
                    new EditStudyPage(dto, user, currentFrame);  // user, 이전 페이지 넘기기
                }
            }
            clicked = false;
            return "수정";
        }

        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }

}

