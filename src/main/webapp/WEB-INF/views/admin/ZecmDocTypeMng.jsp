<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*"%>
<%@include file="../common/CommonInclude.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />    
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0, minimum-scale=1.0, maximum-scale=2.0, user-scalable=no">
<title>NADi4.0 :: <spring:eval expression="@${lang}['DOC_TYPE_MANAGEMENT']" /></title>
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
var sDocName = "";		//리스트 선택 문서유형명
var sDocCode = "";		//리스트 선택 문서유형 코드
var sDocRetention = "";	//리스트 선택 문서유형 보존년한
var sDocTypeID = "";	//리스트 선택 문서유형 아이디
$(document).ready( function() {
	//입력값 초기화
	resetInput();
	
	//초기화 버튼
	$("#btnInit").click(function() {
		resetInput();
	});

	//저장 버튼
	$("#btnNew").click(function() {
		if (objectIsEmpty(fn_Common.select)) {
			fn_Common.save("add");
		} else {
			fn_Common.save("change");
		}
	});
	
	//삭제 버튼
	$("#btnDel").click(function() {
		fn_Common.disable();
	});

	//복구 버튼
	$("#btnRes").click(function() {
		fn_Common.restore();
	});

	//폐기 버튼
	$("#btnDis").click(function() {
		fn_Common.discard();
	});

	//문서유형 리스트 클릭
	$(document.body).delegate('#DocTypeList tr', 'click', function() {
		$("input[id^='Doc']").val('');
		$("input[id^='Mob']").val('');
		var meta = $(this).data("meta");
		fn_Common.select = meta;
		$("#btnNew").show();
		$("#DocName").val(meta.name);
		$("#DocCode").val(meta.code);
		$("#DocRetention").val(meta.retentionid);
		$("#DocCode").attr("disabled", true);
		
		if (meta.isactive == "Y") {
			$("#btnDel").show();
			$("#btnRes").hide();
			$("#btnDis").hide();
		} else {
			$("#btnDel").hide();
			$("#btnRes").show();
			$("#btnDis").show();
		}
		
		// 문서유형 일반문서일때 삭제 불가하도록 수정.	
		// [문서 드래그 등록등 빠른등록시 디폴트값으로 사용하고 있어, 데이터 삭제 방지위해 추가]
		if(meta.code == "GENERAL"){	
			$("#btnDel").hide();
			$("#btnRes").hide();
			$("#btnDis").hide();
		}	
		
	});
	
	//모바일 문서유형 리스트 클릭
	$(document.body).delegate('#MobileDocTypeList p', 'click', function() {
		$("input[id^='Doc']").val('');
		$("input[id^='Mob']").val('');		
		var meta = $(this).parent().data("meta");
		fn_Common.select = meta;
		$("#MobDocName").val(meta.name);
		$("#MobDocCode").val(meta.code);
		$("#MobDocRetention").val(meta.retentionid);
		$("#MobDocCode").attr("disabled", true);
		
		if (meta.isactive == "Y") {
			$("#btnDel").show();
			$("#btnRes").hide();
			$("#btnDis").hide();
		} else {
			$("#btnDel").hide();
			$("#btnRes").show();
			$("#btnDis").show();
		}
		
		// 문서유형 일반문서일때 삭제 불가하도록 수정.	
		// [문서 드래그 등록등 빠른등록시 디폴트값으로 사용하고 있어, 데이터 삭제 방지위해 추가]
		if(meta.code == "GENERAL"){
			$("#btnDel").hide();
			$("#btnRes").hide();
			$("#btnDis").hide();
		}	
	});
	
	
	fn_Common.getRetentionList();	// 보존년한 목록 조회

});

