/*
  시스템 함수 대체
*/
CREATE OR REPLACE FUNCTION `SPLIT_PART`(
	str VARCHAR(255) ,
	delim VARCHAR(12) ,
	pos INT
) RETURNS VARCHAR(255) CHARSET utf8 RETURN REPLACE(
	SUBSTRING(
		SUBSTRING_INDEX(str , delim , pos) ,
		CHAR_LENGTH(
			SUBSTRING_INDEX(str , delim , pos - 1)
		) + 1
	) ,
	delim ,
	''
);


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
CREATE OR REPLACE FUNCTION `get_banow` (pdays int) RETURNS varchar(25)
BEGIN
	
  DECLARE res varchar(25);
  
  select concat(DATE_ADD(get_now(), INTERVAL pdays DAY), ' ', get_now('%T')) into res from dual;

  RETURN res;
END;

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
CREATE OR REPLACE FUNCTION `get_classid_by_content` (pid varchar(64), ptype varchar(2)) RETURNS char(64)
BEGIN
	
  DECLARE res char(64);
  
  select IFNULL(CLS.classid, '') into res
    from zapp_class CLS
	   , zapp_classobject CLSOBJ
   where CLS.classid = CLSOBJ.classid
     and CLSOBJ.cobjid = pid
	 and CLSOBJ.cobjtype = ptype
	 and CLS.types not in ('02', '03');          

  RETURN res;

END;

/*
	분류 경로 조회 (* sp_get_classpath_upward 먼저 생성 요망)
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 분류아이디 

	---------------------------------------------------
	결과값
	---------------------------------------------------
	분류 경로
	
*/
--DROP FUNCTION `get_classpath_upward`;
CREATE OR REPLACE FUNCTION `get_classpath_upward` (pid char(64)) RETURNS VARCHAR(4096)
BEGIN
	
  DECLARE path VARCHAR(4096) DEFAULT '';
  
  CALL sp_get_classpath_upward(pid, @res);
  SELECT CAST(@res as VARCHAR(4096)) INTO path;

  RETURN path;

END;


/*
	분류 경로 조회 (* sp_get_classpath_upward_by_content 먼저 생성 요망)
	
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
-- DROP FUNCTION get_classpath_upward_by_content;
CREATE OR REPLACE FUNCTION `get_classpath_upward_by_content` (pid char(64), ptype varchar(2)) RETURNS VARCHAR(4096)
BEGIN
	
  DECLARE path VARCHAR(4096) DEFAULT '';
  
  CALL sp_get_classpath_upward_by_content(pid, ptype, @res);
  SELECT CAST(@res as VARCHAR(4096)) INTO path;

  RETURN path;

END;

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
-- DROP FUNCTION get_classpath_upward_direct;
CREATE OR REPLACE FUNCTION get_classpath_upward_direct (pid char(64), ptype varchar(2), pskip char(1)) RETURNS VARCHAR(4096)
BEGIN
	
  DECLARE path VARCHAR(4096) DEFAULT '';
  
  if pskip = 'N' then
	  select cpath into path
		from zapp_class
	   where classid in (select NVL(CLS.classid, '')
				  from zapp_class CLS
				 , zapp_classobject CLSOBJ
				 where CLS.classid = CLSOBJ.classid
				   and CLSOBJ.cobjid = pid
				   and CLSOBJ.cobjtype = ptype
				   and CLS.types not in ('02', '03'));
  end if;
			   
  RETURN path;

END;


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
CREATE OR REPLACE FUNCTION `get_fileid_by_maxversion` (pid char(64)) RETURNS char(64)
BEGIN
	
  DECLARE res char(64);
  
	select ufileid into res
  	 from (select ufileid
		   	       , row_number() over (order by hver desc, lver desc) as rno
			       from zarch_version 
		         where mfileid = pid) LIST
	 where rno = 1;

  RETURN res;

END;

/*
	해당 분류 아이디 명칭 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1) 분류 아이디 (CHAR)
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	분류 명칭
	
*/
CREATE OR REPLACE FUNCTION `get_name_by_classid` (pid varchar(64), ptype varchar(2)) RETURNS varchar(150)
BEGIN
	
  DECLARE res varchar(300);
  
  select concat(CLS.classid, '：', IFNULL(CLS.name, '')) into res
    from zapp_class CLS
	   , zapp_classobject CLSOBJ
   where CLS.classid = CLSOBJ.classid
     and CLSOBJ.cobjid = pid
	 and CLSOBJ.cobjtype = ptype
	 and CLS.types not in ('02', '03');

  RETURN res;

