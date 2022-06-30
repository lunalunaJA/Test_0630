CREATE TABLE zapp_accesslog (
	logid varchar(64) NOT NULL,
	companyid varchar(64) NOT NULL,
	logobjid varchar(64) NULL,
	loggerid varchar(64) NOT NULL,
	loggername varchar(50) NOT NULL,
	loggerdeptid varchar(64) NOT NULL,
	loggerdeptname varchar(150) NOT NULL,
	logtime varchar(25) NOT NULL,
	logtype varchar(2) NOT NULL,
	action varchar(2) NOT NULL,
	logs varchar(1000) NULL,
	logip varchar(20) NULL,
	CONSTRAINT pk_accesslog PRIMARY KEY (logid)
);
CREATE INDEX idx_accesslog_01 ON zapp_accesslog (companyid);
CREATE INDEX idx_accesslog_02 ON zapp_accesslog (logobjid);

CREATE TABLE zapp_additorybundle (
	bundleid varchar(64) NOT NULL,
	dynamic01 varchar(64) NULL,
	dynamic02 varchar(1) NULL,
	dynamic03 varchar(1) NULL,
	dynamic04 varchar(1) NULL,
	dynamic05 varchar(1) NULL,
	dynamic06 varchar(1) NULL,
	dynamic07 varchar(1) NULL,
	dynamic08 varchar(1) NULL,
	dynamic09 varchar(1) NULL,
	dynamic10 varchar(1) NULL,
	drafter varchar(150) NULL,
	summary varchar(150) NULL,
	CONSTRAINT pk_additorybundle PRIMARY KEY (bundleid)
);


CREATE TABLE zapp_additoryclass (
	classid varchar(64) NOT NULL,
	dynamic01 varchar(64) NULL,
	dynamic02 varchar(1) NULL,
	dynamic03 varchar(1) NULL,
	dynamic04 varchar(1) NULL,
	dynamic05 varchar(1) NULL,
	dynamic06 varchar(1) NULL,
	dynamic07 varchar(1) NULL,
	dynamic08 varchar(1) NULL,
	dynamic09 varchar(1) NULL,
	dynamic10 varchar(1) NULL,
	CONSTRAINT pk_additoryclass PRIMARY KEY (classid)
);


CREATE TABLE zapp_apm (
	apmid varchar(64) NOT NULL,
	apm varchar(1000) NOT NULL,
	apmtype varchar(2) NOT NULL,
	apmtime varchar(20) NOT NULL,
	CONSTRAINT pk_apm PRIMARY KEY (apmid)
);

CREATE TABLE zapp_bundle (
	bundleid varchar(64) NOT NULL,
	bno varchar(50) NULL,
	title varchar(500) NOT NULL,
	creatorid varchar(64) NOT NULL,
	creatorname varchar(50) NOT NULL,
	holderid varchar(64) NOT NULL,
	createtime varchar(25) NOT NULL,
	updatetime varchar(25) NULL,
	expiretime varchar(25) NOT NULL,
	discarderid varchar(64) NULL,
	retentionid varchar(64) NULL,
	state varchar(2) NOT NULL,
	btype varchar(2) DEFAULT '01' NULL,
	seclevel smallint DEFAULT 0 NOT NULL,
	CONSTRAINT pk_bundle PRIMARY KEY (bundleid)
);


CREATE TABLE zapp_classacl (
	aclid varchar(64) NOT NULL,
	classid varchar(64) NOT NULL,
	aclobjid varchar(64) NOT NULL,
	aclobjtype varchar(2) NOT NULL,
	acls number NOT NULL,
	CONSTRAINT pk_classacl PRIMARY KEY (aclid)
);
CREATE INDEX idx_classacl_01 ON zapp_classacl (classid, aclobjid, aclobjtype);


CREATE TABLE zapp_classobject (
	classobjid varchar(64) NOT NULL,
	classid varchar(64) NOT NULL,
	classtype varchar(2) NOT NULL,
	cobjid varchar(64) NOT NULL,
	cobjtype varchar(2) NOT NULL,
	CONSTRAINT pk_classobject PRIMARY KEY (classobjid)
);
CREATE INDEX idx_classobject_01 ON zapp_classobject (classid, classtype);


