package main;

import java.sql.*;
import java.util.List;
import java.util.Scanner;

import DAO.LoginDAO;
import DAO.StudyGroupDAO;
import DTO.LoginResult;
import GUI.SignUp;
import model.LoginStatus;
import model.StudyGroup;
import model.User;

public class AppMain {
    public static Connection conn;

    public static void main(String[] args) {
        String DRIVER = "com.mysql.cj.jdbc.Driver";
        String DBURL = "jdbc:mysql://localhost:3306/db2025team02";
        String DBID = "root"; //본인 sql user 이름 입력하세요
        String DBPW = "wkd110614!"; //본인 sql pw 입력하세요

        try {
            Class.forName(DRIVER);

            conn = DriverManager.getConnection(DBURL, DBID, DBPW);
            System.out.println("연결되었습니다.");
            
            new SignUp();

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 드라이버를 찾을 수 없습니다.");
        } catch (SQLException e) {
            System.out.println("DB 연결 오류");
        }
        
        LoginDAO loginDAO = new LoginDAO();
        LoginResult result = loginDAO.login("akd", "wwwww");

        switch (result.getStatus()) {
            case SUCCESS -> {
                System.out.println("✅ 로그인 성공!");
                // User 정보 출력
                User user = result.getUser();
                if (user != null) {
                    System.out.println("▶ User ID: " + user.getUserId());
                    System.out.println("▶ Login ID: " + user.getLoginId());
                    System.out.println("▶ User Name: " + user.getUserName());
                    System.out.println("▶ Points: " + user.getPoints());
                }
            }
            case INVALID_PASSWORD -> System.out.println("❌ 비밀번호 오류!");
            case ID_NOT_FOUND -> System.out.println("❌ ID가 존재하지 않음!");
            case ERROR -> System.out.println("⚠️ 시스템 오류 발생!");
        }
        
        StudyGroupDAO dao = new StudyGroupDAO();
//        boolean result1 = dao.createStudyGroup(
//            "스터디 그룹 테스트", 
//            1,  // 리더 user_id
//            "자바 공부하는 스터디입니다.", 
//            Date.valueOf("2025-05-10"), 
//            Date.valueOf("2025-06-10"), 
//            "text", 
//            5000
//        );
//
//        if (result1) {
//            System.out.println("✅ 스터디 개설 성공! 리더도 자동 등록됨.");
//        } else {
//            System.out.println("❌ 스터디 개설 실패.");
//        }
        
        List<StudyGroup> studyGroups = dao.getAllStudyGroups();

        System.out.println("📚 현재 스터디 목록:");
        for (StudyGroup group : studyGroups) {
            System.out.println("▶ 스터디명: " + group.getName() + "스터디 id " + group.getStudyId());
        }
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("\n조회할 스터디 ID를 입력하세요: ");
        int inputStudyId = scanner.nextInt();

        StudyGroup selectedGroup = dao.getStudyGroupById(inputStudyId);
        if (selectedGroup != null) {
            System.out.println("\n📖 스터디 상세 정보:");
            System.out.println("▶ 스터디명: " + selectedGroup.getName());
            System.out.println("▶ 설명: " + selectedGroup.getDescription());
            System.out.println("▶ 리더 ID: " + selectedGroup.getLeaderId());
            System.out.println("▶ 시작일: " + selectedGroup.getStartDate());
            System.out.println("▶ 종료일: " + selectedGroup.getEndDate());
            System.out.println("▶ 인증 방법: " + selectedGroup.getCertMethod());
            System.out.println("▶ 예치금: " + selectedGroup.getDeposit());
        } else {
            System.out.println("❌ 해당 스터디 그룹이 존재하지 않습니다.");
        }

        scanner.close();
     
        
   }
}