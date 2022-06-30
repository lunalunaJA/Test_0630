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
var arrDelGroupUser = [];
var arrChangGroupUser = [];

$(document).ready(function () {
	//트리 초기화
	fn_InitTree.jstree();
	
	//부서트리 초기화
	treeDept.jstree();
	
	//입력값 초기화
	resetInput();

   	// 입력 초기화
	$("#btnInit").click(function(){
		$("#btnNew").show();
		resetInput();
	});		
   	
   	// 그룹정보  등록
	$("#btnNew").click(function(){
		fn_Common.save();
	});
   	
   	// 그룹정보 삭제
	$("#btnDel").click(function(){
		fn_Common.disable();
	});	

   	// 그룹정보 복원
	$("#btnRes").click(function() {
		fn_Common.restore();
	});

   	// 그룹정보 폐기
	$("#btnDis").click(function(){
		fn_Common.discard();
	});

   	//부서 선택 탭
	$('.addGroupuser .tabmenu li').click(function(){
		var tabNum = $(this).index();

		$(this).siblings().removeClass('on');
		$(this).addClass('on');

		$('.tabCont .contdiv').css('display','none');
		$('.tabCont .contdiv').eq(tabNum).css('display','block')
	});

	// 그룹정보 검색
	$("#btnOrgnSearch").click(function(){
		treeDept.search();	
	});
   			
   	// 그룹 엔터키 검색
	$("#S_data").keydown(function(key){
		if (key.keyCode == 13) { // 엔터키
			treeDept.search();	
		}
	});					
	
   	// 그룹원 추가
	$("#btnGroupUserAdd").click(function(){
		var counttr = $("#GroupUserData >tr").length
		fn_InitTree.addGroupUser(counttr);
	});  
   	
   	// 그룹원 삭제
	$("#btnGroupModDetail").click(function(){
		fn_InitTree.deleteGroupUser();
	});    	

   	// 그룹원(부서) 탭
	$("#Btn_Orgn").click(function(){
   		
		$('#Btn_Orgn').attr('class','current');
		$('#Btn_Search').removeClass('current');
   		
		$('#OrgnArea').show();
		$('#SearchArea').hide();
   		
	});
   	
   	// 그룹원(검색) 탭
	$("#Btn_Search").click(function(){
   		
		$('#Btn_Search').attr('class','current');
		$('#Btn_Orgn').removeClass('current');
   		
		$('#SearchArea').show();
		$('#OrgnArea').hide();
	});   	
   	   	
   	//전체선택(그룹원정보)
	$("#GroupInfoALLCheck").click(function(){
		var isChecked = $("input:checkbox[id='GroupInfoALLCheck']").is(":checked");
		$("#GroupUserData").find('input').prop("checked", isChecked);
   	});       	
   	
	//전체선택(부서탭 - 사용자명)
	$("#OrgnAreaUserALLCheck").click(function(){
		var isChecked =$("input:checkbox[id='OrgnAreaUserALLCheck']").is(":checked");
		$("#OrgnAreaUserData").find('input').prop("checked", isChecked);
	});     	
   	
   	//전체선택(검색 탭 - 부서,사용자)
	$("#SearchAreaUserALLCheck").click(function(){
		var isChecked =$("input:checkbox[id='SearchAreaUserALLCheck']").is(":checked")
		$("#SearchAreaUserData").find('input').prop("checked", isChecked);
	});

});

