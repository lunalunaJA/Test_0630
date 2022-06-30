package com.zenithst.core.common.conts;

import com.zenithst.framework.conts.ZstFwConst;

/**  
* <pre>
* <b>
* 1) Description : Provides constants that can be used throughout the system. <br> 
* 2) History : <br>
*         - v1.0 / 2020.10.08 / khlee  / New
* 
* 3) Usage or Example : <br>
* 
*    ZappConts.ACTION.ADD
*    ...
*    
* Copyright (C) by ZENITHST All right reserved.
* </b>
* </pre>
*/

public class ZappConts extends ZstFwConst {

    /**
	 * <p><b>
	 * [OUT] Processing type
	 * </b></p>
	 * <p>
	 *   ex) ZappConts.ACTION.ADD	
	 * </p>
     */
	public enum ACTION {
		/** Add */
		ADD,			// Add
		/** Edit (PK) */
		CHANGE_PK,		// Edit (PK)
		/** Edit */
		CHANGE,			// Edit
		/** Delete (PK) */
		DISABLE_PK,		// Delete (PK)
		/** Delete */
		DISABLE,		// Delete
		/** Recover (PK) */
		ENABLE_PK,		// Recover (PK)
		/** Recover */
		ENABLE,			// Recover
		/** Discard (PK) */
		DISCARD_PK,		// Discard (PK)
		/** Discard (VERSION) */
		DISCARD_VERSION,		// Discard (VERSION)
		/** Discard */
		DISCARD,		// Discard
		/** Inquire (PK) */
		VIEW_PK,		// Inquire (PK)
		/** Inquire */
		VIEW,			// Inquire
		/** Copy */
		REPLICATE,		// Copy
		/** Move */
		RELOCATE, 		// Move
		/** Reorder */
		REARRANGE,		// Reorder
		/** Lock */
		LOCK,			// Lock
		/** Unlock */
		UNLOCK,	// Unlock
		/** Share */
		SHARE,			// Share
		/** Unshare */
		UNSHARE,	// Unshare
		
		/** List */
		LIST,			// List
		/** Version-up */
		VERSION_UP,
		/** Ignore */
		IGNORE,
		/** Mark */
		MARK,
		/** Unmark */
		UNMARK,
		/** Approve */
		APPROVE,
		/** Return */
		RETURN,
		/* Withdraw */
		WITHDRAW,
		
		/** Access Control List */
		ACL,
		/** Approve */
		APPROVAL,
		
		/** Connect (Web) */
		CONNECT_WEB,
		/** Connect (CS)  */
		CONNECT_CS,
		/** Disconnect (Web) */
		DISCONNECT_WEB,
		/** Disconnect (CS) */
		DISCONNECT_CS,
		/** Check connection (Web) */
		CHECK_CONNECT_WEB,
		/** Check connection (CS) */
		CHECK_CONNECT_CS, 
		/** Check mutiple dept. */
		CHECK_MULTI_DEPTS
	}
	
    /**
	 * <p><b>
	 * [OUT] Authentication
	 * </b></p>
	 * <p>
	 *   ex) ZappConts.AUTHENTICATION.JWT_TYPE.auth	
	 * </p>
     */
	public enum AUTHENTICATION {
		
		/** JSON Web Token Type */
		JWT_TYPE("typ", "JWT"),
		/** JSON Web Token Algorithm */
		JWT_ALGORITHM("alg", "HS256"),
		
		/** Accessor info. */
		ACCESSOR_INFO("accessorInfo", ""),
		/** Accessor location info. */
		ACCESSOR_POINT("accessorPoint", ""),
		/** License info. */
		ACCESSOR_LICENSE("licenseInfo", ""),
		
		/** Access path (Web) */
		ACCESSPATH_WEB("W", ""),
		/** Access path (CS) */
		ACCESSPATH_CS("C", "")
		
		;
		
		public final String auth;
		public final String comment;
		
		AUTHENTICATION(String auth, String comment) {
			this.auth = auth;
			this.comment = comment;
		}
	}		
	
    /**
	 * <p><b>
	 * [OUT] Alias
	 * </b></p>
	 * <p>
	 *   ex) ZappConts.ALIAS.ACCESSLOG.alias	
	 * </p>
     */
	public enum ALIAS {
		
