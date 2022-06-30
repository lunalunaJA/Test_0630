<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*"%>
<%@include file="../common/CommonInclude.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />    
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0, minimum-scale=1.0, maximum-scale=2.0, user-scalable=no">
<title>NADi4.0 :: <spring:eval expression="@${lang}['CABINET_MANAGEMENT']" /></title>
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
var sCabinetID = "";			//리스트 선택 캐비닛 아이디
var sCabinetName = "";			//리스트 선택 캐비닛명
var sCabinetDesc = "";			//리스트 선택 캐비닛 설명
var sCabinetMaxCapacity = "";	//리스트 선택 캐비닛 저장량
var sCabinetState = "";			//리스트 선택 캐비닛 상태
var sCabinetPath = "";			//리스트 선택 캐비닛 경로

$(document).ready(function() {
	//입력값 초기화
	resetInput();
	
	//초기화 버튼
	$("#btnInit").click(function() {
		resetInput();
	});

	//저장 버튼
	$("#btnNew").click(function() {
		console.log("btnNew fn_Common.select : "+JSON.stringify(fn_Common.select));
		if (objectIsEmpty(fn_Common.select)) {
			fn_Common.save("add");
		} else {
			fn_Common.save("change");
		}
	});
	
	//업무 select option 변경시 호출
	$("#taskList").change(function() {
		resetInput();
		fn_Common.getCabinetList();
	});

	//삭제
	$("#btnDel").click(function() {
		fn_Common.discard();
	})
	
	//캐비닛 용량 입력 값 체크
	$("#CabinetMaxCapacity").focusout(function() {
		var currentVal = $(this).val();
		if (10 > currentVal) {
			alert("<spring:eval expression="@${msgLang}['MINUMUN_CAPACITY']"/>");
			$("#CabinetMaxCapacity").val(10);
		}
	});

	//캐비닛 리스트 선택
	$(document.body).delegate('#CabinetList tr', 'click', function() {
		$("input[id^='Cabinet']").val('');
		$("input[id^='Mob']").val('');		
		var meta = $(this).data("meta");
		fn_Common.select = meta;
		$("#CabinetName").val(meta.name);
		$("#CabinetDesc").val(meta.descpt);
		var maxMB = parseInt(meta.maxcapacity / 1024 / 1024);
		$("#CabinetMaxCapacity").val(maxMB);
		$("#CabinetState").val(meta.state).attr("disabled", true);
		if(meta.state == 1){
			$("#CabinetPath").val(meta.mountpath).attr("disabled", false);  
		}else{
			$("#CabinetPath").val(meta.mountpath).attr("disabled", true);
		}
		$("#btnDel").show();
	});
	
	//모바일 캐비닛 리스트 클릭
	$(document.body).delegate('#MobileCabinetList p', 'click', function() {
		$("input[id^='Cabinet']").val('');
		$("input[id^='Mob']").val('');
		var meta = $(this).parent().data("meta");
		fn_Common.select = meta;
		$("#MobCabinetName").val(meta.name);
		$("#MobCabinetDesc").val(meta.descpt);
		var maxMB = parseInt(meta.maxcapacity / 1024 / 1024);
		$("#MobCabinetMaxCapacity").val(maxMB);
		$("#MobCabinetState").val(meta.state).attr("disabled", true);
		if(meta.state == 1){
			$("#MobCabinetPath").val(meta.mountpath).attr("disabled", false);  
		}else{
			$("#MobCabinetPath").val(meta.mountpath).attr("disabled", true);
		}
		$("#btnDel").show();
	});
		
	fn_Common.getTaskList();	//업무 조회
});

/*********************************
 Name   : resetInput
 Desc   : 초기화 
 Param  : 없음
 **********************************/
