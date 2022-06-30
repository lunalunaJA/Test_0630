<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*"%>
<%@include file="../common/CommonInclude.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />    
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0, minimum-scale=1.0, maximum-scale=2.0, user-scalable=no">
<title>NADi4.0 :: <spring:eval expression="@${lang}['COMPANY_MANAGEMENT']" /></title>
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
var sCompanyID = "";	//리스트 선택 기업아이디
var sCompanyName = "";	//리스트 선택 기업명
var sCompanyCode = "";	//리스트 선택 기업 코드
var sCompanyAddr = "";	//리스트 선택 기업 주소
var sCompanyTel = "";	//리스트 선택 기업 전화번호

$(document).ready(function() {
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

	//기업 리스트 클릭
	$(document.body).delegate('#CompanyList tr', 'click', function() {
		$("input[id^='Com']").val('');
		$("input[id^='Mob']").val('');
		var meta = $(this).data("meta");
		fn_Common.select = meta;
		$("#CompanyCode").prop("disabled", true);
		$("#CompanyName").val(meta.name);
		$("#CompanyCode").val(meta.code);
		$("#CompanyTel").val(meta.tel);
		$("#CompanyAddr").val(meta.address);
		if (meta.isactive == "Y") {
			$("#btnDel").show();
			$("#btnRes").hide();
			$("#btnDis").hide();
		} else {
			$("#btnDel").hide();
			$("#btnRes").show();
			$("#btnDis").show();
		}
	});
	
	//모바일 기업 리스트 클릭
	$(document.body).delegate('#MobileCompanyList p', 'click', function() {
		$("input[id^='Com']").val('');
		$("input[id^='Mob']").val('');
		var meta = $(this).parent().data("meta");
		fn_Common.select = meta;
		$("#MobCompanyCode").prop("disabled", true);
		$("#MobCompanyName").val(meta.name);
		$("#MobCompanyCode").val(meta.code);
		$("#MobCompanyTel").val(meta.tel);
		$("#MobCompanyAddr").val(meta.address);
		if (meta.isactive == "Y") {
			$("#btnDel").show();
			$("#btnRes").hide();
			$("#btnDis").hide();
		} else {
			$("#btnDel").hide();
			$("#btnRes").show();
			$("#btnDis").show();
		}
	});

	//기업 목록 가져오기
	fn_Common.getCompanyList();
});

