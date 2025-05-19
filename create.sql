# DB 생성
CREATE DATABASE DB2025Team02;
USE DB2025Team02;

# DB 유저 생성 + 권한 부여
CREATE USER 'DB2025Team02' @localhost identified by 'DB2025Team02';
GRANT ALL PRIVILEGES on DB2025Team02.* to 'DB2025Team02' @localhost;


/* 아래로 테이블 생성 (스키마) */

# 01. 유저 테이블 - 사용자 정보 저장 
CREATE TABLE Users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,	-- 각 사용자의 고유 식별자(PK)
    login_id VARCHAR(50) NOT NULL UNIQUE,	-- 사용자의 실질적 ID(중복 방지)
    user_name VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,	-- 비밀번호의 해시값, 가변 문자열 사용
    points INT DEFAULT 0					-- 시스템 내 포인트 내역(보증금 및 정산에 사용, 한 유저가 여러 스터디 그룹에 가입할 수 있기 때문에 총 포인트를 저장하는 속성을 따로 뒀습니다.)
);


# 02. 스터디 그룹 테이블 - 스터디 그룹 정보 저장
CREATE TABLE StudyGroups (
    study_id INT AUTO_INCREMENT PRIMARY KEY,	-- 각 스터디 그룹의 고유 번호(PK)
    name VARCHAR(100) NOT NULL,					-- 스터디 이름
    leader_id INT NOT NULL,						-- 스터디장의 사용자 ID
    description TEXT,							-- 스터디 설명 (가입하려는 사람들 읽으라고...)
    start_date DATE,							-- 스터디 개설일
    end_date DATE,								-- 스터디 종료일
    cert_method VARCHAR(50),					-- 인증 방식 (text, photo, video...etc)
    deposit INT,								-- 보증금 금액 명시
    FOREIGN KEY (leader_id) REFERENCES Users(user_id)	-- 스터디장 ID는 Users의 user_id를 참조하는 FK
);


# 03. GroupMembers 테이블 - 스터디 참여자 관리, M:N 관계 중간 테이블
CREATE TABLE GroupMembers (
    study_id INT,							-- 가입한 스터디 ID(FK)
    user_id INT,							-- 참여자의 ID(FK)
    accumulated_fine INT DEFAULT 0,			-- 누적된 벌금
    status VARCHAR(20)  DEFAULT 'active',	-- 참여자들의 상태(active, kicked_out, vacation, suspended 등으로 표현 가능?)
    PRIMARY KEY (study_id, user_id),		-- 같은 스터디에 동일한 참여자가 중복 가입을 하지 못하도록 스터디 ID와 유저 ID로 복합 기본키를 생성(PK)
    FOREIGN KEY (study_id) REFERENCES StudyGroups(study_id),	-- 가입한 스터디 ID 참조하는 외래키
    FOREIGN KEY (user_id) REFERENCES Users(user_id)				-- 참여자의 ID 참조하는 외래키
);


# 04. DailyCerts 테이블 - 인증 내역을 저장 (CertHistory로 이름 바꿔도 괜찮을 듯)
CREATE TABLE DailyCerts (
    cert_id INT AUTO_INCREMENT PRIMARY KEY,		-- 인증 고유 ID(PK)
    user_id INT,								-- 인증자의 유저 ID(FK)
    study_id INT,								-- 인증자가 속한 스터디의 ID(FK)
    cert_date DATE,								-- 인증한 날짜
    content TEXT,								-- 인증 자료? 설명이나 기록, 링크/경로 등
    is_approved BOOLEAN DEFAULT FALSE,			-- 스터디장 승인 여부 (default false 빼고 null로 둬도 괜찮긴 한데 일단 null 최대한 줄입니다)
    FOREIGN KEY (user_id) REFERENCES Users(user_id),			-- 인증자의 유저 ID를 참조하는 외래키
    FOREIGN KEY (study_id) REFERENCES StudyGroups(study_id)		-- 인증자가 속한 스터디 ID를 참조하는 외래키
);

# 05. Rules 테이블 - 스터디별 규칙을 저장
CREATE TABLE Rules (
    rule_id INT AUTO_INCREMENT PRIMARY KEY,	-- 룰 고유 ID(PK)
    study_id INT,							-- 해당 룰 설립한 스터디 ID(FK)
    cert_deadline TIME,						-- 인증 마감 시각 (예: 23:00:00)
    cert_cycle INT,							-- 인증 주기 (예: 7일)
	grace_period INT,						-- 인증 유예 허용 기간
    fine_late INT, 							-- 지각 시 벌금
    fine_absent INT,						-- 미인증 시 벌금 (보증금 깎을 때는 penalty_absent-penalty_late값으로 처리?)
    ptsettle_cycle INT,						-- 보증금 정산 주기 (예: 7일)
    last_modified DATE,						-- 마지막 규칙 수정일
    FOREIGN KEY (study_id) REFERENCES StudyGroups(study_id)		-- 해당 룰 설립한 스터디 ID를 참조하는 외래키
);

