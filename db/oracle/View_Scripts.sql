create view zapp_group_mv as
 SELECT zapp_dept.deptid AS groupid,
    zapp_dept.companyid,
    zapp_dept.name,
    zapp_dept.upid,
    '02' AS types,
    zapp_dept.code,
    zapp_dept.priority,
    zapp_dept.isactive
   FROM zapp_dept
UNION ALL
 SELECT get_sha256('전체사용자99') AS groupid,
    '' AS companyid,
    '전체사용자' AS name,
    '' AS upid,
    '99' AS types,
    '' AS code,
    1 AS priority,
    'Y' AS isactive
   FROM dual
UNION ALL
 SELECT zapp_group.groupid,
    zapp_group.companyid,
    zapp_group.name,
    zapp_group.upid,
    zapp_group.types,
    zapp_group.code,
    zapp_group.priority,
    zapp_group.isactive
   FROM zapp_group;


 create view zapp_groupuser_mv as
 SELECT get_sha256(((((deptuserid) || (deptid)) || '01'))) AS groupuserid,
    deptuserid AS gobjid,
    deptid AS groupid,
    '01' AS gobjtype,
    'N' AS editable
  from (
select d.deptid, du.deptuserid, level as lvl 
  from zapp_dept d
     , zapp_deptuser du 
 START WITH d.deptid = du.deptid and du.issupervisor = 'Y'
 CONNECT BY PRIOR d.deptid = d.upid)
 where lvl > 1
UNION ALL
 SELECT zapp_groupuser.groupuserid,
    zapp_groupuser.gobjid,
    zapp_groupuser.groupid,
    zapp_groupuser.gobjtype,
    'Y' AS editable
   FROM zapp_groupuser
UNION ALL
 SELECT get_sha256(((((list.groupid) || (list.gobjid)) || '01'))) AS groupuserid,
    list.gobjid,
    list.groupid,
    '01' AS gobjtype,
    'N' AS editable
   FROM ( SELECT zapp_deptuser.deptuserid AS gobjid,
            get_sha256('전체사용자99') AS groupid
           FROM zapp_deptuser) list;