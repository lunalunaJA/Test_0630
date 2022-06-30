/*
  시스템 함수 대체
*/
CREATE OR REPLACE FUNCTION split_part (string VARCHAR2, delimiter VARCHAR2, n NUMBER)
    RETURN VARCHAR2
  IS
    v_start NUMBER(5) := 1;
    v_end NUMBER(5);
  BEGIN
    -- Find the position of n-th -1 delimiter
    IF n > 1 THEN
      v_start := INSTR(string, delimiter, 1, n - 1);
 
       -- Delimiter not found
       IF v_start = 0 THEN
          RETURN NULL;
       END IF;
 
       v_start := v_start + LENGTH(delimiter);
 
    END IF;
 
    -- Find the position of n-th delimiter
    v_end := INSTR(string, delimiter, v_start, 1);
 
    -- If not found return until the end of string
    IF v_end = 0 THEN
      RETURN SUBSTR(string, v_start);
    END IF;
 
    RETURN SUBSTR(string, v_start, v_end - v_start);
  END;
  /
  
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
create or replace NONEDITIONABLE FUNCTION get_now RETURN VARCHAR2 AS 
res VARCHAR2(25);
BEGIN

  res := '';

   select TO_CHAR(SYSDATE, 'YYYY-MM-DD') into res from dual;

  RETURN res;

  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;

END get_now;
/

create or replace FUNCTION get_nowt(ptype varchar2) RETURN VARCHAR2 AS 
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
       
END get_nowt;
/

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

  select to_char(sysdate + days,'YYYY-MM-DD') || ' ' || get_nowt('HH24:MI:SS') into res from dual;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_banow;
/

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
/


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
/

/*
	분류 경로 조회 (폴더 정보에서 직접 조회)
	
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
create or replace FUNCTION get_classpath_upward_direct(pid char, ptype char, pskip char) RETURN VARCHAR2 
IS
  
  res VARCHAR2(4000) := '';
  
BEGIN

  if pskip = 'N' then
	  select cpath into res
		from zapp_class
	   where classid = get_classid_by_content(pid, ptype);
  end if;
  
  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_classpath_upward_direct;
/

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
  order by lvl desc;
  
BEGIN

  for cls in c_class
    loop
      res := res || ' > ' || cls.name;
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
/

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
/

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
create or replace NONEDITIONABLE FUNCTION get_fileid_by_maxversion(pid CHAR) RETURN CHAR AS 
res CHAR(64);
BEGIN

  res := '';

	select ufileid into res
	 from (select ufileid
		    , ROWNUM  rno
		 from zarch_version 
		where mfileid = pid
        order by hver desc, lver desc) LIST
	 where rno = 1;

  RETURN res;

  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;

END get_fileid_by_maxversion;
/

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
/

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
/

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
/

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
/

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
/

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
/

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
/

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
/


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
CREATE OR REPLACE FUNCTION GET_NAME_BY_CLASSID(pid CHAR, ptype CHAR) RETURN VARCHAR2 AS 
res VARCHAR2(150);
BEGIN
  
  res := '';
  
  select CLS.classid || '：' || COALESCE(CLS.name, '') into res
    from zapp_class CLS
       , zapp_classobject CLSOBJ
   where CLS.classid = CLSOBJ.classid
     and CLSOBJ.cobjid = pid
	 and CLSOBJ.cobjtype = ptype
	 and CLS.types not in ('02', '03');

  RETURN res;
       
END GET_NAME_BY_CLASSID;
/

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
create or replace NONEDITIONABLE FUNCTION islinked(pid VARCHAR2) RETURN CHAR AS 
res CHAR(1);
BEGIN

  res := 'N';
  
     select (case (select 'Y'
                     from dual
                    where exists(select 1
                                   from zapp_linkedobject
                                  where sourceid = pid))
             when 'Y' then 'Y'
             else 'N'
             end) into res
     from dual;  

  RETURN res;

END islinked;
/

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
create or replace NONEDITIONABLE FUNCTION islocked(pid CHAR, ptype CHAR, puid CHAR) RETURN VARCHAR2 AS 
res VARCHAR2(2);
BEGIN

  res := 'N';

     select (case (select 'Y'
                     from dual
                    where exists(select 1
                                   from zapp_lockedobject
                                  where lobjid = pid
                                    and lobjtype = ptype))
             when 'Y' then 'Y'
             else 'N'
             end) into res
     from dual;  

     select (case (select 'Y'
                     from dual
                    where exists(select 1
                                   from zapp_lockedobject
                                  where lobjid = pid
                                    and lobjtype = ptype
                                    and lockerid = puid))
             when 'Y' then 'YS'
             else res
             end) into res
     from dual;  

  RETURN res;


END islocked;
/

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
create or replace NONEDITIONABLE FUNCTION isshared(pid CHAR, ptype CHAR) RETURN CHAR AS 
res CHAR(1);
BEGIN

  res := 'N';

     select (case (select 'Y'
                     from dual
                    where exists(select 1
                                   from zapp_sharedobject
                                  where sobjid = pid
                                    and sobjtype = ptype))
             when 'Y' then 'Y'
             else 'N'
             end) into res
     from dual;  

  RETURN res;

END isshared;
/

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
/

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
CREATE OR REPLACE FUNCTION get_random_string(pdigit INT) RETURN VARCHAR2 AS 
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
/

/*
	잠금자명 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 컨텐츠아이디
	2. 컨텐츠 유형

	---------------------------------------------------
	결과값
	---------------------------------------------------
	랜덤값
	
*/
CREATE OR REPLACE FUNCTION get_lockername(pid CHAR, ptype CHAR) RETURN VARCHAR2 AS 
res VARCHAR2(150);
BEGIN
  
  res := '';
  
  select COALESCE(get_name_by_deptuserid(lockerid), '') into res
    from zapp_lockedobject
   where lobjid = pid
     and lobjtype = ptype;

  RETURN res;
  
