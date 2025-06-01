#데이터베이스 및 권한 설정
DROP DATABASE IF EXISTS DB2025Team02;
DROP USER IF EXISTS DB2025Team02@localhost;
create user DB2025Team02@localhost IDENTIFIED BY 'DB2025Team02';
create database DB2025Team02;
grant all privileges on
    DB2025Team02.* to DB2025Team02@localhost with grant option;
commit;
use DB2025Team02;

/* 아래로 테이블 생성 (스키마) */

# 01. 유저 테이블 - 사용자 정보 저장 
CREATE TABLE DB2025Team02Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,	-- 각 사용자의 고유 식별자(PK)
    login_id VARCHAR(50) NOT NULL UNIQUE,	-- 사용자의 실질적 ID(중복 방지)
    user_name VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,	-- 비밀번호의 해시값, 가변 문자열 사용
    points INT DEFAULT 0					-- 시스템 내 포인트 내역(보증금 및 정산에 사용, 한 유저가 여러 스터디 그룹에 가입할 수 있기 때문에 총 포인트를 저장하는 속성을 따로 뒀습니다.)
);


# 02. 스터디 그룹 테이블 - 스터디 그룹 정보 저장
CREATE TABLE DB2025Team02StudyGroups (
    study_id INT AUTO_INCREMENT PRIMARY KEY,               -- 스터디 고유 ID
    name VARCHAR(100) NOT NULL,                            -- 스터디 이름
    leader_id INT,                                         -- 스터디장 ID
    description TEXT,                                      -- 스터디 설명
    start_date DATE,                                       -- 시작일
    end_date DATE,                                         -- 종료일
    cert_method VARCHAR(50),                               -- 인증 방식
    deposit INT,                                           -- 보증금
    status ENUM('ongoing', 'closed') DEFAULT 'ongoing',  -- 스터디 상태
    FOREIGN KEY (leader_id) REFERENCES DB2025Team02Users(user_id) ON DELETE SET NULL
);



# 03. GroupMembers 테이블 - 스터디 참여자 관리, M:N 관계 중간 테이블
CREATE TABLE DB2025Team02GroupMembers (

    study_id INT,							-- 가입한 스터디 ID(FK)
    user_id INT,							-- 참여자의 ID(FK)
    accumulated_fine INT DEFAULT 0,			-- 누적된 벌금
    status ENUM('active', 'suspended', 'withdrawn', 'completed') DEFAULT 'active',	-- 참여자들의 상태
    PRIMARY KEY (study_id, user_id),		-- 같은 스터디에 동일한 참여자가 중복 가입을 하지 못하도록 스터디 ID와 유저 ID로 복합 기본키를 생성(PK)
    FOREIGN KEY (study_id) REFERENCES DB2025Team02StudyGroups(study_id),	-- 가입한 스터디 ID 참조하는 외래키
    FOREIGN KEY (user_id) REFERENCES DB2025Team02Users(user_id)				-- 참여자의 ID 참조하는 외래키
);

# 04. DailyCerts 테이블 - 인증 내역을 저장 (CertHistory로 이름 바꿔도 괜찮을 듯)
CREATE TABLE DB2025Team02DailyCerts (
    cert_id INT AUTO_INCREMENT PRIMARY KEY,		-- 인증 고유 ID(PK)
    user_id INT,								-- 인증자의 유저 ID(FK)
    study_id INT,								-- 인증자가 속한 스터디의 ID(FK)
    cert_date DATE,								-- 인증한 날짜
    content TEXT,
    cycle_no INT NOT NULL, -- 인증 주차 (스터디 몇주차의 인증인지)
    approval_status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    FOREIGN KEY (user_id) REFERENCES DB2025Team02Users(user_id),			-- 인증자의 유저 ID를 참조하는 외래키
    FOREIGN KEY (study_id) REFERENCES DB2025Team02StudyGroups(study_id)		-- 인증자가 속한 스터디 ID를 참조하는 외래키
);