END;

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
CREATE OR REPLACE FUNCTION `get_name_by_codeid` (pid varchar(64)) RETURNS varchar(150)
BEGIN
	
  DECLARE res varchar(150);
  
  select IFNULL(name, '') into res
    from zapp_code
   where codeid = pid;

  RETURN res;

END;

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
CREATE OR REPLACE FUNCTION `get_name_by_codename` (puid varchar(64), pid varchar(50)) RETURNS varchar(150)
BEGIN
	
  DECLARE res varchar(150);
  
  select IFNULL(name, '')  into res
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

END;

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
CREATE OR REPLACE FUNCTION `get_name_by_companyid` (pid char(64)) RETURNS varchar(150)
BEGIN
	
  DECLARE res varchar(150);
  
  select IFNULL(name, '') into res
    from zapp_company
   where companyid = pid;

  RETURN res;

END;

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
CREATE OR REPLACE FUNCTION `get_name_by_deptid` (pid char(64)) RETURNS varchar(150)
BEGIN
	
  DECLARE res varchar(150);
  
  select IFNULL(name, '') into res
    from zapp_dept
   where deptid = pid;

  RETURN res;

END;

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
CREATE OR REPLACE FUNCTION `get_name_by_deptuserid` (pid varchar(64)) RETURNS varchar(150)
BEGIN
	
  DECLARE res varchar(150);
  
  select IFNULL(name, '') into res
    from zapp_user users
       , zapp_deptuser du
   where users.userid = du.userid
     and du.deptuserid = pid;

  RETURN res;

END;

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
CREATE OR REPLACE FUNCTION `get_name_by_groupid` (pid char(64)) RETURNS varchar(150)
BEGIN
	
  DECLARE res varchar(150);
  
  select IFNULL(name, '') into res
    from zapp_group_mv gu
   where gu.groupid = pid;

  RETURN res;

END;

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
CREATE OR REPLACE FUNCTION `get_name_by_groupuserid` (pid char(64)) RETURNS varchar(150)
BEGIN
	
  DECLARE res varchar(150);
  
  select IFNULL(name, '') into res
    from zapp_user users
       , zapp_groupuser_mv gu
   where users.userid = gu.groupuserid
     and gu.groupuserid = pid;

  RETURN res;

END;

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
CREATE OR REPLACE FUNCTION `get_name_by_userid` (pid char(64)) RETURNS varchar(150)
BEGIN
	
  DECLARE res varchar(150);
  
  select IFNULL(name, '') into res
    from zapp_user
   where userid = pid;

  RETURN res;

END;

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
CREATE OR REPLACE FUNCTION `get_now` () RETURNS varchar(25)
BEGIN
	
  DECLARE res varchar(25);

  select date_format(now(), '%Y-%m-%d') into res from dual;

  RETURN res;
END;

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
CREATE OR REPLACE FUNCTION `islinked` (pid varchar(64)) RETURNS varchar(150)
BEGIN
	
  DECLARE res varchar(150);
  
  select IFNULL('Y', 'N') into res
    from dual
   where exists(select 1
                  from zapp_linkedobject
                 where sourceid = pid);

  RETURN res;

END;

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
CREATE OR REPLACE FUNCTION `islocked` (pid varchar(64), ptype varchar(2), puid varchar(64)) RETURNS char(1)
BEGIN
	
  DECLARE res char(1);
  
  select IFNULL('Y', 'N') into res
    from dual
   where exists(select 1
    		  from zapp_lockedobject
   		  where lobjid = pid
	           and lobjtype = ptype);
             
 select IFNULL('YS', res) into res
    from dual
   where exists(select 1
    		  from zapp_lockedobject
   		 where lobjid = pid
		   and lobjtype = ptype
		   and lockerid = puid);            

  RETURN res;

