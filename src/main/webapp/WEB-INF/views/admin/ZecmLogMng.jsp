<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*"%>
<%@include file="../common/CommonInclude.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />    
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0, minimum-scale=1.0, maximum-scale=2.0, user-scalable=no">
<title>NADi4.0 :: <spring:eval expression="@${lang}['LOG']" /></title>
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
$(document).ready(function() {
	$.datepicker.setDefaults($.datepicker.regional['en']);

	//검색일자 기본 날자 셋팅
	$("#RegSDate").datepicker({dateFormat: 'yy-mm-dd'});
	$("#RegEDate").datepicker({dateFormat: 'yy-mm-dd'});			
	$("#RegSDate").datepicker().datepicker("setDate", -7); 			//오늘기준 -7일전 날짜
	$("#RegEDate").datepicker().datepicker("setDate", new Date());	// 검색기간 날짜 받아오기

	//초기 로그 조회
	SearchLog(1);
		
	// 검색버튼 눌렀을때 날짜 체크후 1페이지 출력
	$("#btnSrch").click(function() {
		var startDate = $('#RegSDate').val();
		var endDate = $("#RegEDate").val();
			
		if (startDate == "" && endDate == "") {
			alert("<spring:eval expression="@${msgLang}['ENTER_DATE']"/>");
			return;
		}
		if (startDate > endDate) {
			alert("<spring:eval expression="@${msgLang}['STARTDATE_CANNOT_ENDDATE']"/>");
			return;
		}
		SearchLog(1);
	});

	// 로그타입 선택하면 셀렉트박스 및 헤더 변경
	$('#inlogselectlist').change(function() {
		var selected = this.value;
		$('#SearchByName').val("");
		$("#logList").html("<tr><td colspan=8><spring:eval expression="@${msgLang}['NOTEXIST_LOG']"/></td></tr>");
		$("#totCnt").html("<span id='totCnt' style='margin: 5px 5px 5px 5px; font-size: 14px; font-weight: bold;'><spring:eval expression="@${lang}['TOTAL']"/> : 0</span>");
		$("#paging").html("");
		
		SearchLog(1);
		//로그타입 내용
		var contentType = [ {
			type : '<spring:eval expression="@${lang}['BUNDLE']"/>', 	value : '01'}, {
			type : '<spring:eval expression="@${lang}['FILE']"/>', 		value : '02'}, {
			type : '<spring:eval expression="@${lang}['CLASSIFICATION']"/>', value : '03'}, {
			type : '<spring:eval expression="@${lang}['LINK']"/>', 		value : '04'}, {
			type : '<spring:eval expression="@${lang}['SHARE']"/>', 	value : '05'}, {
			type : '<spring:eval expression="@${lang}['LOCK']"/>', 		value : '06'} ];
		var contentAction = [ {
			type : "<spring:eval expression="@${lang}['THE_ENTIRE']"/>", value : "none"}, {
			type : "<spring:eval expression="@${lang}['NEW']"/>", 		value : "A1"}, {
			type : "<spring:eval expression="@${lang}['CHANGE']"/>", 	value : "B1"}, {
			type : "<spring:eval expression="@${lang}['MOVE']"/>", 		value : "C1"}, {
			type : "<spring:eval expression="@${lang}['COPY']"/>", 		value : "D1"}, {
			type : "<spring:eval expression="@${lang}['DELETE']"/>", 	value : "E1"}, {
			type : "<spring:eval expression="@${lang}['DISCARD']"/>", 	value : "F1"}, {
			type : "<spring:eval expression="@${lang}['SEARCH']"/>", 	value : "G1"}, {
			type : "<spring:eval expression="@${lang}['SHARE']"/>", 	value : "Z1"} ];
		
		//로그타입 접근
		var accessType = [ {
			type : "<spring:eval expression="@${lang}['CERIFIED']"/>",	value : "01"} ];
		var accessAction = [ {
			type : "<spring:eval expression="@${lang}['THE_ENTIRE']"/>", 	value : "none"}, {
			type : "<spring:eval expression="@${lang}['LOGIN']"/>", 		value : "01"}, {
			type : "<spring:eval expression="@${lang}['LOGOUT']"/>",		value : "02"}, {
			type : "<spring:eval expression="@${msgLang}['LOGIN_FAILED']"/>", value : "03"} ];
			
		//로그타입 시스템
		var systemType = [ {
			type : "<spring:eval expression="@${lang}['COMPANY']"/>", 		value : "01"}, {
			type : "<spring:eval expression="@${lang}['DEPARTMENT']"/>", 	value : "02"}, {
			type : "<spring:eval expression="@${lang}['USER']"/>", 			value : "03"}, {
			type : "<spring:eval expression="@${lang}['GROUP']"/>", 		value : "04"}, {
			type : "<spring:eval expression="@${lang}['PREFERENCE']"/>", 	value : "11"}, {
			type : "<spring:eval expression="@${lang}['CODE']"/>",			value : "12"} ];
		var systemAction = [ {
			type : "<spring:eval expression="@${lang}['THE_ENTIRE']"/>", 	value : "none"}, {
			type : "<spring:eval expression="@${lang}['NEW']"/>", 			value : "A1"}, {
			type : "<spring:eval expression="@${lang}['CHANGE']"/>", 		value : "B1"}, {
			type : "<spring:eval expression="@${lang}['MOVE']"/>", 			value : "C1"}, {
			type : "<spring:eval expression="@${lang}['COPY']"/>", 			value : "D1"}, {
			type : "<spring:eval expression="@${lang}['DELETE']"/>", 		value : "E1"}, {
			type : "<spring:eval expression="@${lang}['DISCARD']"/>", 		value : "F1"}, {
			type : "<spring:eval expression="@${lang}['INQUIRY']"/>", 		value : "G1"}, {
			type : "<spring:eval expression="@${lang}['SHARE']"/>", 		value : "Z1"} ];
		
		var targetType = $('#inlogtypelist');
		var targetAction = $('#inlogactionlist');
		
		var logType;
		var logAction;
		
		if (selected == 'contentlog') {
			logType = contentType;
			logAction = contentAction;
		} else if (selected == 'accesslog') {
			logType = accessType;
			logAction = accessAction;
		} else if (selected == 'systemlog') {
			logType = systemType;
			logAction = systemAction;
		}
		
		targetType.empty();
		targetAction.empty();
		
		for (x in logType) {
			var opt = document.createElement("option");
			opt.value = logType[x].value;
			opt.innerHTML = logType[x].type;
			targetType.append(opt);
		}
		
		for (x in logAction) {
			var opt = document.createElement("option");
			opt.value = logAction[x].value;
			opt.innerHTML = logAction[x].type;
			targetAction.append(opt);
		}
		
		//로그 유형에 따라서 헤더 변경
		var logHeader_dataHtml = "";
		
		if (selected == 'contentlog') {
			logHeader_dataHtml += "<tr><th><spring:eval expression="@${lang}['LOG_TYPE']"/></th>"
			logHeader_dataHtml += "<th><spring:eval expression="@${lang}['PROCESSING_TYPE']"/></th>"
			logHeader_dataHtml += "<th><spring:eval expression="@${lang}['DOC_NO']"/></th>"
			if (logType == "06") {
				logHeader_dataHtml += "<th><spring:eval expression="@${lang}['DOC_NO']"/></th>"
			} else {
				logHeader_dataHtml += "<th><spring:eval expression="@${lang}['TITLE']"/></th>"                        
			}
			logHeader_dataHtml += "<th><spring:eval expression="@${lang}['OP_USER']"/></th>"
			logHeader_dataHtml += "<th><spring:eval expression="@${lang}['OP_DEPT']"/></th>"
			logHeader_dataHtml += "<th><spring:eval expression="@${lang}['OP_DATE']"/></th></tr>"
		} else if (selected == 'accesslog') {
			logHeader_dataHtml += "<tr><th><spring:eval expression="@${lang}['LOG_TYPE']"/></th>"
			logHeader_dataHtml += "<th><spring:eval expression="@${lang}['PROCESSING_TYPE']"/></th>"
			logHeader_dataHtml += "<th><spring:eval expression="@${lang}['IP_ADDRESS']"/></th>"
			logHeader_dataHtml += "<th><spring:eval expression="@${lang}['OP_USER']"/></th>"
			logHeader_dataHtml += "<th><spring:eval expression="@${lang}['OP_DEPT']"/></th>"
			logHeader_dataHtml += "<th><spring:eval expression="@${lang}['OP_DATE']"/></th></tr>"
		} else if (selected == 'systemlog') {
			logHeader_dataHtml += "<tr><th><spring:eval expression="@${lang}['LOG_TYPE']"/></th>"
			logHeader_dataHtml += "<th><spring:eval expression="@${lang}['PROCESSING_TYPE']"/></th>"
			logHeader_dataHtml += "<th><spring:eval expression="@${lang}['NAME']"/></th>"
			logHeader_dataHtml += "<th><spring:eval expression="@${lang}['OP_USER']"/></th>"
			logHeader_dataHtml += "<th><spring:eval expression="@${lang}['OP_DEPT']"/></th>"
			logHeader_dataHtml += "<th><spring:eval expression="@${lang}['OP_DATE']"/></th></tr>"
		}
		
		$('#logHeader').html(logHeader_dataHtml);
	});
		   
	$('#inlogtypelist').change(function() {
		SearchLog(1);
	});
		
	$('#inlogactionlist').change(function() {
		SearchLog(1);
	});
});

