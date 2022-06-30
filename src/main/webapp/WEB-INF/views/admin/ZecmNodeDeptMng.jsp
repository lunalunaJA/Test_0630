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
 
$(document).ready(function() {
	//버튼 숨기기
	initBtn();
	//폴더 이동버튼 클릭 이벤트
	$("#btnMove").click(function() {
		$('.bg').fadeIn();
		var $layerPopupObj = $('#docmodal');
		var left = ($(window).scrollLeft() + ($(window).width() - $layerPopupObj.width()) / 2);
		var top = ($(window).scrollTop() + ($(window).height() - $layerPopupObj.height()) / 2);
		//console.log("left : "+left+", top : "+top);
		$layerPopupObj.css({
			'left' : left,
			'top' : top,
			'position' : 'absolute'
		});

		$("#NodeMoveArea").show();
		$("#docmodal").draggable({
			containment : 'body',
			scroll : false
		});
		$("#docmodal").css('z-index', modalZIndex + 10);
		modalZIndex = modalZIndex + 10;

		//initTreeObj("znTreeMove1", "MOVE_Tree1");
		fn_move.jstree_dept();
	});
	//폴더이동 저장버튼 클릭 이벤트
	$("#btnMoveSave").click(function() {
	//znTreeMove1 MOVE_Tree1, znTreeMove2 MOVE_Tree2
		console.log("====fn_tree : " + fn_tree.select.classid);
		console.log("====fn_move : " + fn_move.select.id);
		if (fn_tree.select.classid == fn_move.select.id) {
			alert("<spring:eval expression="@${msgLang}['MOVE_SAME_LOCATION']"/>");
			return;
		}
		var message = "<spring:eval expression="@${msgLang}['DO_YOU_MOVE_FOLDER']"/>"//deptTree.selectText+" 폴더를 "+deptTree.popupText+"로 이동하시겠습니까?";
		noty({
			layout : "center",
			text : message,
			buttons : [ {
				addClass : 'b_btn',
				text : "Ok",
				onClick : function($noty) {
					$noty.close();
					$('.bg').fadeOut();
					fn_move.relocateNodeInfo();
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

	});
	//폴더이동 닫기 클릭 이벤트
	$("#btnMoveClose").click(function() {
		fn_move.moveItem = {};
		$(".bg").fadeOut();
		$("#NodeMoveArea").hide();
	});
	
    // 입력 초기화
	$("#btnInit").click(function() {
		AllReset();
		fn_tree.type= "ADD";
		$("#btnNewGroup").show();
		$("#btnDel").text("<spring:eval expression="@${lang}['DELETE']"/>").hide();
		$("#btnDis").hide();
    });
    
    // 저장 (등록, 수정)
	$("#btnNewGroup").click(function() {
		/* if(objectIsEmpty(fn_tree.select.node)){ */
		if(fn_tree.type == "ADD"){
			NodeReg();
		}else{
			NodeMod();
		}
	});

	// 폴더정보  삭제(미사용으로 변경)
	$("#btnDel").click(function() {
		NodeDel();
	});
	//폴더정보 폐기(실제 삭제)
	$("#btnDis").click(function() {
		NodeDis();
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
	$("#OrganAreaUserALLCheck").click(function() {
		var checked = $("input:checkbox[name=OrganAreaUserALLCheck]").is(":checked");
		$("input:checkbox[name=OrganAreaUser]").each(function() {
			$(this).prop("checked", checked);
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
	$("#btnOrganSearch").click(function() {
		searchRightGroup();
	});

	// 그룹정보 검색
	$("#S_data").keydown(function(key) {
		if (key.keyCode == 13) { // 엔터키
			searchRightGroup();
		}
	});
	
	$("input[name=searchTarget]").change(function() {
		tabReset(2);
	});
	//동적할당된 폴더 목록 리스트의 폴더권한이 변경될때 체크해 문서권한을 변경해 준다
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
    //그룹원 추가

	$("#tab_Btn_Organ").click(function() {
		tabReset(0);
	});
	//그룹
	$("#tab_Btn_Group").click(function() {
		tabReset(1);
	});
	//검색
	$("#tab_Btn_Search").click(function() {
		tabReset(2);
	});
    
	initAclList();
	initApprov();
	fn_tree.jstree();
	fn_dept.jstree();
	fn_group.jstree();
    
	$('.addGroupuser .tabmenu li').click(function(){
		var tabNum = $(this).index();
		console.log(tabNum);

		$(this).siblings().removeClass('on');
		$(this).addClass('on');

		$('.tabCont .contdiv').css('display','none');
		$('.tabCont .contdiv').eq(tabNum).css('display','block')
	});
	
	$('#closeBtn').unbind("click").bind("click", function(){
		fn_move.moveItem = {};
		$('.bg').fadeOut();
		$('#NodeMoveArea').fadeOut();
    });
    $("img[id=reorder]").click(function() {

        var action = $(this).attr("name");
        var priority = $("#znTree").jstree("get_node", fn_tree.select.classid).a_attr.priority;
        
        //console.log("priority : " + priority);
        
        if (action == 'up') {
          priority = priority - 1;
        } else if (action == 'down') {
          priority = priority + 1;
        }
        
        //console.log("reorder priority : " + priority);
        
     	reorder(priority);
     });

});

var modalZIndex = 100;
var relocate = {};
var selectClassText = "";
var selectWfid = "";
var selectWfrequired = 0;
var tabPosition = 0;
//폴더 및 문서권한 사용
var nodeClassAclList = [];
var nodeContentAclList = [];

//서버에서 받아오는 초기 정보
var originalAcls = [];

//수정 추가 삭제시 temp 
var modFolderUserInfo = [];

//권한 목록 가져오기
var initAclList = function() {
	nodeContentAclList = sysCodeList("${ctxRoot}", "07", "${Authentication.objCompanyid}");
	nodeClassAclList = sysCodeList("${ctxRoot}", "06", "${Authentication.objCompanyid}");
}

var initBtn = function() {
	$("#btnNewGroup").hide();
	$("#btnDel").text("<spring:eval expression="@${lang}['DELETE']"/>").hide();
	$("#btnDis").hide();
}

/*********************************
Name   : fn_tree
Desc   : 부서 폴더 트리
Param  : 없음
**********************************/
var fn_tree = {
	treeId : "znTree",
	id : (userType == USERTYPES["COMPANY"]) ? companyid : deptid,
	name : "<spring:eval expression="@${lang}['DEPARTMENT_FOLDER_BOX']"/>",
	type:"ADD",
	item : {},
	arroval : {},
	select : {},
	$tree : {},
	jstree : function() {
		console.log("====init jstree");
		var tree = this;
		tree.$tree = $("#" + tree.treeId);
		tree.$tree.jstree({
			'plugins' : ["state", "unique", "dnd"],
			'core' : {
				'data' : [],
				'check_callback' : function (op, node, parent, position, more) {
					var np = node.parent;
					var pid = parent.id;
					if (op == 'move_node' && more.ref == undefined) {
						if(np != pid){
							return false;
						}else{
							console.log(parent.a_attr.priority);
							reorderNodeInfo(parent.a_attr.priority);
							renderPriority(parent);
						}
					}
				}
			},
			sort : function(a, b) {
				var a1 = this.get_node(a);
				var b1 = this.get_node(b);
				if (sortType === "asc") {
					return (a1.a_attr.priority > b1.a_attr.priority) ? 1 : -1;
				} else {
					return (a1.a_attr.priority > b1.a_attr.priority) ? -1 : 1;
				}
			}
		}).on("select_node.jstree", function(event, data) {
		// tree.getFolderList(tree.select.upid);
			AllReset();
			initBtn();
			console.log("====event : ",event.target.id);
			console.log("====data : ",data.node.id);
			var item = data.instance.get_node(data.selected);
			var parentid = item.parent; //부모아이디
			var parentname = data.instance.get_node(parentid).text; //부모 명
			console.log("====select item : ", item);
			var selfid = item.id;
			var selfname = item.text;
			tree.select = data;
			tree.type= "UPDATE";
			console.log("=====tree.select : ",tree.select);
			console.log("====selected type : ", item.a_attr.type);
			if (item.a_attr.type == "D") {
				$("#btnMove").hide();
				$("#priority_tab").hide();
				$("#folderPriority").children('option').remove();
	          
				$("#ClassId").val(data.node.a_attr.code).attr("disabled", true);
				$("#ClassName").val(data.node.text).attr("disabled", true);
				$("#ClassDescpt").attr("disabled", true);
				tree.getFolderList(item.id);
			} else if (item.a_attr.type == "F") {
				if (parentid == "#") {
						$("#btnMove").hide();
				} else {
					$("#btnMove").show();
				}
	
				var orderList = data.instance.get_node(parentid);
				renderPriority(orderList);
				tree.getFolderDetail(item.id);
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
	
		}).on('ready.jstree', function() {
			console.log("====ready====");
			tree.getDeptList();
		});

	},
	createDeptNode : function(item) {
		var tree = this;
		tree.$tree.jstree('create_node', (item.upid == companyid) ? "#" : item.upid, {
			"text" : item.name,
			"id" : item.deptid,
			"icon" : (item.isactive == "Y") ? item.icon : TREEICONS["N"],
			"state" : {
				"opened" : false
			},
			"a_attr" : {
				"code" : item.code,
				"priority" : item.priority,
				"descpt" : item.descpt,
				"isactive" : item.isactive,
				"type" : "D"
			}
		}, "last", false, false);

	},
	createFolderNode : function(item) {
		var tree = this;
		tree.$tree.jstree('create_node', item.upid, {
			"text" : item.name,
			"id" : item.classid,
			"icon" : (item.isactive == "Y") ? item.icon : TREEICONS["N"],
			"state" : {
				"opened" : false
			},
			"a_attr" : {
				"code" : item.code,
				"priority" : item.priority,
				"descpt" : item.descpt,
				"isactive" : item.isactive,
				"descpt":item.descpt,
				"type" : "F"
			}
		}, "last", false, false);
	},removeNode : function(id) { 
		this.$tree.jstree(true).delete_node(id); 

	},refresh : function(selectID) {
		this.$tree.jstree(true).refresh(); 
		if(selectID){
			this.getNode(selectID);
		}
    
	},getCoreData : function(){
		return this.$tree.jstree(true).settings.core.data;
	},
	setCoreData : function(item){
		this.$tree.jstree(true).settings.core.data = item;
		this.$tree.jstree(true).refresh();
	},
	getData : function() {
		var newData = []; 
		var jdata = this.$tree.jstree(true).get_json("#", {flat:true}); 
		for (var i=0; i<jdata.length; i++) { 
			var current = jdata[i]; 
			var id = current.id; 
			var data = this.findCoreData(id); 
			if (data) { 
				newData.push($.extend(data, current)); 
			} 
		} 
		return newData; 
	},
	findCoreData : function(id) { 
		var coreData = this.$tree.jstree(true).settings.core.data; 
		for (var i=0; i<coreData.length; i++) {
			var data = coreData[i]; 
			if (data.id == id) {
				return data;
			}
		}
		return null; 
	},
	updateCoreData : function(id){
		var jData = this.$tree.jstree(true).get_json('#', {flat:true});
		$.each(jData,function(i, item){
			if(item.id == id){
				console.log("====item.a_attr.isactive : ",item.a_attr.isactive);
				item.a_attr.isactive = (item.a_attr.isactive == "N")?"Y":"N";
				item.icon = (item.a_attr.isactive == "N")?TREEICONS["N"]:TREEICONS["0"];
				//break;
			}
		});
		console.log("====id  : ",id);
		console.log("====vjData  : ",jData);
		this.setCoreData(jData);
  
	},
	getNode : function(nodeId) {
		return this.$tree.jstree(true).get_node(nodeId);
	},
	openNode : function(nodeId) {
		this.$tree.jstree("open_node", nodeId);
	},
	clickNode : function(nodeId) {
		var node = this.$tree.jstree(true).get_node(nodeId);
			
		if(node){
			this.$tree.jstree("deselect_all");
			this.$tree.jstree("select_node", nodeId);	
			this.$tree.jstree("toggle_node", nodeId);
			this.openNode(nodeId);
			var duration = setTimeout(function(){
				try{
					var offset = $("#"+nodeId).offset();
						
					$("#" + tree.treeId).scrollTop(offset.top-200);//트리영역의 상단 높이 영역을 뺀다
					}
					catch(e){					
					}
			},200);
		this.currentNodeId = nodeId;
		}
	},
	getDeptList : function() {
		var tree = this;
		var sendData = {
			companyid : companyid,
			deptid : deptid,
			objIsMngMode : true
		};
		console.log("====getDeptList : ", sendData);
		// 조회
		$.ajax({
			url : "${ctxRoot}/api/organ/dept/list/down",
			type : "POST",
			dataType : "json",
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
				success : function(data) {
					//alert("data ==> "    +JSON.stringify(data));
					console.log("selectDeptList : ", data);
					if (data.status == "0000") {
						if (objectIsEmpty(data.result)) {
							return;
						}
						tree.item = $.extend(true,{},data.result);
						$.each(data.result, function(i, item) {
							item.icon = ADMINTREEICONS["CLS_ROOT"];
							tree.createDeptNode(item);
	
						});
	
					}
				},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			}
		});
	},
	getFolderList : function(upid,selectID) {
		var tree = this;
		$("#GroupUserData").empty();
		var sendData = {
			"objIsTest" : "N",
			"objSkipAcl" : true,
			"upid" : upid,
			"types" : CLSTYPES["DEPT"],
			objIsMngMode : true
		};
		console.log("====sendData : ",sendData);
		$.ajax({
			url : "${ctxRoot}/api/classification/list/down",
			type : "POST",
			dataType : "json",
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
				//alert("data ==> "    +JSON.stringify(data));
				console.log("==getFolderList data : ", data);
				if (data.status == "0000") {
					if (objectIsEmpty(data.result)) {
						return;
					}

					$.each(data.result, function(i, item) {
						var itemCls = item.zappClassification;
						itemCls.icon = ADMINTREEICONS["CLS_CENT"];
						tree.createFolderNode(itemCls);
					});
					if(!objectIsEmpty(selectID)){
						tree.clickNode(selectID);
					} 
					tree.openNode(upid);
					} else {
						alertErr(data.message);
					}
				},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			}
		})
    },
	getFolderLoad : function(scFolderClassid,selectID) {
		var tree = this;
		$("#GroupUserData").empty();
		var sendData = {
			"objIsTest" : "N",
			"classid" : scFolderClassid,
			objIsMngMode : true
		}
		console.log("====sendData : ",sendData);
		$.ajax({
			url : "${ctxRoot}/api/classification/get",
			type : "POST",
			dataType : "json",
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
				//alert("data ==> "    +JSON.stringify(data));
				console.log("data : ", data);
					if (data.status == "0000") {
						if (objectIsEmpty(data.result)) {
							return;
						}

						var itemCls = data.result.zappClassification;
						itemCls.icon = ADMINTREEICONS["CLS_CENT"];
						tree.createFolderNode(itemCls);
              
						if(!objectIsEmpty(selectID)){
							tree.clickNode(selectID);
						}
						tree.openNode(scFolderClassid);
					} else {
						alertErr(data.message);
					}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			}
		})
	},
    
	getFolderDetail : function(scFolderClassid) {
		var tree = this;
		console.log("====폴더 상세보기===");
		$("#GroupUserData").empty();
		var sendData = {
			"objIsTest" : "N",
			"classid" : scFolderClassid,
			objIsMngMode : true
		}

		$.ajax({
			url : "${ctxRoot}/api/classification/get",
			type : "POST",
			dataType : "json",
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
				console.log("data : ", data);

				originalAcls.length = 0;
				if (data.status == "0000") {
					if (objectIsEmpty(data.result)) {
						$("#GroupUserData").html("");
						return;
					}
					var clsResult = data.result.zappClassification;
					var isNodeActive = clsResult.isactive;

					$("#ClassId").val(clsResult.code).attr("disabled", true);
					$("#ClassName").val(clsResult.name);
					$("#ClassDescpt").val(clsResult.descpt);
					$("#ClassState").text((isNodeActive == 'Y') ? "<spring:eval expression="@${lang}['USE']"/>" : "<spring:eval expression="@${lang}['NOT_USE']"/>");
            
					tree.select = clsResult;
					console.log("====folde tree.select : ",tree.select);
					selectClassText = clsResult.name;
					selectWfid = clsResult.wfid;
					selectWfrequired = clsResult.wfrequired;
					if(objectIsEmpty(selectWfid)){
						$("#ApprovalSelect option:eq(0)").prop("selected", true).change();
					}else{  
						$('#ApprovalSelect option[value=' + selectWfid + ']').prop('selected', true).change();
					}
          
					var NodeListInfo = data.result.zappUnionAcls;
			
					$.each(data.result.zappUnionAcls, function(i, item) {
              		//console.log(JSON.stringify(item));

						var classAcl = 1; //접근불가는 제외여서 1부터 시작
						var contentAcl = 0;
						var $innerHtml = "";
						var $tr = $("<tr></tr>");
						var uniq = "" + i;
						console.log(" i : " + i);
						var nodeName = item[0].objname;
						var nodeType = (item[0].aclobjtype == "01") ? "<spring:eval expression="@${lang}['USER']"/>"
						: (item[0].aclobjtype == "02") ? "<spring:eval expression="@${lang}['DEPARTMENT']"/>" : "<spring:eval expression="@${lang}['GROUP']"/>";
						$innerHtml += "<td id='NodeInfoAcl' aclid='"+item[0].aclid+"'><input type='checkbox' name='NodeInfoCheck' id='NodeInfoCheck"+i+"' /><label for=NodeInfoCheck"+i+"></label></td>";
						$innerHtml += "<td id='NodeInfoId' aclobjid='"+item[0].aclobjid+"'>" + nodeName + "</td>";
						$innerHtml += "<td id='NodeInfoName' aclobjtype='"+item[0].aclobjtype+"'>" + nodeType + "</td>";
					
						var classItem = item.filter(function(value) {
							return (!zChkString.fn_isEmpty(value.classid))
						});
						var selectClass = {
							select : (classItem[0].acls == 0) ? 1 : classItem[0].acls,
							start : 1,
							end : nodeClassAclList.length
						}
						$innerHtml += "<td ><select id='InfoClassAcls' style='width:100%'>"
						+ renderAcls(nodeClassAclList, selectClass.select, selectClass.start, selectClass.end) + "</select></td>";

						var contentItem = item.filter(function(value) {
							return (!zChkString.fn_isEmpty(value.contentid))
						});
						var contentClass = {
							select : contentItem[0].acls,
							start : 0,
							end : (selectClass.select == 1) ? 2 : nodeContentAclList.length
						}

						$innerHtml += "<td><select id='InfoContentAcls'  style='width:100%'>"
							+ renderAcls(nodeContentAclList, contentClass.select, contentClass.start, contentClass.end) + "</select></td>";

						//서버 값과 수정값 비교를 위해서 사용한다.
						originalAcls.push({
							aclid : item[0].aclid,
							aclobjid : item[0].aclobjid,
							aclobjtype : item[0].aclobjtype,
							classacl : selectClass.select,
							contentacl : contentClass.select,
							type : 'ORIGINAL',
						});
						//console.log("originalAcls : "+JSON.stringify(originalAcls));
						//console.log("nodeType : "+nodeType);

						$tr.append($innerHtml);
						$("#GroupUserData").append($tr);

					});

					var holderid = clsResult.holderid;
					console.log("=== holderid:" + holderid);
					console.log("=== deptuserid:" + deptuserid);
        			           		
					var isOwner = false;
					if (holderid == deptuserid)
					isOwner = true;
					activeBtn(isNodeActive, isOwner);
					tree.openNode(scFolderClassid);
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			}
		})
	}

};

/*********************************
Name   : fn_dept
Desc   : 부서(조직) 트리 처리 함수
Param  : 없음
**********************************/
var fn_dept ={
	id : companyid,
	treeId : "znTreeDept",
	name : companyName,
	item : {},
	arroval : {},
	select : {},
	$tree : {},
	initData : function(){
		var initData =[];
		initData.push({
			"id": companyid,
			"text": companyName,
			"icon": ADMINTREEICONS["COMPANY"], 			
			"state": {
				"opened": true,
				"disabled": false
			}
			,"li_attr": {},
			"a_attr": {"itemcode":"","itemname":"","itemtype":"N","itemisactive":"Y",
				class: "no_checkbox"
			}
		});
		return initData;
	},
	$tree : {},
	jstree : function() {
		console.log("====init jstree");
		var tree = this;
		tree.$tree = $("#" + tree.treeId);
		tree.$tree.jstree({
			"checkbox" : {
				//"keep_selected_style" : false
				three_state : false,
				whole_node :  false,
				tie_selection : false
			},		
			'plugins': ["state","checkbox"],
			'core' : {
				'data' : tree.initData(),
				'check_callback' : true
			},
			sort : function(a, b) {
				var a1 = this.get_node(a);
				var b1 = this.get_node(b);
				if (sortType === "asc") {
					return (a1.a_attr.priority > b1.a_attr.priority) ? 1 : -1;
				} else {
					return (a1.a_attr.priority > b1.a_attr.priority) ? -1 : 1;
				}
			}
		}).on('select_node.jstree', function(event, data){
			var item = data.instance.get_node(data.selected);
			var selfid = item.id;
			console.log("====selfid : ",selfid);
			tree.select = item;
			$("input:checkbox[id='OrganAreaUserALLCheck']").attr("checked", false);
			tree.getDeptUserList();
		}).on('ready.jstree', function () {

			tree.getDeptList(); 

		});
       
	},
	createNode : function(item){
		var tree = this;
		tree.$tree.jstree (
			'create_node'
			, item.upid
			, {	  "text"   : item.name
				, "id"     : item.deptid
				, "icon"   : ADMINTREEICONS["DEPT"]
				, "state"  : { "opened": true }
				, "a_attr" : {   "itemcode":item.code
								, "itemname":item.name
								, "itemtype":"N"
								, "itemisactive":item.isactive
						          
							} 
				}
			, "last"
			, false
			, false
		);

	},
	getDeptList : function(){
		var tree = this;
		var sendData = {
			companyid :companyid,
			upid : companyid,
			isactive : "Y"
		};
		console.log("====getDeptList ",sendData);
		// 조회
		$.ajax({
			url :"${ctxRoot}/api/organ/dept/list/down"
			, type : "POST"
			, dataType : "json"
			, contentType : 'application/json'
			, async : false
			, data : JSON.stringify(sendData)
			, success : function(data){
				//alert("data ==> "    +JSON.stringify(data));
				console.log("getDeptList : ",data);
				if(data.status == "0000") {
					if(objectIsEmpty(data.result)){
						return;
					}
					$.each(data.result, function(i,result){
						tree.createNode(result);
					});
				}else{
					alertErr(data.message);
				}
			}, error : function(request, status, error) {
				alertNoty(request,status,error,"<spring:eval expression="@${msgLang}['ARE_YOU_GROUP_DELETE']" />");
				}
		})
	},getDeptUserList : function(){
		var tree = this;
		$('#OrganAreaUserData').html("");
      	
		var sendData = {
			companyid :companyid,
			deptid : tree.select.id,
			isactive : "Y"
		};
		console.log("=====sendData : ",sendData);
		// 조회
		$.ajax({
			url : "${ctxRoot}/api/organ/deptuser/list"
			, type : "POST"
			, dataType : "json"
			, contentType : 'application/json'
			, async : false
			, data : JSON.stringify(sendData)
			, success : function(data){
				console.log("===deptuser/list : ",data);
				if(data.status == "0000") {
					if(objectIsEmpty(data.result)){
						return;
					}				
					var keyName = "OrganAreaUser";
					$.each(data.result, function(i, result){
						console.log("=== zappUser:", data.result[i].zappUser);
						var $tr = $("<tr></tr>");	
						var inHtml = "<td><input type=checkbox name="+keyName+" id="+keyName+""+i+" value='"+data.result[i].deptuserid+"' textData='"+data.result[i].zappUser.name+"' ><label for="+keyName+""+i+"></label></td>";
						inHtml += "<td>" + data.result[i].zappUser.name + " (" + data.result[i].zappUser.loginid + ")</td>";
						$tr.append(inHtml);
						$tr.data('meta', result);
						$("#OrganAreaUserData").append($tr);
					});
				}
			},   error : function(request, status, error) {
					alertNoty(request,status,error);
				}
		})
	}
     
}


/*********************************
Name   : fn_group
Desc   : 그룹 트리 처리 함수
Param  : 없음
**********************************/
var fn_group = {
	id : companyid,
	treeId : "znTreeGroup",
	name : companyName,
	item : {},
	arroval : {},
	select : {},
	$tree :{},
	initData : function(){
		var tree = this;
		var initData =[];
		initData.push(tree.initNode(GROUPCODES["COMPANY"],"<spring:eval expression="@${lang}['COMPANY_GROUP']"/>"));
		initData.push(tree.initNode(GROUPCODES["DEPT"],"<spring:eval expression="@${lang}['DEPARTMENT_GROUP']"/>"));
		initData.push(tree.initNode(GROUPCODES["COLLABO"],"<spring:eval expression="@${lang}['COLLABORATIVE_GROUP']"/>"));      	
		return initData;
	},
	initNode : function(type,name){
		return {
			"id" : type,
			"text" : name,
			"icon" : ADMINTREEICONS["COMPANY"],
			"state" : {
				"opened" : true,
				"disabled" : false
			},
			"li_attr" : {},
			"a_attr" : {
				"itemcode" : type,
				"itemname" : name,
				"itemtype" : "G",
				"itemisactive" : "Y",
				"itemroottype" : type,
				class: "no_checkbox"
			}
		}
	},
	$tree : {},
	jstree : function() {
		console.log("====init jstree");
		var tree = this;
		tree.$tree = $("#" + tree.treeId);
		tree.$tree.jstree({
			"checkbox" : {
			//"keep_selected_style" : false
				three_state : false,
				whole_node :  false,
				tie_selection : false
			},	
			'plugins': ["state","checkbox"],
			'core' : {
				'data' : tree.initData(),
				'check_callback' : true
			},
			sort : function(a, b) {
				var a1 = this.get_node(a);
				var b1 = this.get_node(b);
				if (sortType === "asc") {
					return (a1.a_attr.priority > b1.a_attr.priority) ? 1 : -1;
				} else {
					return (a1.a_attr.priority > b1.a_attr.priority) ? -1 : 1;
					}
			}
		}).on("select_node.jstree", function (event, data) { 
			var item = data.instance.get_node(data.selected);
			if(item.id == GROUPCODES["COMPANY"] ||item.id == GROUPCODES["DEPT"] ||item.id == GROUPCODES["COLLABO"]){
				return;
			}
			tree.select = $.extend(true,{},item);
			console.log("====group select===");
			tree.getGroupUserList();
		}).on('ready.jstree', function () {
			tree.getGroupList(GROUPCODES["COMPANY"]);  //전사
			tree.getGroupList(GROUPCODES["DEPT"]);  //부서
			tree.getGroupList(GROUPCODES["COLLABO"]);  //협업
			tree.getGroupList(GROUPCODES["SUPER"]);  //전체사용자 
		})       
	},
	createNode : function(item){
		//console.log("====createNode : ",item);
		var tree = this;
		tree.$tree.jstree (
			'create_node'
			, item.rootcode
			, {	  "text"   : item.name
				, "id"     : item.groupid
				, "icon"   : item.icon
				, "state"  : { "opened": true }
				, "a_attr" : {   "itemcode":item.code
								, "itemname":item.name
								, "itemtype":item.types
								, "itemisactive":item.isactive	} 
				}
			, "last"
			, false
			, false
		);
	},
	clickNode : function(nodeId){
		var node = this.$tree.jstree(true).get_node(nodeId);  			
		if(node){
			this.$tree.jstree("deselect_all");
			this.$tree.jstree("select_node", nodeId);	
			this.$tree.jstree("toggle_node", nodeId);
			this.openNode(nodeId);
			var duration = setTimeout(function(){
				try{
					var offset = $("#"+nodeId).offset();
					$("#" + tree.treeId).scrollTop(offset.top-200);//트리영역의 상단 높이 영역을 뺀다
					}
					catch(e){					
					}
			},200);
			this.currentNodeId = nodeId;
		}
	}
	,getGroupList : function(type){
		var tree = this;
		var sendData ={
			"objIsTest" : "N",
			"zappGroup" : { "companyid":companyid
							,"isactive" : "Y"
							,"types":type
						}
		};
		console.log("====getGroupList : ",sendData);
		// 조회
		$.ajax({
			url :"${ctxRoot}/api/organ/group/list"
			, type : "POST"
			, dataType : "json"
			, contentType : 'application/json'
			, async : false
			, data : JSON.stringify(sendData) 
			, success : function(data){
				//alert("data ==> "    +JSON.stringify(data));
				console.log("getGroupList : ",data);
				if(data.status == "0000") {
					if(objectIsEmpty(data.result)){
						return;
					}
					$.each(data.result, function(i,result){  
						var grInfo = $.extend(true,{},result);;
						if(type == GROUPCODES["SUPER"]){
							grInfo.icon = ADMINTREEICONS["DEPT"];
							grInfo.code = GROUPCODES["SUPER"];
							grInfo.rootcode = "#";
						}else{
							var icon = (result.isactive == "Y") ? ADMINTREEICONS["DEPT"]:TREEICONS["N"];
							grInfo.icon = icon;	
							grInfo.rootcode ="#"+type;
						}
						tree.createNode(grInfo);
					});     					
				}else{
					alertErr(data.message);
				}
			},   error : function(request, status, error) {
					alertNoty(request,status,error);
				}
		});
	}
	,getGroupUserList : function(){
		var tree = this;
		$('#GroupAreaUserData').html("");
		var keyName = "";
		var sendData = {
			"objIsTest" : "N",
			"zappGroup" : { "groupid" : tree.select.id }
			};
		console.log("====getGroupUserList : ",sendData);
		// 조회
		$.ajax({
			url : "${ctxRoot}/api/organ/group/get"
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
					var keyName = "GroupAreaUser";         			
					if(!objectIsEmpty(data.result.zappGroupUserExtends)){	 
						$.each(data.result.zappGroupUserExtends,function(i,result) {
							var $tr = $("<tr></tr>");	
							var inHtml ="";
                 			
							if(!objectIsEmpty(result.zappUser.name)){
								inHtml = "<td><input type=checkbox name="+keyName+" id="+keyName+""+i+" value='"+result.gobjid+"' textData='"+result.zappUser.name+"' disabled><label for="+keyName+""+i+"></label></td>";
								inHtml += "<td>" + result.zappUser.name + " (" + result.zappUser.loginid + ")</td>";
							}else if(!objectIsEmpty(result.zappDept.name)){
								inHtml = "<td><input type=checkbox name="+keyName+" id="+keyName+" value='"+result.gobjid+"' textData='"+result.zappDept.name+"' disabled></td>";
								inHtml += "<td>" + result.zappDept.name + "</td>";
							}
							$tr.append(inHtml);
							$tr.data('meta', result);
							$("#GroupAreaUserData").append($tr);
						});
					}
				} else{
					alertErr(data.message);
				}
			},   
			error : function(request, status, error) {
				alertNoty(request,status,error);
			}
		})
	}
}
  
/*********************************
Name   : initApprov
Desc   : 승인정보
Param  : 없음
**********************************/
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
					$select.append($("<option data-meta='" + JSON.stringify(result) + "' value='" + appGroupId + "'>" + appText + "</option>"));
				});
				$select.bind("change", changeApprov);
				$td.append($select);
				$tr.append($td).append($("<td id='approvalID'></td>")).append($("<td id='approvalState'></td>"));
				$("#ApprovalInfo").append($tr);
			} else {
				alertErr(data.message);
			}
		},
		error : function(request, status, error) {
			alertNoty(request, status, error);
		}
	});
}