		ACCESSLOG("ACCESSLOG", ""),
		ADDITORYBUNDLE("ADDBUNDLE", ""),
		APM("APM", ""),
		BUNDLE("BUNDLE", ""),
		ADDITORYCLASS("ADDCLS", ""),
		CLASS("CLS", ""),
		CLASSACL("CLSACL", ""),
		CLASSOBJECT("CLSOBJ", ""),
		COMMENT("CMNT", ""),
		CODE("CODE", ""),
		COMPANY("COMPANY", ""),
		CYCLELOG("CYCLELOG", ""),
		DEPT("DEPT", ""),
		DEPTUSER("DEPTUSER", ""),
		ENV("ENV", ""),
		CONTENTACL("CONTENTACL", ""),
		CONTENTLOG("CONLOG", ""),
		CONWF("CONWF", ""),
		GROUP("GROUPS", ""),
		GROUPUSER("GROUPUSER", ""),
		KEYWORD("KW", ""),
		KEYWORDOBJECT("KWOBJ", ""),
		LINKEDOBJECT("LINKOBJ", ""),
		LOCKEDOBJECT("LOCKOBJ", ""),
		MARKEDOBJECT("MARKOBJ", ""),
		MFILE("ZAPP_MFILE", ""),
		ARCHMFILE("ZA_MFILE", ""),
		ORGANTASK("ORGANTASK", ""),
		SHAREDOBJECT("SHAREOBJ", ""),
		SYSTEMLOG("SYSLOG", ""),
		TAG("TAG", ""),
		TASKTAG("TASKTAG", ""),
		TMPOBJECT("TMPOBJ", ""),
		IMG("IMG", ""),
		USER("USERS", ""),
		WORKFLOW("WF", ""),
		WORKFLOWER("WFER", ""),
		WORKFLOWOBJECT("WFOBJ", "")
		;
		
		public final String alias;
		public final String comment;
		
		ALIAS(String alias, String comment) {
			this.alias = alias;
			this.comment = comment;
		}
	}	
	
	
    /**
	 * <p><b>
	 * [OUT] State
	 * </b></p>
	 * <p>
	 *   ex) ZappConts.STATES.BUNDLE_NORMAL.state	
	 * </p>
     */
	public enum STATES {
		
		/** Bundle(Normal) */
		BUNDLE_NORMAL("00", "Normal", "Y"),
		/** Bundle(Waiting for discarding) */
		BUNDLE_DISCARD_WAIT("01", "Waiting for discarding", "N"),
		/** Bundle(Locked) */
		BUNDLE_LOCK("03", "Lock", "Y"),
		/** Bundle(Request for edit) */
		BUNDLE_CHANGE_REQUEST("A0", "Request for edit", "Y"),
		/** Bundle(Request for deleting) */
		BUNDLE_DISABLE_REQUEST("A1", "Request for deleting", "Y"),
		/** Bundle(Request for recovering) */
		BUNDLE_ENABLE_REQUEST("A2", "Request for recovering", "Y"),
		/** Bundle(Request for moving) */
		BUNDLE_RELOCATE_REQUEST("A3", "Request for moving", "Y"),
		/** Bundle(Request for copying) */
		BUNDLE_REPLICATE_REQUEST("A4", "Request for copying", "Y"),
		/** Bundle(Request for lock) */
		BUNDLE_LOCK_REQUEST("A5", "Request for lock", "Y"),
		/** Bundle(Request for moving) */
		BUNDLE_RELOCATE_ADD_REQUEST("A6", "Request for moving", "Y"),		
		/** Bundle(Refuse to be edited) */
		BUNDLE_CHANGE_RETURN("C0", "Refuse to be edited", "Y"),
		/** Bundle(Refuse to be deleted) */
		BUNDLE_DISABLE_RETURN("C1", "Refuse to be deleted", "Y"),
		/** Bundle(Refuse to be recovered) */
		BUNDLE_ENABLE_RETURN("C2", "Refuse to be recovered", "Y"),
		/** Bundle(Refuse to be moved) */
		BUNDLE_RELOCATE_RETURN("C4", "Refuse to be moved", "Y"),
		/** Bundle(Refuse to be copied) */
		BUNDLE_REPLICATE_RETURN("C5", "Refuse to be copied", "Y"),
		/** Bundle(Refuse to be locked) */
		BUNDLE_LOCK_RETURN("C6", "Refuse to be locked", "Y"),
		
		/* Unable to inquire */
		/** Bundle(Request for registering) */
		BUNDLE_ADD_REQUEST("B1", "Request for registering", "N"),
		/** Bundle(Request for discarding) */
		BUNDLE_DISCARD_REQUEST("B2", "Request for discarding", "N"),
		/** Bundle(Request for registering) */
		BUNDLE_REPLICATE_ADD_REQUEST("B3", "Request for registering through replication", "N"),
		/** Bundle(Request for discarding) */
		BUNDLE_DISCARD_VERSION_REQUEST("B4", "Request for discarding a specific version file", "N"),
		/** Bundle(Refuse to be registered) */
		BUNDLE_ADD_RETURN("D1", "Refuse to be registered", "N"),
		/** Bundle(Refuse to be discarded) */
		BUNDLE_DISCARD_RETURN("D2", "Refuse to be discarded", "N"),
		/** Bundle(Refuse to be discarded) */
		BUNDLE_DISCARD_VERSION_RETURN("D3", "Refuse to be discarded", "N"),		
		/* Check file name */
		CHECK_FILE_ONLY_ADD("01", "", "N"),
		CHECK_FILE_ONLY_RENAME("02", "", "N"),
		CHECK_FILE_RENAME_VERSIONUP_WOFILE("03", "", "N"),
		CHECK_FILE_RENAME_VERSIONUP("04", "", "N"),
		
