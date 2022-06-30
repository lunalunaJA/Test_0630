/*
  [Code]
*/

/* Korean */
INSERT INTO zapp_code(codeid, companyid, name, codevalue, upid, types, codekey, priority, isactive) values 
(get_sha256('DEFAULT' || 'ROOT' || '01' || 'ENV' || 'ENV'),'DEFAULT','환경설정','ENV','ROOT','01','ENV', 1,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '02' || 'POSITION' || 'POSITION'),'DEFAULT','직위','POSITION','ROOT','02','POSITION', 2,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '03' || 'DUTY' || 'DUTY'),'DEFAULT','직무','DUTY','ROOT','03','DUTY', 3,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '04' || 'SECURITY' || 'SECURITY'),'DEFAULT','보안등급','SECURITY','ROOT','04','DUTY', 4,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '05' || 'RETENTION' || 'RETENTION'),'DEFAULT','보존기간','RETENTION','ROOT','05','RETENTION', 5,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '06' || 'NODEACL' || 'NODEACL'),'DEFAULT','분류권한','NODEACL','ROOT','06','NODEACL', 6,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '07' || 'CONTACL' || 'CONTACL'),'DEFAULT','컨텐츠권한','CONTACL','ROOT','07','CONTACL', 7,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '08' || 'USERTYPE' || 'USERTYPE'),'DEFAULT','사용자유형','USERTYPE','ROOT','08','USERTYPE', 8,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '09' || 'GROUPTYPE' || 'GROUPTYPE'),'DEFAULT','그룹유형','GROUPTYPE','ROOT','09','GROUPTYPE', 9,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '10' || 'CLASTYPE' || 'CLASTYPE'),'DEFAULT','분류유형','CLASTYPE','ROOT','10','CLASTYPE', 10,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '11' || 'OBJECTTYPE' || 'OBJECTTYPE'),'DEFAULT','권한대상유형','OBJECTTYPE','ROOT','11','OBJECTTYPE', 11,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '12' || 'CONTENTTYPE' || 'CONTENTTYPE'),'DEFAULT','컨텐츠유형','CONTENTTYPE','ROOT','12','CONTENTTYPE', 12,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '13' || 'PAY' || 'PAY'),'DEFAULT','급여','PAY','ROOT','13','PAY', 13,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '99' || 'UNKNOWN' || 'UNKNOWN'),'DEFAULT','미지정','UNKNOWN','ROOT','99','UNKNOWN', 99,'Y') 
,
(get_sha256('DEFAULT' || '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848' || '07' || '0' || 'CONTACL'), 'DEFAULT', '접근불가', '0', '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848', '07', 'CONTACL', 1, 'Y')
,
(get_sha256('DEFAULT' || '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848' || '07' || '1' || 'CONTACL'), 'DEFAULT', '목록', '1', '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848', '07', 'CONTACL', 2, 'Y')
,
(get_sha256('DEFAULT' || '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848' || '07' || '2' || 'CONTACL'), 'DEFAULT', '열람', '2', '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848', '07', 'CONTACL', 3, 'Y')
,
(get_sha256('DEFAULT' || '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848' || '07' || '4' || 'CONTACL'), 'DEFAULT', '인쇄', '3', '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848', '07', 'CONTACL', 4, 'Y')
, 
(get_sha256('DEFAULT' || '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848' || '07' || '3' || 'CONTACL'), 'DEFAULT', '다운로드', '4', '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848', '07', 'CONTACL', 4, 'Y')
,
(get_sha256('DEFAULT' || '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848' || '07' || '5' || 'CONTACL'), 'DEFAULT', '편집', '5', '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848', '07', 'CONTACL', 5, 'Y')
,
(get_sha256('DEFAULT' || '1D26246137AB02ADF2C8E33DC50D6198CC9F8509A2468F23A2218C2AAD107305' || '06' || '0' || 'NODEACL'), 'DEFAULT', '조회불가 + 등록불가', '0', '1D26246137AB02ADF2C8E33DC50D6198CC9F8509A2468F23A2218C2AAD107305', '06', 'NODEACL', 1, 'Y')
,
(get_sha256('DEFAULT' || '1D26246137AB02ADF2C8E33DC50D6198CC9F8509A2468F23A2218C2AAD107305' || '06' || '1' || 'NODEACL'), 'DEFAULT', '조회가능 + 등록불가', '1', '1D26246137AB02ADF2C8E33DC50D6198CC9F8509A2468F23A2218C2AAD107305', '06', 'NODEACL', 2, 'Y')
,
(get_sha256('DEFAULT' || '1D26246137AB02ADF2C8E33DC50D6198CC9F8509A2468F23A2218C2AAD107305' || '06' || '2' || 'NODEACL'), 'DEFAULT', '조회가능 + 등록가능', '2', '1D26246137AB02ADF2C8E33DC50D6198CC9F8509A2468F23A2218C2AAD107305', '06', 'NODEACL', 3, 'Y')
;