CREATE TABLE zapp_company (
	companyid varchar(64) NOT NULL,
	name varchar(20) NOT NULL,
	tel varchar(50) NULL,
	address varchar(500) NULL,
	code varchar(30) NOT NULL,
	abbrname varchar(50) NOT NULL,
	isactive varchar(1) NOT NULL,
	CONSTRAINT pk_company PRIMARY KEY (companyid)
);


CREATE TABLE zapp_contentacl (
	aclid varchar(64) NOT NULL,
	contentid varchar(64) NOT NULL,
	aclobjid varchar(64) NOT NULL,
	aclobjtype varchar(2) NOT NULL,
	acls number NOT NULL,
	contenttype varchar(2)DEFAULT '01'  NOT NULL,
	CONSTRAINT pk_contentacl PRIMARY KEY (aclid)
);
CREATE INDEX idx_contentacl_01 ON zapp_contentacl (contentid, aclobjid, aclobjtype);


CREATE TABLE zapp_contentlog (
	logid varchar(64) NOT NULL,
	companyid varchar(64) NULL,
	logobjid varchar(64) NULL,
	loggerid varchar(64) NULL,
	loggername varchar(50) NULL,
	loggerdeptid varchar(64) NULL,
	loggerdeptname varchar(150) NULL,
	logtime varchar(25) NULL,
	logtype varchar(2) NULL,
	action varchar(2) NULL,
	logs varchar(1000) NULL,
	logtext varchar(500) NULL,
	CONSTRAINT pk_contentlog PRIMARY KEY (logid)
);
CREATE INDEX idx_contentlog_01 ON zapp_contentlog (companyid, logobjid);


CREATE TABLE zapp_contentworkflow (
	cwfid varchar(64) NOT NULL,
	contentid varchar(64) NOT NULL,
	contenttype varchar(2) NOT NULL,
	drafterid varchar(64) NOT NULL,
	draftername varchar(100) NOT NULL,
	wferid varchar(64) NOT NULL,
	wfername varchar(100) NOT NULL,
	wftime varchar(25) NOT NULL,
	comments varchar(500) NULL,
	status varchar(2) NOT NULL,
	confirmed varchar(1) DEFAULT 'N' NULL,
	CONSTRAINT zapp_contentworkflow_pkey PRIMARY KEY (cwfid)
);

CREATE TABLE zapp_cyclelog (
	cycleid varchar(64) NOT NULL,
	companyid varchar(64) NOT NULL,
	cycletime varchar(25) NOT NULL,
	cycletype varchar(2) NOT NULL,
	cyclelogs varchar(1000) NULL,
	CONSTRAINT pk_cyclelog PRIMARY KEY (cycleid)
);
CREATE INDEX idx_cyclelog_01 ON zapp_cyclelog (companyid, cycletype);


CREATE TABLE zapp_deptuser (
	deptuserid varchar(64) NOT NULL,
	deptid varchar(64) NOT NULL,
	userid varchar(64) NOT NULL,
	usertype varchar(2) NOT NULL, -- 01:General User, 02:Dept. Manager, 03:Company Manager, 04:System Manager
	originyn varchar(1) NOT NULL,
	positionid varchar(64) NOT NULL,
	dutyid varchar(64) NOT NULL,
	seclevelid varchar(64) NOT NULL,
	isactive varchar(1) NOT NULL,
	issupervisor varchar(1) DEFAULT 'N' NOT NULL,
	CONSTRAINT pk_deptuser PRIMARY KEY (deptuserid)
);
CREATE INDEX idx_deptuser_01 ON zapp_deptuser (userid);


-- Column comments

COMMENT ON COLUMN zapp_deptuser.usertype IS '01:General User, 02:Dept. Manager, 03:Company Manager, 04:System Manager';


CREATE TABLE zapp_group (
	groupid varchar(64) NOT NULL,
	companyid varchar(64) NOT NULL,
	name varchar(150) NOT NULL,
	upid varchar(64) NULL,
	types varchar(2) NOT NULL,
	isactive varchar(1) NOT NULL,
	code varchar(50) NULL,
	priority number NOT NULL,
	CONSTRAINT pk_group PRIMARY KEY (groupid)
);
CREATE INDEX idx_group_01 ON zapp_group (companyid, upid, types);