		;
		
		public final String state;
		public final String comment;
		public final String viewable;
		
		STATES(String state, String comment, String viewable) {
			this.state = state;
			this.comment = comment;
			this.viewable = viewable;
		}
	}	
	
    /**
	 * <p><b>
	 * [OUT] Type
	 * </b></p>
	 * <p>
	 *   ex) ZappConts.TYPES.CONTENT_NODE.type	
	 * </p>
     */
	public enum TYPES {
		
		/** LOG (Authentication) */
		LOG_ATHENTICATION("01", "Authentication"),
		/** LOG (Failed after trying to connect) */
		LOG_ACTION_TRY_CONNECT("02", "Failed after trying to connect"),
		/** LOG (Disconnect) */
		LOG_ACTION_DISCONNECT("03", "Disconnect"),
		
		/** Content type (Classification) */
		CONTENT_NODE("00", "Classification"),
		/** Content type (Bundle) */
		CONTENT_BUNDLE("01", "Bundle"),
		/** Content type (File) */
		CONTENT_FILE("02", "File"),
		
		/** Classification Type (Organization) */
		CLASS_DEPT("D1", "Department"),
		/** Classification Type (Group) */
		CLASS_GROUP("G1", "Group"),		
		/** Classification Type (General) */
		CLASS_FOLDER("01", "General folder"),
		/** Classification Type (Company) */
		CLASS_FOLDER_COMPANY("N1", "Company folder"),
		/** Classification Type (Department) */
		CLASS_FOLDER_DEPARTMENT("N2", "Department folder"),
		/** Classification Type (Personal) */
		CLASS_FOLDER_PERSONAL("N3", "Personal folder"),
		/** Classification Type (Cooperation) */
		CLASS_FOLDER_COLLABORATION("N4", "Cooperation"),
		/** Classification Type (Classification system) */		
		CLASS_CLASS("02", "Classification system"),
		/** Classification Type (Content type) */
		CLASS_DOCTYPE("03", "Content type"),
		

		/* Workflow */
		WFTYPE_USER("01", "User"),
		WFTYPE_SYSTEM("02", "System"),
		/* Workflower */
		WFERTYPE_APPROVAL("01", "Approval"),
		WFERTYPE_CIRCULATION("02", "Circulation"),
		
		/* Target type */
		/** Target type (User) */
		OBJTYPE_USER("01", "User"),
		/** Target type (Department) */
		OBJTYPE_DEPT("02", "Department"),
		/** Target type (Group) */
		OBJTYPE_GROUP("03", "Group"),
		
		/* Link */
		/** Link Type (Bundle) */
		LINK_BTOB("01", "-> Bundle"),
		/** Link Type (File) */
		LINK_BTOF("02", "-> File"),
		/** Link Type (Classification) */
		LINK_CTOC("03", "-> Classification"),
		
		/* List Processing type */
		/** List Type (None) */
		LIST_NONE("00", "NONE"),
		/** List Type (Classification) */
		LIST_CLASS("01", "Classification"),
		/** List Type (Link) */
		LIST_LINK("02", "Link"),
		/** List Type (Share) */
		LIST_SHARE("03", "Share"),
		/** List Type (Lock) */
		LIST_LOCK("04", "Lock"),
		/** List Type (Mark) */
		LIST_MARK("05", "Mark"),
		
		/** List Type (Recently registered) */
		LIST_RECENT_ADD("01", "Recently registered"),
		/** List Type (Registered by myself) */
		LIST_SELF_ADD("02", "Registered by myself"),
		/** List Type (Owned) */
		LIST_OWN("03", "Owned"),
		/** List Type (Recently edited) */
		LIST_RECENT_CHANGE("04", "Recently edited"),
		/** List Type (Coming to expiry) */
		LIST_COMING_EXPIRE("05", "Coming to expiry"),
		/** List Type (Expired) */
		LIST_EXPIRE("06", "Expired"),
		/** List Type (Deleted) */
		LIST_BIN("07", "Deleted"),
		/** List Type (Discarded) */
		LIST_DISCARD("08", "Discarded"),
		/** List Type (Expired for manager) */
		LIST_EXPIRE_ADMIN("09", "Expired"),
		/** List Type (Discarded for manager)*/
		LIST_DISCARD_ADMIN("10", "Discarded"),
		/** List Type (Requested) */
		LIST_APPROVAL_REQUESTED("11", "Requested by myself"),		
		/** List Type (Approved) */
		LIST_APPROVAL_APPROVED("12", "Approved by approvers"),		
		/** List Type (Returned) */
		LIST_APPROVAL_RETURNED("13", "Returned by approvers"),		
		/** List Type (Target for approval) */
		LIST_APPROVAL_OBJECT("14", "Target for approval"),		
		
