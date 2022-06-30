<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*"%>
<%@include file="../common/CommonInclude.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />    
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0, minimum-scale=1.0, maximum-scale=2.0, user-scalable=no">
<title>NADi4.0 :: <spring:eval expression="@${lang}['COMPANY_FOLDER_BOX']" /></title>
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
var sNodeId = "";		//리스트 선택 전사폴더아이디
var sNodeCode = "";		//리스트 선택 전사폴더코드
var sNodeName = "";		//리스트 선택 전사폴더명
var sNodeDescpt = "";	//리스트 선택 전사폴더 설명
var ButtonMode = "add";	//등록 수정 여부
												
$(document).ready(function() {
	//입력값 초기화
	resetInput();
	
	// 입력 초기화
	$("#btnInit").click(function() {
		resetInput();
	});

	// 전사폴더  등록, 수정
	$("#btnNew").click(function() {
		fn_Common.save(ButtonMode);
	});

	// 전사폴더 삭제
	$("#btnDel").click(function() {
		fn_Common.disable();
	});
	
	// 전사폴더 폐기
	$("#btnDis").click(function() {
		fn_Common.discard();
	});

	// 전사폴더 복원
	$("#btnRes").click(function() {
		fn_Common.restore();
	});

	$("img[id=reorder]").click(function() {
		var action = $(this).attr("name");
		var priority = $("#znTree").jstree("get_node", fn_InitTree.select.id).a_attr.priority;
		if (action == 'up') {
			priority = priority - 1;
		} else if (action == 'down') {
			priority = priority + 1;
		}
		fn_Common.relocate(priority);
	});

	fn_InitTree.jstree();
});

