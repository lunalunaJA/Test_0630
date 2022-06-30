<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*"%>
<%@include file="../common/CommonInclude.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />    
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0, minimum-scale=1.0, maximum-scale=2.0, user-scalable=no">
<title>NADi4.0 :: <spring:eval expression="@${lang}['TASK_MANAGEMENT']" /></title>
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
var sTaskID = "";		//리스트 선택 업무아이디
var sTaskName = "";		//리스트 선택 업무명
var sTaskDesc = "";		//리스트 선택 업무설명
var sTaskCode = "";		//리스트 선택 업무코드

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
		fn_Common.discard();
	});
	
	//업무 리스트 선택
	$(document.body).delegate('#TaskList tr', 'click',	function() {
		$("input[id^='Tas']").val('');
		$("input[id^='Mob']").val('');		
		var meta = $(this).data("meta");
		fn_Common.select = meta;
		$("#TaskName").val(meta.zappTask.name);
		$("#TaskDesc").val(meta.zappTask.descpt);
		$("#TaskCode").val(meta.zappTask.code);
		$("#btnDel").show();
	});
	
	//모바일 업무 리스트 클릭
	$(document.body).delegate('#MobileTaskList p', 'click', function() {
		$("input[id^='Task']").val('');
		$("input[id^='Mob']").val('');
		var meta = $(this).parent().data("meta");
		fn_Common.select = meta;
		$("#MobTaskName").val(meta.zappTask.name);
		$("#MobTaskDesc").val(meta.zappTask.descpt);
		$("#MobTaskCode").val(meta.zappTask.code);
		$("#btnDel").show();
	});
	    
	fn_Common.getTaskList();	// 업무조회
});

/*********************************
Name   : resetInput
Desc   : 초기화
Param  : 없음
**********************************/
var resetInput = function() {
	$("input[id^=Task]").val("");			//PC Mode 입력값 초기화
	$("input[id^=Mob]").val("");			//Mobile Mode 입력값 초기화
	$("#btnDel").hide();
	
	fn_Common.select = {};
}

