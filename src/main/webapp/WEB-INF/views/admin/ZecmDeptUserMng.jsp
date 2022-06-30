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
<link rel="stylesheet" href="${css}/jstree.css" />
<link rel="stylesheet" href="${js}/jquery-ui-1.13.1/jquery-ui.min.css">

<script src="${js}/jquery-1.12.2.min.js"></script>
<script src="${js}/jquery-ui-1.13.1/jquery-ui.min.js"></script>
<script src="${js}/jquery.noty.packaged.min.js"></script>
<script src="${js}/jquery-ui.js"></script>
<script src="${js}/jstree.js"></script>
<script src="${js}/common.js"></script>

<script type="text/javascript">

var CODELIST = {"position" :"02" ,"dutyid":"03","seclevelid":"04"};
var rootTree = {};		//트리 정보
var codeListInfo={};	//코드리스트 정보
var userInfo = {};		//유저 정보
var modalZIndex = 100; 
userInfo.employee = "<spring:eval expression="@${lang}['EMPLOYEE_ID']"/>";

$(document).ready(function () {
	//트리 기본정보 세팅
	treeRoot();
	//트리 초기화
	initTree();
	
	//부서 항목 초기화 버튼
	initDeptBtnState();
	//사용자 정보 초기화 버튼
	initDeptUserBtnState();
   	
	initPosition();
	//부서가 선택되었을때
	initDeptBtn();
   	
   	//부서 초기화 버튼
	$("#btnInitDept").click(function(){
	  initDept();
	});
	
	//부서 저장(생성, 수정) 버튼
	$("#btnNewDept").click(function(){
	  if($(this).attr("name") == "create"){
      	fn_Dept.save();
      }else if($(this).attr("name") == "modify"){
      	fn_Dept.modify();
      }
	});
	//부서 삭제 버튼
	$("#btnDelDept").click(function(){
	  fn_Dept.disable();
	});
    //부서 폐기 버튼
	$("#btnDisDept").click(function(){
	  fn_Dept.discard();
	});
	//부서 복구 버튼
	$("#btnResDept").click(function(){
	  fn_Dept.enable();
	});
	
	//부서사용자 입력창 초기화 버튼
	$("#btnInitUser").click(function(){
	  initDeptUser();
	});
	//부서사용자 조회 버튼
	$("#btnOrgnSearch").click(function(){
	  fn_DeptUser.search();
	});
	//부서사용자 저장(생성, 수정) 버튼
	$("#btnNewDeptUser").click(function(){
		console.log("$(this).attr(name) : " + $(this).attr("name"));
	  if($(this).attr("name") == "create"){
	    fn_DeptUser.save();
      }else if($(this).attr("name") == "modify"){
        fn_DeptUser.modify();
      }
	});
	//부서사용자 삭제 버튼
	$("#btnDelDeptUser").click(function(){
	  fn_DeptUser.disable();
	});
    //부서사용자 폐기 버튼
	$("#btnDisDeptUser").click(function(){
	  fn_DeptUser.discard();
	});
	//겸직 추가 show
	$("#btnOriginAdd").click(function(){
		$('.bg').fadeIn();
		fn_openPop("additionalJob");
	    initDeptTree();
	    initDeptInfo();
	});
	//겸직 삭제 버튼
	$("#btnOriginDel").click(function(){
		Origin.remove();
	});
	//겸직 저장 버튼
	$("#btnOriginSave").click(function(){
		Origin.add();
	});
	//겸직 추가 버튼
	$("#btnOriginClose").click(function(){
		Origin.close();
	});
	//비밀번호 초기화 버튼
   	$("#btnResetPWD").click(function(){
   	 fn_DeptUser.reset();
   	});
	
	// 부서순서 변경
    $("img[id=reorder]").click(function() {

        var action = $(this).attr("name");
        var priority = $("#znTree").jstree("get_node", rootTree.selectId).a_attr.priority;
        
        //console.log("priority : " + priority);
        
        if (action == 'up') {
          priority = priority - 1;
        } else if (action == 'down') {
          priority = priority + 1;
        }
        
        //console.log("reorder priority : " + priority);
        
     	reorder(priority);
     });
	
	//유저 리스트 클릭
	$(document.body).delegate('#searchItem', 'click', function() {
		var meta = $(this).data("meta");
		$("#UserDeptName").val(meta.deptname).attr("deptname",meta.deptname).attr("disabled",true);
		$("#UserEmpno").val(meta.empno).attr("deptuserid",meta.deptuserid).attr("disabled",true);
		$("#UserID").val(meta.loginid).attr("userid",meta.userid).attr("disabled",true);
		$("#UserName").val(meta.name);
		$("#UserEmail").val(meta.email);
		$("#UserState").val(meta.isactive);
		$("#ConcurrentOffice").text((meta.originyn =="Y")?"<spring:eval expression="@${lang}['ORIGINAL_JOB']"/>":"<spring:eval expression="@${lang}['ADDITIONAL_JOB']"/>").val(meta.originyn);
		if(meta.originyn == "Y"){
			$("#btnOriginAdd").show();
			$("#btnOriginDel").hide();
		}else{
			$("#btnOriginAdd").hide();
			$("#btnOriginDel").show();
		}
		
		$("#UserDeptType").val(meta.usertype);
		$("#UserDeptPosition").val(meta.positionid);
		$("#UserDeptDutyId").val(meta.dutyid);
		$("#UserDeptSeclevelId").val(meta.seclevelid);
		$("#UserDeptSupervisor").val(meta.issupervisor); 
		
		setDeptUserBtnState(true,(meta.isactive == "Y") ,!(meta.isactive == "Y"));
		$("#btnNewDeptUser").attr("name","modify");
	
		$("#btnResetPWD").show();
		$("#btnNewDeptUser").show();
		
		var class_check =  $(this).attr("class");
		for(var i=1;i<=$("#SearchAreaUserData tr").length;i++){
			$('.num'+i).removeClass("Selected_tr");
		}
		$("."+class_check).addClass("Selected_tr");
	});
	
	// 팝업 닫기
	$('#closeBtn').unbind("click").bind("click", function(){
		console.log("close click");
		$('.bg').fadeOut();
		$('#additionalJob').fadeOut();
    });
});
/*********************************
Name   : searchEnt
Desc   : 검색칸에 키보드입력 감지
Param  : 입력
**********************************/
var searchEnt = function(e){
	var key = e.keyCode;
	console.log("key : " + key);
	console.log("key : " + typeof key);
	if(key == 13){
		fn_DeptUser.search();
	}
}
/*********************************
Name   : initDeptBtn
Desc   : 부서 선택시 사용자 정보 항목의 버튼을 숨김
Param  : 없음
**********************************/
var initDeptBtn = function(){
	$("button[id$=DeptUser]").hide();
  	$("#btnResetPWD").hide();
}
/*********************************
Name   : Origin
Desc   : 원직 추가 및 겸직 삭제, 모달창 열고 닫기, 추가시에 알럿창띄우기 
Param  : 없음
**********************************/
var Origin = {
	show : function(){
		var $layerPopupObj = $('#docmodal');
		var left = ($(window).scrollLeft() + ($(window).width() - $layerPopupObj.width()) / 2);
		var top = ($(window).scrollTop() + ($(window).height() - $layerPopupObj.height()) / 2);
		//console.log("left : "+left+", top : "+top);
		$layerPopupObj.css({
			'left': left,
			'top': top,
			'position': 'absolute'
		});
			
		$("#NodeMoveArea").show();
		$("#docmodal").draggable({
			containment: 'body',
			scroll: false
		});
		$("#docmodal").css('z-index', modalZIndex + 10);
		modalZIndex = modalZIndex + 10; 
			    
		initDeptTree();
		initDeptInfo();
	},
	add: function(){
		if(rootTree.popupId && rootTree.popupText){
			if(rootTree.popupId == rootTree.selectId){
				alert("<spring:eval expression="@${msgLang}['CANNOT_SAME_DEPT']"/>");
				return;
			}
				
		}
		if(objectIsEmpty(rootTree.popupId )){
			alert("<spring:eval expression="@${msgLang}['SELECT_DEPT_REGISTER']" />");
		}else{
			var message = "<spring:eval expression="@${msgLang}['WOULD_YOU_REGISTER_POSITION']"/>"; //rootTree.popupText+"에 겸직 등록을 하시겠습니까?";
			Origin.confirm(message,addOrigin)
		}
	},
	remove:function(){
		var message = "<spring:eval expression="@${msgLang}['ARE_YOU_DELETE_POSITIONS']"/>";//"겸직을 삭제 하시겠습니까?";
		Origin.confirm(message,removeOrigin)
	},
	close:function(){
		rootTree.popupId="";
		rootTree.popupText=""; 
		$('.bg').fadeOut();
       $('#additionalJob').fadeOut();
	},
	confirm:function(message,fun){
		noty({
			layout:"center",
			text : message,
			buttons : [ {
				addClass : 'b_btn',
				text : "Ok",
				onClick : function($noty) {
					$noty.close();
		        
					fun();
				}
			},
			{
				addClass : 'btn-danger',
				text : "Cancel",
				onClick : function($noty) {
					$noty.close();
				}
			}],
			type : "information",
			killer:true
		});
	}
		
}

