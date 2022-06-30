
--  Table
create table zapp_company
(	
	companyid varchar(64) primary key, 
	name varchar(150) not null,
	tel varchar(45) not null,
	address varchar(500) ,
	code varchar(45) not null,
	abbrname varchar(45) ,
	isactive char(1) not null
)
tablespace ts_nadi4_data ;

comment on column zapp_company.companyid is '기관 해시 아이디 - hash(code + name)';
comment on column zapp_company.name is '기관 명칭';
comment on column zapp_company.tel is '전화번호';
comment on column zapp_company.address is '주소';
comment on column zapp_company.code is '코드';
comment on column zapp_company.abbrname is '약어';
comment on column zapp_company.isactive is '활성화여부';



create table zapp_user
(	
  userid varchar(64) primary key,
  companyid varchar(64) not null,
  loginid varchar(45) not null,
  name varchar(45) not null,
  passwd varchar(64) not null,
  passwdst varchar(64) not null,
  empno varchar(45) not null,
  email varchar(100) not null,
  isactive char(1) not null default 'Y',
  CONSTRAINT fk_ZAPP_USER_ZAPP_COMPANY1 FOREIGN KEY (companyid) REFERENCES zapp_company (companyid) 
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
tablespace ts_nadi4_data ;

comment on column zapp_user.userid is '사용자 해시 아아디 - hash (기관아이디 + 로그인아이디)';
comment on column zapp_user.companyid is '기관아이디';
comment on column zapp_user.loginid is '로그인 아이디';
comment on column zapp_user.name is '사용자명';
comment on column zapp_user.passwd is '패스워드';
comment on column zapp_user.passwdst is '패스워드 salt';
comment on column zapp_user.empno is '사번';
comment on column zapp_user.email is '이메일';
comment on column zapp_user.isactive is '상태';



create table zapp_dept
(	
  deptid varchar(64) primary key,
  companyid varchar(64) not null,
  name varchar(150) not null,
  code varchar(45) not null,
  upid varchar(64) null,
  abbrname varchar(45) null,
  priority int not null default 1,
  isactive char(1) not null default 'Y',
  CONSTRAINT fk_ZAPP_DEPT_ZAPP_COMPANY1 FOREIGN KEY (companyid) REFERENCES zapp_company (companyid) 
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
tablespace ts_nadi4_data ;

comment on column zapp_dept.deptid is '부서 해시 아이디  - hash(기관아이디+부모아이디+부서명)';
comment on column zapp_dept.companyid is '기관아이디';
comment on column zapp_dept.name is '부서명';
comment on column zapp_dept.code is '부서코드';
comment on column zapp_dept.upid is '부모아이디';
comment on column zapp_dept.abbrname is '부서약어';
comment on column zapp_dept.priority is '부서 순서';
comment on column zapp_dept.isactive is '상태';


create table zapp_env
(	
  envid varchar(64) primary key,
  companyid varchar(64) not null,
  title varchar(150) not null,
  setval varchar(20) not null,
  envtype varchar(2) not null,
  settype varchar(1) not null,
  setopt varchar(150) null,
  editable char(1) not null,
  envkey varchar(50) not null,
  isused char(1) not null default 'Y',
  CONSTRAINT fk_ZAPP_ENV_ZAPP_COMPANY1 FOREIGN KEY (companyid) REFERENCES zapp_company (companyid) 
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
tablespace ts_nadi4_data ;

comment on column zapp_env.envid is '설정 아이디 - hash (기관아이디 + 설정 유형)';
comment on column zapp_env.companyid is '기관 아이디';
comment on column zapp_env.title is '설정명';
comment on column zapp_env.setval is '설정값';
comment on column zapp_env.envtype is '설정유형';
comment on column zapp_env.settype is '설정값 유형';
comment on column zapp_env.setopt is '설정값 옵션';
comment on column zapp_env.editable is '편집 가능 여부';
comment on column zapp_env.envkey is '설정키';
comment on column zapp_env.isused is '사용여부';



create table zapp_code
(	
  codeid varchar(64) primary key,
  companyid varchar(64) not null,
  name varchar(45) not null,
  codevalue varchar(50) null,
  upid varchar(64) not null,
  types char(1) not null,
  priority int4 not null default 1,
  CONSTRAINT fk_ZAPP_CODE_ZAPP_COMPANY1 FOREIGN KEY (companyid) REFERENCES zapp_company (companyid) 
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
tablespace ts_nadi4_data ;

comment on column zapp_code.codeid is '코드 아이디 - hash (기관아이디 + 부모아이디 + 명칭 + 유형)';
comment on column zapp_code.companyid is '기관아이디';
comment on column zapp_code.name is '명칭';
comment on column zapp_code.upid is '부모아이디';
comment on column zapp_code.types is '유형';
comment on column zapp_code.priority is '순서';


create table zapp_groupuser
(	
  groupuserid varchar(64) primary key,
  objid varchar(64) not null,
  userid varchar(64) not null,
  objtype varchar(2) not null
)
tablespace ts_nadi4_data ;

comment on column zapp_groupuser.codeid is '대상 아이디 (부서아이디, 기관업무아이디, ...)';
comment on column zapp_groupuser.objtype is '대상 유형 (1: 부서, 2: 업무, ...)';


create table zapp_deptuser
(	
  deptuserid varchar(64) primary key,
  deptid varchar(64) not null,
  userid varchar(64) not null,
  usertype varchar(2) not null default '1',
  originyn char(1) not null default 'Y',
  positionid varchar(64) not null,
  dutyid varchar(64) not null,
  seclevelid varchar(64) not null,
  isactive char(1) not null default 'Y',
  CONSTRAINT fk_ZAPP_DEPTUSER_ZAPP_DEPT FOREIGN KEY (deptid) REFERENCES zapp_dept (deptid) 
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_ZAPP_DEPTUSER_ZAPP_USER1 FOREIGN KEY (userid) REFERENCES zapp_user (userid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_ZAPP_DEPTUSER_ZAPP_CODE1 FOREIGN KEY (positionid) REFERENCES zapp_code (codeid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_ZAPP_DEPTUSER_ZAPP_CODE2 FOREIGN KEY (dutyid) REFERENCES zapp_code (codeid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_ZAPP_DEPTUSER_ZAPP_CODE3 FOREIGN KEY (seclevelid) REFERENCES zapp_code (codeid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
tablespace ts_nadi4_data ;

comment on column zapp_deptuser.deptuserid is '부서 사용자 아이디 - hash(부서아이디 + 사용자아이디)';
comment on column zapp_deptuser.deptid is '부서 아이디';
comment on column zapp_deptuser.userid is '사용자 아이디';
comment on column zapp_deptuser.usertype is '사용자 유형';
comment on column zapp_deptuser.originyn is '원직 여부';
comment on column zapp_deptuser.positionid is '직위아이디';
comment on column zapp_deptuser.dutyid is '직무 아이디';
comment on column zapp_deptuser.seclevelid is '보안등급';
comment on column zapp_deptuser.isactive is '상태';



create table zapp_class
(	
  classid varchar(64) primary key,
  companyid varchar(64) not null,
  name varchar(500) not null,
  upid varchar(64) null,
  holderid varchar(64) null,
  types varchar(2) not null,
  priority int4 not null default 1,
  isactive char(1) not null default 'Y',
  CONSTRAINT fk_ZAPP_CLASS_ZAPP_COMPANY1 FOREIGN KEY (companyid) REFERENCES zapp_company (companyid) 
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
tablespace ts_nadi4_data ;

comment on column zapp_class.classid is '분류 아이디 - hash(기관아이디 + 부모아이디 + 상위아이디 + 분류유형)';
comment on column zapp_class.companyid is '기관아이디';
comment on column zapp_class.name is '계층명';
comment on column zapp_class.upid is '부모아이디';
comment on column zapp_class.holderid is '소유자 아이디';
comment on column zapp_class.types is '분류 유형 (01: 노드, 02: 분류체계, 03:문서유형, 04:즐겨찾기...)';
comment on column zapp_class.priority is '정렬순서';
comment on column zapp_class.isactive is '활성화여부';



create table zapp_classacl
(	
  aclid varchar(64) primary key,
  classid varchar(64) not null,
  objid varchar(64) not null,
  objtype varchar(2) not null,
  objacl int4 not null,
  CONSTRAINT fk_ZAPP_HIERARCHYACL_ZAPP_HIERARCHY1 FOREIGN KEY (classid) REFERENCES zapp_class (classid) 
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
tablespace ts_nadi4_data ;

comment on column zapp_classacl.aclid is '공유 아이디 - hash (계층아이디 + 권한대상아아디 + 권한대상유형)';
comment on column zapp_classacl.classid is '분류 아이디';
comment on column zapp_classacl.objid is '권한 대상 아이디';
comment on column zapp_classacl.objtype is '권한 대상 유형 (1:node, ...)';
comment on column zapp_classacl.objacl is '권한';



create table zapp_bundle_temp
(	
  bundleid varchar(64) primary key,
  bno varchar(45) not null,
  retentionid varchar(64) not null,
  creatorid varchar(64) not null,
  holderid varchar(64) not null,
  title varchar(500) not null,
  createtime varchar(25) not null,
  updatetime varchar(25) null,
  expiretime varchar(25) null,
  state varchar(2) not null default '1',
  CONSTRAINT fk_ZAPP_BINDER_E_ZAPP_DEPTUSER1 FOREIGN KEY (holderid) REFERENCES zapp_deptuser (deptuserid) 
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_ZAPP_BUNDLE_temp_ZAPP_CODE1 FOREIGN KEY (retentionid) REFERENCES zapp_code (codeid) 
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
tablespace ts_nadi4_data ;

comment on column zapp_bundle_temp.bundleid is '묵음아이디';
comment on column zapp_bundle_temp.bno is '묶음 번호';
comment on column zapp_bundle_temp.retentionid is '보존기간아이디';
comment on column zapp_bundle_temp.creatorid is '등록자 아이디';
comment on column zapp_bundle_temp.holderid is '소유자 아이디';
comment on column zapp_bundle_temp.title is '묶음 명칭';
comment on column zapp_bundle_temp.createtime is '생성 일시';
comment on column zapp_bundle_temp.state is '상태';



create table zapp_classobject
(	
  classobjid varchar(64) primary key,
  classid varchar(64) not null,
  objid varchar(64) not null,
  objtype varchar(2) not null,
  CONSTRAINT fk_ZAPP_HIERARCHYDOC_ZAPP_HIERARCHY1 FOREIGN KEY (classid) REFERENCES zapp_class (classid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
tablespace ts_nadi4_data ;

comment on column zapp_classobject.classobjid is '분류 대상 아이디 - hash (분류아이디 + bundle 아이디 또는 file 아이디)';
comment on column zapp_classobject.classid is '분류 아이디 (node, classification, ...)';
comment on column zapp_classobject.objid is '대상 아이디 (bundle, file, node, doctype)';



create table zapp_contentacl
(	
  aclid varchar(64) primary key,
  contentid varchar(64) not null,
  objid varchar(64) not null,
  objtype varchar(2) not null,
  acls int not null
)
tablespace ts_nadi4_data ;

comment on column zapp_contentacl.aclid is '컨텐츠 권한 아이디 - hash (컨텐츠아이디 + 권한대상아이디 + 권한대상유형)';
comment on column zapp_contentacl.contentid is '컨텐츠아이디';
comment on column zapp_contentacl.objid is '권한 대상 아아디';
comment on column zapp_contentacl.objtype is '권한 대상 유형 (01:사용자, 02:부서, 03:그룹, ...)';
comment on column zapp_contentacl.acls is '권한';



create table zapp_linkedobject
(	
  linkedobjid varchar(64) primary key,
  sourceid varchar(64) not null,
  targetid varchar(64) not null,
  linkerid varchar(64) not null,
  linktime varchar(20) not null,
  linktype varchar(2) not null default '01'

)
tablespace ts_nadi4_data ;

comment on column zapp_linkedobject.linkedobjid is '링크 대상 아이디';
comment on column zapp_linkedobject.sourceid is '소스 아이디';
comment on column zapp_linkedobject.targetid is '대상아이디';
comment on column zapp_linkedobject.linktime is '링크 시간';
comment on column zapp_linkedobject.linktype is '링크 유형 (01:파일-파일, 02:파일-번들, 03:파일-노드, 04:번들 -> 파일, 05:번들 -> 번들, 06:번들 -> 노드, 07:노드 -> 파일, 08:노드 -> 번들, 09:노드 -> 노드)';




create table zapp_sharedobject
(	
  shareobjid varchar(64) primary key,
  objid varchar(64) not null,
  objtype varchar(2) not null default '01',
  sharerid varchar(64) not null,
  readerid varchar(64) not null,
  sharetime varchar(20) not null,
  CONSTRAINT fk_ZAPP_SHAREDOBJECT_ZAPP_DEPTUSER1 FOREIGN KEY (sharerid) REFERENCES zapp_deptuser (deptuserid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_ZAPP_SHAREDOBJECT_ZAPP_DEPTUSER2 FOREIGN KEY (readerid) REFERENCES zapp_deptuser (deptuserid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
tablespace ts_nadi4_data ;

comment on column zapp_sharedobject.shareobjid is '공유 해시 아이디 - hash (공유대상아이디 + 공유대상유형 + 공유자아이디 + 공유 받는 사람 아이디)';
comment on column zapp_sharedobject.objid is '공유 대상 아이디';
comment on column zapp_sharedobject.objtype is '공유 대상 유형 (1:node, 2:bundle, 3:file, ...)';
comment on column zapp_sharedobject.sharerid is '공유한 사람 아이디';
comment on column zapp_sharedobject.readerid is '공유 받은 사함 아이디';
comment on column zapp_sharedobject.sharetime is '공유 만료 일자 ';


create table zapp_lockedobject
(	
  lockobjid varchar(64) primary key,
  contentid varchar(64) not null,
  lockerid varchar(64) not null,
  locktime varchar(25) not null,
  releasetime varchar(25) not null,
  reason varchar(4000) not null
)
tablespace ts_nadi4_data ;

comment on column zapp_lockedobject.lockobjid is 'pk';
comment on column zapp_lockedobject.contentid is '컨텐츠아이디';
comment on column zapp_lockedobject.locktime is '잠금시간';
comment on column zapp_lockedobject.releasetime is '해제시간';


create table zarch_task
(	
  taskid varchar(64)primary key,
  name varchar(100) not null,
  descpt varchar(45) null,
  code varchar(30) not null
)
tablespace ts_nadi4_data ;


create table zapp_organtask
(	
  organtaskid varchar(64) primary key,
  companyid varchar(64) not null,
  deptid varchar(64) null,
  taskid varchar(64) not null,
  objtype varchar(2) not null,
  CONSTRAINT fk_ZAPP_ORGANTASK_ZAPP_COMPANY1 FOREIGN KEY (companyid) REFERENCES zapp_company (companyid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_ZAPP_ORGANTASK_ZARCH_TASK1 FOREIGN KEY (taskid) REFERENCES zarch_task (taskid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
tablespace ts_nadi4_data ;

comment on column zapp_organtask.organtaskid is '조직 업무 아이디 - hash(기관아이디 + (부서아이디) + 업무아이디 + 대상유형)';
comment on column zapp_organtask.companyid is '기관 아이디';
comment on column zapp_organtask.deptid is '부서아이디';
comment on column zapp_organtask.taskid is '업무 아이디';
comment on column zapp_organtask.objtype is '대상유형 (01:기관, 02:부서, ...)';



create table zapp_tempobject
(	
  tmpobjid char(64) primary key,
  objid char(64) not null,
  objtype varchar(2) not null,
  holderid char(64) not null,
  retentionid char(64) not null,
  expiretime varchar(45) null,
  tmptime varchar(25) not null,
  acls json null,
  classes json null,
  files json null,
  CONSTRAINT fk_ZAPP_MFILE_ZAPP_CODE1 FOREIGN KEY (retentionid) REFERENCES zapp_code (codeid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
tablespace ts_nadi4_data ;

comment on column zapp_tempobject.objtype is '01:번들, 02:파일, 03:노드';


create table zapp_mfile
(	
  mfileid varchar(64) primary key,
  fno varchar(45) not null,
  retentionid varchar(64) not null,
  expiretime varchar(25) null,
  holderid varchar(64) not null,
  CONSTRAINT fk_ZAPP_MFILE_ZAPP_CODE2 FOREIGN KEY (retentionid) REFERENCES zapp_code (codeid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
tablespace ts_nadi4_data ;



create table zarch_cabinet
(	
  cabinetid varchar(64) primary key,
  name varchar(100) not null,
  descpt varchar(100) null,
  maxcapacity float not null,
  mountpath varchar(4000) not null,
  seq int4 not null default 1,
  state char(1) not null
)
tablespace ts_nadi4_data ;


create table zarch_taskcabinet
(	
  taskid varchar(64) primary key,
  cabinetid varchar(64) not null,
  CONSTRAINT fk_table1_ZARCH_TASK1 FOREIGN KEY (taskid) REFERENCES zarch_task (taskid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_table1_ZARCH_CABINET1 FOREIGN KEY (cabinetid) REFERENCES zarch_cabinet (cabinetid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
  
)
tablespace ts_nadi4_data ;


create table zarch_filelog
(	
  filelogid char(64) primary key,
  logs text not null,
  logtime varchar(25) not null,
  loggerid char(64) not null,
  loggername varchar(100) not null,
  logtype varchar(2) not null
)
tablespace ts_nadi4_data ;


create table zarch_mfile
(	
  mfileid varchar(64) primary key,
  linkid varchar(64) null,
  filename varchar(4000) not null,
  seq int not null,
  creator varchar(64) not null,
  createtime varchar(25) not null,
  updatetime varchar(25) null,
  state char(1) not null
)
tablespace ts_nadi4_data ;


create table zarch_format
(	
  formatid char(64) primary key,
  name varchar(100) not null,
  descpt varchar(100) null,
  code varchar(30) not null,
  mxsize float null,
  ext varchar(10) not null,
  icon varchar(50) null
)
tablespace ts_nadi4_data ;


create table zarch_ufile
(	
  ufileid varchar(64) primary key,
  cabinetid varchar(64) not null,
  formatid varchar(64) not null,
  hashid varchar(64) not null,
  filesize float not null,
  createtime varchar(25) not null,
  isencrypted char(1) not null default 'Y',
  CONSTRAINT fk_ZARCH_UFILE_ZARCH_FORMAT1 FOREIGN KEY (formatid) REFERENCES zarch_format (formatid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_ZARCH_UFILE_ZARCH_CABINET1 FOREIGN KEY (cabinetid) REFERENCES zarch_cabinet (cabinetid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
  
)
tablespace ts_nadi4_data ;



create table zarch_version
(	
  versionid varchar(64) primary key,
  mfileid varchar(64) not null,
  ufileid varchar(64) not null,
  hver int4 not null default 1,
  lver int4 not null default 0,
  creator varchar(50) not null,
  createtime varchar(4000) not null,
  CONSTRAINT fk_ZARCH_VERSION_ZARCH_MFILE1 FOREIGN KEY (mfileid) REFERENCES zarch_mfile (mfileid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_ZARCH_VERSION_ZARCH_UFILE1 FOREIGN KEY (ufileid) REFERENCES zarch_ufile (ufileid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
  
)
tablespace ts_nadi4_data ;

create table zapp_aprovpath
(	
  aprovpathid varchar(64) primary key,
  aprovpathname varchar(150) not null,
  aprovpathtypeid varchar(45) not null,
  aprovpathtype char(1) not null,
  isused char(1) not null default 'Y'
)
tablespace ts_nadi4_data ;

create table zapp_approver
(	
  pathapproverid varchar(64) primary key,
  aprovpathid varchar(64) not null,
  approverid varchar(64) not null,
  aprovorder int4 not null default 1,
  approvertype varchar(2) not null,
  isarbitrary char(1) not null default 'N',
  titleid varchar(64) not null,
  isused char(1) not null default 'Y',
  CONSTRAINT fk_ZAPP_APPROVER_ZAPP_APROVPATH1 FOREIGN KEY (aprovpathid) REFERENCES zapp_aprovpath (aprovpathid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
tablespace ts_nadi4_data ;


create table zapp_aprovdoc
(	
  aprovdocid varchar(64) primary key,
  pathapproverid varchar(64) not null,
  bundleid varchar(64) not null,
  aprovtype char(1) not null,
  CONSTRAINT fk_ZAPP_APROVDOC_ZAPP_APPROVER1 FOREIGN KEY (pathapproverid) REFERENCES zapp_approver (pathapproverid)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
tablespace ts_nadi4_data ;


create table zapp_aprovdoctrace
(	
  aprovdoctraceid varchar(64) primary key,
  txid varchar(64) not null,
  versionid varchar(64) null,
  objid varchar(64) not null,
  approvername varchar(100) not null,
  aprovorder int4 not null,
  aprovreason text null,
  revisedinfo text null,
  title varchar(150) not null,
  whenapproved varchar(25) not null,
  aprovstate varchar(2) not null
)
tablespace ts_nadi4_data ;
------------------------------------------------------------------------------------------------------
