CREATE TABLE zapp_accesslog
(
    logid char(64) NOT NULL,
    companyid varchar2(64) NOT NULL,
    loggerid varchar2(64) NOT NULL,
    loggername varchar2(50) NOT NULL,
    loggerdeptid varchar2(64) NOT NULL,
    loggerdeptname varchar2(150) NOT NULL,
    logtime varchar2(25) NOT NULL,
    logtype varchar2(2) NOT NULL,
    action varchar2(2) NOT NULL,
    logobjid varchar2(64) NOT NULL,
    logs text ,
    PRIMARY KEY(logid)
);

CREATE TABLE zapp_bundle
(
    bundleid char(64) NOT NULL,
    bno varchar2(50) NOT NULL,
    title varchar2(500) NOT NULL,
    creatorid char(64) NOT NULL,
    creatorname varchar2(50) NOT NULL,
    holderid char(64) NOT NULL,
    createtime varchar2(25) NOT NULL,
    updatetime varchar2(25) ,
    expiretime varchar2(25) NOT NULL,
    state varchar2(2) NOT NULL,
    discarderid varchar2(64) ,
    retentionid char(64) NOT NULL, 
    PRIMARY KEY(bundleid)
);

CREATE TABLE zapp_class
(
    classid char(64) NOT NULL,
    companyid char(64) NOT NULL,
    name varchar2(500) NOT NULL,
    upid varchar2(64) ,
    holderid varchar2(64) ,
    types varchar2(2) NOT NULL,
    priority smallint NOT NULL,
    isactive char(1) NOT NULL,
    code varchar2(64) ,
    PRIMARY KEY(classid)
);

CREATE TABLE zapp_classacl
(
    aclid char(64) NOT NULL,
    classid char(64) NOT NULL,
    aclobjid varchar2(64) NOT NULL,
    aclobjtype varchar2(2) NOT NULL,
    acls smallint NOT NULL,
    PRIMARY KEY(aclid)
);

CREATE TABLE zapp_classobject
(
    classobjid char(64) NOT NULL,
    classid char(64) NOT NULL,
    classtype varchar2(2) NOT NULL,
    cobjid varchar2(64) NOT NULL,
    cobjtype varchar2(2) NOT NULL,
    PRIMARY KEY(classobjid)
);

CREATE TABLE zapp_code
(
    codeid char(64) NOT NULL,
    companyid char(64) NOT NULL,
    name varchar2(150) NOT NULL,
    codevalue varchar2(50) NOT NULL,
    upid varchar2(64) ,
    types varchar2(2) NOT NULL,
    codekey varchar2(64) NOT NULL,
    priority smallint NOT NULL,
    isactive char(1) NOT NULL,
    PRIMARY KEY(codeid)
);

CREATE TABLE zapp_company
(
    companyid char(64) NOT NULL,
    name varchar2(20) NOT NULL,
    tel varchar2(50) ,
    address varchar2(500) ,
    code varchar2(30) NOT NULL,
    abbrname varchar2(50) NOT NULL,
    isactive char(1) NOT NULL,
    PRIMARY KEY(companyid)
);

CREATE TABLE zapp_contentacl
(
    aclid char(64) NOT NULL,
    contentid char(64) NOT NULL,
    aclobjid varchar2(64) NOT NULL,
    aclobjtype varchar2(2) NOT NULL,
    acls smallint NOT NULL,
    contenttype varchar2(2) NOT NULL DEFAULT '01',
    PRIMARY KEY(aclid)
);

CREATE TABLE zapp_contentlog
(
    logid char(64) ,
    companyid varchar2(64) ,
    loggerid varchar2(64) ,
    loggername varchar2(50) ,
    loggerdeptid varchar2(64) ,
    loggerdeptname varchar2(150) ,
    logtime varchar2(25) ,
    logtype varchar2(2) ,
    action varchar2(2) ,
    logs text ,
    logtext varchar2(500) ,
    logobjid varchar2(64), 
    PRIMARY KEY(logid)
);