var getChildrenValue = function(obj,key){
	return  obj.children(key).attr("value");
}

/*********************************
Name   : fn_Dept
Desc   : 부서 생성, 수정, 삭제, 폐기, 복구 기능
Param  : 없음
**********************************/
var fn_Dept ={
	save : function(){
		var bEmpty= true;
		var sendData={};
		if($("#DeptCode").is(":disabled")){
			alert("<spring:eval expression="@${msgLang}['PRESS_INITIALZATION_REGISTER_BTN']"/>");	
			return;
		}
		if(rootTree.selectId == ''){
			alert("<spring:eval expression="@${msgLang}['SELECT_DEPART']"/>")
			return;
		}
		sendData.confirmMessage = "<spring:eval expression="@${msgLang}['DO_YO_REGISTER_DEPT']"/>"
		inputObjs = $("#deptinput input");
				
		sendData.data = {
			"companyid" : companyid,
			"name" : $.trim($("#DeptName").val()),
			"code" : $.trim($("#DeptCode").val()),
			"abbrname" : $.trim($("#DeptAbbrName").val()),
			"upid" : rootTree.selectId			
		}
		sendData.url = "api/organ/dept/add";
	  	//console.log("===inputObjs===",inputObjs);
	  	inputObjs.each(function(index) {
	  		if ($(this).val() == '') {
	  		focus = $(this);
	  		bEmpty = false;
	  		var message = $(this).attr('title');
	  		message+="\n";
	  		message+="<spring:eval expression="@${msgLang}['INVALID_INFO']"/>";
	  		alert(message);
	  		$(this).focus();
	  		return false;
	  		}
	  	});
	  	
	  	if (!bEmpty) return;
	  	
	  	sendData.data.objIsTest="N";
	  	sendData.data.objDebugged=false;
	  	
	    fn_Confirm(sendData);
	},
	modify : function(){
		var bEmpty= true;
		var sendData={};
		sendData.confirmMessage = "<spring:eval expression="@${msgLang}['DO_YOU_MODIFY_DEPT']"/>"
		inputObjs = $("#deptinput input");
		if(rootTree.selectId == ''){
			alert("<spring:eval expression="@${msgLang}['SELECT_DEPART']"/>")
			return;
		}
		sendData.data = {
			"abbrname" : $.trim($("#DeptAbbrName").val()),
			"deptid":rootTree.selectId
		}
		sendData.url = "api/organ/dept/change";
		//console.log("===inputObjs===",inputObjs);
		inputObjs.each(function(index) {
			if ($(this).val() == '') {
				focus = $(this);
				bEmpty = false;
				var message = $(this).attr('title');
				message+="\n";
				message+="<spring:eval expression="@${msgLang}['INVALID_INFO']"/>";
				alert(message);
				$(this).focus();
				return false;
			}
		});
	    	
		if (!bEmpty) return;
	    	
		sendData.data.objIsTest="N";
		sendData.data.objDebugged=false;
	    	
		fn_Confirm(sendData);
	},
	disable : function(){
		var sendData={};
		sendData.type="disable";
		sendData.confirmMessage = "<spring:eval expression="@${msgLang}['ARE_YOU_DELETE_DEPART']"/>"
		inputObjs = $("#deptinput input");
		if(rootTree.selectId == ''){
			alert("<spring:eval expression="@${msgLang}['SELECT_DEPART']"/>")
			return;
		}
		sendData.data = {
			"objIncLower" : "Y",
			"deptid":rootTree.selectId
		}
		sendData.url = "api/organ/dept/disable";
		sendData.data.objIsTest="N";
		sendData.data.objDebugged=false;
	    	
		fn_Confirm(sendData);
	},
	enable : function(){
		var sendData={};
		sendData.type="enable";
		sendData.confirmMessage = "<spring:eval expression="@${msgLang}['ARE_YOU_RESTORE_DEPART']"/>"
		inputObjs = $("#deptinput input");
		if(rootTree.selectId == ''){
			alert("<spring:eval expression="@${msgLang}['SELECT_DEPART']"/>")
			return;
		}
		sendData.data = {
			"objIncLower" : "Y",
			"deptid":rootTree.selectId
		}
		sendData.url = "api/organ/dept/enable";
		sendData.data.objIsTest="N";
		sendData.data.objDebugged=false;
	    	
		fn_Confirm(sendData);
	},
	discard : function(){
		var sendData={};
		sendData.confirmMessage = "<spring:eval expression="@${msgLang}['ARE_YOU_DEPT_DISCARD']"/>";
		inputObjs = $("#deptinput input");
		if(rootTree.selectId == ''){
			alert("<spring:eval expression="@${msgLang}['SELECT_DEPART']"/>")
			return;
		}
		sendData.data = {
			"deptid": rootTree.selectId
		}
		sendData.url = "api/organ/dept/discard";
		//console.log("===inputObjs===",inputObjs);
		inputObjs.each(function(index) {
			if ($(this).val() == '') {
				focus = $(this);
				bEmpty = false;
				var message = $(this).attr('title');
				message+="\n";
				message+="<spring:eval expression="@${msgLang}['INVALID_INFO']"/>";
				alert(message);
				$(this).focus();
				return false;
			}
		});
	    	
		sendData.data.objIsTest="N";
		sendData.data.objDebugged=false;
	    	
		fn_Confirm(sendData);
	}
}
/*********************************
Name : fn_DeptUser
Desc : 부서원 관리
	 	init - 버튼 초기화
	 	save - 부서원 저장
	 	modify - 부서원 수정
	 	disable - 부서원 삭제(state - 사용안함)
	 	enable - 부서원 사용(state - 사용)
	 	discard - 부서원 폐기(삭제)
	 	search - 부서원 검색(검색값이 100% 일치)
	 	reset - 비밀번호 초기화 (ID!@#)
Param  : 없음
**********************************/
var fn_DeptUser ={
	init : function(){
		$("a[id$=DeptUser]").hide();
		$("#btnResetPWD").hide();
	},
	save : function(){
		if(rootTree.selectId == ''){
			alert("<spring:eval expression="@${msgLang}['SELECT_DEPT_REGISTER']"/>");
			return;
		}
		var bEmpty = true;
		var sendData={};
		var inputObjs = $("#deptuserinput input");
		var originyn =( $("#ConcurrentOffice").text()== "<spring:eval expression="@${lang}['ORIGINAL_JOB']"/>"? "Y":"N");
		sendData.confirmMessage = "<spring:eval expression="@${msgLang}['DO_TO_REGISTER_USERS']"/>"
		sendData.data ={
			"objIsTest" : "N",
			"companyid":companyid,
			"empno":$.trim($("#UserEmpno").val()),
			"loginid":$.trim($("#UserID").val()),
			"name":$.trim($("#UserName").val()),
			"passwd":$.trim($("#UserID").val())+"!@#",
			"email":$.trim($("#UserEmail").val())
		};
		sendData.data.zappDeptUser = {
			"deptid":rootTree.selectId,
			"usertype": $("#UserDeptType option:selected").val(),
			"positionid": $("#UserDeptPosition option:selected").val(),
			"dutyid": $("#UserDeptDutyId option:selected").val(),
			"seclevelid": $("#UserDeptSeclevelId option:selected").val(),
			"isactive" : "Y",
			"originyn": originyn,
			"issupervisor" : $("#UserDeptSupervisor option:selected").val()
		}
		sendData.url = "api/organ/users/add";
				
		//console.log("===inputObjs===",inputObjs);
		inputObjs.each(function(index) {
			if ($(this).val() == '') {
				focus = $(this);
				bEmpty = false;
				var message = $(this).attr('title');
				message+="\n";
				message+="<spring:eval expression="@${msgLang}['INVALID_INFO']"/>";
				alert(message);
				$(this).focus();
				return false;
			}
		});
    	
		if (!bEmpty) return;
    	
		sendData.data.objIsTest="N";
		sendData.data.objDebugged=false;
    	
		fn_Confirm(sendData);
	},
	modify : function(){
		console.log("modify 시작");
		var sendData ={};
		var bEmpty = true;
		var inputObjs = $("#deptuserinput input");
		sendData.data = {
			"empno":$.trim($("#UserEmpno").val()),
			"loginid":$.trim($("#UserID").val()),
			"name":$.trim($("#UserName").val()),
			"email":$.trim($("#UserEmail").val())
					
		}
		var deptuserid = $("#UserEmpno").attr("deptuserid");
		var name = $("#UserName").val();
		if(deptuserid == ''){
			alert("<spring:eval expression="@${msgLang}['SELECT_USER']"/>")
			return;
		}
		sendData.confirmMessage = "<spring:eval expression="@${msgLang}['DO_YOU_MODIFY_USER']"/>";//"사용자 "+name+"을(를) 수정 하시겠습니까?"
		sendData.data = {
			"userid":	$("#UserID").attr("userid"),
			"name":$.trim($("#UserName").val()),
			"email":$("#UserEmail").val()
		};
		sendData.data.zappDeptUser = {
			"deptuserid":deptuserid,
			"usertype": $("#UserDeptType option:selected").val(),
			"positionid": $("#UserDeptPosition option:selected").val(),
			"dutyid": $("#UserDeptDutyId option:selected").val(),
			"seclevelid": $("#UserDeptSeclevelId option:selected").val(),
			"issupervisor": $("#UserDeptSupervisor option:selected").val()
		};
		sendData.data.isactive = $("#UserState option:selected").val();
		sendData.data.objIsTest="N";
		sendData.data.objDebugged=false;
			
		//console.log("===inputObjs===",inputObjs);
		inputObjs.each(function(index) {
			if ($(this).val() == '') {
				focus = $(this);
				bEmpty = false;
				var message = $(this).attr('title');
				message+="\n";
				message+="<spring:eval expression="@${msgLang}['INVALID_INFO']"/>";
				alert(message);
				$(this).focus();
				return false;
			}
    	});
    	
	if (!bEmpty) return;
    	
		sendData.url = "api/organ/users/change";
		fn_Confirm(sendData);
	},
	disable : function(){
		var sendData ={};
		var deptuserid = $("#UserEmpno").attr("deptuserid");
		var name = $("#UserName").val();
		if(deptuserid == ''){
			alert("<spring:eval expression="@${msgLang}['SELECT_USER']"/>")
			return;
		}
		sendData.confirmMessage ="<spring:eval expression="@${msgLang}['ARE_YOU_DELETE_USER']"/>";// "사용자 "+name+"을(를) 삭제 하시겠습니까?"
		sendData.data = {
			"userid":	$("#UserID").attr("userid")
			/* 				
			,"zappDeptUser":{
			deptuserid" : deptuserid 					
			}*/
		}
		sendData.data.objIsTest="N";
		sendData.data.objDebugged=false;
    	
		sendData.url = "api/organ/users/disable";
		fn_Confirm(sendData);
  
	},
    enable : function(){
      
    },
	discard : function(){
		var sendData ={};
		var deptuserid = $("#UserEmpno").attr("deptuserid");
		sendData.confirmMessage = "<spring:eval expression="@${msgLang}['ARE_YOU_DELETE_USER']"/>";
		sendData.data = {
			"userid":	$("#UserID").attr("userid")
			/*,"zappDeptUser":{
			"deptuserid" : deptuserid 					
			} */
		}
		sendData.data.objIsTest="N";
		sendData.data.objDebugged=false;
		sendData.url = "api/organ/users/discard";
		
		fn_Confirm(sendData);

    },
	search : function(){
		var sendData ={};
		if(objectIsEmpty(rootTree.selectId)){
			if(${Authentication.sessOnlyDeptUser.usertype == '03'}){
				rootTree.selectId = companyid;
			}else{
				rootTree.selectId = deptid; 
			}
  			
		}
		//console.log("testtesttesttesttesttest",rootTree.selectId)
		var inputObjs = $("#searchData input"); 
		var key = $("#searchTarget option:selected").val();
		var title = $("#searchTarget option:selected").text();
		var value =   $.trim($("#S_data").val());
		sendData.notFoundMessage = "<spring:eval expression="@${msgLang}['NO_MATCHING_TARGET']"/>";
		sendData.data = {
			[key.toLowerCase()] :  value
		}
		sendData.data.objIsTest="N";
		sendData.data.objDebugged=false;
  		
		//console.log("===inputObjs===",inputObjs);
		inputObjs.each(function(index) {
			if ($(this).val() == '') {
				focus = $(this);
				bEmpty = false;
				var message = $(this).attr('title');
				message+="\n";
				message+="<spring:eval expression="@${msgLang}['INVALID_INFO']"/>";
				alert(message);
				$(this).focus();
				return false;
			}
		});
  	
		//console.log("===sendData===",sendData);
		$.ajax({
			type : 'POST',
			url : "${ctxRoot}/api/organ/user/list",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData.data),
			success : function(data){
			//console.log("search data : ",data);
				if(data.status == "0000"){
					//$("input[name=searchData]").val('');
					$("#SearchAreaUserData").empty();
					if(objectIsEmpty(data.result)){
						alert(sendData.notFoundMessage);
						selectDeptUserList();
						return;
					}else{
						getDeptUserInfo(data.result[0].userid);
					}
				}else{
					selectDeptUserList();
				}
			},   
			error : function(request, status, error) {
				alertNoty(request,status,error);
			}					
		});
  	
	},
	reset : function(){
		var sendData ={};
		sendData.confirmMessage = "<spring:eval expression="@${msgLang}['DO_YOU_WANT_RESET_PASSWORD']"/>";
		sendData.data = {
			"userid" :  $.trim($("#UserID").attr("userid")),
			"passwd" : $.trim($("#UserID").val()) +"!@#",
			"companyid":companyid,
			"empno":$.trim($("#UserEmpno").val()),
			"loginid":$.trim($("#UserID").val()),
			"name":$.trim($("#UserName").val()),
			"email":$.trim($("#UserEmail").val())
		}
		sendData.url = "api/organ/user/change";
		fn_Confirm(sendData);
		
	}
}
	
