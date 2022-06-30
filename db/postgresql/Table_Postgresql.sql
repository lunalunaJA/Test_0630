/*
  [POSTGRESQL] 
*/

-- DUAL
CREATE TABLE IF NOT EXISTS public.dual (
	"X" bpchar(1) NOT NULL
);

-- 접근 로그 정보
CREATE TABLE  IF NOT EXISTS  public.zapp_accesslog (
	logid bpchar(64) NOT NULL,
	companyid varchar(64) NOT NULL,
	logobjid varchar(64) NULL,
	loggerid varchar(64) NOT NULL,
	loggername varchar(50) NOT NULL,
	loggerdeptid varchar(64) NOT NULL,
	loggerdeptname varchar(150) NOT NULL,
	logtime varchar(25) NOT NULL,
	logtype varchar(2) NOT NULL,
	"action" varchar(2) NOT NULL,
	logs text NULL,
	CONSTRAINT pk_accesslog PRIMARY KEY (logid)
);
CREATE UNIQUE INDEX uidx_accesslog_01 ON public.zapp_accesslog USING btree (logid);
CREATE INDEX idx_accesslog_01 ON public.zapp_accesslog USING btree (companyid);
CREATE INDEX idx_accesslog_02 ON public.zapp_accesslog USING btree (logobjid);

-- 추가 번들 정보
CREATE TABLE  IF NOT EXISTS  public.zapp_additorybundle (
	bundleid bpchar(64) NOT NULL,
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
	summary varchar(2000) NULL,
	CONSTRAINT pk_additorybundle PRIMARY KEY (bundleid)
);
CREATE UNIQUE INDEX uidx_additorybundle_01 ON public.zapp_additorybundle USING btree (bundleid);

