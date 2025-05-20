package DAO;

import DTO.StudyListDTO;
import main.AppMain;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudyListDAO {

    public List<StudyListDTO> getAllStudies() {
        List<StudyListDTO> list = new ArrayList<>();
        String sql = "SELECT study_id, name, start_date, end_date, cert_method, deposit FROM StudyGroups";

        try (PreparedStatement ps = AppMain.conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                StudyListDTO dto = new StudyListDTO(
                    rs.getInt("study_id"),
                    rs.getString("name"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getString("cert_method"),
                    rs.getInt("deposit")
                );
                list.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