CREATE TABLE zapp_groupuser (
	groupuserid varchar(64) NOT NULL,
	groupid varchar(64) NOT NULL,
	gobjid varchar(64) NOT NULL,
	gobjtype varchar(2) NOT NULL, -- 01:User, 02:Department, 03:Group
	editable varchar(20) NULL,
	gobjseq number NULL,
	CONSTRAINT pk_groupuser PRIMARY KEY (groupuserid)
);
CREATE INDEX idx_groupuser_01 ON zapp_groupuser (groupid);


-- Column comments

COMMENT ON COLUMN zapp_groupuser.gobjtype IS '01:User, 02:Department, 03:Group';


CREATE TABLE zapp_keywords (
	kwordid varchar(64) NOT NULL,
	kword varchar(50) NOT NULL,
	isactive varchar(1) NOT NULL,
	CONSTRAINT pk_keywords PRIMARY KEY (kwordid)
);


CREATE TABLE zapp_linkedobject (
	linkedobjid varchar(64) NOT NULL,
	sourceid varchar(64) NOT NULL,
	targetid varchar(64) NOT NULL,
	linkerid varchar(64) NOT NULL,
	linktime varchar(25) NOT NULL,
	linktype varchar(2) NOT NULL,
	CONSTRAINT pk_linkedobject PRIMARY KEY (linkedobjid)
);
CREATE INDEX idx_linkedobject_01 ON zapp_linkedobject (sourceid);
CREATE INDEX idx_linkedobject_02 ON zapp_linkedobject (linkerid);


CREATE TABLE zapp_markedobject (
	markedobjid varchar(64) NOT NULL,
	mobjid varchar(64) NOT NULL,
	mobjtype varchar(2) NOT NULL,
	markerid varchar(64) NOT NULL,
	marktime varchar(25) NOT NULL,
	CONSTRAINT pk_markedobject PRIMARY KEY (markedobjid)
);
CREATE INDEX idx_markedobject_01 ON zapp_markedobject (markerid);
CREATE INDEX idx_markedobject_02 ON zapp_markedobject (mobjid, mobjtype);


CREATE TABLE zapp_status (
	statusid varchar(64) NOT NULL,
	stacompanyid varchar(64) NOT NULL,
	staobjid varchar(64) NOT NULL,
	staobjtype varchar(64) NOT NULL,
	stacnt number NOT NULL,
	stadate varchar(25) NOT NULL,
	staaction varchar(2) NOT NULL,
	statermtype varchar(2) NOT NULL,
	CONSTRAINT pk_status PRIMARY KEY (statusid)
);
CREATE INDEX IDX_ZAPPSTATUS_01 ON zapp_status (stacompanyid, staobjtype, stadate);
CREATE INDEX idx_status_01 ON zapp_status (stacompanyid, staobjid, staobjtype);


CREATE TABLE zapp_systemlog (
	logid varchar(64) NOT NULL,
	companyid varchar(64) NULL,
	logobjid varchar(64) NULL,
	loggerid varchar(64) NULL,
	loggername varchar(50) NULL,
	loggerdeptid varchar(64) NULL,
	loggerdeptname varchar(150) NULL,
	logtime varchar(25) NULL,
	logtype varchar(2) NULL,
	action varchar(2) NULL,
	logs varchar(1000) NULL,
	CONSTRAINT pk_systemlog PRIMARY KEY (logid)
);
CREATE INDEX idx_systemlog_01 ON zapp_systemlog (companyid, logobjid);


CREATE TABLE zapp_tmpobject (
	tmpobjid varchar(64) NOT NULL,
	tobjid varchar(64) NOT NULL,
	tobjtype varchar(2) NOT NULL,
	title varchar(500) NULL,
	holderid varchar(64) NULL,
	retentionid varchar(64) NULL,
	expiretime varchar(64) NULL,
	tmptime varchar(25) NULL,
	handlerid varchar(64) NULL,
	acls varchar(500) NULL,
	classes varchar(500) NULL,
	files varchar(500) NULL,
	states varchar(10) NULL,
	keywords varchar(500) NULL,
	taskid varchar(64) NULL,
	addinfo varchar(500) NULL,
	CONSTRAINT pk_tmpobject PRIMARY KEY (tmpobjid)
);
CREATE INDEX idx_tmpobject_01 ON zapp_tmpobject (tobjid, tobjtype);


