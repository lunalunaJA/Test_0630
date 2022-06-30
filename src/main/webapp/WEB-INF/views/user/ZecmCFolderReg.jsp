<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${js}/common.js"></script>
<title>ECM4.0 :: 폴더 등록</title>
<script type="text/javascript">

  var tabPosition = 0;
  //전사트리
  var groupList = "";

  //문서함 및 문서권한 사용
  var nodeClassAclList = [];
  var nodeContentAclList = [];

  //서버에서 받아오는 초기 정보
  var originalAcls = [];

  //수정 추가 삭제시 temp 
  var modFolderUserInfo = [];


  /*
  name : initApprov
  desc : 승인그룹 정보를 가져온다.
  */
  var initApprov = function() {
    $("#ApprovalInfo").empty();

    var sendData = {
      "objIsTest" : "N",
      "zappGroup" : {
        "upid" : companyid,
        "types" : "05",
        "isactive" : "Y"
      }
    };

    console.log("===sendData : " + JSON.stringify(sendData));
    // 조회
    $.ajax({
      url : "${ctxRoot}/api/organ/group/list",
      type : "POST",
      dataType : "json",
      contentType : 'application/json',
      async : false,
      data : JSON.stringify(sendData),
      success : function(data) {

        console.log("data : ", data);
        if (data.status == "0000") {
          if (objectIsEmpty(data.result)) {
            return;
          }

          var $tr = $('<tr></tr>');
          var $td = $("<td></td>");
          var $select = $("<select style='min-width:90px; width: 90%;' id='ApprovalSelect'></select>");
          $select.append($("<option>" + "<spring:eval expression="@${lang}['UNCKECKED']"/>" + "</option>"));
          $.each(data.result, function(index, result) {
            var appCode = result.code;
            var appGroupId = result.groupid;//
            var appText = result.name;
            var isactive = result.isactive;
            var types = result.types;
            var priority = result.priority;
            console.log("=====index : ", index);
            $select.append($("<option data-meta='" + JSON.stringify(result) + "' value='" + appGroupId + "''>" + appText + "</option>"));

          });
          $select.bind("change", changeApprov);
          $td.append($select);
          $tr.append($td).append($("<td id='approvalID'></td>")).append($("<td id='approvalState'></td>"));
         
          $("#ApprovalInfo").append($tr);

        }
      },
      error : function(request, status, error) {
        alertNoty(request, status, error);
      }
    });
  }
  
  /*
  name : changeApprov
  desc : 승인권자 선택시 상세 정보 가져오기.
  */
  var changeApprov = function(e) {
    console.log("=== $(this)===", $(this));
    var value = $(this).val();
    var meta = $(this).find("option:selected").data("meta");
    if (!objectIsEmpty(meta)) {
      $("#AppovalGroup").show();
      console.log("====value : ", value);
      console.log("====subValue : ", meta.groupid);
 	 $("#approvalID").text(meta.code);
      
      var $select = $("<select style='min-width:90px; width: 90%;' id='ApprovalRequired'></select>");
      $select.append($("<option value='0'>" + "<spring:eval expression="@${lang}['APPROVAL0']"/>" + "</option>"));
      $select.append($("<option value='99'>" + "<spring:eval expression="@${lang}['APPROVAL99']"/>" + "</option>"));
      $("#approvalState").append($select);
      
      selectAppoval(meta.groupid);
    } else {
      $("#AppovalGroup").hide();
      $("#AppovalGroupUserData").empty()
    }
  }
  
  /*
  name : selectAppoval
  param : 그룹아이디
  desc : 선택된 승인그룹의 상세 정보를 가져온다.
  */
  var selectAppoval = function(scGroupid) {
    $("#AppovalGroupUserData").empty();

    $.ajax({
      url : "${ctxRoot}/api/organ/group/get",
      type : "POST",
      dataType : "json",
      contentType : 'application/json',
      async : false,
      data : JSON.stringify({
        "objIsTest" : "N",
        "zappGroup" : {
          "groupid" : scGroupid
        }
      }),
      success : function(data) {
        var GroupUserData_dataHtml = "";

        if (data.status == "0000") {
          if (objectIsEmpty(data.result)) {
            $("#AppovalGroupUserData").html("");
            return;
          }

          $.each(data.result.zappGroupUserExtends, function(index, result) {

            var $tr = $('<tr></tr>');
            var UserExtends = result;
            var groupuserid = UserExtends.groupuserid;
            var gobjtype = UserExtends.gobjtype;
            var groupid = UserExtends.gobjid;
            var editable = UserExtends.editable;
            var gobjseq = UserExtends.gobjseq;
            var groupname = "";

            if (gobjtype == "01") {
              gobjtypeStr = "<spring:eval expression="@${lang}['USER']"/>";
              groupname = UserExtends.zappUser.name;
            } else {
              gobjtypeStr = "<spring:eval expression="@${lang}['DEPARTMENT']"/>";
              groupname = UserExtends.zappDept.name;
            }

            var inHtml = "";
            inHtml += "<td>" + groupname + "</td>";
            inHtml += "<td>" + gobjtypeStr + "</td>";
            inHtml += "<td>" + gobjseq + "</td>";
            $tr.append(inHtml);
            $("#AppovalGroupUserData").append($tr);

          });

        }

      },
      error : function(request, status, error) {
        alertNoty(request, status, error);
      }
    })

  }

  
 
