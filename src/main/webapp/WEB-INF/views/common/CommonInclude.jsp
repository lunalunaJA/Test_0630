<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@page import="com.zenithst.core.authentication.vo.ZappAuth"%>
<%@page import="com.zenithst.core.common.utility.ZappUtility"%>
<%@page import="com.zenithst.archive.vo.ZArchTask"%>
<%@page import="java.util.List"%>

<c:set var="ctxRoot" value="${pageContext.servletContext.contextPath}" />
<c:set var="image" value="${pageContext.servletContext.contextPath}/resources/images" />
<c:set var="logo" value="${pageContext.servletContext.contextPath}/resources/logoImg" />
<c:set var="css" value="${pageContext.servletContext.contextPath}/resources/css" />
<c:set var="js" value="${pageContext.servletContext.contextPath}/resources/js" />
<c:set var="manual" value="${pageContext.servletContext.contextPath}/resources/manual" />

<c:set var="appTitle" value="NADi4.0 Zenith ECM" />

<c:set var="ID" value="${Authentication.sessUser.loginid}" />
<c:set var="UserID" value="${Authentication.sessUser.userid}" />
<c:set var="UserName" value="${Authentication.sessUser.name}" />
<c:set var="CompanyName" value="${Authentication.sessCompany.name}" />
<c:set var="CompanyABBRName" value="${Authentication.sessCompany.abbrname}" />
<c:set var="UserType" value="${Authentication.sessOnlyDeptUser.usertype}" />

<c:set var="RootText" value="${sessCompany.name}" />

<c:set var="sysLang" value="en" />

<c:if test="${sessLang != null}">
	<c:set var="sysLang" value="${sessLang}" />
</c:if>
<c:set var="lang" value="cap_${sysLang }" />
<c:set var="msgLang" value="msg_${sysLang }" />


<%
String sessUserID = "";
String sessID = "";
String sessUserName = "";
String sessCompanyCode = "";
String sessCompanyID = "";
String sessCompanyName = "";
String sessCompanyABBRNAME = "";
String sessUserAuth = "";
String sessDeptName = "";
String sessDeptID = "";
String sessAclUsed = "";
String sessMobile = "";
String sessCanUpdate = "";
String sessAuth = "";
String sessGmp = "";
String sessUserType = "";

String AuthV = "";
String AuthD = "";
String AuthC = "";
String AuthDM = "";
String AuthUM = "";
String sTaskID = "";
ZappAuth rZappAuth = null;
try {
	Object obj = session.getAttribute("Authentication");
	rZappAuth = (ZappAuth) obj;
	
	if (rZappAuth != null) {
		sessUserID = ZappUtility.parseString(rZappAuth.getSessUser().getUserid());
		sessUserType = ZappUtility.parseString(rZappAuth.getSessUser().getObjType());
	}
	
	//세션에 기관의 업무 리스트를 리턴 
	//EDMS의 경우 한개의 업무를 사용하고 (업무코드:EDMS) 이미지시스템은 여러개의 업무를 사용하기 때문에 해당 로직이 맞추어 작업 필요
	Object objTask  = session.getAttribute("sessTask");
	List<ZArchTask> rZArchTaskList = (List<ZArchTask>) objTask;
	System.out.println("rZArchTaskList ====>"+rZArchTaskList);
	//EDMS일경우 해당 업무아이디 조회 
	if(rZArchTaskList != null){
		for(ZArchTask archTask : rZArchTaskList) {
			if(archTask.getCode().equals("EDMS")){
				sTaskID = archTask.getTaskid();
				break;
			}
		}
	}
} catch (Exception e) {
	e.printStackTrace();
}
%>