CREATE TABLE zapp_workflow (
	workflowid varchar(64) NOT NULL,
	name varchar(150) NOT NULL,
	wftype varchar(1) NOT NULL,
	isactive varchar(1) NOT NULL,
	CONSTRAINT pk_workflow PRIMARY KEY (workflowid)
);


CREATE TABLE zapp_workflower (
	workflowerid varchar(64) NOT NULL,
	workflowid varchar(64) NOT NULL,
	userid varchar(64) NOT NULL,
	wfseq number NOT NULL,
	wfertype varchar(2) NOT NULL,
	isactive varchar(1) NOT NULL,
	CONSTRAINT pk_workflower PRIMARY KEY (workflowerid)
);
CREATE INDEX idx_workflower_01 ON zapp_workflower (workflowid);


CREATE TABLE zapp_workflowobject (
	wfobjid varchar(64) NOT NULL,
	wferid varchar(64) NOT NULL,
	contentid varchar(64) NOT NULL,
	contenttype varchar(2) NOT NULL
);
CREATE INDEX idx_workflowobject_01 ON zapp_workflowobject (contentid, contenttype);



CREATE TABLE zarch_admin (
	adminid varchar(50) NOT NULL,
	name varchar(100) NOT NULL,
	passwd varchar(64) NOT NULL,
	salt varchar(64) NOT NULL,
	CONSTRAINT pk_admin PRIMARY KEY (adminid)
);


CREATE TABLE zarch_cabinet (
	cabinetid varchar(64) NOT NULL,
	name varchar(100) NOT NULL,
	descpt varchar(100) NULL,
	maxcapacity number NOT NULL,
	mountpath varchar(1000) NOT NULL,
	seq number NOT NULL,
	state varchar(1) NOT NULL, -- 1:사용대기, 2:사용중, 3:사용완료
	CONSTRAINT pk_cabinet PRIMARY KEY (cabinetid)
);


-- Column comments

COMMENT ON COLUMN zarch_cabinet.state IS '1:사용대기, 2:사용중, 3:사용완료';


CREATE TABLE zarch_day (
	day varchar(2) NOT NULL
);

CREATE TABLE zarch_filelog (
	filelogid varchar(64) NOT NULL,
	logs varchar(1000) NOT NULL,
	logtime varchar(25) NOT NULL,
	loggerid varchar(64) NOT NULL,
	loggername varchar(100) NOT NULL,
	logtype varchar(2) NOT NULL,
	CONSTRAINT pk_filelog PRIMARY KEY (filelogid)
);


CREATE TABLE zarch_format (
	formatid varchar(64) NOT NULL,
	name varchar(100) NOT NULL,
	descpt varchar(100) NULL,
	code varchar(30) NULL,
	mxsize numeric NOT NULL,
	ext varchar(15) NOT NULL,
	icon varchar(50) NULL,
	CONSTRAINT pk_format PRIMARY KEY (formatid)
);



CREATE TABLE zarch_mfile (
	mfileid varchar(64) NOT NULL,
	linkid varchar(64) NOT NULL,
	filename varchar(1000) NOT NULL,
	seq number NOT NULL,
	creator varchar(64) NOT NULL,
	createtime varchar(25) NOT NULL,
	updatetime varchar(25) NULL,
	state varchar(2) NOT NULL, -- 00:정상, 01:삭제대기, 03:잠김, A0:편집요청, A1:삭제요청, A2:복구요청, A3:이동요청, A4:복사요청, A5:잠금요청, A6:이동요청, C0:Refuse to be edited, C1:Refuse to be deleted, C2:Refuse to be recovered, C4:Refuse to be moved, C5:Refuse to be copied, C6:Refuse to be locked, B1:Request for registering, B2:Request for discarding, B3:Request for registering through replication, D1:Refuse to be registered, D2:Refuse to be discarded
	CONSTRAINT pk_mfile_01 PRIMARY KEY (mfileid)
);
CREATE INDEX idx_amfile_01 ON zarch_mfile (linkid);