/*********************************
Name   : fn_Common
Desc   : 공통 처리 함수
Param  : 없음
**********************************/
var fn_Common = {
	save : function(type) {	//그룹정보 정보 저장
		//그룹 선택 여부 확인
		if(objectIsEmpty(fn_InitTree.select.id)){
			alert("<spring:eval expression="@${msgLang}['SELECTED_GROUP_NOT_EXIST']"/>");
			return;
		}
	
		if(!(fn_InitTree.select.rtype == GROUPCODES["ALLUSER"] || fn_InitTree.select.rtype == GROUPCODES["SUPER"])){
			if(objectIsEmpty($.trim($("#GroupCode").val()))){
				alert("<spring:eval expression="@${msgLang}['ENTER_GROUP_CODE']"/>");
				$("#GroupCode").focus();
				return;
			}
		}
		if(objectIsEmpty($.trim($("#GroupName").val()))){
			alert("<spring:eval expression="@${msgLang}['ENTER_GROUP_NAME']"/>");
			$("#GroupName").focus();
			return;
		}
		var sendData={};
		if(objectIsEmpty($.trim($("#GroupID").val()))){
			var arrGroupUser = [];
			$('input:checkbox[name="GroupUser"]').each(function() {
				var user ={};
				user.gobjid = $(this).val();
				user.gobjtype = $(this).attr("valueType");
				if(fn_InitTree.select.rtype == GROUPCODES["APPROVAL"]){
					user.gobjseq = Number($(this).parent().parent().children().eq(3).find("select[name='aprval_lvl'] option:selected").val());
				}
				arrGroupUser.push(user);
			});
			arrUserSort(arrGroupUser);
			if(arrGroupUser.length == 0){
				alert("<spring:eval expression="@${msgLang}['GROUP_MEMBER_NOT_EXIST']"/>");
				return;
			}
			sendData.message= "<spring:eval expression="@${msgLang}['ARE_YOU_GROUP_INFO']"/>";
			sendData.url = "${ctxRoot}/api/organ/group/add";
			sendData.data = {
				"objIsTest" : "N",
				"zappGroup" : { "name" : $.trim($("#GroupName").val()) }
				,"zappGroupUsers" : arrGroupUser		
			}
	  	  		
			if(fn_InitTree.select.rtype == GROUPCODES["COMPANY"]){ 
				sendData.data.zappGroup.upid = companyid;
				sendData.data.zappGroup.types = GROUPCODES["COMPANY"];
			}else if(fn_InitTree.select.rtype == GROUPCODES["DEPT"]){
				sendData.data.zappGroup.upid = userid;
				sendData.data.zappGroup.types =  GROUPCODES["DEPT"];
			}else if(fn_InitTree.select.rtype == GROUPCODES["USER"]){
				sendData.data.zappGroup.upid =  deptuserid;
				sendData.data.zappGroup.types =  GROUPCODES["USER"];
			}else if(fn_InitTree.select.rtype == GROUPCODES["COLLABO"]){
				sendData.data.zappGroup.upid =  companyid;
				sendData.data.zappGroup.types =  GROUPCODES["COLLABO"];
			}else if(fn_InitTree.select.rtype ==GROUPCODES["APPROVAL"]){
			  //부서사용자는 upid가 해당부서,기업관리자는 upid가 company
				if(userType == USERTYPES["DEPT"]){
					sendData.data.zappGroup.upid = userid;
				}else if(userType == USERTYPES["COMPANY"]){
					sendData.data.zappGroup.upid = companyid;
				}
				sendData.data.zappGroup.types = GROUPCODES["APPROVAL"];
			}else{
				sendData.data.zappGroup.upid = companyid;
				sendData.data.zappGroup.types = fn_InitTree.select.rtype;
			}
	  	  			
			if(!(fn_InitTree.select.rtype == GROUPCODES["ALLUSER"] || fn_InitTree.select.rtype == GROUPCODES["SUPER"])){
				sendData.data.zappGroup.code = $.trim($("#GroupCode").val());
			}
	  	  			
			if(AprLvlChk() != 0){
				alert("<spring:eval expression="@${msgLang}['CHECK_APL']"/>");
				return;
			}
	  	  			
		}else{//수정
			//신규 그룹원
			var arrGroupUser = [];
			$('input:checkbox[name="GroupUser"]').each(function() {
				if($(this).attr("dataType") == "temp"){
					arrGroupUser.push({
					gobjid : $(this).val(),
					gobjtype : $(this).attr("valueType"),
					objAction : "ADD"
					});			
				}
			});

			arrUserSort(arrGroupUser);
			
			sendData.message= "<spring:eval expression="@${msgLang}['ARE_YOU_MODIFY_GROUP_INFO']"/>";
			sendData.url = "${ctxRoot}/api/organ/group/change";
			sendData.data={};
			sendData.data.objIsTest = "N";
			sendData.data.zappGroup = {"groupid" : $.trim($("#GroupID").val()), "name" : $.trim($("#GroupName").val())};
			//arrDelGroupUser:삭제 그룹원
			if(arrGroupUser.length > 0 && arrDelGroupUser.length > 0 && arrChangGroupUser.length > 0){ // 신규, 삭제, 수정 포함
				sendData.data.zappGroupUsers = arrGroupUser.concat(arrDelGroupUser).concat(arrChangGroupUser);
			}else if(arrGroupUser.length == 0 && arrDelGroupUser.length > 0 && arrChangGroupUser.length > 0){ // 삭제, 수정 포함
				sendData.data.zappGroupUsers = arrDelGroupUser.concat(arrChangGroupUser);
			}else if(arrGroupUser.length > 0 && arrDelGroupUser.length == 0 && arrChangGroupUser.length > 0){ // 신규, 수정 포함
				sendData.data.zappGroupUsers = arrGroupUser.concat(arrChangGroupUser);
			}else if(arrGroupUser.length > 0 && arrDelGroupUser.length == 0 && arrChangGroupUser.length == 0){	// 신규
				sendData.data.zappGroupUsers = arrGroupUser;
			}else if(arrGroupUser.length == 0 && arrDelGroupUser.length > 0 && arrChangGroupUser.length == 0){ // 삭제
				sendData.data.zappGroupUsers = arrDelGroupUser;
			}else if(arrGroupUser.length == 0 && arrDelGroupUser.length == 0 && arrChangGroupUser.length > 0){ // 수정
				sendData.data.zappGroupUsers = arrChangGroupUser;
			}
			if(fn_InitTree.select.rtype == GROUPCODES["SUPER"]){
				delete sendData.data.zappGroup.name;
			}
	  	  	    	
			if(AprLvlChk() != 0){
				alert("<spring:eval expression="@${msgLang}['CHECK_APL']"/>");
				return;
			}
	  	  	    	
		}
		fn_Common.publicCommon(sendData);
	},
	disable : function() {	//그룹정보 삭제 플레그 처리
	  	var sendData={};
		sendData.message= "<spring:eval expression="@${msgLang}['ARE_YOU_GROUP_DELETE']"/>";
		sendData.url = "${ctxRoot}/api/organ/group/disable";
		sendData.data={};
		sendData.data.objIsTest = "N";
		sendData.data.zappGroup = {"groupid" : $.trim($("#GroupID").val()), "objIncLower" : "Y"};       
		     	
		fn_Common.publicCommon(sendData);
	},
	restore : function() {	//그룹정보 복원
		var sendData={};
		sendData.message= "<spring:eval expression="@${msgLang}['DO_YOU_RESTORE_GROUP']"/>";
		sendData.url = "${ctxRoot}/api/organ/group/enable";
		sendData.data={};
		sendData.data.objIsTest = "N";
		sendData.data.zappGroup = {"groupid" : $.trim($("#GroupID").val()), "objIncLower" : "Y"};           
		
		fn_InitTree.select = {};
		fn_Common.publicCommon(sendData);
	},
	discard : function() {	//그룹정보 정보 폐기
		var sendData={};
		sendData.message= "<spring:eval expression="@${msgLang}['ARE_YOU_GROUP_DISCARD']"/>";
		sendData.url = "${ctxRoot}/api/organ/group/discard";
		sendData.data={};
		sendData.data.objIsTest = "N";
		sendData.data.zappGroup = {"groupid" : $.trim($("#GroupID").val())};   
		    	
		fn_Common.publicCommon(sendData);
	},
	publicCommon : function(sendData) {	//공통 처리 로직 호출
		//console.log("publicCommon sendData :", JSON.stringify(sendData));

		noty({
			layout:"center",
			text : sendData.message,
		    buttons : [ {
				addClass : 'b_btn',
				text : "Ok",
				onClick : function($noty) {
					$noty.close();
					$.ajax({
						type : 'POST',
						url : sendData.url,
						dataType : 'json',
						contentType : 'application/json',
						async : false,
						data : JSON.stringify(sendData.data),
						success : function(data){
							if(data.status == "0000"){
								alert("<spring:eval expression="@${msgLang}['OPERATION_IS_COMPLETED']"/>");
								fn_InitTree.jstree();
								resetInput();
							}else{
								alertErr(data.message);
							}
						},   error : function(request, status, error) {
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
}

/*********************************
Name   : resetInput
Desc   : 초기화
Param  : 없음
**********************************/
var resetInput = function() {
	$("#GroupCode").attr("disabled", false);
	$("#GroupState").attr("disabled", true);

	$("#znTreeDept").jstree("deselect_all");
	$("#znTreeDept").jstree(true).uncheck_all();
	
	//버튼 초기화
	$("#btnRes").hide();
	$("#btnDel").hide();
	$("#btnDis").hide();

	//검색탭 초기화
	$("input:checkbox[id$='ALLCheck']").attr("checked", false);
	$("input:radio[name='searchTarget']:radio[value='DEPT']").prop('checked', true); 
	$('#S_data').val("");
	
	$('#Btn_Orgn').trigger("click");
	//input, check, list 초기화
	$("input:checkbox").removeAttr("checked");
	$("tbody[id$='UserData']").empty("");
	$("input[id^=Group]").val("");
	
}

/*********************************
Name   : fn_InitTree
Desc   : 트리 초기화
Param  : 
**********************************/
var fn_InitTree = {
	id : "GR", 
	name : "<spring:eval expression="@${lang}['GROUP']"/>",
	select : {},
	jstree : function(){
		$('#znTreeGroup').remove();
		var treeHtml = "<ul id='znTreeGroup'></ul>";
		$('.contNav').append(treeHtml);
		
		var sortType = "asc";
		var initData =[];
		if(userType == USERTYPES.USER || userType == USERTYPES.DEPT){ //일반사용자 or 부서관리자
			initData.push(this.createRootData(GROUPCODES.USER,"<spring:eval expression="@${lang}['PERSONAL_GROUP']"/>"));
		} else{ //기업관리자
			initData.push(this.createRootData(GROUPCODES.APPROVAL,"<spring:eval expression="@${lang}['APPROVAL_GROUP']"/>")); // 승인
			initData.push(this.createRootData(GROUPCODES.COLLABO,"<spring:eval expression="@${lang}['COLLABORATIVE_GROUP']"/>")); //협업
			initData.push(this.createRootData(GROUPCODES.COMPANY,"<spring:eval expression="@${lang}['COMPANY_GROUP']"/>")); //전사
		}
		$("#znTreeGroup").jstree({ 
			'plugins': ["state","sort"],
			'core': {
				'data': initData,
				'check_callback': true
			},
			sort: function(a, b){
				var a1 = this.get_node(a);
				var b1 = this.get_node(b);
				if (sortType === "asc"){
					return (a1.a_attr.priority > b1.a_attr.priority) ? 1 : -1;
					} else {
						return (a1.a_attr.priority > b1.a_attr.priority) ? -1 : 1;
					}
			}
		})
		.on("select_node.jstree", function (event, data) { 
			resetInput();
			var obj = data.instance.get_node(data.selected);
			fn_InitTree.select.parentid = obj.parent;
			fn_InitTree.select.id = obj.id;
			fn_InitTree.select.name = obj.text;
			fn_InitTree.select.code = obj.a_attr.itemcode;
			fn_InitTree.select.type = obj.a_attr.itemtype;
			fn_InitTree.select.isactive = obj.a_attr.itemisactive;
			fn_InitTree.select.rtype = obj.a_attr.itemroottype;
			
			if(fn_InitTree.select.rtype != "05"){
				$("#aprlvl").css('display', "none");
			}else{
				$("#aprlvl").css('display', '');
			}
		 	
			$("#GroupUserData").empty();
			if(fn_InitTree.select.type == "N"){
				$("#GroupState").val((fn_InitTree.select.isactive=="Y")?"<spring:eval expression="@${lang}['USE']"/>":"<spring:eval expression="@${lang}['NOT_USE']"/>");
				$("#GroupCode").val(fn_InitTree.select.code);
				$("#GroupName").val(fn_InitTree.select.name);
				$("#GroupID").val(fn_InitTree.select.id);
				
				if(obj.parent != "#" || fn_InitTree.select.code == GROUPCODES["SUPER"]){
					fn_InitTree.getGroup(fn_InitTree.select.id);//그룹정보 조회 정보
				}
				arrDelGroupUser.length = 0;
				$("#GroupCode").attr("disabled", true);
			}else{
				$("#GroupState").text("<spring:eval expression="@${lang}['USE']"/>");
				$("input[id^=Group]").val("");
				$("#GroupUserData").empty();	
				$("#GroupCode").attr("disabled", false);
			}
			//그룹 종류별 버튼 처리
			//전사그룹 COMPANY 01 / 부서그룹 DEPT 02 / 개인그룹 USER 03 / 협업그룹 COLLABO 04 / 승인그룹 APPROVAL 05 / 슈퍼권한그룹 SUPER 99
			if(fn_InitTree.select.parentid == "#"){
				if(fn_InitTree.select.rtype == GROUPCODES["SUPER"]){
					$("#btnInit").hide();
					$("#btnNew").show();
				}else if(fn_InitTree.select.rtype !=  GROUPCODES["DEPT"]){
					$("#btnInit").show();
					$("#btnNew").hide();
				}
			}else{
				$("#btnNew").show();
				if(fn_InitTree.select.rtype ==  GROUPCODES["DEPT"]){
					$("#btnDel").hide();
				}else{
					//사용유무에 따라 버튼 처리
					if (fn_InitTree.select.isactive == "Y") {
						$("#btnDel").show();
						$("#btnRes").hide();
						$("#btnDis").hide();
					} else {
						$("#btnDel").hide();
						$("#btnRes").show();
						$("#btnDis").show();
					}
				}
			}
		})
		.on('ready.jstree', function () {
			//01:일반, 02:부서, 03:기업, 04:전체 
			if(userType == USERTYPES.USER || userType == USERTYPES.DEPT){ 
				fn_InitTree.getGroupList(GROUPCODES["USER"]);//개인그룹 
				//}else if(userType == USERTYPES.DEPT){ 
				//fn_InitTree.getGroupList(GROUPCODES["DEPT"]);//부서그룹 
			}else{
				fn_InitTree.getGroupList(GROUPCODES["COMPANY"]);//전사그룹 
				fn_InitTree.getGroupList(GROUPCODES["COLLABO"]);//협업그룹
				fn_InitTree.getGroupList(GROUPCODES["APPROVAL"]);//승인그룹
				fn_InitTree.getGroupList(GROUPCODES["SUPER"]);//슈퍼권한그룹 아직 안됨
			}
		
		});
	},
	createRootData : function(code,name){
		return {
			"id": code,
			"text": name,
			"icon": ADMINTREEICONS.COMPANY , 			
			"state": {
				"opened": true,
				"disabled": false
			}
			,"li_attr": {}
			,"a_attr": {
				"itemcode":code,
				"itemname":name,
				"itemtype":"N",
				"itemisactive":"Y",
				"itemroottype":code, 
				"priority":0
			} 
		}
	},
	createNode :  function(item){
		$('#znTreeGroup').jstree (
			'create_node'
			, item.rootcode
			, {	  "text"   : item.name
				, "id"     : item.groupid
				, "icon"   : item.icon
				, "state"  : { "opened": true }
				, "a_attr" : {   "itemcode":item.code
					, "itemname":item.name
					, "itemtype":item.itemtype 
					, "itemisactive":item.isactive
					, "itemroottype":item.types
					,"priority":item.priority
							} 
				}
			, "last"
			, false
			, false
		);
	},
	active : function(id){
		$('#znTreeGroup').jstree("open_node", id);//트리 이벤트
		$('#znTreeGroup').jstree("activate_node", id);//트리	이벤트	
	},
	getGroup : function(scGroupid){
		$.ajax({
			url :"${ctxRoot}/api/organ/group/get"
			, type : "POST"
			, dataType : "json"
			, contentType : 'application/json'
			, async : false
			, data : JSON.stringify({
				"objIsTest" : "N",
				"zappGroup" : { "groupid" : scGroupid }
			})
			, success : function(data){
				//console.log("getGroup data:", JSON.stringify(data));
				if(data.status == "0000") {
					if(objectIsEmpty(data.result)){
						$("#GroupUserData").html("");
						return;
					}
					var GroupUserList =  data.result.zappGroupUserExtends.length;
						
					$.each(data.result.zappGroupUserExtends, function(index, item){
						var $tr = $("<tr></tr>");
						var userHtml = "";
						var groupuserid = item.groupuserid;
						var gobjtype = item.gobjtype;
						var groupid = item.gobjid;
						var editable = item.editable;
						var approvalLevel = item.gobjseq;
						var groupname = "";
						var gobjtypeStr = "";
						if(gobjtype == "01"){
							gobjtypeStr = "<spring:eval expression="@${lang}['USER']"/>";
							groupname = item.zappUser.name;
						}else{
							gobjtypeStr = "<spring:eval expression="@${lang}['DEPARTMENT']"/>";
							groupname = item.zappDept.name;
						}
						var disabled = ((editable == "N")?"disabled":"enabled")
						userHtml += "<td><input type=checkbox name=GroupUser id=GroupUser"+index+" value='"+groupid+"' valueGroupuserid='"+groupuserid+"' valueType='"+gobjtype+"' dataType='real' "+disabled+"><label for=GroupUser"+index+"></label></td>";
						userHtml += "<td>"+groupname+"</td>";
						userHtml += "<td>"+gobjtypeStr+"</td>";
						if(fn_InitTree.select.rtype == "05"){
							userHtml += "<td>"+getAprLvlSelectBox(GroupUserList, approvalLevel, index)+"</td></tr>";
						}
						$tr.append(userHtml);
						$("#GroupUserData").append($tr);
					});
				}else{
					alertErr(data.message);
				}
				
			},   error : function(request, status, error) {
					alertNoty(request,status,error);
			}
		})
	},
	getGroupList : function(type) {		//그룹목록을 조회한다.
		var sendData = {
			"objIsTest" : "N",
			"zappGroup" : { 
			"types":type,
			"companyid":companyid
			}
		};    
		if(userType == USERTYPES.USER || userType == USERTYPES.DEPT){
			sendData.zappGroup.upid = deptuserid;
		}
		// 조회
		$.ajax({
			url :"${ctxRoot}/api/organ/group/list"
			, type : "POST"
			, dataType : "json"
			, contentType : 'application/json'
			, async : false
			, data : JSON.stringify(sendData)
			, success : function(data){
				//console.log("===getGroupList data : "+JSON.stringify(data));
				if(data.status == "0000") {
					if(objectIsEmpty(data.result)){
						return;
					}
					$.each(data.result, function(index, item){
						var grInfo = item;
						grInfo.itemtype = "N";
						if(type == GROUPCODES.SUPER){
							grInfo.icon = ADMINTREEICONS.DEPT;
							grInfo.rootcode = "#";
							grInfo.code = GROUPCODES["SUPER"];
						}else{
							var icon = (item.isactive == "Y") ? ADMINTREEICONS.DEPT:TREEICONS["N"];
							grInfo.icon = icon;	
							grInfo.rootcode ="#"+type;
						}
						fn_InitTree.createNode(grInfo);
					});
					
					if(!objectIsEmpty(fn_InitTree.select.id)){
						fn_InitTree.active(fn_InitTree.select.id);
					}
				}
			},	error : function(request, status, error) {
					alertNoty(request,status,error);
			}
		})
	},
	addGroupUser : function(counttr){
		//그룹원 추가
		if(fn_InitTree.select.rtype != "05"){
			//선택된 부서정보
			var result = $("#znTreeDept").jstree('get_checked');
			$.each(result, function(index, item){
				var node = $('#znTreeDept').jstree(true).get_node(item);
				if(node.parent != '#'){
					if(fn_InitTree.GroupUserDupCheck(node.id) != "DUP" ){//중복체크
						var $tr = $("<tr></tr>");
						var userHtml = "";
						userHtml += "<td><input type=checkbox name=GroupUser id=GroupUser"+counttr+" value='"+node.id+"' valueType='02' dataType='temp' ><label for=GroupUser"+counttr+"></label></td>";
						userHtml += "<td>"+node.text+"</td>";
						userHtml += "<td><spring:eval expression="@${lang}['DEPARTMENT']"/></td>";
						$tr.append(userHtml);
						$("#GroupUserData").append($tr);
						counttr += 1;
					}
				}
			});
		}
	  	
	  	//선택된 부서정보
		$("input[name=OrgnAreaUser]:checked").each(function(idx) {
			if(fn_InitTree.GroupUserDupCheck($(this).val()) != "DUP"){//중복체크
				var $tr = $("<tr></tr>");
				var userHtml = "";
				userHtml += "<td><input type=checkbox name=GroupUser id=GroupUser"+counttr+" value='"+$(this).val()+"' valueType='01' dataType='temp' ><label for=GroupUser"+counttr+"></label></td>";
				userHtml += "<td>"+$(this).attr("textData")+"</td>";
				userHtml += "<td><spring:eval expression="@${lang}['USER']"/></td>";
				if(fn_InitTree.select.rtype == "05"){
					userHtml += "<td>"+setAprLvlSelectBox()+"</td>";
				}
				$tr.append(userHtml);
				$("#GroupUserData").append($tr);
				counttr += 1;
			}
		});
	  	//검색 결과
		$("input[name=SearchAreaUser]:checked").each(function(idx) {
			if(fn_InitTree.GroupUserDupCheck($(this).val()) != "DUP"){//중복체크
				var sc_dataText =  "<spring:eval expression="@${lang}['DEPARTMENT']"/>";
				if($(this).attr("valueType") == USERTYPES["USER"]){
					sc_dataText = "<spring:eval expression="@${lang}['USER']"/>";
				}
				var $tr = $("<tr></tr>");
				var userHtml = "";
				userHtml += "<td><input type=checkbox name=GroupUser id=GroupUser"+counttr+" value='"+$(this).val()+"' valueType='"+$(this).attr("valueType")+"' dataType='temp' ><label for='GroupUser"+counttr+"'></label></td>";
				userHtml += "<td>"+$(this).attr("textData")+"</td>";
				userHtml += "<td>"+sc_dataText+"</td>";
				if(fn_InitTree.select.rtype == "05"){
	 	  			userHtml += "<td>"+setAprLvlSelectBox()+"</td>";
	 	  		}
	  			$tr.append(userHtml);
	  	   		$("#GroupUserData").append($tr);
				counttr += 1;
	  		}
	  	});
	},
	deleteGroupUser : function(){		//그룹원 삭제		
		$('input:checkbox[name="GroupUser"]').each(function() {
	  		if($(this).is(":checked") && !$(this).is(":disabled")){
	  			if($(this).attr("dataType") == "temp"){
	  				$(this).parent().parent().remove();
	  			}else{
	  				//삭제 정보 추가
					arrDelGroupUser.push({
						groupuserid : $(this).attr("valueGroupuserid"),
						objAction : "DISCARD"
					});
					$(this).parent().parent().remove();
				}
			}
		});
		$("input:checkbox[id='GroupInfoALLCheck']").attr("checked", false);

	},
	GroupUserDupCheck : function(inData) {	//그룹원 중복체크
		var rst = "";
		var GrData = document.getElementsByName("GroupUser");

		for(var k = 0; k < GrData.length; k++){
			if(GrData[k].value == inData || inData == companyid){
				rst = "DUP";
				break;
			}
		}
		return 	rst;
	},
	confirm : function(sendData){

	}		
}

/*********************************
Name   : treeDept
Desc   : 부서 트리
Param  : 없음
**********************************/
var treeDept = {
	id :companyid,
	name : companyName,
	select : {},
	jstree : function(){
		$('#znTreeDept').jstree({ 
			"checkbox" : {
				three_state : false,
				whole_node :  false,
				tie_selection : false
			},	
			'plugins': ["state","checkbox"],
			'core': {
				'data': [{
					"id": treeDept.id,
					"text": treeDept.name,
					"icon": ADMINTREEICONS.COMPANY, 			
					"state": {
						"opened": true,
						"disabled": false
					}
					,"li_attr": {},
					"a_attr": {"itemcode":"","itemname":"","itemtype":"N","itemisactive":"Y", class: "no_checkbox"}
				}],
				'check_callback': true
			}

  		}).on('select_node.jstree', function(event, data){
			var obj = data.instance.get_node(data.selected);
			treeDept.select.parentid = obj.parent;				//부모아이디
			treeDept.select.id = obj.id;
			treeDept.select.name = obj.text;
			treeDept.select.code = obj.a_attr.itemcode;
			treeDept.select.type = obj.a_attr.itemtype;
			treeDept.select.sactive = obj.a_attr.itemisactive;
  			
			$("input:checkbox[id='OrgnAreaUserALLCheck']").attr("checked", false);
			treeDept.selectDeptUserList(treeDept.select.id);//부서 사용자 조회
		}).on('ready.jstree', function () {
			treeDept.getDeptList(); 
		});
	},
	getDeptList : function(){
  		//부서정보 조회
		$.ajax({
			url :"${ctxRoot}/api/organ/dept/list/down"
			, type : "POST"
			, dataType : "json"
			, contentType : 'application/json'
			, async : false
			, data : JSON.stringify({
				companyid : companyid,
				upid : companyid,
  				//isactive : "Y",
				objIsMngMode : true
			})
			, success : function(data){
				//console.log("===getDeptList data : "+JSON.stringify(data));
				if(data.status == "0000") {
					if(objectIsEmpty(data.result)){
						return;
					}
					$.each(data.result, function(index, item){
						treeDept.createDept(item);
					});
				}else{ 
					alertErr(data.message);
				}
			}, error : function(request, status, error) {
				alertNoty(request,status,error,"<spring:eval expression="@${msgLang}['ARE_YOU_GROUP_DELETE']" />");
			}
		})
	},
	createDept : function(item){
		$('#znTreeDept').jstree (
			'create_node'
			, item.upid
			, {	  "text"   : item.name
				, "id"     : item.deptid
				, "icon"   : ADMINTREEICONS.DEPT
				, "state"  : { "opened": true }
				, "a_attr" : {   "itemcode":item.code
								, "itemname":item.name
								, "itemtype":"Y"
								, "itemisactive":"Y"  
							} 
				}
				, "last"
  	  			, false
  	  			, false
			);
  	  	
	},
	//부서 사용자 조회
	selectDeptUserList  : function(deptid) {
		$('#OrgnAreaUserData').empty();
  		// 조회
		$.ajax({
			url :"${ctxRoot}/api/organ/deptuser/list"
			, type : "POST"
			, dataType : "json"
			, contentType : 'application/json'
			, async : false
			, data : JSON.stringify({
				"deptid" : deptid,
				"isactive" : "Y"
			})
			, success : function(data){
				if(data.status == "0000") {
					if(objectIsEmpty(data.result)){
						$("#OrgnAreaUserData").html("");
						return;
					}
  					
					$.each(data.result, function(index, result){
						var userHtml = "";
						var $tr = $("<tr></tr>");
						userHtml += "<td><input type=checkbox name=OrgnAreaUser id=OrgnAreaUser"+index+" value='"+result.deptuserid+"' textData='"+result.zappUser.name+"' ><label for=OrgnAreaUser"+index+"></label></td>"
						userHtml += "<td>" + result.zappUser.name + " (" + result.zappUser.loginid + ")</td>";
						$tr.append(userHtml);
						$('#OrgnAreaUserData').append($tr);
					});
				}
			},  error : function(request, status, error) {
				alertNoty(request,status,error,"<spring:eval expression="@${msgLang}['ARE_YOU_GROUP_DELETE']" />");
			}
		})
	},
	search : function(){
  		
		if( $.trim($("#S_data").val()) == ""){
			alert("<spring:eval expression="@${msgLang}['ENTER_TERM']"/>");
			$("#S_data").focus();
			return;
		}
  		
		var inputValue = $("input[name='searchTarget']:checked").val(); 
		var sendData = {};
		var data = {};
  		
		if(inputValue == "USER"){
			sendData.url = '${ctxRoot}/api/organ/deptusers/list';
			data = {"companyid" : "${Authentication.objCompanyid}",
					"zappUser" : {"name": $.trim($("#S_data").val())}};
		}else{
			sendData.url = '${ctxRoot}/api/organ/dept/list';
			data = {"companyid" : "${Authentication.objCompanyid}",
					"name": $.trim($("#S_data").val())};
		}

		$.ajax({
			type : 'POST',
			url : sendData.url,
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(data),
			success : function(data){
				//console.log("===search data : "+JSON.stringify(data));
				if(data.status == "0000"){
					if(objectIsEmpty(data.result)){
						$("#SearchAreaUserData").html("");
						return;
					}
  							
					$("#SearchAreaUserData").empty();
						$.each(data.result, function(index, item){
							var searHtml = "";
							var $tr = $("<tr></tr>");
							if(inputValue == "USER"){
								searHtml += "<td><input type=checkbox name=SearchAreaUser id=SearchAreaUser"+index+" value='"+item.deptuserid+"' textData='"+item.zappUser.name+"' valueType='01' ><label for=SearchAreaUser"+index+"></label></td>";
								searHtml += "<td>"+item.zappUser.name+"["+item.zappDept.name+"]"+"</td>";
								}else{
									searHtml += "<td><input type=checkbox name=SearchAreaUser id=SearchAreaUser"+index+" value='"+item.deptid+"' textData='"+item.name+"' valueType='02' ><label for=SearchAreaUser"+index+"></label></td>";
									searHtml += "<td>"+item.name+"</td>";
								}
							$tr.append(searHtml);
							$("#SearchAreaUserData").append($tr);
						});
  						 
					}else{
						alertErr(data.message);
					}
  						
				},   error : function(request, status, error) {
						alertNoty(request,status,error);
				}					
		});			
  	}
};

/*********************************
Name   : getAprLvlSelectBox
Desc   : 승인레벨 콤보박스 만들기
Param  : length, seq, idx(GroupUserList, approvalLevel, index)
**********************************/
var getAprLvlSelectBox = function(length, seq, idx){
	var AprLvlSelect = "<select id='aprval_"+idx+"' name = 'aprval_lvl' onchange='ChangeAprLvlSelectBox("+idx+")'>";
	for(var i = 0 ; i < 4; i++){
		if((i+1) == seq){
			AprLvlSelect += "<option value = "+(i+1)+" selected>"+(i+1)+"</option>";	
		}else{
			AprLvlSelect += "<option value = "+(i+1)+">"+(i+1)+"</option>";
		}
	}
	AprLvlSelect += "</select>";
	
	return AprLvlSelect;
}

/*********************************
Name   : setAprLvlSelectBox
Desc   : 승인레벨 콤보박스 만들기
Param  : 없음
**********************************/
var setAprLvlSelectBox = function(){
	
	var selHtml = "<select name = 'aprval_lvl'>";
	var option = "";
	for(var i = 0 ; i < 4 ; i++){
		option += "<option value = '"+(i+1)+"'>"+(i+1)+"</option>";	
	}
	selHtml += option;
	selHtml += "</select>";
	return selHtml;
}

/*********************************
Name   : arrUserSort
Desc   : 유저 정렬 
Param  : ArrGroupUser
**********************************/
var arrUserSort = function(ArrGroupUser){
	ArrGroupUser.sort(function(a, b){
		return a.gobjseq - b.gobjseq;
	})
	for(var i = 0 ; i < ArrGroupUser.length; i++){
		if((i+1) != ArrGroupUser.length){
			var preIdx = ArrGroupUser[i].gobjseq;
			var nextIdx = ArrGroupUser[i+1].gobjseq;
			if((nextIdx - preIdx) > 1){
				ArrGroupUser[i+1].gobjseq = preIdx+1;
			}
		}
	}
}

/*********************************
Name   : ChangeAprLvlSelectBox
Desc   : 승인레벨 콤보박스 변경
Param  : idx
**********************************/
var ChangeAprLvlSelectBox = function(idx){
	
	var selval = $("#aprval_"+idx+" option:selected").val();
	var groupuserid = $("#GroupUserData tr:eq("+idx+")").children().children().eq(0).attr("valuegroupuserid");
	arrChangGroupUser.push({
		groupuserid : groupuserid,
		objAction : "CHANGE",
		gobjseq : Number(selval)
	})
}

var AprLvlChk = function(){
	var ChkCnt = 0;
	var arrGroupUser =[];
	$('input:checkbox[name="GroupUser"]').each(function() {
		var user ={};
		user.gobjseq = Number($(this).parent().parent().children().eq(3).find("select[name='aprval_lvl'] option:selected").val());
		arrGroupUser.push(user);
	});
	
	arrGroupUser.sort(function(a, b){
		return a.gobjseq - b.gobjseq;
	})
	
	for(var i = 0 ; i < arrGroupUser.length; i++){
		if((i+1) != arrGroupUser.length){
			var preIdx = arrGroupUser[i].gobjseq;
			var nextIdx = arrGroupUser[i+1].gobjseq;
			if((nextIdx - preIdx) > 1){
				ChkCnt++;	
			}
		}
	}
	
	return ChkCnt;
}
</script>
</head>
<body>
	<!--header stard-->
	<c:import url="../common/TopPage.jsp" />
	<main>
		<div class="flx">
			<c:import url="../common/AdminLeftMenu.jsp" />
			<section id="content">
				<div class="innerWrap innerWrap_scroll">
					<h2 class="pageTit"><img src="${image}/icon/Group 158.png" alt=""><spring:eval expression="@${lang}['GROUP_MANAGEMENT']" /></h2>
					<div class="flex-content">
						<div class="contNav">
							<ul id="znTreeGroup"></ul>
						</div><!--contNav//-->
						<div class="rgt_area_groupuser">
							<div class="wdt100">
								<h3 class="innerTit"><spring:eval expression="@${lang}['GROUP']" /></h3>
								<div class="btn_wrap">
									<button type="button" class="btbase" id="btnInit" name="new"><spring:eval expression="@${lang}['INITIALIZATION']" /></button>
									<button type="button" class="btbase" id="btnNew" name="add"><spring:eval expression="@${lang}['SAVE']" /></button>
									<button type="button" class="btbase" id="btnRes" name="res"><spring:eval expression="@${lang}['RESTORE']" /></button>
									<button type="button" class="btbase" id="btnDel" name="del"><spring:eval expression="@${lang}['DELETE']" /></button>
									<button type="button" class="btbase" id="btnDis" name="dis"><spring:eval expression="@${lang}['DISCARD']" /></button>
								</div>
								<table class="inner_tbl">
									<colgroup>
										<col width="34%">
										<col width="33%">
										<col width="33%">
									</colgroup>
									<thead>
										<th><spring:eval expression="@${lang}['GROUP_CODE']" /></th>
										<th><spring:eval expression="@${lang}['GROUP_NAME']" /></th>
										<th><spring:eval expression="@${lang}['USE_OR_NOT']" /></th>
									</thead>
									<tbody>
										<tr>
											<td><input type="text" id="GroupCode" title="<spring:eval expression="@${lang}['CODE']"/>"><input type="hidden" id="GroupID" value="" /></td>
											<td><input type="text" id="GroupName" title="<spring:eval expression="@${lang}['CODE_NAME']"/>" onkeyup='pubByteCheckTextarea(event,150)'></td>
											<td><input type="text" id="GroupState" title="<spring:eval expression="@${lang}['USE_OR_NOT']"/>" /></td>
										</tr>
									</tbody>
								</table>
							</div>
							<div class="wdt100">
								<h3 class="innerTit"><spring:eval expression="@${lang}['GROUP_MEMBER_INFO']" /></h3>
								<div class="btn_wrap">
									<button type="button" class="btbase" id="btnGroupModDetail"><spring:eval expression="@${lang}['DELETE_GROUP_MEMBER']" /></button>
								</div>
								<table class="inner_tbl">
									<colgroup>
										<col width="4%">
										<col width="40%">
										<col width="40%">
									</colgroup>
									<thead>
										<th><input type="checkbox" name="selectAll" id="GroupInfoALLCheck"><label for ="GroupInfoALLCheck"></label></th>
										<th><spring:eval expression="@${lang}['GROUP_MEMBER']" /></th>
										<th><spring:eval expression="@${lang}['GROUP_TYPE']" /></th>
										<th id="aprlvl" style="display: none;"><spring:eval expression="@${lang}['APPROVAL_LEVEL']" /></th>
									</thead>
								</table>
								<div style="height:160px;overflow-y:scroll">
									<table class="inner_tbl" style="margin:auto">
										<colgroup>
											<col width="4%">
											<col width="40%">
											<col width="40%">
										</colgroup>
										<tbody id="GroupUserData">
										</tbody>
									</table>
								</div>
							</div>
							<div class="wdt100">
								<div class="addGroupuser">
									<div>
									<ul class="tabmenu">
										<li class="on"><spring:eval expression="@${lang}['DEPARTMENT']" /></li>
										<li><spring:eval expression="@${lang}['SEARCH']" /></li>
									</ul>
									<div class="btn_wrap">
										<button type="button" class="btbase" id="btnGroupUserAdd"><spring:eval expression="@${lang}['ADD_GROUP_MEMBER']" /></button>
									</div>
									</div>
									<div class="tabCont">
										<div class="contdiv" id="cont01">
											<div id="OrgnArea" style="height:270px;padding:0;overflow:unset;">
												<div id="Dept_Tree" style="float: left; width: 60%; height: 100%; overflow-y: auto; zoom: 1; border: 1px solid #d2d2d2; background-color: rgb(245, 245, 245);">
													<ul id="znTreeDept"></ul>
												</div>
												<div style="float: right;height:100%; width: 40%; border:0px;padding:0px;overflow:unset;" class="info_tb" >
													<table class="inner_tbl" style="margin-top:0;height:38px">
													<caption></caption>
														<colgroup>
															<col width="10%">
															<col width="90%">
														</colgroup>
														<thead>
															<tr>
																<th><input type="checkbox" name="OrgnAreaUserALLCheck" id="OrgnAreaUserALLCheck"><label for ="OrgnAreaUserALLCheck"></label></th>
																<th><spring:eval expression="@${lang}['USER']" /></th>
															</tr>
														</thead>
													</table>
													<div style="height: calc(100% - 50px);border:none;overflow:scroll;">
														<table style="margin-top:0" class="inner_tbl">
															<colgroup>	
																<col width="10%">
																<col width="90%">
															</colgroup>
															<tbody id="OrgnAreaUserData" style="border-top:0;">
															</tbody>
														</table>
													</div>
												</div>
											</div>
										</div><!--cont01//-->
										<div class="contdiv" id="cont02">
											<div class="inner_uiGroup mgt0">
												<p style="width:5%"><spring:eval expression="@${lang}['TARGET']" /></p>
												<input type="radio" name="searchTarget" value="DEPT" id="S_dept" checked><label for="rd01"><spring:eval expression="@${lang}['DEPARTMENT']" /></label>
												<input type="radio" name="searchTarget" value="USER" id="S_user" ><label for="rd02" ><spring:eval expression="@${lang}['USER']" /></label>
												<div style="display:inline;float:right">
												<input type="text" value="" name="searchData" id="S_data" style="width: 180px;" />
												<button type="button" class="btbase" id="btnOrgnSearch"><spring:eval expression="@${lang}['SEARCH']" /></button>
												</div>
											</div>
											<table class="inner_tbl">
												<colgroup>
													<col width="10%">
													<col width="90%">
												</colgroup>
												<thead>
													<th><input type=checkbox name=SearchAreaUserALLCheck id=SearchAreaUserALLCheck /><label for="SearchAreaUserALLCheck"></label></th>
													<th><spring:eval expression="@${lang}['MEMBER']" /></th>
												</thead>
											</table>
											<div style="height: 172px;border:none;overflow:scroll;">
												<table style="margin-top:0" class="inner_tbl">
													<colgroup>	
														<col width="10%">
														<col width="90%">
													</colgroup>
													<tbody id="SearchAreaUserData" style="border-top:0;">
													</tbody>
												</table>
											</div>
										</div> <!--cont02-->                      
									</div>
								</div>
							</div>
						</div><!--rgt_area//-->
					</div><!--flex-content//-->    
				</div><!--innerWrap//-->
			</section>
		</div>
	</main>
</body>
</html>