<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp"%>
<%
//String[] uAuth = Utility.split(sessUserAuth, "|");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>NADi4.0 :: 문서함</title>
<link type="text/css" rel="stylesheet" href="${css}/common.css" />
<link type="text/css" rel="stylesheet" href="${css}/jquery-ui-1.11.0.min.css" />
<link type="text/css" rel="stylesheet" href="${css}/jstree.css" />
<script type="text/javascript" src="${js}/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src="${js}/jquery-ui-1.11.0.js"></script>
<script type="text/javascript" src="${js}/jquery.noty.packaged.min.js"></script>
<script type="text/javascript" src="${js}/jstree.js"></script>
<script type="text/javascript" src="${js}/common.js"></script>
<script type="text/javascript">
</script>
<style>
th {
  position: sticky;
  top: 0;
  z-index: 2;
}

.lockInput {
  border: none !important;
}
</style>

<script type="text/javascript">

$(document).ready(function () {

	$("#menu04-2").addClass("on");
	
	initBtn();
	treeRoot();
	
   	// 입력 초기화
	$("#btnInit").click(function(){
		AllReset();
		$("#btnNewGroup").show();
		$("#btnDel").text("<spring:eval expression="@${lang}['DELETE']"/>").hide();
		$("#btnDis").hide();
	});		
   	
 	// 저장 (등록, 수정)
   	$("#btnNewGroup").click(function(){
   		NodeNew();
   	});

   	// 문서함정보  삭제(미사용으로 변경)
   	$("#btnDel").click(function(){
   		NodeDel();
   	});   	
	//문서함정보 폐기(실제 삭제)
	$("#btnDis").click(function(){
		NodeDis();
	});
   	
   
  //권한 삭제
	$("#btnGroupModDetail").click(function(){
		GroupNodeDel();
	});
	
	//권한 정보 체크박스(전체 체크)
	$("#GroupInfoALLCheck").click(function(){
		
		var checked = $("input:checkbox[name=GroupInfoALLCheck]").is(":checked");
		$("input:checkbox[name=NodeInfoCheck]").each(function(){
			$(this).prop("checked",checked);
		});
	});
   	//권한 추가
   	$("#btnGroupUserAdd").click(function(){
   		
   		addGroupDeptList();
   	});
   	
   	//조직 검색 전체 체크
	$("#OrganAreaUserALLCheck").click(function(){
		var checked = $("input:checkbox[name=OrganAreaUserALLCheck]").is(":checked");
		$("input:checkbox[name=OrganAreaUser]").each(function(){
			$(this).prop("checked",checked);
		});
	}); 
   	
   	//그룹 검색 전체 체크
	$("#GroupAreaUserALLCheck").click(function(){
		var checked = $("input:checkbox[name=GroupAreaUserALLCheck]").is(":checked");
		$("input:checkbox[name=GroupAreaUser]").each(function(){
			$(this).prop("checked",checked);
		});
	});
	
	
	//검색된 데이터 전부 체크
	$("#SearchAreaUserALLCheck").click(function(){
		var checked = $("input:checkbox[name=SearchAreaUserALLCheck]").is(":checked");
		$("input:checkbox[name=SearchAreaUser]").each(function(){
			$(this).prop("checked",checked);
		});
	});
	
	//검색
	$("#btnOrganSearch").click(function(){
		searchRightGroup();
	});
	
	// 그룹정보 검색
   	$("#S_data").keydown(function(key){
   		if (key.keyCode == 13) { // 엔터키
   			searchRightGroup();	
   		}
   	});	

   	$("input[name=searchTarget]").change(function() {
   		tabReset(2);
   	});
   	//동적할당된 문서함 목록 리스트의 문서함권한이 변경될때 체크해 문서권한을 변경해 준다
	$(document.body).delegate(
			'#InfoClassAcls',
			'change',
			function() {
				var selectContent = $(this).parent().parent().children().children("#InfoContentAcls");

				var selectOption ={
						select :2,
						start:0,
						end : ($(this).prop('selectedIndex')==0)?2:nodeContentAclList.length
				} 
					var innerHtml = renderAcls(nodeContentAclList,selectOption.select,selectOption.start,selectOption.end);
					selectContent.empty().append(innerHtml);
	});
   	//그룹원 추가
	//조직	
   	$("#tab_Btn_Organ").click(function(){
   		tabReset(0);
   	});
	//그룹
	$("#tab_Btn_Group").click(function(){
   		tabReset(1);
   	     initGroupTree();
   	});
	//검색
	$("#tab_Btn_Search").click(function(){
   		tabReset(2);
   	});
	
	initTree();
	initAclList();
   	initDeptTree();
});


//메인트리
var deptList ="";
var treeRootId = "";
var treeRootText = "";
var treeRootDeptText="";
var treeRootGroupText="";
var treeDeptRootId =""
var treeSelectId = "";
var treeSelectText = "";
var treeSelectCode = "";
var treeSelecDescpt = "";


//그룹문서함 트리
var treeRootId_GROUP = "";
var treeRootText_GROUP = "";
var treeSelectId_GROUP = "";
var treeSelectText_GROUP = "";
var treeSelectCode_GROUP = "";
var tabPosition = 0;
//전사트리
var groupList="";

//문서함 및 문서권한 사용
var nodeClassAclList=[];
var nodeContentAclList=[];

//서버에서 받아오는 초기 정보
var originalAcls=[];

//수정 추가 삭제시 temp 
var modFolderUserInfo = [];

//권한 목록 가져오기
var initAclList = function(){
	nodeContentAclList = sysCodeList("${ctxRoot}","07","${Authentication.objCompanyid}");	
	nodeClassAclList = sysCodeList("${ctxRoot}","06","${Authentication.objCompanyid}");
}

var initBtn = function(){
	$("#btnInit").show();
	$("#btnNewGroup").hide();
	$("#btnDel").text("<spring:eval expression="@${lang}['DELETE']"/>").hide();
	$("#btnDis").hide();

}