-- Column comments

COMMENT ON COLUMN zarch_mfile.state IS '00:정상, 01:삭제대기, 03:잠김, A0:편집요청, A1:삭제요청, A2:복구요청, A3:이동요청, A4:복사요청, A5:잠금요청, A6:이동요청, C0:Refuse to be edited, C1:Refuse to be deleted, C2:Refuse to be recovered, C4:Refuse to be moved, C5:Refuse to be copied, C6:Refuse to be locked, B1:Request for registering, B2:Request for discarding, B3:Request for registering through replication, D1:Refuse to be registered, D2:Refuse to be discarded';

CREATE TABLE zarch_mon (
	mon varchar(2) NOT NULL
);

CREATE TABLE zarch_statistic (
	regday varchar(50) NOT NULL,
	cabinetid varchar(64) NOT NULL,
	filesize numeric NOT NULL,
	filecnt number NOT NULL,
	CONSTRAINT pk_statistic PRIMARY KEY (regday)
);


CREATE TABLE zarch_task (
	taskid varchar(64) NOT NULL,
	name varchar(100) NOT NULL,
	descpt varchar(100) NULL,
	code varchar(30) NULL,
	CONSTRAINT pk_task PRIMARY KEY (taskid)
);


CREATE TABLE zarch_year (
	year varchar(4) NOT NULL
);

CREATE TABLE zapp_class (
	classid varchar(64) NOT NULL,
	companyid varchar(64) NOT NULL,
	name varchar(500) NOT NULL,
	upid varchar(64) NULL,
	holderid varchar(64) NULL,
	types varchar(2) NOT NULL, -- N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류
	priority number NOT NULL,
	isactive varchar(1) NOT NULL,
	code varchar(64) NULL,
	descpt varchar(500) NULL,
	wfid varchar(64) NULL,
	wfrequired number NULL, -- 0: Not applied, 1:Registering, 2:Editing, 4:Deleting, 8:Recovering, 99:All
	affiliationid varchar(64) NULL,
	retentionid varchar(64) NULL,
	cpath varchar2(4000) NULL,
	CONSTRAINT zapp_class_pkey PRIMARY KEY (classid),
	CONSTRAINT fk_class_01 FOREIGN KEY (companyid) REFERENCES zapp_company(companyid)
);
CREATE INDEX idx_class_01 ON zapp_class (companyid, upid);


-- Column comments

COMMENT ON COLUMN zapp_class.types IS 'N1:전사노드분류, N2:부서노드분류, N3:개인노드분류, N4:협업노드분류';
COMMENT ON COLUMN zapp_class.wfrequired IS '0: Not applied, 1:Registering, 2:Editing, 4:Deleting, 8:Recovering, 99:All';


CREATE TABLE zapp_code (
	codeid varchar(64) NOT NULL,
	companyid varchar(64) NOT NULL,
	name varchar(150) NOT NULL,
	codevalue varchar(50) NOT NULL,
	upid varchar(64) NULL,
	types varchar(2) NOT NULL,
	codekey varchar(64) NOT NULL,
	priority number NOT NULL,
	isactive varchar(1) NOT NULL,
	CONSTRAINT pk_code PRIMARY KEY (codeid),
	CONSTRAINT fk_code_01 FOREIGN KEY (companyid) REFERENCES zapp_company(companyid)
);
CREATE INDEX idx_code_01 ON zapp_code (upid, types);


CREATE TABLE zapp_dept (
	deptid varchar(64) NOT NULL,
	companyid varchar(64) NOT NULL,
	name varchar(300) NOT NULL,
	upid varchar(64) NULL,
	code varchar(30) NOT NULL,
	abbrname varchar(50) NOT NULL,
	priority number NOT NULL,
	isactive varchar(1) NOT NULL,
	CONSTRAINT pk_dept PRIMARY KEY (deptid),
	CONSTRAINT fk_dept_01 FOREIGN KEY (companyid) REFERENCES zapp_company(companyid)
);
CREATE INDEX idx_dept_01 ON zapp_dept (companyid, upid);
CREATE UNIQUE INDEX uidx_dept_02 ON zapp_dept (companyid, code);


