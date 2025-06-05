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
    FOREIGN KEY (leader_id) REFERENCES DB2025Team02Users(user_id) ON DELETE SET NULL,

    -- 시작일이 종료일보다 이전에 있도록 수정
    CHECK (startDate <= endDate),
    -- 시작일은 생성일로부터 7일 이내여야 함
    CHECK (startDate <= DATE_ADD(createdAt, INTERVAL 7 DAY))
);



# 03. GroupMembers 테이블 - 스터디 참여자 관리, M:N 관계 중간 테이블
CREATE TABLE DB2025Team02GroupMembers (

    study_id INT,							-- 가입한 스터디 ID(FK)
    user_id INT ,							-- 참여자의 ID(FK)
    accumulated_fine INT DEFAULT 0,			-- 누적된 벌금
    status ENUM('active', 'suspended', 'withdrawn', 'completed') DEFAULT 'active',	-- 참여자들의 상태
    PRIMARY KEY (study_id, user_id),		-- 같은 스터디에 동일한 참여자가 중복 가입을 하지 못하도록 스터디 ID와 유저 ID로 복합 기본키를 생성(PK)
    FOREIGN KEY (study_id) REFERENCES DB2025Team02StudyGroups(study_id),	-- 가입한 스터디 ID 참조하는 외래키
    FOREIGN KEY (user_id) REFERENCES DB2025Team02Users(user_id) ON DELETE CASCADE -- 참여자 ID 참조하는 외래키 (참여자 삭제시 해당 스터디 참여 정보도 삭제)
);

# 04. DailyCerts 테이블 - 인증 내역을 저장 (CertHistory로 이름 바꿔도 괜찮을 듯)
CREATE TABLE DB2025Team02DailyCerts (
    cert_id INT AUTO_INCREMENT PRIMARY KEY,		-- 인증 고유 ID(PK)
    user_id INT,								-- 인증자의 유저 ID(FK)
    study_id INT,								-- 인증자가 속한 스터디의 ID(FK)
    cert_date DATE,								-- 인증한 날짜
    content TEXT,
    cycle_no INT, -- 인증 주차 (스터디 몇주차의 인증인지)
    approval_status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    FOREIGN KEY (user_id) REFERENCES DB2025Team02Users(user_id) ON DELETE SET NULL,			-- 인증자의 유저 ID를 참조하는 외래키
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
    FOREIGN KEY (user_id) REFERENCES DB2025Team02Users(user_id) ON DELETE SET NULL,	-- 벌금 부과 받은 사용자의 ID 외래키
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
    FOREIGN KEY (user_id) REFERENCES DB2025Team02Users(user_id) ON DELETE SET NULL,			-- 입금한 사용자의 ID 참조하는 외래키
    FOREIGN KEY (study_id) REFERENCES DB2025Team02StudyGroups(study_id)		-- 보증금 낸 스터디 ID 참조하는 외래키
);


#1. DailyCertsDAO에서 사용되는 인덱스
CREATE INDEX idx_cert_user_study_week_status_date
    ON DB2025Team02DailyCerts(user_id, study_id, cycle_no, approval_status, cert_date);

#2. 스터디그룹의 이름으로 검색하는데 사용하는 인덱스
CREATE INDEX idx_study_name ON DB2025Team02StudyGroups(name);

#3. 스터디 그룹의 정렬에 사용되는 인덱스
CREATE INDEX idx_sort_covering
    ON db2025team02StudyGroups(start_date, study_id, name, end_date, cert_method, deposit, status);


#4. 반환된 보증금 조회에 사용되는 인덱스
CREATE INDEX idx_deposit_user_refund
    ON db2025team02Deposits(user_id, is_refunded);


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

#인덱스 잘 활용되고 있는지 테스트 - report에 쓰면 좋을듯

