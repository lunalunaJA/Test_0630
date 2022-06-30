<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ECM4.0 :: 권한</title>
<script type="text/javascript" src="${js}/common.js"></script>
<script type="text/javascript">

if (!String.prototype.startsWith) {
	String.prototype.startsWith = function(search, pos) {
		return this.substr(!pos || pos < 0 ? 0 : +pos, search.length) === search;
	};
}


var fn_DocReg_Tree = {
	consoleLog : function(data) {
		$.each(data, function(i, v) {
		});
	},
	treeId : "organTree",
	$tree : {},
	root : {
		id : companyid,
		type : CLSTYPES["COMPANY"]
	},
	initData : function() {
		var companyData = [];
		companyData.push({
			id : companyid,
			parent : "#",
			icon : POPTREE["COMPANY"],
			text : companyName,
			a_attr : {
				type : "N1",
      class: "no_checkbox"
			}
		});//companyid
		return companyData;
	},
	jstree : function() {
		var that = this;
		this.$tree = $('#'+this.treeId);
		this.$tree.jstree({
			core : {
				check_callback : true,
				data : fn_DocReg_Tree.initData()
			/* 최초에 보여지 최상위 Root Tree */
			},
			types : {
				"default" : {
					"icon" : "glyphicon glyphicon-flash"
				},
				file : {
					icon : "fa fa-file text-inverse fa-lg"
				}
			},
			checkbox : {
				"three_state" : false,
				"whole_node" : false,
				"tie_selection" : false
			},
			plugins : [ "checkbox", "massload", "unique" ]
		}).on("select_node.jstree", function(event, data) { // 노드가 선택된 뒤 처리할 이벤트   	 
			var id = data.node.id;
			var type = data.node.a_attr.type;
			if (type === GROUPCODES["DEPT"])
				fn_DocReg_Tree.getUserList(data.node.id);
		}).on("loaded.jstree", function() {
			//root node 로드된후 처음 한번 이벤트
			fn_DocReg_Tree.getDeptList();				
			fn_DocReg_Tree.openNode(fn_DocReg_Tree.root.id);
		});
	},
	getCheckNodes : function() {
		//부서 체크 정보
		$.each(this.$tree.jstree("get_checked", true), function() {
			//alert(this.id);
			var id = this.id;
			var node = fn_DocReg_Tree.getNode(id);//$('#organTree').jstree(true).get_node(id);
			var attr = node.a_attr;
			var share = {};
			share.readerid = node.id;
			share.name = node.text;
			share.readertype = attr.type;
			console.log("share : ", share);
			addShareRow(share);
		});
		
		
		
		//사용자 체크 정보
		$("#organTreeUserList").find("input[name='chkUser']:checked").each(function() {
		
			let $li = $(this).parent().parent();
			var node = $li.data("meta");
			var attr = node.a_attr;
			var share = {};
			share.readerid = node.id;
			share.name = node.text;
			share.readertype = attr.type;
			addShareRow(share);
		});
	},
	unCheckNode : function(id) {
		this.$tree.jstree("uncheck_node", id);
	},
	getNode : function(id) {			
		return this.$tree.jstree(true).get_node(id);
	},
	openNode : function(id) {			
		this.$tree.jstree("open_node", id);
	},
	getDeptList : function() {
		var that = this;
		$.ajax({
			url : "${ctxRoot}/api/organ/dept/list/down",
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify({
				upid : companyid,
				companyid : companyid,
				isactive : 'Y'
			}),
			success : function(data) {
				$.each(data.result, function(i, obj) {
					var attr = {}
					attr.type = "02";
					var child = {};
					child.id = obj.deptid;
					child.text = obj.name;
					child.icon = POPTREE["DEPT"];
					child.a_attr = attr;
					that.$tree.jstree('create_node', obj.upid, child,
							"last", false, false);
				});
			},
		  error : function(request, status, error) {
        alertNoty(request,status,error);
			},
			beforeSend : function() {
			},
			complete : function() {
			}
		});
	},
	getUserList : function(selectedNode) {
		$.ajax({
			url : "${ctxRoot}/api/organ/deptuser/list",
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify({
				deptid : selectedNode,
				isactive : 'Y'
			}),
			success : function(data) {
				$.each(data.result, function(i, obj) {
					var attr = {}
					attr.type = "01";
					var child = {};
					child.id = obj.deptuserid;
					child.text = obj.zappUser.name;
					child.icon = POPTREE["USER"];
					child.a_attr = attr;
					child.upid = selectedNode;
					addUserList(child);
				});
			},
		  error : function(request, status, error) {
        alertNoty(request,status,error);
			},
			beforeSend : function() {
			},
			complete : function() {
			}
		});
	}
}
//Tree End
var group ={};
group.enterprise="<spring:eval expression="@${lang}['COMPANY_GROUP']"/>"
group.department="<spring:eval expression="@${lang}['DEPARTMENT_GROUP']"/>"
group.collaborative="<spring:eval expression="@${lang}['COLLABORATIVE_GROUP']"/>"
group.personal="<spring:eval expression="@${lang}['PERSONAL_GROUP']"/>"
group.supergroup="<spring:eval expression="@${lang}['SUPER_GROUP']"/>"