/*********************************
Name   : SearchLog
Desc   : 로그 검색
Param  : pageNum
**********************************/
var SearchLog = function(pageNum) {
	$("#paging").html("");
	var SearchName = $('#SearchByName').val();
	var SDate = $('#RegSDate').val() + " 00:00:00";
	var EDate = $("#RegEDate").val() + " 23:59:59";
	var logtypeselected = $("#inlogtypelist option:selected").val();
	var serchDate = SDate + "：" + EDate;
	var selectedType = $("#inlogselectlist").val();
	var logactionselected = $("#inlogactionlist option:selected").val();	//날짜, 사용자 이름, 선택된 셀렉트 박스 값 할당
	 
	// contentlog 검색
	if (selectedType == "contentlog") {
		var sendData = {
			objIsTest : "N",
			objDebugged : false,
			logtype : logtypeselected,
			objpgnum : pageNum,
			logtime : serchDate
		};
		if (logactionselected != 'none') {	//로그 유형 미선택시 전체 선택
			sendData.action = logactionselected;
		}
		
		if (SearchName != '') {		//사용자 이름 입력시 사용자 이름 추가
			sendData.loggername = SearchName;
		}
	
		// 조회
		$.ajax({
			url : "${ctxRoot}/api/log/content/list",
			type : "POST",
			dataType : "json",
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
				drawContentLog(data, pageNum);
				
				$(".btn_arr").click(function() {
					$(this).addClass('active');
				});
			},
			error : function(request, status, error) {
				alertNoty(request,status,error,"<spring:eval expression="@${msgLang}['ERR_DURING_LOOKUP']" />");
			}
		})
	// accesslog 검색
	} else if (selectedType == "accesslog") {
		console.log("accesslog", selectedType)
		var sendData = {
			objIsTest : "N",
			objDebugged : false,
			logtype : logtypeselected,
			objpgnum : pageNum,
			logtime : serchDate
		};
		if (logactionselected != 'none') {
			sendData.action = logactionselected;
		}
		
		if (SearchName != '') {
			sendData.loggername = SearchName;
		}
		
		console.log("sendData========" + JSON.stringify(sendData))
		// 조회
		$.ajax({
			url : "${ctxRoot}/api/log/access/list",
			type : "POST",
			dataType : "json",
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
				drawAccessLog (data, pageNum);
				
				$(".btn_arr").click(function() {
					$(this).addClass('active');
				});
			},
			error : function(request, status, error) {
				alertNoty(request,status,error,"<spring:eval expression="@${msgLang}['ERR_DURING_LOOKUP']" />");
			}
		})
	// systemlog 검색
	}else if (selectedType == "systemlog") {
		console.log("systemlog", selectedType)
		var sendData = {
			objIsTest : "N",
			objDebugged : false,
			logtype : logtypeselected,
			objpgnum : pageNum,
			logtime : serchDate
		};
		
		if (logactionselected != 'none') {
			sendData.action = logactionselected;
		}
		
		if (SearchName != '') {
			sendData.loggername = SearchName;
		}
		console.log("sendData========" + JSON.stringify(sendData))
		
		// 조회
		$.ajax({
			url : "${ctxRoot}/api/log/system/list",
			type : "POST",
			dataType : "json",
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
				drawSystemLog (data, pageNum);
				
				$(".btn_arr").click(function() {
					$(this).addClass('active');
				});
			},
			error : function(request, status, error) {
				alertNoty(request,status,error,"<spring:eval expression="@${msgLang}['ERR_DURING_LOOKUP']" />");
			}
		})
	}
}