/* English */
INSERT INTO zapp_code(codeid, companyid, name, codevalue, upid, types, codekey, priority, isactive) values 
(get_sha256('DEFAULT' || 'ROOT' || '01' || 'ENV' || 'ENV'),'DEFAULT','Preferences','ENV','ROOT','01','ENV', 1,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '02' || 'POSITION' || 'POSITION'),'DEFAULT','Position','POSITION','ROOT','02','POSITION', 2,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '03' || 'DUTY' || 'DUTY'),'DEFAULT','Duty','DUTY','ROOT','03','DUTY', 3,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '04' || 'SECURITY' || 'SECURITY'),'DEFAULT','Security Level','SECURITY','ROOT','04','DUTY', 4,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '05' || 'RETENTION' || 'RETENTION'),'DEFAULT','Retention Period','RETENTION','ROOT','05','RETENTION', 5,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '06' || 'NODEACL' || 'NODEACL'),'DEFAULT','Classification Access Permission','NODEACL','ROOT','06','NODEACL', 6,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '07' || 'CONTACL' || 'CONTACL'),'DEFAULT','Content Access Permission','CONTACL','ROOT','07','CONTACL', 7,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '08' || 'USERTYPE' || 'USERTYPE'),'DEFAULT','User Type','USERTYPE','ROOT','08','USERTYPE', 8,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '09' || 'GROUPTYPE' || 'GROUPTYPE'),'DEFAULT','Group Type','GROUPTYPE','ROOT','09','GROUPTYPE', 9,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '10' || 'CLASTYPE' || 'CLASTYPE'),'DEFAULT','Classification Type','CLASTYPE','ROOT','10','CLASTYPE', 10,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '11' || 'OBJECTTYPE' || 'OBJECTTYPE'),'DEFAULT','Organization type','OBJECTTYPE','ROOT','11','OBJECTTYPE', 11,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '12' || 'CONTENTTYPE' || 'CONTENTTYPE'),'DEFAULT','Content Type','CONTENTTYPE','ROOT','12','CONTENTTYPE', 12,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '13' || 'PAY' || 'PAY'),'DEFAULT','Salary','PAY','ROOT','13','PAY', 13,'Y') 
,
(get_sha256('DEFAULT' || 'ROOT' || '99' || 'UNKNOWN' || 'UNKNOWN'),'DEFAULT','Unknown','UNKNOWN','ROOT','99','UNKNOWN', 99,'Y') 
,
(get_sha256('DEFAULT' || '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848' || '07' || '0' || 'CONTACL'), 'DEFAULT', 'No Access', '0', '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848', '07', 'CONTACL', 1, 'Y')
,
(get_sha256('DEFAULT' || '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848' || '07' || '1' || 'CONTACL'), 'DEFAULT', 'List', '1', '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848', '07', 'CONTACL', 2, 'Y')
,
(get_sha256('DEFAULT' || '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848' || '07' || '2' || 'CONTACL'), 'DEFAULT', 'View', '2', '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848', '07', 'CONTACL', 3, 'Y')
, 
(get_sha256('DEFAULT' || '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848' || '07' || '3' || 'CONTACL'), 'DEFAULT', 'Print', '3', '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848', '07', 'CONTACL', 4, 'Y')
,
(get_sha256('DEFAULT' || '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848' || '07' || '4' || 'CONTACL'), 'DEFAULT', 'Download', '4', '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848', '07', 'CONTACL', 4, 'Y')
,
(get_sha256('DEFAULT' || '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848' || '07' || '5' || 'CONTACL'), 'DEFAULT', 'Edit', '5', '6B193A7BDA845C932BFC1D6547AE3964BEA87A5955A290C4D6BFD315532AD848', '07', 'CONTACL', 5, 'Y')
,
(get_sha256('DEFAULT' || '1D26246137AB02ADF2C8E33DC50D6198CC9F8509A2468F23A2218C2AAD107305' || '06' || '0' || 'NODEACL'), 'DEFAULT', 'Not Viewable + Not Creatable', '0', '1D26246137AB02ADF2C8E33DC50D6198CC9F8509A2468F23A2218C2AAD107305', '06', 'NODEACL', 1, 'Y')
,
(get_sha256('DEFAULT' || '1D26246137AB02ADF2C8E33DC50D6198CC9F8509A2468F23A2218C2AAD107305' || '06' || '1' || 'NODEACL'), 'DEFAULT', 'Viewable + Not Creatable', '1', '1D26246137AB02ADF2C8E33DC50D6198CC9F8509A2468F23A2218C2AAD107305', '06', 'NODEACL', 2, 'Y')
,
(get_sha256('DEFAULT' || '1D26246137AB02ADF2C8E33DC50D6198CC9F8509A2468F23A2218C2AAD107305' || '06' || '2' || 'NODEACL'), 'DEFAULT', 'Viewable + Creatable', '2', '1D26246137AB02ADF2C8E33DC50D6198CC9F8509A2468F23A2218C2AAD107305', '06', 'NODEACL', 3, 'Y')
;