var fn_DocRegGroup_Tree = {
	consoleLog : function(data) {
		$.each(data, function(i, v) {
		});
	},
	treeId : "groupTree",
	$tree : {},
	initData : function() {
		var companyGroup = {
			id : companyid,
			parent : "#",
			icon : POPTREE["COMPANY"],
			text : group.enterprise,
			a_attr : {
				type : GROUPCODES["COMPANY"],
				root : true,
      class: "no_checkbox"
			}
		};
		var userGroup = {
			id : deptuserid,
			parent : "#",
			icon : POPTREE["COMPANY"],
			text : group.personal,
			a_attr : {
				type : GROUPCODES["USER"],
				root : true,
      class: "no_checkbox"
			}
		};
		var collaborGroup = {
			id : "04",
			parent : "#",
			icon : POPTREE["COMPANY"],
			text : group.collaborative,
			a_attr : {
				type : GROUPCODES["COLLABO"],
				root : true,
      class: "no_checkbox"
			}
		};
		var groupRootNode = [ companyGroup,  userGroup,
				collaborGroup];
		return groupRootNode;
	},
	jstree : function() {
	 	 var tree = this;
		this.$tree = $("#"+this.treeId);
		this.$tree.jstree({
			core : {
				check_callback : true,
				data : fn_DocRegGroup_Tree.initData(),
			},
			types : {
				"default" : {
					"icon" : "glyphicon glyphicon-flash"
				},
				file : {
					icon : "fa fa-file text-inverse fa-lg"
				}
			},
			checkbox : {
				"three_state" : false,
				"whole_node" : false,
				"tie_selection" : false
			},
			plugins : [ "checkbox", "massload", "unique" ]
		}).on("select_node.jstree", function(event, data) { // 노드가 선택된 뒤 처리할 이벤트   
		}).on("loaded.jstree", function() {
			console.log("fn_DocRegGroup_Tree : " + tree.initData());
			$.each(tree.initData(), function(index, item){
				console.log("item.a_attr.type : " +item.a_attr.type);
			  tree.getGroupList(item.a_attr.type);
			});
		});
	},
	getCheckNodes : function() {
		var result = 	this.$tree.jstree("get_checked", true);
	  console.log("===getCheckNodes : "+result);
	
		for (var i = 0; i < result.length; i++) {
			var id = result[i];
			var node = fn_DocRegGroup_Tree.getNode(id);
			var attr = node.a_attr;
			if (!attr.root) {
				var share = {};
				share.readerid = node.id;
				share.name = node.text;
				share.readertype = "03"; //그룹 유형		
				addShareRow(share);
			} else {
				fn_DocRegGroup_Tree.unCheckNode(node.id);
			}
		}
	},
	unCheckNode : function(id) {
		this.$tree.jstree("uncheck_node", id);
	},
	getNode : function(id) {			
		return this.$tree.jstree(true).get_node(id);
	},
	getGroupList : function(type) {
		 var sendData = {
				 "objIsTest" : "N",
				 "zappGroup" : { 
								 "types":type,
								 "companyid":companyid,
								 "isactive":"Y",
					           }
						};
		 
		if(type=="01"){
			sendData.zappGroup.upid = companyid;
		}else if(type=="03"){
			sendData.zappGroup.upid = deptuserid;
		}else if(type=="04"){
			sendData.zappGroup.upid = companyid;
		}
		 
		 
		$.ajax({
			url : "${ctxRoot}/api/organ/group/list",
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
			  console.log("==groupList : ",data);
				$.each(data.result, function(i, obj) {
					var attr = {}
					attr.type = "03";
					var child = {};
					child.id = obj.groupid;
					child.text = obj.name;
					child.icon = POPTREE["GOURP"];
					child.a_attr = attr;
					var upid = obj.upid;
					if(type == GROUPCODES["DEPT"]){
					  upid = GROUPCODES["DEPT"];
					}else if(type == GROUPCODES["USER"]){
					  upid = deptuserid;
					}else if(type == GROUPCODES["COLLABO"]){
					  upid = GROUPCODES["COLLABO"];
					}
					$('#groupTree').jstree('create_node', upid, child,
							"last", false, false);
				});
			},
		  error : function(request, status, error) {
        alertNoty(request,status,error);
			},
			beforeSend : function() {
			},
			complete : function() {
			}
		});
	}
}
//GroupTree End

