<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*"%>
<%@include file="../common/CommonInclude.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />    
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0, minimum-scale=1.0, maximum-scale=2.0, user-scalable=no">
<title>NADi4.0 :: <spring:eval expression="@${lang}['STAT_MANAGEMENT']" /></title>
<link rel="stylesheet" href="${css}/reset.css">
<link rel="stylesheet" href="${css}/common.css">
<link rel="stylesheet" href="${css}/style.css">
<link rel="stylesheet" href="${css}/jstree.css" />
<link rel="stylesheet" href="${js}/jquery-ui-1.13.1/jquery-ui.min.css">

<script src="${js}/jquery-1.12.2.min.js"></script>
<script src="${js}/jquery-ui-1.13.1/jquery-ui.min.js"></script>
<script src="${js}/jquery.noty.packaged.min.js"></script>
<script src="${js}/jquery-ui.js"></script>
<script src="${js}/common.js"></script>
<script src="${js}/jstree.js"></script>
<script type="text/javascript">
var rootTree = {};
$(document).ready(function () {

	fn_SetCalendar();
	
	fn_TreeRoot();
	
	fn_InitTree();
   	
	//검색 버튼
	$("#btnSrch").click(function() {
		fn_OrgnSearch();
	});
	
	//대상 변경시 달력 변경
	$("#searchTermType").change(function() {
		fn_TermTypeChange();
	});
	
	//검색 시작 달력 변경 선택 처리
	$("#smChange").change(function() {
		fn_SmChange();
	});

	//검색 종료 달력 변경 선택 처리
	$("#emChange").change(function() {
		fn_EmChange();
	});
});

/*********************************
Name   : fn_SetCalendar
Desc   : 달력 초기 입력값 처리
Param  : 
**********************************/
var fn_SetCalendar = function() {
	let today = new Date();   
	let year = today.getFullYear(); 	// 년도
	let month = today.getMonth() + 1;  	// 월
	let date = today.getDate();  		// 날짜
	let day = today.getDay();  			// 요일

	for (var i = year-5; i <= year; i++) {
		s_year.options[i - (year-5)] = new Option(i + "년", i);
	}
	for (var i = 0; i < 12; i++) {
		s_month.options[i] = new Option(i + 1 + "월", i+1);
	}
	
	for (var i = year-5; i <= year; i++) {
		e_year.options[i - (year-5)] = new Option(i + "년", i);
	}
	for (var i = 0; i < 12; i++) {
		e_month.options[i] = new Option(i + 1 + "월", i+1);
	}	
	
	fn_SmChange();	//검색 시작일 변경 처리
	fn_EmChange();	//검새 종료일 변경 처리
	
	$("#s_year").val(year).prop("selected", true);
	$("#e_year").val(year).prop("selected", true);
	$("#e_month").val(month).prop("selected", true);
	$("#e_day").val(date).prop("selected", true);
	
	fn_TermTypeChange();	//대상 변경관련 처리
}

/*********************************
Name   : fn_SmChange
Desc   : 달력 검색 시작일자 변경
Param  : 
**********************************/
var fn_SmChange = function() {
	var cmbday = document.getElementById("s_day");
	cmbday.options.length = 0;
	var year = parseInt(s_year.options[s_year.selectedIndex].value);
	var month = parseInt(s_month.options[s_month.selectedIndex].value);

	//날짜간 덧셈, 뺄샘 과정이 일어날 수 있음으로 number로 형변환
	var lastDay = getLastDay(year, month);
	for (var i = 0; i <= lastDay - 1; i++) {
	    cmbday.options[i] = new Option(i + 1 + "일", i + 1);
	}
}

/*********************************
Name   : fn_EmChange
Desc   : 달력 검색 종료일자 변경
Param  : 
**********************************/
var fn_EmChange = function() {
	var cmbday = document.getElementById("e_day");
	cmbday.options.length = 0;
	var year = parseInt(e_year.options[e_year.selectedIndex].value);
	var month = parseInt(e_month.options[e_month.selectedIndex].value);

	//날짜간 덧셈, 뺄샘 과정이 일어날 수 있음으로 number로 형변환
	var lastDay = getLastDay(year, month);
	for (var i = 0; i <= lastDay - 1; i++) {
	    cmbday.options[i] = new Option(i + 1 + "일", i + 1);
	}
}

