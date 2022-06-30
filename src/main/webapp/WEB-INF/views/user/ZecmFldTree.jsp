<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ECM4.0 :: 폴더트리</title>
<script type="text/javascript">

	var lsCallBack;//레이어 팝업 호출시 데이터 할당

	//Tree Start
	if (!String.prototype.startsWith) {
		String.prototype.startsWith = function(search, pos) {
			return this.substr(!pos || pos < 0 ? 0 : +pos, search.length) === search;
		};
	}

	//OgranTree Start
	var fn_Fld_Pop_Tree = {
			
		consoleLog : function(data) {
			$.each(data, function(i, v) {
			});
		},
		rootId : companyid,
		//rootId : "N4",
		selectedId : "",
		initData : function() {
			var rootData = [];
			// N1:전사문서함, N2:부서문서함, N3:개인문서함, N4:협업문서함
			console.log("fn_Fld_Pop_Tree selectedClsType : " + selectedClsType);
			if ("${Authentication.sessOnlyDeptUser.usertype}" == "03"
					&& selectedClsType == "N3") {
				rootData.push({
							id : this.rootId,
							parent : "#",
							icon : POPTREE["COMPANY"],
							//text : "문서함",
							text : "<spring:eval expression="@${lang}['COMPANY_FOLDER_BOX']"/>",
							a_attr : {
								type : CLSTYPES["COMPANY"],
								nsearch : true
							}
						});//companyid
			}
			if (selectedClsType == "N2" || selectedClsType == "N3") {
				rootData.push({
							id : CLSTYPES["DEPT"],
							parent : "#",
							icon : POPTREE["DEPTGROUP"],
							text : "<spring:eval expression="@${lang}['DEPARTMENT_FOLDER_BOX']"/>",
							a_attr : {
								type : CLSTYPES["DEPT"],
								nsearch : true
							}
						});//deptid
			}
			if (selectedClsType == "N4" || selectedClsType == "N3") {
				rootData.push({
							id : CLSTYPES["COLLABO"],
							parent : "#",
							icon : POPTREE["COLLABOGROUP"],
							text : "<spring:eval expression="@${lang}['COLLABORATIVE_FOLDER_BOX']"/>",
							a_attr : {
								type : CLSTYPES["COLLABO"],
								nsearch : true
							}
						});//userid
			}
			if (selectedClsType == "N3") {
				rootData.push({
							id : userid,
							parent : "#",
							icon : POPTREE["USER"],
							text : "<spring:eval expression="@${lang}['PERSONAL_FOLDER_BOX']"/>",
							a_attr : {
								type : CLSTYPES["USER"],
								nsearch : true
							}
						});//userid
			}
			return rootData;
		},
		tree : {},
		jstree : function() {
			var tree = this;
			this.tree = $('#fldPopTree');
			$('#fldPopTree')
					.jstree({
						core : {
							check_callback : true,
							data : fn_Fld_Pop_Tree.initData()
						/* 최초에 보여지 최상위 Root Tree */
						},
						types : {
							"default" : {"icon" : "glyphicon glyphicon-flash"},
							file : {icon : "fa fa-file text-inverse fa-lg"}
						},
						checkbox : {
							"three_state" : false,
							"tie_selection" : true
						},
						plugins : [ "massload", "unique" ]
					})
					.on("select_node.jstree", function(event, data) { // 노드가 선택된 뒤 처리할 이벤트   	 
						var id = data.node.id;
						var type = data.node.a_attr.type;
						if (type.startsWith("N")) {
							targetClsid = id;
							tree.selectedId = id;
							//if (!data.node.children.length){
							tree.getNodeList(data.node.id, data.node.a_attr.type);
							//}
						} else {
							targetClsid = "";
							fn_Fld_Pop_Tree.selectedId = "";
						}
						tree.openNode(id);
					})
					.on("loaded.jstree", function() {
						$.each(tree.initData(), function(index, item) {
							if (item.a_attr.type == CLSTYPES["DEPT"] || item.a_attr.type == CLSTYPES["COLLABO"]) {
								tree.getRootNodeList(item.a_attr.type);
							}
						});
						//root node 로드된후 처음 한번 이벤트
						fn_Fld_Pop_Tree.getNodeList(deptid, selectedClsType);//문서함 유형은 바꿔야 함. (N1:전사문서함, N2:부서문서함, N3:개인문서함, N4:협업문서함)
						$('#fldPopTree').jstree("open_node", selectedClsType);
						$('#fldPopTree').jstree("open_node", fn_Fld_Pop_Tree.rootId);
					})
					.on("after_open.jstree", function (e, data) {
						console.log("=== after_open.jstree data", data);
					});
		},
		openAll : function(){
			//$('#fldPopTree').jstree("open_all");
		},
		getCheckNodes : function() {
			var result = $('#fldPopTree').jstree('get_selected');
			for (var i = 0; i < result.length; i++) {
				var id = result[i];
				var node = $('#fldPopTree').jstree(true).get_node(id);
				console.log("check node : " + JSON.stringify(node));
				var attr = node.a_attr;
				var acl = {};
				acl.aclobjid = node.id;
				acl.name = node.text;
				acl.aclobjtype = attr.type;
				acl.acls = "2";
			}
		},
		unCheckNode : function(id) {
			$("#fldPopTree").jstree("uncheck_node", id);
		},
		getRootNodeList : function(type) {
			var sendData = {
				objIsTest : "N",
				companyid : companyid,
				types : type,
				isactive : "Y"
			}
			var gUpid, childid;
			console.log("==== getRootNodeList sendData: ", sendData);
			$.ajax({
				url : "${ctxRoot}/api/classification/list/down_1st",
				type : "POST",
				dataType : 'json',
				contentType : 'application/json',
				async : false,
				data : JSON.stringify(sendData),
				success : function(data) {
					console.log("=== getRootNodeList data : ", data);
					$.each(data.result, function(i, result) {
						var attr = {}
						var obj;
						obj = (result.zappClassification) ? result.zappClassification : result;
						attr.type = obj.types;
						var child = {};
						child.id = obj.classid;
						child.text = obj.name;
						attr.nsearch = true;
						child.a_attr = attr;
						var upid = obj.upid;
						if (type == CLSTYPES["DEPT"]) {
							child.icon = POPTREE["DEPT"];
							if (upid == companyid) {
								upid = CLSTYPES["DEPT"];
							}
						} else if (type == CLSTYPES["COLLABO"]) {
							child.icon = POPTREE["COLLABO"];
							if (upid == companyid) {
								upid = CLSTYPES["COLLABO"];
							}
						}
						if (obj.isactive == 'Y') {
							$('#fldPopTree').jstree(
									'create_node', upid, child, "last", false, true);
							
							gUpid = upid;
						}
					});
				},
				error : function(request, status, error) {
					alertNoty(request, status, error);
				},
				beforeSend : function() {
				},
				complete : function() {
					console.log("=== gUpid:" + gUpid);
					$('#fldPopTree').jstree("open_node", gUpid);
				}
			});
		},
		getNodeList : function(selectedNode, type) {
			console.log("===getNodeList : " + selectedNode);
			$.ajax({
				url : "${ctxRoot}/api/classification/list/down",
				type : "POST",
				dataType : 'json',
				contentType : 'application/json',
				async : false,
				data : JSON.stringify({
					objIsTest : "N",
					companyid : "${Authentication.objCompanyid}",
					upid : selectedNode,
					types : type,
					isactive : "Y"
				}),
				success : function(data) {
					console.log("data : ", data);
					$.each(data.result, function(i, result) {
						var attr = {}
						var obj;
						var acl = result.zappClassAcl;
						obj = (result.zappClassification) ? result.zappClassification : result;
						attr.type = obj.types;
						var child = {};
						child.id = obj.classid;
						child.text = obj.name;
						attr.acls = acl.acls;
						child.a_attr = attr;
						if(obj.isactive == 'Y'){
							child.icon = POPTREE[acl.acls];
						}else{
							child.icon = POPTREE[obj.isactive];
						}
						
						$('#fldPopTree').jstree('create_node',
								obj.upid, child, "last", false, false);
					});
				},
				error : function(request, status, error) {
					alertNoty(request, status, error);
				},
				beforeSend : function() {
				},
				complete : function() {
				}
			});
		},openNode : function(nodeId){
			$("#fldPopTree").jstree("open_node", nodeId);		
		}
	}
	//Tree End

	$(document).ready(function() {
		console.log("====selectedClsType : " + selectedClsType);
		fn_Fld_Pop_Tree.jstree();
		// 팝업 닫기
		$('#closeBtn').unbind("click").bind("click", function(){
			$('.bg').fadeOut();
	        $('.popup').fadeOut();
	    });
	});
	
	//메인페이지에서 넘긴 콜백 함수 호출
	var callback = function() {
		var selectCls = {};
		selectCls.classid = fn_Fld_Pop_Tree.selectedId;
		var obj = $("#fldPopTree").jstree(true).get_node(fn_Fld_Pop_Tree.selectedId);
		var isRoot = obj.a_attr.nsearch;
		if (isRoot) {
			alert("<spring:eval expression="@${msgLang}['CAN_NOT_SELECT_TOP-FOLDER']" />");
			return;
		}
		
		if(obj.a_attr.acls != 2){
			alert("해당 폴더에는 문서를 복사 할 수 없습니다.");
			return;
		}
		
		
		if (lsCallBack.data.zappClassObjects[0].classid == selectCls.classid) {
			var message = (lsCallBack.gubun == "COPY") ? "<spring:eval expression="@${msgLang}['CAN_NOT_COPY_SAME_DOC']" />"
					: "<spring:eval expression="@${msgLang}['CAN_NOT_MOVE_SAME_DOC']" />";
			alert(message);
			return;
		}
		lsCallBack.data.zappClassObjects.push(selectCls);
		lsCallBack.func(lsCallBack.data, lsCallBack.param.list);

	}
</script>
</head>
<body>
<div class="popup" style="display: block;">
	<h3 class="pageTit">문서</h3>
	<button type="button" id="closeBtn">
		<img src="${image}/icon/x.png">
	</button>
	<div class="tabCont" style="width: 320px;">
		<!--cont01-->
		<div id="cont03" class="contdiv" style="display:block;">
			<h3 class="innerTit">부서 문서함정보</h3>
			<div class="flex-content">
				<div class="cont_list">
					<div id="fldPopTree" class="sub"></div>
				</div>
			</div>
		</div>
	</div>
	<div style="text-align: center;">
		<button class="btbase" onclick="javascript:callback();">저장</button>
	</div>
</div>
</body>
</html>