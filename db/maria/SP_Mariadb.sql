/*
   디버깅
   
   ---------------------------------------------------
   인자값
   ---------------------------------------------------
   1. 디버깅여부
   2. 타이틀
   3. 메세지

   ---------------------------------------------------
   결과값
   ---------------------------------------------------
   N/A
   
*/
-- DROP PROCEDURE IF EXISTS sp_debug_msg;
CREATE OR REPLACE PROCEDURE `sp_debug_msg`(enabled BOOLEAN, title VARCHAR(1024), msg VARCHAR(1024))
BEGIN
  IF enabled THEN
    select concat(title, msg);
  END IF;
END;

/*
   해당 폴더 경로 정보 조회
   
   ---------------------------------------------------
   인자값
   ---------------------------------------------------
   1. IN 분류아이디
   2. OUT 경로정보

   ---------------------------------------------------
   결과값
   ---------------------------------------------------
   경로정보
   
*/
--DROP PROCEDURE IF EXISTS sp_get_classpath_upward;
CREATE OR REPLACE PROCEDURE `sp_get_classpath_upward`(IN pid char(64), OUT res VARCHAR(4096))
BEGIN

  DECLARE v_allpath VARCHAR(4096) DEFAULT ''; /* 전체 경로 */
  DECLARE v_name VARCHAR(200) DEFAULT '';     /* 임시 명칭 */
  DECLARE v_len INT DEFAULT 0;                  /* 결로 길이 */
  /* 커서가 가지고 있는 row를 모두 소진했는지에 대한 변수 */
  DECLARE mDone INT DEFAULT 0;
  
  /* 분유 정보 커서에 저장 */
  DECLARE curs CURSOR FOR
  WITH RECURSIVE CTE AS (
    select classid, name, upid
        , 1 AS level
     from zapp_class NODE
    where NODE.classid = pid
    union all
    select NODE.classid,
             NODE.name, NODE.upid
       , 1+level as level
     from zapp_class NODE
    INNER JOIN CTE CTEA ON NODE.classid = CTEA.upid
  )
  select name from CTE order by level desc;
   
  /* 커서가 데이터를 찾지 못하면 mDone이라는 변수에 1을 할당함 */
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET mDone = 1;
  
  /* 커서 오픈 */ 
  OPEN curs;
  
   read_loop : LOOP
   
    FETCH curs INTO v_name;

     SELECT CONCAT(v_allpath, ' > ', v_name) INTO v_allpath;
    
    /* 데이터가 있는지 확인 */
    IF mDone THEN
      LEAVE read_loop;
    END IF;
            
  END LOOP;
  
  /* 경로 정리 작업 */
  SELECT LENGTH(v_allpath) INTO v_len;
  IF v_len > 1 THEN
     SELECT SUBSTRING(v_allpath, 3) INTO v_allpath;
  END IF;
  
  /* 결과 저장 */
  SELECT v_allpath INTO res;

  /* 커서 종료 */
  CLOSE curs;

END;