var resetInput = function() {
	typeMode = "ADD";
	$("input[id^=Cabinet]").val("");
	$("input[id^=Mob]").val("");
	$("#CabinetPath").attr("disabled", false);
	$("#CabinetState").attr("disabled", true).val("1");
	$("#MobCabinetPath").attr("disabled", false);
	$("#MobCabinetState").attr("disabled", true).val("1");
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
	save : function(type) {	//케비닛 정보 저장
		sCabinetID = fn_Common.select.cabinetid;
		if(window.innerWidth < 1101){	//모바일 화면 처리
			if (!isEmptyInput($("input[id^=MobCabinet]"))) {
				return;
			}

			sCabinetName = $.trim($("#MobCabinetName").val());
			sCabinetDesc = $.trim($("#MobCabinetDesc").val());
			sCabinetMaxCapacity = $.trim($("#MobCabinetMaxCapacity").val()) * 1024 * 1024;
			sCabinetPath = $.trim($("#MobCabinetPath").val());
		}else{		//PC 화면 처리
			if (!isEmptyInput($("input[id^=Cabinet]"))) {
				return;
			}
			sCabinetName = $.trim($("#CabinetName").val());
			sCabinetDesc = $.trim($("#CabinetDesc").val());
			sCabinetMaxCapacity = $.trim($("#CabinetMaxCapacity").val()) * 1024 * 1024;
			sCabinetPath = $.trim($("#CabinetPath").val());
		}

		if (type == "change") {
			if (sCabinetName == fn_Common.select.name
					&& sCabinetDesc == fn_Common.select.descpt
					&& sCabinetMaxCapacity == fn_Common.select.maxcapacity
					&& sCabinetPath == fn_Common.select.mountpath) {
				alert("<spring:eval expression="@${msgLang}['NO_ALERTED_CONTENT']"/>");
				return;
			}
		}
		
		var sendData = {};
		sendData.type = type;	//처리방식 (add:등록, change:수정)
		sendData.url = "${ctxRoot}/api/system/cabinet/" + type;
		sendData.data = {objIsTest : "N"}
		sendData.data.name = sCabinetName;
		sendData.data.descpt = sCabinetDesc;
		sendData.data.maxcapacity = sCabinetMaxCapacity;
		sendData.data.mountpath = sCabinetPath;
		sendData.data.cabinetid = sCabinetID;
		sendData.data.objTaskid = $.trim($("#taskList option:selected").val());
			
		fn_Common.publicCommon(sendData);
	},
	discard : function() {	//케비닛 정보 삭제
		sCabinetID = fn_Common.select.cabinetid;
		if (!sCabinetID) {
			alert("<spring:eval expression="@${msgLang}['PLEASE_SELECT_CABINET_DELETE']"/>");
			return;
		}
				
		if(confirm("<spring:eval expression="@${msgLang}['ARE_YOU_DEL_CABINET']"/>") == false){
			return;
		}
		
		var sendData = {};
		sendData.type = "discard";
		sendData.url = "${ctxRoot}/api/system/cabinet/discard";
		sendData.data = {objIsTest : "N"}
		sendData.data = {
			cabinetid : sCabinetID
		};
		
		fn_Common.publicCommon(sendData);
	},
	getCabinetList : function() {	//케비닛 정보 조회하기
		var sendData = {
			"objIsTest" : "N",
			"objTaskid" : $.trim($("#taskList option:selected").val()),
			"objmaporder" : {"name":"asc"}
		};
		
		$.ajax({
			type : 'POST',
			url : '${ctxRoot}/api/system/cabinet/list',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
				console.log("getCabinetList data:", JSON.stringify(data));
				if (data.message == "Success") {
					$("#CabinetList").empty();
					$(".inner_tbl_line").remove(".pc_none");

					if (objectIsEmpty(data.result)) {
						return;
					}
					
					$.each(data.result, function(index, result) {
						//PC 모드
						var $tr = $("<tr></tr>");
						var innerHtml = "";
						var stateText = [ "<spring:eval expression="@${lang}['WAITING_FOR_USE']"/>", "<spring:eval expression="@${lang}['BE_IN_USE']"/>",
								"<spring:eval expression="@${lang}['COMPLETE_USE']"/>" ]; //1 : 사용대기, 2 : 사용중, 3 : 사용완료
						innerHtml += "<td id='name'>" + result.name + "</td>";
						innerHtml += "<td id='descpt'>" + ((result.descpt == null) ? '' : result.descpt) + "</td>";
						var maxMB = parseInt(result.maxcapacity / 1024 / 1024);
						innerHtml += "<td id='maxcapacity'>" + maxMB.toLocaleString('ko-KR') + " MB</td>";
						innerHtml += "<td id='state' >" + stateText[result.state - 1] + "</td>";
						innerHtml += "<td id='mountpath'>" + result.mountpath + "</td></tr>";
						$tr.append(innerHtml);
						$tr.data('meta', result);
						$("#CabinetList").append($tr);

						//모바일 모드
						innerHtml = "";
						var $div = $("<div class='inner_tbl_line pc_none' id='MobileCabinetList'></div>");
						innerHtml += "<p class='sub'><spring:eval expression="@${lang}['CABINET_NAME']"/></p>";
						innerHtml += "<p class='text'>" + (result.name) + "</p>";
						innerHtml += "<p class='sub'><spring:eval expression="@${lang}['CABINET_DESCRIPTION']"/></p>";
						innerHtml += "<p class='text'>" + ((result.descpt == null) ? '' : result.descpt) + "</p>";
						innerHtml += "<p class='sub'><spring:eval expression="@${lang}['CABINET_MAXIMUM_CAPACITY']"/>(MB)</p>";
						var maxMB = parseInt(result.maxcapacity / 1024 / 1024);
						innerHtml += "<p class='text'>" + maxMB.toLocaleString('ko-KR') + "</p>";
						innerHtml += "<p class='sub'><spring:eval expression="@${lang}['CABINET_STATE']"/></p>";
						innerHtml += "<p class='text'>" + stateText[result.state - 1] + "</p>";
						innerHtml += "<p class='sub'><spring:eval expression="@${lang}['CABINET_PATH']"/></p>";
						innerHtml += "<p class='text'>" + result.mountpath + "</p>";
						$div.append(innerHtml);
						$div.data('meta', result);
						$("#pc_none_tbl_line").after($div);
					});
				} else {
					alertErr(data.message);
				}
			},error : function(request, status, error) {
				alertNoty(request, status, error);
			}
		});		
	},
	getTaskList : function() {	//업무 정보 조회하기
		$("#taskList").empty();
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
					var option = "";
					//option += "<option id='taskid' code='' value=''>-- 선택 --</option>";
					$.each(data.result, function(idx, result) {
						option += "<option id='taskid' code='"+data.result[idx].zappTask.code+"' value='"+data.result[idx].zappTask.taskid+"'>" + data.result[idx].zappTask.name + "</option>";
					});
				
					$("#taskList").append(option);
					//var userPosition = $("#taskList option:selected").text();
					//var taskId = $("#taskList option:selected").val();
					fn_Common.getCabinetList();
				
				} else {
					alertErr(data.message);
				}
			},error : function(request, status, error) {
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
				if (data.message == "Success") {
					messageNotice(sendData.type);
					fn_Common.getCabinetList();
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
		message = "<spring:eval expression="@${msgLang}['REGISTERED_CABINET']"/>";
	} else if (type == 'change') {
		message = "<spring:eval expression="@${msgLang}['CABINET_MODIFIED']"/>";
	} else if (type == 'discard') {
		message = "<spring:eval expression="@${msgLang}['CABINET_DELETED']"/>";
	}
	if (message) {
		alert(message);
	}
}