var treeRoot = function() {

	treeRootId = "N4"; // 
	treeRootDeptText=companyName;
	treeRootText =companyName; //"문서함"; //
	treeRootGroupText = "<spring:eval expression="@${lang}['GROUP']"/>";
	treeDeptRootId = "${Authentication.objCompanyid}";
	console.log("${Authentication.sessOnlyDeptUser.usertype}")
}
var initTree = function(){

	
	$('#znTree').remove();
	var treeHtml = "<ul id='znTree'></ul>";
	$('.Doc_Tree').append(treeHtml);
	
	$('#znTree').jstree({
		'plugins': ["state"],
			'core': {
			'data': [
				  {
				"id": treeRootId,
				"text": treeRootText,
				"icon": "${image}/jstree/tree_user_icon09.png", 			
				"state": {
					"opened": true,
					"disabled": false
					}
				,"li_attr": {},
				"a_attr": {"itemcode":"","itemname":"","itemtype":"N","itemdescpt":""}
			}  
			],
				'check_callback': true
		}
	});

			
	//트리 선택 이벤트
	$('#znTree').bind('select_node.jstree', function(event, data){
		AllReset();
		initBtn();
		
		var parentid = data.instance.get_node(data.selected).parent;				//부모아이디
		var parentname = data.instance.get_node(parentid).text;						//부모 명
		
		var selfid = data.instance.get_node(data.selected).id;
		var selfname = data.instance.get_node(data.selected).text;
		var item_code = data.instance.get_node(data.selected).a_attr.itemcode;
		var item_type = data.instance.get_node(data.selected).a_attr.itemtype;
		var item_isactive = data.instance.get_node(data.selected).a_attr.isactive;
		var item_descpt = data.instance.get_node(data.selected).a_attr.itemdescpt;
		
			treeSelectId = selfid;
			treeSelectText = selfname;
			treeSelectCode = item_code;
			treeSelecDescpt = item_descpt;
			if(treeSelectCode == ""){
				treeSelectCode = "N4";
			}else{
				AllReset();
			}
			if(item_isactive == 'N'){
				$("#btnInit").hide();
			}else{
				$("#btnInit").show();
			}
			if(item_type == "N"){
				$("#ClassName").val(treeSelectText);
				$("#ClassId").attr("disabled",true);
			}else 
			if(item_type == "D"){
				$("#ClassId").val(treeSelectCode);
				$("#ClassName").val(treeSelectText);
				if(treeSelectCode){
					$("#ClassId").attr("disabled",true);
				}
				selectFolderDetail(treeSelectId);//문서함(문서)권한 정보 조회
			}else{
				AllReset();
				initBtn();
				$("#btnNewGroup").show();
				
			}
		
			$("#ClassDescpt").val(item_descpt);
	});
		
	$('#znTree').on('ready.jstree', function () {
		selectClassList(); 
	  
	});
};
//문서문서함 조회
var selectClassList = function() {

	var sendData = {
			objIsTest : "N",
			companyid : "${Authentication.objCompanyid}",
			types : "N4",
			objIsMngMode : true
		}
	console.log("===sendData : ",sendData);
	// 조회
	$.ajax({
		url :"${ctxRoot}/api/classification/list/down"
		, type : "POST"
		, dataType : "json"
		, contentType : 'application/json'
		, async : false
		, data : JSON.stringify(sendData)
		, success : function(data){
			console.log("selectClassList : "+JSON.stringify(data));
			if(data.status == "0000") {
				if(objectIsEmpty(data.result)){
					return;
				}
				
				$.each(data.result, function(i, result) {		
					var obj = result.zappClassification;
					tree_parent_code = obj.upid;
					treeCode = obj.code;
					treeId = obj.classid;//
					treeText = obj.name;
					var descpt = obj.descpt;

					rootId = tree_parent_code; //(tree_parent_code == treeRootId)?"#":tree_parent_code;
					var icon = (obj.isactive == "Y") ? "${image}/jstree/tree_user_icon15.png": "${image}/icon/img_folder_off.png";
					createNode("#znTree",rootId, treeId, treeText, "last", icon, treeCode, treeText, "D", descpt,"Y",true);
					
				}); 
			}
		},  error : function(request, status, error) {
      alertNoty(request,status,error);
		}
	})
};

//문서문서함 조회
var selectDeptList = function() {
	
	var usrUpid = "${Authentication.sessDeptUser.zappDept.deptid}";
	if(${Authentication.sessOnlyDeptUser.usertype == '03'}){
		usrUpid = "${Authentication.objCompanyid}"; //
	}
	console.log("====usrUpid : "+usrUpid);
	var sendData = {
			companyid : "${Authentication.objCompanyid}",
			upid : usrUpid,
			objIsMngMode : true	
	}
	console.log("===sendData : ",sendData);
	// 조회
	$.ajax({
		url :"${ctxRoot}/api/organ/dept/list/down"
		, type : "POST"
		, dataType : "json"
		, contentType : 'application/json'
		, async : false
		, data : JSON.stringify(sendData)
		, success : function(data){
			console.log("selectDeptList : "+JSON.stringify(data));
			if(data.status == "0000") {
				if(objectIsEmpty(data.result)){
					return;
				}
				deptList = data;
				for(var i=0; i<data.result.length; i++) {
					tree_parent_code = deptList.result[i].upid;
					treeCode = deptList.result[i].code;
					treeId = deptList.result[i].deptid;//
					treeText = deptList.result[i].name;
		
					createNode("#znTreeDept",tree_parent_code, treeId, treeText, "last", "${image}/jstree/tree_user_icon09.png", treeCode, treeText, "N", "Y",true);
				}
			}
		},   error : function(request, status, error) {
      alertNoty(request,status,error);
		}
	})
};
//트리 초기화(부서)
var initDeptTree = function(){
 	$("#priority_tab").attr("style","display:none");
	$('#znTreeDept').remove();
	var treeDeptHtml = "<ul id='znTreeDept'></ul>";
	$('.Dept_Tree').append(treeDeptHtml);
	
	$('#znTreeDept').jstree({
		"checkbox" : {
			three_state : false,
			whole_node :  false,
			tie_selection : false
		    },		
		'plugins': ["state","checkbox"],
			'core': {
			'data': [{
				"id": treeDeptRootId,
				"text": treeRootDeptText,
				"icon": "${image}/jstree/tree_user_icon09.png", 			
				"state": {
					"opened": true,
					"disabled": false
					}
				,"li_attr": {},
				"a_attr": {"itemcode":"","itemname":"","itemtype":"N"
			           , "isactive":"Y",
			           class: "no_checkbox"}
			}],
				'check_callback': true
		}
	});
	

			
	//트리 선택 이벤트
	$('#znTreeDept').bind('select_node.jstree', function(event, data){

		var parentid = data.instance.get_node(data.selected).parent;				//부모아이디
		var parentname = data.instance.get_node(parentid).text;						//부모 명
		
		var selfid = data.instance.get_node(data.selected).id;
		var selfname = data.instance.get_node(data.selected).text;
		var item_code = data.instance.get_node(data.selected).a_attr.itemcode;
		
		selectDeptUserList("DEPT",selfid);//그룹정보 조회 정보
	});
		

	$('#znTreeDept').on('ready.jstree', function () {
		if(deptList){
			for(var i=0; i<deptList.result.length; i++) {
				tree_parent_code = deptList.result[i].upid;
				treeCode = deptList.result[i].code;
				treeId = deptList.result[i].deptid;//
				treeText = deptList.result[i].name;
	
				createNode("#znTreeDept",tree_parent_code, treeId, treeText, "last", "${image}/jstree/tree_user_icon09.png", treeCode, treeText, "N", "Y",true);
				
			}
		}else{ 
			selectDeptList(); 
		}
		
	});
};

//트리 초기화(부서)
var initGroupTree = function(){

	console.log("treeRootId : "+treeRootId);
	treeRootId_GROUP = "#GR";
	
	var initData =[];
	initData.push(initTreeData("01","<spring:eval expression="@${lang}['COMPANY_FOLDER_BOX']"/>"));
	initData.push(initTreeData("02","<spring:eval expression="@${lang}['DEPARTMENT_GROUP']"/>"));
	initData.push(initTreeData("04","<spring:eval expression="@${lang}['COLLABORATIVE_GROUP']"/>"));
	$('#znTreeGroup').jstree({
		"checkbox" : {
			three_state : false,
			whole_node :  false,
			tie_selection : false
		    },		
		'plugins': ["state","checkbox"],
		'core': {'data': initData,'check_callback': true}
	});
	

			
	//트리 선택 이벤트
	$('#znTreeGroup').bind('select_node.jstree', function(event, data){
		var parentid = data.instance.get_node(data.selected).parent;				//부모아이디
		var parentname = data.instance.get_node(parentid).text;						//부모 명
		
		var selfid = data.instance.get_node(data.selected).id;
		var selfname = data.instance.get_node(data.selected).text;
		var item_code = data.instance.get_node(data.selected).a_attr.itemcode;
		selectDeptUserList("GROUP",selfid);//그룹정보 조회 정보
		

	});
		

	$('#znTreeGroup').on('ready.jstree', function () {
		selectGroupLista("01");  //전사
		selectGroupLista("02");  //부서
		selectGroupLista("04");  //협업
		selectGroupLista("98");  //전체사용자 
	});
};