		/* Result type */
		/** Result type (Count) */
		RESULT_COUNT("COUNT", "Count"),
		/** Result type (List) */
		RESULT_LIST("LIST", "List"),
		
		/** Code Type (Preferences) */
		CODE_ENV("01", "Preferences"),
		/** Code Type (Position) */
		CODE_POSITION("02", "Position"),
		/** Code Type (Duty) */
		CODE_DUTY("03", "Duty"),
		/** Code Type (Security level) */
		CODE_SECURITY("04", "Security level"),
		/** Code Type (Retention period) */
		CODE_RETENTION("05", "Retention period"),
		/** Code Type (Classification access control) */
		CODE_CLASSACL("06", "Classification access control"),
		/** Code Type (Content  access control) */
		CODE_CONTENTACL("07", "Content  access control"),
		/** Code Type (User type) */
		CODE_USERTYPE("08", "User type"),
		/** Code Type (Group type) */
		CODE_GROUPTYPE("09", "Group type"),
		/** Code Type (Classification type) */
		CODE_CLASTYPE("10", "Classification type"),
		/** Code Type (Right target type) */
		CODE_OBJECTTYPE("11", "Right target type"),
		/** Code Type (Content type) */
		CODE_CONTENTTYPE("12", "Content type"),
		/** Code Type (Unspecified) */
		CODE_UNKNOWN("99", "Unspecified"),
		
		/** User type (General) */
		USERTYPE_NORMAL("01", "General"),
		/** User type (Dept. Manager) */
		USERTYPE_DEPT("02", "Dept. Manager"),
		/** User type (Company Manager) */
		USERTYPE_COMPANY("03", "Company Manager"),
		/** User type (System Manager) */
		USERTYPE_SUPERVISOR("04", "System Manager"),
		
		/** Group Type (Company) */
		GROUPTYPE_COMPANY("01", "Company"),
		/** Group Type (Department) */
		GROUPTYPE_DEPT("02", "Department"),
		/** Group Type (Personal) */
		GROUPTYPE_PERSONAL("03", "Personal"),	// Upper ID (User ID)
		/** Group Type (Cooperation) */
		GROUPTYPE_COLLABORATION("04", "Cooperation"),
		/** Group Type (Workflow) */
		GROUPTYPE_WORKFLOW("05", "Workflow"),
		/** Group Type (All user) */
		GROUPTYPE_ALLUSER("98", "All user"),
		/** Group Type (Unlimited access) */
		GROUPTYPE_FREEACCESS("99", "Unlimited access"),
		
		// Inquire Type
		/** Inquire Type (Inquire) */
		VIEWTYPE_VIEW("01", "Inquire"),
		/** Inquire Type (Edit) */
		VIEWTYPE_EDIT("02", "Edit"),
		
		//
		STATUS_DAY("D", "By day"),
		STATUS_WEEK("W", "By week"),
		STATUS_MONTH("M", "By month"),
		STATUS_QUARTER("Q", "By quarter"),
		STATUS_HALF("H", "By half-year"),
		STATUS_YEAR("Y", "By year"),
		
		// Cycle Job Type
		/** Cycle Job Type (Batch) */
		JOBTYPE_BATCH("B", "Batch"),
		/** Cycle Job Type (Real-time) */
		JOBTYPE_REALTIME("R", "Real-time"),
		
		/* APM */
		APM_DB("01", ""),
		APM_DB_Lock("02", ""),
		APM_OS("03", ""),
		APM_DISK("04", ""),
		APM_CHECK("05", ""),
		
		/* Bundle Type */
		BUNDLE_NORMAL("01", "Normal"),
		BUNDLE_VIRTUAL("02", "Virtual"),
		
		/* Task */
		TASK_COMPANY("01", "Company"),
		TASK_DEPT("02", "Department")
		
		;
		
		public final String type;
		public final String comment;
		
		TYPES(String type, String comment) {
			this.type = type;
			this.comment = comment;
		}
	}	
	
	
    /**
	 * <p><b>
	 * [OUT] Preferences
	 * </b></p>
	 * <p>
	 *   ex) ZappConts.ENVS.APPROVAL_ADD_YN.env	
	 * </p>
     */
	public enum ENVS {