/*********************************
Name   : changeApprov
Desc   : 승인권자 선택시 상세 정보 가져오기.
Param  : 없음
**********************************/
var changeApprov = function(e) {
	console.log("=== $(this)===", $(this));
	var value = $(this).val();
	var meta = $(this).find("option:selected").data("meta");
	if (!objectIsEmpty(meta)) {
		$("#AppovalGroup").show();
		console.log("====value : ", value);
		console.log("====subValue : ", meta.groupid);
		$("#approvalID").text(meta.code);
		$("#approvalState").empty();
		var $select = $("<select style='min-width:90px; width: 90%;' id='ApprovalRequired'></select>");
		$select.append($("<option value='0'>" + "<spring:eval expression="@${lang}['APPROVAL0']"/>" + "</option>"));
		$select.append($("<option value='99'>" + "<spring:eval expression="@${lang}['APPROVAL99']"/>" + "</option>"));
		$("#approvalState").append($select);
		$('#ApprovalRequired option[value=' + selectWfrequired + ']').attr('selected', true);
		selectAppoval(meta.groupid);
	} else {
		$("#AppovalGroup").hide();
		$("#AppovalGroupUserData").empty()
		$("#approvalID").text('');
		$("#approvalState").empty();
	}
}


/*********************************
Name   : selectAppoval
Desc   : 선택된 승인그룹의 상세 정보를 가져온다.
Param  : scGroupid(그룹아이디)
**********************************/
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