/*********************************
Name   : fn_Common
Desc   : 공통 처리 함수
Param  : 없음
**********************************/
var fn_Common = {
	save : function(type) {	//전사폴더 정보 저장
		sNodeId = fn_InitTree.select.id;
		sNodeCode = $.trim($("#NodeCode").val());
		sNodeName = $.trim($("#NodeName").val());
		sNodeDescpt = $.trim($("#NodeDescpt").val());
		if (!sNodeCode) {
			alert("<spring:eval expression="@${msgLang}['FOLDER_CODE']"/>");
			$("#NodeCode").focus();
			return;
		}
		if (!sNodeName) {
			alert("<spring:eval expression="@${msgLang}['FOLDER_NAME']"/>");
			$("#NodeName").focus();
			return;
		}			

		if (!sNodeId) {
			alert("<spring:eval expression="@${msgLang}['SELECTED_FOLDER_NOT_EXIST']"/>");
			return;
		}			
			
		if (type == "change") {
			if (sNodeName == fn_InitTree.select.name
					&& sNodeDescpt == fn_InitTree.select.descpt) {
				alert("<spring:eval expression="@${msgLang}['NO_ALERTED_CONTENT']"/>");
				return;
			}
		}
		
		var sendData = {};
		sendData.type = type;	//처리방식 (add:등록, change:수정)
		sendData.url = "${ctxRoot}/api/classification/" + type;
		if(type == "add"){
			sendData.data = {
					objIsTest : "N",
					objDebugged : false,
					types : CLSTYPES["COMPANY"],
			        zappClassAcls : [ {} ],
					zappContentAcls : [ {} ]
				}
			sendData.data.code = sNodeCode;
		}else{
			sendData.data = {
					objIsTest : "N",
					objDebugged : false
				}
			sendData.data.classid = sNodeId;
		}
		sendData.data.upid = sNodeId;		
		sendData.data.name = sNodeName;
		sendData.data.descpt = sNodeDescpt;
		sendData.data.companyid = companyid;
		
		fn_Common.publicCommon(sendData);
	},
	disable : function() {	//전사폴더 삭제 플레그 처리
		sNodeId = fn_InitTree.select.id;
		if (!sNodeId) {
			alert("<spring:eval expression="@${msgLang}['SELECTED_FOLDER_NOT_EXIST']"/>");
			return;
		}
		
		if(confirm("<spring:eval expression="@${msgLang}['ARE_YOU_DELETE_FOLDER_INFO']"/>") == false){
			return;
		}
		
		var sendData = {};
		sendData.type = "disable";
		sendData.url = "${ctxRoot}/api/classification/disable";
		sendData.data = {
			objIsTest : "N",	//고정 값 사용
			objDebugged : false,
			objIncLower : "Y",
			classid : sNodeId
		};
		fn_InitTree.select = {};
		fn_Common.publicCommon(sendData);
	},
	restore : function() {	//전사폴더 복원
		sNodeId = fn_InitTree.select.id;
		if (!sNodeId) {
			alert("<spring:eval expression="@${msgLang}['SELECTED_FOLDER_NOT_EXIST']"/>");
			return;
		}

		if(confirm("<spring:eval expression="@${msgLang}['RECCOVER_FOLDER_INFO']"/>") == false){
			return;
		}
		
		var sendData = {};
		sendData.type = "restore";
		sendData.url = "${ctxRoot}/api/classification/enable";
		sendData.data = {
			objIsTest : "N",	//고정 값 사용
			objDebugged : false,
			objIncLower : "Y",
			classid : sNodeId
		};
		fn_InitTree.select = {};
		fn_Common.publicCommon(sendData);
	},
	discard : function() {	//전사폴더 정보 폐기
		sNodeId = fn_InitTree.select.id;
		if (!sNodeId) {
			alert("<spring:eval expression="@${msgLang}['SELECTED_FOLDER_NOT_EXIST']"/>");
			return;
		}

		if(confirm("<spring:eval expression="@${msgLang}['ARE_YOU_FOLDER_DISCARD']"/>") == false){
			return;
		}

		var sendData = {};
		sendData.type = "discard";
		sendData.url = "${ctxRoot}/api/classification/discard";
		sendData.data = {
			objIsTest : "N",	//고정 값 사용
			objDebugged : false,
			classid : sNodeId
		};
		fn_InitTree.select = {};
		fn_Common.publicCommon(sendData);
	},
	relocate : function(priority) {	//전사폴더 순서변경
		sNodeId = fn_InitTree.select.id;
		if(confirm("<spring:eval expression="@${msgLang}['DO_YOU_MOVE_FOLDER_INFO']"/>") == false){
			return;
		}

		var sendData = {};
		sendData.type = "reorder";
		sendData.url = "${ctxRoot}/api/classification/reorder";
		sendData.data = {
			objIsTest : "N",	//고정 값 사용
			objDebugged : false,
			classid : sNodeId
		};

		sendData.data.priority = priority;

		fn_InitTree.select = {};
		fn_Common.publicCommon(sendData);
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
				if (data.status == "0000") {
					messageNotice(sendData.type);
					fn_InitTree.jstree();
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
Name   : resetInput
Desc   : 초기화
Param  : 없음
**********************************/
var resetInput = function() {
	$("#NodeState").prop("disabled", true);
	$("#NodeCode").attr("disabled", false);
	$("input[id^='Node']").val('');
	$("#btnRes").hide();
	$("#btnDel").hide();
	$("#btnDis").hide();
	$("img[id=reorder]").hide();
	ButtonMode = "add";
}

/*********************************
Name   : fn_InitTree
Desc   : 트리 초기화
Param  : 
**********************************/
var fn_InitTree = {
	id : (userType == USERTYPES["COMPANY"]) ? companyid : deptid,
	name : companyName,
	select : {},	//트리 선택 값 전역 변수
	jstree : function() {
		console.log("USERTYPES_COMPANY:", USERTYPES["COMPANY"]);
		console.log("companyName:", companyName);

		$("img[id=reorder]").hide();
		$('#znTree').remove();
		var treeHtml = "<ul id='znTree'></ul>";
		$('.contNav').append(treeHtml);

		$('#znTree').jstree({
			'plugins' : [ "state" ],
			'core' : {
				'data' : [ {
					"id" : fn_InitTree.id,
					"text" : fn_InitTree.name,
					"icon" : ADMINTREEICONS["CLS_ROOT"],
					"state" : {
						"opened" : true,
						"disabled" : false
					},
					"li_attr" : {},
					"a_attr" : {
						"itemcode" : "",
						"itemname" : "",
						"itemdescpt" : "",
						"item_status" : "Y"
					}
				} ],
				'check_callback' : true
			}
		})
		.on('select_node.jstree', function(event, data) {
			var obj = data.instance.get_node(data.selected);
			fn_InitTree.select.parentid = obj.parent;
			fn_InitTree.select.id = obj.a_attr.id;
			fn_InitTree.select.name = obj.a_attr.itemname;
			fn_InitTree.select.code = obj.a_attr.itemcode;
			fn_InitTree.select.descpt = obj.a_attr.descpt;
			fn_InitTree.select.isactive = obj.a_attr.isactive;
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
			
			ButtonMode = "change";

			//PC 모드 트리 선택
			$("#NodeCode").attr("disabled", true);
			$("#NodeCode").val(fn_InitTree.select.code);
			$("#NodeName").val(fn_InitTree.select.name);
			$("#NodeDescpt").val(fn_InitTree.select.descpt);
			
			if (obj.parent == "#") {	//최상위 선택시
				resetInput();
			} else {
				if (fn_InitTree.select.isactive == "Y") {
					$("#NodeState").val("<spring:eval expression="@${lang}['USE']"/>");
				} else {
					$("#NodeState").val("<spring:eval expression="@${lang}['NOT_USE']"/>");
				}
				
			}

			//트리 이동 버튼 생성
			if (obj.parent != "#") {
				var parentNode = $("#znTree").jstree("get_node", obj.parent);
				var firstNode = parentNode.children[0];
				var lastNode = parentNode.children[parentNode.children.length - 1];
				$("img[id=reorder]").hide();
				if (fn_InitTree.select.id !== firstNode) {
					$("img[id=reorder][name=up]").show();
				}
				if (fn_InitTree.select.id !== lastNode) {
					$("img[id=reorder][name=down]").show();
				}
			}
		})
		.on('ready.jstree', function() {
			openLayer("<spring:eval expression="@${msgLang}['RETRIEVING_LIST_LIST']"/>");
			fn_InitTree.getClsList();
		});
	},
	getClsList : function() {
		var sendData = {};
		sendData.objIsTest = "N";
		sendData.upid = companyid;
		sendData.types = CLSTYPES["COMPANY"];
		sendData.objIsMngMode = true;
		//console.log("getClsList sendData:", JSON.stringify(sendData));
		// 조회
		$.ajax({
			url : "${ctxRoot}/api/classification/list/down",
			type : "POST",
			dataType : "json",
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
				//console.log("getClsList data:", JSON.stringify(data));
				if (data.status == "0000") {
					if (objectIsEmpty(data.result)) {
						return;
					}
					$.each(data.result, function(index, item) {
						var obj = item.zappClassification;
						fn_InitTree.createNode(obj);
					});
					fn_InitTree.clickNode(fn_InitTree.select.treeid);
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			},
			complete : function() {
				closeLayer();
			}
		});
	},
	createNode : function(item) {
		$('#znTree').jstree(
			'create_node',
			item.upid,
			{
				"text" : item.name,
				"id" : item.classid,
				"icon" : (item.isactive == "Y") ? ADMINTREEICONS["CLS_SUB"] : ADMINTREEICONS["DISABLE"],
				"state" : {
					"opened" : true
				},
				"a_attr" : {
					"itemcode" : item.code,
					"itemname" : item.name,
					"descpt" : item.descpt,
					"isactive" : item.isactive,
					"priority" : item.priority

				}
			}, "last", false, false);
	},
	clickNode : function(nodeId) {
		if (!objectIsEmpty(nodeId)) {
			$('#znTree').jstree("deselect_all");
			$('#znTree').jstree("select_node", nodeId);
			$('#znTree').jstree("toggle_node", nodeId);
			$('#znTree').jstree("open_node", nodeId);
		}
	}
};

/*********************************
Name   : messageNotice
Desc   : 등록 수정 삭제 폐기 시 해당 정보를 알럿창으로 보여줌
Param  : type (add change discard disable )
**********************************/
function messageNotice(type) {

	var message = '';
	if (type == 'add') {
		message = "<spring:eval expression="@${msgLang}['REGISTERED_FOLDER_INFO']"/>";
	} else if (type == 'change') {
		message = "<spring:eval expression="@${msgLang}['FOLDER_INFO_CHANGED']"/>";
	} else if (type == 'disable') {
		message = "<spring:eval expression="@${msgLang}['FOLDER_STATE_CHANGED']"/>";
	} else if (type == 'restore') {
		message = "<spring:eval expression="@${msgLang}['RECCOVER_FOLDER_INFO']"/>";
	} else if (type == 'discard') {
		message = "<spring:eval expression="@${msgLang}['FOLDER_INFO_DISCARDED']"/>";
	}
	if (message) {
		alert(message);
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
					<h1 class="pageTit" style="width:35%">
						<img src="${image}/icon/icon_b05.png" alt=""><spring:eval expression="@${lang}['COMPANY_FOLDER_BOX']" />
						<img src="${image}/icon/arrow_up.png" title="<spring:eval expression="@${lang}['MOVE_FOLDER_ORDER']"/>"
							style="display: none; cursor: pointer; margin-right: 15px; line-height: 26px; height: 15px; padding: 5px; float: right;"
							id="reorder" name="up" />
						<img src="${image}/icon/arrow_down.png" title="<spring:eval expression="@${lang}['MOVE_FOLDER_ORDER']"/>"
							style="display: none; cursor: pointer; margin-right: 15px; line-height: 26px; height: 15px; padding: 5px; float: right;"
							id="reorder" name="down" />
					</h1>
					<div class="flex-content">
						<div class="contNav">
							<ul id="znTree"></ul>
						</div>
						<div class="rgt_area">
							<div class="wdt100">
								<h3 class="innerTit"><spring:eval expression="@${lang}['COMPANY_FOLDER_BOX']" /></h3>
								<div class="btn_wrap">
									<button type="button" class="btbase" id="btnInit" name="new"><spring:eval expression="@${lang}['INITIALIZATION']" /></button>
									<button type="button" class="btbase" id="btnNew" name="add"><spring:eval expression="@${lang}['SAVE']" /></button>
									<button type="button" class="btbase" id="btnRes" name="res"><spring:eval expression="@${lang}['RESTORE']" /></button>
									<button type="button" class="btbase" id="btnDel" name="del"><spring:eval expression="@${lang}['DELETE']" /></button>
									<button type="button" class="btbase" id="btnDis" name="dis"><spring:eval expression="@${lang}['DISCARD']" /></button>
								</div>
								<div class="inner_uiGroup">
									<p><spring:eval expression="@${lang}['FOLDER_CODE']" /></p>
									<input type="text" id="NodeCode" title="<spring:eval expression="@${lang}['FOLDER_CODE']"/>" onkeyup='pubByteCheckTextarea(event,64)' />
									<p><spring:eval expression="@${lang}['FOLDER_NAME']" /></p>
									<input type="text" id="NodeName" title="<spring:eval expression="@${lang}['FOLDER_NAME']"/>" onkeyup='pubByteCheckTextarea(event,500)' />
									<p><spring:eval expression="@${lang}['FOLDER_DESCRIPTION']" /></p>
									<input type="text" id="NodeDescpt" title="<spring:eval expression="@${lang}['FOLDER_DESCRIPTION']"/>" onkeyup='pubByteCheckTextarea(event,500)' />
									<p><spring:eval expression="@${lang}['USE_OR_NOT']" /></p>
									<input type="text" id="NodeState" title="<spring:eval expression="@${lang}['USE_OR_NOT']"/>" />
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