-- View: public.zapp_group_mv

-- DROP MATERIALIZED VIEW public.zapp_group_mv;

/* Korean */
CREATE OR REPLACE VIEW zapp_group_mv
AS
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
 SELECT get_sha256(concat(zapp_company.companyid, '전체사용자98')) AS groupid,
    zapp_company.companyid,
    '전체사용자' AS name,
    zapp_company.companyid AS upid,
    '98' AS types,
    '' AS code,
    1 AS priority,
    'Y' AS isactive
   FROM zapp_company
  WHERE (zapp_company.isactive = 'Y')
UNION ALL
 SELECT get_sha256(concat(zapp_company.companyid, '전체접근그룹99')) AS groupid,
    zapp_company.companyid,
    '전체접근그룹' AS name,
    zapp_company.companyid AS upid,
    '99' AS types,
    '' AS code,
    1 AS priority,
    'Y' AS isactive
   FROM zapp_company
  WHERE (zapp_company.isactive = 'Y')
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

/* English */
-- DROP VIEW public.zapp_group_mv;
CREATE OR REPLACE VIEW zapp_group_mv
AS
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
 SELECT get_sha256(concat(zapp_company.companyid, '전체사용자98')) AS groupid,
    zapp_company.companyid,
    'All user' AS name,
    zapp_company.companyid AS upid,
    '98' AS types,
    '' AS code,
    1 AS priority,
    'Y' AS isactive
   FROM zapp_company
  WHERE (zapp_company.isactive = 'Y')
UNION ALL
 SELECT get_sha256(concat(zapp_company.companyid, '전체접근그룹99')) AS groupid,
    zapp_company.companyid,
    'Unlimited access' AS name,
    zapp_company.companyid AS upid,
    '99' AS types,
    '' AS code,
    1 AS priority,
    'Y' AS isactive
   FROM zapp_company
  WHERE (zapp_company.isactive = 'Y')
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

-- View: public.zapp_groupuser_mv

-- DROP VIEW zapp_groupuser_mv;

CREATE OR REPLACE VIEW zapp_groupuser_mv
AS
 WITH RECURSIVE depttree AS (
         SELECT 1 AS level,
            dept.deptid,
            du.deptuserid
           FROM zapp_dept dept,
            zapp_deptuser du
          WHERE ((dept.deptid = du.deptid) AND (du.issupervisor = 'Y'))
        UNION ALL
         SELECT (t.level + 1),
            r.deptid,
            t.deptuserid
           FROM zapp_dept r,
            depttree t
          WHERE (r.upid = t.deptid)
        )
 SELECT get_sha256(concat(depttree.deptuserid, depttree.deptid, '01')) AS groupuserid,
    depttree.deptuserid AS gobjid,
    depttree.deptid AS groupid,
    '01' AS gobjtype,
    0 AS gobjseq,
    'N' AS editable
   FROM depttree
  WHERE (depttree.level > 1)
UNION ALL
 SELECT zapp_groupuser.groupuserid,
    zapp_groupuser.gobjid,
    zapp_groupuser.groupid,
    zapp_groupuser.gobjtype,
    zapp_groupuser.gobjseq,
    'Y' AS editable
   FROM zapp_groupuser
UNION ALL
 SELECT get_sha256(concat(list.groupid, list.gobjid, '01')) AS groupuserid,
    list.gobjid,
    list.groupid,
    '01' AS gobjtype,
    0 AS gobjseq,
    'N' AS editable
   FROM ( SELECT du.deptuserid AS gobjid,
            get_sha256(concat(u.companyid, '전체사용자98')) AS groupid
           FROM zapp_deptuser du,
            zapp_user u
          WHERE (du.userid = u.userid)) list
UNION ALL
 SELECT get_sha256(concat(zapp_deptuser.deptid, zapp_deptuser.deptuserid, '01')) AS groupuserid,
    zapp_deptuser.deptid AS gobjid,
    zapp_deptuser.deptuserid AS groupid,
    '01' AS gobjtype,
    0 AS gobjseq,
    'N' AS editable
   FROM zapp_deptuser;

-- View: zapp_class_mv

-- DROP VIEW zapp_class_mv;

CREATE OR REPLACE VIEW zapp_class_mv
AS
select concat(BUNDLE.bundleid, '01') as uid
     , concat(CLS.classid, '：', COALESCE(CLS.name, '')) as clsname
    from zapp_class CLS
        , zapp_classobject CLSOBJ
	, zapp_bundle BUNDLE
   where CLS.classid = CLSOBJ.classid
     and CLSOBJ.cobjid = BUNDLE.bundleid
     and CLSOBJ.cobjtype = '01'
     and CLS.types not in ('02', '03')
union all
select concat(MFILE.mfileid, '02') as uid
     , concat(CLS.classid, '：', COALESCE(CLS.name, '')) as clsname
    from zapp_class CLS
        , zapp_classobject CLSOBJ
	, zapp_mfile MFILE
   where CLS.classid = CLSOBJ.classid
     and CLSOBJ.cobjid = MFILE.mfileid
     and CLSOBJ.cobjtype = '02'
     and CLS.types not in ('02', '03');