/*
 name : createNode
 param : treeid, 부모아이디(upid), 신규id, 노드이름, 추가될 순서, 표시될 아이콘, 코드, 이름, type, 상태 
 */
  var createNode = function(obj,parent_code, new_id, new_text, position, icon, item_code, item_name, item_type, item_isactive){
  	var rtnVal = $(obj).jstree (
  							  'create_node'
  							, parent_code
  							, {	  "text"   : new_text
  								, "id"     : new_id
  								, "icon"   : icon
  								, "state"  : { "opened": true }
  								, "a_attr" : {   "itemcode":item_code
  									           , "itemname":item_name
  									           , "itemtype":item_type
  									           , "itemisactive":item_isactive
  									          
  									         } 
  							  }
  							, position
  							, false
  							, false
  				);
  	
  };

  /*
 name  : selectDeptUserList
 desc : 선택된 부서의 사용자 정보를 가져온다.	
 */
 var selectDeptUserList = function(treeSelectId_Dept_Param) {
 	
 		$('#GroupAreaUserData').html("");

 	var AreaUserData_dataHtml = "";
 	var keyName = "";
 	
 	var sendData = {
 				 "objIsTest" : "N",
 				 "zappGroup" : { "groupid" : treeSelectId_Dept_Param }
 			}
  	
 	// 조회
 	$.ajax({
 		url : "${ctxRoot}/api/organ/group/get"
 		, type : "POST"
 		, dataType : "json"
 		, contentType : 'application/json'
 		, async : false
 		, data : JSON.stringify(sendData)
 		, success : function(data){
 		  console.log("===group get : ",data);
 			if(data.status == "0000") {
 				if(objectIsEmpty(data.result)){
 					return;
 				}				
 				var keyName = "OrganAreaUser";

 					keyName = "GroupAreaUser";
 					console.log("data.result.zappGroupUserExtends.length : "+data.result.zappGroupUserExtends.length);
 					for(var i=0; i<data.result.zappGroupUserExtends.length; i++) {
 						if(!objectIsEmpty(data.result.zappGroupUserExtends[i].zappUser.name)){
 							AreaUserData_dataHtml += "<tr>";
 							AreaUserData_dataHtml += "<td>";
 							AreaUserData_dataHtml +="<input type=checkbox name="+keyName+" id="+keyName+" value='"+data.result.zappGroupUserExtends[i].gobjid+"' textData='"+data.result.zappGroupUserExtends[i].zappUser.name+"' gobjtype='"+data.result.zappGroupUserExtends[i].gobjtype+"'>";
 							AreaUserData_dataHtml +="</td>";
 							AreaUserData_dataHtml +="<td>"+data.result.zappGroupUserExtends[i].zappUser.name+"("+data.result.zappGroupUserExtends[i].zappUser.empno+")</td>";
 							AreaUserData_dataHtml +="</tr>";
 						}else if(!objectIsEmpty(data.result.zappGroupUserExtends[i].zappDept.name)){
 							AreaUserData_dataHtml += "<tr>";
 							AreaUserData_dataHtml += "<td>";
 							AreaUserData_dataHtml +="<input type=checkbox name="+keyName+" id="+keyName+" value='"+data.result.zappGroupUserExtends[i].gobjid+"' textData='"+data.result.zappGroupUserExtends[i].zappDept.name+"' gobjtype='"+data.result.zappGroupUserExtends[i].gobjtype+"'>";
 							AreaUserData_dataHtml +="</td>";
 							AreaUserData_dataHtml +="<td>"+data.result.zappGroupUserExtends[i].zappDept.name+"</td>";
 							AreaUserData_dataHtml +="</tr>";
 						}
 					}

 					$('#GroupAreaUserData').html(AreaUserData_dataHtml);
 				
 			}
 		},   error : function(request, status, error) {
       alertNoty(request,status,error);
 		}
 	})
 };
  
 