END get_lockername;
/

/*
	잠금자 부서명 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 컨텐츠아이디
	2. 컨텐츠 유형

	---------------------------------------------------
	결과값
	---------------------------------------------------
	부서명
	
*/
CREATE OR REPLACE FUNCTION get_lockerdeptname(pid CHAR, ptype CHAR) RETURN VARCHAR2 AS 
res VARCHAR2(150);
BEGIN
  
  res := '';
  
  select d.name into res
    from zapp_dept d
       , zapp_deptuser du
       , zapp_lockedobject lo
   where d.deptid = du.deptid
     and du.deptuserid = lo.lockerid
     and lo.lobjid = pid
     and lo.lobjtype = ptype;

  RETURN res;
  
END get_lockerdeptname;
/

/*
	최대 버전 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 임의값

	---------------------------------------------------
	결과값
	---------------------------------------------------
	버전
	
*/
CREATE OR REPLACE FUNCTION get_maxversion(pid CHAR) RETURN VARCHAR2 AS 
res VARCHAR2(150);
BEGIN
  
  res := '';
  
	select VER into res
	  from (
      		select (hver  || '.' || lver) as VER
      		     , rownum as rno 
		  from ZARCH_VERSION ZA_VERSION
		 where mfileid = pid
		  order by hver desc, lver desc
		) ILST
	 where rno = 1;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_maxversion;
/

/*
	최대 버전의 파일명 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. Master 파일 아이디

	---------------------------------------------------
	결과값
	---------------------------------------------------
	파일명
	
*/
CREATE OR REPLACE FUNCTION get_max_version_filename(pid CHAR) RETURN VARCHAR2 AS 
res VARCHAR2(4000);
BEGIN
  
  res := '';
  
	select fn into res
	  from (
      		select filename as fn
      		     , rownum as rno 
		  from ZARCH_VERSION ZA_VERSION
		 where mfileid = pid
		  order by hver desc, lver desc
		) ILST
	 where rno = 1;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_max_version_filename;
/

create or replace FUNCTION get_max_version_filename_4_0(pid CHAR) RETURN VARCHAR2 AS 
res VARCHAR2(4000);
BEGIN

  res := '';

	select fn into res
	  from (
      		select (filename || '：' || versionid) as fn
      		     , rownum as rno 
		  from ZARCH_VERSION ZA_VERSION
		 where mfileid = pid
		  order by hver desc, lver desc
		) ILST
	 where rno = 1;

  RETURN res;

  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;

END get_max_version_filename_4_0;


/*
	승인 코멘트 조회 
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. Master 파일 아이디

	---------------------------------------------------
	결과값
	---------------------------------------------------
	파일명
	
*/
CREATE OR REPLACE FUNCTION get_comment(pid CHAR, ptype CHAR, puid CHAR, pstate CHAR) RETURN VARCHAR2 AS 
res VARCHAR2(4000);
BEGIN
  
  res := '';
  
	select comments into res
	  from (
			select CW.comments
				 , rownum as rno 
			  from zapp_contentworkflow CW
 			 where contentid = pid
			   and contenttype = ptype
			   and drafterid = puid
			   and status = pstate
			   and confirmed = 'N'
			 order by wftime desc
		) ILST
	 where rno = 1;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_comment;
/

