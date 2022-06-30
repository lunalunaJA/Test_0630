/*
	해당 컨텐츠의 분류아이디 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1) 컨텐츠 아이디 (CHAR)
	2) 컨텐츠 유형 (CHAR) 
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	분류아이디
	
*/
CREATE OR REPLACE FUNCTION get_classid_by_content(pid CHAR, ptype VARCHAR) RETURN VARCHAR2 AS 
res VARCHAR2(64);
BEGIN
  
  res := '';
  
  select NVL(CLS.classid, '') into res
    from zapp_class CLS
       , zapp_classobject CLSOBJ
   where CLS.classid = CLSOBJ.classid
     and CLSOBJ.cobjid = pid
     and CLSOBJ.cobjtype = ptype
     and CLS.types not in ('02', '03');

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_classid_by_content;

/*
	해당 Master 파일 아이디 최종 버전의 Unique 파일아이디 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1) Master 파일 아이디 (CHAR)
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	Unique 파일아이디
	
*/
CREATE OR REPLACE FUNCTION get_fileid_by_maxversion(pid CHAR) RETURN CHAR AS 
res CHAR(64);
BEGIN
  
  res := '';
  
	select ufileid into res
	 from (select ufileid
		    , row_number () OVER (order by hver desc, lver desc) as rno
		 from zarch_version 
		where mfileid = pid) LIST
	 where rno = 1;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_fileid_by_maxversion;

/*
	해당 코드 아이디 명칭 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1) 코드 아이디 (CHAR)
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	코드 명칭
	
*/
CREATE OR REPLACE FUNCTION get_name_by_codeid(pid CHAR) RETURN VARCHAR2 AS 
res VARCHAR2(150);
BEGIN
  
  res := '';
  
  select NVL(name, '') into res
    from zapp_code
   where codeid = pid;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_name_by_codeid;

/*
	해당 코드 아이디 명칭 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1) 코드 아이디 (CHAR)
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	코드 명칭
	
*/
CREATE OR REPLACE FUNCTION get_name_by_codename(puid VARCHAR, pid VARCHAR) RETURN VARCHAR2 AS 
res VARCHAR2(150);
BEGIN
  
  res := '';
  
	select nvl(name, '')  into res
	  from zapp_code 
	 where codekey = puid
	   and isactive = 'Y'
	   and codevalue = pid
	   and upid = (select codeid 
	                 from zapp_code 
			where codevalue = puid 
			and isactive = 'Y')
	;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_name_by_codename;

/*
	해당 기관 아이디 명칭 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1) 기관 아이디 (CHAR)
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	기관 명칭
	
*/
CREATE OR REPLACE FUNCTION get_name_by_companyid(pid CHAR) RETURN VARCHAR2 AS 
res VARCHAR2(150);
BEGIN
  
  res := '';
  
  select nvl(name, '') into res
    from zapp_company
   where companyid = pid;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_name_by_companyid;

/*
	해당 부서 아이디 명칭 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1) 부서 아이디 (CHAR)
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	부서 명칭
	
*/
CREATE OR REPLACE FUNCTION get_name_by_deptid(pid CHAR) RETURN VARCHAR2 AS 
res VARCHAR2(150);
BEGIN
  
  res := '';
  
  select nvl(name, '') into res
    from zapp_dept
   where deptid = pid;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_name_by_deptid;

/*
	해당 부서사용자 명칭 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1) 부서사용자 아이디 (CHAR)
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	부서사용자 명칭
	
*/
CREATE OR REPLACE FUNCTION get_name_by_deptuserid(pid CHAR) RETURN VARCHAR2 AS 
res VARCHAR2(150);
BEGIN
  
  res := '';
  
  select nvl(name, '') into res
    from zapp_user users
       , zapp_deptuser du
   where users.userid = du.userid
     and du.deptuserid = pid;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_name_by_deptuserid;