//트리 초기화(그룹)
  var initGroupTree = function(){

  	$('#znTreeGroup').jstree({
  		"checkbox" : {
  			three_state : false,
  			whole_node :  false,
  			tie_selection : false
  		    },		
  		'plugins': ["state","checkbox"],
  			'core': {
  			'data': 
  			[
  			  {
  		  		"id": "root",
  		  		"text": "<spring:eval expression="@${lang}['COLLABORATIVE_GROUP']"/>",
  		  		"icon": POPTREE["COLLABOGROUP"] , 			
  		  		"state": {
  		  			"opened": true,
  		  			"disabled": false
  		  			}
  		  		,"li_attr": {},
  		  		"a_attr": {"itemcode":"04","itemname":"<spring:eval expression="@${lang}['COLLABORATIVE_GROUP']"/>","itemtype":"N","itemisactive":"Y","itemroottype":"04",class: "no_checkbox"} 
  		  	}
  				]
  		    ,
  				'check_callback': true
  		}
  	});
  	

  			
  	//트리 선택 이벤트
  	$('#znTreeGroup').bind('select_node.jstree', function(event, data){
  		var parentid = data.instance.get_node(data.selected).parent;				//부모아이디
  		var parentname = data.instance.get_node(parentid).text;						//부모 명
  		
  		var selfid = data.instance.get_node(data.selected).id;
  		var selfname = data.instance.get_node(data.selected).text;
  		var item_code = data.instance.get_node(data.selected).a_attr.itemcode;
  		
  		
  		selectDeptUserList(selfid);//그룹정보 조회 정보
  		

  	});
  		

  	$('#znTreeGroup').on('ready.jstree', function () {

  		selectGroupList();  //협업

  	});
  };
