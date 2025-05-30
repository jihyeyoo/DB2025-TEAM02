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

    -- 시작일이 종료일보다 이전에 있도록 수정
    CHECK (startDate <= endDate),
    -- 시작일은 생성일로부터 7일 이내여야 함
    CHECK (startDate <= DATE_ADD(createdAt, INTERVAL 7 DAY))
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
    content TEXT,								-- 인증 자료? 설명이나 기록, 링크/경로 등
    approval_status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    FOREIGN KEY (user_id) REFERENCES DB2025Team02Users(user_id),			-- 인증자의 유저 ID를 참조하는 외래키
    FOREIGN KEY (study_id) REFERENCES DB2025Team02StudyGroups(study_id)		-- 인증자가 속한 스터디 ID를 참조하는 외래키
);

# 05. Rules 테이블 - 스터디별 규칙을 저장
CREATE TABLE DB2025Team02Rules (
    rule_id INT AUTO_INCREMENT PRIMARY KEY,	-- 룰 고유 ID(PK)
    study_id INT,							-- 해당 룰 설립한 스터디 ID(FK)
    cert_deadline TIME,						-- 인증 마감 시각 (예: 23:00:00)
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
    is_paid boolean,
    reason VARCHAR(100),						-- 벌금 사유 (미인증, 지각 등)
    amount INT,									-- 벌금 액수
    date DATE,									-- 벌금 부과 일자
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

# 01. 인증 자료 조회 시 사용자와 날짜 기준으로 빠르게 접근
-- 유저가 인증 기간 내에 인증했는지 여부 조회할 때 사용
CREATE INDEX indx_dailycerts ON DB2025Team02DailyCerts(user_id, study_id, cert_date); #dao에서 활용되게 해야함

# 02. 반환되지 않은 보증금 빠르게 조회
-- 정산 시 반환되지 않은 보증금을 찾을 필요 있음.
CREATE INDEX idx_refund ON DB2025Team02Deposits(user_id, is_refunded); #dao에서 활용되게 해야함

# # 03. 스터디와 유저에 따른 보증금 빠르게 조회
CREATE INDEX idx_refundid ON DB2025Team02Deposits(user_id, study_id); #dao에서 활용되게 해야함

# # 05. 스터디그룹의 이름 인덱스를 만들어서 이름으로 서치가 가능
CREATE INDEX idx_study_name ON DB2025Team02StudyGroups(name);

# # 06. 시작일 인덱스를 만들어서 시작일로 정렬 가능
CREATE INDEX idx_start_date ON DB2025Team02StudyGroups(start_date);

# # 07. 종료일 인덱스를 만들어서 종료일로 정렬 가능
CREATE INDEX idx_end_date ON DB2025Team02StudyGroups(end_date);

# # 08. 보증금 인덱스를 만들어서 보증금으로 정렬 가능
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


# 02. 스터디장의 마이스터디 페이지에서 스터디원들의 인증내역(중 아직 승인되지 않은것)을 확인하는 뷰
CREATE VIEW DB2025Team02PendingCertifications AS
SELECT
    sg.study_id,
    sg.name AS study_name,
    sg.leader_id,
    u.user_id,
    u.user_name,
    dc.cert_id,
    dc.cert_date,
    dc.content,
    dc.approval_status
FROM DB2025Team02DailyCerts dc
         JOIN DB2025Team02Users u ON dc.user_id = u.user_id
         JOIN DB2025Team02StudyGroups sg ON dc.study_id = sg.study_id
WHERE
    dc.approval_status = 'pending';


# 자동으로 StudyGroup 상태 업데이트하는 event scheduler

SET GLOBAL event_scheduler = ON;

DROP EVENT IF EXISTS DB2025Team02UpdateStudyStatus;

DELIMITER //

CREATE EVENT DB2025Team02UpdateStudyStatus
    ON SCHEDULE EVERY 1 DAY
        STARTS CURRENT_TIMESTAMP
    DO
    BEGIN
        UPDATE DB2025Team02StudyGroups
        SET status = 'closed'
        WHERE end_date < CURDATE() AND status != 'closed';

        UPDATE DB2025Team02StudyGroups
        SET status = 'ongoing'
        WHERE end_date >= CURDATE() AND status != 'ongoing';
    END;
//

DELIMITER ;

SET GLOBAL event_scheduler = ON;

DROP EVENT IF EXISTS UpdateNextCertDate;

DELIMITER //

CREATE EVENT UpdateNextCertDate
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP
DO
BEGIN
  UPDATE DB2025Team02Rules r
  JOIN DB2025Team02StudyGroups sg ON r.study_id = sg.study_id
  SET r.next_cert_date =
    CASE
      WHEN CURDATE() <= sg.start_date THEN 
        DATE_ADD(sg.start_date, INTERVAL r.cert_cycle DAY)
      ELSE 
        DATE_ADD(
          sg.start_date,
          INTERVAL CEIL(DATEDIFF(CURDATE(), sg.start_date) / r.cert_cycle) * r.cert_cycle DAY
        )
    END;
END;
//

DELIMITER ;