# 05. Rules 테이블 - 스터디별 규칙을 저장
CREATE TABLE DB2025Team02Rules (
    rule_id INT AUTO_INCREMENT PRIMARY KEY,	-- 룰 고유 ID(PK)
    study_id INT,							-- 해당 룰 설립한 스터디 ID(FK)
    cert_cycle INT,							-- 인증 주기 (예: 7일)
	grace_period INT,						-- 인증 유예 허용 기간
    fine_late INT, 							-- 지각 시 벌금
    fine_absent INT,						-- 미인증 시 벌금 (보증금 깎을 때는 penalty_absent-penalty_late값으로 처리?)
    ptsettle_cycle INT,						-- 보증금 정산 주기 (예: 7일)
    last_modified DATE,						-- 마지막 규칙 수정일
    next_cert_date DATE,					-- 다음 인증 날짜
    FOREIGN KEY (study_id) REFERENCES DB2025Team02StudyGroups(study_id)		-- 해당 룰 설립한 스터디 ID를 참조하는 외래키
);

# 06. Fines 테이블 - 벌금 부과 기록 저장 (이것도 fine history로 적어도 괜찮을 듯)
CREATE TABLE DB2025Team02Fines (
    fine_id INT AUTO_INCREMENT PRIMARY KEY,		-- 벌금 내역 고유 ID(PK)
    user_id INT,								-- 벌금을 부과받은 사용자의 ID(FK)
    study_id INT,
    reason VARCHAR(100),						-- 벌금 사유 (미인증, 지각 등)
    amount INT,									-- 벌금 액수
    date DATE,									-- 벌금이 부과된 인증 주차의 마지막 날
    FOREIGN KEY (user_id) REFERENCES DB2025Team02Users(user_id),	-- 벌금 부과 받은 사용자의 ID 외래키
    FOREIGN KEY (study_id) REFERENCES DB2025Team02StudyGroups(study_id)
);

# 07. Deposits 테이블 - 보증금 입금 및 반환 내역 관리
CREATE TABLE DB2025Team02Deposits (
    deposit_id INT AUTO_INCREMENT PRIMARY KEY,	-- 보증금 내역 고유 ID(PK)
    user_id INT,								-- 입금한 사용자의 ID(FK)
    study_id INT,								-- 보증금 낸 스터디 ID(FK)
    amount INT,									-- 입금한 금액 (구매한 포인트)
    deposit_date DATE,							-- 입금일
    is_refunded BOOLEAN DEFAULT FALSE,			-- 보증금(포인트) 반환 여부
    FOREIGN KEY (user_id) REFERENCES DB2025Team02Users(user_id),			-- 입금한 사용자의 ID 참조하는 외래키
    FOREIGN KEY (study_id) REFERENCES DB2025Team02StudyGroups(study_id)		-- 보증금 낸 스터디 ID 참조하는 외래키
);



/* 아래로 인덱스 정의 - 쿼리 쓸 때 편하라고... 이전 그룹들 github 보면서 비슷하게 만들어 봤습니다. 수정 편하게 하세요. */

# 01. DailyCertsDAO에서 사용되는 인덱스
CREATE INDEX idx_cert_user_study_week_status_date
    ON DB2025Team02DailyCerts(user_id, study_id, cycle_no, approval_status, cert_date);

#2. 스터디그룹의 이름 인덱스를 만들어서 이름으로 서치가 가능
CREATE INDEX idx_study_name ON DB2025Team02StudyGroups(name);

#3. 시작일 인덱스를 만들어서 시작일로 정렬 가능
CREATE INDEX idx_start_date ON DB2025Team02StudyGroups(start_date);

#4. 종료일 인덱스를 만들어서 종료일로 정렬 가능
CREATE INDEX idx_end_date ON DB2025Team02StudyGroups(end_date);

#5. 보증금 인덱스를 만들어서 보증금으로 정렬 가능
CREATE INDEX idx_deposit ON DB2025Team02StudyGroups(deposit);


/* 아래로 뷰 정의 */

# 01. 스터디 그룹마다 현재 활동 중인(still active) 참여자 수, 누적 벌금 합계, 참여자 목록
CREATE VIEW DB2025Team02StudySummary AS
SELECT
    sg.study_id,
    sg.name AS study_name,
    COUNT(gm.user_id) AS member_count,
    IFNULL(SUM(gm.accumulated_fine), 0) AS total_fine,
    GROUP_CONCAT(DISTINCT u.user_name ORDER BY u.user_name SEPARATOR ', ') AS member_names
FROM DB2025Team02StudyGroups sg
         LEFT JOIN DB2025Team02GroupMembers gm ON sg.study_id = gm.study_id AND gm.status = 'active'
         LEFT JOIN DB2025Team02Users u ON gm.user_id = u.user_id
GROUP BY sg.study_id;