/*********************************
Name   : fn_TermTypeChange
Desc   : 검색 대상별 달력 처리
Param  : 
**********************************/
var fn_TermTypeChange = function() {
	var termType = $("#searchTermType option:selected").val();
	if (termType == "Y") {
		$("#e_year").attr("disabled", false);
		$("#s_month").attr("disabled", true);
		$("#e_month").attr("disabled", true);
		$("#s_day").attr("disabled", true);
		$("#e_day").attr("disabled", true);
	} else if (termType == "M") {
		$("#e_year").attr("disabled", true);
		$("#s_month").attr("disabled", true);
		$("#e_month").attr("disabled", true);
		$("#s_day").attr("disabled", true);
		$("#e_day").attr("disabled", true);
	} else if (termType == "D") {
		$("#e_year").attr("disabled", false);
		$("#s_month").attr("disabled", false);
		$("#e_month").attr("disabled", false);
		$("#s_day").attr("disabled", false);
		$("#e_day").attr("disabled", false);
	}
}

/*********************************
Name   : fn_OrgnSearch
Desc   : 현황 및 통계 조회
Param  : 
**********************************/
var fn_OrgnSearch = function() {	
	var sDate, eDate;
	var organType = $("#searchOrganType option:selected").val();
	var termType = $("#searchTermType option:selected").val();
	if (termType == "Y") {
		sDate = $("#s_year").val();
		eDate = $("#e_year").val();
	} else if (termType == "M") {
		sDate = $("#s_year").val();
	} else if (termType == "D") {
		sDate = $("#s_year").val() + "-" + $("#s_month").val().padStart(2, '0') + "-" + $("#s_day").val().padStart(2, '0');
		eDate = $("#e_year").val() + "-" + $("#e_month").val().padStart(2, '0') + "-" + $("#e_day").val().padStart(2, '0');  			
	
		var date1 = new Date(sDate);
		var date2 = new Date(eDate);
		if (date1 > date2) {
			alert('시작일자가 종료일자보다 큽니다.');
			return;
		}
		var diffDate = (date2-date1)/1000/60/60/24;

		if (diffDate > 31) {
			alert('일자별 검색의 최대 기간은 31일입니다.');
			return;
		}
	}
	
	var paramObjType = $("#searchOrganType option:selected").val(); // 00:기관, 01:사용자, 02:부서
	var paramTermType = $("#searchTermType option:selected").val(); // D:Day, W: Week, M: Month, Q:Quarter, H: Half year, Y:Year
	var paramSDate = sDate;
	var paramEDate = eDate;
	var paramAction = "A1-F1";
	
	openLayer("<spring:eval expression="@${msgLang}['RETRIEVING_LIST_LIST']"/>");
	
	setTimeout(function(){
		// 처리현황 조회 (전체)
		fn_Status.getProcessListAll(paramObjType, paramTermType, paramSDate, paramEDate, paramAction);
	},1000)
}

/*********************************
Name   : fn_OrgnSearch
Desc   : rootTree 생성
Param  : 
**********************************/
var fn_TreeRoot = function() {
	if(${Authentication.sessOnlyDeptUser.usertype == '03'}){	//기관 관리자 일경우 기관전체
		rootTree.id = companyid;
	}else{
		rootTree.id = deptid; 
	}

	rootTree.text = companyName;
}

/*********************************
Name   : getStartDay, getLastDay
Desc   : 달력 관련 처리 함수
Param  : 
**********************************/
function getStartDay(year, month) {
	var date = new Date(year, month);
	return date.getDay();
}
function getLastDay(year, month) {
	var lastDay = new Date(year, month);
	lastDay.setDate(0);
	return lastDay.getDate();
}

