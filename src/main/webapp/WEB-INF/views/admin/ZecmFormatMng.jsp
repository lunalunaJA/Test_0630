<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*"%>
<%@include file="../common/CommonInclude.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />    
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0, minimum-scale=1.0, maximum-scale=2.0, user-scalable=no">
<title>NADi4.0 :: <spring:eval expression="@${lang}['FORMAT_MANAGE']" /></title>
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
var sFormatID = "";				//리스트 선택 파일유형 아이디
var sFormatExtension = "";		//리스트 선택 파일유형 확장자
var sFormatName = "";			//리스트 선택 파일유형 이름
var sFormatRegisteredSize = "";	//리스트 선택 파일유형 등록 사이즈

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
	
	//파일 유형 리스트 선택
	$(document.body).delegate('#FormatList tr', 'click',	function() {
		$("input[id^='Format']").val('');
		$("input[id^='Mob']").val('');		
		var meta = $(this).data("meta");
		fn_Common.select = meta;
		$("#FormatExtension").val(meta.ext);
		$("#FormatName").val(meta.name);
		$("#FormatRegisteredSize").val(uncomma(meta.mxsize/1000));
		$("#btnDel").show();
	});

	//모바일 파일 유형 리스트 클릭
	$(document.body).delegate('#MobileFormatList p', 'click', function() {
		$("input[id^='Format']").val('');
		$("input[id^='Mob']").val('');
		var meta = $(this).parent().data("meta");
		fn_Common.select = meta;
		$("#MobFormatExtension").val(meta.ext);
		$("#MobFormatName").val(meta.name);
		$("#MobFormatRegisteredSize").val(uncomma(meta.mxsize/1000));
		$("#btnDel").show();
	});   
	
	fn_Common.getFormatList();	// 파일유형 조회
});