var initTreeData =function(type,name){
	return {
		"id": type,
		"text": name,
		"icon": "${image}/jstree/tree_user_icon09.png" , 			
		"state": {
			"opened": true,
			"disabled": false
			}
		,"li_attr": {},
		"a_attr": {"itemcode":type,"itemname":name,"itemtype":"N","itemisactive":"Y","itemroottype":type} 
	}
}
//트리 추가(부서) 공통사용 itemisactive는 없는경우 기본으로 Y로 넣어준다.
//itemtype는 없는경우에는 ""으로 처리한다
var createNode = function(tree_id,parent_code, new_id, new_text, position, icon, item_code, item_name,item_type,item_descpt,item_isactive, item_isopen){
	 $(tree_id).jstree (
							 'create_node'
							, parent_code
							, {	  "text"   : new_text
								, "id"     : new_id
								, "icon"   : icon
								, "state"  : { "opened": item_isopen }
								, "a_attr" : {   "itemcode":item_code
								          		,"itemname":item_name
								           		,"itemtype":item_type
								              	, "itemdescpt":item_descpt
								           		,"isactive":(item_isactive =="")?"Y":item_isactive
								         } 
							  }
							, position
							, false
							, false
				);
	
	
};

/*
 * name : tabReset
 * desc : tab(조직, 그룹, 검색)선택시 속성 및 초기화 진행
 * param : tabPosition (0 : 조직, 1 : 그룹, 2 : 검색)
 */
var tabReset =function(position){
	 tabPosition = position;
	 $("#S_data").val('');
	$("div[id$='Area']").each(function(){
		//console.log($(this).attr('id'));
		if(tabPosition ==0 && ($(this).attr('id')=='OrganArea')){
			$(this).attr("style","display:visible");
		}else if(tabPosition ==1 && ($(this).attr('id')=='GroupArea')){
			$(this).attr("style","display:visible");
		}else if(tabPosition ==2 && ($(this).attr('id')=='SearchArea')){
			$(this).attr("style","display:visible");
		}else{
			$(this).attr("style","display:none");
		}
		
	});
	$("a[id^='tab_']").each(function(){
		//console.log($(this).attr('id'));
		if(tabPosition ==0 && ($(this).attr('id')=='tab_Btn_Organ')){
			$(this).attr('class','current');
		}else if(tabPosition ==1 && ($(this).attr('id')=='tab_Btn_Group')){
			$(this).attr('class','current');
		}else if(tabPosition ==2 && ($(this).attr('id')=='tab_Btn_Search')){
			$(this).attr('class','current');
		}else{
			$(this).removeClass('current');
		}
	
	});
	
	
}
/*
 *NAME : chkNodeDiscard 
 *DESC : 삭제된 권한 정보를 temp에 저장한다. 
 *PARAM : 없음
 */
var chkNodeDiscard = function(){
	$("input:checkbox[name=NodeInfoCheck]").each(function(){		
		console.log("checked : "+$(this).is(":checked"));
		if($(this).is(":checked")){
			var parent = $(this).parent().parent();
			var classidx = parent.children().children("#InfoClassAcls").prop('selectedIndex');
			var contentidx = parent.children().children("#InfoContentAcls").prop('selectedIndex');
			var aclobjid =parent.children("#NodeInfoId").attr("aclobjid");
			var aclobjtype =  parent.children("#NodeInfoName").attr('aclobjtype');
			classidx += 1; // 조회불가 + 등록불가 (0) 이 빼짐으로 기본값에 1을 더 해준다.
			
				var index = -1;
				 for(var i=0; i<originalAcls.length; i++ ){
					 if((originalAcls[i].aclid ==  parent.children("#NodeInfoAcl").attr("aclid")) && (originalAcls[i].aclobjid == aclobjid)){
						 index = i;
						 break;
					 }
				 }
				console.log("index : "+index);
				
				//일치하는 데이터가 있으면 데이터를 전송하기전 임시로 modFolderUserInfo에 데이터를 저장해 둔다.
				 if(index> -1){
					 	
						 var sIndex = findItemIndex(modFolderUserInfo,aclobjid);
							if(sIndex> -1){ 
									modFolderUserInfo[sIndex].type="DISCARD";
						 	}else{
						 		 var disItem = cloneObject(originalAcls[index]);
								 disItem.classacl = classidx;
								 disItem.contentacl = contentidx;
								 disItem.type = "DISCARD";
								 modFolderUserInfo.push(disItem);
						 	}
							originalAcls.splice(index,1);
						 	parent.remove();
						 
				 }else{
						 var index = findItemIndex(modFolderUserInfo,aclobjid);
						console.log("findItemIndex : "+index);
						
						if(index> -1){ 
							if(modFolderUserInfo){
								if(modFolderUserInfo[index].type == 'ADD'){
									modFolderUserInfo.splice(index,1);
									parent.remove();
								}else if(modFolderUserInfo[index].type == 'CHANGE'){
										modFolderUserInfo[index].type = "DISCARD";
								}
							}
						 }
				 	}
				}
		});
	}
	
var findItemIndex = function(findItem, code){
	 var fIndex = -1;
	 for(var i=0; i<findItem.length; i++ ){
		   if(findItem[i].aclobjid == code){
			   fIndex = i;
			   break;
		   }
	 }
	
	return fIndex;
}
var chkNodeInfo = function(type){
	$("input:checkbox[name=NodeInfoCheck]").each(function(){
			var parent = $(this).parent().parent();
			var classidx = parent.children().children("#InfoClassAcls").prop('selectedIndex');
			var contentidx = parent.children().children("#InfoContentAcls").prop('selectedIndex');
			var aclobjid =parent.children("#NodeInfoId").attr("aclobjid");
			var aclobjtype =  parent.children("#NodeInfoName").attr('aclobjtype');
			classidx += 1; // 조회불가 + 등록불가 (0) 이 빼짐으로 기본값에 1을 더 해준다.
			 var index = -1;
			 for(var i=0; i<originalAcls.length; i++ ){
				 if((originalAcls[i].aclid ==  parent.children("#NodeInfoAcl").attr("aclid")) && (originalAcls[i].aclobjid == aclobjid)){
					index=i;
					break;
				 }
			 }
			 
			//일치하는 데이터가 있으면 데이터를 전송하기전 임시로 modFolderUserInfo에 데이터를 저장해 둔다.
			 if(index> -1){
				 
					 if(originalAcls[index].classacl != classidx || originalAcls[index].contentacl !=contentidx){					
						 originalAcls[index].classacl = classidx;
						 originalAcls[index].contentacl = contentidx;
						 var changeItem = cloneObject(originalAcls[index]);
						 changeItem.type =type;
						 
						 modFolderUserInfo.push(changeItem);
					 }else{
							 var index = findItemIndex(modFolderUserInfo,aclobjid);
							 if(index>-1){
								  modFolderUserInfo[index].classacl = classidx;
								  modFolderUserInfo[index].contentacl = contentidx;
							 }
					 }
			 }else{
				 var index = findItemIndex(modFolderUserInfo,aclobjid);
				 //console.log("modFolderUserInfo index : "+index);	
				 if(index>-1){
					  modFolderUserInfo[index].classacl = classidx;
					  modFolderUserInfo[index].contentacl = contentidx;
					  if(modFolderUserInfo[index].type != 'ADD'){
					  	modFolderUserInfo[index].type = type;
					  }
				 }
			 }
	});
}
var NodeNew = function(){	
	//true - 수정, 삭제, false - 신규
	if($("#ClassId").is(":disabled")){
		NodeMod();
	}else{
		NodeReg();
	}
}


