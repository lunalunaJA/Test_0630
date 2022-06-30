-- 현재까지 모든 데이타 통계 정보 만들기용
select concat('CALL sp_status_day (\'', COM.companyid , '\',\'', t,  '\');')  
 from (
	select substring(createtime, 1, 10) as t, count(*) as n 
	 from zapp_bundle group by substring(createtime, 1, 10)
      ) LST,
      zapp_company COM
 order by t desc;