/*********************************
Name   : fn_Confirm
Desc   : 작업 수행시 확인 알럿창을 띄워준다
Param  : sendData(url, confirmMessage,))
**********************************/
var fn_Confirm = function(sendData){
	//console.log("===confirm sendData===",sendData);
	noty({
		layout:"center",
		text : sendData.confirmMessage,
		buttons : [ {
			addClass : 'b_btn',
			text : "Ok",
			onClick : function($noty) {
				$noty.close();
				$.ajax({
					type : 'POST',
					url : "${ctxRoot}/"+sendData.url,
					dataType : 'json',
					contentType : 'application/json',
					async : false,
					data : JSON.stringify(sendData.data),
					success : function(data){
						//console.log("data : ",data);
						if(data.status == "0000"){
						alert("<spring:eval expression="@${msgLang}['OPERATION_IS_COMPLETED']" />");
						//console.log(sendData.url.indexOf("reorder"));
						//console.log("sendData.url:"+sendData.url);
						if(sendData.url.indexOf("reorder") > 0){
							//console.log("순서 변경");
							//initTree();
							initTree(rootTree.selectId);
						}else if(sendData.url == "api/organ/dept/add" || sendData.url == "api/organ/dept/change" || sendData.url == "api/organ/dept/disable"){//부서 수정, 삭제
							initTree(rootTree.selectId);
						}else if(sendData.url == "api/organ/dept/discard"){//부서 추가,폐기
							var parentID = $("#znTree").jstree().get_node(rootTree.selectId).parent;
							initTree(parentID);
						}else if(sendData.url == "api/organ/users/add" || sendData.url == "api/organ/users/change" || sendData.url == "api/organ/users/disable"){//사용자 수정, 삭제 
							selectDeptUserList();	
						}else if(sendData.url == "api/organ/users/discard"){//사용자 폐기
							selectDeptUserList();	
							initDeptUser();
						}else{
							initTree();
							initDeptUser();
							selectDeptUserList();	
	  						}	
						}else{
							alertErr(data.message);
						}
					},  
					error : function(request, status, error) {
					alertNoty(request,status,error);
					}					
				});
			}
		},
	    {
			addClass : 'btn-danger',
			text : "Cancel",
			onClick : function($noty) {
				$noty.close();
	      	}
		}],
    	type : "information",
    	killer:true
	});
}