//문서함정보 수정처리
var NodeMod = function(){
	if( $.trim($("#ClassName").val()) == ""){
	    alert("<spring:eval expression="@${msgLang}['ENTER_DOCUMENT_BOX_NAME']"/>");
	    $("#ClassName").focus();
	    return;
	}

	if( treeSelectId == ""){
	    //alert("선택된 문서함정보가 존재하지 않습니다.");
	    return;
	}
	
	chkNodeInfo("CHANGE");

	if(treeSelectText != $.trim($("#ClassName").val()) && modFolderUserInfo.length<=0){
		
		//문서함명만 봐꾸기 위해서 get으로 받은 데이터를 다시 서버로 던져준다(필수값때문에 사용)		
		if(confirm("<spring:eval expression="@${msgLang}['DO_YOU_DOCUMENT_BOX_MODIFY']"/>") == true){
			$.ajax({
				type : 'POST',
				url : '${ctxRoot}/api/classification/change/name',
				dataType : 'json',
				contentType : 'application/json',
				async : false,
				data : JSON.stringify({
					objIsTest : "N",
					objDebugged : false,
					classid : treeSelectId, //분류아이디
					name : $.trim($("#ClassName").val()),
					
				}),
				success : function(data){
					if(data.status == "0000"){				
						alert("<spring:eval expression="@${msgLang}['DOCUMENT_BOX_MODIFIED']"/>");
						AllReset();
						initBtn();
						$("#btnNewGroup").attr("style",'visibility:visible');
						initTree();
					}else{
						alertErr(data.message);
					}
				},   error : function(request, status, error) {
	        alertNoty(request,status,error);
			    }					
			});
		}
		
		
	}else{

 	if(modFolderUserInfo.length<=0  ){
 	  if(treeSelecDescpt == $.trim($("#ClassDescpt").val())){
		  alert("<spring:eval expression="@${msgLang}['NO_ALERTED_CONTENT']"/>");
		    return;
 	  }
	}
	
	
	var zappClassAcls=[];
	var zappContentAcls=[];
	
	if(modFolderUserInfo.length>0){
  	$.each(modFolderUserInfo,function(i,item){
  		if(item.type == "CHANGE"){
  			zappClassAcls.push({aclid :item.aclid, acls :item.classacl,objAction:item.type});
  			zappContentAcls.push({aclid :item.aclid, acls :item.contentacl, objAction:item.type});
  		}else if(item.type == "ADD"){
  			zappClassAcls.push({aclobjid :item.aclobjid, aclobjtype :item.aclobjtype, acls :item.classacl, objAction:item.type});
  			zappContentAcls.push({aclobjid :item.aclobjid, aclobjtype :item.aclobjtype, acls :item.contentacl, objAction:item.type});
  		}else if(item.type=="DISCARD"){
  			zappClassAcls.push({aclid :item.aclid,objAction:item.type});
  			zappContentAcls.push({aclid :item.aclid,objAction:item.type});
  		}
  	});
	}
	var sendData = {
			objIsTest : "N",
			objDebugged : false,
			classid : treeSelectId, //분류아이디
			types:"N4", //01:일반노드분류, N1:전사노드분류, N2:부서노드분류,N3:개인노드분류, N4:협업노드분류,	02:분류체계,03:문서유형
			name : $.trim($("#ClassName").val()),
			descpt:$.trim($("#ClassDescpt").val()),
			zappClassAcls:zappClassAcls, 
			zappContentAcls :zappContentAcls
		};
	console.log(JSON.stringify(sendData));
	
	if(confirm("<spring:eval expression="@${msgLang}['DO_YOU_DOCUMENT_BOX_REGISTER']"/>") == true){
		$.ajax({
			type : 'POST',
			url : '${ctxRoot}/api/classification/change',
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data){
				if(data.status == "0000"){
				
					alert("<spring:eval expression="@${msgLang}['DOCUMENT_BOX_REGISTERED']"/>");
					AllReset();
					initBtn();
					$("#btnNewGroup").attr("style",'visibility:visible');
					initTree();

				}else{
					alertErr(data.message);
				}
			},   error : function(request, status, error) {
        alertNoty(request,status,error);
		    }					
		});
	}
	}
}

//문서함정보 사용/미사용 처리  //objIncLower:"Y" 하위분류 삭제를 Y한 경우 오류 발생
var NodeDel = function(){
	
	var activeName =($("#ClassActive").text()=='<spring:eval expression="@${lang}['NOT_USE']"/>')?"enable":"disable";
	var msg = (activeName=='enable')?"<spring:eval expression="@${msgLang}['DOCUMENT_BOX_ENABLED']"/>":"<spring:eval expression="@${msgLang}['DOCUMENT_BOX_DISABLED']"/>";
	if(confirm(msg) == true){
		var sendData = {
				objIsTest : "N",
				objDebugged : false,
				classid : treeSelectId, //분류아이디
				objIncLower:(activeName=='enable')?"N":"Y"//하위 분류 disable 여부 (Y/N)
			};
		$.ajax({
			type : 'POST',
			url : '${ctxRoot}/api/classification/'+activeName,
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data){
				if(data.status == "0000"){
					alert("<spring:eval expression="@${msgLang}['DOCUMENT_BOX_CHANGED']"/>");
					AllReset();
					initBtn();
					$("#btnNewGroup").attr("style",'visibility:visible');
					initTree();
				}else{
					alertErr(data.message);
				}
			},    error : function(request, status, error) {
        alertNoty(request,status,error);
		    }					
		});
	}
}
//문서함정보 폐기처리
var NodeDis = function(){
	
	//'discard'
	//return;
	if(confirm("<spring:eval expression="@${msgLang}['ARE_YOU_DOCUMENT_BOX_INFO_DISCARD']"/>") == true){
		$.ajax({
			type : 'POST',
			url : '${ctxRoot}/api/classification/discard',
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify({
				objIsTest : "N",
				objDebugged : false,
				classid : treeSelectId, //분류아이디
			}),
			success : function(data){
				if(data.status == "0000"){
	
					alert("<spring:eval expression="@${msgLang}['DOCUMENT_BOX_DISCARDED']"/>");
					AllReset();
					initBtn();
					$("#btnNewGroup").attr("style",'visibility:visible');
					initTree();
				}else{
					alert(data.status);
				}
			},   error : function(request, status, error) {
        alertNoty(request,status,error);
		    }					
		});
	}
}
//문서함정보 삭제
var GroupNodeDel = function(){
	
	
	if(!chkAddNode()){ //체크된 항목이 없을 경우
		return;
	}
	chkNodeDiscard();
}

//부서 사용자 조회
var selectDeptUserList = function(treeSelectedId_Class,treeSelectId_Dept_Param) {
	
	if(treeSelectedId_Class == "DEPT"){
		$('#OrganAreaUserData').html("");
	}else {
		$('#GroupAreaUserData').html("");
	}

	var AreaUserData_dataHtml = "";
	var keyName = "";
	
	var sendData = {};
	
 	if(treeSelectedId_Class == "GROUP"){ 
		sendData.url = "${ctxRoot}/api/organ/group/get";
		sendData.data = {
				 "objIsTest" : "N",
				 "zappGroup" : { "groupid" : treeSelectId_Dept_Param }
			}
 	}else{
		sendData.url = "${ctxRoot}/api/organ/deptuser/list";
		sendData.data = {
			  	"deptid" : treeSelectId_Dept_Param,
				"isactive" : "Y"
			}
	} 
	// 조회
	$.ajax({
		url : sendData.url
		, type : "POST"
		, dataType : "json"
		, contentType : 'application/json'
		, async : false
		, data : JSON.stringify(sendData.data)
		, success : function(data){
			if(data.status == "0000") {
				if(objectIsEmpty(data.result)){
					return;
				}				
				console.log("data : "+JSON.stringify(data.result));
				var keyName = "OrganAreaUser";
				if(treeSelectedId_Class == "DEPT"){
					keyName = "OrganAreaUser";
					for(var i=0; i<data.result.length; i++) {
							AreaUserData_dataHtml += "<tr><td><input type=checkbox name="+keyName+" id="+keyName+" value='"+data.result[i].deptuserid+"' textData='"+data.result[i].zappUser.name+"' ></td><td>"+data.result[i].zappUser.name+"("+data.result[i].zappUser.empno+")</td></tr>";
					}
				}else {
					keyName = "GroupAreaUser";
					console.log("data.result.zappGroupUserExtends.length : "+data.result.zappGroupUserExtends.length);
					for(var i=0; i<data.result.zappGroupUserExtends.length; i++) {
						if(!objectIsEmpty(data.result.zappGroupUserExtends[i].zappUser.name)){
							AreaUserData_dataHtml += "<tr>";
							AreaUserData_dataHtml += "<td>";
							AreaUserData_dataHtml +="<input type=checkbox name="+keyName+" id="+keyName+" value='"+data.result.zappGroupUserExtends[i].groupuserid+"' textData='"+data.result.zappGroupUserExtends[i].zappUser.name+"' disabled>";
							AreaUserData_dataHtml +="</td>";
							AreaUserData_dataHtml +="<td>"+data.result.zappGroupUserExtends[i].zappUser.name+"("+data.result.zappGroupUserExtends[i].zappUser.empno+")</td>";
							AreaUserData_dataHtml +="</tr>";
						}else if(!objectIsEmpty(data.result.zappGroupUserExtends[i].zappDept.name)){
							AreaUserData_dataHtml += "<tr>";
							AreaUserData_dataHtml += "<td>";
							AreaUserData_dataHtml +="<input type=checkbox name="+keyName+" id="+keyName+" value='"+data.result.zappGroupUserExtends[i].groupuserid+"' textData='"+data.result.zappGroupUserExtends[i].zappDept.name+"' disabled>";
							AreaUserData_dataHtml +="</td>";
							AreaUserData_dataHtml +="<td>"+data.result.zappGroupUserExtends[i].zappDept.name+"</td>";
							AreaUserData_dataHtml +="</tr>";
						}
					}
						
				}
				
				
				
				if(treeSelectedId_Class == "DEPT"){
					$('#OrganAreaUserData').html(AreaUserData_dataHtml);
				}else if(treeSelectedId_Class == "GROUP"){
					$('#GroupAreaUserData').html(AreaUserData_dataHtml);
				}else{
					//$('#OrganAreaUserData').html("<tr><td colspan='2'></td></tr>");
				}
			}
		},   error : function(request, status, error) {
      alertNoty(request,status,error);
		}
	})
};

