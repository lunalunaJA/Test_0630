-- Database 생성 (as root)
CREATE DATABASE zenith_kr
CHARACTER SET = 'utf8'
COLLATE = 'utf8_general_ci';

-- 생성된 Database 확인
SHOW DATABASES;

-- User 생성
CREATE USER 'zenith_kr'@'%' IDENTIFIED BY 'zenith_kr';

-- 권한 부여
--GRANT ALL PRIVILEGES ON 데이터베이스.* TO '아이디'@'%';
GRANT ALL PRIVILEGES ON zenith_kr.* TO 'zenith_kr'@'%';
FLUSH PRIVILEGES;