<SCRIPT LANGUAGE=JavaScript>
var RootText = "${sessCompany.name}";
var companyid = "${Authentication.objCompanyid }";
var companyName = "${Authentication.sessCompany.name }";
var companyAbbrName = "${Authentication.sessCompany.abbrname}";
var deptid = "${Authentication.sessOnlyDeptUser.deptid }";
var deptname = "${Authentication.sessDeptUser.zappDept.name}";
var deptuserid = "${Authentication.sessOnlyDeptUser.deptuserid }";
var userid = "${Authentication.sessOnlyDeptUser.userid }";
var userType = "${Authentication.sessOnlyDeptUser.usertype}";
var username = "${Authentication.sessUser.name}";
var useremail = "${Authentication.sessUser.email}";
var userempno = "${Authentication.sessUser.empno}";
var userloginId = "${Authentication.sessUser.loginid}";
var taskid = "<%=sTaskID%>";
var context = "${pageContext.servletContext.contextPath}";
var lang = "${sessLang}";
var pagecnt = "${Authentication.sessEnv.get('SYS_LIST_CNT_PER_PAGE').setval}";
var loginid = "${Authentication.sessUser.loginid}";
var GROUPCODES = {
	COMPANY : "01",
	DEPT : "02",
	USER : "03",
	COLLABO : "04",
	APPROVAL : "05",
	ALLUSER : "98",
	SUPER : "99"
};
var USERTYPES = {
	USER : "01",
	DEPT : "02",
	COMPANY : "03",
	SYSTEM : "04"
}
var ACLTYPES = {
	USER : "01",
	DEPT : "02",
	GROUP : "03"
}
var CLSTYPES = {
	NOMAL : "01",
	COMPANY : "N1",
	DEPT : "N2",
	USER : "N3",
	COLLABO : "N4",
	CLASSIFICATION : "02",
	DOCTYPE : "03"
}
var CLSACL = {
	NOTVIEW : "0",
	VIEW : "1",
	REGISTER : "2"
}
var CONACL = {
	NOACCESS : "0",
	LIST : "1",
	VIEW : "2",
	PRINT : "3",
	DOWNLOAD : "4",
	EDIT : "5"
}

//사용자 트리 아이콘
var TREEICONS = {
	COMPANY : "${image}/jstree/white/ic_enterprise_01.png",			// 회사
	DEPTGROUP : "${image}/jstree/white/icon_24px_class_01.png",		// 부서그룹
	DEPT : "${image}/jstree/white/ic_department_04.png",				// 부서
	COLLABOGROUP : "${image}/jstree/white/ic_collaboration_03.png",	// 협업그룹
	COLLABO : "${image}/jstree/white/ic_collaboration_02.png",			// 협업
	USER : "${image}/jstree/white/tree_user_icon03.png",				// 사용자
	NOMAL : "${image}/jstree/yellow/ic_folder_00.png",					// 일반폴더
	NP11 : "${image}/jstree/yellow/ic_document_w01.png",				// 승인요청 문서
	NP12 : "${image}/jstree/yellow/ic_document_b03.png",				// 승인완료 문서
	NP13 : "${image}/jstree/yellow/ic_document_b05.png",				// 승인반려 문서
	NP14 : "${image}/jstree/yellow/ic_document_w02.png",				// 내가 승인할 문서
	CREATE : "${image}/jstree/yellow/ic_folder_02.png",					// 등록
	VIEW : "${image}/jstree/yellow/ic_tree_view_01.png",				// 조회
	NOTVIEW : "${image}/jstree/yellow/ic_tree_nomal_01.png",				// 조회 불가
	CLS_ROOT : "${image}/jstree/yellow/icon_24px_class_01.png",		// 
	CLS_SUB : "${image}/jstree/yellow/ic_document_w00.png",
	DISABLE : "${image}/jstree/yellow/icon_24px_class_02.png",			// 사용불가
	FAV : "${image}/jstree/yellow/ic_tree_favorite_01.png",				// 즐겨찾기
	TRH : "${image}/jstree/yellow/tree_user_icon07.png",				// 
	LTY : "${image}/jstree/yellow/tree_user_icon06.png",				//
	0 : "${image}/jstree/yellow/ic_tree_nomal_01.png",					// 트리 권한 없음
	1 : "${image}/jstree/yellow/ic_tree_view_01.png",					// 트리 조회
	2 : "${image}/jstree/yellow/ic_folder_02.png",						// 트리 수정
	N : "${image}/jstree/yellow/icon_24px_class_02.png",				// 
	GOURP : "${image}/jstree/yellow/tree_user_icon08.png",				// 그룹
	MYJOB : "${image}/jstree/yellow/ic_tree_my_01.png",					// 나만의 작업
	MYDOC : "${image}/jstree/yellow/tree_user_icon08.png",				// 개인 폴더
	LATELY : "${image}/jstree/yellow/tree_user_icon08.png",				//
	LOCK : "${image}/jstree/yellow/ic_tree_lock_02.png",				// 반출폴더
	APPEXPIRE : "${image}/jstree/yellow/ic_tree_arrived_01.png",		//
	EXPIRED : "${image}/jstree/yellow/ic_tree_elapsed_01.png",
	SHARE : "${image}/jstree/yellow/ic_tree_share_01.png",				// 공유
	SHARED : "${image}/jstree/yellow/tree_user_icon06.png",				// 공유한문서
	SHAREDBY : "${image}/jstree/yellow/tree_user_icon06.png",			// 공유받은문서
	FAVERITE : "${image}/jstree/yellow/ic_tree_favorite_01.png",		// 즐겨찾기
	FAVDOC : "${image}/jstree/yellow/ic_tree_contents_01.png",			// 즐겨찾기 문서
	FAVFLD : "${image}/jstree/yellow/ic_tree_favorite_02.png",			// 즐겨찾기 폴더
	TRASH : "${image}/jstree/yellow/ic_tree_trash_01.png"				// 휴지통
}