//문서함정보 조회 정보
var selectFolderDetail = function(scFolderClassid) {
	
	console.log("----selectFolderDetail :"+scFolderClassid);
		
	// 조회
	$.ajax({
		url :"${ctxRoot}/api/classification/get"
		, type : "POST"
		, dataType : "json"
		, contentType : 'application/json'
		, async : false
		, data : JSON.stringify({
			 "objIsTest" : "N",
			 "classid" : scFolderClassid 
		})
		, success : function(data){
			//alert("data ==> "    +JSON.stringify(data));
			console.log("data : "+JSON.stringify(data));
			
			originalAcls.length = 0;
				if(data.status == "0000") {
					//addNodeList.length = 0;
					if(objectIsEmpty(data.result)){
						$("#GroupUserData").html("");
						return;
					}
					var isClassActive = data.result.zappClassification.isactive;
				
					var NodeListInfo = data.result.zappUnionAcls;
				
					$.each(data.result.zappUnionAcls,function(i,item){
						
						var classAcl = 1; //접근불가는 제외여서 1부터 시작
						var contentAcl = 0;
						var $innerHtml ="";
						var $tr = $("<tr></tr>");
						var uniq = ""+i; 
						console.log(" i : "+i);
						var className = item[0].objname;
						var classType=(item[0].aclobjtype=="01")?"<spring:eval expression="@${lang}['USER']"/>":(item[0].aclobjtype=="02")?"<spring:eval expression="@${lang}['DEPARTMENT']"/>":"<spring:eval expression="@${lang}['GROUP']"/>";
						$innerHtml+="<td id='NodeInfoAcl' aclid='"+item[0].aclid+"'><input type='checkbox' name='NodeInfoCheck' id='NodeInfoCheck' /></td>";
						$innerHtml+="<td id='NodeInfoId' aclobjid='"+item[0].aclobjid+"'>"+className+"</td>";
						$innerHtml+="<td id='NodeInfoName' aclobjtype='"+item[0].aclobjtype+"'>"+classType+"</td>";
						
						var classItem = item.filter(function(value){
							return (!zChkString.fn_isEmpty(value.classid))
						});
						var selectClass ={
								select :(classItem[0].acls==0)?1:classItem[0].acls,
								start:1,
								end : nodeClassAclList.length
						} 
						$innerHtml+="<td ><select id='InfoClassAcls' style='width:100%'>"+renderAcls(nodeClassAclList,selectClass.select,selectClass.start,selectClass.end)+"</select></td>";
						
						var contentItem = item.filter(function(value){
							return (!zChkString.fn_isEmpty(value.contentid))
						});
						var contentClass ={
								select :contentItem[0].acls,
								start:0,
								end : (selectClass.select == 1)?2:nodeContentAclList.length
						} 
						
						$innerHtml+="<td><select id='InfoContentAcls'  style='width:100%'>"+renderAcls(nodeContentAclList,contentClass.select,contentClass.start,contentClass.end)+"</select></td>";
						
						//서버 값과 수정값 비교를 위해서 사용한다.
						originalAcls.push({
								aclid : item[0].aclid,
								aclobjid : item[0].aclobjid,
								aclobjtype : item[0].aclobjtype,
								classacl : selectClass.select,
								contentacl : contentClass.select,
								type : 'ORIGINAL',
				
								
						});
					
						 $tr.append($innerHtml);
						 $("#GroupUserData").append($tr);
					
					});
				
					activeBtn(isClassActive);
				}			
		},   error : function(request, status, error) {
      alertNoty(request,status,error);
		}
	})
};
/*
 *name : activeBtn 
 *desc : 문서함명, 문서함상태, 등록, 삭제, 폐기, 버튼을 상태값에 따라 활성화 비활성화 시켜준다
 *param : isClassActive(Y or N)
 */
 var activeBtn =function(isClassActive){
	$("#ClassName").attr("disabled",(isClassActive == 'N'));
	$("#ClassActive").text((isClassActive=='Y')?"<spring:eval expression="@${lang}['USE']"/>":"<spring:eval expression="@${lang}['NOT_USE']"/>");
	if(isClassActive == 'Y'){
		$("#btnNewGroup").show();
		$("#btnDel").text("<spring:eval expression="@${lang}['DELETE']"/>").show();
		$("#btnDis").hide();
	}else{
		$("#btnNewGroup").hide();
		$("#btnDel").text("<spring:eval expression="@${lang}['RESTORE']"/>").show();
		$("#btnDis").show();
	}
}

//그룹 조회
var selectGroupLista = function(type) {
		var sendData ={
			 "objIsTest" : "N",
			 "zappGroup" : { "upid" : "${Authentication.objCompanyid}"
				            ,"isactive" : "Y"
				            ,"types":type
				           }
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
				console.log("data : "+JSON.stringify(data));
				if(data.status == "0000") {
					if(objectIsEmpty(data.result)){
						return;
					}
					groupList = data;
					for(var i=0; i<data.result.length; i++) {
						tree_parent_code = data.result[i].upid;
						treeCode = data.result[i].code;
						treeId = data.result[i].groupid;//
						treeText = data.result[i].name;
						
						if(type == "98"){ //전체사용자그룹 최상위 루트가 없어서 #으로 최상위를 지정해준다
							createNode("#znTreeGroup","#", treeId, treeText, "last", "${image}/jstree/tree_user_icon15.png", treeCode, treeText, "N1", "Y",false);
						}else{
							createNode("#znTreeGroup","#"+type, treeId, treeText, "last", "${image}/jstree/tree_user_icon15.png", treeCode, treeText, "N1", "Y",false);
						}
					}
				}
			},   error : function(request, status, error) {
        alertNoty(request,status,error);
			}
		});
};


var rootCheck = function(className){
	var root = "";
	if(className == treeRootDeptText || className == treeRootGroupText || className == "<spring:eval expression="@${lang}['COMPANY_GROUP']"/>"){
		root = "root";
	}
	return root;
}

