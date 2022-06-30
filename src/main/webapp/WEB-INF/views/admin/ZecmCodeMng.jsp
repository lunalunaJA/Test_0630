<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*"%>
<%@include file="../common/CommonInclude.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />    
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0, minimum-scale=1.0, maximum-scale=2.0, user-scalable=no">
<title>NADi4.0 :: <spring:eval expression="@${lang}['CODE_MANAGEMENT']" /></title>
<link rel="stylesheet" href="${css}/reset.css">
<link rel="stylesheet" href="${css}/common.css">
<link rel="stylesheet" href="${css}/style.css">
<link rel="stylesheet" href="${js}/jquery-ui-1.13.1/jquery-ui.min.css">

<script src="${js}/jquery-1.12.2.min.js"></script>
<script src="${js}/jquery-ui-1.13.1/jquery-ui.min.js"></script>
<script src="${js}/jquery.noty.packaged.min.js"></script>
<script src="${js}/jquery-ui.js"></script>
<script src="${js}/common.js"></script>
<script type="text/javascript">
var sMainCodeTypeList = "";		//선택 코드구분
var sMainCodeName = "";			//선택 코드명
var sMainCodeValue = "";		//선택 코드값
var sMainCodeTypes = "";		//선택 코드구분
var sMainCodeKey = "";			//선택 코드 키
var sMainCodeID = "";			//선택 코드  ID
var sSubCodeValue = "";			//선택 하위 코드값
var sSubCodeName = "";			//선택 하위 코드명
var sSubCodeID = "";			//선택 하위 코드  ID

$(document).ready( function() {
	//입력값 초기화
	resetInputMain();
	resetInputSub();
	
	//Main 초기화 버튼
	$("#btnMainInit").click(function() {
		resetInputMain();
	});

	//Main 저장 버튼
	$("#btnMainNew").click(function() {
		if (objectIsEmpty(fn_CodeMain.select)) {
			fn_CodeMain.save("add");
		} else {
			fn_CodeMain.save("change");
		}
	});
	
	//Main 삭제 버튼
	$("#btnMainDel").click(function() {
		fn_CodeMain.disable();
	});

	//Main 복구 버튼
	$("#btnMainRes").click(function() {
		fn_CodeMain.restore();
	});

	//Main 폐기 버튼
	$("#btnMainDis").click(function() {
		fn_CodeMain.discard();
	});

	//Main 코드 select option 변경시 호출
	$("#MainCodeTypeList").change(function() {
		resetInputMain();
		resetInputSub();
		$("#MobMainCodeTypeList").val($("#MainCodeTypeList").val());
		fn_CodeMain.getCodeSelect();
	});

	//Main 코드 select option 변경시 호출
	$("#MobMainCodeTypeList").change(function() {
		resetInputMain();
		resetInputSub();
		$("#MainCodeTypeList").val($("#MobMainCodeTypeList").val());
		fn_CodeMain.getCodeSelect();
	});
	
	//Sub 코드 초기화 버튼
	$("#btnSubInit").click(function() {
		resetInputSub();
	});

	//Sub 코드 저장 버튼
	$("#btnSubNew").click(function() {
		if (objectIsEmpty(fn_CodeSub.select)) {
			fn_CodeSub.save("add");
		} else {
			fn_CodeSub.save("change");
		}
	});
	
	//Sub 코드 삭제 버튼
	$("#btnSubDel").click(function() {
		fn_CodeSub.disable();
	});

	//Sub 코드 복구 버튼
	$("#btnSubRes").click(function() {
		fn_CodeSub.restore();
	});

	//Sub 코드 폐기 버튼
	$("#btnSubDis").click(function() {
		fn_CodeSub.discard();
	});
	
	//Sub 코드 리스트 클릭
	$(document.body).delegate('#CodeList tr', 'click', function() {
		$("input[id^='Sub']").val('');
		$("input[id^='MobSub']").val('');
		var meta = $(this).data("meta");
		
		console.log("CodeList click meta : "+JSON.stringify(meta));
		
		fn_CodeSub.select = meta;
		$("#SubCodeValue").val(meta.codevalue);
		$("#SubCodeName").val(meta.name);
		
		if (meta.isactive == "Y") {
			$("#btnSubDel").show();
			$("#btnSubRes").hide();
			$("#btnSubDis").hide();
		} else {
			$("#btnSubDel").hide();
			$("#btnSubRes").show();
			$("#btnSubDis").show();
		}
	});

	//모바일 Sub 코드 리스트 클릭
	$(document.body).delegate('#MobileCodeList p', 'click', function() {
		$("input[id^='Sub']").val('');
		$("input[id^='MobSub']").val('');
		var meta = $(this).parent().data("meta");
		fn_CodeSub.select = meta;
		$("#MobSubCodeValue").val(meta.codevalue);
		$("#MobSubCodeName").val(meta.name);
		
		if (meta.isactive == "Y") {
			$("#btnSubDel").show();
			$("#btnSubRes").hide();
			$("#btnSubDis").hide();
		} else {
			$("#btnSubDel").hide();
			$("#btnSubRes").show();
			$("#btnSubDis").show();
		}
	});
	
	
	fn_CodeMain.getCodeTypeList();	//코드 구분 목록 조회

});

