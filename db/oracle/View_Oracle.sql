/*
  Materialized view (refresh 필요함)
*/

create materialized view zapp_group_mv as
 SELECT zapp_dept.deptid AS groupid
      , zapp_dept.companyid
      , zapp_dept.name
      , zapp_dept.upid
      , '02' AS types
      , zapp_dept.code
      , zapp_dept.priority
      , zapp_dept.isactive
   FROM zapp_dept
UNION ALL
 SELECT get_sha256(companyid || '전체사용자98') AS groupid
      , companyid
      ,'전체사용자' AS name
      , companyid AS upid
      , '98' AS types
      , '' AS code
      , 1 AS priority
      , 'Y' AS isactive
   FROM zapp_company
  WHERE isactive = 'Y'
UNION ALL
 SELECT get_sha256(companyid || '전체접근그룹99') AS groupid
      , companyid
      , '전체접근그룹' AS name
      , companyid AS upid
      , '99' AS types
      , '' AS code
      , 1 AS priority
      , 'Y' AS isactive
   FROM zapp_company
  WHERE isactive = 'Y'
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


CREATE MATERIALIZED VIEW zapp_groupuser_mv
AS
 SELECT zapp_groupuser.groupuserid,
    zapp_groupuser.gobjid,
    zapp_groupuser.groupid,
    zapp_groupuser.gobjtype,
	zapp_groupuser.gobjseq,
    'Y' AS editable
   FROM zapp_groupuser
UNION ALL
 SELECT get_sha256(((list.groupid || list.gobjid) || '01')) AS groupuserid,
    list.gobjid,
    list.groupid,
    '01' AS gobjtype,
	0 as gobjseq,
    'N' AS editable
   FROM ( SELECT du.deptuserid AS gobjid,
            get_sha256(u.companyid || '전체사용자98') AS groupid
           FROM zapp_deptuser du, zapp_user u 
	  WHERE du.userid = u.userid) list
union all
 SELECT get_sha256(deptid || deptuserid || '01') AS groupid,
	    deptid,
	    deptuserid,
       '01' AS gobjtype,
	   0 as gobjseq,
       'N' AS editable
   FROM zapp_deptuser;


/* 
 * uid 를 cid 로 변경함 
*/
CREATE MATERIALIZED VIEW zapp_class_mv
AS
select  concat(BUNDLE.bundleid, '01') as cid
     ,  concat(concat(CLS.classid, '：'), NVL(CLS.name, '')) as clsname
    from zapp_class CLS
        , zapp_classobject CLSOBJ
	      , zapp_bundle BUNDLE
   where CLS.classid = CLSOBJ.classid
     and CLSOBJ.cobjid = BUNDLE.bundleid
     and CLSOBJ.cobjtype = '01'
     and CLS.types not in ('02', '03')
union all
select concat(MFILE.mfileid, '02') as cid
     , concat(concat(CLS.classid, '：'), NVL(CLS.name, '')) as clsname
    from zapp_class CLS
        , zapp_classobject CLSOBJ
	, zapp_mfile MFILE
   where CLS.classid = CLSOBJ.classid
     and CLSOBJ.cobjid = MFILE.mfileid
     and CLSOBJ.cobjtype = '02'
     and CLS.types not in ('02', '03');
     
    
/* 
   General View
*/
create view zapp_group_mv as
 SELECT zapp_dept.deptid AS groupid
      , zapp_dept.companyid
      , zapp_dept.name
      , zapp_dept.upid
      , '02' AS types
      , zapp_dept.code
      , zapp_dept.priority
      , zapp_dept.isactive
   FROM zapp_dept
UNION ALL
 SELECT get_sha256(companyid || '전체사용자98') AS groupid
      , companyid
      ,'전체사용자' AS name
      , companyid AS upid
      , '98' AS types
      , '' AS code
      , 1 AS priority
      , 'Y' AS isactive
   FROM zapp_company
  WHERE isactive = 'Y'
UNION ALL
 SELECT get_sha256(companyid || '전체접근그룹99') AS groupid
      , companyid
      , '전체접근그룹' AS name
      , companyid AS upid
      , '99' AS types
      , '' AS code
      , 1 AS priority
      , 'Y' AS isactive
   FROM zapp_company
  WHERE isactive = 'Y'
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

   
/* 
  General View
*/
CREATE VIEW zapp_groupuser_mv
AS
 SELECT zapp_groupuser.groupuserid,
    zapp_groupuser.gobjid,
    zapp_groupuser.groupid,
    zapp_groupuser.gobjtype,
	zapp_groupuser.gobjseq,
    'Y' AS editable
   FROM zapp_groupuser
UNION ALL
 SELECT get_sha256(((list.groupid || list.gobjid) || '01')) AS groupuserid,
    list.gobjid,
    list.groupid,
    '01' AS gobjtype,
	0 as gobjseq,
    'N' AS editable
   FROM ( SELECT du.deptuserid AS gobjid,
            get_sha256(u.companyid || '전체사용자98') AS groupid
           FROM zapp_deptuser du, zapp_user u 
	  WHERE du.userid = u.userid) list
union all
 SELECT get_sha256(deptid || deptuserid || '01') AS groupid,
	    deptid,
	    deptuserid,
       '01' AS gobjtype,
	   0 as gobjseq,
       'N' AS editable
   FROM zapp_deptuser;


/* 
 * uid 를 cid 로 변경함 
*/
CREATE VIEW zapp_class_mv
AS
select  concat(BUNDLE.bundleid, '01') as cid
     ,  concat(concat(CLS.classid, '：'), NVL(CLS.name, '')) as clsname
    from zapp_class CLS
        , zapp_classobject CLSOBJ
	      , zapp_bundle BUNDLE
   where CLS.classid = CLSOBJ.classid
     and CLSOBJ.cobjid = BUNDLE.bundleid
     and CLSOBJ.cobjtype = '01'
     and CLS.types not in ('02', '03')
union all
select concat(MFILE.mfileid, '02') as cid
     , concat(concat(CLS.classid, '：'), NVL(CLS.name, '')) as clsname
    from zapp_class CLS
        , zapp_classobject CLSOBJ
	, zapp_mfile MFILE
   where CLS.classid = CLSOBJ.classid
     and CLSOBJ.cobjid = MFILE.mfileid
     and CLSOBJ.cobjtype = '02'
     and CLS.types not in ('02', '03');
     