/*
	해당 그룹 명칭 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1) 그룹 아이디 (CHAR)
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	그룹 명칭
	
*/
CREATE OR REPLACE FUNCTION get_name_by_groupid(pid CHAR) RETURN VARCHAR2 AS 
res VARCHAR2(150);
BEGIN
  
  res := '';
  
  select nvl(name, '') into res
    from zapp_group_mv gu
   where gu.groupid = pid;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_name_by_groupid;

/*
	해당 그룹사용자 명칭 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1) 그룹사용자 아이디 (CHAR)
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	그룹사용자 명칭
	
*/
CREATE OR REPLACE FUNCTION get_name_by_groupuserid(pid CHAR) RETURN VARCHAR2 AS 
res VARCHAR2(150);
BEGIN
  
  res := '';
  
  select nvl(name, '') into res
    from zapp_user users
       , zapp_groupuser_mv gu
   where users.userid = gu.groupuserid
     and gu.groupuserid = pid;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_name_by_groupuserid;

/*
	해당 사용자 명칭 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1) 사용자 아이디 (CHAR)
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	사용자 명칭
	
*/
CREATE OR REPLACE FUNCTION get_name_by_userid(pid CHAR) RETURN VARCHAR2 AS 
res VARCHAR2(150);
BEGIN
  
  res := '';
  
  select nvl(name, '') into res
    from zapp_user
   where userid = pid;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_name_by_userid;

/*
	현재 일시 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 날짜 형식 'YYYY-MM-DD HH24:MI:SS'

	---------------------------------------------------
	결과값
	---------------------------------------------------
	현재 일시
	
*/
create or replace FUNCTION get_now(ptype varchar2) RETURN VARCHAR2 AS 
res VARCHAR2(25);
BEGIN
  
  res := '';
  
   select TO_CHAR(SYSDATE, ptype) into res from dual;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_now;

/*
	랜덤값 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 임의값

	---------------------------------------------------
	결과값
	---------------------------------------------------
	랜덤값
	
*/
CREATE OR REPLACE FUNCTION get_random_string(ptype CHAR) RETURN VARCHAR2 AS 
res VARCHAR2(150);
BEGIN
  
  res := '';
  
  select (dbms_random.string('A', 8) || trunc(dbms_random.value(10000, 125))) into res from dual;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_random_string;

/*
	SHA256 조회 (https://github.com/CruiserX/sha256_plsql)
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 최초값

	---------------------------------------------------
	결과값
	---------------------------------------------------
	SHA256
	
*/
CREATE OR REPLACE FUNCTION get_sha256(pstr VARCHAR) RETURN VARCHAR2 AS 
res VARCHAR2(64);
BEGIN
  
  res := '';
  
  select sha256.encrypt(pstr) into res from dual;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_sha256;

/*
	공유 여부 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1) 컨텐츠 아이디 (CHAR)
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	공유 여부
	
*/
CREATE OR REPLACE FUNCTION islinked(pid VARCHAR2) RETURN CHAR AS 
res CHAR(1);
BEGIN
  
  res := '';
  
  select NVL('Y', 'N') into res
    from dual
   where exists(select 1
                  from zapp_linkedobject
                 where sourceid = pid);
  
  if res is null then
  	select 'N' into res from dual;
  end if;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END islinked;

/*
	잡금 여부 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1) 컨텐츠 아이디 (CHAR)
	2) 컨텐츠 유형 (CHAR)
	3) 잠금자 아이디 (CHAR)
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	잡금 여부
	
*/
CREATE OR REPLACE FUNCTION islocked(pid CHAR, ptype CHAR, puid CHAR) RETURN CHAR AS 
res CHAR(1);
BEGIN
  
  res := '';
  
  select NVL('Y', 'N') into res
    from dual
   where exists(select 1
    		  from zapp_lockedobject
   		  where lobjid = pid
	           and lobjtype = ptype);
				   
  select NVL('YS', res) into res
    from dual
   where exists(select 1
    		  from zapp_lockedobject
   		 where lobjid = pid
		   and lobjtype = ptype
		   and lockerid = puid);				   
  
  if res is null then
  	select 'N' into res from dual;
  end if;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END islocked;