/*********************************
Name   : fn_InitTree
Desc   : 트리 초기화
Param  : 
**********************************/
var fn_InitTree = function(){
	$('#znTree').remove();
	var treeHtml = "<ul id='znTree'></ul>";
	$('.contNav').append(treeHtml);
	
	$('#znTree').jstree({
        'plugins' : [ "state", "unique" ],
			'core': {
			'data': [{
				"id": rootTree.id,
				"text": rootTree.text,
				"icon": ADMINTREEICONS.COMPANY,
				"state": {
					"opened": true,
					"disabled": false
					}
				,"li_attr": {},
				"a_attr": {"itemcode":"","itemname":"","appid":rootTree.id,"itemisactive":"Y"}
			}],
				'check_callback': true
		}
	})

	//트리 선택 이벤트
	$('#znTree').bind('select_node.jstree', function(event, data){
		var parentid = data.instance.get_node(data.selected).parent;				//부모아이디
		var parentname = data.instance.get_node(parentid).text;						//부모 명
		var selfid = data.instance.get_node(data.selected).id;
		var selfname = data.instance.get_node(data.selected).text;
		var item_abbrname = data.instance.get_node(data.selected).a_attr.itemname;
		var item_code = data.instance.get_node(data.selected).a_attr.itemcode;
		var item_isactive = data.instance.get_node(data.selected).a_attr.itemisactive;
		var company_user = data.instance.get_node(data.selected).a_attr.company_user;
		var item_type = data.instance.get_node(data.selected).a_attr.itemtype;
		
		rootTree.selectId = selfid;
		rootTree.selectText = selfname;
		rootTree.selectCode = item_code;
		$("#DeptCode").attr("disabled",true).val(rootTree.selectCode);
		$("#btnNewDept").attr("name","modify");
		$("#DeptName").attr("disabled",true).val(selfname);
		$("#DeptAbbrName").val(item_abbrname);
		$("#UserDeptName").text(selfname);
		$("#DeptId").val(selfid);

		if (selfid == companyid) {
	 		$("a[id^=btn][id$=Dept]").hide();
  			$("a[id^=btn][id$=User]").hide();
		} else {
    		//부서 사용자 정보를 받아 온다
    		fn_SelectDeptUserList();
    		
    		var sDate, eDate;
      		var termType = $("#searchTermType option:selected").val();
      		var organType = $("#searchOrganType option:selected").val();
      		var termType = $("#searchTermType option:selected").val();
      		if (termType == "Y") {
      			sDate = $("#s_year").val();
      			eDate = $("#e_year").val();
      		} else if (termType == "M") {
      			sDate = $("#s_year").val();
      		} else if (termType == "D") {
      			sDate = $("#s_year").val() + "-" + $("#s_month").val().padStart(2, '0') + "-" + $("#s_day").val().padStart(2, '0');
      			eDate = $("#e_year").val() + "-" + $("#e_month").val().padStart(2, '0') + "-" + $("#e_day").val().padStart(2, '0');  			

      			var date1 = new Date(sDate);
      			var date2 = new Date(eDate);
      			if (date1 > date2) {
      				alert('시작일자가 종료일자보다 큽니다.');
      				return;
      			}
      			var diffDate = (date2-date1)/1000/60/60/24;
      			if (diffDate > 31) {
      				alert('일자별 검색의 최대 기간은 31일입니다.');
      				return;
      			}
      		}

    		var paramObjId = selfid;
    		var paramObjType = item_type; // 00:기관, 01:사용자, 02:부서
    		var paramTermType = termType // D:Day, W: Week, M: Month, Q:Quarter, H: Half year, Y:Year
    		var paramSDate = sDate;
    		var paramEDate = eDate;
    		var paramAction = "A1-F1";
    
    		// 처리현황 조회
    		fn_Status.getProcessList(selfname, paramObjId, paramObjType, paramTermType, paramSDate, paramEDate, paramAction);
		}
	});
		
	$('#znTree').on('ready.jstree', function () {
		fn_SelectDeptList(); 
	});
};

