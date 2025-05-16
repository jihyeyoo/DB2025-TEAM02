package DAO;

import java.sql.*;
import DTO.MyStudyDetailDTO;
import main.AppMain;

public class MyStudyDetailDAO {
    public MyStudyDetailDTO getDetailById(int studyId) {
        MyStudyDetailDTO dto = null;

        String sql = """
        	    SELECT 
        	        sg.name AS study_name,
        	        u.user_name AS leader_name,
        	        COUNT(gm.user_id) AS member_count,
        	        IFNULL(SUM(gm.accumulated_fine), 0) AS total_fine,
        	        r.last_modified
        	    FROM StudyGroups sg
        	    JOIN Users u ON sg.leader_id = u.user_id
        	    LEFT JOIN GroupMembers gm ON sg.study_id = gm.study_id
        	    LEFT JOIN Rules r ON sg.study_id = r.study_id
        	    WHERE sg.study_id = ?
        	    GROUP BY sg.study_id, sg.name, u.user_name, r.last_modified;
        	""";


        try (PreparedStatement stmt = AppMain.conn.prepareStatement(sql)) {
            stmt.setInt(1, studyId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                dto = new MyStudyDetailDTO(
                    rs.getString("study_name"),
                    rs.getString("leader_name"),
                    rs.getInt("member_count"),
                    rs.getInt("total_fine"),
                    rs.getDate("last_modified")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dto;
    }
}