END;

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
CREATE OR REPLACE FUNCTION `isshared` (pid varchar(64), ptype varchar(2)) RETURNS char(1)
BEGIN
	
  DECLARE res char(1);
  
  select IFNULL('Y', 'N') into res
    from dual
   where exists(select 1
                  from zapp_sharedobject
                 where sobjid = pid
                   and sobjtype = ptype);          

  RETURN res;

END;

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
CREATE OR REPLACE FUNCTION `get_sha256` (pstr varchar(1024)) RETURNS varchar(64)
BEGIN
	
  DECLARE res varchar(64);
  
  select UPPER(SHA2(pstr ,256)) into res from dual;

  RETURN res;

END;

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
CREATE OR REPLACE FUNCTION `get_random_string` (pstr INT) RETURNS varchar(150)
BEGIN
	
  DECLARE res varchar(150);
  
  select UUID() into res from dual;

  RETURN res;

END;

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
--DROP FUNCTION IF EXISTS gen_random_uuid;
CREATE OR REPLACE FUNCTION `gen_random_uuid`() RETURNS varchar(150) CHARSET utf8
BEGIN
	
  DECLARE res varchar(150);
  
  select UUID() into res from dual;

  RETURN res;

END;

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
CREATE OR REPLACE FUNCTION `get_lockername` (pid varchar(64), ptype varchar(2)) RETURNS varchar(150)
BEGIN
	
  DECLARE res varchar(150);
  
  select IFNULL(get_name_by_deptuserid(lockerid), '') into res
    from zapp_lockedobject
   where lobjid = pid
     and lobjtype = ptype;

  RETURN res;

END;

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
CREATE OR REPLACE FUNCTION `get_lockerdeptname` (pid varchar(64), ptype varchar(2)) RETURNS varchar(150)
BEGIN
	
  DECLARE res varchar(150);
  
  select d.name into res
    from zapp_dept d
       , zapp_deptuser du
       , zapp_lockedobject lo
   where d.deptid = du.deptid
     and du.deptuserid = lo.lockerid
     and lo.lobjid = pid
     and lo.lobjtype = ptype;

  RETURN res;

END;

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
CREATE OR REPLACE FUNCTION `get_maxversion` (pid char(64)) RETURNS varchar(50)
BEGIN
	
  DECLARE res varchar(50);
  
	select VER into res
	  from (
      		select concat(hver, '.', lver) as VER
      		     , row_number() over (order by hver desc, lver desc) as rno 
              from ZARCH_VERSION ZA_VERSION
		     where mfileid = pid
		    ) ILST
	 where rno = 1; 

  RETURN res;

END;


/*
	최대 버전 파일명 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. 파일 ID
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	파일명
	
*/
CREATE OR REPLACE FUNCTION get_max_version_filename (pid char(64)) RETURNS mediumtext
BEGIN
	
  DECLARE res mediumtext;
  
	select filename into res
	  from (
      		select filename as filename
      		     , row_number() over (order by hver desc, lver desc) as rno 
              from ZARCH_VERSION ZA_VERSION
		     where mfileid = pid
		    ) ILST
	 where rno = 1; 

  RETURN res;

END;

CREATE OR REPLACE FUNCTION get_max_version_filename_4_0 (pid char(64)) RETURNS mediumtext
BEGIN
	
  DECLARE res mediumtext;
  
	select filename into res
	  from (
      		select concat(filename, '：', versionid) as filename
      		     , row_number() over (order by hver desc, lver desc) as rno 
              from ZARCH_VERSION ZA_VERSION
		     where mfileid = pid
		    ) ILST
	 where rno = 1; 

  RETURN res;

END;

/*
	항위에 폴더 존재 여부
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1. Upper ID
	2. Types
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	Y/N
	
*/
CREATE OR REPLACE FUNCTION hasFolder (pid varchar(64), ptype varchar(2)) RETURNS char(1)
BEGIN
	
  DECLARE res char(1);
  
	select IFNULL('Y', 'N') into res
    from dual
   where exists(select 1
    			  from zapp_class
   				 where upid = pid
			       and types = ptype);

  RETURN res;