/*********************************
Name   : renderPriority
Desc   : 폴더 순서
Param  : item(선택된 순서)
**********************************/    
var renderPriority = function(item) {
	console.log("=====renderPriority item : ", item);
	var maxPriority = 0;
	$.each(item.children, function(i, data) {
		//if(item.id == )
		var node = fn_tree.getNode(data);
		// console.log("====node : "+i+"===",node);
		if (node.a_attr.type == "F") {
			if (item.id == node.parent) {
				maxPriority++
			}
		}
	});
	console.log("====maxPriority : ", maxPriority);
	//console.log(item);
	if (maxPriority <= 1) {
		return;
	}
	$("#priority_tab").show();
	$("#folderPriority").children('option').remove();
	var optionHtml = "";
	optionHtml += "<option value='0'>선택</option>";
	for (var i = 1; i <= maxPriority; i++) {
		optionHtml += "<option value='" + (i) + "'>" + (i) + "</option>";
	}
	$("#folderPriority").append(optionHtml);
	$("#folderPriority").change(function() {
		console.log("====tree.select : ", fn_tree.select);
		var tObj = $("#znTree").jstree(true).get_node(fn_tree.select.classid);
		var item_priority = tObj.a_attr.priority;
		var position = $(this).val();
		if (item_priority == position) {
			alert("<spring:eval expression="@${msgLang}['MOVE_SAME_LOCATION']"/>");
			return;
		}
		if (fn_tree.select.classid !== "" || position == 0) {
			reorderNodeInfo(position);
		}
	});
}