/*********************************
Name   : fn_SelectDeptList
Desc   : 부서 정보 조회
Param  : 
**********************************/
var fn_SelectDeptList = function() {
	var usrUpid = deptid;
	if(userType== "03"){
		usrUpid = companyid; 
	}
	
	$.ajax({
		url :"${ctxRoot}/api/organ/dept/list/down"
		, type : "POST"
		, dataType : "json"
		, contentType : 'application/json'
		, async : false
		, data : JSON.stringify({
			companyid : companyid,
			upid : companyid,
			 objIsMngMode : true
		})
		, success : function(data){
			if(data.status == "0000") {
				//console.log("fn_SelectDeptList data:", JSON.stringify(data));
				if(objectIsEmpty(data.result)){
					return;
				}
				rootTree.deptList = data.result;
				$.each(data.result,function(index,item){
					tree_parent_code = item.upid;
					treeCode = item.code;
					treeId = item.deptid;
					treeText = item.name;
					treeAbbrName = item.abbrname;
					isactive  = item.isactive;
					order = item.priority;
					fn_CreateNode("#znTree", "#" + tree_parent_code, treeId, treeText, "last"
							, (isactive =='Y') ? ADMINTREEICONS.DEPT : ADMINTREEICONS.DISABLE
							, treeCode, treeAbbrName, isactive, rootTree.id, order, "02"); // 01:사용자, 02:부서
				});
			}
		},   
		error : function(request, status, error) {
      		alertNoty(request,status,error);
		}
	})
};

/*********************************
Name   : fn_SelectDeptUserList
Desc   : 선택된 부서의 사용자 목록
Param  : 
**********************************/
var fn_SelectDeptUserList = function() {

	var sendData = {deptid : rootTree.selectId, objIsMngMode : true}
	//console.log("fn_SelectDeptUserList sendData:", JSON.stringify(sendData));
	$.ajax({
		url :"${ctxRoot}/api/organ/deptuser/list",
		type : "POST",
		dataType : "json",
		contentType : 'application/json',
		async : false,
		data : JSON.stringify(sendData),
		success : function(data){
			//console.log("fn_SelectDeptUserList data:", JSON.stringify(data));
			if (data.status == "0000") {
				if(objectIsEmpty(data.result)){
					$("#SearchAreaUserData").empty();
					return;
				}
				
				$.each(data.result, function(index,item){
					tree_parent_code = rootTree.selectId;
					treeCode = item.code;
					treeId = item.deptuserid;
					treeText = item.zappUser.name;
					treeAbbrName = item.abbrname;
					isactive  = item.isactive;
					order = item.priority;
					fn_CreateNode("#znTree", "#" + tree_parent_code, treeId, treeText, "last", 
							ADMINTREEICONS.USER, treeCode, treeAbbrName, isactive, rootTree.id, order, "01"); // 01:사용자, 02:부서
				});
			} else {
				alertErr(data.message);
			} 
		},   
		error : function(request, status, error) {
      		alertNoty(request,status,error);
		}		
	})
};

/*********************************
Name   : fn_CreateNode
Desc   : 부서 정보 트리 표시
Param  : 
**********************************/
var fn_CreateNode = function(tree_id, parent_code, new_id, new_text, position, icon, item_code, item_name, itemisactive, f_appid, priority, item_type){
	var rtnVal = $(tree_id).jstree (
  		'create_node'
		, parent_code
		, {	  "text"   : new_text
			, "id"     : new_id
			, "icon"   : icon
			, "state"  : { "opened": true }
			, "a_attr" : {   "itemcode" : item_code
				           , "itemname" : item_name
				           , "itemisactive" : itemisactive
				           , "appid" : f_appid
				           , "priority" : priority
				           , "itemtype" : item_type
				         } 
		  }
		, position
		, false
		, false
	);
};