/*
  [Preference]
*/

/* Korean */
INSERT INTO zapp_env(envid, companyid, name, setval, envtype, settype, setopt, editable, envkey, isactive) values 
   (get_sha256('DEFAULT' || '01' || 'SYS_APPROVAL_YN'),'DEFAULT','승인적용여부','N','01','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','N','SYS_APPROVAL_YN','Y')
,  (get_sha256('DEFAULT' || '01' || 'SYS_APPROVAL_ADD_YN'),'DEFAULT','등록 승인 여부','N','01','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','N','SYS_APPROVAL_ADD_YN','Y')
,  (get_sha256('DEFAULT' || '01' || 'SYS_APPROVAL_VIEW_YN'),'DEFAULT','조회 승인 여부','N','01','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','N','SYS_APPROVAL_VIEW_YN','Y')
,  (get_sha256('DEFAULT' || '01' || 'SYS_APPROVAL_CHANGE_YN'),'DEFAULT','편집 승인 여부','N','01','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','N','SYS_APPROVAL_CHANGE_YN','Y')
,  (get_sha256('DEFAULT' || '01' || 'SYS_APPROVAL_DISABLE_YN'),'DEFAULT','삭제 승인 여부','N','01','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','N','SYS_APPROVAL_DISABLE_YN','Y')
,  (get_sha256('DEFAULT' || '01' || 'SYS_APPROVAL_DISCARD_YN'),'DEFAULT','폐기 승인 여부','N','01','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','N','SYS_APPROVAL_DISCARD_YN','Y')
,  (get_sha256('DEFAULT' || '01' || 'SYS_APPROVAL_REPLICATE_YN'),'DEFAULT','복사 승인 여부','N','01','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','N','SYS_APPROVAL_REPLICATE_YN','Y')
,  (get_sha256('DEFAULT' || '01' || 'SYS_APPROVAL_RELOCATE_YN'),'DEFAULT','이동 승인 여부','N','01','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','N','SYS_APPROVAL_RELOCATE_YN','Y')
,  (get_sha256('DEFAULT' || '01' || 'SYS_APPROVAL_ADD_COMPANYNODE_YN'),'DEFAULT','전사문서함등록 승인처리여부','Y','01','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','Y','SYS_APPROVAL_ADD_COMPANYNODE_YN','Y')
,  (get_sha256('DEFAULT' || '02' || 'SYS_CONTENTACL_YN'),'DEFAULT','컨텐츠권한적용여부','Y','02','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','N','SYS_CONTENTACL_YN','Y')
,  (get_sha256('DEFAULT' || '02' || 'SYS_CLASSACL_YN'),'DEFAULT','분류권한적용여부','Y','02','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','N','SYS_CLASSACL_YN','Y')
,  (get_sha256('DEFAULT' || '02' || 'SYS_ACL_INHERIT_YN'),'DEFAULT','권한상속여부','Y','02','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','N','SYS_ACL_INHERIT_YN','Y')
,  (get_sha256('DEFAULT' || '02' || 'SYS_DEPT_RANGE'),'DEFAULT','부서권한적용범위','2','02','2','{\\"1\\":\\"접속부서\\",\\"2\\":\\"소속부서\\",\\"3\\":\\"소속부서하위전체\\"}','Y','SYS_DEPT_RANGE','Y')
,  (get_sha256('DEFAULT' || '03' || 'SYS_REMOTE_YN'),'DEFAULT','원격여부','N','03','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','N','SYS_REMOTE_YN','Y')
,  (get_sha256('DEFAULT' || '03' || 'SYS_ENCRYPTION_YN'),'DEFAULT','암호화여부','Y','03','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','N','SYS_ENCRYPTION_YN','Y')
,  (get_sha256('DEFAULT' || '03' || 'SYS_CHECKFORMAT_YN'),'DEFAULT','파일형식체크여부','N','03','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','Y','SYS_CHECKFORMAT_YN','Y')
,  (get_sha256('DEFAULT' || '04' || 'SYS_CAN_REG_DOC_TO_COMPANY_NODE'),'DEFAULT','전사노드등록가능여부','Y','04','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','Y','SYS_CAN_REG_DOC_TO_COMPANY_NODE','Y')
,  (get_sha256('DEFAULT' || '05' || 'SYS_VERSION_YN'),'DEFAULT','버전적용여부','Y','05','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','Y','SYS_VERSION_YN','Y')
,  (get_sha256('DEFAULT' || '05' || 'SYS_VERSION_UPONLYHIGH_YN'),'DEFAULT','상위버전 up 여부','Y','05','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','N','SYS_VERSION_UPONLYHIGH_YN','Y')
,  (get_sha256('DEFAULT' || '05' || 'SYS_VERSION_UPWITHNOSAMEHASH_YN'),'DEFAULT','동일 파일 해쉬값에 따라 버전업 여부','Y','05','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','N','SYS_VERSION_UPWITHNOSAMEHASH_YN','Y')
,  (get_sha256('DEFAULT' || '06' || 'SYS_DOC_INTEGRITY'),'DEFAULT','문서 무결성 항목','1','06','2','{\\"1\\":\\"PK\\",\\"2\\":\\"문서번호\\"}','N','SYS_DOC_INTEGRITY','Y')
,  (get_sha256('DEFAULT' || '06' || 'SYS_DOC_CONTENTNO_RULE'),'DEFAULT','컨텐츠번호생성룰','1','06','2','{\\"1\\":\\"기관\\",\\"2\\":\\"부서\\",\\"3\\":\\"폴더\\"}','N','SYS_DOC_CONTENTNO_RULE','Y')
,  (get_sha256('DEFAULT' || '07' || 'SYS_LIST_QUERY_OBJECT'),'DEFAULT','목록 조회 유형 (B: Bundle, F:File, A:All)','A','07','2','{\\"A\\":\\"전체\\",\\"B\\":\\"번들\\",\\"F\\":\\"파일\\"}','Y','SYS_LIST_QUERY_OBJECT','Y')
,  (get_sha256('DEFAULT' || '07' || 'SYS_LIST_RECENT_DAY'),'DEFAULT','최근 목록 조회 일자','07','7','1','-','Y','SYS_LIST_RECENT_DAY','Y')
,  (get_sha256('DEFAULT' || '07' || 'SYS_LIST_CNT_PER_PAGE'),'DEFAULT','페이지당 목록 수','10','07','1','-','Y','SYS_LIST_CNT_PER_PAGE','Y')
,  (get_sha256('DEFAULT' || '08' || 'SYS_ALLOW_JSON'),'DEFAULT','JSON 허용 여부','Y','08','2','{\\"Y\\":\\"적용함\\",\\"N\\":\\"적용안함\\"}','Y','SYS_ALLOW_JSON','Y')
,  (get_sha256('DEFAULT' || '09' || 'SYS_LOG_ACCESS_YN'),'DEFAULT','접근 로그 기록 여부','Y','09','2','{\\"Y\\":\\"적용함\\",\\"N\\":\\"적용안함\\"}','Y','SYS_LOG_ACCESS_YN','Y')
,  (get_sha256('DEFAULT' || '09' || 'SYS_LOG_CONTENT_YN'),'DEFAULT','컨텐츠 로그 기록 여부','Y','09','2','{\\"Y\\":\\"적용함\\",\\"N\\":\\"적용안함\\"}','Y','SYS_LOG_CONTENT_YN','Y')
,  (get_sha256('DEFAULT' || '09' || 'SYS_LOG_SYSTEM_YN'),'DEFAULT','시스템 로그 기록 여부','Y','09','2','{\\"Y\\":\\"적용함\\",\\"N\\":\\"적용안함\\"}','Y','SYS_LOG_SYSTEM_YN','Y')
,  (get_sha256('DEFAULT' || '10' || 'SYS_MAIL_SEND_APPROVAL_YN'),'DEFAULT','승인 메일 송신 여부','N','10','2','{\\"Y\\":\\"적용함\\",\\"N\\":\\"적용안함\\"}','Y','SYS_MAIL_SEND_APPROVAL_YN','Y')
,  (get_sha256('DEFAULT' || '11' || 'SYS_CYCLE_STATUS_BUILD_TYPE'),'DEFAULT','통계정보생성유형','B','11','2','{\\"B\\":\\"Batch\\",\\"R\\":\\"Real-Time\\"}','N','SYS_CYCLE_STATUS_BUILD_TYPE','Y')
,  (get_sha256('DEFAULT' || '11' || 'SYS_CYCLE_STATUS_DISCARD_EXPIRED_YN'),'DEFAULT','만기문서자동폐기여부','Y','11','2','{\\"Y\\":\\"사용\\",\\"N\\":\\"사용안함\\"}','Y','SYS_CYCLE_STATUS_DISCARD_EXPIRED_YN','Y')
,  (get_sha256('DEFAULT' || '98' || 'SYS_SESSION_TIME'),'DEFAULT','세션 유지 시간 (ms)','3600','98','1','-','Y','SYS_SESSION_TIME','Y')
,  (get_sha256('DEFAULT' || '99' || 'SYS_MOBILE_YN'),'DEFAULT','모바일사용여부','N','99','2','{\\"Y\\":\\"적용함\\",\\"N\\":\\"적용안함\\"}','Y','SYS_MOBILE_YN','Y')
;