CREATE TABLE zapp_env (
	envid varchar(64) NOT NULL,
	companyid varchar(64) NOT NULL,
	userid varchar(64) NULL,
	name varchar(150) NOT NULL,
	setval varchar(30) NOT NULL,
	envtype varchar(2) NOT NULL,
	settype varchar(2) NOT NULL,
	setopt varchar(200) NOT NULL,
	editable varchar(1) NOT NULL,
	envkey varchar(50) NOT NULL,
	isactive varchar(1) NOT NULL,
	sysdiv varchar(2) NULL,
	CONSTRAINT pk_env PRIMARY KEY (envid),
	CONSTRAINT fk_env_01 FOREIGN KEY (companyid) REFERENCES zapp_company(companyid)
);
CREATE UNIQUE INDEX uidx_env_02 ON zapp_env (companyid, envkey);



CREATE TABLE zapp_keywordobject (
	kwobjid varchar(64) NOT NULL,
	kwordid varchar(64) NOT NULL,
	kobjid varchar(64) NOT NULL,
	kobjtype varchar(2) NOT NULL,
	CONSTRAINT pk_keywordobject PRIMARY KEY (kwobjid),
	CONSTRAINT fk_keywordobject_01 FOREIGN KEY (kwordid) REFERENCES zapp_keywords(kwordid)
);
CREATE INDEX idx_keywordobject_01 ON zapp_keywordobject (kwordid);




CREATE TABLE zapp_lockedobject (
	lockobjid varchar(64) NOT NULL,
	lobjid varchar(64) NOT NULL,
	lockerid varchar(64) NOT NULL,
	locktime varchar(25) NOT NULL,
	releasetime varchar(25) NULL,
	reason varchar(300) NULL,
	lobjtype varchar(2) DEFAULT '01' NOT NULL,
	CONSTRAINT pk_lockedobject PRIMARY KEY (lockobjid),
	CONSTRAINT fk_lockedobject_01 FOREIGN KEY (lockerid) REFERENCES zapp_deptuser(deptuserid)
);
CREATE INDEX idx_lockedobject_01 ON zapp_lockedobject (lockerid);


CREATE TABLE zapp_mfile (
	mfileid varchar(64) NOT NULL,
	fno varchar(45) NULL,
	retentionid varchar(64) NULL,
	expiretime varchar(25) NOT NULL,
	holderid varchar(64) NOT NULL,
	creatorname varchar(100) DEFAULT '' NOT NULL,
	discarderid varchar(64) NULL,
	ext varchar(30) NULL,
	dynamic01 varchar(10) NULL,
	dynamic02 varchar(1) NULL,
	dynamic03 varchar(1) NULL,
	drafter varchar(150) NULL,
	summary varchar(150) NULL,
	seclevel smallint DEFAULT 0 NOT NULL,
	CONSTRAINT pk_mfile_02 PRIMARY KEY (mfileid),
	CONSTRAINT fk_mfile_01 FOREIGN KEY (mfileid) REFERENCES zarch_mfile(mfileid)
);




CREATE TABLE zapp_organtask (
	organtaskid varchar(64) NOT NULL,
	companyid varchar(64) NOT NULL,
	deptid varchar(64) NULL,
	taskid varchar(64) NOT NULL,
	tobjtype varchar(2) NOT NULL,
	CONSTRAINT pk_organtask PRIMARY KEY (organtaskid),
	CONSTRAINT fk_organtask_01 FOREIGN KEY (companyid) REFERENCES zapp_company(companyid),
	CONSTRAINT fk_organtask_02 FOREIGN KEY (taskid) REFERENCES zarch_task(taskid)
);
CREATE INDEX idx_organtask_01 ON zapp_organtask (companyid);