/*********************************
Name   : removeOrigin
Desc   : 겸직 추가
Param  : 없음
**********************************/
var addOrigin = function(){
	var sendData ={};
	
		sendData.alertMessage = "<spring:eval expression="@${msgLang}['OPERATION_IS_COMPLETED']" />";
		inputObjs = $("#deptuserinput input");
		
		sendData.data ={
						"deptid":rootTree.popupId,
						"userid":	$("#UserID").attr("userid"),
						"usertype": $("#PopupUserDeptType option:selected").val(),
						"originyn": "N",
						"positionid": $("#PopupUserDeptPosition option:selected").val(),
						"dutyid": $("#PopupUserDeptDutyId option:selected").val(),
						"seclevelid": $("#PopupUserDeptSeclevelId option:selected").val(),
						"issupervisor": $("#PopupUserDeptSupervisor option:selected").val()
		}

		sendData.data.objIsTest="N";
		sendData.data.objDebugged=false;
		sendData.url = "api/organ/deptuser/add";
		//console.log("===sendData===",sendData);
	 	$.ajax({
			type : 'POST',
			url : "${ctxRoot}/"+sendData.url,
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData.data),
			success : function(data){
				//console.log("data : "+JSON.stringify(data));
				if(data.status == "0000"){
					
						alert(sendData.alertMessage);

						Origin.close();
						
						selectDeptUserList();
					
					//초기화 진행
				}else{
					alert(data.status);
				}
			},   error : function(request, status, error) {
        alertNoty(request,status,error);
		    }					
		});
}

/*********************************
Name   : removeOrigin
Desc   : 겸직 제거
Param  : 없음
**********************************/
var removeOrigin = function(){
		var sendData ={};
		
		var deptuserid = $("#UserEmpno").attr("deptuserid");
		var name = $("#UserName").val();
		if(deptuserid == ''){
		  alert("<spring:eval expression="@${msgLang}['NO_USERS']" />");	
			return;
		}
	
		sendData.data = {
			"deptuserid":deptuserid
		}
		
		sendData.data.objIsTest="N";
		sendData.data.objDebugged=false;
		sendData.url = "api/organ/deptuser/disable";
		//console.log("===sendData===",sendData);
	 	$.ajax({
			type : 'POST',
			url : "${ctxRoot}/"+sendData.url,
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData.data),
			success : function(data){
				//console.log("data : "+JSON.stringify(data));
				if(data.status == "0000"){
					
						alert("<spring:eval expression="@${msgLang}['OPERATION_IS_COMPLETED']" />");	
						initDeptUser();
						selectDeptUserList();
					
					//초기화 진행
				}else{
					alert(data.status);
				}
			},  
			error : function(request, status, error) {
        		alertNoty(request,status,error);
		    }					
		});
}

var treeRoot = function() {
	if(${Authentication.sessOnlyDeptUser.usertype == '03'}){
		rootTree.id = companyid; //
	}else{
		rootTree.id = deptid; 
	}
	rootTree.text = companyName;
	//console.log("===rootTree.text : "+rootTree.text);
}

/*********************************
Name   : initTree
Desc   : 트리 초기화
Param  : streeId
**********************************/
var initTree = function(streeId){

	$('#znTree').remove();
	var treeHtml = "<ul id='znTree'></ul>";
	$('.contNav').append(treeHtml);
	
	$('#znTree').jstree({
		'plugins': ["state"],
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
		
		rootTree.selectId = selfid;
		rootTree.selectText = selfname;
		rootTree.selectCode = item_code;
		$('#searchTarget option:eq(0)').prop("selected", "selected");
		$("#DeptCode").attr("disabled",true).val(rootTree.selectCode);
		$("#btnNewDept").attr("name","modify");
		$("#DeptName").attr("disabled",true).val(selfname);
		$("#DeptAbbrName").val(item_abbrname);
		$("#UserDeptName").text(selfname);
		$("#S_data").val("");

		if (selfid == companyid){
	 		$("#SearchAreaUserData").html("");
	 		initDeptUser();
	 		
	 		$("#btnDelDept").hide();
	 		$("#btnDisDept").hide();
	 		
  			$("a[id^=btn][id$=User]").hide();
  			
		} else {
    		
    		//사용자정보 입력창을 초기화시킨다.
    		initDeptUser();
    		//부서 사용자 정보를 받아 온다
    		selectDeptUserList();
    		//사용 상태에 따라서 버튼 숨김처리
    		if(item_isactive == "N"){ 
    	 		$("a[id^=btn][id$=Dept]").hide();
    	  		$("a[id^=btn][id$=User]").hide();
    		}else{
    			$("a[id^=btnInit]").show();
    			$("a[id^=btnNew]").show();
    		}
    		setDeptBtnState(true,true ,!(item_isactive == "Y"));
		}

	 	var parentNode = $("#znTree").jstree("get_node", parentid);
	    var firstNode = parentNode.children[0];
	    var lastNode = parentNode.children[parentNode.children.length - 1];
		$("img[id=reorder]").hide();
	    if (selfid !== firstNode) {
	    	$("img[id=reorder][name=up]").show();
	    }
	    if (selfid !== lastNode) {
	    	$("img[id=reorder][name=down]").show();
	    }
	});
		
	$('#znTree').on('ready.jstree', function () {
		selectDeptList(); 
		if(streeId != undefined){
        	$("#znTree").jstree("select_node", streeId);
        	$("#znTree").jstree("toggle_node", streeId);
        	$("#znTree").jstree("open_node", streeId);			
		}		
	});
};