		// Approve (01)
		/** Approve (Approval or not) */
		APPROVAL_YN("SYS_APPROVAL_YN", "01", "Apply approval or not"),
			/** Approve (Approval for registering or not) */
			APPROVAL_ADD_YN("SYS_APPROVAL_ADD_YN", "01", "Apply create approval or not"),
			APPROVAL_VIEW_YN("SYS_APPROVAL_VIEW_YN", "01", "Apply view approval or not"),
			APPROVAL_CHANGE_YN("SYS_APPROVAL_CHANGE_YN", "01", "Apply edit approval or not"),
			APPROVAL_DISABLE_YN("SYS_APPROVAL_DISABLE_YN", "01", "Apply delete approval or not"),
			APPROVAL_DISCARD_YN("SYS_APPROVAL_DISCARD_YN", "01", "Apply discard approval or not"),
			APPROVAL_REPLICATE_YN("SYS_APPROVAL_REPLICATE_YN", "01", "Apply copy approval or not"),
			APPROVAL_RELOCATE_YN("SYS_APPROVAL_RELOCATE_YN", "01", "Apply move approval or not"),
			APPROVAL_ADD_COMPANYNODE_YN("SYS_APPROVAL_ADD_COMPANYNODE_YN", "01", "Apply approval for registering content to the company folder or not"),	
		
		// Access Control List (02)
		CONTENTACL_YN("SYS_CONTENTACL_YN", "02", "Apply content access controls or not"),
		CLASSACL_YN("SYS_CLASSACL_YN", "02", "Apply classification access controls or not"),
		ACL_INHERIT_YN("SYS_ACL_INHERIT_YN", "02", "Apply access control inheritance or not"),
		DEPT_RANGE("SYS_DEPT_RANGE", "02", "The scope of the department to be searched when checking access control info."),
		INHERITANCE_CLASSACL_YN("SYS_INHERITANCE_CLASSACL_YN", "02", "Whether to inherit the basic content access control info. in the classification info."),
		
		// File (03)
		REMOTE_YN("SYS_REMOTE_YN", "03", "File location is remote or not"),
		REMOTE_IP("SYS_REMOTE_IP", "03", "Remote IP address"),
		REMOTE_PORT("SYS_REMOTE_PORT", "03", "Remote Port"),
		REMOTE_URL("SYS_REMOTE_URL", "03", "Remote URL"),
		ENCRYPTION_YN("SYS_ENCRYPTION_YN", "03", "Apply encryption or not"),
		TEMPORARY_PATH("SYS_TEMPORARY_PATH", "03", "Temporary file path for uploading"),
		CHECKFORMAT_YN("SYS_CHECKFORMAT_YN", "03", "Apply checking file format or not"),
		
		// Folder and Content type (04)
		LAST_FOLDER_TYPE("SYS_LAST_FOLDER_TYPE", "04", "Final folder type(Folder/Content type)"),
			LAST_FOLDER_TYPE_NODE("1", "04", "Folder"),
			LAST_FOLDER_TYPE_DOCTYPE("2", "04", "Content type"),
		CAN_REG_DOC_TO_MID_NODE("SYS_CAN_REG_DOC_TO_MID_NODE", "04", "Whether it is possible to register content in the intermediate folder"),
		CAN_REG_DOC_TO_COMPANY_FOLDER("SYS_CAN_REG_DOC_TO_COMPANY_FOLDER", "04", "Whether general users can register content in company folders"),		
		CAN_REG_COMPANY_FOLDER("SYS_CAN_REG_COMPANY_FOLDER", "04", "Apply creating company folder or not"),								
		CAN_REG_DEPT_FOLDER("SYS_CAN_REG_DEPT_FOLDER", "04", "Apply creating dept. folder or not"),											
		CAN_REG_FOLDER_IN_LOWER("SYS_CAN_REG_FOLDER_IN_LOWER", "04", "Apply creating sub-dept. folder or not"),				
		
		// Version (05)
		VERSION_YN("SYS_VERSION_YN", "05", "Apply version or not"),
			VERSION_UPONLYHIGH_YN("SYS_VERSION_UPONLYHIGH_YN", "05", "Apply major version-up or not"),
			VERSION_UPWITHNOSAMEHASH_YN("SYS_VERSION_UPWITHNOSAMEHASH_YN", "05", "Apply same hash file version-up or not"),
		
		// Integrity (06)
		DOC_INTEGRITY("SYS_DOC_INTEGRITY", "06", "Rules for generating content key"),
			DOC_INTEGRITY_PK("1", "06", "Content ID"),
			DOC_INTEGRITY_DOCNO("2", "06", "Content No"),
		DOC_CONTENTNO_RULE("SYS_DOC_CONTENTNO_RULE", "06", "Rules for creating content no."),
			