/*********************************
Name   : fn_Common
Desc   : 공통 처리 함수
Param  : 없음
**********************************/
var fn_Common = {
	select : {},			//선택한 리스트 전체 값
	save : function(type) {	//문서유형 정보 저장
		if(window.innerWidth < 1101){	//모바일 화면 처리
			sDocName = $.trim($("#MobDocName").val());
			sDocCode = $.trim($("#MobDocCode").val());
			sDocRetention = $.trim($("#MobDocRetention").val());
			if (!sDocName) {
				alert("<spring:eval expression="@${msgLang}['ENTER_CONTENT_TYPE_NAME']"/>");
				$("#MobDocName").focus();
				return;
			}
			if (!sDocCode) {
				alert("<spring:eval expression="@${msgLang}['ENTER_CONTENT_TYP_CODE']"/>");
				$("#MobDocCode").focus();
				return;
			}		
		}else{		//PC 화면 처리
			sDocName = $.trim($("#DocName").val());		//선택 문서유형명
			sDocCode = $.trim($("#DocCode").val());		//선택 문서유형 코드
			sDocRetention = $.trim($("#DocRetention").val());	//선택 문서유형 보존년한
			if (!sDocName) {
				alert("<spring:eval expression="@${msgLang}['ENTER_CONTENT_TYPE_NAME']"/>");
				$("#DocName").focus();
				return;
			}
			if (!sDocCode) {
				alert("<spring:eval expression="@${msgLang}['ENTER_CONTENT_TYPE_CODE']"/>");
				$("#DocCode").focus();
				return;
			}
		}

		if (type == "change") {
			if (sDocName == fn_Common.select.name
					&& sDocCode == fn_Common.select.code
					&& sDocRetention == fn_Common.select.retentionid) {
				alert("<spring:eval expression="@${msgLang}['NO_ALERTED_CONTENT']"/>");
				return;
			}
		}

		var sendData = {};
		sendData.type = type;	//처리방식 (add:등록, change:수정)
		sendData.url = "${ctxRoot}/api/classification/" + type;
		sendData.data = {objIsTest : "N"
				, objDebugged : false
				, companyid : companyid
				, holderid : deptuserid
				, types : CLSTYPES["DOCTYPE"]
			}
		sendData.data.name = sDocName;
		sendData.data.code = sDocCode;
		sendData.data.upid = companyid;
		sendData.data.retentionid = sDocRetention;
		sendData.data.classid = fn_Common.select.classid;

		fn_Common.publicCommon(sendData);
	},
	disable : function() {	//문서유형 정보 삭제 플레그 처리
		sDocTypeID = fn_Common.select.classid;
		if (!sDocTypeID) {
			alert("<spring:eval expression="@${msgLang}['SELECT_CONTENT_TYPE_DELETE']"/>");
			return;
		}
		
		if(confirm("<spring:eval expression="@${msgLang}['ARE_YOU_DELETE_CONTENT_TYPE']"/>") == false){
			return;
		}
		
		var sendData = {};
		sendData.data = {objIsTest : "N"
			, objDebugged : false
		}
		sendData.type = "disable";
		sendData.url = "${ctxRoot}/api/classification/disable";
		sendData.data.classid = fn_Common.select.classid;
		sendData.data.objIncLower = "Y";
		
		fn_Common.publicCommon(sendData);

	},
	restore : function() {	//문서유형 정보 복원
		sDocTypeID = fn_Common.select.classid;
		if (!sDocTypeID) {
			alert("<spring:eval expression="@${msgLang}['SELECT_CONTENT_TYPE_RESTORE']"/>");
			return;
		}
		
		if(confirm("<spring:eval expression="@${msgLang}['DO_YOU_RESTORE_CONTENT_TYPE']"/>") == false){
			return;
		}
		
		var sendData = {};
		sendData.data = {objIsTest : "N"
			, objDebugged : false
		}
		sendData.type = "restore";
		sendData.url = "${ctxRoot}/api/classification/enable";
		sendData.data.classid = fn_Common.select.classid;
		sendData.data.objIncLower = "Y";
		
		fn_Common.publicCommon(sendData);
	},
	discard : function() {	//문서유형 정보 폐기
		sDocTypeID = fn_Common.select.classid;
		if (!sDocTypeID) {
			alert("<spring:eval expression="@${msgLang}['SELECT_CONTENT_TYPE_DISCARD']"/>");
			return;
		}
		
		if(confirm("<spring:eval expression="@${msgLang}['ARE_YOU_DISCARD_CONTENT_TYPE']"/>") == false){
			return;
		}
		
		var sendData = {};
		sendData.data = {objIsTest : "N"
			, objDebugged : false
		}
		sendData.type = "discard";
		sendData.url = "${ctxRoot}/api/classification/discard";
		sendData.data.classid = fn_Common.select.classid;
		
		fn_Common.publicCommon(sendData);
	},
	getDocList : function() {	//문서유형 정보 조회하기
		var sendData = {
				"objIsTest" : "N",
				"objDebugged" : false,
				"types" : CLSTYPES["DOCTYPE"],	//03
				"objmaporder" : {"priority":"asc"}
		}
		
		console.log("getDocList sendData : "+JSON.stringify(sendData));
		
		$.ajax({
			url : "${ctxRoot}/api/classification/list",
			type : "POST",
			dataType : "json",
			contentType : "application/json",
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
				console.log("getDocList data : "+JSON.stringify(data));
				if (data.status == "0000") {
					$("#DocTypeList").empty();
					$(".inner_tbl_line").remove(".pc_none");
					
					if (objectIsEmpty(data.result)) {
						return;
					}
					
					$.each(data.result, function(index, result) {
						//PC 모드
						var $tr = $("<tr></tr>");
						var innerHtml = "";
						innerHtml += "<td id='name'>" + result.name + "</td>";
						innerHtml += "<td id='code'>" + result.code + "</td>";
						var retentionName = "";
						var retentions = document.getElementById("DocRetention").options;
						console.log("getDocList retentions : "+JSON.stringify(retentions));
						$.each(retentions, function(index1, result1) {
							if (result.retentionid == retentions[index1].value) {
								retentionName = retentions[index1].text;
							}
						});
						innerHtml += "<td id='retention'>" + retentionName + "</td>";
						innerHtml += "<td id='isactive'>"
								+ ((result.isactive == 'Y') ? "<spring:eval expression="@${lang}['USE']"/>" : "<spring:eval expression="@${lang}['NOT_USE']"/>")
								+ "</td>";
						$tr.append(innerHtml);
						$tr.data("meta", result);
						$("#DocTypeList").append($tr);
						
						//모바일 모드
						innerHtml = "";
						var $div = $("<div class='inner_tbl_line pc_none' id='MobileDocTypeList'></div>");
						innerHtml += "<p class='sub'>"+"<spring:eval expression="@${lang}['DOC_TYPE']"/>"+"</p>";
						innerHtml += "<p class='text'>" + (result.name) + "</p>";
						innerHtml += "<p class='sub'>"+"<spring:eval expression="@${lang}['DOC_CODE']"/>"+"</p>";
						innerHtml += "<p class='text'>" + (result.code) + "</p>";
						innerHtml += "<p class='sub'>"+"<spring:eval expression="@${lang}['RETENTION_PERIOD']"/>"+"</p>";
						innerHtml += "<p class='text'>" + (retentionName) + "</p>";
						innerHtml += "<p class='sub'>"+"<spring:eval expression="@${lang}['USE_OR_NOT']"/>"+"</p>";
						innerHtml += "<p class='text'>" + ((result.isactive == 'Y') ? "<spring:eval expression="@${lang}['USE']"/>" : "<spring:eval expression="@${lang}['NOT_USE']"/>") + "</p>";

						$div.append(innerHtml);
						$div.data('meta', result);
						$("#pc_none_tbl_line").after($div);
					});
				} else {
					alertErr(data.message);
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			},
			complete : function() {
				resetInput();
			}
		});
	},
	getRetentionList : function() {	//보존년한 정보 조회하기
		var sendData = {
				"objIsTest" : "N",
				"isactive" : "Y：N",
				"companyid" : companyid,
				"types" : "05",
				"objmaporder" : {"priority":"asc"}
		};
	
		//console.log("getRetentionList sendData : "+JSON.stringify(sendData));

		$.ajax({
			url : "${ctxRoot}/api/system/code/list",
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
				//console.log("retentionList data : "+JSON.stringify(data));
				if (data.status == "0000") {
					$("#DocRetention").empty();
					$("#MobDocRetention").empty();
					
					var option = "";
					$.each(data.result, function(idx, result) {
						if (idx == 0) { //수정필요 영문 or 한글 값 
							option += "<option id='regRetentionid' value='"+result.codeid+"' selected>" + result.name + "</option>";
						} else {
							option += "<option id='regRetentionid' value='"+result.codeid+"'>" + result.name + "</option>";
						}
					});
					$("#DocRetention").append(option);
					$("#MobDocRetention").append(option);
					
					fn_Common.getDocList();			// 문서유형 조회
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
	publicCommon : function(sendData) {	//공통 처리 로직 호출
		console.log("publicCommon sendData "+sendData.type+":", JSON.stringify(sendData));
		$.ajax({
			type : 'POST',
			url : sendData.url,
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData.data),
			success : function(data) {
				console.log("publicCommon data "+sendData.type+":", JSON.stringify(data));
				if (data.status == "0000") {
					messageNotice(sendData.type);
					fn_Common.getDocList();
				} else {
					alertErr(data.message);
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
				resetInput();
			},
			complete : function() {
				resetInput();
			}
		});
	}
}


/*********************************
Name   : resetInput
Desc   : 초기화
Param  : 없음
**********************************/
var resetInput = function() {
	$("input[id^=Doc]").val('');
	$("input[id^=Mob]").val('');
	$("#DocCode").attr("disabled", false);
	$("#MobDocCode").attr("disabled", false);
	$("#btnRes").hide();
	$("#btnDel").hide();
	$("#btnDis").hide();
	fn_Common.select = {};
}

/* 
Name : messageNotice
Desc : 등록 수정 삭제 폐기 시 해당 정보를 알럿창으로 보여줌
Param : type (add change discard disable )
*/
function messageNotice(type) {

	var message = '';
	if (type == 'add') {
		message = "<spring:eval expression="@${msgLang}['REGISTERED_CONTENT_TYPE']"/>";
	} else if (type == 'change') {
		message = "<spring:eval expression="@${msgLang}['MODIFIED_CONTENT_TYPE']"/>";
	} else if (type == 'disable') {
		message = "<spring:eval expression="@${msgLang}['DISABLED_CHANGE_CONTENT_TYPE']"/>";
	} else if (type == 'restore') {
		message = "<spring:eval expression="@${msgLang}['RESTORED_CONTENT_TYPE']"/>";
	} else if (type == 'discard') {
		message = "<spring:eval expression="@${msgLang}['DELETED_CONTENT_TYPE']"/>";
	}
	if (message) {
		alert(message);
	}
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
						<h2 class="pageTit"><img src="${image}/icon/icon_b02.png" alt=""><spring:eval expression="@${lang}['DOC_TYPE_MANAGEMENT']" /></h2>			
						<div class="wdt100">
							<h3 class="innerTit"><spring:eval expression="@${lang}['DOC_TYPE']" /></h3>
							<div class="btn_wrap">
								<button type="button" class="btbase" id="btnInit" name="new"><spring:eval expression="@${lang}['INITIALIZATION']" /></button>
								<button type="button" class="btbase" id="btnNew" name="add"><spring:eval expression="@${lang}['SAVE']" /></button>
								<button type="button" class="btbase" id="btnRes" name="res"><spring:eval expression="@${lang}['RESTORE']" /></button>
								<button type="button" class="btbase" id="btnDel" name="del"><spring:eval expression="@${lang}['DELETE']" /></button>
								<button type="button" class="btbase" id="btnDis" name="dis"><spring:eval expression="@${lang}['DISCARD']" /></button>
							</div>
							<table class="inner_tbl mob_none">
								<colgroup>
									<col width="34%">
									<col width="33%">
									<col width="33%">
								</colgroup>
								<thead>
									<th><spring:eval expression="@${lang}['DOC_TYPE']" /></th>
									<th><spring:eval expression="@${lang}['DOC_CODE']" /></th>
									<th><spring:eval expression="@${lang}['RETENTION_PERIOD']" /></th>
								</thead>
								<tbody>
									<tr>
										<td><input type="text" id="DocName" title="<spring:eval expression="@${lang}['DOC_TYPE']"/>" onkeyup='pubByteCheckTextarea(event,64)' /></td>
										<td><input type="text" id="DocCode" title="<spring:eval expression="@${lang}['DOC_CODE']"/>" onkeyup='pubByteCheckTextarea(event,64)' /></td>
										<td>
											<select id="DocRetention" title="<spring:eval expression="@${lang}['RETENTION_PERIOD']"/>">
											</select>
										</td>
									</tr>
								</tbody>
							</table>
							<div class="inner_uiGroup pc_none">
								<p><spring:eval expression="@${lang}['DOC_TYPE']" /></p>
								<input type="text" id="MobDocName" title="<spring:eval expression="@${lang}['DOC_TYPE']"/>" onkeyup='pubByteCheckTextarea(event,100)' />
								<p><spring:eval expression="@${lang}['DOC_CODE']" /></p>
								<input type="text" id="MobDocCode" title="<spring:eval expression="@${lang}['DOC_CODE']"/>" onkeyup='pubByteCheckTextarea(event,100)' />
								<p><spring:eval expression="@${lang}['RETENTION_PERIOD']" /></p>
									<select id="MobDocRetention" title="<spring:eval expression="@${lang}['RETENTION_PERIOD']"/>">
									</select>
							</div>                                
						</div> 
						<div class="tbl_wrap_admin">
							<table class="inner_tbl mob_none" id="pc_none_tbl_line">
								<colgroup>
									<col width="30%">
									<col width="25%">
									<col width="25%">
									<col width="20%">
								</colgroup>
								<thead>
									<th><spring:eval expression="@${lang}['DOC_TYPE']" /></th>
									<th><spring:eval expression="@${lang}['DOC_CODE']" /></th>
									<th><spring:eval expression="@${lang}['RETENTION_PERIOD']" /></th>
									<th><spring:eval expression="@${lang}['USE_OR_NOT']" /></th>
								</thead>
							</table>
							<div class="tbody_wrap mob_none">
								<table>
									<colgroup>
										<col width="30%">
										<col width="25%">
										<col width="25%">
										<col width="20%">
									</colgroup>
									<tbody id="DocTypeList">
	
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