CREATE TABLE zapp_sharedobject (
	shareobjid varchar(64) NOT NULL,
	sobjid varchar(64) NOT NULL,
	sobjtype varchar(2) NOT NULL,
	sharerid varchar(64) NOT NULL,
	readerid varchar(64) NOT NULL,
	sharetime varchar(25) NOT NULL,
	readertype varchar(2) DEFAULT '01' NOT NULL,
	CONSTRAINT pk_sharedobject PRIMARY KEY (shareobjid),
	CONSTRAINT fk_sharedobject_01 FOREIGN KEY (sharerid) REFERENCES zapp_deptuser(deptuserid)
);
CREATE INDEX idx_sharedobject_01 ON zapp_sharedobject (sharerid);
CREATE INDEX idx_sharedobject_02 ON zapp_sharedobject (readerid, readertype);




CREATE TABLE zapp_user (
	userid varchar(64) NOT NULL,
	companyid varchar(64) NOT NULL,
	empno varchar(30) NOT NULL,
	loginid varchar(50) NOT NULL,
	name varchar(50) NOT NULL,
	passwd varchar(64) NOT NULL,
	passwdsalt varchar(64) NOT NULL,
	email varchar(100) NOT NULL,
	isactive varchar(1) NOT NULL,
	maclimit varchar(100) NULL,
	iplimit varchar(100) NULL,
	CONSTRAINT pk_user PRIMARY KEY (userid),
	CONSTRAINT fk_user_01 FOREIGN KEY (companyid) REFERENCES zapp_company(companyid)
);
CREATE INDEX idx_user_01 ON zapp_user (companyid);
CREATE UNIQUE INDEX uidx_user_02 ON zapp_user (companyid, empno);
CREATE UNIQUE INDEX uidx_user_03 ON zapp_user (email);



CREATE TABLE zarch_taskcabinet (
	taskid varchar(64) NOT NULL,
	cabinetid varchar(64) NOT NULL,
	CONSTRAINT pk_taskcabinet PRIMARY KEY (taskid, cabinetid),
	CONSTRAINT fk_taskcabinet_01 FOREIGN KEY (taskid) REFERENCES zarch_task(taskid),
	CONSTRAINT fk_taskcabinet_02 FOREIGN KEY (cabinetid) REFERENCES zarch_cabinet(cabinetid)
);


CREATE TABLE zarch_ufile (
	ufileid varchar(64) NOT NULL,
	hashid varchar(64) NOT NULL,
	formatid varchar(64) NOT NULL,
	cabinetid varchar(64) NOT NULL,
	filesize numeric NOT NULL,
	createtime varchar(25) NOT NULL,
	isencrypted varchar(1) NOT NULL,
	CONSTRAINT pk_ufile PRIMARY KEY (ufileid),
	CONSTRAINT fk_ufile_01 FOREIGN KEY (cabinetid) REFERENCES zarch_cabinet(cabinetid),
	CONSTRAINT fk_ufile_02 FOREIGN KEY (formatid) REFERENCES zarch_format(formatid)
);
CREATE INDEX idx_ufile_01 ON zarch_ufile (cabinetid);



CREATE TABLE zarch_version (
	versionid varchar(64) NOT NULL,
	mfileid varchar(64) NOT NULL,
	ufileid varchar(64) NOT NULL,
	hver number NOT NULL,
	lver number NOT NULL,
	creator varchar(64) NOT NULL,
	createtime varchar(25) NOT NULL,
	filename varchar(1000) NOT NULL,
	CONSTRAINT pk_version PRIMARY KEY (versionid),
	CONSTRAINT fk_version_01 FOREIGN KEY (mfileid) REFERENCES zarch_mfile(mfileid),
	CONSTRAINT fk_version_02 FOREIGN KEY (ufileid) REFERENCES zarch_ufile(ufileid)
);
CREATE INDEX idx_version_01 ON zarch_version (mfileid);


CREATE TABLE ZAPP_COMMENT
(
    COMMENTID CHAR(64) NOT NULL,
    COBJID CHAR(64) NOT NULL,
    COBJTYPE VARCHAR2(2) NOT NULL,
    COMMENTS VARCHAR2(500),
    COMMENTTIME VARCHAR2(25) NOT NULL,
    COMMENTER VARCHAR2(100) NOT NULL,
    COMMENTERID VARCHAR2(64) NOT NULL,
    CONSTRAINT  pk_comment PRIMARY KEY (COMMENTID)
);
CREATE INDEX idx_comment_01 ON ZAPP_COMMENT (COBJID, COBJTYPE);