/* English */
INSERT INTO zapp_env(envid, companyid, name, setval, envtype, settype, setopt, editable, envkey, isactive) values 
   (get_sha256('DEFAULT' || '01' || 'SYS_APPROVAL_YN'),'DEFAULT','Apply approval for creating company folder or not','Y','01','2','{\\"Y\\":\\"Use\\",\\"N\\":\\"Not Use\\"}','Y','SYS_APPROVAL_ADD_COMPANYNODE_YN','Y')
,  (get_sha256('DEFAULT' || '01' || 'SYS_APPROVAL_ADD_YN'),'DEFAULT','Apply create approval or not','N','01','2','{\\"Y\\":\\"Use\\",\\"N\\":\\"Not Use\\"}','N','SYS_APPROVAL_ADD_YN','Y')
,  (get_sha256('DEFAULT' || '01' || 'SYS_APPROVAL_VIEW_YN'),'DEFAULT','Apply view approval or not','N','01','2','{\\"Y\\":\\"Use\\",\\"N\\":\\"Not Use\\"}','N','SYS_APPROVAL_VIEW_YN','Y')
,  (get_sha256('DEFAULT' || '01' || 'SYS_APPROVAL_CHANGE_YN'),'DEFAULT','Apply edit approval or not','N','01','2','{\\"Y\\":\\"Use\\",\\"N\\":\\"Not Use\\"}','N','SYS_APPROVAL_CHANGE_YN','Y')
,  (get_sha256('DEFAULT' || '01' || 'SYS_APPROVAL_DISABLE_YN'),'DEFAULT','Apply delete approval or not','N','01','2','{\\"Y\\":\\"Use\\",\\"N\\":\\"Not Use\\"}','N','SYS_APPROVAL_DISABLE_YN','Y')
,  (get_sha256('DEFAULT' || '01' || 'SYS_APPROVAL_DISCARD_YN'),'DEFAULT','Apply discard approval or not','N','01','2','{\\"Y\\":\\"Use\\",\\"N\\":\\"Not Use\\"}','N','SYS_APPROVAL_DISCARD_YN','Y')
,  (get_sha256('DEFAULT' || '01' || 'SYS_APPROVAL_REPLICATE_YN'),'DEFAULT','Apply copy approval or not','N','01','2','{\\"Y\\":\\"Use\\",\\"N\\":\\"Not Use\\"}','N','SYS_APPROVAL_REPLICATE_YN','Y')
,  (get_sha256('DEFAULT' || '01' || 'SYS_APPROVAL_RELOCATE_YN'),'DEFAULT','Apply move approval or not','N','01','2','{\\"Y\\":\\"Use\\",\\"N\\":\\"Not Use\\"}','N','SYS_APPROVAL_RELOCATE_YN','Y')
,  (get_sha256('DEFAULT' || '02' || 'SYS_CONTENTACL_YN'),'DEFAULT','Apply content access controls or not','Y','02','2','{\\"Y\\":\\"Use\\",\\"N\\":\\"Not Use\\"}','N','SYS_CONTENTACL_YN','Y')
,  (get_sha256('DEFAULT' || '02' || 'SYS_ACL_INHERIT_YN'),'DEFAULT','Apply access control inheritance or not','Y','02','2','{\\"Y\\":\\"Use\\",\\"N\\":\\"Not Use\\"}','N','SYS_ACL_INHERIT_YN','Y')
,  (get_sha256('DEFAULT' || '03' || 'SYS_REMOTE_YN'),'DEFAULT','File location is remote or not','N','03','2','{\\"Y\\":\\"Use\\",\\"N\\":\\"Not Use\\"}','N','SYS_REMOTE_YN','Y')
,  (get_sha256('DEFAULT' || '03' || 'SYS_ENCRYPTION_YN'),'DEFAULT','Apply encryption or not','Y','03','2','{\\"Y\\":\\"Use\\",\\"N\\":\\"Not Use\\"}','N','SYS_ENCRYPTION_YN','Y')
,  (get_sha256('DEFAULT' || '05' || 'SYS_VERSION_YN'),'DEFAULT','Apply version or not','Y','05','2','{\\"Y\\":\\"Use\\",\\"N\\":\\"Not Use\\"}','Y','SYS_VERSION_YN','Y')
,  (get_sha256('DEFAULT' || '05' || 'SYS_VERSION_UPONLYHIGH_YN'),'DEFAULT','Apply major version-up or not','Y','05','2','{\\"Y\\":\\"Use\\",\\"N\\":\\"Not Use\\"}','N','SYS_VERSION_UPONLYHIGH_YN','Y')
,  (get_sha256('DEFAULT' || '05' || 'SYS_VERSION_UPWITHNOSAMEHASH_YN'),'DEFAULT','Apply same hash file version-up or not','Y','05','2','{\\"Y\\":\\"Use\\",\\"N\\":\\"Not Use\\"}','N','SYS_VERSION_UPWITHNOSAMEHASH_YN','Y')
,  (get_sha256('DEFAULT' || '06' || 'SYS_DOC_INTEGRITY'),'DEFAULT','Rules for generating content key','1','06','2','{\\"1\\":\\"PK\\",\\"2\\":\\"Content No.\\"}','N','SYS_DOC_INTEGRITY','Y')
,  (get_sha256('DEFAULT' || '06' || 'SYS_DOC_CONTENTNO_RULE'),'DEFAULT','Rules for generating content no.','1','06','2','{\\"1\\":\\"Company\\",\\"2\\":\\"Dept.\\",\\"3\\":\\"Folder\\"}','N','SYS_DOC_CONTENTNO_RULE','Y')
,  (get_sha256('DEFAULT' || '07' || 'SYS_LIST_RECENT_DAY'),'DEFAULT','Period for recently registered contents','7','07','1','-','Y','SYS_LIST_RECENT_DAY','Y')
,  (get_sha256('DEFAULT' || '07' || 'SYS_LIST_CNT_PER_PAGE'),'DEFAULT','No. of searched data per list','10','07','1','-','Y','SYS_LIST_CNT_PER_PAGE','Y')
,  (get_sha256('DEFAULT' || '08' || 'SYS_ALLOW_JSON'),'DEFAULT','JSON allowed or not','Y','08','2','{\\"Y\\":\\"Apply\\",\\"N\\":\\"Not Apply\\"}','Y','SYS_ALLOW_JSON','Y')
,  (get_sha256('DEFAULT' || '09' || 'SYS_LOG_ACCESS_YN'),'DEFAULT','Apply access log or not','Y','09','2','{\\"Y\\":\\"Apply\\",\\"N\\":\\"Not Apply\\"}','Y','SYS_LOG_ACCESS_YN','Y')
,  (get_sha256('DEFAULT' || '09' || 'SYS_LOG_CONTENT_YN'),'DEFAULT','Apply content log or not','Y','09','2','{\\"Y\\":\\"Apply\\",\\"N\\":\\"Not Apply\\"}','Y','SYS_LOG_CONTENT_YN','Y')
,  (get_sha256('DEFAULT' || '09' || 'SYS_LOG_SYSTEM_YN'),'DEFAULT','Apply system log or not','Y','09','2','{\\"Y\\":\\"Apply\\",\\"N\\":\\"Not Apply\\"}','Y','SYS_LOG_SYSTEM_YN','Y')
,  (get_sha256('DEFAULT' || '10' || 'SYS_MAIL_SEND_APPROVAL_YN'),'DEFAULT','Apply sending approval email or not','N','10','2','{\\"Y\\":\\"Apply\\",\\"N\\":\\"Not Apply\\"}','Y','SYS_MAIL_SEND_APPROVAL_YN','Y')
,  (get_sha256('DEFAULT' || '99' || 'SYS_MOBILE_YN'),'DEFAULT','Use mobile or not','N','99','2','{\\"Y\\":\\"Apply\\",\\"N\\":\\"Not Apply\\"}','Y','SYS_MOBILE_YN','Y')
,  (get_sha256('DEFAULT' || '07' || 'SYS_LIST_QUERY_OBJECT'),'DEFAULT','Data type for list (B: Bundle, F:File, A:All)','A','07','2','{\\"A\\":\\"All\\",\\"B\\":\\"Bundle\\",\\"F\\":\\"File\\"}','Y','SYS_LIST_QUERY_OBJECT','Y')
,  (get_sha256('DEFAULT' || '02' || 'SYS_DEPT_RANGE'),'DEFAULT','Department scope','2','02','2','{\\"1\\":\\"Connected Dept.\\",\\"2\\":\\"Belonged Dept.\\",\\"3\\":\\"All sub-dept of belonged dept.\\"}','Y','SYS_DEPT_RANGE','Y')
,  (get_sha256('DEFAULT' || '02' || 'SYS_CLASSACL_YN'),'DEFAULT','Apply classification access controls or not','Y','02','2','{\\"Y\\":\\"Use\\",\\"N\\":\\"Not Use\\"}','N','SYS_CLASSACL_YN','Y')
,  (get_sha256('DEFAULT' || '03' || 'SYS_CHECKFORMAT_YN'),'DEFAULT','Apply checking file format or not','N','03','2','{\\"Y\\":\\"Use\\",\\"N\\":\\"Not Use\\"}','Y','SYS_CHECKFORMAT_YN','Y')
,  (get_sha256('DEFAULT' || '11' || 'SYS_CYCLE_STATUS_BUILD_TYPE'),'DEFAULT','Statistics Generation Type','B','11','2','{\\"B\\":\\"Batch\\",\\"R\\":\\"Real-Time\\"}','N','SYS_CYCLE_STATUS_BUILD_TYPE','Y')
,  (get_sha256('DEFAULT' || '11' || 'SYS_CYCLE_STATUS_DISCARD_EXPIRED_YN'),'DEFAULT','Apply automatic discarding expired contents or not','Y','11','2','{\\"Y\\":\\"Use\\",\\"N\\":\\"Not Use\\"}','Y','SYS_CYCLE_STATUS_DISCARD_EXPIRED_YN','Y')
,  (get_sha256('DEFAULT' || '98' || 'SYS_SESSION_TIME'),'DEFAULT','The duration of the session (ms)','3600','98','1','-','Y','SYS_SESSION_TIME','Y')
,  (get_sha256('DEFAULT' || '04' || 'SYS_CAN_REG_DOC_TO_COMPANY_NODE'),'DEFAULT','Apply creating company folder or not','Y','04','2','{\\"Y\\":\\"Use\\",\\"N\\":\\"Not Use\\"}','Y','SYS_CAN_REG_DOC_TO_COMPANY_NODE','Y')
;