//그룹 조회
  var selectGroupList = function() {
  		var sendData ={
  			 "objIsTest" : "N",
  			 "zappGroup" : { "upid" : companyid
  				            ,"isactive" : "Y"
  				            ,"types":"04",
  				            
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
  				console.log("data : ",data);
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
  						
  						if(treeId == selectedUpid){	
  							createNode("#znTreeGroup","root", treeId, treeText, "last", POPTREE["COLLABO"], treeCode, treeText, "N1", "Y",false);
  				  			
  				  			if(rootCheck(treeText) != "root"){
  				  				var classType = "<spring:eval expression="@${lang}['GROUP']"/>";
  				  				renderDeptGroupList(treeId,treeText,classType,nodeClassAclList,nodeContentAclList);
  				  			}
  						}
  					}
  				}else{
  					alertErr(data.message);
  				}
  			},   error : function(request, status, error) {
          alertNoty(request,status,error);
  			}
  		});
  };
  
  var rootCheck = function(className){
  	var root = "";
  	if(className == "<spring:eval expression="@${lang}['COMPANY_GROUP']"/>"){
  		root = "root";
  	}
  	return root;
  }

  //권한 정보를 등록한다 (중복체크는 aclobjid값으로 처리한다.)
  var addGroupDeptList = function(){
  
  		var selectGroupNode = $("#znTreeGroup").jstree('get_checked');
  		$.each(selectGroupNode, function(i, item){
  			if($("#znTreeGroup").jstree(true)){
  				var tObj = $("#znTreeGroup").jstree(true).get_node(item);
  				var className = tObj.text;
  				if(rootCheck(className) != "root"){
  				  console.log("===znTreeGroup tObj : ",tObj);
  					var uniq = tObj.id; //중복체크 확인을 위해서 사용한다.
  					console.log("DUP : "+uniq);
  					var classType = "<spring:eval expression="@${lang}['GROUP']"/>";
  					if(dupCheck(uniq)!='DUP'){
  						renderDeptGroupList(uniq,className,classType,nodeClassAclList,nodeContentAclList);
  					}
  				}
  			}
  		});
  	
  		//그룹 사용자 목록에서 체크된 값이 있는 경우에 추가로 추가해준다.
  		$("input:checkbox[name=GroupAreaUser]").each(function(){
  				//console.log("사용자 체크 : "+$(this).val());
  				if($(this).is(":checked")){
  					var uniq = $.trim($(this).val()); //중복체크 확인을 위해서 사용한다.
  					 console.log("===GroupAreaUser tObj : ",$(this));
  					var classType = "<spring:eval expression="@${lang}['USER']"/>";
  					if($.trim($(this).attr("gobjtype")) =="02"){
  					  classType = "<spring:eval expression="@${lang}['DEPT']"/>";
  					}else if($.trim($(this).attr("gobjtype")) =="03"){
  					  classType = "<spring:eval expression="@${lang}['GROUP']"/>";
  					}
  					var className = $.trim($(this).attr("textData"));
  					if(rootCheck(className) != "root"){
  					
  						if(dupCheck(uniq)!='DUP'){
  							renderDeptGroupList(uniq,className,classType,nodeClassAclList,nodeContentAclList);
  						}
  					}
  					
  					
  				}
  			});	
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
  	if(code == '04'){ //전사, 부서, 협업의 root는 막아준다 (98 전체사용자는 사용가능)
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
  
//문서함정보 삭제
  var GroupNodeDel = function(){
  	
  	
  	if(!chkAddNode()){ //체크된 항목이 없을 경우
  		return;
  	}
  	chkNodeDiscard();
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
  	
  /*
   * name : NodeReg
   * desc : 폴더 생성
   */
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


  	chkNodeInfo("ADD");
  	var zappClassAcls =[];
  	var zappContentAcls =[];
  	
  	$.each(modFolderUserInfo,function(i,item){
  		zappClassAcls.push({aclobjid :item.aclobjid, aclobjtype :item.aclobjtype, acls :item.classacl, objAction:item.type});
  		zappContentAcls.push({aclobjid :item.aclobjid, aclobjtype :item.aclobjtype, acls :item.contentacl, objAction:item.type});
  		 
  	});
  	var wfid =""; //wfrequired = 0 ?"":groupid
  	var wfrequired = 0; //0:사용안함, 99 : ALL
      
  	var selectItem = $("#ApprovalRequired option:selected").val();
  	if(!objectIsEmpty(selectItem)){
  	  wfrequired = selectItem;
      if(wfrequired>0){
        var meta = $("#ApprovalSelect").find("option:selected").data("meta");
        if (!objectIsEmpty(meta)) {
          wfid = meta.groupid;
        } 
      }
  	}
      console.log("====wfrequired : ",wfrequired);
  	var sendData = {
  			objIsTest : "N",
  			objDebugged : false,
  			companyid : companyid, //세션자동입력
  			code : $.trim($("#ClassId").val()), //분류코드
  			name : $.trim($("#ClassName").val()), //분류명칭
  			descpt:$.trim($("#ClassDescpt").val()), //설명
  			upid : selectNodeId(selectedClsid) , //부모분류아이디
  			holderid : deptuserid, //세션자동입력
  			types : selectedClsType, //01 : 일반 노드, N1 : 전사노드, N2 : 부서 노드, N3 : 개인노드, N4 : 협업노드, 02 : 분류체계, 03 : 문서유형
  			zappClassAcls:zappClassAcls,
  			zappContentAcls:zappContentAcls,
  			wfid:wfid,
  			affiliationid:selectNodeId(selectedUpid),
  			wfrequired:wfrequired
  }
  	console.log("=====add sendData ",sendData);
  	
  	noty({
	    layout : "center",
	    text : "<spring:eval expression="@${msgLang}['DO_YOU_DOCUMENT_BOX_REGISTER']"/>",
	    buttons : [ {
	      addClass : 'b_btn',
	      text : "Ok",
	      onClick : function($noty) {
	        $noty.close();
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
	    					folderRegClose();
	    					fn_fldTree.clickNode(selectedClsid);
	    				}else{
	    					alertErr(data.message);
	    				}
	    		
	    			},  error : function(request, status, error) {
	            alertNoty(request,status,error);
	    		    }					
	    		});
	      }
	    }, {
	      addClass : 'btn-danger',
	      text : "Cancel",
	      onClick : function($noty) {
	        $noty.close();
	      }
	    } ],
	    type : "information",
	    killer : true
	  });
  }

   
 //권한 목록 가져오기
   var initAclList = function(){
   	nodeContentAclList = sysCodeList("${ctxRoot}","07","${Authentication.objCompanyid}");	
   	nodeClassAclList = sysCodeList("${ctxRoot}","06","${Authentication.objCompanyid}");
   }
  $(document).ready(function() {

    console.log("=====folderReg====");

    //treeRoot();

    $("#Reg_Tab1").click(function() {
      $('a[id^=Reg_Tab]').removeClass('current');
      $('div[id^=Div_Reg]').hide();
      $("#Reg_Tab1").addClass('current');
      $("#Div_Reg1").show();
    });

    $("#Reg_Tab2").click(function() {
      $('a[id^=Reg_Tab]').removeClass('current');
      $('div[id^=Div_Reg]').hide();
      $("#Reg_Tab2").addClass('current');
      $("#Div_Reg2").show();

    });

    //권한 삭제
    $("#btnGroupModDetail").click(function() {
      GroupNodeDel();
    });

    //권한 정보 체크박스(전체 체크)
    $("#GroupInfoALLCheck").click(function() {

      var checked = $("input:checkbox[name=GroupInfoALLCheck]").is(":checked");
      $("input:checkbox[name=NodeInfoCheck]").each(function() {
        $(this).prop("checked", checked);
      });
    });
    //권한 추가
    $("#btnGroupUserAdd").click(function() {

      addGroupDeptList();
    });


    //그룹 검색 전체 체크
    $("#GroupAreaUserALLCheck").click(function() {
      var checked = $("input:checkbox[name=GroupAreaUserALLCheck]").is(":checked");
      $("input:checkbox[name=GroupAreaUser]").each(function() {
        $(this).prop("checked", checked);
      });
    });




    //동적할당된 문서함 목록 리스트의 문서함권한이 변경될때 체크해 문서권한을 변경해 준다
    $(document.body).delegate('#InfoClassAcls', 'change', function() {
      var selectContent = $(this).parent().parent().children().children("#InfoContentAcls");

      var selectOption = {
        select : 2,
        start : 0,
        end : ($(this).prop('selectedIndex') == 0) ? 2 : nodeContentAclList.length
      }
      var innerHtml = renderAcls(nodeContentAclList, selectOption.select, selectOption.start, selectOption.end);
      selectContent.empty().append(innerHtml);
    });

    initApprov();
    initAclList();
    initGroupTree();

  });
