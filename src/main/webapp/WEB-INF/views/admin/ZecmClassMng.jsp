<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*"%>
<%@include file="../common/CommonInclude.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />    
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, height=device-height, initial-scale=1.0, minimum-scale=1.0, maximum-scale=2.0, user-scalable=no">
<title>NADi4.0 :: <spring:eval expression="@${lang}['CLASSIFICATION_MANAGEMENT']" /></title>
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
var sClassId = "";		//리스트 선택 문서분류아이디
var sClassCode = "";	//리스트 선택 문서분류코드
var sClassName = "";	//리스트 선택 문서분류명
var sClassDescpt = "";	//리스트 선택 문서분류 설명
var ButtonMode = "add";	//등록 수정 여부
												
$(document).ready(function() {
	//입력값 초기화
	resetInput();
	
	// 입력 초기화
	$("#btnInit").click(function() {
		resetInput();
	});

	// 분류정보  등록, 수정
	$("#btnNew").click(function() {
		fn_Common.save(ButtonMode);
	});

	// 분류정보 삭제
	$("#btnDel").click(function() {
		fn_Common.disable();
	});
	
	//분류정보 폐기
	$("#btnDis").click(function() {
		fn_Common.discard();
	});

	//분류정보 복원
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
	save : function(type) {	//문서분류 정보 저장
		sClassId = fn_InitTree.select.id;
		sClassCode = $.trim($("#ClassCode").val());
		sClassName = $.trim($("#ClassName").val());
		sClassDescpt = $.trim($("#ClassDescpt").val());
		if (!sClassCode) {
			alert("<spring:eval expression="@${msgLang}['ENTER_CLASSIFICATION_CODE']"/>");
			$("#ClassCode").focus();
			return;
		}
		if (!sClassName) {
			alert("<spring:eval expression="@${msgLang}['ENTER_CLASSIFICATION_NAME']"/>");
			$("#ClassName").focus();
			return;
		}			

		if (!sClassId) {
			alert("<spring:eval expression="@${msgLang}['SELECTED_CLASSIFICATION_INFO_NOT_EXIST']"/>");
			return;
		}			
			
		if (type == "change") {
			if (sClassName == fn_InitTree.select.name
					&& sClassDescpt == fn_InitTree.select.descpt) {
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
					companyid : companyid,
					holderid : deptuserid,
					types : CLSTYPES["CLASSIFICATION"]
				}
			sendData.data.code = sClassCode;
			sendData.data.upid = sClassId;
		}else{
			sendData.data = {
					objIsTest : "N",
					objDebugged : false
				}
		}
		sendData.data.classid = sClassId;
		
		sendData.data.name = sClassName;
		sendData.data.descpt = sClassDescpt;
		
		fn_Common.publicCommon(sendData);
	},
	disable : function() {	//문서분류 정보 삭제 플레그 처리
		sClassId = fn_InitTree.select.id;
		if (!sClassId) {
			alert("<spring:eval expression="@${msgLang}['SELECTED_CLASSIFICATION_INFO_NOT_EXIST']"/>");
			return;
		}
		
		if(confirm("<spring:eval expression="@${msgLang}['DO_YOU_DELETE_CLASSIFICATION_INFO']"/>") == false){
			return;
		}
		
		var sendData = {};
		sendData.type = "disable";
		sendData.url = "${ctxRoot}/api/classification/disable";
		sendData.data = {
			objIsTest : "N",	//고정 값 사용
			classid : sClassId
		};
		fn_InitTree.select = {};
		fn_Common.publicCommon(sendData);
	},
	restore : function() {	//문서분류 정보 복원
		sClassId = fn_InitTree.select.id;
		if (!sClassId) {
			alert("<spring:eval expression="@${msgLang}['SELECTED_CLASSIFICATION_INFO_NOT_EXIST']"/>");
			return;
		}

		if(confirm("<spring:eval expression="@${msgLang}['DO_YOU_RESTORE_CLASSIFICATION_INFO']"/>") == false){
			return;
		}
		
		var sendData = {};
		sendData.type = "restore";
		sendData.url = "${ctxRoot}/api/classification/enable";
		sendData.data = {
			objIsTest : "N",	//고정 값 사용
			classid : sClassId
		};
		fn_InitTree.select = {};
		fn_Common.publicCommon(sendData);
	},
	discard : function() {	//문서분류 정보 폐기
		sClassId = fn_InitTree.select.id;
		if (!sClassId) {
			alert("<spring:eval expression="@${msgLang}['SELECTED_CLASSIFICATION_INFO_NOT_EXIST']"/>");
			return;
		}

		if(confirm("<spring:eval expression="@${msgLang}['DO_WO_DISCARD_CLASSIFICATION_INFO']"/>") == false){
			return;
		}

		var sendData = {};
		sendData.type = "discard";
		sendData.url = "${ctxRoot}/api/classification/discard";
		sendData.data = {
			objIsTest : "N",	//고정 값 사용
			classid : sClassId
		};
		fn_InitTree.select = {};
		fn_Common.publicCommon(sendData);
	},
	relocate : function(priority) {		//문서분류 순서변경
		sClassId = fn_InitTree.select.id;
		if(confirm("<spring:eval expression="@${msgLang}['DO_YOU_MOVE_CLASSIFICATION_INFO']"/>") == false){
			return;
		}

		var sendData = {};
		sendData.type = "reorder";
		sendData.url = "${ctxRoot}/api/classification/reorder";
		sendData.data = {
			objIsTest : "N",	//고정 값 사용
			classid : sClassId
		};

		sendData.data.priority = priority;

		fn_InitTree.select = {};
		fn_Common.publicCommon(sendData);
	},
	publicCommon : function(sendData) {	//공통 처리 로직 호출
		//console.log("publicCommon sendData "+sendData.type+":", JSON.stringify(sendData));
		$.ajax({
			type : 'POST',
			url : sendData.url,
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData.data),
			success : function(data) {
				//console.log("publicCommon data "+sendData.type+":", JSON.stringify(data));
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
	$("#ClassState").prop("disabled", true);
	$("#ClassCode").attr("disabled", false);
	$("input[id^='Class']").val('');
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
	id : companyid,
	name : "<spring:eval expression="@${lang}['CLASSIFICATION']"/>",
	select : {},	//트리 선택 값 전역 변수
	jstree : function() {
		$("img[id=reorder]").hide();
		$('#znTree').remove();
		var treeHtml = "<ul id='znTree'></ul>";
		$('.contNav').append(treeHtml);

		$('#znTree').jstree({
			'plugins' : [ "state", "html_data" ],
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
						"itemcode" : fn_InitTree.id,
						"itemname" : "",
						"isactive" : "",
						"priority" : 0,
						"descpt" : ""
					}
				} ],
				'check_callback' : true
			}
		})
		.on('select_node.jstree', function(event, data) {
			var obj = data.instance.get_node(data.selected);
			fn_InitTree.select.parentid = obj.parent;
			fn_InitTree.select.id = obj.id;
			fn_InitTree.select.name = obj.text;
			fn_InitTree.select.code = obj.a_attr.itemcode;
			fn_InitTree.select.isactive = obj.a_attr.isactive;
			fn_InitTree.select.priority = obj.a_attr.priority;
			fn_InitTree.select.descpt = obj.a_attr.descpt;
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
			$("#ClassCode").attr("disabled", true);
			$("#ClassCode").val(fn_InitTree.select.code);
			$("#ClassName").val(fn_InitTree.select.name);
			$("#ClassDescpt").val(fn_InitTree.select.descpt);
			
			if (obj.parent == "#") {	//최상위 선택시
				resetInput();
			} else {
				if (fn_InitTree.select.isactive == "Y") {
					$("#ClassState").val("<spring:eval expression="@${lang}['USE']"/>");
				} else {
					$("#ClassState").val("<spring:eval expression="@${lang}['NOT_USE']"/>");
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
		sendData.types = CLSTYPES["CLASSIFICATION"];
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
		message = "<spring:eval expression="@${msgLang}['REGISTERED_CLASSIFICATION_INFO']"/>";
	} else if (type == 'change') {
		message = "<spring:eval expression="@${msgLang}['CORRECTED_CLASSIFICATION_INFO']"/>";
	} else if (type == 'disable') {
		message = "<spring:eval expression="@${msgLang}['REMOVED_CLASSIFICATION_INFO']"/>";
	} else if (type == 'restore') {
		message = "<spring:eval expression="@${msgLang}['RESTORED_CLASSIFICATION_INFO']"/>";
	} else if (type == 'discard') {
		message = "<spring:eval expression="@${msgLang}['DISCARDED_CLASSIFICATION_INFO']"/>";
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
						<img src="${image}/icon/icon_b03.png" alt=""><spring:eval expression="@${lang}['CLASSIFICATION_MANAGEMENT']" />
						<img src="${image}/icon/arrow_up.png" title="<spring:eval expression="@${lang}['MOVE_CLASSIFICATION_ORDER']"/>"
							style="display: none; cursor: pointer; margin-right: 15px; line-height: 26px; height: 15px; padding: 5px; float: right;"
							id="reorder" name="up" />
						<img src="${image}/icon/arrow_down.png" title="<spring:eval expression="@${lang}['MOVE_CLASSIFICATION_ORDER']"/>"
							style="display: none; cursor: pointer; margin-right: 15px; line-height: 26px; height: 15px; padding: 5px; float: right;"
							id="reorder" name="down" />
					</h1>
					<div class="flex-content">
						<div class="contNav">
							<ul id="znTree"></ul>
						</div>
						<div class="rgt_area">
							<div class="wdt100">
								<h3 class="innerTit"><spring:eval expression="@${lang}['CLASSIFICATION_INFO']" /></h3>
								<div class="btn_wrap">
									<button type="button" class="btbase" id="btnInit" name="new"><spring:eval expression="@${lang}['INITIALIZATION']" /></button>
									<button type="button" class="btbase" id="btnNew" name="add"><spring:eval expression="@${lang}['SAVE']" /></button>
									<button type="button" class="btbase" id="btnRes" name="res"><spring:eval expression="@${lang}['RESTORE']" /></button>
									<button type="button" class="btbase" id="btnDel" name="del"><spring:eval expression="@${lang}['DELETE']" /></button>
									<button type="button" class="btbase" id="btnDis" name="dis"><spring:eval expression="@${lang}['DISCARD']" /></button>
								</div>
								<div class="inner_uiGroup">
									<p><spring:eval expression="@${lang}['CLASSIFICATION_CODE']" /></p>
									<input type="text" id="ClassCode" title="<spring:eval expression="@${lang}['CLASSIFICATION_CODE']"/>" onkeyup='pubByteCheckTextarea(event,30)' />
									<p><spring:eval expression="@${lang}['CLASSIFICATION_NAME']" /></p>
									<input type="text" id="ClassName" title="<spring:eval expression="@${lang}['CLASSIFICATION_NAME']"/>" onkeyup='pubByteCheckTextarea(event,300)' />
									<p><spring:eval expression="@${lang}['CLASSIFICATION_SYSTEM_DESC']" /></p>
									<input type="text" id="ClassDescpt" title="<spring:eval expression="@${lang}['CLASSIFICATION_SYSTEM_DESC']"/>" onkeyup='pubByteCheckTextarea(event,50)' />
									<p><spring:eval expression="@${lang}['USE_OR_NOT']" /></p>
									<input type="text" id="ClassState" title="<spring:eval expression="@${lang}['USE_OR_NOT']"/>" />
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