INSERT INTO public.zarch_format(formatid, name, descpt, code, mxsize, ext, icon) VALUES
('0AC8B624229A6F7DF96DA4B3ACBD3F528D8E4FFE378DA0588C139C609CAA974C','xls','','','1000000','.xls','icon'),
('0B60EEE4718D9A36D7890711D2BFA2F01DCD1300D78E257F1DEB68F4C2293849','hwp','','','1000000','.hwp','icon'),
('4AAD850E95EA1912D11E1202985BBC58C453BC7918C24B224F7291B16C991FBE','pdf','','','1000000','.pdf','icon'),
('4D93FF48AF01A688817B7C86660AD52D49505EBB5DB4939E79EFC1978B5B6117','doc','','','1000000','.doc','icon'),
('55FA1B84FEB9F53802842CF17CB2C4CDF333A24D17E38AB1F321E925F59728BD','ppt','','','1000000','.ppt','icon'),
('7C7A56025933AC99AFE9D85C9945B5F2BB4DF1FEC897B164E1E65AC9587AF3B3','xlsx','','','1000000','.xlsx','icon'),
('7D53D6E1FF4F1A259D76C293DCD7A779573771829258164EF84CC75A3848AE7F','docx','','','1000000','.docx','icon'),
('8EEBCB8144F208F17FA70A0DCA0AA2A444B031B27CC7847020A8CD4564FB77CF','hwp','','','1000000','.hwpx','icon'),
('8F8CBB7DCF46E0BC7D53265749A6C17D116093A6BA95E442764060C76FD4A86C','png','png','','1000000','.png','icon'),
('9DBF777E09638BAF76E3531B9F2CFBA2834F61D777F15B7200D0AD5D69F5E1D6','pptx','','','1000000','.pptx','icon'),
('C6FE6FBF33856EEC567A9ACD18AEB2CCE67E1B6BDDF8969F7A730F5E49E91EEB','txt','','','1000000','.txt','icon'),
('D912C70ACF20EDA7217A21A7AFBAC5409DFB279F2C003754FF7B64A9DB9A3667','jpg','','','1000000','.jpg','icon'),
('7F43C29FAC1A69EA15F7B2E740E64E952CF6D0327245F8744DC4636610C13588','jpeg','','','1000000','.jpeg','icon'),
('089DB924A535494A44EFA4E1AD97F5C533B27D9115F47F55CA1E6BCF826A7154','tif','','','1000000','.tif','icon'),
('23759997B3C59884DC4C0FF5320D6301B0E7F63BF0F6483A7B54D7D43BC5CCD1','unknown','unknown','\N','1000000','.unknown','icon')
;

