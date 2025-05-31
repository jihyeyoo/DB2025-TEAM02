package DB2025Team02DAO;

import DB2025Team02DTO.StudyListDTO;
import DB2025Team02main.AppMain;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudyListDAO {

    public List<StudyListDTO> getAllStudies() {
        List<StudyListDTO> list = new ArrayList<>();
        String sql = "SELECT study_id, name, start_date, end_date, cert_method, deposit, status FROM db2025team02StudyGroups";

        try (PreparedStatement ps = AppMain.conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                StudyListDTO dto = new StudyListDTO(
                    rs.getInt("study_id"),
                    rs.getString("name"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getString("cert_method"),
                    rs.getInt("deposit"),
                        rs.getString("status")
                );
                list.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public List<StudyListDTO> searchStudiesByName(String keyword) {
        List<StudyListDTO> list = new ArrayList<>();
        String sql = "SELECT study_id, name, start_date, end_date, cert_method, deposit, status " +
                     "FROM db2025team02StudyGroups WHERE name LIKE ? ORDER BY name ASC";

        try (PreparedStatement ps = AppMain.conn.prepareStatement(sql)) {
            ps.setString(1, keyword + "%");	// 이 부분 파라미터 지정 순서가 틀려서 위로 올려뒀습니다.
            ResultSet rs = ps.executeQuery(); 

          
            while (rs.next()) {
                StudyListDTO dto = new StudyListDTO(
                    rs.getInt("study_id"),
                    rs.getString("name"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getString("cert_method"),
                    rs.getInt("deposit"),
                        rs.getString("status")
                );
                list.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    
    
    public List<StudyListDTO> getSortedStudies(String sortField, String sortOrder) {
        List<StudyListDTO> list = new ArrayList<>();

        List<String> allowedFields = List.of("name", "start_date", "end_date", "deposit");
        List<String> allowedOrder = List.of("ASC", "DESC");

        if (!allowedFields.contains(sortField)) sortField = "name";
        if (!allowedOrder.contains(sortOrder)) sortOrder = "ASC";

        String sql = "SELECT study_id, name, start_date, end_date, cert_method, deposit, status " +
                "FROM db2025team02StudyGroups ORDER BY " + sortField + " " + sortOrder;

        try (PreparedStatement ps = AppMain.conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                StudyListDTO dto = new StudyListDTO(
                    rs.getInt("study_id"),
                    rs.getString("name"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getString("cert_method"),
                    rs.getInt("deposit"),
                        rs.getString("status")
                );
                list.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    
    
    public List<StudyListDTO> searchAndSortStudies(String keyword, String sortField, String sortOrder) {
        List<StudyListDTO> list = new ArrayList<>();

        List<String> allowedFields = List.of("name", "start_date", "end_date", "deposit");
        List<String> allowedOrder = List.of("ASC", "DESC");

        if (!allowedFields.contains(sortField)) sortField = "name";
        if (!allowedOrder.contains(sortOrder)) sortOrder = "ASC";

        String sql = "SELECT study_id, name, start_date, end_date, cert_method, deposit, status " +
                     "FROM db2025team02StudyGroups WHERE name LIKE ? ORDER BY " + sortField + " " + sortOrder;

        try (PreparedStatement ps = AppMain.conn.prepareStatement(sql)) {
            ps.setString(1, keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                StudyListDTO dto = new StudyListDTO(
                    rs.getInt("study_id"),
                    rs.getString("name"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getString("cert_method"),
                    rs.getInt("deposit"),
                        rs.getString("status")
                );
                list.add(dto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    
    public List<StudyListDTO> getStudiesByMember(int userId) {
        List<StudyListDTO> list = new ArrayList<>();
        String sql = """
    SELECT sg.study_id, sg.name, sg.start_date, sg.end_date, sg.cert_method, sg.deposit, sg.status
    FROM db2025team02StudyGroups sg
    JOIN db2025team02GroupMembers gm ON sg.study_id = gm.study_id
    WHERE gm.user_id = ? AND gm.status = 'active'
    ORDER BY sg.name ASC
""";


        try (PreparedStatement ps = AppMain.conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new StudyListDTO(
                    rs.getInt("study_id"),
                    rs.getString("name"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getString("cert_method"),
                    rs.getInt("deposit"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }



}