CREATE TABLE zapp_dept
(
    deptid char(64) NOT NULL,
    companyid char(64) NOT NULL,
    name varchar2(300) NOT NULL,
    upid char(64) ,
    code varchar2(30) NOT NULL,
    abbrname varchar2(50) NOT NULL,
    priority smallint NOT NULL,
    isactive char(1) NOT NULL,
    PRIMARY KEY(deptid)
);

CREATE TABLE zapp_deptuser
(
    deptuserid char(64) NOT NULL,
    deptid char(64) NOT NULL,
    userid char(64) NOT NULL,
    usertype varchar2(2) NOT NULL,
    originyn char(1) NOT NULL,
    positionid varchar2(64) NOT NULL,
    dutyid varchar2(64) NOT NULL,
    seclevelid varchar2(64) NOT NULL,
    isactive char(1) NOT NULL,
    issupervisor char(1) NOT NULL DEFAULT 'N',
    PRIMARY KEY(deptuserid)
);

CREATE TABLE zapp_env
(
    envid char(64) NOT NULL,
    companyid char(64) NOT NULL,
    name varchar2(150) NOT NULL,
    setval varchar2(30) NOT NULL,
    envtype varchar2(2) NOT NULL,
    settype varchar2(2) NOT NULL,
    setopt varchar2(50) NOT NULL,
    editable char(1) NOT NULL,
    envkey varchar2(50) NOT NULL,
    isactive char(1) NOT NULL,
    PRIMARY KEY(envid)
);

CREATE TABLE zapp_group
(
    groupid char(64) NOT NULL,
    name varchar2(150) NOT NULL,
    upid varchar2(64) ,
    types varchar2(2) NOT NULL,
    isactive char(1) NOT NULL,
    companyid char(64) NOT NULL,
    code varchar2(50) ,
    priority smallint NOT NULL,
    PRIMARY KEY (groupid)
);

CREATE TABLE zapp_groupuser
(
    groupuserid char(64) NOT NULL,
    gobjid varchar2(64) NOT NULL,
    groupid varchar2(64) NOT NULL,
    gobjtype varchar2(2) NOT NULL,
    editable varchar2(20) NOT NULL DEFAULT 'N',
    PRIMARY KEY(groupuserid)
);

CREATE TABLE zapp_keywordobject
(
    kwobjid char(64) NOT NULL,
    kobjid varchar2(64) NOT NULL,
    kobjtype varchar2(2) NOT NULL,
    kwordid char(64) NOT NULL,
    PRIMARY KEY (kwobjid)
);

CREATE TABLE zapp_keywords
(
    kwordid char(64) NOT NULL,
    kword varchar2(50) NOT NULL,
    isactive char(1) NOT NULL,
    PRIMARY KEY (kwordid)
);

CREATE TABLE zapp_linkedobject
(
    linkedobjid char(64) NOT NULL,
    sourceid varchar2(64) NOT NULL,
    targetid varchar2(64) NOT NULL,
    linkerid varchar2(64) NOT NULL,
    linktime varchar2(25) NOT NULL,
    linktype varchar2(2) NOT NULL,
    PRIMARY KEY(linkedobjid)
);

CREATE TABLE zapp_lockedobject
(
    lockobjid char(64) NOT NULL,
    lobjid varchar2(64) NOT NULL,
    lockerid varchar2(64) NOT NULL,
    locktime varchar2(25) NOT NULL,
    releasetime varchar2(25) ,
    reason varchar2(300) ,
    lobjtype varchar2(2) NOT NULL DEFAULT '01',
    PRIMARY KEY(lockobjid)
);

CREATE TABLE zapp_markedobject
(
    markedobjid char(64) NOT NULL,
    mobjid varchar2(64) NOT NULL,
    mobjtype varchar2(2) NOT NULL,
    markerid varchar2(64) NOT NULL,
    marktime varchar2(25) NOT NULL,
    PRIMARY KEY (markedobjid)
);