/*********************************
Name   : fn_Status.getProcessList
Desc   : 해당 부서/사용자 별 조회
Name   : fn_Status.getProcessListAll
Desc   : 전체 부서/사용자 별 조회
Param  : 
**********************************/
var fn_Status ={
    init : function(){
    	$("a[id$=DeptUser]").hide();
        $("#btnResetPWD").hide();
    },
  	// 해당 부서/사용자 별 조회
	getProcessList : function(selOrganName, paramObjId, paramObjType, paramTermType, paramSDate, paramEDate, paramAction) {
		var dataHtml = "";
		var sendData = {
			"objIsTest" : "N",
			"objType" : paramObjType, 		// 01(사용자), 02(부서)
			"staobjid" : paramObjId,
			"staobjtype" : paramObjType, 	// 00:기관, 01:사용자, 02:부서
			"statermtype" : paramTermType, 	// D:Day, W: Week, M: Month, Q:Quarter, H: Half year, Y:Year
			"stasdate" : paramSDate,
			"staedate" : paramEDate,
			"staaction" : paramAction
		};
		//console.log("getProcessList sendData:", JSON.stringify(sendData));

		$.ajax({
			url : "${ctxRoot}/api/status/list/processall",
			type : "POST",
			dataType : "json",
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
				//console.log("getProcessList data:", JSON.stringify(data));
				if (data.status == "0000") {
					if(objectIsEmpty(data.result)){
						$("#SearchAreaUserData").empty();
						return;
					}
					fn_RenderSearchItem(data, selOrganName);
				} else {
					alertErr(data.message);
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			}
		})
	},
  	// 전체 부서/사용자 별 조회
	getProcessListAll : function(paramObjType, paramTermType, paramSDate, paramEDate, paramAction) {
		var dataHtml = "";
		var sendData = {
			"objIsTest" : "N",
			"objType" : paramObjType, 		// 01(사용자), 02(부서)
			"staobjtype" : paramObjType, 	// 00:기관, 01:사용자, 02:부서
			"statermtype" : paramTermType, 	// D:Day, W: Week, M: Month, Q:Quarter, H: Half year, Y:Year
			"stasdate" : paramSDate,
			"staedate" : paramEDate,
			"staaction" : paramAction			
		};
		//console.log("getProcessListAll sendData:", JSON.stringify(sendData));
		if (paramTermType == "M") {
			sendData.stayear = paramSDate;
		}

		$.ajax({
			url : "${ctxRoot}/api/status/list/processall",
			type : "POST",
			dataType : "json",
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
				//console.log("getProcessListAll data:", JSON.stringify(data));
				if (data.status == "0000") {
					fn_RenderSearchAllItem(data);
				} else {
					alertErr(data.message);
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			}
		})
	}
}

/*********************************
Name   : fn_RenderSearchItem
Desc   : 해당 부서/사용자 별 조회 정보 표시
Param  : 
**********************************/
var fn_RenderSearchItem = function(data, selOrganName){
	$("#SearchAreaUserData").empty();
	var SearchAreaUserData_dataHtml ="";
	var statData = data.result["A1"];
	var metaData = [];
	
	for (var user in statData) {
		var Num = 1;
		for (k=0; k<statData[user].length; k++) {
			var itemA1 = data.result["A1"][user][k];
			var itemF1 = data.result["F1"][user][k];

			var meta ={};
			meta.user = user;
			meta.stasdate = itemA1.stasdate;
			meta.staA1cnt = itemA1.stacnt;
			meta.staF1cnt = itemF1.stacnt;
			metaData.push(meta);
			
			Num++;
			SearchAreaUserData_dataHtml +="<tr>"
			SearchAreaUserData_dataHtml +="<td>" + user + "</td>";
			SearchAreaUserData_dataHtml +="<td>"+meta.stasdate+"</td>";
			SearchAreaUserData_dataHtml +="<td>"+meta.staA1cnt+"</td>";
			SearchAreaUserData_dataHtml +="<td>"+meta.staF1cnt+"</td>";
			SearchAreaUserData_dataHtml +="</tr>"
		}
	}
	$("#SearchAreaUserData").html(SearchAreaUserData_dataHtml);
	$("#statusTable").rowspan(0);
	closeLayer();
}

/*********************************
Name   : fn_RenderSearchAllItem
Desc   : 전체 부서/사용자 별 조회 정보 표시
Param  : 
**********************************/
var fn_RenderSearchAllItem = function(data){
	$("#SearchAreaUserData").empty();
	var SearchAreaUserData_dataHtml ="";
	var statData = data.result["A1"];
	var metaData = [];
	
	for (var user in statData) {
		var Num = 1;
		for (k=0; k<statData[user].length; k++) {
			var itemA1 = data.result["A1"][user][k];
			var itemF1 = data.result["F1"][user][k];
			var meta ={};
			meta.user = user;
			meta.stasdate = itemA1.stasdate;
			meta.staA1cnt = itemA1.stacnt;
			meta.staF1cnt = itemF1.stacnt;
			metaData.push(meta);
			
			Num++;
			SearchAreaUserData_dataHtml +="<tr>"
			SearchAreaUserData_dataHtml +="<td>" + user + "</td>";
			SearchAreaUserData_dataHtml +="<td>"+meta.stasdate+"</td>";
			SearchAreaUserData_dataHtml +="<td>"+meta.staA1cnt+"</td>";
			SearchAreaUserData_dataHtml +="<td>"+meta.staF1cnt+"</td>";
			SearchAreaUserData_dataHtml +="</tr>"
		}
	}

	$("#SearchAreaUserData").html(SearchAreaUserData_dataHtml);
	$("#statusTable").rowspan(0);
	
	closeLayer();
}