END;


/*
	파일 사이즈 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1) 분류 아이디 (CHAR)
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	분류 명칭
	
*/
CREATE OR REPLACE FUNCTION get_filesize (pid char(64)) RETURNS double
BEGIN
	
	DECLARE res double;
	DECLARE fileid char(64);
  
	/* 파일 아이디 조회 */
	select ufileid into fileid
	  from (
		select ufileid
		     , row_number() over (order by hver desc, lver desc) as rno 
	         from ZARCH_VERSION ZA_VERSION
		     where mfileid = pid
		    ) ILST
	 where rno = 1; 
	 
	 select filesize into res
	   from zarch_ufile
	  where ufileid = fileid;

  RETURN res;

END;

/* 폴더 권한 체크용 함수 */
--DROP FUNCTION get_class_acl;
CREATE OR REPLACE FUNCTION get_class_acl (pid char(64), pholderid char(64), pacllist varchar(8192)) RETURNS smallint
BEGIN
	
  DECLARE res smallint DEFAULT 0;
  
  
  select IFNULL((case (select 1
                  from dual
                 where exists (select 1 
                                 from dual 
                                where LOCATE(concat('01', '.', pholderid), pacllist) = 1))
          when 1 then 1  
          else ( select sum(ifnull(checksum, 0))
                   from (select 1 as checksum
                           from dual
                          where exists ( select 1
				           from zapp_classacl
                                          where classid = pid
               				    and acls > 0
				            and LOCATE(concat(aclobjtype, '.', aclobjid), pacllist) > 0
			                )
			 union all
			 select -1 as checksum
			   from dual
			  where exists ( select 1
					   from zapp_classacl
                                          where classid = pid
					    and acls = 0
                                            and aclobjtype = '01'
					    and LOCATE(concat('01', '.', aclobjid), pacllist) > 0
					)
                        ) IL
		 )
           end), 0) into res;
  
  RETURN res;
  
END;

/* 컨텐츠 권한 체크용  */
--DROP FUNCTION get_content_acl;
CREATE OR REPLACE FUNCTION get_content_acl (pid char(64), ptype varchar(2), pholderid char(64), pacllist varchar(8192)) RETURNS smallint
BEGIN
	
  DECLARE res smallint DEFAULT 0;
  
  
  select IFNULL((case (select 1
                  from dual
                 where exists (select 1 
                                 from dual 
                                where LOCATE(concat('01', '.', pholderid), pacllist) = 1))
          when 1 then 1  
          else ( select sum(ifnull(checksum, 0))
                   from (select 1 as checksum
                           from dual
                          where exists ( select 1
					   from zapp_contentacl CONTENTACL
                                              , zapp_classobject CLSOBJ
                                          where CONTENTACL.contentid = CLSOBJ.classid
                                            and CONTENTACL.contenttype = '00'   -- Classification (Node)
                                            and CLSOBJ.cobjid = pid  
                                            and CLSOBJ.classtype in ('01','N2','N4')   -- Node
                                            and CLSOBJ.cobjtype = ptype      -- Bundle / File
                                            and CONTENTACL.acls > 0              														 
					    and LOCATE(concat(CONTENTACL.aclobjtype, '.', CONTENTACL.aclobjid), pacllist) > 0
                                         union all
                                         select 1
                                           from zapp_contentacl CONTENTACL
                                          where CONTENTACL.contentid = pid
                                            and CONTENTACL.contenttype = ptype   
                                            and CONTENTACL.acls > 0
					    and LOCATE(concat(CONTENTACL.aclobjtype, '.', CONTENTACL.aclobjid), pacllist) > 0
                                        )
			 union all
			 select -1 as checksum
			  from dual
			  where exists ( select 1
					  from zapp_contentacl CONTENTACL
                                              , zapp_classobject CLSOBJ
                                          where CONTENTACL.contentid = CLSOBJ.classid
                                           and CONTENTACL.contenttype = '00' -- Classification (Node)
                                           and CONTENTACL.acls = 0
                                           and CLSOBJ.cobjid = pid  
                                           and CLSOBJ.classtype in ('01','N2','N4')   -- Node
                                           and CLSOBJ.cobjtype = ptype        -- Bundle
					   and LOCATE(concat('01', '.', aclobjid), pacllist) > 0
                                       union all
                                       select 1
                                         from zapp_contentacl CONTENTACL
                                        where CONTENTACL.contentid = pid
                                          and CONTENTACL.contenttype = ptype   
                                          and CONTENTACL.acls = 0
				          and LOCATE(concat('01', '.', aclobjid), pacllist) > 0
                                        )
                       ) IL
	       )
               end), 0) into res;
  
  RETURN res;
  