/*********************************
Name   : AllReset
Desc   : 모든 입력 데이터 초기화
Param  : 없음
**********************************/  
var AllReset = function() {
	$("#ClassId").val("").attr("disabled", false);
	$("#ClassName").val("").attr("disabled", false);
	$("#ClassDescpt").val("").attr("disabled", false);
	$("#ClassState").text('');
	$("#GroupUserData").empty();

	$("input:checkbox[name$='ALLCheck']").prop("checked", false).attr("disabled", false);
	$("input:checkbox[name$=AreaUser]").prop("checked",false);
	$("input:checkbox[name=NodeInfoCheck]").prop("checked",false);
  
	$("#znTreeDept").jstree().uncheck_all(true);
	$("#znTreeDept").jstree().deselect_all(true);
	$("#znTreeGroup").jstree().uncheck_all(true);
	$("#znTreeGroup").jstree().deselect_all(true);


	$("#znTreeMove").jstree("deselect_all");
	$("#S_data").val('');
  
	$("#ApprovalSelect option:eq(0)").prop("selected", true).change();
	originalAcls.length = 0;
	//addNodeList.length = 0;

	modFolderUserInfo.length = 0;
}


/*********************************
Name   : tabReset 
Desc   : tab(조직, 그룹, 검색)선택시 속성 및 초기화 진행
Param  : tabPosition (0 : 조직, 1 : 그룹, 2 : 검색)
**********************************/  
var tabReset =function(position){
	tabPosition = position;
	$("#S_data").val('');
}
 
/*********************************
Name   : chkNodeDiscard 
Desc   : 삭제된 권한 정보를 temp에 저장한다.
Param  : 없음
**********************************/  
var chkNodeDiscard = function() {
	$("input:checkbox[name=NodeInfoCheck]").each(function() {
		console.log("checked : " + $(this).is(":checked"));
		if ($(this).is(":checked")) {
			var parent = $(this).parent().parent();
			var classidx = parent.children().children("#InfoClassAcls").prop('selectedIndex');
			var contentidx = parent.children().children("#InfoContentAcls").prop('selectedIndex');
			var aclobjid = parent.children("#NodeInfoId").attr("aclobjid");
			var aclobjtype = parent.children("#NodeInfoName").attr('aclobjtype');
			classidx += 1; // 조회불가 + 등록불가 (0) 이 빼짐으로 기본값에 1을 더 해준다.

			console.log("=== originalAcls.length: " + originalAcls.length);

			var index = -1;
			for (var i = 0; i < originalAcls.length; i++) {
				console.log("=== originalAcls[i].aclid: " + originalAcls[i].aclid);
				console.log("=== parent.children(#NodeInfoAcl).attr(aclid): " + parent.children("#NodeInfoAcl").attr("aclid"));
				console.log("=== aclobjid: " + aclobjid);
				if ((originalAcls[i].aclid == parent.children("#NodeInfoAcl").attr("aclid")) && (originalAcls[i].aclobjid == aclobjid)) {
					index = i;
					break;
				}
			}
			console.log("index : " + index);

			//일치하는 데이터가 있으면 데이터를 전송하기전 임시로 modFolderUserInfo에 데이터를 저장해 둔다.
			if (index > -1) {

				var sIndex = findItemIndex(modFolderUserInfo, aclobjid);
				if (sIndex > -1) {
					modFolderUserInfo[sIndex].type = "DISCARD";
				} else {
					var disItem = cloneObject(originalAcls[index]);
					disItem.classacl = classidx;
					disItem.contentacl = contentidx;
					disItem.type = "DISCARD";
					modFolderUserInfo.push(disItem);
				}
				originalAcls.splice(index, 1);
				parent.remove();

			} else {
				var index = findItemIndex(modFolderUserInfo, aclobjid);
				console.log("findItemIndex : " + index);

				if (index > -1) {
					if (modFolderUserInfo) {
						if (modFolderUserInfo[index].type == 'ADD') {
							modFolderUserInfo.splice(index, 1);
							parent.remove();
						} else if (modFolderUserInfo[index].type == 'CHANGE') {
							modFolderUserInfo[index].type = "DISCARD";
						}
					}
				}
			}
		}
	});
}

/*********************************
Name   : findItemIndex 
Desc   : 해당 아이템의 인덱스를 찾는다
Param  : findItem, code
**********************************/ 
var findItemIndex = function(findItem, code) {
	var fIndex = -1;
	for (var i = 0; i < findItem.length; i++) {
		if (findItem[i].aclobjid == code) {
			fIndex = i;
			break;
		}
	}
	return fIndex;
}