/*********************************
Name   : CountLog
Desc   : 총 페이지의 수
Param  :logtypeselected, pageNum, selectedType, logactionselected, SearchName
**********************************/	
var CountLog = function(logtypeselected, pageNum, selectedType, logactionselected, SearchName) {
	var logtypeselected = logtypeselected;
	var SDate = $('#RegSDate').val() + " 00:00:00";
	var EDate = $("#RegEDate").val() + " 23:59:59";
	var serchDate = SDate + "：" + EDate;
	var pageSize = pagecnt;
	var sendData = {
		objIsTest : "N",
		objDebugged : false,
		logtype : logtypeselected,
		logtime : serchDate
	};
	
	if (logactionselected != 'none') {
		sendData.action = logactionselected;
	}
	
	if (SearchName != '') {
		sendData.loggername = SearchName;
	}
	
	$.ajax({
		url : "${ctxRoot}/api/log/" + selectedType + "/count",
		type : "POST",
		dataType : "json",
		contentType : "application/json",
		async : false,
		data : JSON.stringify(sendData),
		success : function(data) {
			var dataCnt = data.result;
			var pageCnt = Math.ceil(dataCnt / pageSize)
			$("#totCnt").text("<spring:eval expression="@${lang}['TOTAL']"/> : " + data.result)
			
			Paging(pageCnt, pageNum);
		},
		error : function(request, status, error) {
			alertNoty(request,status,error,"<spring:eval expression="@${msgLang}['ERR_PAGING_OCCURRED']" />");
		}
	})	    
};

/*********************************
Name   : Paging
Desc   : 총 페이지의 수, 현재 위치한 페이지의 값
Param  : pageCnt, pageNum
**********************************/	
var Paging = function(pageCnt, pageNum) {
	var pageNext = pageNum + 1;
	var pagePrev = pageNum - 1;
	var pageNext_10 = pageNum + 10;
	var pagePrev_10 = pageNum - 10;
	var pagingNum = 10;
	var block = Math.ceil(pageNum / pagingNum);
	if (pagePrev_10 <= 0) {
		pagePrev_10 = 1;
	}
	if (pageNext_10 >= pageCnt) {
		pageNext_10 = pageCnt;
	}
	if (pageNum <= 0) {
		pageNum = 1;
	}
	
	N = block * pagingNum;
	var activePos = ((pageNum % pagingNum) - 1) + Math.floor(pageNum / pagingNum) * pagingNum;
	var logPaging_dataHtml = "";
	logPaging_dataHtml += "<ul>";
	logPaging_dataHtml += "<li><a href=\"javascript:SearchLog("+pagePrev_10+");\" class='pagBtn pprev'><img src='${image}/icon/double_arrow.png'></a></li>";
	logPaging_dataHtml += "<li><a href=\"javascript:SearchLog("+pagePrev+");\" class='pagBtn prev'><img src='${image}/icon/next_arrow.png'></a></li>";
	for (var i = N - pagingNum; i < N; i++) {
		pageNum = i + 1;
		if (i == activePos) {
			logPaging_dataHtml += "<li><a class='numBtn active' onClick='SearchLog(" + pageNum + ");'>" + pageNum + "</a></li>"
		} else {
			logPaging_dataHtml += "<li><a class='numBtn' onClick='SearchLog(" + pageNum + ");'>" + pageNum + "</a></li>"
		}
	
		if (pageNum > pageCnt - 1) {
			console.log("pageCnt", pageCnt);
			break;
		}
	}
	
	logPaging_dataHtml += "<li><a href=\"javascript:SearchLog("+pageNext+");\" class='pagBtn next'><img src='${image}/icon/next_arrow.png'></a></li>";
	logPaging_dataHtml += "<li><a href=\"javascript:SearchLog("+pageNext_10+");\" class='pagBtn nnext'><img src='${image}/icon/double_arrow.png'></a></li>";
	
	/*
	if(block > 1){
		str += "<li><a href=\"javascript:pageLink"+"(1);\" class = 'pagBtn pprev'><img src='${image}/icon/double_arrow.png'></a></li>";
		str += "<li><a href=\"javascript:pageLink"+"("+prevIndex+");\" class = 'pagBtn prev'><img src='${image}/icon/next_arrow.png'></a></li>";
	}

	for(var i = startPage; i <= endPage ; i++) {
		if(i == curPage) {
			str += "<li><a href=\"#\" class = 'numBtn'>"+i+"&nbsp;</a>";
		} else {
			str += "<li><a href=\"javascript:pageLink"+"("+i+");\" class = 'pagb num'>"+i+"&nbsp;</a>";
		}
	}

	if((block != totalBlock) && totalBlock >0){	
		str += "<li><a href=\"javascript:pageLink"+"("+nextIndex+");\" class = 'pagBtn next'><img src='${image}/icon/double_arrow.png'></a></li>";
		str += "<li><a href=\"javascript:pageLink"+"("+totalPage+");\" class = 'pagBtn nnext'><img src='${image}/icon/double_arrow.png'></a></a></li>";
	}
*/
	$('#pageing').html(logPaging_dataHtml);

	if (activePos == 0) {
		$('.pprev').hide();
		$('.prev').hide();
	}
	if (activePos == pageCnt - 1) {
		$('.next').hide();
		$('.nnext').hide();
	}
};