/*********************************
Name   : initDeptTree
Desc   : 트리 초기화
Param  : 없음
**********************************/
var initDeptTree = function(){

	$('znTreeDept li').remove();
	
	$('#znTreeDept').jstree({
		'plugins': ["state"],
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
	});
	
	//트리 선택 이벤트
	$('#znTreeDept').bind('select_node.jstree', function(event, data){
		//console.log("===znTreeDept select_node : ",data.node);
		var parentid = data.instance.get_node(data.selected).parent;				//부모아이디
		var parentname = data.instance.get_node(parentid).text;						//부모 명
		var selfid = data.instance.get_node(data.selected).id;
		var selfname = data.instance.get_node(data.selected).text;
		
		rootTree.popupId=selfid;
		rootTree.popupText=selfname; 
	});
	
	
	$('#znTreeDept').on('ready.jstree', function () {
		for(var i=0; i<rootTree.deptList.length; i++) {
			tree_parent_code = rootTree.deptList[i].upid;
			treeCode = rootTree.deptList[i].code;
			treeId = rootTree.deptList[i].deptid;//
			treeText = rootTree.deptList[i].name;
			treeAbbrName = rootTree.deptList[i].abbrname;
			isactive  = rootTree.deptList[i].isactive;
			order = rootTree.deptList[i].priority;
			
			createNode("#znTreeDept","#"+tree_parent_code, treeId, treeText, "last", "${image}/jstree/yellow/ic_department_04.png", treeCode, treeAbbrName, isactive, rootTree.id, order);
		}
	});
};

/*********************************
Name   : initDeptInfo
Desc   : 겸직추가 버튼 클릭시 설정
Param  : 없음
**********************************/
var initDeptInfo = function(){
	
	$("label[id='PopupUserEmpno']").text($("#UserEmpno").val());
	$("label[id='PopupUserID']").text($("#UserID").val());
	$("label[id='PopupUserName']").text($("#UserName").val());
	
	var optionId = ["position","dutyid","seclevelid"];
	renderCodeOption(codeListInfo.positions,"#PopupUserDeptPosition",optionId[0]);
	renderCodeOption(codeListInfo.dutyid,"#PopupUserDeptDutyId",optionId[1]);
	renderCodeOption(codeListInfo.seclevelid,"#PopupUserDeptSeclevelId",optionId[2]);
	 
	$('#PopupUserDeptType').find('option:first').attr('selected', 'selected');
	$('#PopupUserDeptPosition').find('option:first').attr('selected', 'selected');
	$('#PopupUserDeptDutyId').find('option:first').attr('selected', 'selected');
	$('#PopupUserDeptSeclevelId').find('option:first').attr('selected', 'selected');
	$('#PopupUserDeptSupervisor').val("N");

}

/*********************************
name : renderCodeOption 
desc : 직위, 직무, 보안등급 옵션을 추가해 준다
param : data(list), id(option이 들어갈 부모 id), optionid(선택값을 가져오기 위한 id)
**********************************/
var renderCodeOption = function(data, id, optionid){
	$(id).children('option').remove();
	var option = "";
	$.each(data, function(idx,result){ 	 
		var codeid = result.codeid;
		var name = result.name;
		option += "<option id='"+optionid+"' value='"+codeid+"'>"+name+"</option>";
	});
	$(id).append(option);
}

/*********************************
Name   : initPosition
Desc   : 시스템코드
Param  : 2: 직위, 3: 직무, 4:보안등급
**********************************/
var initPosition = function(){
	$.each(CODELIST, function (index, item) {
		loadSystemCodeList(item);
	});
}

/*********************************
Name   : loadSystemCodeList
Desc   : 코드 정보 목록을 조회
Param  : 2: 직위, 3: 직무, 4:보안등급
**********************************/
var loadSystemCodeList = function(type){
	$.ajax({
		type : 'POST',
		url : "${ctxRoot}/api/system/code/list",
		dataType : 'json',
		contentType : 'application/json',
		async : false,
		data : JSON.stringify({
			"companyid" : companyid,
			"types" : type,
			"isactive" : "Y"
		}),
		success : function(data){
			if(data.status == "0000"){
				renderCodeList(type, data);	
			}else{
				alert(data.status);
			}
		},   error : function(request, status, error) {
       alertNoty(request,status,error);
	    }					
	});
}

function custonSort(a, b) { if(a.priority == b.priority){ return 0} return a.priority > b.priority ? 1 : -1; }

/*********************************
Name   : renderCodeList
Desc   : 코드 정보 목록을 받아서 화면에 뿌려준다.
Param  : type(2: 직위, 3: 직무, 4:보안등급), data(코드 목록 리스트)
**********************************/
var renderCodeList = function(type, data){
	var optionId = ["position","dutyid","seclevelid"];
	
	var pos = 0;
	if(typeof type == "string"){
		pos = parseInt(type)-2;
	}else if(typeof type == "number"){
		pos = type - 2;
	}
	var option = "";
	$.each(data.result, function(idx,result){ 	 
		var codeid = result.codeid;
		var name = result.name;
		option += "<option id='"+optionId[pos]+"' value='"+codeid+"'>"+name+"</option>";
	});

	if(type == CODELIST.position){
		codeListInfo.positions =data.result;
		$("#UserDeptPosition").append(option);
	}else if(type == CODELIST.dutyid){
		codeListInfo.dutyid = data.result;
		$("#UserDeptDutyId").append(option);
	}else if(type == CODELIST.seclevelid){
		codeListInfo.seclevelid = data.result;
		$("#UserDeptSeclevelId").append(option);
	}
}

/*********************************
Name   : selectDeptList
Desc   : 트리를 그려준다
Param  : 없음
**********************************/
var selectDeptList = function() {
	var usrUpid = deptid;
	if(userType== "03"){
		usrUpid = companyid;
	}
		
	// 조회
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
				if(objectIsEmpty(data.result)){
					return;
				}
				rootTree.deptList = data.result;
				$.each(data.result,function(index,item){
					tree_parent_code = item.upid;
					treeCode = item.code;
					treeId = item.deptid;//
					treeText = item.name;
					treeAbbrName = item.abbrname;
					isactive  = item.isactive;
					order = item.priority;
					createNode("#znTree","#"+tree_parent_code, treeId, treeText, "last", (isactive =='Y')?ADMINTREEICONS.DEPT:ADMINTREEICONS.DISABLE, treeCode, treeAbbrName, isactive, rootTree.id, order);
				});
			}
		},   error : function(request, status, error) {
		alertNoty(request,status,error);
		}
	})
};

/*********************************
Name   : createNode
Desc   : 트리추가
Param  : tree_id, parent_code, new_id, new_text, position, icon, item_code, item_name, itemisactive, f_appid, priority
**********************************/
var createNode = function(tree_id, parent_code, new_id, new_text, position, icon, item_code, item_name, itemisactive, f_appid, priority){
	var rtnVal = $(tree_id).jstree (
		'create_node'
		,parent_code
		,{"text" :new_text
		,"id"    :new_id
		,"icon"  : icon
		,"state"  : {"opened": true }
		,"a_attr" : {"itemcode":item_code
					,"itemname":item_name
					,"itemisactive":itemisactive
					,"appid":f_appid
					,"priority":priority
					} 
		}
		,position
		,false
		,false
	);
};

