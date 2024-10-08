-- 스키마와 테이블 생성
CREATE DATABASE IF NOT EXISTS study_db;

USE study_db;

-- DEPT 테이블 생성
CREATE TABLE IF NOT EXISTS DEPT (
                                    DEPTNO INT PRIMARY KEY,
                                    DNAME VARCHAR(50),
    LOC VARCHAR(50)
    );

-- 초기 데이터 삽입
INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (10, 'ACCOUNTING', 'NEW YORK');
INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (20, 'RESEARCH', 'DALLAS');
INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (30, 'SALES', 'CHICAGO');
INSERT INTO DEPT (DEPTNO, DNAME, LOC) VALUES (40, 'OPERATIONS', 'BOSTON');