//권한 추가
var addUserList = function(user) {
	//설정되지 않은 권한만 추가		
	console.log("addUserList : ", user)
	var child = {};
	child.id = user.id;
	child.a_attr = user.a_attr;
	child.icon = POPTREE["USER"];
	child.text = user.text;
	
	
	console.log("child : ", child);
	
	$("#organTree").jstree("create_node", user.upid, child,"last", false, false);
	$("#organTree").jstree("open_node", user.upid);
}	


var setServerShare = function(data) {
	console.log("setServerShare : ", data);
	if(data.length > 0) {
		$("#shareObjList").empty();

		$.each(data,function(i,share){				

			var $tr = $('<tr></tr>');
			var inHtml = "";
			var typeName = "<spring:eval expression="@${lang}['USER']"/>";
			var type = share.readertype;
			if (type == "02") {
				typeName = "<spring:eval expression="@${lang}['DEPARTMENT']"/>"
			} else if (type == "03") {
				typeName = "<spring:eval expression="@${lang}['GROUP']"/>"
			}
			;
			inHtml += "<td align='center'><input type='checkbox' name='chkAddAcl' id='chkAddAcl"+share.shareobjid+"' value='"+share.shareobjid+"'></td>";
			inHtml += "<td>" + typeName + "</td>";
			inHtml += "<td>" + share.readername + "</td>";

			var item = {};
			item.shareobjid = share.shareobjid;	
			item.readerid = share.readerid;
			item.readertype = share.readertype;
			$tr.data('meta', item).append(inHtml);
			$("#shareObjList").append($tr);
		});
		
	}else{
		$("#shareObjList").empty();
	}
}

//조직탭,그룹탭에따라 호출 함수 분기
var selectShare = liTabNum;
var addShare = function() {
  console.log("===liTabNum : "+ liTabNum);
  console.log("===selectShare : "+ selectShare);
	 if (liTabNum == 0) {
		fn_DocReg_Tree.getCheckNodes();
	} else {
		fn_DocRegGroup_Tree.getCheckNodes();
	}
}
//권한 추가
var addShareRow = function(share) {
	var shareList = settedShareList();
	
	var setted = false;
	//multi set modify
	console.log("shareList.length : "+shareList.length);
	for (var i = 0; i < shareList.length; i++) {
		var settedShare = shareList[i];			
		console.log("settedShare : "+JSON.stringify(settedShare));
		console.log("share : "+JSON.stringify(share));
		if (settedShare.readerid == share.readerid) {
			setted = true;
			continue;
		}
	}
	
	console.log("setted : "+setted);
	console.log("addShareRow share : ",share);
	//설정되지 않은 권한만 추가
	if (!setted) {
		var $tr = $('<tr></tr>');
		var inHtml = "";
		var typeName = "<spring:eval expression="@${lang}['USER']"/>";
		var type = share.readertype;
		if (type == "02") {
			typeName = "<spring:eval expression="@${lang}['DEPARTMENT']"/>"
		} else if (type == "03") {
			typeName = "<spring:eval expression="@${lang}['GROUP']"/>"
		}
		;
		inHtml += "<td align='center'><input type='checkbox' name='chkAddAcl' id='chkAddAcl"+share.readerid+"' value='"+share.readerid+"'><label for='chkAddAcl"+share.readerid+"'></label></td>";
		inHtml += "<td>" + typeName + "</td>";
		inHtml += "<td>" + share.name + "</td>";

		
		share.objAction = "ADD";
		$tr.data('meta', share).append(inHtml);
		var tbName ="";
		
		console.log("addShareRow liTabNum : " + liTabNum);
		
		if(liTabNum == 0){
			tbName = "shareObjList1";
		}else{
			tbName = "shareObjList2";
		}
		$("#"+tbName).append($tr);	
	}
}