/*********************************
Name   : initDept
Desc   : 부서 입력창 초기화
Param  : 없음
**********************************/
var initDept = function(){
	inputObjs = $("#deptinput input");
	inputObjs.each(function(index) {
		$(this).attr("disabled",false).val('');
	});
	$("#btnNewDept").attr("name","create").show();
	initDeptBtnState();
}

/*********************************
Name   : initDeptUser
Desc   : 부서사용자 입력창 초기화
Param  : 없음
**********************************/
var initDeptUser = function(){
	inputObjs = $("#deptuserinput input");

	inputObjs.each(function(index) {
			$(this).val('').attr("disabled",false);
	});
	$('#UserDeptType').val('01');
	$('#UserDeptPosition option:eq(0)').prop("selected", "selected");
	$('#UserDeptDutyId option:eq(0)').prop("selected", "selected");
	$('#UserDeptSeclevelId option:eq(0)').prop("selected", "selected");
	$('#UserDeptSupervisor').val("N");
	$('#ConcurrentOffice').text("<spring:eval expression="@${lang}['ORIGINAL_JOB']"/>").val("Y");
	$("#btnOriginAdd").hide();
	$("#btnOriginDel").hide();
	$("#btnResetPWD").hide();
	$('#UserState').val("Y");
	$("#UserDeptName").text(rootTree.selectText);
 	
	initDeptUserBtnState();
	$("#btnNewDeptUser").attr("name","create").show();
}