/*
	공유 여부 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1) 컨텐츠 아이디 (CHAR)
	2) 컨텐츠 유형 (CHAR)
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	공유 여부
	
*/
CREATE OR REPLACE FUNCTION isshared(pid CHAR, ptype CHAR) RETURN CHAR AS 
res CHAR(1);
BEGIN
  
  res := '';
  
  select NVL('Y', 'N') into res
    from dual
   where exists(select 1
                  from zapp_sharedobject
                 where sobjid = pid
                   and sobjtype = ptype);
  
  if res is null then
  	select 'N' into res from dual;
  end if;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END isshared;

/*
	UUID 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	UUID
	
*/
CREATE OR REPLACE FUNCTION gen_random_uuid() RETURN VARCHAR2 AS 
res VARCHAR2(64);
BEGIN
  
  res := '';
  
  select TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')||dbms_random.string('U', 10) into res 
    from dual;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END gen_random_uuid;


/*
	해당 전후 날짜 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 계산 날짜 

	---------------------------------------------------
	결과값
	---------------------------------------------------
	날짜
	
*/
create or replace FUNCTION get_banow(days integer) RETURN VARCHAR2 AS
res VARCHAR2(25);
BEGIN
  
  res := '';

  select to_char(sysdate + days,'YYYY-MM-DD') || ' ' || get_now('HH24:MI:SS') into res from dual;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_banow;

/*
	분류 경로 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 분류아이디 

	---------------------------------------------------
	결과값
	---------------------------------------------------
	분류 경로
	
*/
create or replace FUNCTION get_classpath_upward(pid char) RETURN VARCHAR2 
IS
  
  res VARCHAR2(4000) := '';
  len integer := 0;

  cursor c_class is
  select name, lvl
  from (select NAME
             , level as lvl
        from zapp_class CLS
        START WITH CLS.classid = pid
        CONNECT BY PRIOR CLS.upid = CLS.classid)
  order by lvl asc;
  
BEGIN

  for cls in c_class
    loop
      res := res || ' > ';
    end loop;

  select length(res) into len from dual;
  if len > 1 then
  	select substr(res, 3) into res from dual;
  end if;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_classpath_upward;

/*
	분류 경로 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 컨텐츠아이디 
	2. 컨텐츠유형

	---------------------------------------------------
	결과값
	---------------------------------------------------
	분류 경로
	
*/
create or replace FUNCTION get_classpath_upward_by_content(pid char, ptype char) RETURN VARCHAR2 
IS
  
  res VARCHAR2(4000) := '';
  len integer := 0;

  cursor c_class is
  select classid, name, lvl
  from (select classid, NAME
             , level as lvl
        from zapp_class CLS
        START WITH CLS.classid = get_classid_by_content(pid, ptype)
        CONNECT BY PRIOR CLS.upid = CLS.classid)
  where classid <> pid
  order by lvl asc;
  
BEGIN

  for cls in c_class
    loop
      res := res || ' > ';
    end loop;

  select length(res) into len from dual;
  if len > 1 then
  	select substr(res, 3) into res from dual;
  end if;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_classpath_upward_by_content;

/*
	분류 경로 조회 (본인 분류 제외)
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 분류아이디 

	---------------------------------------------------
	결과값
	---------------------------------------------------
	분류 경로
	
*/
create or replace FUNCTION get_classpath_upward_ex(pid char) RETURN VARCHAR2 
IS
  
  res VARCHAR2(4000) := '';
  len integer := 0;

  cursor c_class is
  select classid, name, lvl
  from (select classid, NAME
             , level as lvl
        from zapp_class CLS
        START WITH CLS.classid = pid
        CONNECT BY PRIOR CLS.upid = CLS.classid)
  where classid <> pid
  order by lvl asc;
  
BEGIN

  for cls in c_class
    loop
      res := res || ' > ';
    end loop;

  select length(res) into len from dual;
  if len > 1 then
  	select substr(res, 3) into res from dual;
  end if;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_classpath_upward_ex;