# 02. 인증 관리 화면에 나타나는 스터디들을 위한 뷰
CREATE VIEW DB2025Team02CertStatusView AS
SELECT
    gm.user_id,
    sg.study_id,
    sg.name AS study_name,
    u.user_name AS leader_name,
    sg.start_date,
    sg.status,
    sg.leader_id,
    r.next_cert_date
FROM db2025team02GroupMembers gm
         JOIN db2025team02StudyGroups sg ON gm.study_id = sg.study_id
         JOIN db2025team02Users u ON sg.leader_id = u.user_id
         LEFT JOIN db2025team02Rules r ON sg.study_id = r.study_id
WHERE gm.status = 'active' AND sg.status = 'ongoing';



DROP EVENT IF EXISTS DB2025Team02UpdateStudyStatus;

DELIMITER //

CREATE EVENT DB2025Team02UpdateStudyStatus
    ON SCHEDULE EVERY 1 DAY
        STARTS CURRENT_TIMESTAMP
    DO
    BEGIN
        -- 1. 스터디 종료 처리
        UPDATE DB2025Team02StudyGroups
        SET status = 'closed'
        WHERE end_date < CURDATE() AND status != 'closed';

        -- 2. 스터디 상태 복원 (종료되지 않은 건 ongoing)
        UPDATE DB2025Team02StudyGroups
        SET status = 'ongoing'
        WHERE end_date >= CURDATE() AND status != 'ongoing';

        -- 3. 종료된 스터디 참여자 상태 completed 처리
        UPDATE DB2025Team02GroupMembers gm
            JOIN DB2025Team02StudyGroups sg ON gm.study_id = sg.study_id
        SET gm.status = 'completed'
        WHERE sg.status = 'closed' AND gm.status = 'active';

        -- 4. 보증금 반환 (포인트 + 보증금 금액, 반환 여부 TRUE 처리)
        UPDATE DB2025Team02Users u
            JOIN DB2025Team02Deposits d ON u.user_id = d.user_id
            JOIN DB2025Team02StudyGroups sg ON d.study_id = sg.study_id
        SET u.points = u.points + sg.deposit,
            d.is_refunded = TRUE
        WHERE sg.status = 'closed' AND d.is_refunded = FALSE;

    END;
//

DELIMITER ;

/* 인덱스 잘 활용되고 있는지 테스트 - report에 쓰면 좋을듯

  # 인덱스 1번 -hasPrevWeekCertified()

EXPLAIN SELECT COUNT(*)
        FROM DB2025Team02DailyCerts
        WHERE user_id = 1 AND study_id = 3 AND cert_date BETWEEN '2025-05-20' AND '2025-05-26' AND approval_status != 'rejected';

 #인덱스 1번 -hasPrevWeekCertifiedInGracePeriod()
EXPLAIN SELECT 1
        FROM db2025team02DailyCerts
        WHERE user_id = 1 AND study_id = 3
          AND cert_date BETWEEN '2025-05-27' AND '2025-05-29'
          AND week_no = 4
          AND approval_status != 'rejected'
        LIMIT 1;

#인덱스 1번 -hasCertifiedWeek()
EXPLAIN SELECT COUNT(*)
        FROM db2025team02DailyCerts
        WHERE user_id = 1 AND study_id = 3 AND week_no = 4;


#인덱스 2번 - 스터디그룹의 이름 인덱스를 만들어서 이름으로 서치가 가능
EXPLAIN SELECT study_id, name, start_date, end_date, cert_method, deposit, status
        FROM db2025team02StudyGroups
        WHERE name LIKE '스터디%'
        ORDER BY name ASC;
#3
EXPLAIN SELECT study_id, name, start_date, end_date, cert_method, deposit, status
        FROM db2025team02StudyGroups
        ORDER BY start_date ASC;
#4
EXPLAIN SELECT study_id, name, start_date, end_date, cert_method, deposit, status
        FROM db2025team02StudyGroups
        ORDER BY end_date DESC;
#5
EXPLAIN SELECT study_id, name, start_date, end_date, cert_method, deposit, status
        FROM db2025team02StudyGroups
        ORDER BY deposit DESC;

#2,5
EXPLAIN SELECT study_id, name, start_date, end_date, cert_method, deposit, status
        FROM db2025team02StudyGroups
        WHERE name LIKE '알고리즘%'
        ORDER BY deposit ASC;
*/