/*********************************
Name   : selectDeptUserList
Desc   : 선택된 부서의 사용자 목록을 불러온다
Param  : 없음
**********************************/
var selectDeptUserList = function() {
	var sendData = {deptid : rootTree.selectId,objIsMngMode : true}

	// 조회
	$.ajax({
		url :"${ctxRoot}/api/organ/deptuser/list"
		, type : "POST"
		, dataType : "json"
		, contentType : 'application/json'
		, async : false
		, data : JSON.stringify(sendData)
		, success : function(data){
			if(data.status == "0000") {
				if(objectIsEmpty(data.result)){
				 	$("#SearchAreaUserData").empty();
					return;
				}
				renderSearchItem(data);
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
Name   : getDeptUserInfo
Desc   : 선택된 값으로 사용자 정보를 조회한다
Param  : item = userid
**********************************/
var getDeptUserInfo = function(item) {
	// 조회
	$.ajax({
		url :"${ctxRoot}/api/organ/deptuser/get"
		, type : "POST"
		, dataType : "json"
		, contentType : 'application/json'
		, async : false
		, data : JSON.stringify({
			"userid" : item
		})
		, success : function(data){
			 if(data.status == "0000") {
				 if(objectIsEmpty(data.result)){
				   $("#SearchAreaUserData").empty();
					 return;
				 }
				 renderSearchItem(data);
				}else{
					alertErr(data.message);
			} 
		},   
		error : function(request, status, error) {
      		alertNoty(request,status,error);
		}
	})
};

/*********************************
Name   : renderSearchItem
Desc   : 부서 사용자목록에 사용자 정보를 추가해준다
Param  : data
**********************************/
var renderSearchItem = function(data){
	$("#SearchAreaUserData").empty();
	
	var SearchAreaUserData_dataHtml ="";
	for(var i=0; i<data.result.length; i++) {
		var meta ={};
		
		meta.deptuserid = data.result[i].deptuserid;
		meta.deptname = data.result[i].zappDept.name;
		meta.userid = data.result[i].zappUser.userid;
		meta.isactive = data.result[i].zappUser.isactive;
		meta.usertype = data.result[i].usertype;
		meta.originyn = data.result[i].originyn;
		meta.positionid = data.result[i].positionid;
		meta.dutyid = data.result[i].dutyid;
		meta.seclevelid = data.result[i].seclevelid;
		meta.issupervisor = data.result[i].issupervisor;
		meta.empno = data.result[i].zappUser.empno;
		meta.loginid = data.result[i].zappUser.loginid;
		meta.name = data.result[i].zappUser.name;
		meta.email = data.result[i].zappUser.email;
		
		var Num = i+1
		var isActive = (meta.isactive == 'N')?'color:red':'';			
		SearchAreaUserData_dataHtml += "<tr id='searchItem' data-meta='"+JSON.stringify(meta)+"' class='num"+Num+"' style='"+isActive+"'>";
		SearchAreaUserData_dataHtml +="<td id='empno' style='"+isActive+"'>" + meta.empno + "</td>";
		SearchAreaUserData_dataHtml +="<td id='loginid' style='"+isActive+"'>"+meta.loginid+"</td>";
		SearchAreaUserData_dataHtml +="<td id='name' style='"+isActive+"'>" + meta.name + " [" + meta.deptname + "]</td>";
		SearchAreaUserData_dataHtml +="<td style='display:none' id='email'>"+meta.email+"</td>";
		SearchAreaUserData_dataHtml +="</tr>"
				
	}
	$("#SearchAreaUserData").html(SearchAreaUserData_dataHtml);
}

/*********************************
Name   : setDeptBtnState
Desc   : 부서사용자 상태에 따라서 보여지는 버튼이 달라짐
Param  : Mod,Del,Dis
**********************************/
var setDeptUserBtnState = function(Mod,Del,Dis){

	if(Mod == true){
		$("#btnModDeptUser").show();
	}else{
		$("#btnModDeptUser").hide();
	}
	if(Del == true){
		$("#btnDelDeptUser").show();
	}else{
		$("#btnDelDeptUser").hide();
	}
	
	if(Dis == true){
		$("#btnDisDeptUser").show();
	}else{
		$("#btnDisDeptUser").hide();
	}
}

/*********************************
Name   : setDeptBtnState
Desc   : 부서 상태에 따라서 보여지는 버튼이 달라짐
Param  : Mod,Del,Dis
**********************************/
var setDeptBtnState = function(Mod,Del,Dis){
	if(Mod == true){
		$("#btnModDept").show();
	}else{
		$("#btnModDept").hide();
	}
	if((Dis == true)){
		$("#btnDelDept").hide();
	}else{
		if(Del == true){
			$("#btnDelDept").show();
		}else{
			$("#btnDelDept").hide();
		}
	}
	
	if(Dis == true){
		$("#btnResDept").show();
		$("#btnDisDept").show();
	}else{
		$("#btnResDept").hide();
		$("#btnDisDept").hide();
	}
}

/*********************************
Name   : initDeptBtnState
Desc   : 부서정보 버튼 초기 상태값
Param  : 없음
**********************************/
var initDeptBtnState = function(){
	setDeptBtnState(false,false,false);
	
}

/*********************************
Name   : initDeptUserBtnState
Desc   : 부서사용자정보 버튼 초기 상태값
Param  : 없음
**********************************/
var initDeptUserBtnState = function(){
	setDeptUserBtnState(false,false,false);
	
}

/*********************************
Name   : reorder
Desc   : 부서 순서 변경
Param  : priority(선택된 부서의 priority값)
**********************************/
var reorder = function(priority) {
    var sendData = {
      data : {
        objIsTest : "N",
        objDebugged : false,
      }
    };

    sendData.confirmMessage = "<spring:eval expression="@${msgLang}['DO_YOU_MOVE_DEPT_INFO']"/>";
    sendData.url = 'api/organ/dept/reorder';
    sendData.data.deptid = rootTree.selectId;
    sendData.data.priority = priority;
    fn_Confirm(sendData);
};
/*********************************
Name   : fn_openPop
Desc   : 팝업 띄우기
Param  : selector(띄울 팝업의 id값)
**********************************/
var fn_openPop = function(selector) {
	
	//console.log("selector : " + selector);
	
    //레이어팝업 중앙에 띄우기
    var $layerPopupObj = $('#'+selector);
    var left = ($(window).scrollLeft() + ($(window).width() - $layerPopupObj.width()) / 2);
    var top = ($(window).scrollTop() + ($(window).height() - $layerPopupObj.height()) / 5);
    $layerPopupObj.css({
        'left': left,
        'top': top,
        'position': 'absolute'
    });
    addModal();

    $('#'+selector).show();
	try{
    }catch(e){
    	console.log("e : "+e);
    }

    $('#'+selector).css('z-index', modalZIndex + 10);
    modalZIndex = modalZIndex + 100;
    

};
var addModal = function() {
    $('.sepage_data').append("<div class = 'modalLayer' style = 'position: fixed;z-index: " + modalZIndex + ";left: 0;top: 0;width: 100%;height: 100%;overflow: auto;background-color: rgb(0,0,0);background-color: rgba(0,0,0,0.4);'></div>");
}
</script>        
</head>
	<body>
		<!-- 기관 아이디 -->
		<input type="hidden" id="CompanyID" value="" />
		<input type="hidden" id="MobCompanyID" value="" />

		<!--header stard-->
		<c:import url="../common/TopPage.jsp" />
		<main>
			<div class="bg" style="display:none;"></div>
			<div class="flx">
				<c:import url="../common/AdminLeftMenu.jsp" />
				<section id="content">
					<div class="innerWrap innerWrap_scroll">
						<h2 class="pageTit_reorder">
							<img src="${image}/icon/Group 156.png" alt=""><spring:eval expression="@${lang}['DEPARTMENT_MANAGEMENT']" />
							<img src="${image}/icon/bt_up.png" title="<spring:eval expression="@${lang}['MOVE_DEPARTMENT_ORDER']"/>" style="display: none; cursor: pointer; margin-right: 15px; line-height: 26px; height: 38px;  padding: 5px;float: right" id="reorder" name="up" />
            				<img src="${image}/icon/bt_down.png" title="<spring:eval expression="@${lang}['MOVE_DEPARTMENT_ORDER']"/>" style="display: none; cursor: pointer; margin-right: 15px; line-height: 26px; height: 38px;  padding: 5px;float: right" id="reorder" name="down" />
						</h2>
						<div class="flex-content">
							<div class="contNav">
								<ul id="znTree"></ul>
							</div><!--contNav//-->
							<div class="rgt_area">
								<div class="wdt100">
									<h3 class="innerTit"><spring:eval expression="@${lang}['DEPARTMENT']" /></h3>
									<div class="btn_wrap">
										<button type="button" class="btbase" id="btnInitDept" data-key="dept"><spring:eval expression="@${lang}['INITIALIZATION']" /></button>
										<button type="button" class="btbase" id="btnNewDept" name="create" data-key="dept"><spring:eval expression="@${lang}['SAVE']" /></button>
										<button type="button" class="btbase" id="btnDelDept" data-key="dept" style="display: none;"><spring:eval expression="@${lang}['DELETE']" /></button>
										<button type="button" class="btbase" id="btnResDept" data-key="dept" style="display: none;"><spring:eval expression="@${lang}['RESTORE']" /></button> 
										<button type="button" class="btbase" id="btnDisDept" data-key="dept" style="display: none;"><spring:eval expression="@${lang}['DISCARD']" /></button>
									</div>
									<table class="inner_tbl">
										<colgroup>
											<col width="34%">
											<col width="33%">
											<col width="33%">
										</colgroup>
										<thead>
											<th><spring:eval expression="@${lang}['DEPARTMENT_CODE']" /></th>
											<th><spring:eval expression="@${lang}['DEPARTMENT_NAME']" /></th>
											<th><spring:eval expression="@${lang}['DEPARTMENT_ABBREVIATION']" /></th>
										</thead>
										<tbody>
											<tr id="deptinput">
												<td>
													<input type="text" value="" id="DeptCode" onkeyup='pubByteCheckTextarea(event,30)' style="width: 90%;" title="<spring:eval expression="@${lang}['DEPARTMENT_CODE']"/>">
												</td>
												<td>
													<input type="text" value="" id="DeptName" onkeyup='pubByteCheckTextarea(event,300)' style="width: 90%;" title="<spring:eval expression="@${lang}['DEPARTMENT_NAME']"/>">
												</td>
												<td>
													<input type="text" value="" id="DeptAbbrName" onkeyup='pubByteCheckTextarea(event,50)' style="width: 90%;" title="<spring:eval expression="@${lang}['DEPARTMENT_ABBREVIATION']"/>">
												</td>
											</tr>
										</tbody>
									</table>
								</div>
								<div class="wdt50">
									<h3 class="innerTit"><spring:eval expression="@${lang}['USER_BELONG_TO_DEPARTMENT']" /></h3>
									<div class="inner_uiGroup" style="position:relative;">
										<p><spring:eval expression="@${lang}['TARGET']" /></p>
										<select id="searchTarget">
											<option value="EMPNO"><spring:eval expression="@${lang}['EMPLOYEE_ID']" /></option>
											<option value="LOGINID"><spring:eval expression="@${lang}['ID']" /></option>
											<option value="NAME"><spring:eval expression="@${lang}['NAME']" /></option>
											<option value="EMAIL"><spring:eval expression="@${lang}['E-MAIL']" /></option>
										</select>
										<p><spring:eval expression="@${lang}['SEARCH']" /></p>
											<input type="text" name="searchData" id="S_data" onkeydown="searchEnt(event);"/>
											<input type="submit" id="search">
											<label><img src="${image}/icon/Group 57.png" class="b_btn" id="btnOrgnSearch" data-key="search"></label>
									</div>
									<table class="inner_tbl">
										<colgroup>
											<col width="34%">
											<col width="33%">
											<col width="33%">
										</colgroup>
										<thead>
											<th><spring:eval expression="@${lang}['EMPLOYEE_ID']" /></th>
											<th><spring:eval expression="@${lang}['ID']" /></th>
											<th><spring:eval expression="@${lang}['DEPARTMENT_ABBREVIATION']" /></th>
										</thead>
									</table>
									<div style="height:300px;overflow-y:scroll">
										<table class="inner_tbl" style="margin:auto">
											<colgroup>
												<col width="34%">
												<col width="33%">
												<col width="33%">
											</colgroup>
											<tbody id="SearchAreaUserData">
											</tbody>
										</table>
									</div>
								</div>

								<div class="wdt50" id="deptuserinput">
									<h3 class="innerTit"><spring:eval expression="@${lang}['USER_INFO']" /></h3>
									<div class="btn_wrap">
										<button type="button" class="btbase" id="btnInitUser" data-key="user"><spring:eval expression="@${lang}['INITIALIZATION']" /></button>
										<button type="button" class="btbase" id="btnNewDeptUser" name="create" data-key="user"><spring:eval expression="@${lang}['SAVE']" /></button>
										<button type="button" class="btbase" id="btnDelDeptUser" data-key="dept" style="display: none;"><spring:eval expression="@${lang}['DELETE']" /></button>
										<button type="button" class="btbase" id="btnDisDeptUser" data-key="dept" style="display: none;"><spring:eval expression="@${lang}['DISCARD']" /></button>
									</div>
                                    
									<div class="inner_uiGroup" >
										<P><spring:eval expression="@${lang}['DEPARTMENT_NAME']" /></P>
										<span id="UserDeptName"></span>
										<P><spring:eval expression="@${lang}['EMPLOYEE_ID']" /></P>
										<input type="text" id="UserEmpno">
										<P><spring:eval expression="@${lang}['ID']" /></P>
										<input type="text" id="UserID">
										<P><spring:eval expression="@${lang}['NAME']" /></P>
										<input type="text" id="UserName">
										<P><spring:eval expression="@${lang}['E-MAIL']" /></P>
										<input type="text" id="UserEmail">
										<P><spring:eval expression="@${lang}['USER_TYPE']" /></P>
										<select id="UserDeptType">
											<option value="01" selected="selected"><spring:eval expression="@${lang}['GENERAL_USER']" /></option>
											<option value="02"><spring:eval expression="@${lang}['DEPARTMENT_MANAGER']" /></option>
											<option value="03"><spring:eval expression="@${lang}['COMPANY_MANAGER']" /></option>
											<option value="04" disabled><spring:eval expression="@${lang}['SYSTEM_MANAGER']" /></option>
										</select>
										
										
										
										<P><spring:eval expression="@${lang}['ADDITIONAL_OR_NOT']" /></P>
										<!--  <label id="ConcurrentOffice" value="Y" style="margin-right: 10px;display:inline-block"><spring:eval expression="@${lang}['ORIGINAL_JOB']" /></label>
										<div class="btn_wrap" style="width:65%;">
											<button type="button" class="btbase" id="btnOriginDel" data-key="origin" style="display:none;"><spring:eval expression="@${lang}['DELETE_ADDITIONAL_JOB']" /></button>
											<button type="button" class="btbase" id="btnOriginAdd" data-key="origin" style="display:none;"><spring:eval expression="@${lang}['ADD_ADDITIONAL_JOB']" /></button>&nbsp; 								
										</div>
										-->
										<span>
											<p value="Y" id="ConcurrentOffice" style="width: 30px;"><spring:eval expression="@${lang}['ORIGINAL_JOB']" /></p>
											<div class="btn_wrap">
											<button type="button" class="btbase" id="btnOriginDel" data-key="origin" style="display:none;"><spring:eval expression="@${lang}['DELETE_ADDITIONAL_JOB']" /></button>
											<button type="button" class="btbase" id="btnOriginAdd" data-key="origin" style="display:none;"><spring:eval expression="@${lang}['ADD_ADDITIONAL_JOB']" /></button>&nbsp; 	
											</div>
										</span>
										
										<P><spring:eval expression="@${lang}['POSITION']" /></P>
										<select id="UserDeptPosition">
										</select>
										<P><spring:eval expression="@${lang}['DUTY']" /></P>
										<select id="UserDeptDutyId">
										</select>
										<P><spring:eval expression="@${lang}['SECURITY_LEVEL']" /></P>
										<select id="UserDeptSeclevelId">
										</select>
										<P><spring:eval expression="@${lang}['DESIGNATE_ADMINISTRATOR']" /></P>
										<select id="UserDeptSupervisor">
											<option value="Y"><spring:eval expression="@${lang}['USE']" /></option>
											<option value="N"><spring:eval expression="@${lang}['NOT_USE']" /></option>
										</select>
										<P><spring:eval expression="@${lang}['USE_OR_NOT']" /></P>
										<select id="UserState">
											<option value="Y"><spring:eval expression="@${lang}['USE']" /></option>
											<option value="N"><spring:eval expression="@${lang}['NOT_USE']" /></option>
										</select>
									</div>
                   					<div class="btn_wrap" style="padding-top:5px">
										<button type="button" class="btbase" id="btnResetPWD" style="display: none;" data-key="reset"><spring:eval expression="@${lang}['RESET_PASSWORD']" /></button>
									</div>
								</div>
							</div><!--rgt_area//-->
						</div><!--flex-content//--> 
						<!-- 원직추가 팝업 -->
						<div id="additionalJob" style="display: none; width: 850px; height: 700px;">
							<div class="popup" style="display: block;">
								<h3 class="pageTit"><spring:eval expression="@${lang}['ADD_ADDITIONAL_JOB']" /></h3>
								<button type="button" id="closeBtn">
									<img src="${image}/icon/x.png">
								</button>
								<ul class="tabmenu" style="display:none;">
									<li class="on"><spring:eval expression="@${lang}['ADD_ADDITIONAL_JOB']" /></li>
								</ul>
								<div class="tabCont">
									<div id="cont03" class="contdiv" style="display:block;">
										<h3 class="innerTit"><spring:eval expression="@${lang}['DEPARTMENT']" /></h3>
										<div class="flex-content">
											<div class="cont_list">
												<div style="text-align: left;">
						                			<ul id="znTreeDept"></ul>
						              			</div>
												<div style="text-align: right;">
													<ul id="znTreeDept"></ul>
												</div>
											</div>
											<div>
												<table class="pop_tbl">
													<colgroup>
														<col width="100%">
													</colgroup>
													<thead>
														<th><spring:eval expression="@${lang}['USER_INFO']" /></th>
													</thead>
												</table>
												<div>
													<table style="border: 0px;width:100%">
														<caption></caption>
														<colgroup>
															<col width="35%">
															<col width="65%">
														</colgroup>
														<tbody class="info_user_tb" id="deptuserinput">
															<tr style="height: 43px;">
																<td>
																	<spring:eval expression="@${lang}['EMPLOYEE_ID']" />
																</td>
																<td>
																	<label id="PopupUserEmpno" style="width: 90%;"></label>
																</td>
															</tr>
															<tr style="height: 43px;">
																<td>
																	<spring:eval expression="@${lang}['ID']" />
																</td>
																<td>
																	<label id="PopupUserID" style="width: 90%;"></label>
																</td>
															</tr>
															<tr style="height: 43px;">
																<td>
																	<spring:eval expression="@${lang}['NAME']" />
																</td>
																<td>
																	<label id="PopupUserName" style="width: 90%;"></label>
																</td>
															</tr>
															<tr style="height: 43px;">
																<td>
																	<spring:eval expression="@${lang}['TYPE']" />
																</td>
																<td>
																	<select id="PopupUserDeptType" style="width: 90%">
																		<option value="01" selected="selected"><spring:eval expression="@${lang}['GENERAL']" /></option>
																		<option value="02"><spring:eval expression="@${lang}['DEPARTMENT_MANAGER']" /></option>
																		<option value="03"><spring:eval expression="@${lang}['COMPANY_MANAGER']" /></option>
																		<option value="04"><spring:eval expression="@${lang}['SYSTEM_MANAGER']" /></option>
																	</select>
																</td>
															</tr>
															<tr style="height: 43px;">
																<td>
																	<spring:eval expression="@${lang}['POSITION']" />
																</td>
																<td>
																	<select id="PopupUserDeptPosition" style="width: 90%"></select>
																</td>
															</tr>
															<tr style="height: 43px;">
																<td>
																	<spring:eval expression="@${lang}['DUTY']" />
																</td>
																<td>
																	<select id="PopupUserDeptDutyId" style="width: 90%"></select>
																</td>
															</tr>
															<tr style="height: 43px;">
																<td>
																	<spring:eval expression="@${lang}['SECURITY_LEVEL']" />
																</td>
																<td>
																	<select id="PopupUserDeptSeclevelId" style="width: 90%"></select>
																</td>
															</tr>
															<tr style="height: 43px;">
																<td>
																	<spring:eval expression="@${lang}['DESIGNATE_ADMINISTRATOR']" />
																</td>
																<td>
																	<select id="PopupUserDeptSupervisor" style="width: 90%">
																		<option value="Y"><spring:eval expression="@${lang}['USE']" /></option>
																		<option value="N" selected="selected"><spring:eval expression="@${lang}['NOT_USE']" /></option>
																	</select>
																</td>
															</tr>
														</tbody>
													</table>
												</div>
											</div>
										</div>
									</div>
								</div>
								<button id="btnOriginSave" type="button" class="btbase" style="margin-top: 10px; margin-bottom:-15px; position: relative; left: 45%"><spring:eval expression="@${lang}['SAVE']"/></button>
							</div>
						</div>
					</div><!--innerWrap//-->
				</section>
			</div>
		</main>
	<footer>
	</footer>
	</body>
</html>