/*********************************
Name   : isEmptyInput
Desc   : input val check
Param  : obj
 **********************************/
var isEmptyInput = function(obj) {
	console.log("===isEmptyInput : ", obj);
	var isEmpty = true;
	var inputObjs = obj;
	inputObjs.each(function() {
		if (($(this).val() == '')) {
			isEmpty = false;
			var message = "";
			if ($(this).attr('id') == "CabinetMaxCapacity") {
				message += "<spring:eval expression="@${msgLang}['INVALID_INFO']"/>";
				message += "\n";
				message += "<spring:eval expression="@${msgLang}['MINUMUN_CAPACITY']"/>";
				alert(message);
			} else {
				message = "<spring:eval expression="@${msgLang}['INVALID_INFO']"/>";
				alert(message);
			}
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
					<h2 class="pageTit"><img src="${image}/icon/icon_b06.png" alt=""><spring:eval expression="@${lang}['CABINET_MANAGEMENT']" /></h2>
					<div class=""></div>
					<div class="wdt100">
						<div class="inner_uiGroup">
							<p><span style="font-weight: bold;"><spring:eval expression="@${lang}['TASK_MANAGEMENT_LIST']" /> </span></p>
							<select id="taskList" title="<spring:eval expression="@${lang}['TASK_MANAGEMENT_LIST']"/>">
							</select>
						</div>
					</div>
					<div class="wdt100">
						<h3 class="innerTit"><spring:eval expression="@${lang}['CABINET_MANAGEMENT']" /></h3>
							<div class="btn_wrap">
								<button type="button" class="btbase" id="btnInit" name="new"><spring:eval expression="@${lang}['INITIALIZATION']" /></button>
								<button type="button" class="btbase" id="btnNew" name="add"><spring:eval expression="@${lang}['SAVE']" /></button>
								<button type="button" class="btbase" id="btnDel" name="del"><spring:eval expression="@${lang}['DELETE']" /></button>
							</div>
							<table class="inner_tbl mob_none">
								<colgroup>
									<col width="20%">
									<col width="20%">
									<col width="20%">
									<col width="10%">
									<col width="30%">
								</colgroup>
								<thead>
									<th><spring:eval expression="@${lang}['CABINET_NAME']" /></th>
									<th><spring:eval expression="@${lang}['CABINET_DESCRIPTION']" /></th>
									<th><spring:eval expression="@${lang}['CABINET_MAXIMUM_CAPACITY']" />(MB)</th>
									<th><spring:eval expression="@${lang}['CABINET_STATE']" /></th>
									<th><spring:eval expression="@${lang}['CABINET_PATH']" /></th>
								</thead>
								<tbody>
									<tr>
										<td><input type="text" id="CabinetName" title="<spring:eval expression="@${lang}['CABINET_NAME']"/>" onkeyup='pubByteCheckTextarea(event,100)' /></td>
										<td><input type="text" id="CabinetDesc" title="<spring:eval expression="@${lang}['CABINET_DESCRIPTION']"/>" onkeyup='pubByteCheckTextarea(event,100)' /></td>
										<td><input type="text" id="CabinetMaxCapacity" title="<spring:eval expression="@${lang}['CABINET_MAXIMUM_CAPACITY']"/>" maxlength="13" onkeydown='return onlyNumber(event)' placeholder="Mbyte"/></td>
										<td>
											<select id="CabinetState" title="<spring:eval expression="@${lang}['CABINET_STATE']"/>" >
												<option value="1" selected><spring:eval expression="@${lang}['WAITING_FOR_USE']" /></option>
												<option value="2"><spring:eval expression="@${lang}['BE_IN_USE']" /></option>
												<option value="3"><spring:eval expression="@${lang}['COMPLETE_USE']" /></option>
											</select>
										</td>										
										<td><input type="text" id="CabinetPath" title="<spring:eval expression="@${lang}['CABINET_PATH']"/>" onkeyup='pubByteCheckTextarea(event,10000)' /></td>
									</tr>
								</tbody>
							</table>
							<div class="inner_uiGroup pc_none">
								<p><spring:eval expression="@${lang}['CABINET_NAME']" /></p>
								<input type="text" id="MobCabinetName" title="<spring:eval expression="@${lang}['CABINET_NAME']"/>" onkeyup='pubByteCheckTextarea(event,100)' />
								<p><spring:eval expression="@${lang}['CABINET_DESCRIPTION']" /></p>
								<input type="text" id="MobCabinetDesc" title="<spring:eval expression="@${lang}['CABINET_DESCRIPTION']"/>" onkeyup='pubByteCheckTextarea(event,100)' />
								<p><spring:eval expression="@${lang}['CABINET_MAXIMUM_CAPACITY']" />(MB)</p>
								<input type="text" id="MobCabinetMaxCapacity" title="<spring:eval expression="@${lang}['CABINET_MAXIMUM_CAPACITY']"/>" maxlength="13" onkeydown='return onlyNumber(event)' placeholder="Mbyte"/>
								<p><spring:eval expression="@${lang}['CABINET_STATE']" /></p>
									<select id="MobCabinetState">
										<option value="1" selected><spring:eval expression="@${lang}['WAITING_FOR_USE']" /></option>
										<option value="2"><spring:eval expression="@${lang}['BE_IN_USE']" /></option>
										<option value="3"><spring:eval expression="@${lang}['COMPLETE_USE']" /></option>
									</select>
								<p><spring:eval expression="@${lang}['CABINET_PATH']" /></p>
								<input type="text" id="MobCabinetPath" title="<spring:eval expression="@${lang}['CABINET_PATH']"/>" onkeyup='pubByteCheckTextarea(event,10000)' />
							</div>                                
						</div> 
						<div class="tbl_wrap_admin_cabinet">
						<table class="inner_tbl mob_none" id="pc_none_tbl_line" style="margin-top:0px;">
							<colgroup>
								<col width="20%">
								<col width="20%">
								<col width="20%">
								<col width="10%">
								<col width="30%">
							</colgroup>
							<thead>
								<th><spring:eval expression="@${lang}['CABINET_NAME']" /></th>
								<th><spring:eval expression="@${lang}['CABINET_DESCRIPTION']" /></th>
								<th><spring:eval expression="@${lang}['CABINET_MAXIMUM_CAPACITY']" /></th>
								<th><spring:eval expression="@${lang}['CABINET_STATE']" /></th>
								<th><spring:eval expression="@${lang}['CABINET_PATH']" /></th>
							</thead>
						</table>
						<div class="tbody_wrap_cabinet mob_none">
							<table>
								<colgroup>
									<col width="20%">
									<col width="20%">
									<col width="20%">
									<col width="10%">
									<col width="30%">
								</colgroup>
								<tbody id="CabinetList">

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