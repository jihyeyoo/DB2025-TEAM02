package DB2025Team02DAO;

import DB2025Team02DTO.MyStudyDTO;
import DB2025Team02DTO.UserDTO;
import DB2025Team02main.AppMain;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MyCertDAO {

    /* 내가 가입한 스터디 목록을 가져오는 함수입니다.  MyCertPage에서 스터디명과 함께
    다음 인증 마감일, 인증 날짜, 인증 내용, 승인 여부를 조회하기 위해 CertStatusView를 활용합니다. 또한 MyStudyPage에서 내가 가입한 스터디 목록을 가져오는데 사용됩니다*/
    public List<MyStudyDTO> getMyStudiesWithCertDate(UserDTO user) {
        List<MyStudyDTO> studyList = new ArrayList<>();

        String sql = """
        SELECT study_id, study_name, leader_name,
               start_date, status, leader_id, 
               next_cert_date
        FROM DB2025Team02CertStatusView
        WHERE user_id = ?
        ORDER BY start_date DESC
    """;

        try (PreparedStatement pstmt = AppMain.conn.prepareStatement(sql)) {
            pstmt.setInt(1, user.getUserId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                MyStudyDTO dto = new MyStudyDTO(
                        rs.getInt("study_id"),
                        rs.getString("study_name"),
                        rs.getString("leader_name"),
                        rs.getDate("start_date"),
                        rs.getInt("leader_id"),
                        rs.getString("status"),
                        rs.getDate("next_cert_date")
                );

                // 다음 인증 마감일 setter가 있다면 호출
                if (rs.getDate("next_cert_date") != null) {
                    dto.setNextCertDate(rs.getDate("next_cert_date"));  // ← 이 setter는 MyStudyDTO에 있어야 합니다
                }
                studyList.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return studyList;
    }

}
