USE DB2025Team02;

-- 사용자 삽입 (로그인 및 회원가입용)
-- 더미 데이터 유저들의 비밀번호는 아이디와 동일하게 설정했습니다.
INSERT INTO DB2025Team02Users (login_id, user_name, password_hash, points) VALUES
('leader01', '김민수', 'e7e429b165665e63a2e20c59046542a45d4e6b64277bd73fc7814ec2d8f3d484', 1000),
-- 비밀번호: member01
('member01', '박소영', '33448caa4b401941646d49b496901664caf26f42e406830447c9d17e184ac940', 2000),
-- 비밀번호: member02
('member02', '이현우', '31571d6962d7e2df880ea9f271b7d4dc484591b9e87c8ea89eed290256f6e19b', 500),
-- 비밀번호: newuser01
('newuser01', '강효은', '18b1a1003f214f8ba03122eaf33922bcc3179eb3ffb877e882ec6e330791f960', 0),
-- 비밀번호: user05
('user05', '정유나', '183bbc6b5a7b5b4cfb5aa53eeb5f4d9f86f7fda1631959b1176dc1d630f1a7dd', 500),
-- 비밀번호: user06
('user06', '이하진', 'e96d7b611844a3c55dc64648ed0d71cd8f356f4e6502a9eb5a67c38a3c6e1b71', 1000),
-- 비밀번호: user07
('user07', '최서윤', 'e3387d9f7e9bc7ad7e30f0633c29a4ff50c3c0c037fd317d803f87b487bf6eb8', 800),
-- 비밀번호: user08
('user08', '강민재', '43c1109934b188d02e0a64be292842aee0d195d9f524748fe1248a6a6ed61d66', 900),
-- 비밀번호: user09
('user09', '오다인', 'cbb72f76bcf226e346bb53dd967da6a5ae0d55e6804db98e08709d50ab5f35ae', 700);

-- 스터디 그룹
INSERT INTO DB2025Team02StudyGroups (name, leader_id, description, start_date, end_date, cert_method, deposit, status) VALUES
('SpringBoot스터디', 1, 'SpringBoot 완전정복', '2025-05-01', '2025-06-10', '출석체크', 8000, 'closed'),
('자료구조스터디', 1, '자료구조 정복!', '2025-06-01', '2025-08-01', '사진 인증', 10000, 'ongoing'),
('자바 마스터 스터디', 2, '자바를 극복하자', '2025-05-20', '2025-07-30', '사진 인증', 5000, 'ongoing'),
('네트워크 마스터', 2, 'OSI 7계층부터 TCP까지', '2025-06-10', '2025-07-15', '출석체크', 6000, 'ongoing'),
('디자인패턴 집중반', 1, 'GoF 패턴 학습', '2025-06-05', '2025-07-10', '사진 인증', 7000, 'ongoing'),
('리눅스 핵심정리', 3, '쉘 스크립트와 시스템 관리', '2025-06-08', '2025-08-01', '출석체크', 5000, 'ongoing'),
('기초 알고리즘 트레이닝', 2, '기본기 탄탄히!', '2025-06-03', '2025-07-20', '사진 인증', 4000, 'ongoing'),
('자격증 스터디', 1, '정보처리기사 대비반', '2025-06-07', '2025-08-10', '출석체크', 10000, 'ongoing');

-- 스터디 멤버
INSERT INTO DB2025Team02GroupMembers (study_id, user_id, accumulated_fine, status) VALUES
(1, 1, 0, 'active'), (1, 2, 100, 'active'), (1, 3, 200, 'withdrawn'),
(2, 1, 0, 'active'), (2, 2, 300, 'active'),
(3, 1, 0, 'active'), (3, 2, 0, 'active'), (3, 3, 0, 'active'), (3, 4, 0, 'active'),
(4, 5, 0, 'active'),
(5, 6, 0, 'active'),
(6, 7, 0, 'active'),
(7, 8, 0, 'active'),
(8, 9, 0, 'active');

-- 인증 내역
INSERT INTO DB2025Team02DailyCerts (user_id, study_id, cert_date, content, approval_status) VALUES
(2, 1, '2025-05-02', '출석 인증했습니다.', 'approved'),
(3, 1, '2025-05-03', '늦었지만 제출합니다.', 'rejected'),
(2, 2, '2025-06-03', '교재 1장 인증', 'pending'),
(4, 3, '2025-06-04', '자바 프로젝트 사진 첨부: img_url.jpg', 'pending'),
(3, 3, '2025-06-04', '클래스 정리글 링크 첨부', 'approved');

-- 규칙
INSERT INTO DB2025Team02Rules (study_id, cert_deadline, cert_cycle, grace_period, fine_late, fine_absent, ptsettle_cycle, last_modified, next_cert_date) VALUES
(1, '22:00:00', 3, 1, 300, 500, 7, '2025-05-01', '2025-06-01'),
(2, '23:00:00', 2, 0, 100, 200, 7, '2025-05-28', '2025-06-03'),
(3, '23:30:00', 2, 1, 150, 250, 7, '2025-05-30', '2025-06-05');

-- 벌금
INSERT INTO DB2025Team02Fines (user_id, study_id, is_paid, reason, amount, date) VALUES
(2, 1, TRUE, '미인증', 500, '2025-05-05'),
(3, 1, FALSE, '지각', 300, '2025-05-03'),
(2, 2, FALSE, '미인증', 200, '2025-06-03');

-- 보증금 내역
INSERT INTO DB2025Team02Deposits (user_id, study_id, amount, deposit_date, is_refunded) VALUES
(1, 1, 8000, '2025-05-01', FALSE),
(2, 1, 8000, '2025-05-01', TRUE),
(3, 1, 8000, '2025-05-01', FALSE),
(1, 2, 10000, '2025-06-01', FALSE),
(2, 2, 10000, '2025-06-01', FALSE),
(4, 3, 5000, '2025-06-02', FALSE),
(3, 3, 5000, '2025-06-02', FALSE);