		// List (07)
		LIST_QUERY_OBJECT("SYS_LIST_QUERY_OBJECT", "07", "Data type for list (B: Bundle, F:File, A:All)"),
		LIST_RECENT_DAY("SYS_LIST_RECENT_DAY", "07", "Period for recently registered contents"),
		LIST_CNT_PER_PAGE("SYS_LIST_CNT_PER_PAGE", "07", "No. of searched data per list"),
		
		// Database (08)
		ALLOW_JSON("SYS_ALLOW_JSON", "08", "JSON allowed or not"),
		
		// Log (09)
		LOG_ACCESS_YN("SYS_LOG_ACCESS_YN", "09", "Apply access log or not"),
		LOG_CONTENT_YN("SYS_LOG_CONTENT_YN", "09", "Apply content log or not"),
		LOG_SYSTEM_YN("SYS_LOG_SYSTEM_YN", "09", "Apply system log or not"),
		
		// Mail (10)
		MAIL_SEND_APPROVAL_YN("SYS_MAIL_SEND_APPROVAL_YN", "10", "Apply sending approval email or not"),
		
		// Cycle (11)
		CYCLE_STATUS_DISCARD_EXPIRED_YN("SYS_CYCLE_STATUS_DISCARD_EXPIRED_YN", "11", "Apply automatic discarding expired contents or not"),
		CYCLE_STATUS_BUILD_TYPE("SYS_CYCLE_STATUS_BUILD_TYPE", "11", "Statistics Generation Type"),
		
		// Session (98)
		SESSION_TIME("SYS_SESSION_TIME", "98", "The duration of the session"),
		
		// Mobile (99)
		MOBILE_YN("SYS_MOBILE_YN", "99", "Use mobile or not")
		
		;
		
		public final String env;
		public final String type;
		public final String note;
		
		ENVS(String env, String type, String note) {
			this.env = env;
			this.type = type;
			this.note = note;
		}
	}		
	
    /**
	 * <p><b>
	 * [OUT] LOG
	 * </b></p>
	 * <p>
	 *   ex) ZappConts.LOGS.ACTION_CONNECT.log	
	 * </p>
     */
	public enum LOGS {
		
		/* Connect */
		ACTION_CONNECT("01", "Access Log (Connect)"),
		ACTION_DISCONNECT("02", "Access Log (Disconnect)"),
		ACTION_TRY_CONNECT("03", "Access Log (Failed after trying to connect)"),
		
		ACTION_ADD("A1", "New"),
		ACTION_ADD_REQUEST("A2", "New (Request)"),
		ACTION_ADD_APPROVE("A3", "New (Approve)"),
		ACTION_ADD_RETURN("A4", "New (Return)"),
		ACTION_ADD_VUP("A5", "New (VersionUp)"),		
		ACTION_ADD_VUP_REQUEST("A6", "New (Request VersionUp)"),		
		
		ACTION_CHANGE("B1", "Edit"),
		ACTION_CHANGE_REQUEST("B2", "Edit (Request)"),
		ACTION_CHANGE_APPROVE("B3", "Edit (Approve)"),
		ACTION_CHANGE_RETURN("B4", "Edit (Return)"),
		ACTION_LOCK("B5", "Lock (Request)"),
		ACTION_LOCK_REQUEST("B6", "Lock (Request)"),
		ACTION_LOCK_APPROVE("B7", "Lock (Approve)"),
		ACTION_LOCK_RETURN("B8", "Lock (Return)"),	
		ACTION_UNLOCK("B9", "Unlock (Request)"),
		ACTION_UNLOCK_FORCELY("BA", "Unlock (Forcely)"),
//		ACTION_UNLOCK_REQUEST("B6", "Lock (Request)"),
//		ACTION_UNLOCK_APPROVE("B7", "Lock (Approve)"),
//		ACTION_UNLOCK_RETURN("B8", "Lock (Return)"),		
		
		ACTION_RELOCATE("C1", "Move"),
		ACTION_RELOCATE_REQUEST("C2", "Move (Request)"),
		ACTION_RELOCATE_APPROVE("C3", "Move (Approve)"),
		ACTION_RELOCATE_RETURN("C4", "Move (Return)"),
		
		ACTION_REPLICATE("D1", "Copy"),
		ACTION_REPLICATE_REQUEST("D2", "Copy (Request)"),
		ACTION_REPLICATE_APPROVE("D3", "Copy (Approve)"),
		ACTION_REPLICATE_RETURN("D4", "Copy (Return)"),
		