/*
   해당 컨넨츠의 폴더 경로 정보 조회
   
   ---------------------------------------------------
   인자값
   ---------------------------------------------------
   1. IN 컨텐츠아이디
   1. IN 컨텐츠유형
   2. OUT 경로정보

   ---------------------------------------------------
   결과값
   ---------------------------------------------------
   경로정보
   
*/
--DROP PROCEDURE IF EXISTS sp_get_classpath_upward_by_content;
CREATE OR REPLACE PROCEDURE `sp_get_classpath_upward_by_content`(IN pid char(64), IN ptype varchar(2), OUT res VARCHAR(4096))
BEGIN

  DECLARE v_allpath VARCHAR(4096) DEFAULT ''; /* 전체 경로 */
  DECLARE v_name VARCHAR(200) DEFAULT '';     /* 임시 명칭 */
  DECLARE v_len INT DEFAULT 0;                  /* 결로 길이 */
  /* 커서가 가지고 있는 row를 모두 소진했는지에 대한 변수 */
  DECLARE mDone INT DEFAULT 0;
  
  /* 분유 정보 커서에 저장 */
  DECLARE curs CURSOR FOR
  WITH RECURSIVE CTE AS (
    select classid, name, upid
        , 1 AS level
     from zapp_class NODE
    where NODE.classid = get_classid_by_content(pid, ptype)
    union all
    select NODE.classid,
             NODE.name, NODE.upid
       , 1+level as level
     from zapp_class NODE
    INNER JOIN CTE CTEA ON NODE.classid = CTEA.upid
    )
    select name from CTE 
      where classid <> pid
     order by level desc;
   
  /* 커서가 데이터를 찾지 못하면 mDone이라는 변수에 1을 할당함 */
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET mDone = 1;
  
  /* 커서 오픈 */ 
  OPEN curs;
  
   read_loop : LOOP
   
    FETCH curs INTO v_name;

     SELECT CONCAT(v_allpath, ' > ', v_name) INTO v_allpath;
    
    /* 데이터가 있는지 확인 */
    IF mDone THEN
      LEAVE read_loop;
    END IF;
            
  END LOOP;
  
  /* 경로 정리 작업 */
  SELECT LENGTH(v_allpath) INTO v_len;
  IF v_len > 1 THEN
     SELECT SUBSTRING(v_allpath, 3) INTO v_allpath;
  END IF;
  
  /* 결과 저장 */
  SELECT v_allpath INTO res;

  /* 커서 종료 */
  CLOSE curs;

END;