/*********************************
Name   : fn_Common
Desc   : 공통 처리 함수
Param  : 없음
**********************************/
var fn_Common = {
	select : {},			//선택한 리스트 전체 값
	save : function(type) {	//기업 정보 저장
		sCompanyID = fn_Common.select.companyid;
		if(window.innerWidth < 1101){	//모바일 화면 처리
			sCompanyName = $.trim($("#MobCompanyName").val());
			sCompanyCode = $.trim($("#MobCompanyCode").val());
			sCompanyAddr = $.trim($("#MobCompanyAddr").val());
			sCompanyTel = $.trim($("#MobCompanyTel").val());
			if (!sCompanyName) {
				alert("<spring:eval expression="@${msgLang}['ENTER_NAME_ORGANIZATION']"/>");
				$("#MobCompanyName").focus();
				return;
			}
			if (!sCompanyCode) {
				alert("<spring:eval expression="@${msgLang}['ENTER_ORGAN_CODE']"/>");
				$("#MobCompanyCode").focus();
				return;
			}			
		}else{		//PC 화면 처리
			sCompanyName = $.trim($("#CompanyName").val());	//선택 기업명
			sCompanyCode = $.trim($("#CompanyCode").val());	//선택 기업 코드
			sCompanyAddr = $.trim($("#CompanyAddr").val());	//선택 기업 주소
			sCompanyTel = $.trim($("#CompanyTel").val());	//선택 기업 전화번호
			if (!sCompanyName) {
				alert("<spring:eval expression="@${msgLang}['ENTER_NAME_ORGANIZATION']"/>");
				$("#CompanyName").focus();
				return;
			}
			if (!sCompanyCode) {
				alert("<spring:eval expression="@${msgLang}['ENTER_ORGAN_CODE']"/>");
				$("#CompanyCode").focus();
				return;
			}
		}

		if (type == "change") {
			if (sCompanyName == fn_Common.select.name
					&& sCompanyAddr == fn_Common.select.address
					&& sCompanyTel == fn_Common.select.tel) {
				alert("<spring:eval expression="@${msgLang}['NO_ALERTED_CONTENT']"/>");
				return;
			}
		}
		
		var sendData = {};
		sendData.type = type;	//처리방식 (add:등록, change:수정)
		sendData.url = "${ctxRoot}/api/organ/company/" + type;
		sendData.data = {objIsTest : "N"}
		sendData.data.name = sCompanyName;
		sendData.data.code = sCompanyCode;
		sendData.data.abbrname = sCompanyCode;
		sendData.data.tel = sCompanyTel;
		sendData.data.address = sCompanyAddr;
		sendData.data.companyid = sCompanyID;
		
		fn_Common.publicCommon(sendData);
	},
	disable : function() {	//기업 정보 삭제 플레그 처리
		sCompanyID = fn_Common.select.companyid;
		if (!sCompanyID) {
			alert("<spring:eval expression="@${msgLang}['SELECT_INSTITUTION_DELETE']"/>");
			return;
		}
		
		if(confirm("<spring:eval expression="@${msgLang}['DELETE_INSTITUTION']"/>") == false){
			return;
		}
		
		var sendData = {};
		sendData.type = "disable";
		sendData.url = "${ctxRoot}/api/organ/company/disable";
		sendData.data = {
			objIsTest : "N",	//고정 값 사용
			companyid : sCompanyID
		};
		
		fn_Common.publicCommon(sendData);
	},
	restore : function() {	//기업정보 복원
		sCompanyID = fn_Common.select.companyid;
		if (!sCompanyID) {
			alert("<spring:eval expression="@${msgLang}['SELECT_INSTITUTION_RESTORE']"/>");
			return;
		}

		if(confirm("<spring:eval expression="@${msgLang}['RESTORED_AGENCY']"/>") == false){
			return;
		}
		
		var sendData = {};
		sendData.type = "restore";
		sendData.url = "${ctxRoot}/api/organ/company/enable";
		sendData.data = {
			objIsTest : "N",	//고정 값 사용
			companyid : sCompanyID
		};

		fn_Common.publicCommon(sendData);
	},
	discard : function() {	//기업 정보 폐기
		sCompanyID = fn_Common.select.companyid;
		if (!sCompanyID) {
			alert("<spring:eval expression="@${msgLang}['SELECT_INSTITUTION_DISCARDE']"/>");
			return;
		}

		if(confirm("<spring:eval expression="@${msgLang}['DISCARD_INSTITUTION']"/>") == false){
			return;
		}

		var sendData = {};
		sendData.type = "discard";
		sendData.url = "${ctxRoot}/api/organ/company/discard";
		sendData.data = {
			objIsTest : "N",	//고정 값 사용
			companyid : sCompanyID
		};

		fn_Common.publicCommon(sendData);
	},
	getCompanyList : function() {	//기업정보 조회하기
		if(userType == "04"){
			sCompanyID = "";
		}else{
			sCompanyID = companyid;
		}
		var sendData = {
			"objIsTest" : "N",
			"objDebugged" : false,
			"isactive" : "Y：N",
			"companyid" : sCompanyID,
			"objmaporder" : {"name":"asc"}
		};
		$.ajax({
			type : 'POST',
			url : '${ctxRoot}/api/organ/company/list',
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
				console.log("getCompanyList data:", JSON.stringify(data));
				if (data.status == "0000") {
					$("#CompanyList").empty();
					$(".inner_tbl_line").remove(".pc_none");
					if (objectIsEmpty(data.result)) {
						return;
					}
					
					$.each(data.result, function(index, result) {
						var $tr = $("<tr></tr>");
						if (result.isactive == 'N') {
							$tr.css("opacity", "0.3");
						}
						if(result.companyid.trim() == "DEFAULT")
							 return true;
						
						//PC 모드
						var innerHtml = "";						
						innerHtml += "<td>" + (index + 1) + "</td>";
						innerHtml += "<td>" + (result.name) + "</td>";
						innerHtml += "<td>" + (result.code) + "</td>";
						innerHtml += "<td>" + (result.address) + "</td>";
						innerHtml += "<td>" + (result.tel) + "</td>";
						innerHtml += "<td>" + ((result.isactive == "Y") ? "<spring:eval expression="@${lang}['USE']"/>" : "<spring:eval expression="@${lang}['NOT_USE']"/>") + "</td>";
						$tr.append(innerHtml);
						$tr.data('meta', result);
						$("#CompanyList").append($tr);
						
						//모바일 모드
						innerHtml = "";
						var $div = $("<div class='inner_tbl_line pc_none' id='MobileCompanyList'></div>");
						innerHtml += "<p class='sub'>"+"<spring:eval expression="@${lang}['COMPANY_NAME']"/>"+"</p>";
						innerHtml += "<p class='text'>" + (result.name) + "</p>";
						innerHtml += "<p class='sub'>"+"<spring:eval expression="@${lang}['CODE']"/>"+"</p>";
						innerHtml += "<p class='text'>" + (result.code) + "</p>";
						innerHtml += "<p class='sub'>"+"<spring:eval expression="@${lang}['ADDRESS']"/>"+"</p>";
						innerHtml += "<p class='text'>" + (result.address) + "</p>";
						innerHtml += "<p class='sub'>"+"<spring:eval expression="@${lang}['TELEPHONE_NUMBER']"/>"+"</p>";
						innerHtml += "<p class='text'>" + (result.tel) + "</p>";
						innerHtml += "<p class='sub'>"+"<spring:eval expression="@${lang}['USE_OR_NOT']"/>"+"</p>";
						innerHtml += "<p class='text'>" + ((result.isactive == "Y") ? "<spring:eval expression="@${lang}['USE']"/>" : "<spring:eval expression="@${lang}['NOT_USE']"/>") + "</p>";
						
						$div.append(innerHtml);
						$div.data('meta', result);
						$("#pc_none_tbl_line").after($div);
					});
				} else {
					alert(data.message);
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
					fn_Common.getCompanyList();
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
	$("#CompanyCode").prop("disabled", false);
	$("#MobCompanyCode").prop("disabled", false);
	$("input[id^='Com']").val('');
	$("input[id^='Mob']").val('');
	$("#btnRes").hide();
	$("#btnDel").hide();
	$("#btnDis").hide();

	fn_Common.select = {};
}

 /*********************************
 Name   : messageNotice
 Desc   : 등록 수정 삭제 폐기 시 해당 정보를 알럿창으로 보여줌
 Param  : type (add change discard disable )
 **********************************/
function messageNotice(type) {

	var message = '';
	if (type == 'add') {
		message = "<spring:eval expression="@${msgLang}['REGISTERED_AGENCY']"/>";
	} else if (type == 'change') {
		message = "<spring:eval expression="@${msgLang}['MODIFIED_AGENCY_NAME']"/>";
	} else if (type == 'disable') {
		message = "<spring:eval expression="@${msgLang}['DISABLED_CHANGE_AGENCY']"/>";
	} else if (type == 'restore') {
		message = "<spring:eval expression="@${msgLang}['RESTORED_AGENCY']"/>";
	} else if (type == 'discard') {
		message = "<spring:eval expression="@${msgLang}['DELETED_AGENCY']"/>";
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
						<h2 class="pageTit"><img src="${image}/icon/icon_b10.png" alt=""><spring:eval expression="@${lang}['COMPANY_MANAGEMENT']" /></h2>
							<div class="wdt100">
								<h3 class="innerTit"><spring:eval expression="@${lang}['COMPANY_INFO']" /></h3>
							<div class="btn_wrap">
							<c:if test="${Authentication.sessOnlyDeptUser.usertype eq '04'}">
								<button type="button" class="btbase" id="btnInit" name="new"><spring:eval expression="@${lang}['INITIALIZATION']" /></button>
								<button type="button" class="btbase" id="btnNew" name="add"><spring:eval expression="@${lang}['SAVE']" /></button>
								<button type="button" class="btbase" id="btnRes" name="res"><spring:eval expression="@${lang}['RESTORE']" /></button>
								<button type="button" class="btbase" id="btnDel" name="del"><spring:eval expression="@${lang}['DELETE']" /></button>
								<button type="button" class="btbase" id="btnDis" name="dis"><spring:eval expression="@${lang}['DISCARD']" /></button>
							</c:if>
							<c:if test="${Authentication.sessOnlyDeptUser.usertype eq '03'}">
								<button type="button" class="btbase" id="btnNew" name="add"><spring:eval expression="@${lang}['SAVE']" /></button>
							</c:if>								
							</div>
							<table class="inner_tbl mob_none">
								<colgroup>
									<col width="26%">
									<col width="17%">
									<col width="40%">
									<col width="17%">
								</colgroup>
								<thead>
									<th><spring:eval expression="@${lang}['COMPANY_NAME']" /></th>
									<th><spring:eval expression="@${lang}['CODE']" /></th>
									<th><spring:eval expression="@${lang}['ADDRESS']" /></th>
									<th><spring:eval expression="@${lang}['TELEPHONE_NUMBER']" /></th>
								</thead>
								<tbody>
									<tr>
										<td><input type="text" id="CompanyName" title="<spring:eval expression="@${lang}['COMPANY_NAME']"/>"  onkeyup='pubByteCheckTextarea(event,20)' /></td>
										<td><input type="text" id="CompanyCode" title="<spring:eval expression="@${lang}['CODE']"/>"  onkeyup='pubByteCheckTextarea(event,30)' /></td>
										<td><input type="text" id="CompanyAddr" title="<spring:eval expression="@${lang}['ADDRESS']"/>"  maxlength="100" onkeyup='pubByteCheckTextarea(event,500)' /></td>
										<td><input type="text" id="CompanyTel" title="<spring:eval expression="@${lang}['TELEPHONE_NUMBER']"/>"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' placeholder="<spring:eval expression="@${lang}['ENTER_ONLY_NUMBERS']"/>" /></td>
									</tr>
								</tbody>
							</table>
							<div class="inner_uiGroup pc_none">
								<p><spring:eval expression="@${lang}['COMPANY_NAME']" /></p>
								<input type="text" id="MobCompanyName" title="<spring:eval expression="@${lang}['COMPANY_NAME']"/>"  onkeyup='pubByteCheckTextarea(event,20)' />
								<p><spring:eval expression="@${lang}['CODE']" /></p>
								<input type="text" id="MobCompanyCode" title="<spring:eval expression="@${lang}['CODE']"/>"  onkeyup='pubByteCheckTextarea(event,30)' />
								<p><spring:eval expression="@${lang}['ADDRESS']" /></p>
								<input type="text" id="MobCompanyAddr" title="<spring:eval expression="@${lang}['ADDRESS']"/>"  maxlength="100" onkeyup='pubByteCheckTextarea(event,500)' />
								<p><spring:eval expression="@${lang}['TELEPHONE_NUMBER']" /></p>
								<input type="text" id="MobCompanyTel" title="<spring:eval expression="@${lang}['TELEPHONE_NUMBER']"/>"  onkeydown='return onlyNumber(event)' onkeyup='removeChar(event)' placeholder="<spring:eval expression="@${lang}['ENTER_ONLY_NUMBERS']"/>" />
							</div>                                
						</div> 
						<div class="tbl_wrap_admin">
							<table class="inner_tbl mob_none" id="pc_none_tbl_line" style="margin-top:0px;">
								<colgroup>
									<col width="5%">
									<col width="20%">
									<col width="15%">
									<col width="35%">
									<col width="15%">
									<col width="10%">
								</colgroup>
								<thead>
									<th><spring:eval expression="@${lang}['ORDER']" /></th>
									<th><spring:eval expression="@${lang}['COMPANY_NAME']" /></th>
									<th><spring:eval expression="@${lang}['CODE']" /></th>
									<th><spring:eval expression="@${lang}['ADDRESS']" /></th>
									<th><spring:eval expression="@${lang}['TELEPHONE_NUMBER']" /></th>
									<th><spring:eval expression="@${lang}['USE_OR_NOT']" /></th>
								</thead>
								<tbody id="CompanyList" style="overflow: auto;">
                               
								</tbody>
							</table>
							

						</div>
					</div>
				</div><!--innerWrap//-->
			</section>
		</div>
	</main>
</body>
</html>