//권한 정보를 등록한다 (중복체크는 aclobjid값으로 처리한다.)
var addGroupDeptList = function(){
	 
	if(tabPosition == 0){
		var selectDeptNode = $("#znTreeDept").jstree('get_checked');
		//console.log("selectDeptNode length : "+selectDeptNode.length);
		$.each(selectDeptNode, function(i, item){
				if($("#znTreeDept").jstree(true)){
				var tObj = $("#znTreeDept").jstree(true).get_node(item);
				var className = tObj.text;
				if(rootCheck(className) != "root"){
				
					var uniq = tObj.id; //중복체크 확인을 위해서 사용한다.
					console.log("DUP : "+uniq);
					var classType = (tObj.a_attr.itemtype=="N")?"<spring:eval expression="@${lang}['DEPARTMENT']"/>":"<spring:eval expression="@${lang}['USER']"/>";
					if(dupCheck(uniq)!='DUP'){
						renderDeptGroupList(uniq,className,classType,nodeClassAclList,nodeContentAclList);
					}
				}
			}
		});
	}else if(tabPosition == 1){
		var selectGroupNode = $("#znTreeGroup").jstree('get_checked');
		$.each(selectGroupNode, function(i, item){
			if($("#znTreeGroup").jstree(true)){
				var tObj = $("#znTreeGroup").jstree(true).get_node(item);
				var className = tObj.text;
				if(rootCheck(className) != "root"){
				
					var uniq = tObj.id; //중복체크 확인을 위해서 사용한다.
					console.log("DUP : "+uniq);
					var classType = "<spring:eval expression="@${lang}['GROUP']"/>";
					if(dupCheck(uniq)!='DUP'){
						renderDeptGroupList(uniq,className,classType,nodeClassAclList,nodeContentAclList);
					}
				}
			}
		});
	}
	if(tabPosition == 0){
	//부서(조직) 사용자 목록에서 체크된 값이 있는 경우에 추가로 추가해준다.
	$("input:checkbox[name=OrganAreaUser]").each(function(){
			console.log("사용자 체크 : "+$(this).val());
			if($(this).is(":checked")){
				var uniq = $.trim($(this).val()); //중복체크 확인을 위해서 사용한다.
				var classType = "<spring:eval expression="@${lang}['USER']"/>";
				var className = $.trim($(this).attr("textData"));
				if(rootCheck(className) != "root"){
			
					if(dupCheck(uniq)!='DUP'){
						renderDeptGroupList(uniq,className,classType,nodeClassAclList,nodeContentAclList);
					}
				}
				
				
			}
		});	
	}else if(tabPosition == 1){
		//그룹 사용자 목록에서 체크된 값이 있는 경우에 추가로 추가해준다.
		$("input:checkbox[name=GroupAreaUser]").each(function(){
				if($(this).is(":checked")){
					var uniq = $.trim($(this).val()); //중복체크 확인을 위해서 사용한다.
					var classType = "<spring:eval expression="@${lang}['USER']"/>";
					var className = $.trim($(this).attr("textData"));
					if(rootCheck(className) != "root"){
					
						if(dupCheck(uniq)!='DUP'){
							renderDeptGroupList(uniq,className,classType,nodeClassAclList,nodeContentAclList);
						}
					}
					
					
				}
			});	
	}else if(tabPosition == 2){
		//검색 목록에서 체크된 값이 있는 경우에 추가로 추가해준다.
		$("input:checkbox[name=SearchAreaUser]").each(function(){
				if($(this).is(":checked")){
					var uniq = $.trim($(this).val()); //중복체크 확인을 위해서 사용한다.
					var classType = ($.trim(($(this).attr("valueType")))=='02')?"<spring:eval expression="@${lang}['DEPARTMENT']"/>":"<spring:eval expression="@${lang}['USER']"/>";
					var className = $.trim($(this).attr("textData"));
					if(rootCheck(className) != "root"){
				
						if(dupCheck(uniq)!='DUP'){
							renderDeptGroupList(uniq,className,classType,nodeClassAclList,nodeContentAclList);
						}
					}
					
				}
			});	
	}
}

/* 
Name : renderDeptGroupList
Desc : 권한 정보를 한줄로 보여준다.
Param :  aclobjid(uniqID), className(문서함 정보), classType(유형), classAcls(문서함 권한), contentAcls(사용자 권한)
*/
var renderDeptGroupList = function(aclobjid, className, classType, classAcls,contentAcls){
	var selectClass ={
			select :1,
			start:1,
			end : classAcls.length
	} 
	var contentClass ={
			select :2,
			start:0,
			end : 2
	} 
	var $innerHtml ="";
	var $tr = $("<tr></tr>");
	var type = (classType=='<spring:eval expression="@${lang}['USER']"/>')?'01':(classType=='<spring:eval expression="@${lang}['DEPARTMENT']"/>')?'02':'03';
	$innerHtml+="<td id='NodeInfoAcl' aclid=''><input type='checkbox' name='NodeInfoCheck' id='NodeInfoCheck' /></td>";
	$innerHtml+="<td id='NodeInfoId' aclobjid='"+aclobjid+"'>"+className+"</td>";
	$innerHtml+="<td id='NodeInfoName' aclobjtype='"+type+"'>"+classType+"</td>";
	$innerHtml+="<td ><select id='InfoClassAcls' style='width:100%'>"+renderAcls(classAcls,selectClass.select,selectClass.start,selectClass.end)+"</select></td>";
	$innerHtml+="<td><select id='InfoContentAcls'  style='width:100%'>"+renderAcls(contentAcls,contentClass.select,contentClass.start,contentClass.end)+"</select></td>";
	
	 var index = findItemIndex(modFolderUserInfo,aclobjid);
	 if(index>-1){
		  modFolderUserInfo[index].classacl = selectClass.select;
		  modFolderUserInfo[index].contentacl = contentClass.select;
		  modFolderUserInfo[index].type = "CHANGE";
	 }else{
		 var addItem ={
					aclid : '', 
					aclobjid : aclobjid,
					aclobjtype : type,
					classacl : selectClass.select,
					contentacl :contentClass.select,
					type : "ADD"
				};
		 modFolderUserInfo.push(addItem);
	 }
	 

	 
	 
	 
	$tr.append($innerHtml);
	$("#GroupUserData").append($tr);
} 

/* 
 Name : renderAcls
 Desc : 권한 정보 option을 리턴해준다.
 Param :  aclslist(권한 리스트), pos(선택된 권한이 있는경우에 초기값을 변경하기 위해서 사용)
 */
var renderAcls = function(aclslist, select, start,end){
	var optionHtml="";
	for(var i=0; i<aclslist.length; i++){
		var aclsInfo = aclslist[i];

		if(aclsInfo.codevalue>=start && end>=aclsInfo.codevalue){
		optionHtml+="<option value='"+aclsInfo.codevalue+"' "+(aclsInfo.codevalue==select?"selected":"")+">"+aclsInfo.name+"</option>";
		}
	}
	return optionHtml;
}

var dupCheck =function(code){
	if(code == '01' || code == '02' || code == '04'){ //전사, 부서, 협업의 root는 막아준다 (98 전체사용자는 사용가능)
		return chkNode = "DUP";
	}

	var chkNode="";
		$.each(originalAcls,function(i,item){
			if(item.aclobjid == code){
				return chkNode = "DUP";
			}
		});
		$.each(modFolderUserInfo,function(i,item){
			if(item.aclobjid == code && item.type != 'DISCARD'){
				 return chkNode = "DUP";
			}
		});
	return chkNode;
}

var chkAddNode = function(){
	var isChkNode = false;
	
		$("input:checkbox[name=NodeInfoCheck]").each(function(){
			if($(this).is(":checked")){
				return isChkNode = true;	
			}
		});
	return isChkNode;
}

/*
 * name : searchRightGroup
 * desc : 부서(DEPT) or 사용자(USER)를 검색한다.
 * param : 없음
 */