var delSeverShareArr = [];

//설정된 권한을 삭제한다.
var delShare = function() {
	var tbName ="";
	if(liTabNum == 0){
		tbName = "shareObjList1";
	}else{
		tbName = "shareObjList2";
	}
	
	$("#"+tbName).find("input[name='chkAddAcl']:checked").each(
		function() {
			var $tr = $(this).parent().parent();
			var data = $tr.data("meta");
			if (selectShare == 1) {
				//체크 되어 있을경우 언체크
				if (data.readertype == "02") {
					fn_DocReg_Tree.unCheckNode(data.readerid);
				} else {

				}
			} else {
				fn_DocRegGroup_Tree.unCheckNode(data.readerid);
			}
			$tr.remove();
			//서버에 등록된 데이터 여부 판별하여 서버 데이터를 삭제 했을경우 삭제 데이터에 추가
			if (data.shareobjid) {
				var delshare = {};
				delshare.shareobjid = data.shareobjid;
				delshare.objAction = "DISCARD";
				delSeverShareArr.push(delshare);
			}
		}
	);
}

//설정된 권한 목록
var settedShareList = function() {
	var Items = [];
	if(liTabNum == 0){
		$("#shareObjList1").find('tr').each(function(idx) {
			var item = $(this).data('meta');
			if(item){
				Items.push(item);
			}
		});		
	}else{
		$("#shareObjList2").find('tr').each(function(idx) {
			var item = $(this).data('meta');
			if(item){
				Items.push(item);
			}
		});
	}
	return Items;
}

//공유대상을 변경할 목록
var getEditShareList = function() {
	var Items = [];
	//권한 목록에서 변경된 사항만 담는다.
	$("#shareObjList1").find('tr').each(function(idx) {
		var item = $(this).data('meta');
		if (item) {
			if (item.objAction)
				Items.push(item); //변경사항이 있을경우에만 추가
		}
	});
	
	$("#shareObjList2").find('tr').each(function(idx) {
		var item = $(this).data('meta');
		if (item) {
			if (item.objAction)
				Items.push(item); //변경사항이 있을경우에만 추가
		}
	});
	//삭제된 목록이 있으면 삭제된 목록 병합		
	var shareArray = Items.concat(delSeverShareArr);
	//삭제또는 추가할 공유대상
	console.log("shareArray : " + JSON.stringify(shareArray));
	return shareArray;
}

//권한 관련 끝

//수정전 삭제정보를 초기화
var editDelInfoInit = function() {		
	delSeverShareArr = [];
}


var fn_getSharedList = function(){
	var contentid = shareCallBack.data.contentid;
	var data = {};
		data.objIsTest = "N";
		data.objDebugged = false;
		data.contentid = contentid;
		data.objType = "01";
		console.log("share list param : "+JSON.stringify(data));
	$.ajax({ 
		url : "${ctxRoot}/api/content/share/list" ,
		type : "POST" , 
		dataType : 'json',
		contentType : 'application/json',
		async : false , 
		data : JSON.stringify(data) , 
		success : function(data){ 						
			 console.log("share list result : "+JSON.stringify(data));
			 if(data.status=="0000"){
				 setServerShare(data.result);
			 }
		}, 
	  error : function(request, status, error) {
    alertNoty(request,status,error);
		} , 
		beforeSend : function() {} , 
		complete : function() {} 
	});
}