/*********************************
Name   : fn_CodeMain
Desc   : 상위코드 처리 함수
Param  : 없음
**********************************/
var fn_CodeMain = {
	select : {},			//선택한 리스트 전체 값
	save : function(type) {	//상위코드 정보 저장
		sMainCodeID = fn_CodeMain.select.codeid;
		if(window.innerWidth < 1101){	//모바일 화면 처리
			if (!isEmptyInput($("input[id^=MobMain]"))) {
				return;
			}
			
			sMainCodeName = $.trim($("#MobMainCodeName").val());
			sMainCodeValue = $.trim($("#MobMainCodeValue").val());
			sMainCodeTypes = $.trim($("#MobMainCodeTypes").val());
			sMainCodeKey = $.trim($("#MobMainCodeKey").val());
			
		}else{		//PC 화면 처리
			if (!isEmptyInput($("input[id^=Main]"))) {
				return;
			}

			sMainCodeName = $.trim($("#MainCodeName").val());		//선택 문서유형명
			sMainCodeValue = $.trim($("#MainCodeValue").val());		//선택 문서유형 코드
			sMainCodeTypes = $.trim($("#MainCodeTypes").val());
			sMainCodeKey = $.trim($("#MainCodeKey").val());
		}
		
		if (type == "change") {
			if (sMainCodeName == fn_CodeMain.select.name
					&& sMainCodeValue == fn_CodeMain.select.codevalue) {
				alert("<spring:eval expression="@${msgLang}['NO_ALERTED_CODE']"/>");
				return;
			}
		}

		var sendData = {};
		sendData.type = type;	//처리방식 (add:등록, change:수정)
		sendData.url = "${ctxRoot}/api/system/code/" + type;
		sendData.data = {objIsTest : "N"
				, objDebugged : false
				, upid : "ROOT"
			}
		sendData.data.name = sMainCodeName;
		sendData.data.codevalue = sMainCodeValue;
		sendData.data.types = sMainCodeTypes;
		sendData.data.codekey = sMainCodeKey;
		sendData.data.codeid = sMainCodeID;
		
		fn_CodeMain.publicCommon(sendData);
	},
	disable : function() {	//상위 코드를 삭제 플레그 처리
		sMainCodeID = fn_CodeMain.select.codeid;
		if (!sMainCodeID) {
			alert("<spring:eval expression="@${msgLang}['SELECT_CODE_DELETE']"/>");
			return;
		}
		
		if(confirm("<spring:eval expression="@${msgLang}['ARE_YOU_DELETE_CODE_TYPE']"/>") == false){
			return;
		}
		
		var sendData = {};
		sendData.data = {objIsTest : "N"
			, objDebugged : false
		}
		sendData.type = "disable";
		sendData.url = "${ctxRoot}/api/system/code/disable";
		sendData.data.codeid = sMainCodeID;
		
		fn_CodeMain.publicCommon(sendData);

	},
	restore : function() {	//상위 코드를 복원
		sMainCodeID = fn_CodeMain.select.codeid;
		if (!sMainCodeID) {
			alert("<spring:eval expression="@${msgLang}['SELECT_CODE_RESTORE']"/>");
			return;
		}
		
		if(confirm("<spring:eval expression="@${msgLang}['DO_YOU_RESTORE_CODE_TYPE']"/>") == false){
			return;
		}
		
		var sendData = {};
		sendData.data = {objIsTest : "N"
			, objDebugged : false
		}
		sendData.type = "restore";
		sendData.url = "${ctxRoot}/api/system/code/enable";
		sendData.data.codeid = sMainCodeID;
		
		fn_CodeMain.publicCommon(sendData);
	},
	discard : function() {	//상위 코드를 폐기
		sMainCodeID = fn_CodeMain.select.codeid;
		if (!sMainCodeID) {
			alert("<spring:eval expression="@${msgLang}['SELECT_CODE_DISCARD']"/>");
			return;
		}
		
		//코드 하위정보 존재시 삭제 불가
		if(window.innerWidth < 1101){	//모바일 화면 처리
			if($("#MobileCodeList").children().length > 0){
				alert("<spring:eval expression="@${msgLang}['CANNOT_CODE_DELETED']"/>");
				return;
			}			
		}else{
			if($("#CodeList").children().length > 0){
				alert("<spring:eval expression="@${msgLang}['CANNOT_CODE_DELETED']"/>");
				return;
			}
		}

		if(confirm("<spring:eval expression="@${msgLang}['ARE_YOU_DISCARD_CODE_TYPE']"/>") == false){
			return;
		}
		
		var sendData = {};
		sendData.data = {objIsTest : "N"
			, objDebugged : false
		}
		sendData.type = "discard";
		sendData.url = "${ctxRoot}/api/system/code/discard";
		sendData.data.codeid = sMainCodeID;
		
		fn_CodeMain.publicCommon(sendData);
	},
	getCodeTypeList : function() {	//코드 타입 정보 조회하기
		var sendData = {
			"objIsTest" : "N"
			, "companyid" : companyid
			, "upid" : "ROOT"
			, "editable" : "Y"
			, "objmaporder" : {"name":"asc"}
		};
	
		console.log("getCodeTypeList sendData : "+JSON.stringify(sendData));

		$.ajax({
			url : "${ctxRoot}/api/system/code/list",
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
				console.log("getCodeTypeList data : "+JSON.stringify(data));
				if (data.status == "0000") {
					$("#MainCodeTypeList").empty();
					$("#MobMainCodeTypeList").empty();
					var selected = objectIsEmpty($.trim($("#CodeName").val())) ? "selected" : "";
					var option = "";
					$.each(data.result, function(idx, result) {
						if (idx == 0) { //수정필요 영문 or 한글 값 
							option += "<option data-meta='" + JSON.stringify(result) + "' value='"+result.codeid+"'" + selected + ">" + result.name + "</option>";
						} else {
							option += "<option data-meta='" + JSON.stringify(result) + "' value='"+result.codeid+"'>" + result.name + "</option>";
						}
					});
					$("#MainCodeTypeList").append(option);
					$("#MobMainCodeTypeList").append(option);
					
					fn_CodeMain.getCodeSelect();	// 코드 선택 정보 표시
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			},
			beforeSend : function() {
			},
			complete : function() {
			}
		});
	},
	getCodeSelect : function() {	//Main 코드 선택 정보표시
		//입력값 초기화
		resetInputMain();
		
		//선택 코드 모바일 모드 정보 처리
		$("#MobMainCodeTypes").attr("disabled", true);
		$("#MobMainCodeKey").attr("disabled", true);
		var selectItem = $("#MobMainCodeTypeList option:selected");
		var meta = selectItem.data("meta");
		fn_CodeMain.select = meta;
		
		if (!objectIsEmpty(fn_CodeSub.select)) {
			$("#MobMainCodeID").val(meta.codeid);
			$("#MobMainCodeName").val(meta.name);
			$("#MobMainCodeValue").val(meta.codevalue);
			$("#MobMainCodeTypes").val(meta.types);
			$("#MobMainCodeKey").val(meta.codekey);
			$("#MobMainCodeState").val(
			    (meta.isactive == 'Y') ? "<spring:eval expression="@${lang}['USE']"/>" : "<spring:eval expression="@${lang}['NOT_USE']"/>");
		}
		
		//선택 코드 일반 모드 정보 처리
		$("#MainCodeTypes").attr("disabled", true);
		$("#MainCodeKey").attr("disabled", true);
		selectItem = $("#MainCodeTypeList option:selected");
		meta = selectItem.data("meta");
		fn_CodeMain.select = meta;
		
		if (!objectIsEmpty(fn_CodeMain.select)) {
			$("#MainCodeID").val(meta.codeid);
			$("#MainCodeName").val(meta.name);
			$("#MainCodeValue").val(meta.codevalue);
			$("#MainCodeTypes").val(meta.types);
			$("#MainCodeKey").val(meta.codekey);

			$("#MainCodeState").val(
			    (meta.isactive == 'Y') ? "<spring:eval expression="@${lang}['USE']"/>" : "<spring:eval expression="@${lang}['NOT_USE']"/>");
		}

		if (meta.isactive == "Y") {
			$("#btnMainDel").show();
			$("#btnMainRes").hide();
			$("#btnMainDis").hide();
		} else {
			$("#btnMainDel").hide();
			$("#btnMainRes").show();
			$("#btnMainDis").show();
		}
		
		//하위 코드 조회
		if(window.innerWidth < 1101){	//모바일 화면 처리
			fn_CodeSub.getCodeList($("#MobMainCodeTypes").val());			
		}else{
			fn_CodeSub.getCodeList($("#MainCodeTypes").val());
		}
	},	
	publicCommon : function(sendData) {	//공통 처리 로직 호출
		console.log("CodeMain publicCommon sendData "+sendData.type+":", JSON.stringify(sendData));
		$.ajax({
			type : 'POST',
			url : sendData.url,
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData.data),
			success : function(data) {
				console.log("CodeMain publicCommon data "+sendData.type+":", JSON.stringify(data));
				if (data.status == "0000") {
					messageNotice(sendData.type);
					fn_CodeMain.getCodeTypeList();
				} else {
					alertErr(data.message);
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			},
			complete : function() {
			}
		});
	}
}