/*********************************
Name   : drawContentLog
Desc   : 컨텐츠 관련 로그
Param  : data, pageNum
**********************************/	
function drawContentLog (data, pageNum) {
	var SearchName = $('#SearchByName').val();
	var logList_dataHtml = "";
	var logtypeselected = $("#inlogtypelist option:selected").val();
	var logactionselected = $("#inlogactionlist option:selected").val();//날짜, 사용자 이름, 선택된 셀렉트 박스 값 할당
	
	if (data.status == "0000") {
		if (objectIsEmpty(data.result)) {
			$("#logList").html("<tr><td colspan=7><spring:eval expression="@${msgLang}['NOTEXIST_LOG']"/></td></tr>");
			$("#totCnt").text("<spring:eval expression="@${lang}['TOTAL']"/> : 0");
			$("#pageing").html("");
			return;
		}
		var action = "";
		var logtype = "";
		
		for (var i = 0; i < data.result.length; i++) {
			var loggername = data.result[i].loggername;
			var loggerdeptname = data.result[i].loggerdeptname;
			var tmpAction = data.result[i].action;
			
			if (tmpAction.charAt(0) == "A") {
				if (tmpAction.charAt(1) == "1") {        action = "<spring:eval expression="@${lang}['NEW']"/>"
				} else if (tmpAction.charAt(1) == "2") { action = "<spring:eval expression="@${lang}['LOG_NEW_REQUEST']"/>"
				} else if (tmpAction.charAt(1) == "3") { action = "<spring:eval expression="@${lang}['LOG_NEW_APPROVAL']"/>"
				} else if (tmpAction.charAt(1) == "4") { action = "<spring:eval expression="@${lang}['LOG_NEW_RETURN']"/>"
				}
			} else if (tmpAction.charAt(0) == "B") {
				if (tmpAction.charAt(1) == "1") {        action = "<spring:eval expression="@${lang}['MODIFY']"/>"
				} else if (tmpAction.charAt(1) == "2") { action = "<spring:eval expression="@${lang}['LOG_MODIFY_REQUEST']"/>"
				} else if (tmpAction.charAt(1) == "3") { action = "<spring:eval expression="@${lang}['LOG_MODIFY_APPROVAL']"/>"
				} else if (tmpAction.charAt(1) == "4") { action = "<spring:eval expression="@${lang}['LOG_MODIFY_RETURN']"/>"
				} else if (tmpAction.charAt(1) == "5") { action = "<spring:eval expression="@${lang}['LOG_CHECK-OUT_REQUEST']"/>"//변경(요청)
				} else if (tmpAction.charAt(1) == "6") { action = "<spring:eval expression="@${lang}['LOG_CHECK-OUT_REQUEST']"/>"
				} else if (tmpAction.charAt(1) == "7") { action = "<spring:eval expression="@${lang}['LOG_CHECK-OUT_APPROVAL']"/>"
				} else if (tmpAction.charAt(1) == "8") { action = "<spring:eval expression="@${lang}['LOG_CHECK-OUT_RETURN']"/>"
				} else if (tmpAction.charAt(1) == "9") { action = "<spring:eval expression="@${lang}['LOG_CHECK-IN_REQUEST']"/>"//잠금해제
				} else if (tmpAction.charAt(1) == "A") { action = "<spring:eval expression="@${lang}['LOG_CHECK-IN_FOURCE']"/>"
				}
			} else if (tmpAction.charAt(0) == "C") {
				if (tmpAction.charAt(1) == "1") {        action = "<spring:eval expression="@${lang}['MOVE']"/>"
				} else if (tmpAction.charAt(1) == "2") { action = "<spring:eval expression="@${lang}['LOG_MOVE_REQUEST']"/>"
				} else if (tmpAction.charAt(1) == "3") { action = "<spring:eval expression="@${lang}['LOG_MOVE_APPROVAL']"/>"
				} else if (tmpAction.charAt(1) == "4") { action = "<spring:eval expression="@${lang}['LOG_MOVE_RETURN']"/>"
				}
			} else if (tmpAction.charAt(0) == "D") {
				if (tmpAction.charAt(1) == "1") {        action = "<spring:eval expression="@${lang}['COPY']"/>"
				} else if (tmpAction.charAt(1) == "2") { action = "<spring:eval expression="@${lang}['LOG_COPY_REQUEST']"/>"
				} else if (tmpAction.charAt(1) == "3") { action = "<spring:eval expression="@${lang}['LOG_COPY_APPROVAL']"/>"
				} else if (tmpAction.charAt(1) == "4") { action = "<spring:eval expression="@${lang}['LOG_COPY_RETURN']"/>"
				}
			} else if (tmpAction.charAt(0) == "E") {
				if (tmpAction.charAt(1) == "1") {        action = "<spring:eval expression="@${lang}['DELETE']"/>"
				} else if (tmpAction.charAt(1) == "2") { action = "<spring:eval expression="@${lang}['LOG_DELETE_REQUEST']"/>"
				} else if (tmpAction.charAt(1) == "3") { action = "<spring:eval expression="@${lang}['LOG_DELETE_APPROVAL']"/>"
				} else if (tmpAction.charAt(1) == "4") { action = "<spring:eval expression="@${lang}['LOG_DELETE_RETURN']"/>"
				} else if (tmpAction.charAt(1) == "5") { action = "<spring:eval expression="@${lang}['RESTORE']"/>"
				} else if (tmpAction.charAt(1) == "6") { action = "<spring:eval expression="@${lang}['LOG_RESTORE_REQUEST']"/>"
				} else if (tmpAction.charAt(1) == "7") { action = "<spring:eval expression="@${lang}['LOG_RESTORE_APPROVAL']"/>"
				} else if (tmpAction.charAt(1) == "8") { action = "<spring:eval expression="@${lang}['LOG_RESTORE_RETURN']"/>"
				}
			} else if (tmpAction.charAt(0) == "F") {
				if (tmpAction.charAt(1) == "1") {        action = "<spring:eval expression="@${lang}['DISCARD']"/>"
				} else if (tmpAction.charAt(1) == "2") { action = "<spring:eval expression="@${lang}['LOG_DISCARD_REQUEST']"/>(요청)"
				} else if (tmpAction.charAt(1) == "3") { action = "<spring:eval expression="@${lang}['LOG_DISCARD_APPROVAL']"/>"
				} else if (tmpAction.charAt(1) == "4") { action = "<spring:eval expression="@${lang}['LOG_DISCARD_RETURN']"/>"
				} else if (tmpAction.charAt(1) == "5") { action = "<spring:eval expression="@${lang}['LOG_LOG_FOURCE_DISCARD']"/>"
				}
			} else if (tmpAction.charAt(0) == "G") {
				if (tmpAction.charAt(1) == "1") {        action = "<spring:eval expression="@${lang}['VIEW']"/>"
				} else if (tmpAction.charAt(1) == "2") { action = "<spring:eval expression="@${lang}['LOG_SEARCH_REQUEST']"/>"
				} else if (tmpAction.charAt(1) == "3") { action = "<spring:eval expression="@${lang}['LOG_SEARCH_APPROVAL']"/>"
				} else if (tmpAction.charAt(1) == "4") { action = "<spring:eval expression="@${lang}['LOG_SEARCH_RETURN']"/>"
				}
			} else if (tmpAction.charAt(0) == "H") {
				if (tmpAction.charAt(1) == "1") {        action = "<spring:eval expression="@${lang}['SORT']"/>"
				} else if (tmpAction.charAt(1) == "2") { action = "<spring:eval expression="@${lang}['LOG_SORT_REQUEST']"/>"
				} else if (tmpAction.charAt(1) == "3") { action = "<spring:eval expression="@${lang}['LOG_SORT_APPROVAL']"/>"
				} else if (tmpAction.charAt(1) == "4") { action = "<spring:eval expression="@${lang}['LOG_SORT_RETURN']"/>"
				}
			} else if (tmpAction.charAt(0) == "I") {
				if (tmpAction.charAt(1) == "1") {        action = "<spring:eval expression="@${lang}['FAVORITE']"/>"
				} else if (tmpAction.charAt(1) == "2") { action = "<spring:eval expression="@${lang}['UN-FAVORITE']"/>"
				}
			} else if (tmpAction.charAt(0) == "J") {
				if (tmpAction.charAt(1) == "1") {        action = "<spring:eval expression="@${lang}['LOG_NEW_APPROVAL']"/>"
				} else if (tmpAction.charAt(1) == "2") { action = "<spring:eval expression="@${lang}['LOG_NEW_RETURN']"/>"
				}
			} else if (tmpAction.charAt(0) == "Y") {
				if (tmpAction.charAt(1) == "1") {        action = "<spring:eval expression="@${lang}['LINK']"/>"
				} else if (tmpAction.charAt(1) == "2") { action = "<spring:eval expression="@${lang}['LOG_LINK_REQUEST']"/>"
				} else if (tmpAction.charAt(1) == "3") { action = "<spring:eval expression="@${lang}['LOG_LINK_APPROVAL']"/>"
				} else if (tmpAction.charAt(1) == "4") { action = "<spring:eval expression="@${lang}['LOG_LINK_RETURN']"/>"
				} else if (tmpAction.charAt(1) == "5") { action = "<spring:eval expression="@${lang}['UNLINK']"/>"
				}
			} else if (tmpAction.charAt(0) == "Z") {
				if (tmpAction.charAt(1) == "1") {        action = "<spring:eval expression="@${lang}['SHARE']"/>"
				} else if (tmpAction.charAt(1) == "2") { action = "<spring:eval expression="@${lang}['LOG_SHARE_REQUEST']"/>"
				} else if (tmpAction.charAt(1) == "3") { action = "<spring:eval expression="@${lang}['LOG_SHARE_APPROVAL']"/>"
				} else if (tmpAction.charAt(1) == "4") { action = "<spring:eval expression="@${lang}['LOG_SHARE_RETURN']"/>"
				} else if (tmpAction.charAt(1) == "5") { action = "<spring:eval expression="@${lang}['UNSHARE']"/>"
				} else if (tmpAction.charAt(1) == "6") { action = "<spring:eval expression="@${lang}['LOG_UNSHARE_REQUEST']"/>"
				} else if (tmpAction.charAt(1) == "7") { action = "<spring:eval expression="@${lang}['LOG_UNSHARE_APPROVAL']"/>"
				} else if (tmpAction.charAt(1) == "8") { action = "<spring:eval expression="@${lang}['LOG_UNSHARE_RETURN']"/>"
				} else if (tmpAction.charAt(1) == "9") { action = "<spring:eval expression="@${lang}['MODIFY_SHARE']"/>"
				}
			}
					
			if (data.result[i].logtype == "01") {        logtype = "<spring:eval expression="@${lang}['BUNDLE']"/>"
			} else if (data.result[i].logtype == "02") { logtype = "<spring:eval expression="@${lang}['FILE']"/>"
			} else if (data.result[i].logtype == "03") { logtype = "<spring:eval expression="@${lang}['CLASSIFICATION']"/>"
			} else if (data.result[i].logtype == "04") { logtype = "<spring:eval expression="@${lang}['LINK']"/>"
			} else if (data.result[i].logtype == "05") { logtype = "<spring:eval expression="@${lang}['SHARE']"/>"
			} else if (data.result[i].logtype == "06") { logtype = "<spring:eval expression="@${lang}['LOCK']"/>"
			}
			
			var logtime = data.result[i].logtime;
			var logid = data.result[i].logid;
			var logs = data.result[i].logs;
			var fileNo = ""
			var fileList = ""
			var parse = JSON.parse(logs);
			var fileName = parse.TITLE;
			
			if (action == '<spring:eval expression="@${lang}['NEW']"/>') {
			
				if (parse.CONTENT != null && parse.CONTENT.zappFiles != null) {
					var fileListlength = Object.keys(parse.CONTENT.zappFiles).length;
					var fileList = "<spring:eval expression="@${lang}['FILE_NAME']"/> : ";
					for (n = 0; n < fileListlength; n++) {
						fileList += parse.CONTENT.zappFiles[n].filename;
						if (n + 1 != fileListlength) {
							fileList += ", "
						}
					}
				}
			}
			
			if (fileName == undefined) {
				var fileName = parse.title;
				if (fileName == undefined) {
					if(data.result[i].logtype == "03"){ // classification
						console.log(i + " / parse.CONTENT_AFTER : ", parse.CONTENT_AFTER);
						if(parse.CONTENT_AFTER != undefined){
							console.log(i + " / parse.CONTENT_AFTER : ", parse.CONTENT_AFTER.name);
							fileName = parse.CONTENT_AFTER.name;	
						}else{
							fileName ="";    							
						}
					} else if(data.result[i].logtype == "06"){ // lock
						var fileName = "사유 : ";
						fileName += parse.CONTENT.reason;
					}else{
						var fileName = "<spring:eval expression="@${lang}['TITLE']"/> : ";
						fileName += parse.CONTENT.title;
					}
				}
			} else if (fileList != "") {
				fileName = fileList;
			} else {
				fileName = parse.TITLE.split("：")[0];
			}
			
			var loggerid = data.result[i].loggername;
			logList_dataHtml += "<tr>"
			logList_dataHtml += "<td>" + logtype + "</td>"
			logList_dataHtml += "<td>" + action + "</td>"
			
			var fileNo = "";
			   
			// 조회,수정 등등
			if (parse.CONTENT != undefined) {
				if (parse.CONTENT.bno != undefined)
					fileNo = parse.CONTENT.bno;
				if (parse.CONTENT.contentno != undefined)
					fileNo = parse.CONTENT.contentno;
			}
			// 신규
			if (parse.CONTENT_AFTER != undefined) {
				if (parse.CONTENT_AFTER.contentno != undefined)
					fileNo = parse.CONTENT_AFTER.contentno;
			}
	
			if (logtype == "<spring:eval expression="@${lang}['BUNDLE']"/>") {
				if (fileNo == undefined) {
					if (action == "<spring:eval expression="@${lang}['DELETE']"/>" || action == "<spring:eval expression="@${lang}['DISCARD']"/>" || action == "<spring:eval expression="@${lang}['RESTORE']"/>") {
						fileNo = "<spring:eval expression="@${msgLang}['UNABLE_DOC_NUMBER']"/>"
						console.log("fileNo 2",fileNo);
					} else{
						fileNo = parse.CONTENT.contentno;
						if(fileNo == undefined) {
							if(logtype == "<spring:eval expression="@${lang}['BUNDLE']"/>"){
								fileNo = parse.CONTENT.zappBundle.bno;
							}
						}
						if(fileNo == undefined){
							if(logtype == "<spring:eval expression="@${lang}['FILE']"/>"){
								fileNo = parse.CONTENT.zappFile.fno;
							}
						}
						if(fileNo == undefined){
							fileNo = "<spring:eval expression="@${msgLang}['UNABLE_DOC_NUMBER']"/>"
						}
					}
				}
			}
		 
			logList_dataHtml += "<td>" + fileNo + "</td>"
			logList_dataHtml += "<td style='text-align:left; padding-left:10px'>" + fileName + "</td>"
			logList_dataHtml += "<td>" + loggername + "</td>"
			logList_dataHtml += "<td>" + loggerdeptname + "</td>"
			logList_dataHtml += "<td>" + logtime + "</td></tr>";
		} // end for
	
		CountLog(logtypeselected, pageNum, 'content', logactionselected, SearchName);
			
		if (data.result.length > 0) {
			$('#logList').html(logList_dataHtml);
		}
	}
}