END;


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
--DROP FUNCTION get_apporder;
CREATE OR REPLACE FUNCTION get_apporder (pid char(64), ptype varchar(2), pstate varchar(2)) RETURNS smallint
BEGIN
	
  DECLARE apporder smallint DEFAULT 0;
  DECLARE appstatepos smallint DEFAULT 0;
  DECLARE appstate varchar(100) DEFAULT 'A0:A1:A2:A3:A4:A5:A6:C0:C1:C2:C3:C4:C5:C6:B1:B2:B3:D1:D2';

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
					  , ROW_NUMBER() OVER (order by contentid asc) as RNO
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
       
END;

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
-- DROP FUNCTION get_wfinfo;
CREATE OR REPLACE FUNCTION get_wfinfo (pid char(64), ptype varchar(2), puid varchar(64), pproc char(1)) RETURNS VARCHAR(150) 
BEGIN

  DECLARE allinf varchar(150) DEFAULT '';

    if pproc = 'A' then
  		select inf into allinf 
  		 from (
  				select (CW.wftime || '_' || CW.wfername) as inf
  					 , row_number() OVER (order by wftime desc) as rno 
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
  					 , row_number() OVER (order by wftime desc) as rno 
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
  					 , row_number() OVER (order by contentid asc) as rno 
  				  from zapp_workflowobject WO
  				 where contentid = pid
  				   and contenttype = ptype) LST 
  		 where LST.rno = 1;
    end if;

    return allinf;

END;

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
CREATE OR REPLACE FUNCTION get_comment (pid char(64), ptype varchar(2), puid varchar(64),pstate varchar(2)) RETURNS varchar(500)
BEGIN

  DECLARE cmmts varchar(500);

	select comments into cmmts 
	 from (
			select CW.comments
				   , row_number() OVER (order by wftime desc) as rno 
			  from zapp_contentworkflow CW
 			 where contentid = pid
			   and contenttype = ptype
			   and drafterid = puid
			--   and status = pstate
			   and confirmed = 'N') LST 
	 where LST.rno = 1;
   
   return cmmts;

END;

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
DROP FUNCTION IF EXISTS get_optseclevel;
CREATE FUNCTION `get_optseclevel`(pclevel smallint, pulevel smallint) RETURNS smallint(6)
BEGIN

  DECLARE optlevel smallint DEFAULT 1;

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

END;

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
CREATE FUNCTION `get_value_by_codeid` (pid char(64)) RETURNS varchar(150)
BEGIN
	
  DECLARE res varchar(300);
  
  select codevalue into res
    from zapp_code
   where codeid = pid;

  RETURN res;

END;

/*
	부서사용자로 부서 명칭 조회
	
	---------------------------------------------------
	인자값
	---------------------------------------------------
	1) 부서사용자 아이디 (CHAR)
	
	---------------------------------------------------
	결과값
	---------------------------------------------------
	부서명
	
*/
CREATE OR REPLACE FUNCTION `get_dname_by_deptuserid` (pid varchar(64)) RETURNS varchar(150)
BEGIN
	
  DECLARE res varchar(150);
  
  select IFNULL(d.name, '') into res
    from zapp_dept d
       , zapp_deptuser du
   where d.deptid = du.deptid
     and du.deptuserid = pid;

  RETURN res;

END;