/*********************************
Name   : fn_CodeSub
Desc   : 하위 코드 처리 함수
Param  : 없음
**********************************/
var fn_CodeSub = {
	select : {},			//선택한 리스트 전체 값
	save : function(type) {	//하위코드 정보 저장
		console.log("fn_CodeSub.select : "+JSON.stringify(fn_CodeSub.select));
		sSubCodeID = fn_CodeSub.select.codeid;
		if(window.innerWidth < 1101){	//모바일 화면 처리
			sSubCodeValue = $.trim($("#MobSubCodeValue").val());
			sSubCodeName = $.trim($("#MobSubCodeName").val());
			if (!sSubCodeValue) {
				alert("<spring:eval expression="@${msgLang}['ENTER_CODE_VALUE']"/>");
				$("#MobDocName").focus();
				return;
			}
			if (!sSubCodeName) {
				alert("<spring:eval expression="@${msgLang}['ENTER_CODE_NAME']"/>");
				$("#MobDocCode").focus();
				return;
			}		
		}else{		//PC 화면 처리
			sSubCodeValue = $.trim($("#SubCodeValue").val());
			sSubCodeName = $.trim($("#SubCodeName").val());
			if (!sSubCodeValue) {
				alert("<spring:eval expression="@${msgLang}['ENTER_CODE_VALUE']"/>");
				$("#DocName").focus();
				return;
			}
			if (!sSubCodeName) {
				alert("<spring:eval expression="@${msgLang}['ENTER_CODE_NAME']"/>");
				$("#DocCode").focus();
				return;
			}
		}

		if (type == "change") {
			if (sSubCodeValue == fn_CodeSub.select.codevalue
					&& sSubCodeName == fn_CodeSub.select.name) {
				alert("<spring:eval expression="@${msgLang}['NO_ALERTED_CODE']"/>");
				return;
			}
		}

		var sendData = {};
		sendData.type = type;	//처리방식 (add:등록, change:수정)
		sendData.url = "${ctxRoot}/api/system/code/" + type;
		sendData.data = {objIsTest : "N"
				, objDebugged : false
			}
		sendData.data.codeid = sSubCodeID;
		sendData.data.upid = fn_CodeMain.select.codeid;
		sendData.data.codevalue = sSubCodeValue;
		sendData.data.name = sSubCodeName;
		sendData.data.types = fn_CodeMain.select.types;
		sendData.data.codekey = fn_CodeMain.select.codekey;

		fn_CodeSub.publicCommon(sendData);
	},
	disable : function() {	//하위 코드를 삭제 플레그 처리
		sSubCodeID = fn_CodeSub.select.codeid;
		if (!sSubCodeID) {
			alert("<spring:eval expression="@${msgLang}['SELECT_CODE_DELETE']"/>");
			return;
		}
		
		if(confirm("<spring:eval expression="@${msgLang}['ARE_YOU_DELETE_CODE_INFO']"/>") == false){
			return;
		}
		
		var sendData = {};
		sendData.data = {objIsTest : "N"
			, objDebugged : false
		}
		sendData.type = "disable";
		sendData.url = "${ctxRoot}/api/system/code/disable";
		sendData.data.codeid = sSubCodeID;
		
		fn_CodeSub.publicCommon(sendData);

	},
	restore : function() {	//상위 코드를 복원
		sSubCodeID = fn_CodeSub.select.codeid;
		if (!sSubCodeID) {
			alert("<spring:eval expression="@${msgLang}['SELECT_CODE_RESTORE']"/>");
			return;
		}
		
		if(confirm("<spring:eval expression="@${msgLang}['DO_YOU_RESTORE_CODE_INFO']"/>") == false){
			return;
		}
		
		var sendData = {};
		sendData.data = {objIsTest : "N"
			, objDebugged : false
		}
		sendData.type = "restore";
		sendData.url = "${ctxRoot}/api/system/code/enable";
		sendData.data.codeid = sSubCodeID;
		
		fn_CodeSub.publicCommon(sendData);
	},
	discard : function() {	//상위 코드를 폐기
		sSubCodeID = fn_CodeSub.select.codeid;
		if (!sSubCodeID) {
			alert("<spring:eval expression="@${msgLang}['SELECT_CODE_DISCARD']"/>");
			return;
		}
		
		if(confirm("<spring:eval expression="@${msgLang}['ARE_YOU_DISCARD_CODE_INFO']"/>") == false){
			return;
		}
		
		var sendData = {};
		sendData.data = {objIsTest : "N"
			, objDebugged : false
		}
		sendData.type = "discard";
		sendData.url = "${ctxRoot}/api/system/code/discard";
		sendData.data.codeid = sSubCodeID;
		
		fn_CodeSub.publicCommon(sendData);
	},
	getCodeList : function(type) {	//하위 코드 정보 조회하기
		// desc : 선택된 코드 구분을 조회해서 리스트로 가져온다 , isactive 가 Y인값만 가져온다
		// param : type (String) ex)01, 02, 03...
		$("#CodeList").empty();
		$(".inner_tbl_line").remove(".pc_none");

		//CommonInclude.jsp의 코드 조회 함수 호출
		var result = sysCodeList("${ctxRoot}", type, companyid, "isactive_all");
		console.log("getCodeList result : "+JSON.stringify(result));
		
		$.each(result, function(index, item) {
			//PC 모드
			var $tr = $("<tr></tr>");
			var innerHtml = "";
			innerHtml += "<td id='num'>" + (index+1) + "</td>";
			innerHtml += "<td id='codevalue'>" + item.codevalue + "</td>";
			innerHtml += "<td id='name'>" + item.name + "</td>";
			innerHtml += "<td id='isactive'>"
					+ ((item.isactive == 'Y') ? "<spring:eval expression="@${lang}['USE']"/>" : "<spring:eval expression="@${lang}['NOT_USE']"/>")
					+ "</td>";
			$tr.append(innerHtml);
			$tr.data("meta", item);
			$("#CodeList").append($tr);
			
			//모바일 모드
			innerHtml = "";
			var $div = $("<div class='inner_tbl_line pc_none' id='MobileCodeList'></div>");
			innerHtml += "<p class='sub'>"+"<spring:eval expression="@${lang}['ORDER']"/>"+"</p>";
			innerHtml += "<p class='text'>" + (index+1) + "</p>";
			innerHtml += "<p class='sub'>"+"<spring:eval expression="@${lang}['CODE']"/>"+"</p>";
			innerHtml += "<p class='text'>" + (item.codevalue) + "</p>";
			innerHtml += "<p class='sub'>"+"<spring:eval expression="@${lang}['CODE_NAME']"/>"+"</p>";
			innerHtml += "<p class='text'>" + (item.name) + "</p>";
			innerHtml += "<p class='sub'>"+"<spring:eval expression="@${lang}['USE_OR_NOT']"/>"+"</p>";
			innerHtml += "<p class='text'>" + ((item.isactive == 'Y') ? "<spring:eval expression="@${lang}['USE']"/>" : "<spring:eval expression="@${lang}['NOT_USE']"/>") + "</p>";

			$div.append(innerHtml);
			$div.data('meta', item);
			$("#pc_none_tbl_line").after($div);
		});
	},
	publicCommon : function(sendData) {	//공통 처리 로직 호출
		console.log("CodeSub publicCommon sendData "+sendData.type+":", JSON.stringify(sendData));
		$.ajax({
			type : 'POST',
			url : sendData.url,
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData.data),
			success : function(data) {
				console.log("CodeSub publicCommon data "+sendData.type+":", JSON.stringify(data));
				if (data.status == "0000") {
					messageNotice(sendData.type);
					fn_CodeSub.getCodeList(fn_CodeMain.select.types);
				} else {
					alertErr(data.message);
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
				resetInputSub();
			},
			complete : function() {
				resetInputSub();
			}
		});
	}
}