# 06. Fines 테이블 - 벌금 부과 기록 저장 (이것도 fine history로 적어도 괜찮을 듯)
CREATE TABLE Fines (
    fine_id INT AUTO_INCREMENT PRIMARY KEY,		-- 벌금 내역 고유 ID(PK)
    user_id INT,								-- 벌금을 부과받은 사용자의 ID(FK)
    study_id INT,
    is_paid boolean,
    reason VARCHAR(100),						-- 벌금 사유 (미인증, 지각 등)
    amount INT,									-- 벌금 액수
    date DATE,									-- 벌금 부과 일자
    FOREIGN KEY (user_id) REFERENCES Users(user_id),	-- 벌금 부과 받은 사용자의 ID 외래키
    FOREIGN KEY (study_id) REFERENCES StudyGroups(study_id)
);

# 07. Deposits 테이블 - 보증금 입금 및 반환 내역 관리
CREATE TABLE Deposits (
    deposit_id INT AUTO_INCREMENT PRIMARY KEY,	-- 보증금 내역 고유 ID(PK)
    user_id INT,								-- 입금한 사용자의 ID(FK)
    study_id INT,								-- 보증금 낸 스터디 ID(FK)
    amount INT,									-- 입금한 금액 (구매한 포인트)
    deposit_date DATE,							-- 입금일
    is_refunded BOOLEAN DEFAULT FALSE,			-- 보증금(포인트) 반환 여부
    FOREIGN KEY (user_id) REFERENCES Users(user_id),			-- 입금한 사용자의 ID 참조하는 외래키
    FOREIGN KEY (study_id) REFERENCES StudyGroups(study_id)		-- 보증금 낸 스터디 ID 참조하는 외래키
);

#
#
# /* 아래로 인덱스 정의 - 쿼리 쓸 때 편하라고... 이전 그룹들 github 보면서 비슷하게 만들어 봤습니다. 수정 편하게 하세요. */
#
# # 01. 인증 자료 조회 시 사용자와 날짜 기준으로 빠르게 접근
# -- 유저가 오늘 인증했는지 여부 조회할 때 사용
CREATE INDEX idx_dailycerts ON DailyCerts(user_id, cert_date);
#
# # 02. 반환되지 않은 보증금 빠르게 조회
# -- 정산 시 반환되지 않은 보증금을 찾을 필요 있음.
CREATE INDEX idx_refund ON Deposits(user_id, is_refunded);
#
# # 03. 스터디와 유저에 따른 보증금 빠르게 조회
CREATE INDEX idx_refundid ON Deposits(user_id, study_id);
#
# # 04. 스터디그룹의 운영상태에 따라 운영중인 스터디그룹을 빠르게 확인할 수 있도록
# -- 그러나 현재 status 컬럼이 없어서 ALTER로 추가해봤습니다
ALTER TABLE StudyGroups ADD COLUMN status ENUM('모집중', '진행중', '종료') DEFAULT '모집중';
#
# -- 인덱스 추가
CREATE INDEX idx_status ON StudyGroups(status);
# --status 컬럼 컨펌나면 SET GLOBAL event_scheduler = ON;을 추가해서 자동으로 현재날짜에 따라 스터디 상태를 바꿀 수 있도록 하는 것도 좋을것같습니다
#
# # 05. 스터디그룹의 이름 인덱스를 만들어서 이름으로 서치가 가능
CREATE INDEX idx_study_name ON StudyGroup(name);

/* 아래로 뷰 정의 */

# 01. MyStudy 페이지에서 한 스터디의 통계와 스터디 정보를 모두 확인할 수 있도록하는 뷰
# --한 뷰에 담기에 넣기로 한 내용이 다소 많아서, rules 테이블의 내용은 따로 가져오게 처리하는 것을 제안
# --스터디통계와 스터디 내 사람들의 누적벌금, 스터디 총벌금을 보는 뷰를 만듦
CREATE VIEW StudyMember_Summary AS
SELECT 
    sg.study_id,
    sg.name AS study_name,
    COUNT(gm.user_id) AS member_count,
    IFNULL(SUM(gm.accumulated_fine), 0) AS total_fine,
    GROUP_CONCAT(DISTINCT u.user_name ORDER BY u.user_name SEPARATOR ', ') AS member_names,
    gm.user_id,
    u.user_name,
    gm.accumulated_fine
FROM StudyGroups sg
LEFT JOIN GroupMembers gm ON sg.study_id = gm.study_id AND gm.status = 'active'
LEFT JOIN Users u ON gm.user_id = u.user_id
GROUP BY sg.study_id, gm.user_id;

# 02. 스터디장의 마이스터디 페이지에서 다른 스터디원들의 인증내역(중 아직 승인되지 않은것)을 확인하는 뷰
CREATE VIEW PendingCertifications AS
SELECT
    sg.study_id,
    sg.name AS study_name,
    sg.leader_id,
    u.user_id,
    u.user_name,
    dc.cert_id,
    dc.cert_date,
    dc.content,
    dc.is_approved
FROM DailyCerts dc
JOIN Users u ON dc.user_id = u.user_id
JOIN StudyGroups sg ON dc.study_id = sg.study_id
WHERE
    dc.is_approved = FALSE
;

select * from users;