/*********************************
 Name   : fn.rowspan
 Desc   : 같은 값이 있는 열을 병합함
          사용법 : $('#테이블 ID').rowspan(0);
 Param  : 
 **********************************/
 $.fn.rowspan = function(colIdx, isStats) {       
    return this.each(function(){      
        var that;     
        $('tr', this).each(function(row) {      
            $('td:eq('+colIdx+')', this).filter(':visible').each(function(col) {
                if ($(this).html() == $(that).html()
                    && (!isStats || isStats && $(this).prev().html() == $(that).prev().html())) {            
                    rowspan = $(that).attr("rowspan") || 1;
                    rowspan = Number(rowspan)+1;
                    $(that).attr("rowspan",rowspan);
                    $(this).hide();
                     
                } else {            
                    that = this;         
                }          
                that = (that == null) ? this : that;      
            });     
        });    
    });  
}; 
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
					<h1 class="pageTit"><img src="${image}/icon/icon_b08.png" alt=""><spring:eval expression="@${lang}['STAT_MANAGEMENT']" /></h1>
					<div class="flex-content">
						<div class="contNav">
							<ul id="znTree"></ul>
						</div>
						<div class="rgt_area">
							<div class="wdt100">
								<h3 class="innerTit"><spring:eval expression="@${lang}['STAT_MANAGEMENT']" /></h3>
								<div class="inner_uiGroup">
									<p style="width:15%"><spring:eval expression="@${lang}['SEPARATOR']" /></p>
										<select id="searchOrganType">
											<option value="01" selected="selected"><spring:eval expression="@${lang}['USER']" /></option>
											<option value="02"><spring:eval expression="@${lang}['DEPARTMENT']" /></option>
										</select>
									<p style="width:15%"><spring:eval expression="@${lang}['TARGET']" /></p>
									<select id="searchTermType">
										<option value="Y" selected="selected"><spring:eval expression="@${lang}['YEAR']" /></option>
										<option value="M"><spring:eval expression="@${lang}['MONTH']" /></option>
										<option value="D"><spring:eval expression="@${lang}['DAY']" /></option>
									</select>
									<p style="width:15%; align:center;"><spring:eval expression="@${lang}['PERIOD']" /></p>
									<form>
										<select name="" id="s_year" style="width:80px"></select>
										<select name="" id="s_month" style="width:60px" onchange="smChange();"></select>
										<select name="" id="s_day" style="width:60px"></select>
										&nbsp;~&nbsp;
										<select name="" id="e_year" style="width:80px"></select>
										<select name="" id="e_month" style="width:60px" onchange="emChange();"></select>
										<select name="" id="e_day" style="width:60px""></select>
										<button type="button" class="btbase" id="btnSrch" name="srch"><spring:eval expression="@${lang}['VIEW']" /></button>
									</form>
								</div>
								<div class="tbl_wrap_admin">
									<table class="inner_tbl" id="statusTable">
										<colgroup>
											<col width="25%">
											<col width="25%">
											<col width="25%">
											<col width="25%">
										</colgroup>
										<thead>
											<th><spring:eval expression="@${lang}['TARGET']" /></th>
											<th><spring:eval expression="@${lang}['PERIOD']" /></th>
											<th><spring:eval expression="@${lang}['REGISTERED_COUNT']" /></th>
											<th><spring:eval expression="@${lang}['DISCARD_COUNT']" /></th>
										</thead>
									</table>
									<div class="tbody_wrap_stat">
										<table>
											<colgroup>
												<col width="25%">
												<col width="25%">
												<col width="25%">
												<col width="25%">
											</colgroup>
											<tbody id="SearchAreaUserData">

											</tbody>
										</table>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div><!--innerWrap-->
			</section>
		</div>
	</main>
</body>
</html>