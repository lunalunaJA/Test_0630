CREATE OR REPLACE PROCEDURE SP_STATUS_DAY 
(
  PCOMPANYID IN CHAR 
, PDATE IN VARCHAR2 
) AS 
BEGIN
  
    -- 기관 (등록)
    insert into zapp_status (statusid, stacompanyid, staobjid, staobjtype, stadate, staaction, statermtype, stacnt)
    select get_sha256(companyid || pdate || '02A1') as statusid
         , pcompanyid as stacompanyid
         , c.companyid as staobjid
         , '00' as staobjtype
         , pdate as stadate
         , 'A1'	as staaction
       , 'D'	as statermtype
         , coalesce((select count(*) from zapp_contentlog
              where companyid = c.companyid
                  and action = 'A1'
                and logtime like (pdate || '%')
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
    select get_sha256(companyid || pdate || '02A1') as statusid
         , pcompanyid as stacompanyid
         , c.companyid as staobjid
         , '00' as staobjtype
         , pdate as stadate
         , 'F1'	as staaction
       , 'D'	as statermtype
         , coalesce((select count(*) from zapp_contentlog
              where companyid = c.companyid
                  and action = 'F1'
                and logtime like (pdate || '%')
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
	select get_sha256(deptid || pdate || '02A1') as statusid
	     , pcompanyid as stacompanyid
  		 , deptid as staobjid
  		 , '02' as staobjtype
  		 , pdate as stadate
  		 , 'A1'	as staaction
		 , 'D'	as statermtype
  		 , coalesce((select count(*) from zapp_contentlog
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
	select get_sha256(deptid || pdate || '02F1') as statusid
	     , pcompanyid as stacompanyid
  		 , deptid as staobjid
  		 , '02' as staobjtype
  		 , pdate as stadate
  		 , 'F1'	as staaction
		 , 'D'	as statermtype
  		 , coalesce((select count(*) from zapp_contentlog
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
	select get_sha256(deptuserid || pdate || '01A1') as statusid
		 , pcompanyid as stacompanyid
  		 , deptuserid as staobjid
  		 , '01' as staobjtype
  		 , pdate as stadate
  		 , 'A1'	as staaction
		 , 'D'	as statermtype
  		 , coalesce((select count(*) from zapp_contentlog
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
	select get_sha256(deptuserid || pdate || '01F1') as statusid
	     , pcompanyid as stacompanyid
  		 , deptuserid as staobjid
  		 , '01' as staobjtype
  		 , pdate as stadate
  		 , 'F1'	as staaction
		 , 'D'	as statermtype
  		 , coalesce((select count(*) from zapp_contentlog
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
            
END SP_STATUS_DAY;