/*
	파일 사이즈 조회 
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. Master 파일 아이디

	---------------------------------------------------
	결과값
	---------------------------------------------------
	파일명
	
*/
CREATE OR REPLACE FUNCTION get_filezise(pid CHAR) RETURN double AS 
res double;
BEGIN
  
  res := 0.0;
  fileid char(64) := '';
  
	/* 파일 아이디 조회 */
	select ufileid into fileid
	  from (
      		select ufileid
      		     , rowum as rno 
              from ZARCH_VERSION ZA_VERSION
		     where mfileid = pid
			 order by hver desc, lver desc
		    ) ILST
	 where rno = 1; 

	 select filesize into fsize
	   from zarch_ufile
	  where ufileid = fileid;
	  
  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_filezise;
/

/*
	하위 폴더 존재 여부 
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. Master 파일 아이디

	---------------------------------------------------
	결과값
	---------------------------------------------------
	파일명
	
*/
create or replace FUNCTION hasFolder(pid CHAR, ptype VARCHAR) RETURN char AS 
res char(1);
BEGIN
  
  res := 'N';

     select (case (select 'Y'
                     from dual
                    where exists(select 1
                                  from zapp_class
                                 where upid = pid
                                   and types = ptype))
             when 'Y' then 'Y'
             else 'N'
             end) into res
     from dual;  
 	  
  RETURN res;
  
       
END hasFolder;
/

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
CREATE OR REPLACE FUNCTION gen_random_uuid(digit INT) RETURN VARCHAR2 AS 
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
/

/*
	퍼일 사이즈 조회 (mfileid 기준 최종 버번의 파일 사이즈)
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. mfileid
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	파일사이즈
	
*/
create or replace FUNCTION get_filezise(pid CHAR) RETURN number AS 
res number;

BEGIN

  res := 0;

	/* 파일 아이디 조회 */
	 select filesize into res
	   from zarch_ufile ZA_UFILE
          , (select ufileid, rno 
              from (select ufileid
      		             , ROWNUM as rno 
                      from ZARCH_VERSION ZA_VERSION
                    where mfileid = pid
                     order by hver desc, lver desc)
		    ) ZA_VER
	  where ZA_UFILE.UFILEID = ZA_VER.ufileid
	   and ZA_VER.rno = 1;

  RETURN res;

  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;

END get_filezise;
/

/*
	승인 처리 중인 컨텐츠의 승인 단계 조회 
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 컨텐츠 아이디
	2. 컨텐츠 유형
	3. 현재 컨텐츠의 상태

	---------------------------------------------------
	결과값
	---------------------------------------------------
	순서
	
*/
create or replace FUNCTION get_apporder(pid CHAR, ptype CHAR, pstate CHAR) RETURN smallint AS 
apporder smallint;

  appstatepos smallint := 0;
  appstate varchar2(100) := 'A0:A1:A2:A3:A4:A5:A6:C0:C1:C2:C3:C4:C5:C6:B1:B2:B3:D1:D2';

BEGIN
    apporder := 0;

	/* Check states */
	select INSTR(appstate, pstate) into appstatepos from dual;
	if appstatepos is null then
		return 0;
	end if;
	if appstatepos = 0 then
		return 0;
	end if;

	select GROUPUSER.gobjseq into apporder
	  from zapp_classobject CLSOBJ
		 , zapp_class CLS
		 , zapp_groupuser GROUPUSER
		 , ( select contentid
				  , contenttype
				  , wferid
			  from (
				 select contentid
					  , contenttype
					  , wferid
					  , ROWNUM as RNO
				  from zapp_workflowobject 
				 where contentid = pid
			       and contenttype = ptype) LST
			 where rno = 1) WFOBJ
	 where CLSOBJ.classid = CLS.classid
	   and GROUPUSER.groupid = CLS.wfid 
	   and CLSOBJ.cobjid = pid
	   and CLSOBJ.cobjtype = ptype
	   and GROUPUSER.gobjid = WFOBJ.wferid;

   if apporder is null then
   	select 0 into apporder from dual;
   end if;

  RETURN apporder;

END get_apporder;
/