var searchRightGroup = function(){
	 var inputUserText = $.trim($("#S_data").val());
	 if( inputUserText == ""){
		    alert("<spring:eval expression="@${msgLang}['ENTER_TERM']"/>");
		    $("#S_data").focus();
		    return;
		}
	 
	 $("#SearchArea input:radio").each(function(){
		if($(this).is(":checked")){
			var selectVal = $.trim($(this).val());
	
			var sendUrl;
			var sendData = {};
			if (selectVal == 'DEPT') {
				sendUrl = '${ctxRoot}/api/organ/dept/lis';
				sendData.name = inputUserText;
			} else {
				sendUrl = '${ctxRoot}/api/organ/deptusers/list';	
				var zappUser = {};
				zappUser.name = inputUserText;
				sendData.zappUser = zappUser;
			}

			var SearchAreaUserData_dataHtml = "";
			
			$.ajax({
				type : 'POST',
				url : sendAjax.url,
				dataType : 'json',
				contentType : 'application/json',
				async : false,
				data : JSON.stringify(sendAjax.data),
				success : function(data){
					console.log("data : "+JSON.stringify(data));
					if(data.status == "0000"){
						if(objectIsEmpty(data.result)){
							return;
						}
						for(var i=0; i<data.result.length; i++) {
							
							if (selectVal == 'DEPT') {
								var rs_deptid = data.result[i].deptid;
								var rs_name = data.result[i].name;
								var gobjtype = "02";
							} else {
								var rs_deptid = data.result[i].userid;
								var rs_name = data.result[i].zappUser.name;
								var gobjtype = "01";
							}
							SearchAreaUserData_dataHtml += "<tr ><td><input type=checkbox name=SearchAreaUser id=SearchAreaUser value='"+rs_deptid+"' textData='"+rs_name+"' valueType='"+gobjtype+"' ></td><td>"+rs_name+"</td></tr>";
						}
					}else{
						alertErr(data.message);
					}
					$("#SearchAreaUserData").html(SearchAreaUserData_dataHtml);
				},   
				error : function(request, status, error) {
	        		alertNoty(request,status,error);
			    }					
			});			
			}
	 });
 }

	


//모든 입력 데이터 초기화
var AllReset = function(){
	$("#ClassId").val("").attr("disabled",false);
	$("#ClassName").val("").attr("disabled",false);
	$("#ClassDescpt").val("");
	$("#ClassActive").text('');
	
	
	if($("#znTreeDept").jstree(true)){
		$("#znTreeDept").jstree().uncheck_all(true);
	}

	if($("#znTreeGroup").jstree(true)){
		$("#znTreeGroup").jstree().uncheck_all(true);
	}
	
	$("input:checkbox[id$='ALLCheck']").each(function(){
		$(this).prop("checked",false);
		$(this).attr("disabled",false);
	});
	$("tbody[id$='AreaUserData']").each(function(){
		$(this).empty();
	});
	$("#GroupUserData").empty();
	
	$("#S_data").val('');
	originalAcls.length=0;
	//addNodeList.length = 0;

	modFolderUserInfo.length = 0;
}

//문서함정보 등록처리
var NodeReg = function(){
	
	if( $.trim($("#ClassId").val()) == ""){
	    alert("<spring:eval expression="@${msgLang}['ENTER_DOCUMENT_BOX_CODE']"/>");
	    $("#ClassId").focus();
	    return;
	}

	if( $.trim($("#ClassName").val()) == ""){
	    alert("<spring:eval expression="@${msgLang}['ENTER_DOCUMENT_BOX_NAME']"/>");
	    $("#ClassName").focus();
	    return;
	}


	if( treeSelectId == ""){
		treeSelectId = "N4";
	}
	chkNodeInfo("ADD");
	var zappClassAcls =[];
	var zappContentAcls =[];
	
	$.each(modFolderUserInfo,function(i,item){
		zappClassAcls.push({aclobjid :item.aclobjid, aclobjtype :item.aclobjtype, acls :item.classacl, objAction:item.type});
		zappContentAcls.push({aclobjid :item.aclobjid, aclobjtype :item.aclobjtype, acls :item.contentacl, objAction:item.type});
		 
	});
	var sendData = {
			objIsTest : "N",
			objDebugged : false,
			companyid : "${Authentication.objCompanyid}", //세션자동입력
			code : $.trim($("#ClassId").val()), //분류코드
			name : $.trim($("#ClassName").val()), //분류명칭
			decpt:$.trim($("#ClassDescpt").val()), //설명
			upid : treeSelectId, //부모분류아이디
			holderid : "${Authentication.sessOnlyDeptUser.deptuserid}", //세션자동입력
			types : "N4", //01 : 일반 노드, N1 : 전사노드, N2 : 부서 노드, N3 : 개인노드, N4 : 협업노드, 02 : 분류체계, 03 : 문서유형
			zappClassAcls:zappClassAcls,
			zappContentAcls:zappContentAcls
}
	if(confirm("<spring:eval expression="@${msgLang}['DO_YOU_DOCUMENT_BOX_REGISTER']"/>") == true){
		$.ajax({
			type : 'POST',
			url : '${ctxRoot}/api/classification/add',
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data){
				console.log("data : "+JSON.stringify(data));
				if(data.status == "0000"){
					alert("<spring:eval expression="@${msgLang}['DOCUMENT_BOX_REGISTERED']"/>");
					AllReset();
					initBtn();
					$("#btnNewGroup").attr("style",'visibility:visible');
					initTree();
				}else{
					alert(data.status);
				}
		
			},  error : function(request, status, error) {
        alertNoty(request,status,error);
		    }					
		});
	}
}

/*
 * name : tabReset
 * desc : tab(조직, 그룹, 검색)선택시 속성 및 초기화 진행
 * param : tabPosition (0 : 조직, 1 : 그룹, 2 : 검색)
 */