$(document).ready(function() {

	editDelInfoInit();
	fn_DocReg_Tree.jstree();
	fn_DocRegGroup_Tree.jstree();
	
	$('#allchk').unbind("click").bind("click", function(){
        if($('#allchk').prop("checked")){
            $('table input[type=checkbox]').prop('checked',true);
            $('table tbody input[type=checkbox]').closest('tr').css('background-color','#fff6de');
        } else {
            $('table input[type=checkbox]').prop('checked',false);
            $('table tbody input[type=checkbox]').closest('tr').css('background-color','inherit');
        }
    });
	
	$('#allchk1').unbind("click").bind("click", function(){
        if($('#allchk').prop("checked")){
            $('table input[type=checkbox]').prop('checked',true);
            $('table tbody input[type=checkbox]').closest('tr').css('background-color','#fff6de');
        } else {
            $('table input[type=checkbox]').prop('checked',false);
            $('table tbody input[type=checkbox]').closest('tr').css('background-color','inherit');
        }
    });
	
	// 팝업 닫기
	$('#closeBtn').unbind("click").bind("click", function(){
		$('.bg').fadeOut();
        $('.popup').fadeOut();
    });
	
});


var shareCallBack;//레이어 팝업 호출시 데이터 할당
//메인페이지에서 넘긴 콜백 함수 호출
var callback = function(){
	//현재 레이어에서 선택된 데이터 담기
	var items = getEditShareList();
	var sharedObject = [];
	if(items.length>0){
		//변경할 공유대상 정보 설정
		shareCallBack.data.zappSharedObjects = items;
		//콜백함수 호출
		shareCallBack["func"](shareCallBack.data,shareCallBack.param.list);
	}else{
		alert("<spring:eval expression="@${msgLang}['NO_SHARE_TARGET']"/>");
	}
	
}


</script>
</head>
<body>
	<!-- 팝업 -->
	<div class="popup" style="display: block;">
		<h3 class="pageTit">문서공유</h3>
		<button type="button" id="closeBtn">
			<img src="${image}/icon/x.png">
		</button>
		<ul class="tabmenu">
			<li class="on" class="on">부서</li>
			<li>그룹</li>
		</ul>
		<div class="tabCont">
			<!--cont01-->
			<div id="cont03" class="contdiv" style="display:block;">
				<h3 class="innerTit">부서 정보</h3>
				<div class="flex-content">
					<div class="cont_list" >
						<div id="organTree" class="sub"></div>
					</div>
					<div>
						<button type="button" onclick="addShare();">
							<img src="${image}/icon/bt_right.png">
						</button>
						<button type="button" onclick="delShare();">
							<img src="${image}/icon/bt_left.png">
						</button>
					</div>
					<div>
						<table class="pop_tbl">
							<colgroup>
								<col width="20%">
								<col width="40%">
								<col width="40%">
							</colgroup>
							<thead>
								<th><input type="checkbox" id="allchk"><label for="allchk"></label></th>
								<th>권한유형</th>
								<th>공유대상</th>
							</thead>
							<tbody id ="shareObjList1">
							</tbody>
						</table>
					</div>
				</div>
			</div>
			<div class="contdiv" id="cont04" style="display:none;">
				<h3 class="innerTit">그룹 정보</h3>
				<div class="flex-content">
					<div class="cont_list">
						<div id="groupTree" class="sub"></div>
					</div>
					<div>
						<button type="button" onclick="addShare();">
							<img src="${image}/icon/bt_right.png">
						</button>
						<button type="button" onclick="delShare();">
							<img src="${image}/icon/bt_left.png">
						</button>
						
					</div>
					<div>
						<table class="pop_tbl">
							<colgroup>
								<col width="20%">
								<col width="40%">
								<col width="40%">
							</colgroup>
							<thead>
								<th><input type="checkbox" id="allchk1"><label for="allchk1"></label></th>
								<th>권한유형</th>
								<th>공유대상</th>
							</thead>
							<tbody id = "shareObjList2">
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		<div style="text-align: center;">
			<button class="btbase"  onclick="javascript:callback();">저장</button>
		</div>
	</div>
</body>
</html>