-- 사용자 삽입 (로그인 및 회원가입용)
-- 더미 데이터 유저들의 비밀번호는 아이디와 동일하게 설정했습니다.
INSERT INTO DB2025Team02Users (login_id, user_name, password_hash, points) VALUES
('leader01', '김이화', 'e7e429b165665e63a2e20c59046542a45d4e6b64277bd73fc7814ec2d8f3d484', 10000),
('leader02', '박소영', '17b93c58b212fb5fbfb7939ef419e325ee13a6c8d94cfe8c25523376fbf45379', 10000),
('leader03', '이현우', '34e8df6aaecf5fc1bcd1d3c2d18d9b52c6325183b2ca80d8457ec0744f1b37db', 10000),
('member01', '강효은', '33448caa4b401941646d49b496901664caf26f42e406830447c9d17e184ac940', 10000),
('member02', '선유나', '31571d6962d7e2df880ea9f271b7d4dc484591b9e87c8ea89eed290256f6e19b', 9000),
('member03', '이하진', '7f9b974965cba255e1b8c395c0a709331bca051e2172731d5c332a2c4ce6c200', 7000),
('member04', '최서윤', 'bd65301bdcc8d3f003bb4501ccad302a0067b2e12e387dd876aeb34940267aef', 5000),
('member05', '강민재', '33858725e1f8613827c008717f94d31e2b0fb9b00d00c26846c1b2debf392b5e', 10000),
('member06', '오다인', '7277c211fade267df5a06959341f5c365f8160a8d0370f0e092a443268d9293e', 2000),
('member07', '한지우', 'c492a100b9580f15708756242fefa27ab2429d1e7e13d0b282fcbf911acd569a', 8000),
('member08', '백시윤', 'b1824c336d1b3da3f45b789e0fd69fbb39763ef61e11f47401528fe3bbb61e96', 3000),
('member09', '정윤호', 'f2bb95f79076e6324db4e05d4b21a162a21c5125d3730c6c4398f7b6c18c32ea', 15000),
('member10', '서민재', 'b6e1f3cb73b1d82f613d2108a694434c22377ba8230f6c8a3de6bdbe57584c3c', 4000);

-- 스터디 그룹
INSERT INTO DB2025Team02StudyGroups (name, leader_id, description, start_date, end_date, cert_method, deposit, status) VALUES
('게임 수학 스터디', 1, '피직스, 기초 수학을 익히자', '2025-04-01', '2025-05-15', '출석 인증', 8000, 'closed'),
('자료구조 스터디', 1, '자료구조 기본기', '2025-05-10', '2025-06-10', '기록 작성', 9000, 'ongoing'),
('네트워크 마스터', 2, 'OSI~TCP 정리', '2025-05-01', '2025-05-30', '출석 인증', 5000, 'ongoing'),
('디자인패턴 집중반', 3, 'GoF 학습', '2025-04-01', '2025-06-30', '기록 작성', 5000, 'ongoing'),
('알고리즘 입문', 2, '기초 문제풀이', '2025-05-20', '2025-07-10', '사진 인증', 7000, 'ongoing'),
('SQL 집중반', 3, 'JOIN, INDEX 등 실습', '2025-05-15', '2025-05-29', '기록 작성', 4000, 'closed'),
('안드로이드 앱개발', 3, 'compose ui 구현하기', '2025-06-05', '2025-07-19', '클론 코딩을 해보자', 4000, 'ongoing');
;

-- 스터디 멤버
INSERT INTO DB2025Team02GroupMembers (study_id, user_id, accumulated_fine, status) VALUES
(1, 1, 0, 'active'),
(1, 5, 0, 'active'),
(1, 6, 0, 'active'),
(2, 1, 0, 'active'),
(2, 6, 0, 'active'),
(2, 7, 10000, 'withdrawn'),
(3, 2, 0, 'active'),
(3, 7, 0, 'active'),
(3, 8, 0, 'active'),
(4, 3, 0, 'active'),
(4, 8, 0, 'active'),
(4, 9, 0, 'active'),
(4, 10, 0, 'active'),
(5, 2, 0, 'active'),
(5, 9, 0, 'active'),
(5, 10, 30000, 'withdrawn'),
(6, 3, 0, 'active'),
(6, 10, 14000, 'withdrawn'),
(6, 11, 0, 'active'),
(7,6,0,'active'),
(7,3,0,'active');