var tabReset =function(position){
	 tabPosition = position;
	 $("#S_data").val('');
	$("div[id$='Area']").each(function(){
		if(tabPosition ==0 && ($(this).attr('id')=='OrganArea')){
			$(this).attr("style","display:visible");
		}else if(tabPosition ==1 && ($(this).attr('id')=='GroupArea')){
			$(this).attr("style","display:visible");
		}else if(tabPosition ==2 && ($(this).attr('id')=='SearchArea')){
			$(this).attr("style","display:visible");
		}else{
			$(this).attr("style","display:none");
		}
		
	});
	$("a[id^='tab_']").each(function(){
		if(tabPosition ==0 && ($(this).attr('id')=='tab_Btn_Organ')){
			$(this).attr('class','current');
		}else if(tabPosition ==1 && ($(this).attr('id')=='tab_Btn_Group')){
			$(this).attr('class','current');
		}else if(tabPosition ==2 && ($(this).attr('id')=='tab_Btn_Search')){
			$(this).attr('class','current');
		}else{
			$(this).removeClass('current');
		}
	
	});
	
	
}
</script>
</head>
<body>
  <div id="wrap">
    <!--header stard-->
    <c:import url="../common/TopPage.jsp" />
    <!--header end-->

    <!--content stard-->
    <div id="container">
      <!--Left Menu stard-->
      <c:import url="../common/LeftMenu.jsp" />
      <!--Left Menu end-->

      <div class="sepage_r_admin">
        <div class="sepage_ttl">
          <img src="${ctxRoot}/resources/images/iconext/icon_arrow.png" style="margin-right: 15px;" /> <spring:eval expression="@${lang}['COLLABORATIVE_FOLDER_BOX']" />
        </div>

        <div class="Doc_Tree" style="left: 20px;">
          <ul id="znTree"></ul>
        </div>
        <div class="User_info">
          <div class="mgt5 cb">
            <div style="float: left; margin-top: 10px;">
              <img src="${ctxRoot}/resources/images/iconext/arrow_icon.png" /> <span style="font-weight: bold;"> <spring:eval expression="@${lang}['FOLDER_BOX_INFO']"/></span>
            </div>

            <div class="listpage_data" style="height: 25px;">
              <div class="fr">
                <a href="#" class="b_btn mgr5" id="btnInit"><spring:eval expression="@${lang}['INITIALIZATION']"/></a> 
                <a href="#" class="btn_dg mgr5" id="btnNewGroup"><spring:eval expression="@${lang}['CREATE']"/></a> 
                <a href="#" class="btn_dg mgr5" id="btnDel"><spring:eval expression="@${lang}['DELETE']"/></a> 
                <a href="#" class="btn_dg mgr5" id="btnDis"><spring:eval expression="@${lang}['DISCARD']"/></a>
              </div>
            </div>
          </div>
          <div style="height: 80px;">
            <table class="info_tb">
              <caption></caption>
              <colgroup>
                <col width="20%">
                <col width="30%">
                <col width="30%">
                <col width="20%">
              </colgroup>
              <thead>
                <tr>
                  <th><spring:eval expression="@${lang}['FOLDER_BOX_CODE']"/></th>
                  <th><spring:eval expression="@${lang}['FOLDER_BOX_NAME']"/></th>
                  <th><spring:eval expression="@${lang}['FOLDER_BOX_DESCRIPTION']"/></th>
                  <th><spring:eval expression="@${lang}['USE_OR_NOT']"/></th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>
                    <input type="text" value="" id="ClassId" style="width: 100%" maxlength="45">
                  </td>
                  <td>
                    <input type="text" value="" id="ClassName" style="width: 100%" maxlength="500">
                  </td>
                  <td>
                    <input type="text" value="" id="ClassDescpt" style="width: 100%" maxlength="500">
                  </td>
                  <td id="ClassActive"></td>

                </tr>
              </tbody>
            </table>
          </div>

          <div class="mgt5 cb" style="margin-top: 10px;">
            <div style="float: left; margin-top: 10px; height: 25px;">
              <img src="${ctxRoot}/resources/images/iconext/arrow_icon.png" /> <span style="font-weight: bold;"><spring:eval expression="@${lang}['FOLDER_BOX_AUTHORITY_INFO']"/> </span>
            </div>
            <div class="listpage_data" style="height: 25px;">
              <div class="fr">
                <a href="#" class="btn_dg" id="btnGroupModDetail">
                <spring:eval expression="@${lang}['DELETE_AUTHORITY_TARGET']"/>
                </a>
              </div>
            </div>

          </div>
          <div class="info_tb" style="height: 257px; overflow-y: auto;">

            <table style="width: 100%;">
              <caption></caption>
              <colgroup>
                <col width="4%">
                <col width="40%">
                <col width="15%">
                <col width=25%>
                <col width="15%">
                <!-- <col width="1%" style="min-width: 15px;"> -->
              </colgroup>
              <thead>
                <tr>
                  <th><input type="checkbox" name="GroupInfoALLCheck" id="GroupInfoALLCheck" /></th>
                  <th><spring:eval expression="@${lang}['AUTHORITY_TARGET']"/></th>
                  <th><spring:eval expression="@${lang}['AUTHORITY_TYPE']"/></th>
                  <th><spring:eval expression="@${lang}['FOLDER_BOX_AUTHORITY']"/></th>
                  <th><spring:eval expression="@${lang}['DOC_AUTHORITY']"/></th>
                <!-- <th>&nbsp;</th> -->  
                </tr>
              </thead>
              <tbody id="GroupUserData">

              </tbody>

            </table>

          </div>
          <div>
            <div style="margin-top: 20px;">
              <ul class="p_tab">
                <li><a id="tab_Btn_Organ" class="current" href="#"><spring:eval expression="@${lang}['ORGANIZATION']"/></a></li>
                <li><a id="tab_Btn_Group" href="#"><spring:eval expression="@${lang}['GROUP']"/></a></li>
                <li><a id="tab_Btn_Search" href="#"><spring:eval expression="@${lang}['SEARCH']"/></a></li>
                <li style="float: right;">
                <a href="#" id="btnGroupUserAdd" style="height: 24px; padding: 0 10px; line-height: 24px; background: #4f4f4f; border: #646464 1px solid; color: #fff; display: inline-block; border-radius: 2px; text-decoration: none; cursor: pointer; font-weight: bold;">
                <spring:eval expression="@${lang}['ADD_AUTHORITY_TARGET']"/>
                </a>&nbsp;&nbsp;
                </li>
              </ul>
            </div>
            <div style="border: 1px solid #d2d2d2; height: 240px; padding: 5px 5px;">
              <div id="OrganArea">
                <div class="Dept_Tree" style="float: left; width: 60%; height: 238px; overflow-y: auto; zoom: 1; border: 1px solid #d2d2d2; background-color: rgb(245, 245, 245);">
                  <ul id="znTreeDept"></ul>
                </div>
                <div class="info_tb" style="float: right; width: 39%; overflow-y: auto; height: 240px;">
                  <table style="width: 100%">
                    <caption></caption>
                    <colgroup>
                      <col width="10%">
                      <col width="90%">
                    </colgroup>
                    <thead>
                      <tr>
                        <th><input type=checkbox name=OrganAreaUserALLCheck id=OrganAreaUserALLCheck /></th>
                        <th><spring:eval expression="@${lang}['USER_NAME']"/></th>
                      </tr>
                    </thead>
                    <tbody id="OrganAreaUserData">
                    </tbody>
                  </table>
                </div>
              </div>
              <div id="GroupArea" style="display: none;">
                <div class="Group_Tree" style="float: left; width: 60%; height: 238px; overflow-y: auto; zoom: 1; border: 1px solid #d2d2d2; background-color: rgb(245, 245, 245);">
                  <ul id="znTreeGroup"></ul>
                </div>
                <div class="info_tb" style="float: right; width: 39%; overflow-y: auto; height: 240px;">
                  <table style="width: 100%">
                    <caption></caption>
                    <colgroup>
                      <col width="10%">
                      <col width="90%">
                    </colgroup>
                    <thead>
                      <tr>
                        <th><input type=checkbox disabled /></th>
                        <th><spring:eval expression="@${lang}['MEMBER']"/></th>
                      </tr>
                    </thead>
                    <tbody id="GroupAreaUserData">
                    </tbody>
                  </table>
                </div>
              </div>
              <div id="SearchArea" style="display: none;">
                <div style="float: left; width: 100%; height: 238px; border: 1px solid #d2d2d2;">
                  <div>
                    <table class="board_data">
                      <tr>
                        <th scope="col" style="width: 100px;"><spring:eval expression="@${lang}['TARGET']"/></th>
                        <td scope="col">
                          <input type="radio" value="DEPT" name="searchTarget" id="S_dept" checked />
                          <spring:eval expression="@${lang}['DEPARTMENT']"/>&nbsp;
                          <input type="radio" value="USER" name="searchTarget" id="S_user" />
                          <spring:eval expression="@${lang}['USER']"/>
                        </td>
                        <td scope="col" align="right">
                          <input type="text" value="" name="searchData" id="S_data" style="width: 180px;" />
                          &nbsp; <a href="#" class="b_btn" id="btnOrganSearch"><spring:eval expression="@${lang}['SEARCH']"/></a> &nbsp;&nbsp;&nbsp;&nbsp;
                        </td>
                      </tr>
                    </table>
                    <div class="info_tb" style="margin-top: 10px; height: 190px; overflow-y: auto;">
                      <table style="width: 100%;">
                        <caption></caption>
                        <colgroup>
                          <col width="10%">
                          <col width="88%">
                          <col width="2%">
                        </colgroup>
                        <thead>
                          <tr>
                            <th><input type=checkbox name="SearchAreaUserALLCheck" id="SearchAreaUserALLCheck" keydown /></th>
                            <th><spring:eval expression="@${lang}['AUTHORITY_TARGET']"/></th>
                            <th>&nbsp;</th>
                          </tr>
                        </thead>
                        <tbody id="SearchAreaUserData">
                        </tbody>
                      </table>
                    </div>
                  </div>

                </div>
              </div>
            </div>
          </div>
        </div>




      </div>
    </div>
    <!--Footer stard-->
    <c:import url="../common/Footer.jsp" />
    <!--Footer end-->
  </div>
</body>
</html>