/*********************************
Name   : drawAccessLog
Desc   : 로그인/아웃 관련 로그
Param  : data, pageNum
**********************************/	
function drawAccessLog (data, pageNum) {
	var SearchName = $('#SearchByName').val();
	var logList_dataHtml = "";
	var logtypeselected = $("#inlogtypelist option:selected").val();
	var logactionselected = $("#inlogactionlist option:selected").val();//날짜, 사용자 이름, 선택된 셀렉트 박스 값 할당
	
	if (data.status == "0000") {
		if (objectIsEmpty(data.result)) {
			$("#logList").html("<tr><td colspan=7><spring:eval expression="@${msgLang}['NOTEXIST_LOG']"/></td></tr>");
			$("#totCnt").text("<spring:eval expression="@${lang}['TOTAL']"/> : 0");
			return;
		}

		var action = "";
		var logtype = "";
		for (var i = 0; i < data.result.length; i++) {
			var loggername = data.result[i].loggername;
			var loggerdeptname = data.result[i].loggerdeptname;
			
			if (data.result[i].action == "01") {        action = "<spring:eval expression="@${lang}['LOGIN']"/>"
			} else if (data.result[i].action == "02") { action = "<spring:eval expression="@${lang}['LOGOUT']"/>"
			} else if (data.result[i].action == "03") { action = "<spring:eval expression="@${msgLang}['LOGIN_FAILED']"/>"
			}
	
			if (data.result[i].logtype == "01") {
				logtype = "<spring:eval expression="@${lang}['CERIFIED']"/>"
			} else {
				logtype = "undefined"
			}
			
			var logtime = data.result[i].logtime;
			var logid = data.result[i].logid;
			var loginip = data.result[i].logip;
			var logs = data.result[i].logs;
			var parse = JSON.parse(logs);
			
			logList_dataHtml += "<tr>"
			logList_dataHtml += "<td>" + logtype + "</td>"
			logList_dataHtml += "<td>" + action + "</td>"
			logList_dataHtml += "<td>" + loginip + "</td>"
			logList_dataHtml += "<td>" + loggername + "</td>"
			logList_dataHtml += "<td>" + loggerdeptname + "</td>"
			logList_dataHtml += "<td>" + logtime + "</td></tr>";
		}  		
			CountLog(logtypeselected, pageNum, 'access', logactionselected, SearchName);
		if (data.result.length > 0) {
			$('#logList').html(logList_dataHtml);
		}
	}
}