-- 추가 분류 정보
CREATE TABLE  IF NOT EXISTS  public.zapp_additoryclass (
	classid bpchar(64) NULL,
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
CREATE UNIQUE INDEX uidx_additoryclass_01 ON public.zapp_additoryclass USING btree (classid);

-- 번들 정보
CREATE TABLE  IF NOT EXISTS  public.zapp_bundle (
	bundleid bpchar(64) NOT NULL,
	bno varchar(50) NULL,
	title varchar(500) NOT NULL,
	creatorid bpchar(64) NOT NULL,
	creatorname varchar(50) NOT NULL,
	holderid bpchar(64) NOT NULL,
	createtime varchar(25) NOT NULL,
	updatetime varchar(25) NULL,
	expiretime varchar(25) NOT NULL,
	discarderid varchar(64) NULL,
	retentionid bpchar(64) NULL,
	state varchar(2) NOT NULL,
	seclevel smallint NOT NULL DEFAULT 0,
	CONSTRAINT pk_bundle PRIMARY KEY (bundleid)
);
CREATE UNIQUE INDEX uidx_bundle_01 ON public.zapp_bundle USING btree (bundleid);

-- 분류 정보
CREATE TABLE  IF NOT EXISTS  public.zapp_class (
	classid bpchar(64) NOT NULL,
	companyid bpchar(64) NOT NULL,
	"name" varchar(500) NOT NULL,
	upid varchar(64) NULL,
	holderid varchar(64) NULL,
	"types" varchar(2) NOT NULL,
	priority int2 NOT NULL,
	code varchar(64) NULL,
	descpt varchar(500) NULL,
	wfid  varchar(64) NULL,
	wfrequired smallint,
	affiliationid varchar(64) NULL,
	retentionid varchar(64) NULL,
	cpath varchar(4000) NULL,
	isactive bpchar(1) NOT NULL,
	CONSTRAINT pk_class PRIMARY KEY (classid)
);
CREATE UNIQUE INDEX uidx_class_01 ON public.zapp_class USING btree (classid);
CREATE UNIQUE INDEX uidx_class_02 ON public.zapp_class USING btree (code);
CREATE INDEX idx_class_01 ON public.zapp_class USING btree (companyid, upid);

-- 분류 권한
CREATE TABLE  IF NOT EXISTS  public.zapp_classacl (
	aclid bpchar(64) NOT NULL,
	classid bpchar(64) NOT NULL,
	aclobjid varchar(64) NOT NULL,
	aclobjtype varchar(2) NOT NULL,
	acls int2 NOT NULL,
	CONSTRAINT pk_classacl PRIMARY KEY (aclid)
);
CREATE UNIQUE INDEX uidx_classacl_01 ON public.zapp_classacl USING btree (aclid);
CREATE INDEX idx_classacl_01 ON public.zapp_classacl USING btree (classid, aclobjid, aclobjtype);

-- 분류 객체
CREATE TABLE  IF NOT EXISTS  public.zapp_classobject (
	classobjid bpchar(64) NOT NULL,
	classid bpchar(64) NOT NULL,
	classtype varchar(2) NOT NULL,
	cobjid varchar(64) NOT NULL,
	cobjtype varchar(2) NOT NULL,
	CONSTRAINT pk_classobject PRIMARY KEY (classobjid)
);
CREATE UNIQUE INDEX uidx_classobject_01 ON public.zapp_classobject USING btree (classobjid);
CREATE INDEX idx_classobject_01 ON public.zapp_classobject USING btree (classid, classtype);

-- 코드
CREATE TABLE  IF NOT EXISTS  public.zapp_code (
	codeid bpchar(64) NOT NULL,
	companyid bpchar(64) NOT NULL,
	"name" varchar(150) NOT NULL,
	codevalue varchar(50) NOT NULL,
	upid varchar(64) NULL,
	"types" varchar(2) NOT NULL,
	codekey varchar(64) NOT NULL,
	priority int2 NOT NULL,
	isactive bpchar(1) NOT NULL,
	CONSTRAINT pk_code PRIMARY KEY (codeid)
);
CREATE UNIQUE INDEX uidx_code_01 ON public.zapp_code USING btree (codeid);
CREATE INDEX idx_code_01 ON public.zapp_code USING btree (upid, types);

-- 기관
CREATE TABLE  IF NOT EXISTS  public.zapp_company (
	companyid bpchar(64) NOT NULL,
	"name" varchar(20) NOT NULL,
	tel varchar(50) NULL,
	address varchar(500) NULL,
	code varchar(30) NOT NULL,
	abbrname varchar(50) NOT NULL,
	isactive bpchar(1) NOT NULL,
	CONSTRAINT pk_company PRIMARY KEY (companyid)
);
CREATE UNIQUE INDEX uidx_company_01 ON public.zapp_company USING btree (companyid);


-- 컨텐츠 권한
CREATE TABLE  IF NOT EXISTS  public.zapp_contentacl (
	aclid bpchar(64) NOT NULL,
	contentid bpchar(64) NOT NULL,
	aclobjid varchar(64) NOT NULL,
	aclobjtype varchar(2) NOT NULL,
	acls int2 NOT NULL,
	contenttype varchar(2) NOT NULL DEFAULT '01'::character varying,
	CONSTRAINT pk_contentacl PRIMARY KEY (aclid)
);
CREATE UNIQUE INDEX uidx_contentacl_01 ON public.zapp_contentacl USING btree (aclid);
CREATE INDEX idx_contentacl_01 ON public.zapp_contentacl USING btree (contentid, aclobjid, aclobjtype);


-- 컨텐츠 로그
CREATE TABLE  IF NOT EXISTS  public.zapp_contentlog (
	logid bpchar(64) NULL,
	companyid varchar(64) NULL,
	logobjid varchar(64) NULL,
	loggerid varchar(64) NULL,
	loggername varchar(50) NULL,
	loggerdeptid varchar(64) NULL,
	loggerdeptname varchar(150) NULL,
	logtime varchar(25) NULL,
	logtype varchar(2) NULL,
	"action" varchar(2) NULL,
	logs text NULL,
	logtext varchar(500) NULL,
	CONSTRAINT pk_contentlog PRIMARY KEY (logid)
);
CREATE UNIQUE INDEX uidx_contentlog_01 ON public.zapp_contentlog USING btree (logid);
CREATE INDEX idx_contentlog_01 ON public.zapp_contentlog USING btree (companyid, logobjid);


CREATE TABLE IF NOT EXISTS public.zapp_contentworkflow (
	cwfid bpchar(64) NOT NULL,
	contentid bpchar(64) NOT NULL,
	contenttype varchar(2) NOT NULL,
	drafterid varchar(64) NOT NULL,
	draftername varchar(100) NOT NULL,
	wferid varchar(64) NOT NULL,
	wfername varchar(100) NOT NULL,
	wftime varchar(25) NOT NULL,
	"comments" varchar(500) NULL,
	status varchar(2) NOT NULL,
	confirmed bpchar(1) NULL,
	CONSTRAINT zapp_contentworkflow_pkey PRIMARY KEY (cwfid)
);

-- 주기 작업 로그
CREATE TABLE  IF NOT EXISTS  public.zapp_cyclelog (
	cycleid bpchar(64) NOT NULL,
	companyid bpchar(64) NOT NULL,
	cycletime varchar(25) NOT NULL,
	cycletype varchar(2) NOT NULL,
	cyclelogs text NULL,
	CONSTRAINT pk_cyclelog PRIMARY KEY (cycleid)
);
CREATE UNIQUE INDEX uidx_cyclelog_01 ON public.zapp_cyclelog USING btree (cycleid);
CREATE INDEX idx_cyclelog_01 ON public.zapp_cyclelog USING btree (companyid, cycletype);

-- 부서
CREATE TABLE  IF NOT EXISTS  public.zapp_dept (
	deptid bpchar(64) NOT NULL,
	companyid bpchar(64) NOT NULL,
	"name" varchar(300) NOT NULL,
	upid bpchar(64) NULL,
	code varchar(30) NOT NULL,
	abbrname varchar(50) NOT NULL,
	priority int2 NOT NULL,
	isactive bpchar(1) NOT NULL,
	CONSTRAINT pk_dept PRIMARY KEY (deptid)
);
CREATE UNIQUE INDEX uidx_dept_01 ON public.zapp_dept USING btree (deptid);
CREATE UNIQUE INDEX uidx_dept_02 ON public.zapp_dept USING btree (companyid, code);
CREATE INDEX idx_dept_01 ON public.zapp_dept USING btree (companyid, upid);

-- 부서 사용자
CREATE TABLE  IF NOT EXISTS  public.zapp_deptuser (
	deptuserid bpchar(64) NOT NULL,
	deptid bpchar(64) NOT NULL,
	userid bpchar(64) NOT NULL,
	usertype varchar(2) NOT NULL,
	originyn bpchar(1) NOT NULL,
	positionid varchar(64) NOT NULL,
	dutyid varchar(64) NOT NULL,
	seclevelid varchar(64) NOT NULL,
	isactive bpchar(1) NOT NULL,
	issupervisor bpchar(1) NOT NULL DEFAULT 'N'::bpchar,
	CONSTRAINT pk_deptuser PRIMARY KEY (deptuserid)
);
CREATE UNIQUE INDEX uidx_deptuser_01 ON public.zapp_deptuser USING btree (deptuserid);
CREATE INDEX idx_deptuser_01 ON public.zapp_deptuser USING btree (userid);


-- 환경설정
CREATE TABLE  IF NOT EXISTS  public.zapp_env (
	envid bpchar(64) NOT NULL,
	companyid varchar(64) NOT NULL,
	userid varchar(64) NULL,
	"name" varchar(150) NOT NULL,
	setval varchar(30) NOT NULL,
	envtype varchar(2) NOT NULL,
	settype varchar(2) NOT NULL,
	setopt varchar(200) NOT NULL,
	editable bpchar(1) NOT NULL,
	envkey varchar(50) NOT NULL,
	isactive bpchar(1) NOT NULL,
	CONSTRAINT pk_env PRIMARY KEY (envid)
);
CREATE UNIQUE INDEX uidx_env_01 ON public.zapp_env USING btree (envid);
CREATE UNIQUE INDEX uidx_env_02 ON public.zapp_env USING btree (companyid, envkey);

-- 그룹
CREATE TABLE  IF NOT EXISTS  public.zapp_group (
	groupid bpchar(64) NOT NULL,
	companyid bpchar(64) NOT NULL,
	"name" varchar(150) NOT NULL,
	upid varchar(64) NULL,
	"types" varchar(2) NOT NULL,
	isactive bpchar(1) NOT NULL,
	code varchar(50) NULL,
	priority int2 NOT NULL,
	CONSTRAINT pk_group PRIMARY KEY (groupid)
);
CREATE UNIQUE INDEX uidx_group_01 ON public.zapp_group USING btree (groupid);
CREATE INDEX idx_group_01 ON public.zapp_group USING btree (companyid, upid, types);


-- 그룹사용자
CREATE TABLE  IF NOT EXISTS  public.zapp_groupuser (
	groupuserid bpchar(64) NOT NULL,
	groupid varchar(64) NOT NULL,
	gobjid varchar(64) NOT NULL,
	gobjtype varchar(2) NOT NULL,
	editable varchar(20) NULL,
	CONSTRAINT pk_groupuser PRIMARY KEY (groupuserid)
);
CREATE UNIQUE INDEX uidx_groupuser_01 ON public.zapp_groupuser USING btree (groupuserid);
CREATE INDEX idx_groupuser_01 ON public.zapp_groupuser USING btree (groupid);


-- 키워드 객체
CREATE TABLE  IF NOT EXISTS  public.zapp_keywordobject (
	kwobjid bpchar(64) NOT NULL,
	kwordid bpchar(64) NOT NULL,
	kobjid varchar(64) NOT NULL,
	kobjtype varchar(2) NOT NULL,
	CONSTRAINT pk_keywordobject PRIMARY KEY (kwobjid)
);
CREATE UNIQUE INDEX uidx_keywordobject_01 ON public.zapp_keywordobject USING btree (kwobjid);
CREATE INDEX idx_keywordobject_01 ON public.zapp_keywordobject USING btree (kwordid);

-- 키워드
CREATE TABLE  IF NOT EXISTS  public.zapp_keywords (
	kwordid bpchar(64) NOT NULL,
	kword varchar(50) NOT NULL,
	isactive bpchar(1) NOT NULL,
	CONSTRAINT pk_keywords PRIMARY KEY (kwordid)
);
CREATE UNIQUE INDEX uidx_keywords_01 ON public.zapp_keywords USING btree (kwordid);

-- 링크 객체
CREATE TABLE  IF NOT EXISTS  public.zapp_linkedobject (
	linkedobjid bpchar(64) NOT NULL,
	sourceid varchar(64) NOT NULL,
	targetid varchar(64) NOT NULL,
	linkerid varchar(64) NOT NULL,
	linktime varchar(25) NOT NULL,
	linktype varchar(2) NOT NULL,
	CONSTRAINT pk_linkedobject PRIMARY KEY (linkedobjid)
);
CREATE UNIQUE INDEX uidx_linkedobject_01 ON public.zapp_linkedobject USING btree (linkedobjid);
CREATE INDEX idx_linkedobject_01 ON public.zapp_linkedobject USING btree (sourceid);
CREATE INDEX idx_linkedobject_02 ON public.zapp_linkedobject USING btree (linkerid);

-- 잠금 객체
CREATE TABLE  IF NOT EXISTS  public.zapp_lockedobject (
	lockobjid bpchar(64) NOT NULL,
	lobjid varchar(64) NOT NULL,
	lockerid varchar(64) NOT NULL,
	locktime varchar(25) NOT NULL,
	releasetime varchar(25) NULL,
	reason varchar(300) NULL,
	lobjtype varchar(2) NOT NULL DEFAULT '01'::character varying,
	CONSTRAINT pk_lockedobject PRIMARY KEY (lockobjid)
);
CREATE UNIQUE INDEX uidx_lockedobject_01 ON public.zapp_lockedobject USING btree (lockobjid);
CREATE INDEX idx_lockedobject_01 ON public.zapp_lockedobject USING btree (lockerid);

-- 마크 객체
CREATE TABLE  IF NOT EXISTS  public.zapp_markedobject (
	markedobjid bpchar(64) NOT NULL,
	mobjid varchar(64) NOT NULL,
	mobjtype varchar(2) NOT NULL,
	markerid varchar(64) NOT NULL,
	marktime varchar(25) NOT NULL,
	CONSTRAINT pk_markedobject PRIMARY KEY (markedobjid)
);
CREATE UNIQUE INDEX uidx_markedobject_01 ON public.zapp_markedobject USING btree (markedobjid);
CREATE INDEX idx_markedobject_01 ON public.zapp_markedobject USING btree (markerid);
CREATE INDEX idx_markedobject_02 ON public.zapp_markedobject USING btree (mobjid, mobjtype);


-- 파일 추가
CREATE TABLE  IF NOT EXISTS  public.zapp_mfile (
	mfileid bpchar(64) NOT NULL,
	fno varchar(45) NULL,
	retentionid bpchar(64) NOT NULL,
	expiretime varchar(25) NOT NULL,
	holderid varchar(64) NOT NULL,
	creatorname varchar(100) NOT NULL DEFAULT ''::character varying,
	discarderid varchar(64) NULL,
	ext varchar(30) NULL,
	dynamic01 varchar(10) NULL,
	dynamic02 varchar(1) NULL,
	dynamic03 varchar(1) NULL,
	drafer varchar(150) NULL,
	summary varchar(2000) NULL,
	seclevel smallint NOT NULL DEFAULT 0,
	CONSTRAINT pk_mfile_02 PRIMARY KEY (mfileid)
);
CREATE UNIQUE INDEX uidx_mfile_01 ON public.zapp_mfile USING btree (mfileid);

-- 기관 업무
CREATE TABLE  IF NOT EXISTS  public.zapp_organtask (
	organtaskid bpchar(64) NOT NULL,
	companyid bpchar(64) NOT NULL,
	deptid bpchar(64) NULL,
	taskid bpchar(64) NOT NULL,
	tobjtype varchar(2) NOT NULL,
	CONSTRAINT pk_organtask PRIMARY KEY (organtaskid)
);
CREATE UNIQUE INDEX uidx_organtask_01 ON public.zapp_organtask USING btree (organtaskid);
CREATE INDEX idx_organtask_01 ON public.zapp_organtask USING btree (companyid);

-- 공유 객체
CREATE TABLE  IF NOT EXISTS  public.zapp_sharedobject (
	shareobjid bpchar(64) NOT NULL,
	sobjid varchar(64) NOT NULL,
	sobjtype varchar(2) NOT NULL,
	sharerid varchar(64) NOT NULL,
	readerid varchar(64) NOT NULL,
	sharetime varchar(25) NOT NULL,
	readertype varchar(2) NOT NULL DEFAULT '01'::character varying,
	CONSTRAINT pk_sharedobject PRIMARY KEY (shareobjid)
);
CREATE UNIQUE INDEX uidx_sharedobject_01 ON public.zapp_sharedobject USING btree (shareobjid);
CREATE INDEX idx_sharedobject_01 ON public.zapp_sharedobject USING btree (sharerid);
CREATE INDEX idx_sharedobject_02 ON public.zapp_sharedobject USING btree (readerid, readertype);

-- 현황
CREATE TABLE  IF NOT EXISTS  public.zapp_status (
	statusid bpchar(64) NOT NULL,
	stacompanyid bpchar(64) NOT NULL,
	staobjid varchar(64) NOT NULL,
	staobjtype varchar(64) NOT NULL,
	stacnt int8 NOT NULL,
	stadate varchar(25) NOT NULL,
	staaction varchar(2) NOT NULL,
	statermtype varchar(2) NOT NULL,
	CONSTRAINT pk_status PRIMARY KEY (statusid)
);
CREATE UNIQUE INDEX uidx_status_01 ON public.zapp_status USING btree (statusid);
CREATE INDEX idx_status_01 ON public.zapp_status USING btree (stacompanyid, staobjid, staobjtype);


-- 시스템 로그
CREATE TABLE  IF NOT EXISTS  public.zapp_systemlog (
	logid bpchar(64) NULL,
	companyid varchar(64) NULL,
	logobjid varchar(64) NULL,
	loggerid varchar(64) NULL,
	loggername varchar(50) NULL,
	loggerdeptid varchar(64) NULL,
	loggerdeptname varchar(150) NULL,
	logtime varchar(25) NULL,
	logtype varchar(2) NULL,
	"action" varchar(2) NULL,
	logs text NULL,
	CONSTRAINT pk_systemlog PRIMARY KEY (logid)
);
CREATE UNIQUE INDEX uidx_systemlog_01 ON public.zapp_systemlog USING btree (logid);
CREATE INDEX idx_systemlog_01 ON public.zapp_systemlog USING btree (companyid, logobjid);

-- 임시 객체
CREATE TABLE  IF NOT EXISTS  public.zapp_tmpobject (
	tmpobjid bpchar(64) NOT NULL,
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
	CONSTRAINT pk_tmpobject PRIMARY KEY (tmpobjid)
);
CREATE UNIQUE INDEX uidx_tmpobject_01 ON public.zapp_tmpobject USING btree (tmpobjid);
CREATE INDEX idx_tmpobject_01 ON public.zapp_tmpobject USING btree (objid, objtype);

-- 사용자
CREATE TABLE  IF NOT EXISTS  public.zapp_user (
	userid bpchar(64) NOT NULL,
	companyid varchar(64) NOT NULL,
	empno varchar(30) NOT NULL,
	loginid varchar(50) NOT NULL,
	"name" varchar(50) NOT NULL,
	passwd bpchar(64) NOT NULL,
	passwdsalt varchar(64) NOT NULL,
	email varchar(100) NOT NULL,
	isactive bpchar(1) NOT NULL,
	CONSTRAINT pk_user PRIMARY KEY (userid)
);
CREATE UNIQUE INDEX uidx_user_01 ON public.zapp_user USING btree (userid);
CREATE UNIQUE INDEX uidx_user_02 ON public.zapp_user USING btree (companyid, empno);
CREATE UNIQUE INDEX uidx_user_03 ON public.zapp_user USING btree (email);
CREATE INDEX idx_user_01 ON public.zapp_user USING btree (companyid);

-- 워크플로우
CREATE TABLE  IF NOT EXISTS  public.zapp_workflow (
	workflowid bpchar(64) NOT NULL,
	"name" varchar(150) NOT NULL,
	wftype bpchar(1) NOT NULL,
	isactive bpchar(1) NOT NULL,
	CONSTRAINT pk_workflow PRIMARY KEY (workflowid)
);
CREATE UNIQUE INDEX uidx_workflow_01 ON public.zapp_workflow USING btree (workflowid);

-- 워크플로워
CREATE TABLE  IF NOT EXISTS  public.zapp_workflower (
	workflowerid bpchar(64) NOT NULL,
	workflowid bpchar(64) NOT NULL,
	userid bpchar(64) NOT NULL,
	wfseq int2 NOT NULL,
	wfertype varchar(2) NOT NULL,
	isactive bpchar(1) NOT NULL,
	CONSTRAINT pk_workflower PRIMARY KEY (workflowerid)
);
CREATE UNIQUE INDEX uidx_workflower_01 ON public.zapp_workflower USING btree (workflowerid);
CREATE INDEX idx_workflower_01 ON public.zapp_workflower USING btree (workflowid);

-- 워크플로우 객체
CREATE TABLE IF NOT EXISTS public.zapp_workflowobject (
	wfobjid bpchar(64) NOT NULL,
	wferid bpchar(64) NOT NULL,
	contentid varchar(64) NOT NULL,
	contenttype varchar(2) NOT NULL
	CONSTRAINT pk_workflowobject PRIMARY KEY (wfobjid)
);
CREATE UNIQUE INDEX uidx_workflowobject_01 ON public.zapp_workflowobject USING btree (wfobjid);
CREATE INDEX idx_workflowobject_01 ON public.zapp_workflowobject USING btree (contentid, contenttype);

-- 관리자 (아카이브)
CREATE TABLE  IF NOT EXISTS  public.zarch_admin (
	adminid varchar(50) NOT NULL,
	"name" varchar(100) NOT NULL,
	passwd bpchar(64) NOT NULL,
	salt bpchar(64) NOT NULL,
	CONSTRAINT pk_admin PRIMARY KEY (adminid)
);
CREATE UNIQUE INDEX uidx_admin_01 ON public.zarch_admin USING btree (adminid);


-- 캐비닛 (아카이브) 
CREATE TABLE  IF NOT EXISTS  public.zarch_cabinet (
	cabinetid bpchar(64) NOT NULL,
	"name" varchar(100) NOT NULL,
	descpt varchar(100) NULL,
	maxcapacity int8 NOT NULL,
	mountpath text NOT NULL,
	seq int2 NOT NULL,
	state bpchar(1) NOT NULL,
	CONSTRAINT pk_cabinet PRIMARY KEY (cabinetid)
);
CREATE UNIQUE INDEX uidx_cabinet_01 ON public.zarch_cabinet USING btree (cabinetid);


-- 일 (아카이브) 
CREATE TABLE  IF NOT EXISTS  public.zarch_day (
	"day" varchar(2) NOT NULL
);

-- 파일로그 (아카이브) 
CREATE TABLE  IF NOT EXISTS  public.zarch_filelog (
	filelogid bpchar(64) NOT NULL,
	logs text NOT NULL,
	logtime varchar(25) NOT NULL,
	loggerid bpchar(64) NOT NULL,
	loggername varchar(100) NOT NULL,
	logtype varchar(2) NOT NULL,
	CONSTRAINT pk_filelog PRIMARY KEY (filelogid)
);
CREATE UNIQUE INDEX uidx_filelog_01 ON public.zarch_filelog USING btree (filelogid);

-- 파일유형 (아카이브) 
CREATE TABLE  IF NOT EXISTS  public.zarch_format (
	formatid bpchar(64) NOT NULL,
	"name" varchar(100) NOT NULL,
	descpt varchar(100) NULL,
	code varchar(30) NULL,
	mxsize numeric NOT NULL,
	ext varchar(15) NOT NULL,
	icon varchar(50) NULL,
	CONSTRAINT pk_format PRIMARY KEY (formatid)
);
CREATE UNIQUE INDEX uidx_format_01 ON public.zarch_format USING btree (formatid);

-- Mater 파일 (아카이브) 
CREATE TABLE  IF NOT EXISTS  public.zarch_mfile (
	mfileid bpchar(64) NOT NULL,
	linkid varchar(64) NOT NULL,
	filename text NOT NULL,
	seq int2 NOT NULL,
	creator varchar(64) NOT NULL,
	createtime varchar(25) NOT NULL,
	updatetime varchar(25) NULL,
	state varchar(2) NOT NULL,
	CONSTRAINT pk_mfile_01 PRIMARY KEY (mfileid)
);
CREATE UNIQUE INDEX uidx_amfile_01 ON public.zarch_mfile USING btree (mfileid);
CREATE INDEX idx_amfile_01 ON public.zarch_mfile USING btree (linkid);

-- 월 (아카이브) 
CREATE TABLE  IF NOT EXISTS  public.zarch_mon (
	mon varchar(2) NOT NULL
);

-- 파일 통계 (아카이브) 
CREATE TABLE  IF NOT EXISTS  public.zarch_statistic (
	regday varchar(50) NOT NULL,
	cabinetid bpchar(64) NOT NULL,
	filesize numeric NOT NULL,
	filecnt int8 NOT NULL,
	CONSTRAINT pk_statistic PRIMARY KEY (regday)
);
CREATE UNIQUE INDEX uidx_statistic_01 ON public.zarch_statistic USING btree (regday);

-- 업무 (아카이브) 
CREATE TABLE  IF NOT EXISTS  public.zarch_task (
	taskid bpchar(64) NOT NULL,
	"name" varchar(100) NOT NULL,
	descpt varchar(100) NULL,
	code varchar(30) NULL,
	CONSTRAINT pk_task PRIMARY KEY (taskid)
);
CREATE UNIQUE INDEX uidx_task_01 ON public.zarch_task USING btree (taskid);

-- 업무캐비닛 (아카이브) 
CREATE TABLE  IF NOT EXISTS  public.zarch_taskcabinet (
	taskid bpchar(64) NOT NULL,
	cabinetid bpchar(64) NOT NULL,
	CONSTRAINT pk_taskcabinet PRIMARY KEY (taskid, cabinetid)
);
CREATE UNIQUE INDEX uidx_taskcabinet_01 ON public.zarch_taskcabinet USING btree (taskid, cabinetid);

-- Unique 파일 (아카이브) 
CREATE TABLE  IF NOT EXISTS  public.zarch_ufile (
	ufileid bpchar(64) NOT NULL,
	hashid bpchar(64) NOT NULL,
	formatid bpchar(64) NOT NULL,
	cabinetid bpchar(64) NOT NULL,
	filesize numeric NOT NULL,
	createtime varchar(25) NOT NULL,
	isencrypted bpchar(1) NOT NULL,
	CONSTRAINT pk_ufile PRIMARY KEY (ufileid)
);
CREATE UNIQUE INDEX uidx_ufile_01 ON public.zarch_ufile USING btree (ufileid);
CREATE INDEX idx_ufile_01 ON public.zarch_ufile USING btree (cabinetid);

-- 버전 (아카이브) 
CREATE TABLE  IF NOT EXISTS  public.zarch_version (
	versionid bpchar(64) NOT NULL,
	mfileid bpchar(64) NOT NULL,
	ufileid bpchar(64) NOT NULL,
	hver int8 NOT NULL,
	lver int8 NOT NULL,
	creator varchar(64) NOT NULL,
	createtime varchar(25) NOT NULL,
	filename text NOT NULL,
	CONSTRAINT pk_version PRIMARY KEY (versionid)
);
CREATE UNIQUE INDEX uidx_version_01 ON public.zarch_version USING btree (versionid);
CREATE INDEX idx_version_01 ON public.zarch_version USING btree (mfileid);

-- 년 (아카이브) 
CREATE TABLE  IF NOT EXISTS  public.zarch_year (
	"year" varchar(4) NOT NULL
);


-- 코맨트 
CREATE TABLE IF NOT EXISTS public.zapp_comment
(
    commentid character(64) NOT NULL,
    cobjid character(64) NOT NULL,
    cobjtype character varying(2) NOT NULL,
    comments character varying(500) COLLATE pg_catalog."default",
    commenttime character varying(25) NOT NULL,
    commenter character varying(100) NOT NULL,
    commenterid character varying(64) NOT NULL,
    CONSTRAINT zapp_comment_pkey PRIMARY KEY (commentid)
)


/*
  Foreign Key
*/
-- ALTER TABLE zapp_additorybundle 	ADD CONSTRAINT fk_additorybundle_01 FOREIGN KEY (bundleid) 	REFERENCES zapp_bundle (bundleid);
-- ALTER TABLE zapp_additoryclass 	ADD CONSTRAINT fk_additoryclass_01 	FOREIGN KEY (classid) 	REFERENCES zapp_class (classid);
-- ALTER TABLE zapp_class 			ADD CONSTRAINT fk_class_01 			FOREIGN KEY (companyid) REFERENCES zapp_company (companyid);
-- ALTER TABLE zapp_classobject 	ADD CONSTRAINT fk_classobject_01 	FOREIGN KEY (classid) 	REFERENCES zapp_class (classid);
-- ALTER TABLE zapp_code 			ADD CONSTRAINT fk_code_01 			FOREIGN KEY (companyid) REFERENCES zapp_company (companyid);
-- ALTER TABLE zapp_dept			ADD CONSTRAINT fk_dept_01			FOREIGN KEY (companyid)	REFERENCES zapp_company (companyid);
-- ALTER TABLE zapp_deptuser		ADD CONSTRAINT fk_deptuser_01		FOREIGN KEY (deptid)	REFERENCES zapp_dept (deptid);
-- ALTER TABLE zapp_deptuser 		ADD CONSTRAINT fk_deptuser_02		FOREIGN KEY (userid)	REFERENCES zapp_user (userid);
-- ALTER TABLE zapp_env				ADD CONSTRAINT fk_env_01			FOREIGN KEY (companyid)	REFERENCES zapp_company (companyid);
-- ALTER TABLE zapp_group			ADD CONSTRAINT fk_group_01			FOREIGN KEY (companyid)	REFERENCES zapp_company (companyid);
-- ALTER TABLE zapp_groupuser		ADD CONSTRAINT fk_groupuser_01		FOREIGN KEY (groupid)	REFERENCES zapp_group (groupid);
-- ALTER TABLE zapp_keywordobject	ADD CONSTRAINT fk_keywordobject_01	FOREIGN KEY (kwordid)	REFERENCES zapp_keywords (kwordid);
-- ALTER TABLE zapp_lockedobject	ADD CONSTRAINT fk_lockedobject_01	FOREIGN KEY (lockerid)	REFERENCES zapp_deptuser (deptuserid);
-- ALTER TABLE zapp_markedobject	ADD CONSTRAINT fk_markedobject_01	FOREIGN KEY (markerid)	REFERENCES zapp_deptuser (deptuserid);
-- ALTER TABLE zapp_mfile			ADD CONSTRAINT fk_mfile_01			FOREIGN KEY (mfileid)	REFERENCES zarch_mfile (mfileid);
-- ALTER TABLE zapp_organtask		ADD CONSTRAINT fk_organtask_01		FOREIGN KEY (companyid)	REFERENCES zapp_company (companyid);
-- ALTER TABLE zapp_organtask 		ADD CONSTRAINT fk_organtask_02		FOREIGN KEY (taskid)	REFERENCES zarch_task (taskid);
-- ALTER TABLE zapp_sharedobject	ADD CONSTRAINT fk_sharedobject_01	FOREIGN KEY (sharerid)	REFERENCES zapp_deptuser (deptuserid);
-- ALTER TABLE zapp_user			ADD CONSTRAINT fk_user_01			FOREIGN KEY (companyid)	REFERENCES zapp_company (companyid);
-- ALTER TABLE zarch_taskcabinet	ADD CONSTRAINT fk_taskcabinet_01	FOREIGN KEY (taskid)	REFERENCES zarch_task (taskid);
-- ALTER TABLE zarch_taskcabinet 	ADD CONSTRAINT fk_taskcabinet_02	FOREIGN KEY (cabinetid)	REFERENCES zarch_cabinet (cabinetid);
-- ALTER TABLE zarch_ufile			ADD CONSTRAINT fk_ufile_01			FOREIGN KEY (cabinetid)	REFERENCES zarch_cabinet (cabinetid);
-- ALTER TABLE zarch_ufile 			ADD CONSTRAINT fk_ufile_02			FOREIGN KEY (formatid)	REFERENCES zarch_format (formatid);
-- ALTER TABLE zarch_version		ADD CONSTRAINT fk_version_01		FOREIGN KEY (mfileid)	REFERENCES zarch_mfile (mfileid);
-- ALTER TABLE zarch_version 		ADD CONSTRAINT fk_version_02		FOREIGN KEY (ufileid)	REFERENCES zarch_ufile (ufileid);