//관리자 트리 아이콘
var ADMINTREEICONS = {
	COMPANY : "${image}/jstree/yellow/ic_enterprise_01.png",			// 회사
	DEPT : "${image}/jstree/yellow/ic_department_04.png",				// 부서
	DISABLE : "${image}/jstree/black/icon_24px_class_04.png",			// 사용불가
	USER : "${image}/jstree/yellow/tree_user_icon03.png",				// 사용자
	CLS_ROOT : "${image}/jstree/yellow/icon_24px_class_01.png",			// 문서분류 최상위
	CLS_CENT : "${image}/jstree/yellow/icon_24px_class_03.png",			// 문서분류 중간	
	CLS_SUB : "${image}/jstree/yellow/ic_document_w00.png",				// 분서분류 화위
}

var POPTREE = {
	COMPANY : "${image}/jstree/navy/ic_enterprise_01.png",			// 회사
	DEPT : "${image}/jstree/navy/ic_department_04.png",				// 부서
	CLS_ROOT : "${image}/jstree/navy/icon_24px_class_01.png",			// 문서분류 최상위
	CLS_SUB : "${image}/jstree/navy/ic_document_w00.png",				// 분서분류 화위
	USER : "${image}/jstree/navy/tree_user_icon03.png",				// 사용자
	GOURP : "${image}/jstree/navy/ic_department_04.png",				// 그룹
	DEPTGROUP : "${image}/jstree/navy/icon_24px_class_01.png",		// 부서그룹
	COLLABOGROUP : "${image}/jstree/navy/ic_collaboration_03.png",	// 협업그룹
	COLLABO : "${image}/jstree/navy/ic_collaboration_02.png",			// 협업
	NOMAL : "${image}/jstree/navy/icon_24px_class_03.png",					// 일반폴더
	0 : "${image}/jstree/navy/ic_tree_nomal_01.png",					// 트리 권한 없음
	1 : "${image}/jstree/navy/ic_tree_view_01.png",					// 트리 조회
	2 : "${image}/jstree/navy/ic_folder_02.png",						// 트리 수정
}


//error alert
var alertNoty = function(request, status, error, message) {
	
	if (request.status == 401 || request.status == 403) {
		location.replace("${ctxRoot}/go/login");
		return;
	}
  
	var msg = "<spring:eval expression="@${msgLang}['ERROR_HAS_OCCURRED']"/>";
	if (message != null && message.indexOf("[") > -1 && message.lastIndexOf("]") > -1) {
		msg = message.substring(message.lastIndexOf("]") + 1, message.length);
	} else if (message != null) {
		msg = message;
	}
	if (request != null && (request.status == 500 || request.status == 401 || request.status == 403)) {
		if (request.status == 401 || request.status == 403) {
			msg = "<spring:eval expression="@${msgLang}['EXPIRED_SESSION']"/>";
		}
		noty({
			layout : "center",
			text : msg + "</br>" + "ERRCODE : [" + request.status + "]",
			buttons : [ {
				addClass : 'btbase',
				text : 'Ok',
				onClick : function($noty) {
					if(request.status == 401 || request.status == 403){
						location.replace("${ctxRoot}/go/login");
						$noty.close();
					}else if(request.status == 500){
						$noty.close();
					}
				}
			} ],
			type : "error",
			killer : true
		});
	} else {
		noty({
			layout : "center",
			text : msg,
			timeout : "1500",
			type : "information",
			killer : true
		});
	}
}