/*********************************
Name   : chkNodeInfo 
Desc   : 폴더권한정보 변경및 추가
Param  : type
**********************************/ 
var chkNodeInfo = function(type) {
	console.log("chkNodeInfo type : " + type);

	$("input:checkbox[name=NodeInfoCheck]").each(function() {

		var parent = $(this).parent().parent();
		var classidx = parent.children().children("#InfoClassAcls").prop('selectedIndex');
		var contentidx = parent.children().children("#InfoContentAcls").prop('selectedIndex');
		var aclobjid = parent.children("#NodeInfoId").attr("aclobjid");
		var aclobjtype = parent.children("#NodeInfoName").attr('aclobjtype');
		classidx += 1; // 조회불가 + 등록불가 (0) 이 빼짐으로 기본값에 1을 더 해준다.

		var index = -1;
		for (var i = 0; i < originalAcls.length; i++) {
			if ((originalAcls[i].aclid == parent.children("#NodeInfoAcl").attr("aclid")) && (originalAcls[i].aclobjid == aclobjid)) {
				index = i;
				break;
			}
		}

		if (index > -1) {

			if (originalAcls[index].classacl != classidx || originalAcls[index].contentacl != contentidx) {
				originalAcls[index].classacl = classidx;
				originalAcls[index].contentacl = contentidx;
				var changeItem = cloneObject(originalAcls[index]);
				changeItem.type = type;

				modFolderUserInfo.push(changeItem);
			} else {
				var index = findItemIndex(modFolderUserInfo, aclobjid);
				if (index > -1) {
					modFolderUserInfo[index].classacl = classidx;
					modFolderUserInfo[index].contentacl = contentidx;
				}
			}
		} else {
			var index = findItemIndex(modFolderUserInfo, aclobjid);
			//console.log("modFolderUserInfo index : "+index);	
			if (index > -1) {
				modFolderUserInfo[index].classacl = classidx;
				modFolderUserInfo[index].contentacl = contentidx;
				if (modFolderUserInfo[index].type != 'ADD') {
					modFolderUserInfo[index].type = type;
				}
			}
		}
	});
}