CREATE TABLE zapp_mfile
(
    mfileid char(64) NOT NULL,
    fno varchar2(45) ,
    retentionid char(64) NOT NULL,
    expiretime varchar2(25) NOT NULL,
    holderid varchar2(64) NOT NULL,
    creatorname varchar2(100) NOT NULL DEFAULT ''::varchar2,
    discarderid varchar2(64) ,
    ext varchar2(30) ,
    dynamic01 varchar2(10) ,
    dynamic02 varchar2(1) ,
    dynamic03 varchar2(1), 
    PRIMARY KEY(mfileid)
);

CREATE TABLE zapp_organtask
(
    organtaskid char(64) NOT NULL,
    companyid char(64) NOT NULL,
    deptid char(64) ,
    taskid char(64) NOT NULL,
    tobjtype varchar2(2) NOT NULL,
    PRIMARY KEY (organtaskid)
);

CREATE TABLE zapp_sharedobject
(
    shareobjid char(64) NOT NULL,
    sobjid varchar2(64) NOT NULL,
    sobjtype varchar2(2) NOT NULL,
    sharerid varchar2(64) NOT NULL,
    readerid varchar2(64) NOT NULL,
    sharetime varchar2(25) NOT NULL,
    readertype varchar2(2) NOT NULL DEFAULT '01',
    PRIMARY KEY(shareobjid)
);

CREATE TABLE zapp_status
(
    statusid char(64) NOT NULL,
    staobjid varchar2(64) NOT NULL,
    staobjtype varchar2(64) NOT NULL,
    stacnt bigint NOT NULL,
    stadate varchar2(25) NOT NULL,
    staaction varchar2(2) NOT NULL,
    statermtype varchar2(2) NOT NULL,
    PRIMARY KEY (statusid)
);

CREATE TABLE zapp_systemlog
(
    logid char(64) ,
    companyid varchar2(64) ,
    loggerid varchar2(64) ,
    loggername varchar2(50) ,
    loggerdeptid varchar2(64) ,
    loggerdeptname varchar2(150) ,
    logtime varchar2(25) ,
    logtype varchar2(2) ,
    action varchar2(2) ,
    logs text ,
    logobjid varchar2(64) NOT NULL, 
    PRIMARY KEY(logid)
);

CREATE TABLE zapp_tmpobject
(
    tmpobjid char(64) NOT NULL,
    objid varchar2(64) NOT NULL,
    objtype varchar2(2) NOT NULL,
    title varchar2(500) ,
    holderid varchar2(64) ,
    retentionid varchar2(64) ,
    expiretime varchar2(64) ,
    tmptime varchar2(25) ,
    handlerid varchar2(64) ,
    acls varchar2(500) ,
    classes varchar2(500) ,
    files varchar2(500) ,
    states varchar2(10), 
    PRIMARY KEY(tmpobjid)
);

CREATE TABLE zapp_user
(
    userid char(64) NOT NULL,
    companyid varchar2(64) NOT NULL,
    empno varchar2(30) NOT NULL,
    loginid varchar2(50) NOT NULL,
    name varchar2(50) NOT NULL,
    passwd char(64) NOT NULL,
    passwdsalt varchar2(64) NOT NULL,
    email varchar2(100) NOT NULL,
    isactive char(1) NOT NULL,
    PRIMARY KEY(userid)
);

CREATE TABLE zapp_workflow
(
    workflowid char(64) NOT NULL,
    name varchar2(150) NOT NULL,
    wftype char(1) NOT NULL,
    isactive char(1) NOT NULL,
    PRIMARY KEY(workflowid)
);

CREATE TABLE zapp_workflower
(
    workflowerid char(64) NOT NULL,
    workflowid char(64) NOT NULL,
    userid char(64) NOT NULL,
    wfseq smallint NOT NULL,
    wfertype varchar2(2) NOT NULL,
    isactive char(1) NOT NULL,
    PRIMARY KEY(workflowerid)
);

CREATE TABLE zapp_workflowobject
(
    workflowobjid char(64) NOT NULL,
    workflowerid char(64) NOT NULL,
    contentid varchar2(64) NOT NULL,
    contenttype varchar2(2) NOT NULL,
    wfertype varchar2(2) NOT NULL,
    PRIMARY KEY(workflowobjid)
);











