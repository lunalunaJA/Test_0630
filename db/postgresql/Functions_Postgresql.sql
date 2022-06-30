-- 암호화 함수 확장
CREATE EXTENSION pgcrypto;

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
-- FUNCTION: public.get_banow(integer)
-- DROP FUNCTION public.get_banow(integer);
CREATE OR REPLACE FUNCTION public.get_banow(
	days integer)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  nowtime character varying = get_now();
  othertime character varying = '';
  newtime character varying = '';
  
begin

  select substring(nowtime, 12) into othertime;
  select to_char(nowtime::date + days, 'YYYY-MM-DD') into newtime;
  select (newtime || ' ' || othertime) into newtime;
   
  return newtime;

end;
$BODY$;


-- FUNCTION: public.get_classid_by_content(character, character)
-- DROP FUNCTION public.get_classid_by_content(character, character);
CREATE OR REPLACE FUNCTION public.get_classid_by_content(
	pid character,
	ptype character)
    RETURNS character
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  cid character(64) = '';
begin

  select COALESCE(CLS.classid, '') into cid
    from zapp_class CLS
	   , zapp_classobject CLSOBJ
   where CLS.classid = CLSOBJ.classid
     and CLSOBJ.cobjid = pid
	 and CLSOBJ.cobjtype = ptype
	 and CLS.types not in ('02', '03');
   
   return cid;