		ACTION_DISABLE("E1", "Delete"),
		ACTION_DISABLE_REQUEST("E2", "Delete (Request)"),
		ACTION_DISABLE_APPROVE("E3", "Delete (Approve)"),
		ACTION_DISABLE_RETURN("E4", "Delete (Return)"),
		ACTION_ENABLE("E5", "Recover"),
		ACTION_ENABLE_REQUEST("E6", "Recover (Request)"),
		ACTION_ENABLE_APPROVE("E7", "Recover (Approve)"),
		ACTION_ENABLE_RETURN("E8", "Recover (Return)"),
		
		ACTION_DISCARD("F1", "Discard"),
		ACTION_DISCARD_REQUEST("F2", "Discard (Request)"),
		ACTION_DISCARD_APPROVE("F3", "Discard (Approve)"),
		ACTION_DISCARD_RETURN("F4", "Discard (Return)"),
		ACTION_DISCARD_FORCELY("F5", "Forcely discard"),
		
		ACTION_VIEW("G1", "Inquire"),
		ACTION_VIEW_REQUEST("G2", "Inquire (Request)"),
		ACTION_VIEW_APPROVE("G3", "Inquire (Approve)"),
		ACTION_VIEW_RETURN("G4", "Inquire (Return)"),
		
		ACTION_REORDER("H1", "Sorting"),
		ACTION_REORDER_REQUEST("H2", "Sorting (Request)"),
		ACTION_REORDER_APPROVE("H3", "Sorting (Approve)"),
		ACTION_REORDER_RETURN("H4", "Sorting (Return)"),
		
		ACTION_MARK("I1", "Mark"),
		ACTION_UNMARK("I2", "Unmark"),
		
		ACTION_APPROVE("J1", "Approve"),
		ACTION_RETURN("J2", "Return"),
		
		ACTION_WITHDRAW("X1", "Withdraw"),
		
		ACTION_LINK("Y1", "Link"),
		ACTION_LINK_REQUEST("Y2", "Link (Request)"),
		ACTION_LINK_APPROVE("Y3", "Link (Approve)"),
		ACTION_LINK_RETURN("Y4", "Link (Return)"),
		ACTION_LINK_RELEASE("Y5", "Unlink"),
		
		ACTION_SHARE("Z1", "Share"),
		ACTION_SHARE_REQUEST("Z2", "Share (Request)"),
		ACTION_SHARE_APPROVE("Z3", "Share (Approve)"),
		ACTION_SHARE_RETURN("Z4", "Share (Return)"),
		ACTION_UNSHARE("Z5", "Unshare"),
		ACTION_UNSHARE_REQUEST("Z6", "Unshare (Request)"),
		ACTION_UNSHARE_APPROVE("Z7", "Unshare (Approve)"),
		ACTION_UNSHARE_RETURN("Z8", "Unshare (Return)"),	
		ACTION_CHANGE_SHARE("Z9", "Edit share info."),
		
		TYPE_BUNDLE("01", "Bundle"),
		TYPE_ADDBUNDLE("0A", "Additional Bundle"),
		TYPE_FILE("02", "File"),
		TYPE_CLASSIFICATION("03", "Classification"),
		TYPE_LINK("04", "Link"),
		TYPE_SHARE("05", "Share"),
		TYPE_LOCK("06", "Lock"),
		TYPE_WORKFLOWCONTENT("07", "Workflow-Content"),
		
		TYPE_AUTHENTICATION("01", "Authentication"),
		
		TYPE_COMPANY("01", "Company"),
		TYPE_DEPT("02", "Department"),
		TYPE_USER("03", "User"),
		TYPE_GROUP("04", "Group"),
		TYPE_WORKFLOW("05", "Workflow"),
		
		TYPE_ENV("11", "Preferences"),
		TYPE_CODE("12", "Code"),
		TYPE_TASK("13", "Task"),
		
		
		TYPE_CHANGE_OWNER("01", "Change holder"),
		TYPE_CHANGE_OWNDEPT("02", "Change holding dept."),
		TYPE_DISCARD_EXPIRED_CONTENT("11", "Discarding expired contents"),
		TYPE_BUILD_DAILY_STATICS("12", "Daily statistical processing"),
		
		ITEM_LOGGER("USER", "Logger info."),
		ITEM_CLIENT("Client", "Client info."),
		ITEM_CONTENT("CONTENT", "Content"),
		ITEM_CONTENT_ID("ID", "ID"),
		ITEM_CONTENT_TITLE("TITLE", "Title"),
		ITEM_CONTENT_BEFORE("CONTENT_BEFORE", "Content info. before processing"),
		ITEM_CONTENT_AFTER("CONTENT_AFTER", "Content info. After processing"),
		ITEM_CONTENT_COMMENT("Comment", "Comment")
		