/*
	승인 정보 조회 
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 컨텐츠 아이디
	2. 컨텐츠 유형
	3. 기안자 아이디
	4. 처리유형 (A: 승인완료, R: 반려, W: 대기)

	---------------------------------------------------
	결과값
	---------------------------------------------------
	처리시간_처리자명
	
*/
CREATE or replace FUNCTION get_wfinfo (pid char, ptype varchar2, puid varchar2, pproc char) RETURN VARCHAR2 as  allinf VARCHAR2(150);
BEGIN

  allinf := '';

    if pproc = 'A' then
  		select inf into allinf 
  		 from (
  				select (CW.wftime || '_' || CW.wfername) as inf
  				      , ROWNUM as rno 
  				  from zapp_contentworkflow CW
  				 where contentid = pid
  				   and contenttype = ptype
  				   and drafterid = puid
  				   and status in ('A3', 'B3', 'F3', 'C3', 'D3')
  				   and confirmed = 'N'
				   order by wftime desc) LST 
  		 where LST.rno = 1;
    end if;
	
    if pproc = 'R' then
  		select inf into allinf 
  		 from (
  				select (CW.wftime || '_' || CW.wfername) as inf
  				      , ROWNUM as rno 
  				  from zapp_contentworkflow CW
  				 where contentid = pid
  				   and contenttype = ptype
  				   and drafterid = puid
  				   and status in ('C0', 'D1', 'D2')
  				   and confirmed = 'N'
				   order by wftime desc) LST 
  		 where LST.rno = 1;
    end if;	

    if pproc = 'W' then
  		select inf into allinf 
  		 from (
  				select ('' || '_' || get_name_by_deptuserid(WO.wferid)) as inf
  					 , ROWNUM as rno 
  				  from zapp_workflowobject WO
  				 where contentid = pid
  				   and contenttype = ptype
				   order by contentid asc) LST 
  		 where LST.rno = 1;
    end if;

    return allinf;

END get_wfinfo;
/

/*
	승인 코멘트 조회 
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 컨텐츠 아이디
	2. 컨텐츠 유형
	3. 기안자 아이디
	4. 상태

	---------------------------------------------------
	결과값
	---------------------------------------------------
	코멘트
	
*/
CREATE or replace FUNCTION get_comment (pid char, ptype varchar2, puid varchar2, pstate varchar2) return varchar2 as cmmts varchar(500);

BEGIN

	cmmts := '';

	select comments into cmmts 
	 from (
			select CW.comments
			    ,  ROWNUM as rno 
			  from zapp_contentworkflow CW
 			 where contentid = pid
			   and contenttype = ptype
			   and drafterid = puid
			--   and status = pstate
			   and confirmed = 'N'
			   order by wftime desc) LST 
	 where LST.rno = 1;
   
   return cmmts;

END get_comment;
/

/*
	페이징용
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 페이지번호
	2. 페이지당 건수
	3. 타입 (S: 시작, E: 종료)

	---------------------------------------------------
	결과값
	---------------------------------------------------
	페이징값
	
*/

create or replace FUNCTION get_pg(pgnum int, pnumofpg int, ptype char) RETURN INT AS
res INT;
BEGIN

  res := 1;

  IF pgnum > 0 THEN
    IF ptype = 'S' THEN
      select (pgnum - 1) * pnumofpg + 1 into res from dual; 
    ELSE
      select (pgnum * pnumofpg + 1) into res from dual; 
    END IF;
  END IF;

  RETURN res;

END get_pg;
/

/*
	컨텐츠 보안등급 및 사용자 보안등급 비교  
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 컨텐츠 보안등급
	2. 사용자 보안등급

	---------------------------------------------------
	결과값
	---------------------------------------------------
	1: Valid, 0 : Not Valid
	
*/
create or replace FUNCTION get_optseclevel(pclevel smallint, pulevel smallint) RETURN smallint AS 
optlevel smallint;
BEGIN
  
  optlevel := 0;
  
/* 컨텐츠 보안등급 미적용 */
  if pclevel = 0 then
    return 1;
  end if;

  if pclevel > 0 then
    if pclevel >= pulevel then 
      return 1;
    end if;
    if pclevel < pulevel then 
      return 0;
    end if;
  end if;
   
  return optlevel;
  
END get_optseclevel;
/

/*
	코드값 조회 
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 코드아이디

	---------------------------------------------------
	결과값
	---------------------------------------------------
	코드값
	
*/
CREATE OR REPLACE FUNCTION get_value_by_codeid(pid CHAR) RETURN VARCHAR2 AS 
res VARCHAR2(150);
BEGIN
  
  res := '';
  
  select codevalue into res
    from zapp_code
   where codeid = pid;

  RETURN res;
  
  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
       
END get_value_by_codeid;
/

/*
	부서사용자로 부서명 조회 
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 부서사용자아이디

	---------------------------------------------------
	결과값
	---------------------------------------------------
	부서명
	
*/
create or replace NONEDITIONABLE FUNCTION get_dname_by_deptuserid(pid CHAR) RETURN VARCHAR2 AS 
res VARCHAR2(150);
BEGIN

  res := '';

  select nvl(d.name, '') into res
    from zapp_dept d
       , zapp_deptuser du
   where d.deptid = du.deptid
     and du.deptuserid = pid;

  RETURN res;

  EXCEPTION
     WHEN NO_DATA_FOUND THEN
       NULL;
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;

END get_dname_by_deptuserid;
/