/*********************************
Name   : resetInputMain
Desc   : 초기화
Param  : 없음
**********************************/
var resetInputMain = function() {
	$("input[id^=Main]").val('');
	$("input[id^=MobMain]").val('');
	$("#btnMainDel").hide();
	$("#btnMainDis").hide();
	$("#btnMainRes").hide();
	$("#MainCodeTypes").attr("disabled", false);
	$("#MainCodeKey").attr("disabled", false);
	$("#MobMainCodeTypes").attr("disabled", false);
	$("#MobMainCodeKey").attr("disabled", false);
	$("#MainCodeState").val("사용");
	$("#MobMainCodeState").val("사용");
	$("#CodeList").empty();
	$(".inner_tbl_line").remove(".pc_none");
	fn_CodeMain.select = {};
}

/*********************************
Name   : resetInputSub
Desc   : 초기화
Param  : 없음
**********************************/
var resetInputSub = function() {
	$("input[id^=Sub]").val('');
	$("input[id^=MobSub]").val('');
	$("#btnSubRes").hide();
	$("#btnSubDel").hide();
	$("#btnSubDis").hide();
	fn_CodeSub.select = {};
}

/*********************************
Name   : messageNotice
Desc   : 등록 수정 삭제 폐기 시 해당 정보를 알럿창으로 보여줌
Param  : type (add change discard disable )
**********************************/
function messageNotice(type) { 
	var message = '';
	if (type == 'add') {
		message = "<spring:eval expression="@${msgLang}['REGISTERED_CODE']"/>";
	} else if (type == 'change') {
		message = "<spring:eval expression="@${msgLang}['MODIFIED_CODE']"/>";
	} else if (type == 'disable') {
		message = "<spring:eval expression="@${msgLang}['DISABLED_CHANGE_CODE']"/>";
	} else if (type == 'restore') {
		message = "<spring:eval expression="@${msgLang}['RESTORED_CODE']"/>";
	} else if (type == 'discard') {
		message = "<spring:eval expression="@${msgLang}['DELETED_CODE']"/>";
	}
	if (message) {
		alert(message);
	}
}