		;
		
		public final String log;
		public final String note;
		
		LOGS(String log, String note) {
			this.log = log;
			this.note = note;
		}

	}
	
	
    /**
	 * <p><b>
	 * [OUT] TAG 
	 * </b></p>
	 * <p>
	 *   ex) ZappConts.TAGS.DATATYPE_TEXT.tag	
	 * </p>
     */
	public enum TAGS {

		/* Data type */
		DATATYPE_TEXT("1", "Text"),
		DATATYPE_NUMERIC("2", "Number"),
		DATATYPE_DATE("3", "Date"),
		
		/* */
		OBJTYPE_TABLE("TABLE", "Table"),
		OBJTYPE_INDEX("INDEX", "Index"),
		
		/* */
		TABLE_IMG("zapp_img", "master"),
		TABLE_IMGFILE("zapp_imgfile", "file"),
		TABLE_IMGNOTE("zapp_imgnote", "filenote"),
		TABLE_IMGORIGINAL("zapp_imgoriginal", "fileoriginal"),
		TABLE_IMG_ABBREVIATION("IMG", "master"),
		TABLE_IMGFILE_ABBREVIATION("IMGFILE", "file")
		
		;
		
		public final String tag;
		public final String note;
		
		TAGS(String tag, String note) {
			this.tag = tag;
			this.note = note;
		}
		
	}	
	
	
    /**
	 * <p><b>
	 * [OUT] Access Control List 
	 * </b></p>
	 * <p>
	 *   ex) ZappConts.ACLS.CONTENT_LIST.acl	
	 * </p>
     */
	public enum ACLS {

		/* Content */
		CONTENT_INACCESSIBLE(0, "No access"),
		
		CONTENT_LIST(1, "List"),
		CONTENT_READ(2, "View"),
		CONTENT_PRINT(3, "Print"),
		CONTENT_DOWNLOAD(4, "Download"),
		CONTENT_CHANGE(5, "Edit"),
//		CONTENT_CHECKOUT(32, "Lock"),
//		CONTENT_RELOCATE(64, "Move"),
//		CONTENT_REPLICATE(128, "Copy"),
//		CONTENT_DISCARD(265, "Delete"),

		/* Classification */
		CLASS_INACCESSIBLE(0, "Viewing not allowed + Registering not allowed"),
		CLASS_READONLY(1, "Viewing allowed + Registering not allowed"),
		CLASS_READ_ADD(2, "Viewing allowed + Registering allowed"),
		
		;
		
		public final int acl;
		public final String note;
		
		ACLS(int acl, String note) {
			this.acl = acl;
			this.note = note;
		}
		
	}	
	
    /**
	 * <p><b>
	 * [OUT] Approval
	 * </b></p>
	 * <p>
	 *   ex) ZappConts.WORKFLOWS.WF_TYPE_APPROVAL.swt	
	 * </p>
     */
	public enum WORKFLOWS {
		
		/* Approve Type */
		WF_TYPE_APPROVAL("01", 0, "Approve"),
		WF_TYPE_CIRCULATION("02", 0, "Circulation"),
		WF_TYPE_COMBINED("03", 0, "Approve + Circulation"),
		
		WF_OBJECT_NONE("00", 0, "Not applied"),
		WF_OBJECT_ADD("01", 1, "Registering"),
		WF_OBJECT_EDIT("02", 2, "Editing"),
		WF_OBJECT_DELETE("04", 4, "Deleting"),
		WF_OBJECT_RECOVER("08", 8, "Recovering"),
		WF_OBJECT_ALL("99", 99, "All")
		
		;
		
		public final String swt;
		public final int iwt;
		public final String note;
		
		WORKFLOWS(String swt, int iwt, String note) {
			this.swt = swt;
			this.iwt = iwt;
			this.note = note;
		}
	}
	
	/**
	 * <p><b>Language</b></p>
	 */
	public enum OTHERLANGS {

		SERBIAN("se", "Српски")
		;

		public final String lang;
		public final String note;

		OTHERLANGS(String lang, String note) {
			this.lang = lang;
			this.note = note;
		}
	     
	}	
	
	/**
	 * <p><b>Options (Any)</b></p>
	 */
	public enum OPTIONS {

		ENABLE_OVERWRITE("1", "Overwrite content"),
		ENABLE_RENAME("2", "Rename content"),
		ENABLE_CALCEL("3", "Cancel"),
		
		UP_HIGH_VERSION("H", "Up High Version"),
		UP_LOW_VERSION("L", "Up Low Version")
		
		;

		public final String opt;
		public final String note;

		OPTIONS(String opt, String note) {
			this.opt = opt;
			this.note = note;
		}
		
	}
	
}