/*********************************
Name   : drawSystemLog
Desc   : 시스템 관련 로그
Param  : data, pageNum
**********************************/	
function drawSystemLog (data, pageNum) {
	console.log("=== drawSystemLog");
	
	var SearchName = $('#SearchByName').val();
	var logList_dataHtml = "";
	var logtypeselected = $("#inlogtypelist option:selected").val();
	var logactionselected = $("#inlogactionlist option:selected").val();//날짜, 사용자 이름, 선택된 셀렉트 박스 값 할당
	
	if (data.status == "0000") {
		if (objectIsEmpty(data.result)) {
			$("#logList").html("<tr><td colspan=7><spring:eval expression="@${msgLang}['NOTEXIST_LOG']"/></td></tr>");
			$("#totCnt").text("<spring:eval expression="@${lang}['TOTAL']"/> : 0");
			return;
		}

		var action = "";
		var logtype = "";
		for (var i = 0; i < data.result.length; i++) {
			var loggername = data.result[i].loggername;
			var loggerdeptname = data.result[i].loggerdeptname;
			var tmpAction = data.result[i].action;
			var tmpLogtype = data.result[i].logtype;
	
			if (tmpAction.charAt(0) == "A") {        action = "<spring:eval expression="@${lang}['NEW']"/>"
			} else if (tmpAction.charAt(0) == "B") { action = "<spring:eval expression="@${lang}['CHANGE']"/>"
			} else if (tmpAction.charAt(0) == "C") { action = "<spring:eval expression="@${lang}['MOVE']"/>"
			} else if (tmpAction.charAt(0) == "D") { action = "<spring:eval expression="@${lang}['COPY']"/>"
			} else if (tmpAction.charAt(0) == "E") { action = "<spring:eval expression="@${lang}['DELETE']"/>"
			} else if (tmpAction.charAt(0) == "F") { action = "<spring:eval expression="@${lang}['DISCARD']"/>"
			} else if (tmpAction.charAt(0) == "G") { action = "<spring:eval expression="@${lang}['SEARCH']"/>"
			} else if (tmpAction.charAt(0) == "H") { action = "<spring:eval expression="@${lang}['SORT']"/>"
			} else if (tmpAction.charAt(0) == "Y") { action = "<spring:eval expression="@${lang}['LINK']"/>"
			} else if (tmpAction.charAt(0) == "Z") { action = "<spring:eval expression="@${lang}['SHARE']"/>"
			}
			
			if (tmpLogtype == "01") {        logtype = "<spring:eval expression="@${lang}['COMPANY']"/>"
			} else if (tmpLogtype == "02") { logtype = "<spring:eval expression="@${lang}['DEPARTMENT']"/>"
			} else if (tmpLogtype == "03") { logtype = "<spring:eval expression="@${lang}['USER']"/>"
			} else if (tmpLogtype == "04") { logtype = "<spring:eval expression="@${lang}['GROUP']"/>"
			} else if (tmpLogtype == "11") { logtype = "<spring:eval expression="@${lang}['PREFERENCE']"/>"
			} else if (tmpLogtype == "12") { logtype = "<spring:eval expression="@${lang}['CODE']"/>"
			}
			var logtime = data.result[i].logtime;
			var logid = data.result[i].logid;
			var logs = data.result[i].logs;
			var parse = JSON.parse(logs);
			var name;
			
			if (logtype == "<spring:eval expression="@${lang}['COMPANY']"/>" || logtype == "<spring:eval expression="@${lang}['DEPARTMENT']"/>") {
				name = parse.CONTENT.name;
			} else if (logtype == "<spring:eval expression="@${lang}['USER']"/>"){
				name = parse.CONTENT.name + " (" + parse.CONTENT.loginid + ")";
			} else if(logtype == "<spring:eval expression="@${lang}['GROUP']"/>"){
				name = parse.CONTENT.zappGroup.name;
			} else if(logtype == "<spring:eval expression="@${lang}['PREFERENCE']"/>"){
				name = parse.CONTENT.zappWorkflow.name;
			} else if(logtype == "<spring:eval expression="@${lang}['CODE']"/>"){
				name = '';
			}
		
			logList_dataHtml += "<tr>"
			logList_dataHtml += "<td>" + logtype + "</td>"
			logList_dataHtml += "<td>" + action + "</td>"
			logList_dataHtml += "<td>" + name + "</td>"
			logList_dataHtml += "<td>" + loggername + "</td>"
			logList_dataHtml += "<td>" + loggerdeptname + "</td>"
			logList_dataHtml += "<td>" + logtime + "</td></tr>";
		}
	
		CountLog(logtypeselected, pageNum, 'system', logactionselected, SearchName);
		if (data.result.length > 0) {
			$('#logList').html(logList_dataHtml);
		} else {
		}
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
					<h1 class="pageTit"><img src="${image}/icon/icon_b01.png" alt=""><spring:eval expression="@${lang}['LOG']" /></h1>
					<div class="wdt100">
						<div class="inner_uiGroup">
							<p><spring:eval expression="@${lang}['SELECT_LOG_TYPE']" /></p>
							<select id="inlogselectlist">
								<option value="contentlog"><spring:eval expression="@${lang}['CONTENTS']" /></option>
								<option value="accesslog"><spring:eval expression="@${lang}['ACCESS']" /></option>
								<option value="systemlog"><spring:eval expression="@${lang}['SYSTEM']" /></option>
							</select>
							<p><spring:eval expression="@${lang}['LOG_TYPE']" /></p>
							<select id="inlogtypelist">
								<option value="01"><spring:eval expression="@${lang}['BUNDLE']" /></option>
								<option value="02"><spring:eval expression="@${lang}['FILE']" /></option>
								<option value="03"><spring:eval expression="@${lang}['CLASSIFICATION']" /></option>
								<option value="04"><spring:eval expression="@${lang}['LINK']" /></option>
								<option value="05"><spring:eval expression="@${lang}['SHARE']" /></option>
								<option value="06"><spring:eval expression="@${lang}['LOCK']" /></option>
							</select>
							<p><spring:eval expression="@${lang}['PROCESSING_TYPE']" /></p>
							<select id="inlogactionlist">
								<option value="none"><spring:eval expression="@${lang}['THE_ENTIRE']" /></option>
								<option value="A1"><spring:eval expression="@${lang}['NEW']" /></option>
								<option value="B1"><spring:eval expression="@${lang}['MODIFY']" /></option>
								<option value="C1"><spring:eval expression="@${lang}['MOVE']" /></option>
								<option value="D1"><spring:eval expression="@${lang}['COPY']" /></option>
								<option value="E1"><spring:eval expression="@${lang}['DELETE']" /></option>
								<option value="E5"><spring:eval expression="@${lang}['RESTORE']" /></option>
								<option value="F1"><spring:eval expression="@${lang}['DISCARD']" /></option>
								<option value="G1"><spring:eval expression="@${lang}['VIEW']" /></option>
								<option value="Z1"><spring:eval expression="@${lang}['SHARE']" /></option>
							</select>
							<p align:center;"><spring:eval expression="@${lang}['PERIOD']" /></p>
							<input type="date" id="RegSDate">&nbsp;&nbsp;<input type="date" id="RegEDate">
							<p><spring:eval expression="@${lang}['USER']" /></p>
							<input type="text" style="width: 100px;" id="SearchByName">
							<button type="button" class="btbase" id="btnSrch" name="srch"><spring:eval expression="@${lang}['VIEW']" /></button>
						</div>
						<div class="tbl_wrap_admin_log">
							<table class="inner_tbl">
								<thead id="logHeader">
									<th style="width:9%;"><spring:eval expression="@${lang}['LOG_TYPE']" /></th>
									<th style="width:9%;"><spring:eval expression="@${lang}['PROCESSING_TYPE']" /></th>
									<th style="width:12%;"><spring:eval expression="@${lang}['DOC_NO']" /></th>
									<th style="width:30%;"><spring:eval expression="@${lang}['TITLE']" /></th>
									<th style="width:9%;"><spring:eval expression="@${lang}['OP_USER']" /></th>
									<th style="width:9%;"><spring:eval expression="@${lang}['OP_DEPT']" /></th>
									<th style="width:15%;"><spring:eval expression="@${lang}['OP_DATE']" /></th>
								</thead>
							</table>
							<div class="tbody_wrap_code">
								<table>
									<colgroup>
										<col width="9%">
										<col width="9%">
										<col width="12%">
										<col width="30%">
										<col width="9%">
										<col width="9%">
										<col width="15%">
									</colgroup>
									<tbody id="logList">

									</tbody>
								</table>
							</div>
						</div>
						<div class="pagination" id = "pageing">
					</div>
				</div><!--innerWrap-->
			</section>
		</div>
	</main>
</body>
</html>