package DAO;

import DTO.StudyDetailDTO;
import main.AppMain;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudyDetailDAO {

    public StudyDetailDTO getStudyDetailById(int studyId) {
        String sql = "SELECT name, description, start_date, end_date, cert_method, deposit FROM StudyGroups WHERE study_id = ?";
        try {
            Connection conn = AppMain.conn;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studyId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new StudyDetailDTO(
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getString("cert_method"),
                    rs.getInt("deposit")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // 찾지 못한 경우
    }
}