/*
   통계 정보 추출
   
   ---------------------------------------------------
   인자값
   ---------------------------------------------------
   1. IN 기관아이디
   1. IN 날짜 (yyyy-mm-dd)

   ---------------------------------------------------
   결과값
   ---------------------------------------------------
   N/A
   
*/
DROP PROCEDURE IF EXISTS sp_status_day;
CREATE PROCEDURE sp_status_day`(IN pcompanyid char(64), IN pdate varchar(10))
BEGIN

	-- 기관 (등록)
	insert into zapp_status (statusid, stacompanyid, staobjid, staobjtype, stadate, staaction, statermtype, stacnt)
	select get_sha256(concat(companyid, pdate, '02A1')) as statusid
	     , pcompanyid as stacompanyid
  		 , c.companyid as staobjid
  		 , '00' as staobjtype
  		 , pdate as stadate
  		 , 'A1'	as staaction
		 , 'D'	as statermtype
  		 , coalesce((select count(*) from zapp_contentlog
			      where companyid = c.companyid
   			        and action = 'A1'
       				and logtime like concat(pdate, '%')
      			     group by companyid), 0) as stacnt
	  from zapp_company c
	 where not exists (select 1
		 	     from zapp_status
			    where stadate = pdate
			      and staobjid = pcompanyid
			      and staobjtype = '00'
			      and statermtype = 'D'
			      and staaction = 'A1')
	  and companyid = pcompanyid; 

	-- 기관 (폐기)
	insert into zapp_status (statusid, stacompanyid, staobjid, staobjtype, stadate, staaction, statermtype, stacnt)
	select get_sha256(concat(companyid, pdate, '02A1')) as statusid
	     , pcompanyid as stacompanyid
  		 , c.companyid as staobjid
  		 , '00' as staobjtype
  		 , pdate as stadate
  		 , 'F1'	as staaction
		 , 'D'	as statermtype
  		 , coalesce((select count(*) from zapp_contentlog
			      where companyid = c.companyid
   			        and action = 'F1'
       				and logtime like concat(pdate, '%')
      			     group by companyid), 0) as stacnt
	  from zapp_company c
	 where not exists (select 1
		 	     from zapp_status
			    where stadate = pdate
			      and staobjid = pcompanyid
			      and staobjtype = '00'
			      and statermtype = 'D'
			      and staaction = 'A1')
	  and companyid = pcompanyid; 
    
	-- 부서 (등록)
	insert into zapp_status (statusid, stacompanyid, staobjid, staobjtype, stadate, staaction, statermtype, stacnt)
	select get_sha256(concat(deptid, pdate, '02A1')) as statusid
	     , pcompanyid as stacompanyid
  		 , deptid as staobjid
  		 , '02' as staobjtype
  		 , pdate as stadate
  		 , 'A1'	as staaction
		 , 'D'	as statermtype
  		 , IFNULL((select count(*) from zapp_contentlog
					  where loggerdeptid = deptid
   						and action = 'A1'
       					and logtime like concat(pdate, '%')
      				 group by loggerdeptid), 0) as stacnt
	  from zapp_dept
	 where not exists (select 1
		 	     from zapp_status
			    where stadate = pdate
			      and staobjtype = '02'
			      and statermtype = 'D'
			      and staaction = 'A1'); 

	-- 부서 (폐기)
	insert into zapp_status (statusid, stacompanyid, staobjid, staobjtype, stadate, staaction, statermtype, stacnt)
	select get_sha256(concat(deptid, pdate, '02F1')) as statusid
	     , pcompanyid as stacompanyid
  		 , deptid as staobjid
  		 , '02' as staobjtype
  		 , pdate as stadate
  		 , 'F1'	as staaction
		 , 'D'	as statermtype
  		 , IFNULL((select count(*) from zapp_contentlog
					  where loggerdeptid = deptid
   						and action = 'F1'
       					and logtime like concat(pdate, '%')
      				 group by loggerdeptid), 0) as stacnt
	  from zapp_dept 
	 where not exists (select 1
		 	     from zapp_status
			    where stadate = pdate
			      and staobjtype = '02'
			      and statermtype = 'D'
			      and staaction = 'F1'); 
	 
	-- 사용자 (등록)
	insert into zapp_status (statusid, stacompanyid, staobjid, staobjtype, stadate, staaction, statermtype, stacnt)
	select get_sha256(concat(deptuserid, pdate, '01A1')) as statusid
		 , pcompanyid as stacompanyid
  		 , deptuserid as staobjid
  		 , '01' as staobjtype
  		 , pdate as stadate
  		 , 'A1'	as staaction
		 , 'D'	as statermtype
  		 , IFNULL((select count(*) from zapp_contentlog
					  where loggerid = deptid
   						and action = 'A1'
       					and logtime like concat(pdate, '%')
      				 group by loggerid), 0) as stacnt
          from zapp_deptuser
	 where not exists (select 1
		 	     from zapp_status
			    where stadate = pdate
			      and staobjtype = '01'
			      and statermtype = 'D'
			      and staaction = 'A1'); 
	 
	-- 사용자 (폐기)
	insert into zapp_status (statusid, stacompanyid, staobjid, staobjtype, stadate, staaction, statermtype, stacnt)
	select get_sha256(concat(deptuserid, pdate, '01F1')) as statusid
	     , pcompanyid as stacompanyid
  		 , deptuserid as staobjid
  		 , '01' as staobjtype
  		 , pdate as stadate
  		 , 'F1'	as staaction
		 , 'D'	as statermtype
  		 , IFNULL((select count(*) from zapp_contentlog
					  where loggerid = deptid
   						and action = 'F1'
       					and logtime like concat(pdate, '%')
      				 group by loggerid), 0) as stacnt
	 from zapp_deptuser 
	 where not exists (select 1
		 	     from zapp_status
			    where stadate = pdate
			      and staobjtype = '01'
			      and statermtype = 'D'
			      and staaction = 'F1'); 

END;



/*
   mysql_generate_series is a MySQL version of PostgreSQL's generate_series functions.
   (https://github.com/gabfl/mysql_generate_series)
   ---------------------------------------------------
   인자값
   ---------------------------------------------------
   1. 시작닶
   2. 종료값
   3. 단계

   ---------------------------------------------------
   결과값
   ---------------------------------------------------
   serial list
   
*/
--DROP PROCEDURE IF EXISTS sp_generate_series;
CREATE OR REPLACE PROCEDURE `sp_generate_series`(_start VARCHAR(20), _stop VARCHAR(20), _step VARCHAR(40))
BEGIN
    -- establish range type being produced 
    -- establish step increment
    -- call appropriate generation function

  DECLARE _typecheck VARCHAR(8);
  DECLARE _interval  VARCHAR(20);
  -- we don't know the type of these vars till runtime
  SET @start = @stop = @step = NULL;
  DROP TEMPORARY TABLE IF EXISTS series_tmp;

  -- All parameters are required
  IF LENGTH(CAST(_start AS UNSIGNED) + 0) = LENGTH(_start) THEN
    SET _typecheck = 'INTEGER';
    SET @start = CAST(_start AS UNSIGNED);
    SET @stop  = CAST(_stop AS UNSIGNED);
    SET @step  = CAST(_step AS UNSIGNED);
  ELSE
    SET _typecheck = 'DATETIME';

    IF LENGTH(_start) = 10 THEN
      SET @start = CAST(_start AS DATE);
      SET @stop  = CAST(_stop AS DATE);
    ELSE
      SET @start = CAST(_start AS DATETIME);
      SET @stop  = CAST(_stop AS DATETIME);
    END IF;

    IF _step REGEXP 'INTERVAL [0-9]+ (SECOND|MINUTE|HOUR|DAY|WEEK|MONTH|YEAR)' = 1 THEN
       SET _interval = SUBSTRING_INDEX(_step, ' ', -1); 
       SET @step = CAST(SUBSTRING_INDEX(SUBSTRING_INDEX(_step, ' ', 2), ' ', -1) AS UNSIGNED);
     ELSE
       SIGNAL SQLSTATE '45000'
       SET MESSAGE_TEXT = '\'step\' parameter should be in the form INTERVAL n SECOND|MINUTE|HOUR|DAY|WEEK|MONTH|YEAR';
    END IF;
  END IF;

  IF _typecheck = 'INTEGER' THEN
    CREATE TEMPORARY TABLE series_tmp (
      series BIGINT PRIMARY KEY
    ) ENGINE = MEMORY;

    WHILE @start <= @stop DO
      -- Insert in tmp table
      INSERT INTO series_tmp (series) VALUES (@start);
      -- Increment value by step
      SET @start = @start + @step;
    END WHILE;
  ELSE
/*    IF LENGTH(@start) = 10 THEN
      CREATE TEMPORARY TABLE series_tmp(
        series DATE PRIMARY KEY
      ) ENGINE = MEMORY;
    ELSE
      CREATE TEMPORARY TABLE series_tmp(
        series DATETIME PRIMARY KEY
      ) ENGINE = MEMORY;
    END IF; */
    
   /* ECM 4.0 에 맞게 임시 테이블 수정함 */
    CREATE TEMPORARY TABLE 
        IF NOT EXISTS zapp_date_t 
      (dt_s varchar(20) NOT NULL, 
       dt_e varchar(20) NULL, 
       wod int null) ENGINE = MEMORY;

    WHILE @start <= @stop DO
      INSERT INTO zapp_date_t (dt_s) VALUES (@start);

      CASE UPPER(_interval)
        WHEN 'SECOND' THEN
          SET @start = DATE_ADD(@start, INTERVAL @step SECOND);
        WHEN 'MINUTE' THEN
          SET @start = DATE_ADD(@start, INTERVAL @step MINUTE);
        WHEN 'HOUR' THEN
          SET @start = DATE_ADD(@start, INTERVAL @step HOUR);
        WHEN 'DAY' THEN
          SET @start = DATE_ADD(@start, INTERVAL @step DAY);
        WHEN 'WEEK' THEN
          SET @start = DATE_ADD(@start, INTERVAL @step WEEK);
        WHEN 'MONTH' THEN
          SET @start = DATE_ADD(@start, INTERVAL @step MONTH);
        WHEN 'YEAR' THEN
          SET @start = DATE_ADD(@start, INTERVAL @step YEAR);
        END CASE;
     END WHILE;
  END IF;

END;