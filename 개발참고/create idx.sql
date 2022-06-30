
--  INDEX

CREATE INDEX fk_ZAPP_USER_ZAPP_COMPANY1_idx ON zapp_user
(
    companyid ASC
)
tablespace ts_nadi4_idx;


CREATE INDEX fk_ZAPP_DEPT_ZAPP_COMPANY1_idx ON zapp_dept
(
    companyid ASC
)
tablespace ts_nadi4_idx;

CREATE INDEX fk_ZAPP_ENV_ZAPP_COMPANY1_idx ON zapp_env
(
    companyid ASC
)
tablespace ts_nadi4_idx;

CREATE INDEX fk_ZAPP_CODE_ZAPP_COMPANY1_idx ON zapp_code
(
    companyid ASC
)
tablespace ts_nadi4_idx;



CREATE INDEX fk_ZAPP_DEPTUSER_ZAPP_DEPT_idx ON zapp_deptuser
(
    deptid ASC
)
tablespace ts_nadi4_idx;

CREATE INDEX fk_ZAPP_DEPTUSER_ZAPP_USER1_idx ON zapp_deptuser
(
    userid ASC
)
tablespace ts_nadi4_idx;

CREATE INDEX fk_ZAPP_DEPTUSER_ZAPP_CODE1_idx ON zapp_deptuser
(
    positionid ASC
)
tablespace ts_nadi4_idx;

CREATE INDEX fk_ZAPP_DEPTUSER_ZAPP_CODE2_idx ON zapp_deptuser
(
    dutyid ASC
)
tablespace ts_nadi4_idx;

CREATE INDEX fk_ZAPP_DEPTUSER_ZAPP_CODE3_idx ON zapp_deptuser
(
    seclevelid ASC
)
tablespace ts_nadi4_idx;


CREATE INDEX fk_ZAPP_CLASS_ZAPP_COMPANY1_idx ON zapp_class
(
    companyid ASC
)
tablespace ts_nadi4_idx;


CREATE INDEX fk_ZAPP_HIERARCHYACL_ZAPP_HIERARCHY1_idx ON zapp_classacl
(
    classid ASC
)
tablespace ts_nadi4_idx;

CREATE INDEX fk_ZAPP_HIERARCHYDOC_ZAPP_HIERARCHY1_idx ON zapp_classobject
(
    classid ASC
)
tablespace ts_nadi4_idx;

CREATE INDEX fk_ZAPP_HIERARCHYDOC_ZAPP_DOC_E1_idx ON zapp_classobject
(
    objid ASC
)
tablespace ts_nadi4_idx;

CREATE INDEX fk_ZAPP_BUNDLEACL_ZAPP_BUNDLE1_idx ON zapp_contentacl
(
    contentid ASC
)
tablespace ts_nadi4_idx;


CREATE INDEX fk_ZAPP_SHAREDOBJECT_ZAPP_DEPTUSER1_idx ON zapp_sharedobject
(
    sharerid ASC
)
tablespace ts_nadi4_idx;

CREATE INDEX fk_ZAPP_SHAREDOBJECT_ZAPP_DEPTUSER2_idx ON zapp_sharedobject
(
    readerid ASC
)
tablespace ts_nadi4_idx;

CREATE INDEX fk_ZAPP_COMPANYTASK_ZAPP_COMPANY1_idx ON zapp_organtask
(
    companyid ASC
)
tablespace ts_nadi4_idx;

CREATE INDEX fk_ZAPP_COMPANYTASK_ZARCH_TASK1_idx ON zapp_organtask
(
    taskid ASC
)
tablespace ts_nadi4_idx;

CREATE INDEX fk_ZAPP_MFILE_ZAPP_CODE1_idx ON zapp_tempobject
(
    retentionid ASC
)
tablespace ts_nadi4_idx;

CREATE INDEX fk_ZAPP_MFILE_ZAPP_CODE2_idx ON zapp_mfile
(
    retentionid ASC
)
tablespace ts_nadi4_idx;

CREATE INDEX fk_ZARCH_UFILE_ZARCH_FORMAT1_idx ON zarch_ufile
(
    formatid ASC
)
tablespace ts_nadi4_idx;

CREATE INDEX fk_ZARCH_UFILE_ZARCH_CABINET1_idx ON zarch_ufile
(
    cabinetid ASC
)
tablespace ts_nadi4_idx;


CREATE INDEX fk_ZAPP_APPROVER_ZAPP_APROVPATH1_idx ON zapp_approver
(
    aprovpathid ASC
)
tablespace ts_nadi4_idx;

CREATE INDEX fk_ZAPP_APROVDOC_ZAPP_APPROVER1_idx ON zapp_aprovdoc
(
    pathapproverid ASC
)
tablespace ts_nadi4_idx;

CREATE INDEX fk_ZAPP_APROVDOC_ZAPP_BUNDLE1_idx ON zapp_aprovdoc
(
    bundleid ASC
)
tablespace ts_nadi4_idx;