/*********************************
Name   : NodeReg 
Desc   : 폴더 생성
Param  : 없음
**********************************/    
var NodeReg = function(){
	console.log("====NodeReg====");
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

	if(objectIsEmpty(fn_tree.select)){
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
	var upid = "";
	var clsid = "";
	if(fn_tree.select.node){  // 부서
		clsid = fn_tree.select.node.id;
		upid = fn_tree.select.node.parent;
	}else if(fn_tree.select.classid){ // 폴더
		clsid = fn_tree.select.classid;
		upid= fn_tree.select.upid;
	}
	console.log("====wfrequired : ",wfrequired);
	var sendData = {
		objIsTest : "N",
		objDebugged : false,
		companyid : companyid, //세션자동입력
		code : $.trim($("#ClassId").val()), //분류코드
		name : $.trim($("#ClassName").val()), //분류명칭
		descpt:$.trim($("#ClassDescpt").val()), //설명
		upid : clsid , //부모분류아이디
		holderid : deptuserid, //세션자동입력
		types : CLSTYPES["DEPT"], //01 : 일반 노드, N1 : 전사노드, N2 : 부서 노드, N3 : 개인노드, N4 : 협업노드, 02 : 분류체계, 03 : 문서유형
		zappClassAcls:zappClassAcls,
		zappContentAcls:zappContentAcls,
		wfid:wfid,
		affiliationid:upid,
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
						console.log("data : ",data);
						if(data.status == "0000"){
							alert("<spring:eval expression="@${msgLang}['DOCUMENT_BOX_REGISTERED']"/>");
							fn_tree.getFolderList(fn_tree.select.upid,data.result);
	   					
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
  
/*********************************
Name   : NodeMod 
Desc   : 폴더 수정
Param  : 없음
**********************************/   
var NodeMod = function(){
	console.log("====NodeMod====");
	if( $.trim($("#ClassName").val()) == ""){
		alert("<spring:eval expression="@${msgLang}['ENTER_DOCUMENT_BOX_NAME']"/>");
		$("#ClassName").focus();
		return;
	}

	if( objectIsEmpty(fn_tree.select)){
		//alert("선택된 문서함정보가 존재하지 않습니다.");
		return;
	}
	
	chkNodeInfo("CHANGE");
	var isChange = (fn_tree.select.name!= $.trim($("#ClassName").val()) ||fn_tree.select.descpt != $.trim($("#ClassDescpt").val()))
	console.log("====isChange : ",isChange);
	if(isChange && modFolderUserInfo.length<=0){
		
		//문서함명만 봐꾸기 위해서 get으로 받은 데이터를 다시 서버로 던져준다(필수값때문에 사용)		
		//chkNodeInfo("RENAME");
		var sendData={
			objIsTest : "N",
			objDebugged : false,
			classid : fn_tree.select.classid, //분류아이디
			name : $.trim($("#ClassName").val()),
			descpt: $.trim($("#ClassDescpt").val())
			
		}
	  
			noty({
				layout : "center",
				text : "<spring:eval expression="@${msgLang}['DO_YOU_DOCUMENT_BOX_MODIFY']"/>",
				buttons : [ {
					addClass : 'b_btn',
					text : "Ok",
					onClick : function($noty) {
						$noty.close();
						$.ajax({
							type : 'POST',
							url : '${ctxRoot}/api/classification/change/name',
							dataType : 'json',
							contentType : 'application/json',
							async : false,
							data : JSON.stringify(sendData),
							success : function(data){
								//console.log("data : "+JSON.stringify(data));
								if(data.status == "0000"){				
									alert("<spring:eval expression="@${msgLang}['DOCUMENT_BOX_MODIFIED']"/>");
									fn_tree.clickNode(fn_tree.select.classid);
								}else{
									alertErr(data.message);
								}
							},   error : function(request, status, error) {
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
		
		
		}else{
			//selectWfid = cls.wfid;
			//selectWfrequired =cls.wfrequired;
		
			var wfid =""; //wfrequired = 0 ?"":groupid
			var wfrequired = 0; //0:사용안함, 99 : ALL
			
			var meta = $("#ApprovalSelect").find("option:selected").data("meta");
			var selectItem = $("#ApprovalRequired option:selected").val();
			if(!objectIsEmpty(meta)){
				wfid = meta.groupid;
				if(!objectIsEmpty(selectItem)){
					if(selectItem>0){
						wfrequired = selectItem;
					}
				}
			}
			var isSame = false;
			if(selectWfid != wfid){
				isSame=true;
			}else{
				if(wfrequired != selectWfrequired){
					isSame = true;
				}
			}
			console.log("=== sam wfid : "+wfid+", selectWfid : "+selectWfid);
			console.log("=== sam wfrequired : "+wfrequired+", selectWfrequired : "+selectWfrequired);
			console.log("=== isSame : "+isSame);
			console.log("=== modFolderUserInfo.length : "+(modFolderUserInfo.length<=0));
			if(modFolderUserInfo.length<=0  && isSame == false){
				alert("<spring:eval expression="@${msgLang}['NO_ALERTED_CONTENT']"/>");
				return;
			}
	  	
			var zappClassAcls=[];
			var zappContentAcls=[];
	
			if(modFolderUserInfo.length>0){
				$.each(modFolderUserInfo,function(i,item){
					//console.log("item.type : "+item.type)
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
			var upid = "";
			var clsid = "";
			if(fn_tree.select.node){  // 부서
				clsid = fn_tree.select.node.id;
				upid = fn_tree.select.node.parent;
			}else if(fn_tree.select.classid){ // 폴더
				clsid = fn_tree.select.classid;
				upid= fn_tree.select.upid;
			}
			console.log("====wfrequired : ",wfrequired);
			var sendData = {
				objIsTest : "N",
				objDebugged : false,
				classid : clsid, //분류아이디
				types: CLSTYPES["DEPT"], //01:일반노드분류, N1:전사노드분류, N2:부서노드분류,N3:개인노드분류, N4:협업노드분류,	02:분류체계,03:문서유형
				name : $.trim($("#ClassName").val()),
				descpt:$.trim($("#ClassDescpt").val()),
				zappClassAcls:zappClassAcls, 
				zappContentAcls :zappContentAcls,
				wfid:wfid,
				affiliationid:upid,
				wfrequired:wfrequired
			};
			console.log("=====change sendData ",sendData);
			
			noty({
				layout : "center",
				text : "<spring:eval expression="@${msgLang}['DO_YOU_DOCUMENT_BOX_MODIFY']"/>",
				buttons : [ {
					addClass : 'b_btn',
					text : "Ok",
					onClick : function($noty) {
						$noty.close();
						$.ajax({
							type : 'POST',
							url : '${ctxRoot}/api/classification/change',
							dataType : 'json',
							contentType : 'application/json',
							async : false,
							data : JSON.stringify(sendData),
							success : function(data){
								console.log("change data : ",data);
								if(data.status == "0000"){
									alert("<spring:eval expression="@${msgLang}['DOCUMENT_BOX_MODIFIED']"/>");
									fn_tree.clickNode(clsid);
								}else{
									alertErr(data.message);
								}
							},   error : function(request, status, error) {
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
};

/*********************************
Name   : NodeDel 
Desc   : 폴더정보 사용/미사용 처리
Param  : 없음
**********************************/
var NodeDel = function() {

	var activeName = ($("#ClassState").text() == '<spring:eval expression="@${lang}['NOT_USE']"/>') ? "enable" : "disable";
	//return;
	var msg = (activeName == 'enable') ? "<spring:eval expression="@${msgLang}['DO_YOU_FOLDER_STATE_ENABLED']"/>"
	: "<spring:eval expression="@${msgLang}['DO_YOU_FOLDER_STATE_DISABLED']"/>";
	noty({
		layout : "center",
		text : msg,
		buttons : [ {
			addClass : 'b_btn',
			text : "Ok",
			onClick : function($noty) {
				$noty.close();
				var sendData = {
					objIsTest : "N",
					objDebugged : false,
					classid : fn_tree.select.classid, //분류아이디
					//objIncLower:"Y"  
					objIncLower : (activeName == 'enable') ? "N" : "Y"//하위 분류 disable 여부 (Y/N)
				};
				$.ajax({
					type : 'POST',
					url : '${ctxRoot}/api/classification/' + activeName,
					dataType : 'json',
					contentType : 'application/json',
					async : false,
					data : JSON.stringify(sendData),
					success : function(data) {
						console.log("data : ",data);
						if (data.status == "0000") {
							alert("<spring:eval expression="@${msgLang}['FOLDER_STATE_CHANGED']"/>");
							AllReset();
							fn_tree.removeNode(fn_tree.select.classid);
							fn_tree.getFolderLoad(fn_tree.select.classid,fn_tree.select.classid);
						} else {
							alert(data.status);
						}
					},
					error : function(request, status, error) {
						alertNoty(request, status, error);
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

/*********************************
Name   : NodeDis 
Desc   : 폴더정보 폐기처리
Param  : 없음
**********************************/
var NodeDis = function() {
	noty({
		layout : "center",
		text : "<spring:eval expression="@${msgLang}['ARE_YOU_FOLDER_DISCARD']"/>",
		buttons : [ {
			addClass : 'b_btn',
			text : "Ok",
			onClick : function($noty) {
				$noty.close();
				var sendData = {
					objIsTest : "N",
					objDebugged : false,
					classid : fn_tree.select.classid
					//분류아이디
					};
				$.ajax({
					type : 'POST',
					url : '${ctxRoot}/api/classification/discard',
					dataType : 'json',
					contentType : 'application/json',
					async : false,
					data : JSON.stringify(sendData),
					success : function(data) {
						//console.log("data : "+JSON.stringify(data));
						if (data.status == "0000") {

							alert("<spring:eval expression="@${msgLang}['FOLDER_INFO_DISCARDED']"/>");
							AllReset();
							fn_tree.removeNode(fn_tree.select.classid);
							fn_tree.clickNode(fn_tree.select.upid);
						} else {
							alert(data.status);
						}
					},
					error : function(request, status, error) {
						alertNoty(request, status, error);
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
 
/*********************************
Name   : GroupNodeDel 
Desc   : 폴더정보 삭제
Param  : 없음
**********************************/
var GroupNodeDel = function() {

	if (!chkAddNode()) { //체크된 항목이 없을 경우
		return;
	}
	chkNodeDiscard();
}

/*********************************
Name   : activeBtn  
Desc   : 폴더명, 폴더상태, 등록, 삭제, 폐기, 버튼을 상태값에 따라 활성화 비활성화 시켜준다
Param  : isNodeActive(Y or N)
**********************************/
var activeBtn = function(isNodeActive, isOwner) {
	console.log("=== isNodeActive:" + isNodeActive + ", isOwner:" + isOwner);
	$("#ClassName").attr("disabled", (isNodeActive == 'N'));
	$("#ClassState").text((isNodeActive == 'Y') ? "<spring:eval expression="@${lang}['USE']"/>" : "<spring:eval expression="@${lang}['NOT_USE']"/>");
	if (isNodeActive == 'Y' && isOwner) {
		$("#btnNewGroup").show();
		$("#btnDel").text("<spring:eval expression="@${lang}['DELETE']"/>");
  		$("#btnDel").show();
		$("#btnDis").hide();
		$("#btnMove").show();
	} else if (isNodeActive == 'Y' && !isOwner) {
		$("#btnNewGroup").hide();
		$("#btnDel").hide();
		$("#btnDis").hide();
		$("#btnMove").hide();
	} else if (isNodeActive == 'N' && isOwner){
		$("#btnNewGroup").hide();
		$("#btnDel").text("<spring:eval expression="@${lang}['RESTORE']"/>");
		$("#btnDel").show();
		$("#btnDis").show();
		$("#btnMove").hide();
  	} else if (isNodeActive == 'N' && !isOwner){
		$("#btnNewGroup").hide();
		$("#btnDel").hide();
		$("#btnDis").hide();
		$("#btnMove").hide();
  	}
}

/*********************************
Name   : rootCheck  
Desc   : 루트 확인
Param  : className
**********************************/
var rootCheck = function(className){
	var root = "";
	if(className == "<spring:eval expression="@${lang}['COMPANY_GROUP']"/>"){
		root = "root";
	}
	return root;
}


/*********************************
Name   : addGroupDeptList  
Desc   : 권한 정보를 등록한다 (중복체크는 aclobjid값으로 처리한다.)
Param  : 없음
**********************************/
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
					console.log("====tObj : ",tObj);
					var classType = (tObj.a_attr.itemtype=="N")?"<spring:eval expression="@${lang}['DEPARTMENT']"/>":"<spring:eval expression="@${lang}['USER']"/>";
					if(dupCheck(uniq)!='DUP'){
						renderDeptGroupList(uniq,className,classType,nodeClassAclList,nodeContentAclList);
					}
				}
			}
		});
	}else if(tabPosition == 1){
		var selectGroupNode = $("#znTreeGroup").jstree('get_checked');
		//console.log("selectGroupNode length : "+selectGroupNode.length);
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
			//console.log("사용자 체크 : "+$(this).val());
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
			//console.log("사용자 체크 : "+$(this).val());
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

  	
  	//$("input:checkbox[name=OrganAreaUser]").prop("checked",false);
 }


/*********************************
Name   : renderDeptGroupList
Desc   : 권한 정보를 한줄로 보여준다.
Param  : aclobjid(uniqID), nodeName(폴더 정보), nodeType(유형), classAcls(폴더 권한), contentAcls(사용자 권한)
**********************************/ 
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
	var count = $("#GroupUserData tr").length
	var $innerHtml ="";
	var $tr = $("<tr></tr>");
	var type = (classType=='<spring:eval expression="@${lang}['USER']"/>')?'01':(classType=='<spring:eval expression="@${lang}['DEPARTMENT']"/>')?'02':'03';
	$innerHtml+="<td id='NodeInfoAcl' aclid=''><input type='checkbox' name='NodeInfoCheck' id='NodeInfoCheck"+count+"' /><label for=NodeInfoCheck"+count+"></label></td>";
	$innerHtml+="<td id='NodeInfoId' aclobjid='"+aclobjid+"'>"+className+"</td>";
	$innerHtml+="<td id='NodeInfoName' aclobjtype='"+type+"'>"+classType+"</td>";
	$innerHtml+="<td ><select id='InfoClassAcls' style='width:100%'>"+renderAcls(classAcls,selectClass.select,selectClass.start,selectClass.end)+"</select></td>";
	$innerHtml+="<td><select id='InfoContentAcls'  style='width:100%'>"+renderAcls(contentAcls,contentClass.select,contentClass.start,contentClass.end)+"</select></td>";
	//$innerHtml+="<td>&nbsp;</td>";
	//addNodeList.push({aclobjid, className,type:classType});
	
	var index = findItemIndex(modFolderUserInfo,aclobjid);
	if(index>-1){
		modFolderUserInfo[index].classacl = selectClass.select;
		modFolderUserInfo[index].contentacl = contentClass.select;
		modFolderUserInfo[index].type = "CHANGE";
		 
			 //modFolderUserInfo.push(changeItem);
	}else{
		var addItem =
		{
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

/*********************************
Name   : renderAcls
Desc   : 권한 정보 option을 리턴해준다.
Param  : aclslist(권한 리스트), pos(선택된 권한이 있는경우에 초기값을 변경하기 위해서 사용)
**********************************/
var renderAcls = function(aclslist, select, start, end) {
	var optionHtml = "";
	for (var i = 0; i < aclslist.length; i++) {
		var aclsInfo = aclslist[i];

		if (aclsInfo.codevalue >= start && end >= aclsInfo.codevalue) {
			//var aclsInfo = aclslist[i];
			optionHtml += "<option value='" + aclsInfo.codevalue + "' " + (aclsInfo.codevalue == select ? "selected" : "") + ">" + aclsInfo.name + "</option>";
		}
	}
	return optionHtml;
}

/*********************************
Name   : dupCheck
Desc   : 
Param  : code
**********************************/
var dupCheck = function(code) {
	if (code == '01' || code == '02' || code == '04') { //전사, 부서, 협업의 root는 막아준다 (98 전체사용자는 사용가능)
		return chkNode = "DUP";
	}

	var chkNode = "";
	$.each(originalAcls, function(i, item) {
		if (item.aclobjid == code) {
			return chkNode = "DUP";
		}
	});
	$.each(modFolderUserInfo, function(i, item) {
		if (item.aclobjid == code && item.type != 'DISCARD') {
			return chkNode = "DUP";
		}
	});
	return chkNode;
}

/*********************************
Name   : chkAddNode
Desc   : 권한정보 체크 확인
Param  : 없음
**********************************/
var chkAddNode = function() {
	var isChkNode = false;

	$("input:checkbox[name=NodeInfoCheck]").each(function() {
		if ($(this).is(":checked")) {
			return isChkNode = true;
		}
	});
	return isChkNode;
}

/*********************************
Name   : searchRightGroup
Desc   : 부서(DEPT) or 사용자(USER)를 검색한다.
Param  : 없음
**********************************/
var searchRightGroup = function() {
	var inputUserText = $.trim($("#S_data").val());
	if (inputUserText == "") {
		alert("<spring:eval expression="@${msgLang}['ENTER_TERM']"/>");
		$("#S_data").focus();
		return;
	}
	var inputValue = $("input[name='searchTarget']:checked").val(); 
	var sendData = {};
	var data = {};
	console.log();



	if(inputValue == "USER"){
		sendData.url = '${ctxRoot}/api/organ/deptusers/list';
		data = {"companyid" : "${Authentication.objCompanyid}",
				"zappUser" : {
								"name": $.trim($("#S_data").val()) 				
							}
				};
	}else{
		sendData.url = '${ctxRoot}/api/organ/dept/list';
		data = {"companyid" : "${Authentication.objCompanyid}",
				"name": $.trim($("#S_data").val())};
		}
	//console.log("inputValue : "+inputValue);
	$.ajax({
		type : 'POST',
		url : sendData.url,
		dataType : 'json',
		contentType : 'application/json',
		async : false,
		data : JSON.stringify(data),
		success : function(data){
			console.log("data : ", data);
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

/*
   if (selectUserList.length > 0) {
     console.log("====selectUserList : ", selectUserList);

     var searchUser = selectUserList.filter(function(item) {
       return item.zappUser.name.indexOf(inputUserText) != -1;

     });
     console.log("====searchUser : ", searchUser);
     var SearchAreaUserData_dataHtml = "";
     for ( var user in searchUser) {
       console.log(user);
       var gobjtype = "01";
       var rs_name = searchUser[user].zappUser.name;
       var rs_deptid = searchUser[user].deptuserid;
       SearchAreaUserData_dataHtml += "<tr><td><input type=checkbox name=SearchAreaUser id=SearchAreaUser value='"+rs_deptid+"' textData='"+rs_name+"' valueType='"+gobjtype+"' ></td>";
       SearchAreaUserData_dataHtml += "<td>" + rs_name + "</td><td>&nbsp;</td></tr>";
     }

     $("#SearchAreaUserData").html(SearchAreaUserData_dataHtml);
   }
*/
}
 
/*********************************
Name   : reorderNodeInfo
Desc   : 부서 폴더 이동
Param  : position(순서)
**********************************/
var reorderNodeInfo = function(position) {
	var sendData = {
				data : {
					objIsTest : "N",
					objDebugged : false,
					classid : companyid,
					priority : position
					}
			};
	sendData.success = "<spring:eval expression="@${msgLang}['FOLDER_ORDER_CHANGED']"/>";
	sendData.url = '${ctxRoot}/api/classification/reorder';
	sendData.data.classid = fn_tree.select.classid;
	sendData.data.priority = position;
	sendData.confirmMessage = "<spring:eval expression="@${msgLang}['DO_YOU_MOVE_DEPT_INFO']"/>"

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
					url : sendData.url,
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
							//fn_InitTree();
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
Name   : fn_move
Desc   : 폴더이동
Param  : 없음
**********************************/
var fn_move = {
	deptTreeId : "znTreeMove",
	moveItem :{},
	select : {},
	$dtree : {},
	jstree_dept : function(){
		console.log("====init jstree_dept");
		$("#" + fn_move.deptTreeId).remove();
		var treeHtml = "<ul id='"+fn_move.deptTreeId+"'></ul>";
		$('#MOVE_Tree').append(treeHtml);

		var tree = this;
		tree.$dtree = $("#" + tree.deptTreeId);
		tree.$dtree.jstree({
			'plugins' : [ "state", "unique" ],
			'core' : {
				'data' : [],
				'check_callback' : true
				},
			sort : function(a, b) {
				var a1 = this.get_node(a);
				var b1 = this.get_node(b);
				if (sortType === "asc") {
					return (a1.a_attr.priority > b1.a_attr.priority) ? 1 : -1;
				} else {
					return (a1.a_attr.priority > b1.a_attr.priority) ? -1 : 1;
				}
			}
		}).on("select_node.jstree", function(event, data) {
				var item = data.instance.get_node(data.selected);
				tree.select = $.extend(true,{},item);
				console.log("====fn_move select_node====",tree.select);
				tree.getFolderList(tree.select.id);

			}).on('ready.jstree', function() {
				console.log("====parent===="+fn_tree.select.parent);
				console.log("====ready====");
			if(!objectIsEmpty(fn_tree.item)){
				$.each(fn_tree.item,function(i,item){
					console.log("====initDeptData : ",item);
					tree.createDeptNode(item);
				});  
				tree.$dtree.jstree("open_node", deptid);
				tree.$dtree.jstree("select_node", deptid);	
			}
		});
	},
	createDeptNode : function(item) {
		var tree = this;
		tree.$dtree.jstree('create_node', (item.upid == companyid) ? "#" : item.upid, {
			"text" : item.name,
			"id" : item.deptid,
			"icon" : (item.isactive == "Y") ? ADMINTREEICONS["DEPT"] : TREEICONS["N"],
			"state" : {
				"opened" : false
			},
			"a_attr" : {
				"code" : item.code,
				"priority" : item.priority,
				"isactive" : item.isactive,
				"type" : "D" 
			}
		}, "last", false, false);

	},
	createFolderNode : function(item) {
		var tree = this;
		tree.$dtree.jstree('create_node', item.upid, {
			"text" : item.name,
			"id" : item.classid,
			"icon" : (item.isactive == "Y") ? item.icon: TREEICONS["N"],
			"state" : {
				"opened" : false
			},
			"a_attr" : {
				"code" : item.code,
				"priority" : item.priority,
				"isactive" : item.isactive,
				"type" : "F"
			}
		}, "last", false, false);
       

	},getFolderList : function(upid) {
		var tree = this;
		$("#GroupUserData").empty();
		var sendData = {
			"objIsTest" : "N",
			"objSkipAcl" : true,
			"upid" : upid,
			"types" : CLSTYPES["DEPT"],
			objIsMngMode : true
		};

		$.ajax({
			url : "${ctxRoot}/api/classification/list/down",
			type : "POST",
			dataType : "json",
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
				//alert("data ==> "    +JSON.stringify(data));
				console.log("data : ", data);
				if (data.status == "0000") {
					if (objectIsEmpty(data.result)) {
						return;
					}

				$.each(data.result, function(i, item) {
					var itemCls = item.zappClassification;
					itemCls.icon = TREEICONS["0"];
					tree.createFolderNode(itemCls);
				});
				tree.$dtree.jstree("open_node", upid);
             
				} else {
					alertErr(data.message);
				}	
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			}
		})
	},
	relocateNodeInfo : function() {
		var sendData = {
			data : {
				objIsTest : "N",
				objDebugged : false,
         /* 	companyid : "${Authentication.objCompanyid}",
         	holderid : "${Authentication.sessOnlyDeptUser.deptuserid}",
         	types : "N2" //'분류 유형 (01: 노드, 02: 분류체계, 03:문서유형...)' */
			}
		};

		console.log("====fn_move.select : ",fn_move.select);
		console.log("====fn_tree.select : ",fn_tree.select);
		sendData.success = "<spring:eval expression="@${msgLang}['FOLDER_HAS_MOVED']"/>";
		sendData.url = '${ctxRoot}/api/classification/relocate';
		sendData.data.classid = fn_tree.select.classid; //fn_tree.select.classid
		sendData.data.upid = fn_move.select.id; //fn_move.select.id
		console.log("=====relocate sendData : " ,sendData);
		$.ajax({
			type : 'POST',
			url : sendData.url,
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData.data),
			success : function(data) {
				console.log("data : " + JSON.stringify(data));
				if (data.status == "0000") {
					alert(sendData.success);
					AllReset();
					$("#NodeMoveArea").hide();
					$("#btnMove").hide();
					fn_tree.removeNode(fn_tree.select.classid);
					fn_tree.getFolderLoad(fn_tree.select.classid,fn_tree.select.classid);
				}else {
					alert(data.status);
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			}
		});
	}
}

var reorder = function(priority) {
    var sendData = {
      data : {
        objIsTest : "N",
        objDebugged : false,
      }
    };

    sendData.confirmMessage = "<spring:eval expression="@${msgLang}['DO_YOU_MOVE_DEPT_INFO']"/>";
    sendData.url = 'api/organ/dept/reorder';
    sendData.data.deptid = fn_tree.select.classid;
    sendData.data.priority = priority;
    reorderNodeInfo(priority);
};

  

</script>
</head>
	<body>
		<!-- 기관 아이디 -->
		<input type="hidden" id="CompanyID" value="" />
		<input type="hidden" id="MobCompanyID" value="" />

		<!--header stard-->
		<c:import url="../common/TopPage.jsp" />
		<!--header end-->
		<main>
			<div class="bg" style="display:none;"></div>
			<div class="flx">
				<c:import url="../common/AdminLeftMenu.jsp" />
				<section id="content">
					<div class="innerWrap innerWrap_scroll">
						<h2 class="pageTit_reorder">
							<img src="${image}/icon/Group 156.png" alt=""><spring:eval expression="@${lang}['DEPARTMENT_FOLDER_BOX']" />
							<img src="${image}/icon/bt_up.png" title="<spring:eval expression="@${lang}['MOVE_DEPARTMENT_ORDER']"/>" style="display: none; cursor: pointer; margin-right: 15px; line-height: 26px; height: 38px;  padding: 5px;float: right" id="reorder" name="up" />
            				<img src="${image}/icon/bt_down.png" title="<spring:eval expression="@${lang}['MOVE_DEPARTMENT_ORDER']"/>" style="display: none; cursor: pointer; margin-right: 15px; line-height: 26px; height: 38px;  padding: 5px;float: right" id="reorder" name="down" />
<%-- 							<div id="priority_tab" style="display: none;float:right;">
								<label><spring:eval expression="@${lang}['FOLDER_ORDER']" /></label>
								<select style="width: 100px; margin-left: 10px;" id="folderPriority">
								</select>
							</div> --%>
						</h2>
						<div class="flex-content">
							<div class="contNav">
								<ul id="znTree"></ul>
							</div><!--contNav//-->
							<div class="rgt_area_deptmng">
								<div class="wdt100">
									<h3 class="innerTit"><spring:eval expression="@${lang}['FOLDER_INFORMATION']" /></h3>
									<div class="btn_wrap">
										<button type="button" class="btbase" id="btnMove" style="display: none"><spring:eval expression="@${lang}['MOVE_FOLDER']" /></button>
										<button type="button" class="btbase" id="btnInit" ><spring:eval expression="@${lang}['INITIALIZATION']" /></button>
										<button type="button" class="btbase" id="btnNewGroup" style="display: none;"><spring:eval expression="@${lang}['SAVE']" /></button>
										<button type="button" class="btbase" id="btnDel" style="display: none;"><spring:eval expression="@${lang}['DELETE']" /></button> 
										<button type="button" class="btbase" id="btnDis" style="display: none;"><spring:eval expression="@${lang}['DISCARD']" /></button>
									</div>
									<table class="inner_tbl">
										<colgroup>
											<col width="30%">
											<col width="30%">
											<col width="25%">
											<col width="15%">
										</colgroup>
										<thead>
											<th><spring:eval expression="@${lang}['FOLDER_BOX_CODE']" /></th>
											<th><spring:eval expression="@${lang}['FOLDER_BOX_NAME']" /></th>
											<th><spring:eval expression="@${lang}['FOLDER_BOX_DESCRIPTION']" /></th>
											<th><spring:eval expression="@${lang}['USE_OR_NOT']" /></th>
										</thead>
										<tbody>
											<tr id="deptinput">
												<td>
													<input type="text" value="" id="ClassId" onkeyup='pubByteCheckTextarea(event,30)' maxlength="45" style="width: 90%;" title="<spring:eval expression="@${lang}['DEPARTMENT_CODE']"/>">
												</td>
												<td>
													<input type="text" value="" id="ClassName" onkeyup='pubByteCheckTextarea(event,300)' maxlength="500" style="width: 90%;" title="<spring:eval expression="@${lang}['DEPARTMENT_NAME']"/>">
												</td>
												<td>
													<input type="text" value="" id="ClassDescpt" onkeyup='pubByteCheckTextarea(event,50)' maxlength="500" style="width: 90%;" title="<spring:eval expression="@${lang}['DEPARTMENT_ABBREVIATION']"/>">
												</td>
												<td id="ClassState"></td>
												
											</tr>
										</tbody>
									</table>
								</div>
								<div class="wdt100">
									<h3 class="innerTit"><spring:eval expression="@${lang}['APPROVAL_INFO']" /></h3>
									<table class="inner_tbl">
										<colgroup>
											<col width="34%">
											<col width="33%">
											<col width="33%">
										</colgroup>
										<thead>
											<th><spring:eval expression="@${lang}['APPROVAL_GROUP']" /></th>
											<th><spring:eval expression="@${lang}['CODE_VALUE']" /></th>
											<th><spring:eval expression="@${lang}['FOLDER_APPROVAL_DESC']" /></th>
										</thead>
										<tbody id="ApprovalInfo">
										</tbody>
									</table>
									<div class="inner_tbl" style="display: none;" id="AppovalGroup">
										<table style="width: 100%;">
											<colgroup>
												<col width="50%">
												<col width="50%">
											</colgroup>
											<thead>
												<tr>
													<th><spring:eval expression="@${lang}['GROUP_MEMBER']" /></th>
													<th><spring:eval expression="@${lang}['GROUP_TYPE']" /></th>
												</tr>
											</thead>
										</table>
										<div style="overflow-y:scroll;min-height:30px;max-height:108px;">
											<table>
												<colgroup>
													<col width="50%">
													<col width="50%">
												</colgroup>
												<tbody id="AppovalGroupUserData">
												</tbody>
											</table>
										</div>
									</div>
								</div>
								<div class="wdt100">
									<div style="width:48%;float: left;">
										<h3 class="innerTit"><spring:eval expression="@${lang}['ABOUT_FOLDER_PERMISSIONS']" /></h3>
										<table class="inner_tbl">
											<colgroup>
												<col width="4%">
												<col width="35%">
												<col width="15%">
												<col width="27%">
												<col width="19%">
											</colgroup>
											<thead>
												<th><input type="checkbox" id="GroupInfoALLCheck" name="GroupInfoALLCheck"><label for ="GroupInfoALLCheck"></label></th>
												<th><spring:eval expression="@${lang}['AUTHORITY_TARGET']" /></th>
												<th><spring:eval expression="@${lang}['AUTHORITY_TYPE']" /></th>
												<th><spring:eval expression="@${lang}['FOLDER_AUTHORITY']" /></th>
												<th><spring:eval expression="@${lang}['DOC_AUTHORITY']" /></th>
											</thead>
										</table>
										<div style="height:300px;overflow-y:scroll">
											<table class="inner_tbl" style="margin:auto">
												<colgroup>
													<col width="4%">
													<col width="35%">
													<col width="15%">
													<col width="27%">
													<col width="19%">
												</colgroup>
												<tbody id="GroupUserData">
												</tbody>
											</table>
										</div>
									</div>
									<div style="width:3%;float: left;">
										<div style="margin-top: 160px;">
											<img src="${image}/icon/bt_left.png" style="cursor: pointer;width: 40px; height:40px;" id="btnGroupUserAdd" name="left" />
										</div>
										<div>
											<img src="${image}/icon/bt_right.png" style="cursor: pointer;margin-top: 15px; width: 40px; height: 40px;" id="btnGroupModDetail" name="right" />
										</div>
									</div>
									<div style="width:47%;float: right;margin-top:45px">
										<div class="addGroupuser">
											<div>
											<ul class="tabmenu">
												<li class="on" id="tab_Btn_Organ"><spring:eval expression="@${lang}['ORGANIZATION']" /></li>
												<li id="tab_Btn_Group"><spring:eval expression="@${lang}['GROUP']" /></li>
												<li id="tab_Btn_Search"><spring:eval expression="@${lang}['SEARCH']" /></li>
											</ul>
											</div>
											<div class="tabCont">
												<div class="contdiv" id="cont01">
													<div id="OrgnArea" style="height:270px;padding:0;overflow:unset;">
														<div id="Dept_Tree" style="float: left; width: 60%; height: 100%; overflow-y: auto; zoom: 1; border: 1px solid #d2d2d2; background-color: rgb(245, 245, 245);">
															<ul id="znTreeDept"></ul>
														</div>
														<div style="float: right;height:100%; width: 40%; border:0px;padding:0px;overflow:unset;">
															<table class="inner_tbl" style="margin-top:0;height:38px">
															<caption></caption>
																<colgroup>
																	<col width="10%">
																	<col width="90%">
																</colgroup>
																<thead>
																	<tr>
																		<th><input type="checkbox" name="OrganAreaUserALLCheck" id="OrganAreaUserALLCheck"><label for ="OrganAreaUserALLCheck"></label></th>
																		<th><spring:eval expression="@${lang}['USER_NAME']" /></th>
																	</tr>
																</thead>
															</table>
															<div style="height: calc(100% - 50px);border:none;overflow:scroll;">
																<table style="margin-top:0" class="inner_tbl">
																	<colgroup>	
																		<col width="10%">
																		<col width="90%">
																	</colgroup>
																	<tbody id="OrganAreaUserData" style="border-top:0;">
																	</tbody>
																</table>
															</div>
														</div>
													</div>
												</div><!--cont01//-->
												<div class="contdiv" id="cont02">
													<div id="GroupArea" style="height:270px;">
														<div class="Group_Tree" style="float: left; width: 60%; height: 100%; overflow-y: auto; zoom: 1; border: 1px solid #d2d2d2; background-color: rgb(245, 245, 245);">
															<ul id="znTreeGroup"></ul>
														</div>
														<div style="float: right;height:100%; width: 40%; border:0px;padding:0px;overflow:unset;" >
															<table class="inner_tbl" style="margin-top:0;height:38px">
															<caption></caption>
																<colgroup>
																	<col width="10%">
																	<col width="90%">
																</colgroup>
																<thead>
																	<tr>
																		<th><input type="checkbox" name="GroupAreaUserALLCheck" id="GroupAreaUserALLCheck" disabled><label for ="GroupAreaUserALLCheck"></label></th>
																		<th><spring:eval expression="@${lang}['MEMBER']" /></th>
																	</tr>
																</thead>
															</table>
															<div style="height: calc(100% - 50px);border:none;overflow:scroll;">
																<table style="margin-top:0" class="inner_tbl">
																	<colgroup>	
																		<col width="10%">
																		<col width="90%">
																	</colgroup>
																	<tbody id="GroupAreaUserData" style="border-top:0;">
																	</tbody>
																</table>
															</div>
														</div>
													</div>
												</div> <!--cont02-->
												<div class="contdiv" id="cont03">
													<div id="SearchArea">
														<div class="inner_uiGroup mgt0">
															<p style="width:24px"><spring:eval expression="@${lang}['TARGET']" /></p>
															<input type="radio" name="searchTarget" value="DEPT" id="S_dept" checked><label for="rd01"><spring:eval expression="@${lang}['DEPARTMENT']" /></label>
															<input type="radio" name="searchTarget" value="USER" id="S_user" ><label for="rd02" ><spring:eval expression="@${lang}['USER']" /></label>
															<div style="display:inline;float:right;width:52%;margin-right:-15px;">
																<input type="text" value="" name="searchData" id="S_data" style="width:59%;" />
																<button type="button" class="btbase" id="btnOrganSearch"><spring:eval expression="@${lang}['SEARCH']" /></button>
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
														<div style="height: 150px;border:none;overflow:scroll;">
															<table style="margin-top:0" class="inner_tbl">
																<colgroup>	
																	<col width="10%">
																	<col width="90%">
																</colgroup>
																<tbody id="SearchAreaUserData" style="border-top:0;">
																</tbody>
															</table>
														</div>
													</div>  
												</div><!--cont03-->       
											</div>
										</div>
									</div>
								</div>
							</div><!--rgt_area//-->
						</div><!--flex-content//--> 
						<!-- 폴더이동 팝업 -->
						<div id="NodeMoveArea" style="display: none; width: 400px; height: 540px;">
							<div class="popup" style="display: block;">
								<h3 class="pageTit"><spring:eval expression="@${lang}['MOVE_FOLDER']" /></h3>
								<button type="button" id="closeBtn">
									<img src="${image}/icon/x.png">
								</button>
								<ul class="tabmenu" style="display:none;">
									<li class="on"></li>
								</ul>
								<div class="tabCont" style="width:400px">
									<div id="cont03" class="contdiv" style="display:block;">
										<div class="contNav" style="width:100%;height:400px;">
											<div style="text-align: left;" id="MOVE_Tree">
												<ul id="znTreeMove"></ul>
											</div>
										</div>

									</div>
								</div>
								<button id="btnMoveSave" type="button" class="btbase" style="margin-top: 10px; margin-bottom:-15px; position: relative; left: 33%;color:#fff;background:#374162;"><spring:eval expression="@${lang}['SAVE']"/></button>
								<button id="btnMoveClose" type="button" class="btbase" style="margin-top: 10px; margin-bottom:-15px; position: relative; left: 37%"><spring:eval expression="@${lang}['CLOSE']"/></button>
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