/*********************************
Name   : resetInput
Desc   : 초기화
Param  : 없음
**********************************/
var resetInput = function() {
	$("input[id^=Format]").val("");			//PC Mode 입력값 초기화
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
	save : function(type) {	//파일유형 정보 저장
		sFormatID = fn_Common.select.formatid;
		if(window.innerWidth < 1101){	//모바일 화면 처리
			if (!isEmptyInput($("input[id^=MobFormat]"))) {
				return;
			}

			sFormatExtension = $.trim($("#MobFormatExtension").val());
			sFormatName = $.trim($("#MobFormatName").val());
			sFormatRegisteredSize = uncomma($.trim($("#MobFormatRegisteredSize").val()));
		}else{		//PC 화면 처리
			if (!isEmptyInput($("input[id^=Format]"))) {
				return;
			}

			sFormatExtension = $.trim($("#FormatExtension").val());
			sFormatName = $.trim($("#FormatName").val());
			sFormatRegisteredSize = uncomma($.trim($("#FormatRegisteredSize").val()));
		}

		if (1000 > sFormatRegisteredSize) {
			alert("<spring:eval expression="@${msgLang}['MINUMUN_CAPACITY']"/>");
			$("#FormatRegisteredSize").focus();
			return;
		}
		
		if (type == "change") {
			if (sFormatExtension == fn_Common.select.ext
					&& sFormatName == fn_Common.select.name
					&& sFormatRegisteredSize == fn_Common.select.mxsize) {
				alert("<spring:eval expression="@${msgLang}['NO_ALERTED_CONTENT']"/>");
				return;
			}
		}

		var sendData = {};
		sendData.type = type;	//처리방식 (add:등록, change:수정)
		sendData.url = "${ctxRoot}/api/system/format/" + type;
		sendData.data = {objIsTest : "N"}
		sendData.data.formatid = sFormatID;
		sendData.data.ext = sFormatExtension;
		sendData.data.name = sFormatName;
		sendData.data.mxsize = sFormatRegisteredSize*1000;

		fn_Common.publicCommon(sendData);
	},
	discard : function() {	//파일유형 정보 삭제 플레그 처리
		sFormatID = fn_Common.select.formatid;
		if (!sFormatID) {
			alert("<spring:eval expression="@${msgLang}['SELECT_FORMAT_DELETE']"/>");
			return;
		}
				
		if(confirm("<spring:eval expression="@${msgLang}['ARE_YOU_FORMAT_DELETE']"/>") == false){
			return;
		}
		
		var sendData = {};
		sendData.type = "discard";
		sendData.url = "${ctxRoot}/api/system/format/discard";
		sendData.data = {objIsTest : "N"}
		sendData.data.formatid = sFormatID;
		
		fn_Common.publicCommon(sendData);
	},
	getFormatList : function() {	//파일유형 정보 조회하기
		var sendData = {
			"objIsTest" : "N",
			"objmaporder" : {"name":"asc"}
		};
		
		$.ajax({
			type : 'POST',
			url : '${ctxRoot}/api/system/format/list',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
				console.log("getFormatList data:", JSON.stringify(data));
				if (data.message == "Success") {
					$("#FormatList").empty();
					$(".inner_tbl_line").remove(".pc_none");
					
					if (objectIsEmpty(data.result)) {
						return;
					}

					$.each(data.result, function(index, result) {
						//PC 모드
						var $tr = $("<tr></tr>");
						var innerHtml = "";
						innerHtml += "<td id='name'>" + result.ext + "</td>";
						innerHtml += "<td id='descpt'>" + result.name + "</td>";
						innerHtml += "<td id='code'>" + comma(result.mxsize/1000) + "</td>";
						$tr.append(innerHtml);
						$tr.data('meta', result);
						$("#FormatList").append($tr);
						
						//모바일 모드
						innerHtml = "";
						var $div = $("<div class='inner_tbl_line pc_none' id='MobileFormatList'></div>");
						innerHtml += "<p class='sub'><spring:eval expression="@${lang}['FORMAT_EXTENSION']"/></p>";
						innerHtml += "<p class='text'>" + (result.ext) + "</p>";
						innerHtml += "<p class='sub'><spring:eval expression="@${lang}['FORMAT_NAME']"/></p>";
						innerHtml += "<p class='text'>" + (result.name) + "</p>";
						innerHtml += "<p class='sub'><spring:eval expression="@${lang}['FORMAT_REGISTERED_SIZE']"/>(KB)</p>";
						innerHtml += "<p class='text'>" + (comma(result.mxsize/1000)) + "</p>";
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
				if (data.message == "Success") {
					messageNotice(sendData.type);
					fn_Common.getFormatList();
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
		message = "<spring:eval expression="@${msgLang}['REGISTERED_FORMAT']"/>";
	} else if (type == 'change') {
		message = "<spring:eval expression="@${msgLang}['MODIFIED_FORMAT']"/>";
	} else if (type == 'discard') {
		message = "<spring:eval expression="@${msgLang}['DELETE_FORMAT']"/>";
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
			if ($(this).attr('id') == "FormatRegisteredSize" || $(this).attr('id') == "MobFormatRegisteredSize") {
				var message = "<spring:eval expression="@${msgLang}['INVALID_INFO']"/>";
				message+="\n";
				message+="<spring:eval expression="@${msgLang}['MINUMUN_CAPACITY']"/>";
				alert(message);
			} else {
				var message = $(this).attr('title');
				message += "\n";
				message += "<spring:eval expression="@${msgLang}['INVALID_INFO']"/>";
				alert(message);
			}
			$(this).focus();
			return false;
		}
	});
	return isEmpty;
}

/*********************************
Name   : comma
Desc   : 최대 크기 천단위 콤마 표시
Param  : String
 **********************************/
var comma = function(str) {
	str = String(str);
	return str.replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
}
var uncomma = function(str) {
	str = String(str);
	return str.replace(/[^\d]+/g, '');
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
				<div class="innerWrap innerWrap_scroll">
					<div class="full-content">
						<h2 class="pageTit"><img src="${image}/icon/icon_b09.png" alt=""><spring:eval expression="@${lang}['FORMAT_MANAGE']" /></h2>			
						<div class="wdt100">
							<h3 class="innerTit"><spring:eval expression="@${lang}['FORMAT']" /></h3>
							<div class="btn_wrap">
								<button type="button" class="btbase" id="btnInit" name="new"><spring:eval expression="@${lang}['INITIALIZATION']" /></button>
								<button type="button" class="btbase" id="btnNew" name="add"><spring:eval expression="@${lang}['SAVE']" /></button>
								<button type="button" class="btbase" id="btnDel" name="del"><spring:eval expression="@${lang}['DELETE']" /></button>
							</div>
							<table class="inner_tbl mob_none">
								<colgroup>
									<col width="33%">
									<col width="34%">
									<col width="33%">
								</colgroup>
								<thead>
									<th><spring:eval expression="@${lang}['FORMAT_EXTENSION']" /></th>
									<th><spring:eval expression="@${lang}['FORMAT_NAME']" /></th>
									<th><spring:eval expression="@${lang}['FORMAT_REGISTERED_SIZE']" />(KB)</th>
								</thead>
								<tbody>
									<tr>
										<td><input type="text" id="FormatExtension" title="<spring:eval expression="@${lang}['FORMAT_EXTENSION']"/>" onkeyup='pubByteCheckTextarea(event,100)' /></td>
										<td><input type="text" id="FormatName" title="<spring:eval expression="@${lang}['FORMAT_NAME']"/>" onkeyup='pubByteCheckTextarea(event,100)' /></td>
										<td><input type="text" id="FormatRegisteredSize" min="1000" max="100000" placeholder="kilobyte" onkeydown="return onlyNumber(event)" title="<spring:eval expression="@${lang}['FORMAT_REGISTERED_SIZE']"/>"/></td>
									</tr>
								</tbody>
							</table>
							<div class="inner_uiGroup pc_none">
								<p><spring:eval expression="@${lang}['FORMAT_EXTENSION']" /></p>
								<input type="text" id="MobFormatExtension" title="<spring:eval expression="@${lang}['FORMAT_EXTENSION']"/>" onkeyup='pubByteCheckTextarea(event,100)' />
								<p><spring:eval expression="@${lang}['FORMAT_NAME']" /></p>
								<input type="text" id="MobFormatName" title="<spring:eval expression="@${lang}['FORMAT_NAME']"/>" onkeyup='pubByteCheckTextarea(event,100)' />
								<p><spring:eval expression="@${lang}['FORMAT_REGISTERED_SIZE']" />(KB)</p>
								<input type="text" id="MobFormatRegisteredSize" min="1000" max="100000" placeholder="kilobyte" onkeydown="return onlyNumber(event)" title="<spring:eval expression="@${lang}['FORMAT_REGISTERED_SIZE']"/>" />
							</div>                                
						</div> 
						<div class="tbl_wrap_admin">
							<table class="inner_tbl mob_none" id="pc_none_tbl_line">
								<colgroup>
									<col width="33%">
									<col width="34%">
									<col width="33%">
								</colgroup>
								<thead>
									<th><spring:eval expression="@${lang}['FORMAT_EXTENSION']" /></th>
									<th><spring:eval expression="@${lang}['FORMAT_NAME']" /></th>
									<th><spring:eval expression="@${lang}['FORMAT_REGISTERED_SIZE']" />(KB)</th>
								</thead>
							</table>
							<div class="tbody_wrap mob_none">
								<table>
									<colgroup>
										<col width="33%">
										<col width="34%">
										<col width="33%">
									</colgroup>
									<tbody id="FormatList">
	
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