/*********************************
Name   : fn_Common
Desc   : 공통 처리 함수
Param  : 없음
**********************************/
var fn_Common = {
	select : {},			//선택한 리스트 전체 값
	save : function(type) {	//업무 정보 저장
		sTaskID = fn_Common.select.taskid;
		if(window.innerWidth < 1101){	//모바일 화면 처리
			if (!isEmptyInput($("input[id^=MobTask]"))) {
				return;
			}

			sTaskName = $.trim($("#MobTaskName").val());
			sTaskDesc = $.trim($("#MobTaskDesc").val());
			sTaskCode = $.trim($("#MobTaskCode").val());
		}else{		//PC 화면 처리
			if (!isEmptyInput($("input[id^=Task]"))) {
				return;
			}

			sTaskName = $.trim($("#TaskName").val());
			sTaskDesc = $.trim($("#TaskDesc").val());
			sTaskCode = $.trim($("#TaskCode").val());
		}
				
		if (type == "change") {
			if (sTaskName == fn_Common.select.name
					&& sTaskDesc == fn_Common.select.address
					&& sTaskCode == fn_Common.select.tel) {
				alert("<spring:eval expression="@${msgLang}['NO_ALERTED_CONTENT']"/>");
				return;
			}
		}

		var sendData = {};
		sendData.type = type;	//처리방식 (add:등록, change:수정)
		sendData.url = "${ctxRoot}/api/organ/organtask/" + type;
		sendData.data = {objIsTest : "N"}
		sendData.data.zappTask = {
			taskid : sTaskID
			, name : sTaskName
			, descpt : sTaskDesc
			, code : sTaskCode
		};
			
		fn_Common.publicCommon(sendData);
	},
	discard : function() {	//기업 정보 삭제 플레그 처리
		sTaskID = fn_Common.select.taskid;
		if (!sTaskID) {
			alert("<spring:eval expression="@${msgLang}['DELETE_SELECT_TASK']"/>");
			return;
		}
				
		if(confirm("<spring:eval expression="@${msgLang}['ARE_YOU_DELETE_TASK']"/>") == false){
			return;
		}
		
		var sendData = {};
		sendData.type = "discard";
		sendData.url = "${ctxRoot}/api/organ/organtask/discard";
		sendData.data = {objIsTest : "N"}
		sendData.data.zappTask = {
			taskid : sTaskID
		};
		
		fn_Common.publicCommon(sendData);
	},
	getTaskList : function() {	//업무정보 조회하기
		var sendData = {
				"objIsTest" : "N",
				"companyid" : companyid,
				"objmaporder" : {"name":"asc"}
		};
		
		$.ajax({
			type : 'POST',
			url : '${ctxRoot}/api/organ/organtask/get',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
				console.log("getTaskList data:", JSON.stringify(data));
				if (data.status == "0000") {
					$("#TaskList").empty();
					$(".inner_tbl_line").remove(".pc_none");
					
					if (objectIsEmpty(data.result)) {
						return;
					}

					$.each(data.result, function(index, result) {
						//PC 모드
						var $tr = $("<tr></tr>");
						var innerHtml = "";
						innerHtml += "<td id='name'>" + result.zappTask.name + "</td>";
						innerHtml += "<td id='descpt'>" + result.zappTask.descpt + "</td>";
						innerHtml += "<td id='code'>" + result.zappTask.code + "</td>";
						$tr.append(innerHtml);
						$tr.data('meta', result);
						$("#TaskList").append($tr);
						
						//모바일 모드
						innerHtml = "";
						var $div = $("<div class='inner_tbl_line pc_none' id='MobileTaskList'></div>");
						innerHtml += "<p class='sub'><spring:eval expression="@${lang}['TASK_NAME']"/></p>";
						innerHtml += "<p class='text'>" + (result.zappTask.name) + "</p>";
						innerHtml += "<p class='sub'><spring:eval expression="@${lang}['TASK_DESCRIPTION']"/></p>";
						innerHtml += "<p class='text'>" + (result.zappTask.descpt) + "</p>";
						innerHtml += "<p class='sub'><spring:eval expression="@${lang}['TASK_CODE']"/></p>";
						innerHtml += "<p class='text'>" + (result.zappTask.code) + "</p>";
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
					fn_Common.getTaskList();
				}else if (data.status == "0607") {
					alert("<spring:eval expression="@${msgLang}['ERR_DUP_TASK']"/>");
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
Name   : messageNotice
Desc   : 등록 수정 삭제 폐기 시 해당 정보를 알럿창으로 보여줌
Param  : type (add change discard disable )
**********************************/
function messageNotice(type) {

	var message = '';
	if (type == 'add') {
		message = "<spring:eval expression="@${msgLang}['REGISTERED_TASK']"/>";
	} else if (type == 'change') {
		message = "<spring:eval expression="@${msgLang}['EDITED_TASK']"/>";
	} else if (type == 'discard') {
		message = "<spring:eval expression="@${msgLang}['DELETED_TASK']"/>";
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
						<h2 class="pageTit"><img src="${image}/icon/Group 159.png" alt=""><spring:eval expression="@${lang}['TASK_MANAGEMENT']" /></h2>			
						<div class="wdt100">
							<h3 class="innerTit"><spring:eval expression="@${lang}['TASK_MANAGEMENT']" /></h3>
							<div class="btn_wrap">
								<button type="button" class="btbase" id="btnInit" name="new"><spring:eval expression="@${lang}['INITIALIZATION']" /></button>
								<button type="button" class="btbase" id="btnNew" name="add"><spring:eval expression="@${lang}['SAVE']" /></button>
								<button type="button" class="btbase" id="btnDel" name="del"><spring:eval expression="@${lang}['DELETE']" /></button>
							</div>
							<table class="inner_tbl mob_none">
								<colgroup>
									<col width="40%">
									<col width="40%">
									<col width="20%">
								</colgroup>
								<thead>
									<th><spring:eval expression="@${lang}['TASK_NAME']" /></th>
									<th><spring:eval expression="@${lang}['TASK_DESCRIPTION']" /></th>
									<th><spring:eval expression="@${lang}['TASK_CODE']" /></th>
								</thead>
								<tbody>
									<tr>
										<td><input type="text" id="TaskName" title="<spring:eval expression="@${lang}['TASK_NAME']"/>" onkeyup='pubByteCheckTextarea(event,100)' /></td>
										<td><input type="text" id="TaskDesc" title="<spring:eval expression="@${lang}['TASK_DESCRIPTION']"/>" onkeyup='pubByteCheckTextarea(event,100)' /></td>
										<td><input type="text" id="TaskCode" title="<spring:eval expression="@${lang}['TASK_CODE']"/>" onkeyup='pubByteCheckTextarea(event,30)' /></td>
									</tr>
								</tbody>
							</table>
							<div class="inner_uiGroup pc_none">
								<p><spring:eval expression="@${lang}['TASK_NAME']" /></p>
								<input type="text" id="MobTaskName" title="<spring:eval expression="@${lang}['TASK_NAME']"/>" onkeyup='pubByteCheckTextarea(event,100)' />
								<p><spring:eval expression="@${lang}['TASK_DESCRIPTION']" /></p>
								<input type="text" id="MobTaskDesc" title="<spring:eval expression="@${lang}['TASK_DESCRIPTION']"/>" onkeyup='pubByteCheckTextarea(event,100)' />
								<p><spring:eval expression="@${lang}['TASK_CODE']" /></p>
								<input type="text" id="MobTaskCode" title="<spring:eval expression="@${lang}['TASK_CODE']"/>" onkeyup='pubByteCheckTextarea(event,30)' />
							</div>                                
						</div> 
						<div class="tbl_wrap_admin">
							<table class="inner_tbl mob_none" id="pc_none_tbl_line">
								<colgroup>
									<col width="40%">
									<col width="40%">
									<col width="20%">
								</colgroup>
								<thead>
									<th><spring:eval expression="@${lang}['TASK_NAME']" /></th>
									<th><spring:eval expression="@${lang}['TASK_DESCRIPTION']" /></th>
									<th><spring:eval expression="@${lang}['TASK_CODE']" /></th>
								</thead>
							</table>
							<div class="tbody_wrap mob_none">
								<table>
									<colgroup>
										<col width="40%">
										<col width="40%">
										<col width="20%">
									</colgroup>
									<tbody id="TaskList">

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