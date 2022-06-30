-- View: public.zapp_group_mv

-- DROP MATERIALIZED VIEW public.zapp_group_mv;

/* Korean */
CREATE MATERIALIZED VIEW public.zapp_group_mv
TABLESPACE pg_default
AS
 SELECT zapp_dept.deptid AS groupid,
    zapp_dept.companyid,
    zapp_dept.name,
    zapp_dept.upid,
    '02'::character varying AS types,
    zapp_dept.code,
    zapp_dept.priority,
    zapp_dept.isactive
   FROM zapp_dept
UNION ALL
 SELECT get_sha256(companyid || '전체사용자98'::character varying) AS groupid,
    companyid,
    '전체사용자'::character varying AS name,
    companyid AS upid,
    '98'::character varying AS types,
    ''::character varying AS code,
    1 AS priority,
    'Y'::bpchar AS isactive
   FROM zapp_company
  WHERE isactive = 'Y'
UNION ALL
 SELECT get_sha256(companyid || '전체접근그룹99'::character varying) AS groupid,
    companyid,
    '전체접근그룹'::character varying AS name,
    companyid AS upid,
    '99'::character varying AS types,
    ''::character varying AS code,
    1 AS priority,
    'Y'::bpchar AS isactive
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
   FROM zapp_group
WITH DATA;

/* English */
CREATE MATERIALIZED VIEW public.zapp_group_mv
TABLESPACE pg_default
AS
 SELECT zapp_dept.deptid AS groupid,
    zapp_dept.companyid,
    zapp_dept.name,
    zapp_dept.upid,
    '02'::character varying AS types,
    zapp_dept.code,
    zapp_dept.priority,
    zapp_dept.isactive
   FROM zapp_dept
UNION ALL
 SELECT get_sha256(companyid || '전체사용자98'::character varying) AS groupid,
    companyid,
    'All user'::character varying AS name,
    companyid AS upid,
    '98'::character varying AS types,
    ''::character varying AS code,
    1 AS priority,
    'Y'::bpchar AS isactive
   FROM zapp_company
  WHERE isactive = 'Y'
UNION ALL
 SELECT get_sha256(companyid || '전체접근그룹99'::character varying) AS groupid,
    companyid,
    'Unlimited access'::character varying AS name,
    companyid AS upid,
    '99'::character varying AS types,
    ''::character varying AS code,
    1 AS priority,
    'Y'::bpchar AS isactive
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
   FROM zapp_group
WITH DATA;


-- View: public.zapp_groupuser_mv

CREATE MATERIALIZED VIEW public.zapp_groupuser_mv
TABLESPACE pg_default
AS
 WITH RECURSIVE depttree AS (
         SELECT 1 AS level,
            dept.deptid,
            du.deptuserid
           FROM zapp_dept dept,
            zapp_deptuser du
          WHERE dept.deptid = du.deptid AND du.issupervisor = 'Y'::bpchar
        UNION ALL
         SELECT t.level + 1,
            r.deptid,
            t.deptuserid
           FROM zapp_dept r,
            depttree t
          WHERE r.upid = t.deptid
        )
 SELECT get_sha256(((depttree.deptuserid::text || depttree.deptid::text) || '01'::text)::character varying) AS groupuserid,
    depttree.deptuserid AS gobjid,
    depttree.deptid AS groupid,
    '01'::character varying AS gobjtype,
	0 as gobjseq,
    'N'::text AS editable
   FROM depttree
  WHERE depttree.level > 1
UNION ALL
 SELECT zapp_groupuser.groupuserid,
    zapp_groupuser.gobjid,
    zapp_groupuser.groupid,
    zapp_groupuser.gobjtype,
	zapp_groupuser.gobjseq,
    'Y'::text AS editable
   FROM zapp_groupuser
UNION ALL
 SELECT get_sha256(((list.groupid::text || list.gobjid::text) || '01'::text)::character varying) AS groupuserid,
    list.gobjid,
    list.groupid,
    '01'::character varying AS gobjtype,
	0 as gobjseq,
    'N'::text AS editable
   FROM ( SELECT du.deptuserid AS gobjid,
            get_sha256(u.companyid || '전체사용자98'::character varying) AS groupid
           FROM zapp_deptuser du, zapp_user u 
	  WHERE du.userid = u.userid) list
union all
 SELECT get_sha256(deptid || deptuserid || '01'::text) AS groupid,
	    deptid,
	    deptuserid,
       '01'::character varying AS gobjtype,
	   0 as gobjseq,
       'N'::text AS editable
   FROM zapp_deptuser
WITH DATA;

-- View: public.zapp_class_mv

-- DROP MATERIALIZED VIEW public.zapp_class_mv;

CREATE MATERIALIZED VIEW public.zapp_class_mv
TABLESPACE pg_default
AS
select (BUNDLE.bundleid || '01') as uid
     , (CLS.classid || '：' || COALESCE(CLS.name, '')) as clsname
    from zapp_class CLS
        , zapp_classobject CLSOBJ
	, zapp_bundle BUNDLE
   where CLS.classid = CLSOBJ.classid
     and CLSOBJ.cobjid = BUNDLE.bundleid
     and CLSOBJ.cobjtype = '01'
     and CLS.types not in ('02', '03')
union all
select (MFILE.mfileid || '02') as uid
     , (CLS.classid || '：' || COALESCE(CLS.name, '')) as clsname
    from zapp_class CLS
        , zapp_classobject CLSOBJ
	, zapp_mfile MFILE
   where CLS.classid = CLSOBJ.classid
     and CLSOBJ.cobjid = MFILE.mfileid
     and CLSOBJ.cobjtype = '02'
     and CLS.types not in ('02', '03')
WITH DATA;