/*********************************
Name   : isEmptyInput
Desc   : input val check, 전체 입력값 체크
Param  : obj
 **********************************/
var isEmptyInput = function(obj) {
	var isEmpty = true;
	var inputObjs = obj;
	inputObjs.each(function() {
		if (($(this).val() == '')) {
			isEmpty = false;
			var message = $(this).attr('title');
			message += "\n";
			message += "<spring:eval expression="@${msgLang}['INVALID_INFO']"/>";
			alert(message);
			$(this).focus();
			return false;
		}
	});
	return isEmpty;
}
</script>
</head>
<body>

	<!--header stard-->
	<c:import url="../common/TopPage.jsp" />
	<!--header end-->
	<main>
		<div class="flx">
			<c:import url="../common/AdminLeftMenu.jsp" />
		
			<section id="content">
				<div class="innerWrap">
					<div class="full-content">
						<h2 class="pageTit"><img src="${image}/icon/icon_b07.png" alt=""><spring:eval expression="@${lang}['CODE_MANAGEMENT']" /></h2>			
						<div class="wdt100">
							<h3 class="innerTit"><spring:eval expression="@${lang}['CODE_TYPE']" /></h3>
							<div class="btn_wrap">
								<button type="button" class="btbase" id="btnMainInit" name="new"><spring:eval expression="@${lang}['INITIALIZATION']" /></button>
								<button type="button" class="btbase" id="btnMainNew" name="add"><spring:eval expression="@${lang}['SAVE']" /></button>
								<button type="button" class="btbase" id="btnMainRes" name="res"><spring:eval expression="@${lang}['RESTORE']" /></button>
								<button type="button" class="btbase" id="btnMainDel" name="del"><spring:eval expression="@${lang}['DELETE']" /></button>
								<button type="button" class="btbase" id="btnMainDis" name="dis"><spring:eval expression="@${lang}['DISCARD']" /></button>
							</div>
							<table class="inner_tbl mob_none">
								<colgroup>
									<col width="15%">
									<col width="15%">
									<col width="15%">
									<col width="15%">
									<col width="15%">
									<col width="10%">
								</colgroup>
								<thead>
					                <th><spring:eval expression="@${lang}['CODE_SEPARATION']" /></th>
					                <th><spring:eval expression="@${lang}['CODE_NAME']" /></th>
					                <th><spring:eval expression="@${lang}['CODE_VALUE']" /></th>
					                <th><spring:eval expression="@${lang}['CODE_TYPE']" /></th>
					                <th><spring:eval expression="@${lang}['CODE_KEY']" /></th>
					                <th><spring:eval expression="@${lang}['USE_OR_NOT']" /></th>
								</thead>
								<tbody>
									<tr>
										<td>
											<select id="MainCodeTypeList" title="<spring:eval expression="@${lang}['CODE_SEPARATION']"/>">
											</select>
										</td>
										<td>
											<input type="text" id="MainCodeName" title="<spring:eval expression="@${lang}['CODE_NAME']"/>" onkeyup='pubByteCheckTextarea(event,150)' />
											<!-- <input type="hidden" id="MainCodeID" value="" /> -->
										</td>
										<td><input type="text" id="MainCodeValue" title="<spring:eval expression="@${lang}['CODE_VALUE']"/>" onkeyup='pubByteCheckTextarea(event,50)' /></td>
										<td><input type="text" id="MainCodeTypes" title="<spring:eval expression="@${lang}['CODE_TYPE']"/>" onkeyup='pubByteCheckTextarea(event,2)' /></td>
										<td><input type="text" id="MainCodeKey" title="<spring:eval expression="@${lang}['CODE_KEY']"/>" onkeyup='pubByteCheckTextarea(event,64)' /></td>
										<td><input type="text" id="MainCodeState" disabled title="<spring:eval expression="@${lang}['USE_OR_NOT']"/>"/></td>
									</tr>
								</tbody>
							</table>
							<div class="inner_uiGroup pc_none">
								<p><spring:eval expression="@${lang}['CODE_SEPARATION']" /></p>
									<select id="MobMainCodeTypeList" title="<spring:eval expression="@${lang}['CODE_SEPARATION']"/>">
									</select>
								<p><spring:eval expression="@${lang}['CODE_NAME']" /></p>
								<input type="text" id="MobMainCodeName" title="<spring:eval expression="@${lang}['CODE_NAME']"/>" onkeyup='pubByteCheckTextarea(event,150)' />
								<p><spring:eval expression="@${lang}['CODE_VALUE']" /></p>
								<input type="text" id="MobMainCodeValue" title="<spring:eval expression="@${lang}['CODE_VALUE']"/>" onkeyup='pubByteCheckTextarea(event,50)' />
								<p><spring:eval expression="@${lang}['CODE_TYPE']" /></p>
								<input type="text" id="MobMainCodeTypes" title="<spring:eval expression="@${lang}['CODE_TYPE']"/>" onkeyup='pubByteCheckTextarea(event,2)' />
								<p><spring:eval expression="@${lang}['CODE_KEY']" /></p>
								<input type="text" id="MobMainCodeKey" title="<spring:eval expression="@${lang}['CODE_KEY']"/>" onkeyup='pubByteCheckTextarea(event,64)' />
								<p><spring:eval expression="@${lang}['USE_OR_NOT']" /></p>
								<input type="text" id="MobMainCodeState" disabled title="<spring:eval expression="@${lang}['USE_OR_NOT']"/>" />
							</div>
						</div> 
						<div class="wdt100">
							<h3 class="innerTit"><spring:eval expression="@${lang}['CODE_INFO']" /></h3>
							<div class="btn_wrap">
								<button type="button" class="btbase" id="btnSubInit" name="new"><spring:eval expression="@${lang}['INITIALIZATION']" /></button>
								<button type="button" class="btbase" id="btnSubNew" name="add"><spring:eval expression="@${lang}['SAVE']" /></button>
								<button type="button" class="btbase" id="btnSubRes" name="res"><spring:eval expression="@${lang}['RESTORE']" /></button>
								<button type="button" class="btbase" id="btnSubDel" name="del"><spring:eval expression="@${lang}['DELETE']" /></button>
								<button type="button" class="btbase" id="btnSubDis" name="dis"><spring:eval expression="@${lang}['DISCARD']" /></button>
							</div>
							<table class="inner_tbl mob_none">
								<colgroup>
									<col width="50%">
									<col width="50%">
								</colgroup>
								<thead>
									<th><spring:eval expression="@${lang}['CODE']" /></th>
									<th><spring:eval expression="@${lang}['CODE_NAME']" /></th>
								</thead>
								<tbody>
									<tr>
										<td><input type="text" id="SubCodeValue" title="<spring:eval expression="@${lang}['CODE']"/>" onkeyup='pubByteCheckTextarea(event,150)' /></td>
										<td><input type="text" id="SubCodeName" title="<spring:eval expression="@${lang}['CODE_NAME']"/>" onkeyup='pubByteCheckTextarea(event,150)' /></td>
									</tr>
								</tbody>
							</table>
							<div class="inner_uiGroup pc_none">
								<p><spring:eval expression="@${lang}['CODE']" /></p>
								<input type="text" id="MobSubCodeValue" title="<spring:eval expression="@${lang}['CODE']"/>" onkeyup='pubByteCheckTextarea(event,150)' />
								<p><spring:eval expression="@${lang}['CODE_NAME']" /></p>
								<input type="text" id="MobSubCodeName" title="<spring:eval expression="@${lang}['CODE_NAME']"/>" onkeyup='pubByteCheckTextarea(event,150)' />
							</div>                                
						</div> 
						<div class="tbl_wrap_admin_code">
							<table class="inner_tbl mob_none" id="pc_none_tbl_line">
								<colgroup>
									<col width="6%">
									<col width="32%">
									<col width="32%">
									<col width="30%">
								</colgroup>
								<thead>
									<th><spring:eval expression="@${lang}['ORDER']" /></th>
									<th><spring:eval expression="@${lang}['CODE']" /></th>
									<th><spring:eval expression="@${lang}['CODE_NAME']" /></th>
									<th><spring:eval expression="@${lang}['STATE']" /></th>
								</thead>
							</table>
							<div class="tbody_wrap_code mob_none">
								<table>
									<colgroup>
										<col width="6%">
										<col width="32%">
										<col width="32%">
										<col width="30%">
									</colgroup>
									<tbody id="CodeList">
	
									</tbody>
								</table>
							</div>								
						</div>
					</div>
				</div><!--innerWrap//-->
			</section>
		</div>
	</main>
</body>
</html>