</script>
</head>
<body>
<div class="popup" style="display: block;">
		<h3 class="pageTit">폴더등록</h3>
		<button type="button" id="closeBtn"><img src="${image}/icon/x.png"></button>
		<ul class="tabmenu">
			<li class="on">기본 정보</li>
			<li>권한 설정</li>
		</ul>
		<div class="tabCont">
			<div class="contdiv" id = "cont01">
				<h3 class="innerTit"><span id="SelectText"><spring:eval expression="@${lang}['FOLDER_INFORMATION']" /></span></h3>
				<div>
					<table class="pop_tbl">
						<caption></caption>
						<colgroup>
							<col width="20%">
							<col width="30%">
							<col width="30%">
						</colgroup>
						<thead>
							<tr>
								<th><spring:eval expression="@${lang}['FOLDER_CODE']" /></th>
								<th><spring:eval expression="@${lang}['FOLDER_NAME']" /></th>
								<th><spring:eval expression="@${lang}['FOLDER_DESCRIPTION']" /></th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td><input type="text" value="" id="ClassId" style="width: 100%" maxlength="45"></td>
								<td><input type="text" value="" id="ClassName" style="width: 100%" maxlength="500"></td>
								<td><input type="text" value="" id="ClassDescpt" style="width: 100%" maxlength="500"></td>
							</tr>
						</tbody>
					</table>
				</div>
				<h3 class="innerTit"><span id="SelectText"><spring:eval expression="@${lang}['APPROVAL_INFO']" /></span></h3>
				<!-- 첨부 파일 -->
				<div>
					<table class="pop_tbl">
						<colgroup>
							<col width="34%">
							<col width="33%">
							<col width="33%">
						</colgroup>
						<thead>
							<tr>
								<th><spring:eval expression="@${lang}['APPROVAL_GROUP']" /></th>
								<th><spring:eval expression="@${lang}['CODE_VALUE']" /></th>
								<th><spring:eval expression="@${lang}['FOLDER_APPROVAL_DESC']" /></th>
							</tr>
						</thead>
						<tbody id="ApprovalInfo">
						</tbody>
					</table>
				</div>
				<div>
					<table class="pop_tbl">
						<colgroup>
							<col width="34%">
							<col width="33%">
							<col width="33%">
						</colgroup>
						<thead>
							<tr>
								<th><spring:eval expression="@${lang}['GROUP_MEMBER']" /></th>
								<th><spring:eval expression="@${lang}['GROUP_TYPE']" /></th>
								<th><spring:eval expression="@${lang}['APPROVAL_LEVEL']" /></th>
							</tr>
						</thead>
						<tbody id="AppovalGroupUserData">
						
						</tbody>
					</table>
				</div>
			</div>
			<!--cont01//-->
			<div class="contdiv" id = "cont02_1">
				<div>
					<h3 class="innerTit">
						<span style="font-weight: bold;"><spring:eval expression="@${lang}['ABOUT_FOLDER_PERMISSIONS']" /> </span>
						<a href="#" class="btn_dg" id="btnGroupModDetail" style="float:right; height: 24px; padding: 0 10px; line-height: 24px; background: #4f4f4f; border: #646464 1px solid; color: #fff; display: inline-block; border-radius: 2px; text-decoration: none; cursor: pointer; font-weight: bold;"> <spring:eval expression="@${lang}['DELETE_AUTHORITY_TARGET']" /></a>
					</h3>
					<div style='overflow: auto; margin:0px; height: 130px;'>
						<table class="pop_tbl">
							<colgroup>
								<col width="4%">
								<col width="40%">
								<col width="15%">
								<col width=25%>
								<col width="15%">
							</colgroup>
							<thead>
								<tr>
									<th><input type="checkbox" name="GroupInfoALLCheck" id="GroupInfoALLCheck" /></th>
									<th><spring:eval expression="@${lang}['AUTHORITY_TARGET']" /></th>
									<th><spring:eval expression="@${lang}['AUTHORITY_TYPE']" /></th>
									<th><spring:eval expression="@${lang}['FOLDER_AUTHORITY']" /></th>
									<th><spring:eval expression="@${lang}['DOC_AUTHORITY']" /></th>
								</tr>
							</thead>
							<tbody id="GroupUserData">
							</tbody>
						</table>
					</div>
					<div>
						<div style="margin:0px; margin-top: 30px;">
							<ul class="tabmenu2">
								<li id ="tab_Btn_Group"><spring:eval expression="@${lang}['GROUP']" /></a></li>
							</ul>
							<a href="#" id="btnGroupUserAdd" style="float:right; height: 24px; padding: 0 10px; line-height: 24px; background: #4f4f4f; border: #646464 1px solid; color: #fff; display: inline-block; border-radius: 2px; text-decoration: none; cursor: pointer; font-weight: bold;">
											<spring:eval expression="@${lang}['ADD_AUTHORITY_TARGET']" /></a>
						</div>
						<div class="tabCont2">
							<div id="GroupArea" style="display:block;">
								<div class="Group_Tree" style="float: left; width: 60%; height: 185px; overflow-y: auto; zoom: 1; border: 1px solid #d2d2d2; background-color: #CCCCCC; margin: 0px;">
									<ul id="znTreeGroup"></ul>
								</div>
								<div class="info_tb" style="float: right; width: 39%; overflow-y: auto; height: 183px;">
									<table class="pop_tbl">
										<caption></caption>
										<colgroup>
											<col width="10%">
											<col width="90%">
										</colgroup>
										<thead>
											<tr>
												<th><input type=checkbox id="" disabled /><label></label></th>
												<th><spring:eval expression="@${lang}['MEMBER']" /></th>
											</tr>
										</thead>
										<tbody id="GroupAreaUserData">
										</tbody>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<button type="button" style="position:relative; left:45%; margin-top: 25px; margin-bottom: -15px;" class="btbase" onclick="javascript:NodeReg();">저장</button>
	</div>
</body>
</html>