end;
$BODY$;

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
-- FUNCTION: public.get_classpath_upward(character)
-- DROP FUNCTION public.get_classpath_upward(character);
CREATE OR REPLACE FUNCTION public.get_classpath_upward(
	pid character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  fullpath character varying = '';
  classname character varying = '';
  len int = 0; 
  
  query_cursor CURSOR FOR 
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

begin
  
   OPEN query_cursor;
	LOOP
		FETCH query_cursor INTO classname;
		EXIT WHEN NOT FOUND;
		SELECT fullpath || ' > ' || classname INTO fullpath;
	END LOOP;
   CLOSE query_cursor;
  
  select length(fullpath) into len;
  if len > 1 then
  	select substring(fullpath, 3) into fullpath;
  end if;
  
  return fullpath;

end;
$BODY$;

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
-- FUNCTION: public.get_classpath_upward_by_content(character, character)
-- DROP FUNCTION public.get_classpath_upward_by_content(character, character);
CREATE OR REPLACE FUNCTION public.get_classpath_upward_by_content(
	pid character,
	ptype character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  fullpath character varying = '';
  classname character varying = '';
  len int = 0; 
  
  query_cursor CURSOR FOR 
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

begin
  
   OPEN query_cursor;
	LOOP
		FETCH query_cursor INTO classname;
		EXIT WHEN NOT FOUND;
		SELECT fullpath || ' > ' || classname INTO fullpath;
	END LOOP;
   CLOSE query_cursor;
  
  select length(fullpath) into len;
  if len > 1 then
  	select substring(fullpath, 3) into fullpath;
  end if;
  
  return fullpath;

end;
$BODY$;


/*
	분류 경로 조회 (폴더 정보 직접 조회)
	
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
-- DROP FUNCTION public.get_classpath_upward_direct(character, character);
CREATE OR REPLACE FUNCTION public.get_classpath_upward_direct(
	pid character,
	ptype character,
	pskip character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  fullpath character varying = '';
begin

  if pskip = 'N' then
	  select cpath into fullpath
	    from zapp_class
	   where classid = get_classid_by_content(pid, ptype);
  end if;

  return fullpath;

end;
$BODY$;



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
-- FUNCTION: public.get_classpath_upward_ex(character)
-- DROP FUNCTION public.get_classpath_upward_ex(character);
CREATE OR REPLACE FUNCTION public.get_classpath_upward_ex(
	pid character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  fullpath character varying = '';
  classname character varying = '';
  len int = 0; 
  
  query_cursor CURSOR FOR 
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
    select name from CTE 
	where classid <> pid
	order by level desc;

begin
  
   OPEN query_cursor;
	LOOP
		FETCH query_cursor INTO classname;
		EXIT WHEN NOT FOUND;
		SELECT fullpath || ' > ' || classname INTO fullpath;
	END LOOP;
   CLOSE query_cursor;
  
  select length(fullpath) into len;
  if len > 1 then
  	select substring(fullpath, 3) into fullpath;
  end if;
  
  return fullpath;

end;
$BODY$;


-- FUNCTION: public.get_fileid_by_maxversion(character)
-- DROP FUNCTION public.get_fileid_by_maxversion(character);
CREATE OR REPLACE FUNCTION public.get_fileid_by_maxversion(
	pid character)
    RETURNS character
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  hash character(64) = '';
begin

	select ufileid into hash
  	 from (select ufileid
		   	    , row_number () OVER (order by hver desc, lver desc) as rno
			 from zarch_version 
		    where mfileid = pid) LIST
	 where rno = 1;
   
   return hash;

end;
$BODY$;


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
-- FUNCTION: public.get_name_by_codeid(character)
-- DROP FUNCTION public.get_name_by_codeid(character);
CREATE OR REPLACE FUNCTION public.get_name_by_codeid(
	pid character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  names character varying = '';
begin

  select COALESCE(name, '') into names
    from zapp_code
   where codeid = pid;
   
   return names;

end;
$BODY$;

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
-- FUNCTION: public.get_name_by_codename(character varying, character varying)
-- DROP FUNCTION public.get_name_by_codename(character varying, character varying);
CREATE OR REPLACE FUNCTION public.get_name_by_codename(
	in_upcodeid character varying,
	in_codeid character varying)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  out_codename varchar(64); 

	BEGIN
		select coalesce(name, '')  into out_codename
		from zapp_code 
		where codekey = in_upcodeid
		--and companyid = 'B5D0CBE66E55FF2DFB5FEEF9AED48FECBE16C9294DE9C86A75DF4759D914500D'
		and isactive = 'Y'
		and codevalue = in_codeid
		and upid = (select codeid from zapp_code where codevalue = in_upcodeid and isactive = 'Y')
		;

        return out_codename;
	END;
$BODY$;

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
-- FUNCTION: public.get_name_by_companyid(character)
-- DROP FUNCTION public.get_name_by_companyid(character);
CREATE OR REPLACE FUNCTION public.get_name_by_companyid(
	pid character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  names character varying = '';
begin

  select COALESCE(name, '') into names
    from zapp_company
   where companyid = pid;
   
   return names;

end;
$BODY$;

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
-- FUNCTION: public.get_name_by_deptid(character)
-- DROP FUNCTION public.get_name_by_deptid(character);
CREATE OR REPLACE FUNCTION public.get_name_by_deptid(
	pid character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  names character varying = '';
begin

  select COALESCE(name, '') into names
    from zapp_dept
   where deptid = pid;
   
   return names;

end;
$BODY$;

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
-- FUNCTION: public.get_name_by_deptuserid(character)
-- DROP FUNCTION public.get_name_by_deptuserid(character);
CREATE OR REPLACE FUNCTION public.get_name_by_deptuserid(
	pdeptuserid character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  uname character varying = '';
begin

  select COALESCE(name, '') into uname
    from zapp_user users
	   , zapp_deptuser du
   where users.userid = du.userid
     and du.deptuserid = pdeptuserid;
   
   return uname;

end;
$BODY$;

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
-- FUNCTION: public.get_name_by_groupid(character)
-- DROP FUNCTION public.get_name_by_groupid(character);
CREATE OR REPLACE FUNCTION public.get_name_by_groupid(
	pid character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  names character varying = '';
begin

  select COALESCE(name, '') into names
    from zapp_group_mv gu
   where gu.groupid = pid;
   
   return names;

end;
$BODY$;

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
-- FUNCTION: public.get_name_by_groupuserid(character)
-- DROP FUNCTION public.get_name_by_groupuserid(character);
CREATE OR REPLACE FUNCTION public.get_name_by_groupuserid(
	pid character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  names character varying = '';
begin

  select COALESCE(name, '') into names
    from zapp_user users
	   , zapp_groupuser_mv gu
   where users.userid = gu.groupuserid
     and gu.groupuserid = pid;
   
   return names;

end;
$BODY$;

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
-- FUNCTION: public.get_name_by_userid(character)
-- DROP FUNCTION public.get_name_by_userid(character);
CREATE OR REPLACE FUNCTION public.get_name_by_userid(
	puserid character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  uname character varying = '';
begin

  select COALESCE(name, '') into uname
    from zapp_user
   where userid = puserid;
   
   return uname;
end;
$BODY$;

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
-- FUNCTION: public.get_now()
-- DROP FUNCTION public.get_now();
CREATE OR REPLACE FUNCTION public.get_now(
	)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  nowtime character varying = '';
begin

  select to_char(now(), 'YYYY-MM-DD HH24:MI:SS') into nowtime;
   
   return nowtime;

end;
$BODY$;

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
-- FUNCTION: public.islinked(character)
-- DROP FUNCTION public.islinked(character);
CREATE OR REPLACE FUNCTION public.islinked(
	pid character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  res character varying = 'N';
begin

  select COALESCE('Y', 'N') into res
    from dual
   where exists(select 1
    			  from zapp_linkedobject
   				 where sourceid = pid);
  
  if res is null then
  	select 'N' into res;
  end if;
  
  return res;

end;
$BODY$;

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
-- FUNCTION: public.islocked(character, character, character)
-- DROP FUNCTION public.islocked(character, character, character);
CREATE OR REPLACE FUNCTION public.islocked(
	pid character,
	ptype character,
	puid character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  res character varying = 'N';
  ress character varying = 'N';
begin

  select COALESCE('Y', 'N') into ress
    from dual
   where exists(select 1
    			  from zapp_lockedobject
   				 where lobjid = pid
			       and lobjtype = ptype);
				   
  select COALESCE('YS', 'N') into res
    from dual
   where exists(select 1
    			  from zapp_lockedobject
   				 where lobjid = pid
			       and lobjtype = ptype
			       and lockerid = puid);				   
  
  if ress = 'Y' and res = 'N' then
  	select 'Y' into res;
  end if;
  
  if ress = 'Y' and res is null then
  	select 'Y' into res;
  end if;
  if ress = 'N' and res is null then
  	select 'N' into res;
  end if;
  if ress is null and res is null then
  	select 'N' into res;
  end if;
  
  return res;

end;
$BODY$;

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
-- FUNCTION: public.isshared(character, character)
-- DROP FUNCTION public.isshared(character, character);
CREATE OR REPLACE FUNCTION public.isshared(
	pid character,
	ptype character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  res character varying = 'N';
begin

  select COALESCE('Y', 'N') into res
    from dual
   where exists(select 1
    			  from zapp_sharedobject
   				 where sobjid = pid
			       and sobjtype = ptype);
  
  if res is null then
  	select 'N' into res;
  end if;
  
  return res;

end;
$BODY$;

/*
	SHA256 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 최초값

	---------------------------------------------------
	결과값
	---------------------------------------------------
	SHA256
	
*/
-- FUNCTION: public.get_sha256(bytea)
-- DROP FUNCTION public.get_sha256(bytea);
CREATE OR REPLACE FUNCTION public.get_sha256(bytea)
    RETURNS text
    LANGUAGE 'sql'

    COST 100
    IMMUTABLE STRICT 
    
AS $BODY$
SELECT upper(encode(digest($1, 'sha256'), 'hex'))
$BODY$;

-- FUNCTION: public.get_sha256(character varying)

-- DROP FUNCTION public.get_sha256(character varying);

CREATE OR REPLACE FUNCTION public.get_sha256(
	pin character varying)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  res character varying = '';
begin

  select upper(encode(digest(pin, 'sha256'), 'hex')) into res;
   
   return res;

end;
$BODY$;

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

-- FUNCTION: public.get_random_string(integer)
-- DROP FUNCTION public.get_random_string(integer);
CREATE OR REPLACE FUNCTION public.get_random_string(
	length integer)
    RETURNS text
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  chars text[] := '{0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z}';
  result text := '';
  i integer := 0;
begin
  if length < 0 then
    raise exception 'Given length cannot be less than 0';
  end if;
  for i in 1..length loop
    result := result || chars[1+random()*(array_length(chars, 1)-1)];
  end loop;
  return result;
end;
$BODY$;

-- FUNCTION: public.get_sha1(character varying)
-- DROP FUNCTION public.get_sha1(character varying);
CREATE OR REPLACE FUNCTION public.get_sha1(
	pin character varying)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  res character varying = '';
begin

  select upper(encode(digest(pin, 'sha1'), 'hex')) into res;
   
   return res;

end;
$BODY$;

-- FUNCTION: public.get_sha1(bytea)

-- DROP FUNCTION public.get_sha1(bytea);

CREATE OR REPLACE FUNCTION public.get_sha1(bytea)
    RETURNS text
    LANGUAGE 'sql'

    COST 100
    IMMUTABLE STRICT 
    
AS $BODY$
SELECT upper(encode(digest($1, 'sha1'), 'hex'))
$BODY$;

-- FUNCTION: public.get_classpath_by_mv(character)

-- DROP FUNCTION public.get_classpath_by_mv(character);

CREATE OR REPLACE FUNCTION public.get_classpath_by_mv(
	pid character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  cpath character varying = '';
begin

  select COALESCE(classpath, '') into cpath
    from zapp_classpath_mv
   where classid = pid;
   
   return cpath;
end;
$BODY$;

-- FUNCTION: public.get_lockername(character, character)

-- DROP FUNCTION public.get_lockername(character, character);

CREATE OR REPLACE FUNCTION public.get_lockername(pid character, ptype character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  names character varying = '';
begin

  select COALESCE(get_name_by_deptuserid(lockerid), '') into names
    from zapp_lockedobject
   where lobjid = pid
     and lobjtype = ptype;
   
   return names;

end;
$BODY$;

-- FUNCTION: public.get_lockerdeptname(character, character)

-- DROP FUNCTION public.get_lockerdeptname(character, character);

CREATE OR REPLACE FUNCTION public.get_lockerdeptname(
	pid character,
	ptype character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  names character varying = '';
begin

  select d.name into names
    from zapp_dept d
       , zapp_deptuser du
       , zapp_lockedobject lo
   where d.deptid = du.deptid
     and du.deptuserid = lo.lockerid
     and lo.lobjid = pid
     and lo.lobjtype = ptype;
   
   return names;

end;
$BODY$;


-- FUNCTION: public.get_maxversion(character)
-- DROP FUNCTION public.get_maxversion(character);
CREATE OR REPLACE FUNCTION public.get_maxversion(pid character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  names character varying = '';
begin

	select VER into names
	  from (
      		select (hver  || '.' || lver) as VER
      		     , row_number() over (order by hver desc, lver desc) as rno 
              from ZARCH_VERSION ZA_VERSION
		     where mfileid = pid
		    ) ILST
	 where rno = 1; 
   
   return names;

end;
$BODY$;


-- FUNCTION: public.get_max_version_filename(character)
-- DROP FUNCTION public.get_max_version_filename(character);
CREATE OR REPLACE FUNCTION public.get_max_version_filename(pid character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  names character varying = '';
begin

	select filename into names
	  from (
      		select filename
      		     , row_number() over (order by hver desc, lver desc) as rno 
              from ZARCH_VERSION ZA_VERSION
		     where mfileid = pid
		    ) ILST
	 where rno = 1; 
   
   return names;

end;
$BODY$;

-- FUNCTION: public.get_max_version_filename_4_0(character)
-- DROP FUNCTION public.get_max_version_filename_4_0(character);
CREATE OR REPLACE FUNCTION public.get_max_version_filename_4_0(
	pid character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  names character varying = '';
begin

	select filename into names
	  from (
      		select concat(filename, '：', versionid) as filename
      		     , row_number() over (order by hver desc, lver desc) as rno 
              from ZARCH_VERSION ZA_VERSION
		     where mfileid = pid
		    ) ILST
	 where rno = 1; 
   
   return names;

end;
$BODY$;

ALTER FUNCTION public.get_max_version_filename_4_0(character)
    OWNER TO zenith_kr;


-- FUNCTION: public.get_filezise(character)
-- DROP FUNCTION public.get_filezise(character);
CREATE OR REPLACE FUNCTION public.get_filezise(pid character)
    RETURNS double precision
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  fsize double precision = 0.0;
  fileid character(64) = '';
begin

	/* 파일 아이디 조회 */
	select ufileid into fileid
	  from (
      		select ufileid
      		     , row_number() over (order by hver desc, lver desc) as rno 
              from ZARCH_VERSION ZA_VERSION
		     where mfileid = pid
		    ) ILST
	 where rno = 1; 
	 
	 select filesize into fsize
	   from zarch_ufile
	  where ufileid = fileid;
	  
	-- select (fsize / 1024.0) into fsize;
   
   return fsize;

end;
$BODY$;

-- DROP FUNCTION public.getAclObj(character);

CREATE OR REPLACE FUNCTION PUBLIC.getAclObj(puid character)
    RETURNS SETOF aclobjtype AS $BODY$ DECLARE res 
    aclobjtype; 
	
BEGIN
    FOR res IN 
	SELECT
		d.deptid as aclobjid
	  , '02' as aclobjtype	
      FROM zapp_dept d
	     , zapp_deptuser du
	 WHERE d.deptid = du.deptid
	   AND du.deptuserid = puid
    UNION ALL
	SELECT deptuserid as aclobjid
	     , '01' as aclobjtype
	  FROM zapp_deptuser du
	 WHERE du.deptuserid = puid
	UNION ALL
	SELECT g.groupid as aclobjid
	     , '03' as aclobjtype
	  FROM zapp_group g
	     , zapp_groupuser gu
	 WHERE g.groupid = gu.groupid
	   AND gu.gobjid = puid
	   AND gu.gobjtype = '01'
LOOP RETURN NEXT res; 
    END LOOP; 
END; 
$BODY$ LANGUAGE 'plpgsql'

-- DROP FUNCTION public.getAllAclObj(character);

CREATE OR REPLACE FUNCTION PUBLIC.getAllAclObj(puid character)
    RETURNS SETOF aclobjtype AS $BODY$ DECLARE res 
    aclobjtype; 
	
BEGIN
    FOR res IN 
	SELECT
		d.deptid as aclobjid
	  , '02' as aclobjtype	
      FROM zapp_dept d
	     , zapp_deptuser du
	 WHERE d.deptid = du.deptid
	   AND du.userid = puid
    UNION ALL
	SELECT deptuserid as aclobjid
	     , '01' as aclobjtype
	  FROM zapp_deptuser du
	 WHERE du.userid = puid
	UNION ALL
	SELECT g.groupid as aclobjid
	     , '03' as aclobjtype
	  FROM zapp_group g
	     , zapp_groupuser gu
	 WHERE g.groupid = gu.groupid
	   AND gu.gobjid IN (SELECT deptuserid
						   FROM zapp_deptuser du
						  WHERE du.userid = puid)
	   AND gu.gobjtype = '01'
LOOP RETURN NEXT res; 
    END LOOP; 
END
    ; $BODY$ LANGUAGE 'plpgsql'

-- Comment 조회	
-- FUNCTION: public.get_comment(character, character, character, character)
-- DROP FUNCTION public.get_comment(character, character, character, character);

CREATE OR REPLACE 
	FUNCTION PUBLIC.get_comment(pid character, ptype character, puid character, pstate character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  cmmts character varying = '';
begin

	select comments into cmmts 
	 from (
			select CW.comments
				 , row_number () OVER (order by wftime desc) as rno 
			  from zapp_contentworkflow CW
 			 where contentid = pid
			   and contenttype = ptype
			   and drafterid = puid
			   and state = pstate
			   and confirmed = 'N') LST 
	 where LST.rno = 1;
   
   return cmmts;

end;
$BODY$;  


-- FUNCTION: public.get_max_version_filename(character)
-- DROP FUNCTION public.get_max_version_filename(character);
CREATE OR REPLACE FUNCTION public.get_max_version_filename(
	pid character)
    RETURNS character varying
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
declare
  names character varying = '';
begin

	select filename into names
	  from (
      		select filename
      		     , row_number() over (order by hver desc, lver desc) as rno 
              from ZARCH_VERSION ZA_VERSION
		     where mfileid = pid
		    ) ILST
	 where rno = 1; 
   
   return names;

end;
$BODY$;


-- FUNCTION: public.hasFolder(character, character)
-- DROP FUNCTION public.hasFolder(character, character);
CREATE OR REPLACE FUNCTION public.hasFolder(
	pid character,
	ptype character)
    RETURNS character varying
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
declare
  res character varying = 'N';
begin

  select COALESCE('Y', 'N') into res
    from dual
   where exists(select 1
    			  from zapp_class
   				 where upid = pid
			       and types = ptype);
  
  if res is null then
  	select 'N' into res;
  end if;
  
  return res;

end;
$BODY$;


CREATE OR REPLACE FUNCTION public.get_name_by_classid(
	pid character,
	ptype character)
    RETURNS character varying
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  cname character varying = '';
begin

  select CLS.classid || '：' || COALESCE(CLS.name, '') into cname
    from zapp_class CLS
	   , zapp_classobject CLSOBJ
   where CLS.classid = CLSOBJ.classid
     and CLSOBJ.cobjid = pid
	 and CLSOBJ.cobjtype = ptype
	 and CLS.types not in ('02', '03');
   
   return cname;
end;
$BODY$;

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
-- DROP FUNCTION public.get_apporder(character, character, character);
CREATE OR REPLACE FUNCTION public.get_apporder(
	pid character,
	ptype character,
	pstate character)
    RETURNS smallint
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
declare
  apporder smallint = 0;
  appstatepos smallint = 0;
  appstate character varying = 'A0:A1:A2:A3:A4:A5:A6:C0:C1:C2:C3:C4:C5:C6:B1:B2:B3:D1:D2';
begin

	/* Check states */
	select POSITION(pstate in appstate) into appstatepos;
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
					  , ROW_NUMBER () OVER (order by contentid asc) as RNO
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
   	select 0 into apporder;
   end if;

   return apporder;

end;
$BODY$;

/*
	승인자 정보  조회 
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 컨텐츠 아이디
	2. 컨텐츠 유형
	3. 기안자아이디
	4. 처리 종류 (A: 완료, R: 뱐려, W: 대기)

	---------------------------------------------------
	결과값
	---------------------------------------------------
	시간 _ 승인자명 
	
*/
-- DROP FUNCTION public.get_wfinfo(character, character, character, character);
CREATE OR REPLACE FUNCTION public.get_wfinfo(
	pid character,
	ptype character,
	puid character,
	pproc character)
    RETURNS character varying
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
declare
  allinf character varying = '';
begin

    if pproc = 'A' then
		select inf into allinf 
		 from (
				select (CW.wftime || '_' || CW.wfername) as inf
					 , row_number () OVER (order by wftime desc) as rno 
				  from zapp_contentworkflow CW
				 where contentid = pid
				   and contenttype = ptype
				   and drafterid = puid
				   and status in ('A3', 'B3', 'F3', 'C3', 'D3')
				   and confirmed = 'N') LST 
		 where LST.rno = 1;
    end if;
	
    if pproc = 'R' then
		select inf into allinf 
		 from (
				select (CW.wftime || '_' || CW.wfername) as inf
					 , row_number () OVER (order by wftime desc) as rno 
				  from zapp_contentworkflow CW
				 where contentid = pid
				   and contenttype = ptype
				   and drafterid = puid
				   and status in ('C0', 'D1', 'D2')
				   and confirmed = 'N') LST 
		 where LST.rno = 1;
    end if;	

    if pproc = 'W' then
		select inf into allinf 
		 from (
				select ('' || '_' || get_name_by_deptuserid(WO.wferid)) as inf
					 , row_number () OVER (order by contentid asc) as rno 
				  from zapp_workflowobject WO
				 where contentid = pid
				   and contenttype = ptype) LST 
		 where LST.rno = 1;
    end if;
	
   return allinf;

end;
$BODY$;

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
CREATE OR REPLACE FUNCTION get_optseclevel(pclevel int, pulevel int)
    RETURNS int
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE 
    
AS $BODY$
declare
  optlevel int = 0;
begin

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

end;
$BODY$;


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
-- DROP FUNCTION public.get_value_by_codeid(char);
CREATE OR REPLACE FUNCTION public.get_value_by_codeid(
	pid char)
    RETURNS varchar
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
declare
  cval varchar = '';
begin

  select codevalue into cval
    from zapp_code
   where codeid = pid;
   
  return cval;

end;
$BODY$;

/*
	부서 사용자로 부서명 조회  
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 부서사용자아이디

	---------------------------------------------------
	결과값
	---------------------------------------------------
	부사명
	
*/

-- FUNCTION: public.get_dname_by_deptuserid(character)
-- DROP FUNCTION public.get_dname_by_deptuserid(character);
CREATE OR REPLACE FUNCTION public.get_dname_by_deptuserid(
	pid character)
    RETURNS character varying
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
declare
  names character varying = '';
begin

  select COALESCE(d.name, '') into names
    from zapp_dept d, zapp_deptuser du
   where d.deptid = du.deptid
     and deptuserid = pid;
   
   return names;

end;
$BODY$;

