create or replace procedure sp_status_day(pcompanyid character (64), pdate character varying(10))
language plpgsql
as $$
declare
-- variable declaration
begin
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
       					and logtime like pdate || '%'
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
       					and logtime like pdate || '%'
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
       					and logtime like pdate || '%'
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
       					and logtime like pdate || '%'
      				 group by loggerid), 0) as stacnt
	 from zapp_deptuser 
	 where not exists (select 1
		 	     from zapp_status
			    where stadate = pdate
			      and staobjtype = '01'
			      and statermtype = 'D'
			      and staaction = 'F1'); 
	 
end; $$