-- 인증 내역
-- cycle_no는 프로그램에서 계산해서 insert해주기때문에 작성하지 않았습니다.
INSERT INTO DB2025Team02DailyCerts (user_id, study_id, cert_date,  content, approval_status) VALUES
(5, 1, '2025-05-28', '클래스 정리글 링크 첨부', 'pending'),
(6, 1, '2025-05-24', '늦었지만 제출합니다.', 'rejected'),
(6, 2, '2025-05-11', '늦었지만 출석 인증합니다.', 'pending'),
(7, 2, '2025-05-26', '교재 1장 인증', 'approved'),
(2, 3, '2025-05-26', '출석 인증합니다.', 'approved'),
(7, 3, '2025-05-21', '깃허브 확인 요망.', 'approved'),
(8, 3, '2025-05-26', '날짜를 착각해서 이제 인증합니다.', 'rejected'),
(8, 4, '2025-05-22', '깃허브 확인 부탁드립니당.', 'pending'),
(9, 4, '2025-05-29', '오늘도 열심히 공부했습니다~ 레포지토리 확인 부탁드려요~', 'approved'),
(10, 4, '2025-05-22', '풀리퀘 받아주세요. main에 보내뒀음.', 'pending'),
(2, 5, '2025-05-24', '블로그 링크 남깁니다.', 'approved'),
(10, 5, '2025-05-26', '구글 드라이브에 사진 올렸어요.', 'approved'),
(3, 5, '2025-05-24', '책 돌려드렸습니다. 진도 체크해주시면 되어요.', 'approved'),
(9, 5, '2025-05-22', '다음 주 진도까지 나간 듯. week5까지 봐주세요.', 'approved'),
(5, 6, '2025-05-28', '늦었지만 출석 인정 가능한가요ㅠㅠ', 'rejected'),
(6, 6, '2025-05-28', '시험이라 바빠서 늦었습니다.', 'rejected'),
(10, 6, '2025-05-20', '사진 올렸는데 권한 확인 좀 부탁해요.', 'rejected'),
(3, 6, '2025-05-29', '출석합니다~', 'pending'),
(11, 6, '2025-05-27', '교재 인증.', 'approved');

-- 규칙
-- next_cert_date는 프로그램에서 업데이트해주기때문에 임의의 날짜를 넣었습니다.
INSERT INTO DB2025Team02Rules (study_id, cert_cycle, grace_period, fine_late, fine_absent, ptsettle_cycle, last_modified, next_cert_date) VALUES
(1, 7, 1, 1000, 2000, 7, '2025-05-30', '2025-06-02'),
(2, 7, 6, 3000, 5000, 7, '2025-05-30', '2025-06-02'),
(3,  7, 1, 2000, 4000, 7, '2025-05-30', '2025-06-02'),
(4,  9, 1, 5000, 5000, 7, '2025-05-30', '2025-06-02'),
(5,  10, 1, 5000, 10000, 7, '2025-05-30', '2025-06-02'),
(6,  11, 1, 2000, 5000, 7, '2025-05-30', '2025-06-02'),
(7, 7, 1, 1000, 2000, 7, '2025-05-30', '2025-06-02');

-- 벌금
INSERT INTO DB2025Team02Fines (user_id, study_id, reason, amount, date) VALUES
(5, 1,  '지각', 1000, '2025-05-27'),
(1, 1,  '미인증', 2000, '2025-05-29'),
(6, 1, '미인증', 1000, '2025-05-25'),
(1, 2,  '지각', 3000, '2025-05-25'),
(6, 2,  '미인증', 5000, '2025-05-27'),
(7, 2, '미인증', 5000, '2025-05-25'),
(2, 3,  '지각', 2000, '2025-05-28'),
(7, 3, '미인증', 4000, '2025-05-28'),
(3, 4, '지각', 5000, '2025-05-27'),
(8, 4, '지각', 5000, '2025-05-28'),
(9, 4, '미인증', 5000, '2025-05-28'),
(9, 5, '지각', 5000, '2025-05-27'),
(10, 5, '미인증', 10000, '2025-05-29'),
(3, 5, '미인증', 10000, '2025-05-29'),
(11, 6,  '지각', 2000, '2025-05-25'),
(10, 6,  '미인증', 5000, '2025-05-27');

-- 보증금 내역
INSERT INTO DB2025Team02Deposits (user_id, study_id, amount, deposit_date, is_refunded) VALUES
(5, 1, 8000, '2025-05-28', TRUE),
(6, 1, 8000, '2025-05-21', TRUE),
(6, 2, 9000, '2025-05-21', FALSE),
(7, 3, 5000, '2025-05-21', FALSE),
(8, 3, 5000, '2025-05-26', FALSE),
(8, 4, 5000, '2025-05-22', TRUE),
(9, 4, 5000, '2025-05-27', TRUE),
(9, 5, 7000, '2025-05-21', TRUE),
(11, 6, 4000, '2025-05-29', TRUE),
(1, 2, 9000, '2025-05-22', FALSE);