var alert = function(message) {
	var msg = "<spring:eval expression="@${msgLang}['ERROR_HAS_OCCURRED']"/>";
	if (message != null && message.indexOf("[") > -1 && message.lastIndexOf("]") > -1) {
		msg = message.substring(message.lastIndexOf("]") + 1, message.length);
	} else if (message != null) {
		msg = message;
	}
	noty({
		layout : "center",
		text : msg,
		timeout : "1500",
		type : "information",
		killer : true
	});
}

var alertErr = function(message) {
	console.log("====message : ", message);
	
	var msg = "<spring:eval expression="@${msgLang}['ERROR_HAS_OCCURRED']"/>";
	if (message != null && message.indexOf("[") > -1 && message.lastIndexOf("]") > -1) {
		msg = message.substring(message.lastIndexOf("]") + 1, message.length);
	} else if (message != null) {
		msg = message;
	}
	noty({
		layout : "center",
		text : msg,
		buttons : [ {
			addClass : 'btbase',
			text : 'Ok',
			onClick : function($noty) {
			
				$noty.close();
			}
		} ],
		type : "error",
		killer : true
	});
}

//탐색기용
var confirmCS = function(message) {
	  noty({
	    layout : "center",
	    text : message,
	    buttons : [ {
	      addClass : 'b_btn',
	      text : 'Ok',
	      onClick : function($noty) {
	        $noty.close();
			window.top.document.title = "Finish";
	      }
	    } ],
	    type : "information",
	    killer : false
	  });
  }

/*********************************
Name   : sysCodeList
Desc   : 공통 코드 조회 함수
Param  : ctxRoot, types, companyid, eventType
**********************************/
var sysCodeList = function(ctxRoot, types, companyid, eventType) {
	var sendData = {
		"objIsTest" : "N",
		"types" : types,
		"isactive" : "Y",
		"companyid" : companyid
	}
	
	if(eventType = "isactive_all"){
		sendData = {
			"objIsTest" : "N",
			"types" : types,
			"companyid" : companyid,
			"objmaporder" : {"priority":"asc"}
		}    	
	}
	var resultData = [];
	$.ajax({
		type : 'POST',
		url : ctxRoot + '/api/system/code/list',
		dataType : 'json',
		contentType : 'application/json',
		async : false,
		data : JSON.stringify(sendData),
		success : function(data) {
			if (data.status == "0000") {
				$.each(data.result, function(index, item) {
					resultData.push(item);
				});
			} else {
				alertNoty(data.message);
			}
		},
		error : function(request, status, error) {
			alertNoty(request, status, error);
		}
	});
	return resultData;
}

/*********************************
Name   : openLayer
Desc   : 프로그레스바 열기
Param  : message:화면 표시 메시지
**********************************/
var openLayer = function(message) {
	if (!$('.opacity_bg_layer').length) { //
		var innerTable = "<table height='100%' width='100%' border='0'><tr>";
		innerTable += "<td valign='middle' align='center'>";
		innerTable += "<div class='notice'><div class='loading'>";
		innerTable += "		<p>" + message + ". <br /><spring:eval expression="@${msgLang}['WAIT_A_MINUTE']"/></p></div>";
		innerTable += "		<img src='${image}/visual/loading_admin.gif' />";
		innerTable += "</div></td></tr></table>";
		$('<div class="opacity_bg_layer" style="position:absolute;top:0;z-index:2000;"></div>').html(innerTable).prependTo($('body'));
	}
	
	var oj = $(".opacity_bg_layer");
	var w = $(document).width();
	var h = $(window).height();
	
	oj.css({
		'width' : w,
		'height' : h
	});
	oj.fadeIn(0);
}

/*********************************
Name   : closeLayer
Desc   : 프로그레스바 닫기
Param  : 
**********************************/
var closeLayer = function() {
	if ($('.opacity_bg_layer').length) {
		var oj = $('.opacity_bg_layer');
		oj.fadeOut(500, function() {
			oj.remove();
		});
	}
}

var IsNull = function(pstStr) {
	if(pstStr === undefined || pstStr === null)
		return "";
	else
		return pstStr;
}
</SCRIPT>



