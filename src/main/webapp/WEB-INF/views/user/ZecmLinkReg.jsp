<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${js}/common.js"></script>
<title>ECM4.0 :: 가상문서등록</title>
<script type="text/javascript">
	var fileData = {};
	var addFileNO = 0;
	var fileList = [];
	var dontDouble = false;
	var sendFileInfo = {};

	var DocTypeList;
	
	var lsCallBack;//레이어 팝업 호출시 데이터 할당

	var fileHandle = function(files) {
		console.log("=== fileHandle files:", files);
		
		for (var i = 0; i < files.length; i++) {
			if (i == 0) {
				if ($("#title").val() == "")
					$("#title").val(files[i].name);
			}
			
			if(files[i].type != ""){//폴더를 선택한 경우 제외하기 위해 추가함
				fileData[addFileNO] = files[i];
				addFileNO++;
			}
		}
		var inHtml = "";
		var fileCnt = "";
		var sizeStr = "";
		$("#fileInfo").empty();
		inHtml += "<table style='table-layout:fixed'>";
		for ( var key in fileData) {
			var fileKey = key;
			var fileObj = fileData[key];
			var mfileid = fileObj.mfileid;
			var ctype = fileObj.ctype;
			
			var size = fileObj.size;
			
			if(fileObj.type != ""){//폴더를 선택한 경우 제외하기 위해 추가함
				var sizeKB = size / 1024;
				if (parseInt(sizeKB) > 1024) {
					var sizeMB = sizeKB / 1024;
					sizeStr = sizeMB.toFixed(2) + " MB";
				} else {
					sizeStr = sizeKB.toFixed(2) + " KB";
				}
	
				inHtml += "<tr style='height:30px;'><td style='width:30px;'><input type='checkbox' name='chkAddFile' id='chkAddFile" + fileKey + "' value='" + mfileid + "'><label for='chkAddFile" + fileKey + "'></label></td>";
				if (ctype == "01") { //Bundle
					inHtml += "<td class='tdStyle' align='left' title='" + fileObj.name + "'>" + fileObj.name + "</td>";
					inHtml += "<td style='width:70px;padding-left:5px;text-align:center;'>번들</td>";
				} else { //File
					inHtml += "<td class='tdStyle' align='left' title='" + fileObj.name + "'>" + fileObj.name + "</td>";
					inHtml += "<td style='width:70px;padding-left:5px;text-align:center;'>" + sizeStr + "</td>";
				}
				
				inHtml += "</tr>";
				fileCnt++;
			}
			//js_addImageView(fileKey);
		}
		inHtml += "</table>";
		$("#fileInfo").html(inHtml);
	}

	var dupChk = function(addFileName) {
		for ( var key in fileData) {
			var fileKey = key;
			var fileObj = fileData[key];
			var fileName = fileObj.name;
			if (addFileName == fileName)
				return true;
		}
		return false;
	}

	//버튼 메뉴 파일 삭제
	var js_addFileDel = function() {
		$("input[name='chkAddFile']:checked").each(function() {
			var fileKey = $(this).val();
			delete fileData[fileKey];
			$(this).parent().parent('tr').remove();
		});
	}

	if (!String.prototype.startsWith) {
		String.prototype.startsWith = function(search, pos) {
			return this.substr(!pos || pos < 0 ? 0 : +pos, search.length) === search;
		};
	}

	var icon = "${ctxRoot}/resources/images/jstree/tree_user_icon15.png";
	var userIcon = "${ctxRoot}/resources/images/jstree/tree_user_icon03.png";

	//OgranTree Start
	var companyData = [];
	companyData.push({
		id : companyid,
		parent : "#",
		icon : icon,
		text : companyName,
		a_attr : {type : "N1"}
	});//companyid

	var fn_DocReg_Tree = {
		consoleLog : function(data) {
			$.each(data, function(i, v) {
			});
		},
		tree : {},
		jstree : function() {
			this.tree = $('#organTree');
			$('#organTree').jstree({
				core : {
					check_callback : true,
					data : companyData
				/* 최초에 보여지 최상위 Root Tree */
				},
				types : {
					"default" : {"icon" : "glyphicon glyphicon-flash"},
					file : {icon : "fa fa-file text-inverse fa-lg"}
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
				if (type === "02")
					fn_DocReg_Tree.getUserList(data.node.id);
			}).on("loaded.jstree", function() {
				//root node 로드된후 처음 한번 이벤트
				fn_DocReg_Tree.getDeptList();
			});
		},
		getCheckNodes : function() {
			//부서 체크 정보
			$.each($("#organTree").jstree("get_checked", true), function() {
				var id = this.id;
				var node = $('#organTree').jstree(true).get_node(id);
				var attr = node.a_attr;
				var acl = {};
				acl.aclobjid = node.id;
				acl.name = node.text;
				acl.aclobjtype = attr.type;
				acl.acls = "2";
				addAclNew(acl);
			});
			//사용자 체크 정보
			$("#organTreeUserList").find("input[name='chkUser']:checked").each( function() {
				var $li = $(this).parent().parent();
				var node = $li.data("meta");
				var attr = node.a_attr;
				var acl = {};
				acl.aclobjid = node.id;
				acl.name = node.text;
				acl.aclobjtype = attr.type;
				acl.acls = "2";
				addAclNew(acl);
			});
		},
		unCheckNode : function(id) {
			$("#organTree").jstree("uncheck_node", id);
		},
		getDeptList : function() {
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
						child.icon = icon;
						child.a_attr = attr;
						$('#organTree').jstree('create_node', obj.upid, child,
								"last", false, false);
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
					$("#organTreeUserList").empty();
					$.each(data.result, function(i, obj) {
						var attr = {}
						attr.type = "01";
						var child = {};
						child.id = obj.deptuserid;
						child.text = obj.zappUser.name;
						child.icon = userIcon;
						child.a_attr = attr;
						addUserList(child);
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
		}
	}
	//Tree End

	var companyGroup = {
		id : "01",
		parent : "#",
		icon : icon,
		text : "<spring:eval expression="@${lang}['COMPANY_GROUP']"/>",
		a_attr : {
			type : "01",
			root : true
		}
	};
	var deptGroup = {
		id : "02",
		parent : "#",
		icon : icon,
		text : "<spring:eval expression="@${lang}['DEPARTMENT_GROUP']"/>",
		a_attr : {
			type : "02",
			root : true
		}
	};
	var userGroup = {
		id : "03",
		parent : "#",
		icon : icon,
		text : "<spring:eval expression="@${lang}['PERSONAL_GROUP']"/>",
		a_attr : {
			type : "03",
			root : true
		}
	};
	var collaborGroup = {
		id : "04",
		parent : "#",
		icon : icon,
		text : "<spring:eval expression="@${lang}['COLLABORATIVE_GROUP']"/>",
		a_attr : {
			type : "04",
			root : true
		}
	};
	var groupRootNode = [ companyGroup, deptGroup, userGroup, collaborGroup ];
	var fn_DocRegGroup_Tree = {
		consoleLog : function(data) {
			$.each(data, function(i, v) {
			});
		},
		jstree : function() {
			$('#groupTree').jstree({
				core : {
					check_callback : true,
					data : groupRootNode
				/* 최초에 보여지 최상위 Root Tree */
				},
				types : {
					"default" : { "icon" : "glyphicon glyphicon-flash"},
					file : {icon : "fa fa-file text-inverse fa-lg"}
				},
				checkbox : {"three_state" : false},
				plugins : [ "checkbox", "massload", "unique" ]
			}).on("select_node.jstree", function(event, data) { // 노드가 선택된 뒤 처리할 이벤트   	 
				var id = data.node.id;
				var type = data.node.a_attr.type;
				if (type == "01" || type == "02" || type == "03" || type == "04") { //전사, 부서, 개인, 협업 노드인경우에만 리스트를 받아옴
					fn_DocRegGroup_Tree.getGroupList(data.node);
				}
			}).on("loaded.jstree", function() {
				//root node 로드된후 처음 한번 이벤트
				//슈퍼권한, 전체사용자 그룹을 추가하기 위해서 한번 호출해준다.
				fn_DocRegGroup_Tree.getOtherGroupList("98"); // 전체사용자
				fn_DocRegGroup_Tree.getOtherGroupList("99"); //슈퍼권한
			});
		},
		getCheckNodes : function() {
			var result = $('#groupTree').jstree('get_selected');
			for (var i = 0; i < result.length; i++) {
				var id = result[i];
				var node = $('#groupTree').jstree(true).get_node(id);
				var attr = node.a_attr;
				if (!attr.root) {
					var acl = {};
					acl.aclobjid = node.id;
					acl.name = node.text;
					acl.aclobjtype = "03"; //그룹 유형
					acl.acls = "2";
					addAclNew(acl);
				} else {
					fn_DocRegGroup_Tree.unCheckNode(node.id);
				}
			}
		},
		unCheckNode : function(id) {
			$("#groupTree").jstree("uncheck_node", id);
		},
		getGroupList : function(node) {
			var upid = companyid; //node.id;
			if (node.a_attr.type == "03")
				upid = deptuserid;
			var zappGroup = {};
			zappGroup.upid = upid;
			zappGroup.types = node.a_attr.type;
			zappGroup.isactive = "Y";
			var data = {};
			data.objIsTest = "N";
			data.zappGroup = zappGroup;
			console.log("grouplist param : " + JSON.stringify(data));
			console.log("deptid : " + deptid);
			console.log("deptuserid : " + deptuserid);
			$.ajax({
				url : "${ctxRoot}/api/organ/group/list",
				type : "POST",
				dataType : 'json',
				contentType : 'application/json',
				async : false,
				data : JSON.stringify(data),
				success : function(data) {
					console.log("grouplist result : " + JSON.stringify(data));
					$.each(data.result, function(i, obj) {
						var attr = {}
						attr.type = obj.types;
						var child = {};
						child.id = obj.groupid;
						child.text = obj.name;
						child.icon = icon;
						child.a_attr = attr;
						//	if(node.a_attr.type == "04"){ 
						if (obj.upid == companyid || node.a_attr.type == "03") {
							$('#groupTree').jstree('create_node', obj.types, child, "last", false, false);
						} else {
							$('#groupTree').jstree('create_node', obj.upid, child, "last", false, false);
						}
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
		},
		getOtherGroupList : function(type) {
			var zappGroup = {};
			zappGroup.upid = companyid;
			zappGroup.types = type
			zappGroup.isactive = "Y";
			var data = {};
			data.objIsTest = "N";
			data.zappGroup = zappGroup;
			console.log("getSuperGroupList param : " + JSON.stringify(data));

			$.ajax({
				url : "${ctxRoot}/api/organ/group/list",
				type : "POST",
				dataType : 'json',
				contentType : 'application/json',
				async : false,
				data : JSON.stringify(data),
				success : function(data) {
					console.log("grouplist result : " + JSON.stringify(data));
					$.each(data.result, function(i, obj) {
						var attr = {}
						attr.type = type;
						var child = {};
						child.id = obj.groupid;
						child.text = obj.name;
						child.icon = icon;
						child.a_attr = attr;
						var rootId = (obj.upid == companyid) ? "#" : obj.upid;
						$('#groupTree').jstree('create_node', rootId, child, "last", false, false);
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
		}
	}
	//GroupTree End

	//ClsTree Start
	var clsRoot = {
		id : companyid,
		parent : "#",
		icon : POPTREE["CLS_ROOT"],
		text : "<spring:eval expression="@${lang}['CLASSIFICATION']"/>",
		a_attr : {
			type : "02",
			class : "no_checkbox"
		}
	};

	var clsRootNode = [ clsRoot ];
	var fn_DocRegCls_Tree = {
		consoleLog : function(data) {
			$.each(data, function(i, v) {
			});
		},
		jstree : function() {
			$('#clsTree').jstree({
				core : {
					check_callback : true,
					data : clsRootNode
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
					"three_state" : false
				},
				plugins : [ "checkbox", "massload", "unique" ]
			}).on("select_node.jstree", function(event, data) { // 노드가 선택된 뒤 처리할 이벤트   	 
				var id = data.node.id;
				var type = data.node.a_attr.type;
				fn_DocRegCls_Tree.getClsList(data.node.id);
			}).on("loaded.jstree", function() {
				//root node 로드된후 처음 한번 이벤트
				var id = clsRoot.id;
				fn_DocRegCls_Tree.getClsList(id);
				$('#clsTree').jstree("open_node", id);
			});
		},
		getCheckNodes : function() {
			var result = $('#clsTree').jstree('get_selected');
			for (var i = 0; i < result.length; i++) {
				var id = result[i];
				if (id == companyid) { //root id인 경우에는 추가 안함
					continue;
				}
				var node = $('#clsTree').jstree(true).get_node(id);
				var attr = node.a_attr;
				var cls = {};
				cls.classid = node.id;
				cls.name = node.text;
				cls.classtype = attr.type;
				addCls(cls);
			}
		},
		unCheckNode : function(id) {
			$("#clsTree").jstree("uncheck_node", id);
		},
		getClsList : function(upid) {
			$.ajax({
				url : "${ctxRoot}/api/classification/list/down",
				type : "POST",
				dataType : 'json',
				contentType : 'application/json',
				async : false,
				data : JSON.stringify({
					objIsTest : "N",
					upid : upid,
					types : "02",
					isactive : "Y"
				}),
				success : function(data) {
					$.each(data.result, function(i, result) {
						var attr = {}
						var obj;
						obj = (result.zappClassification) ? result.zappClassification : result;
						attr.type = "02";
						var child = {};
						child.id = obj.classid;
						child.text = obj.name;
						child.icon = POPTREE["CLS_SUB"];
						child.a_attr = attr;
						$('#clsTree').jstree('create_node', obj.upid, child, "last", false, false);
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
		}
	}
	//GroupTree End
	//ClsTree End

	//권한 추가
	var addUserList = function(user) {
		//설정되지 않은 권한만 추가		

		var $tr = $('<tr></tr>');
		var inHtml = "";
		inHtml += "<td align='center'><input type='checkbox' name='chkUser' id='chkUser"+user.id+"' value='"+user.id+"'><label for='chkUser"+user.id+"'></label></td>";
		inHtml += "<td>" + user.text + "</td>";
		$tr.data('meta', user).append(inHtml);
		$("#organTreeUserList").append($tr);
	}

	//기본 권한
	var defaultAcl = function(aclArr) {
		$("#defaultAclList").empty();
		var rightLst = rightList();
		for (var i = 0; i < aclArr.length; i++) {
			var acl = aclArr[i];
			var $tr = $('<tr></tr>');
			var inHtml = "";
			var typeName = "<spring:eval expression="@${lang}['USER']"/>";
			var type = acl.aclobjtype;
			if (type == "02") {
				typeName = "<spring:eval expression="@${lang}['DEPARTMENT']"/>";
			} else if (type == "03") {
				typeName = "<spring:eval expression="@${lang}['GROUP']"/>";
			}
			inHtml += "<td style='height: 26px; text-align:left; padding-left:5px;'><input type='checkbox' disabled><label></label></td>";
			inHtml += "<td style='height: 26px; text-align:left;'>" + "<spring:eval expression="@${lang}['DOC_BASIC_INFO']" />" + "</td>";
			inHtml += "<td style='height: 26px; text-align:left;'>" + typeName + "</td>";
			inHtml += "<td style='height: 26px; text-align:left;'>" + acl.name + "</td>";

			for (var j = 0; j < rightLst.length; j++) {
				var right = rightLst[j];
				if (acl.acls == right.codevalue) {
					inHtml += "<td style='height: 26px; text-align:left;>" + right.name + "</td>";
				}
			}

			$tr.data('meta', null);
			$tr.append(inHtml);
			$("#defaultAclList").append($tr);
		}
	}
	//추가 권한
	var listdefaultAcl = function(aclArr) {
		$("#regAclList").empty();
		var rightLst = rightList();
		for (var i = 0; i < aclArr.length; i++) {
			var acl = aclArr[i];
			var $tr = $('<tr></tr>');
			var inHtml = "";
			var typeName = "<spring:eval expression="@${lang}['USER']"/>";
			var type = acl.aclobjtype;
			if (type == "02") {
				typeName = "<spring:eval expression="@${lang}['DEPARTMENT']"/>";
			} else if (type == "03") {
				typeName = "<spring:eval expression="@${lang}['GROUP']"/>";
			}
			inHtml += "<td style='height: 26px; text-align:left; padding-left:5px;'><input type='checkbox' name='chkTargetAcl' id='chkTargetAcl"+acl.aclobjid+"' value='"+acl.aclobjid+"'><label for='chkTargetAcl"+acl.aclobjid+"'></label></td>";
			inHtml += "<td style='height: 26px; text-align:left;'>" + typeName + "</td>";
			inHtml += "<td style='height: 26px; text-align:left;'>" + acl.name + "</td>";

			$tr.data('meta', acl).append(inHtml);
			$("#regAclList").append($tr);
		}
	}

	var delAcl = function() {
		$("#defaultAclList").find("input[name='chkAddAcl']:checked").each( function() {
			var $tr = $(this).parent().parent();
			$tr.remove();
		});
		$("input[name='chkAllTarget']").prop("checked", false);
	}
	//조직탭,그룹탭에따라 호출 함수 분기
	var selectAcl = 1;
	var addAcl = function() {

		var aclList = settedAclList();
		console.log("====addAclRow : ", aclList);

		var rightLst = rightList();
		console.log("=======addAcl======");
		$("#regAclList").find("input[name='chkTargetAcl']:checked") .each(
				function() {
					var $tr = $(this).parent().parent();
					var acl = $tr.data("meta");
					console.log("====data : ", acl);

					var $tr = $('<tr></tr>');
					var inHtml = "";
					var typeName = "<spring:eval expression="@${lang}['USER']"/>";
					var type = acl.aclobjtype;
					if (type == "02") {
						typeName = "<spring:eval expression="@${lang}['DEPARTMENT']"/>";
					} else if (type == "03") {
						typeName = "<spring:eval expression="@${lang}['GROUP']"/>";
					}
					inHtml += "<td style='height: 26px; text-align:left; padding-left:5px;'><input type='checkbox' name='chkAddAcl' id='chkAddAcl"+acl.aclobjid+"' value='"+acl.aclobjid+"'><label for='chkAddAcl"+acl.aclobjid+"'></label></td>";
					inHtml += "<td style='height: 26px; text-align:left'>" + "<spring:eval expression="@${lang}['ADD_AUTHORITY_TARGET']"/>" + "</td>";
					inHtml += "<td style='height: 26px; text-align:left'>" + typeName + "</td>";
					inHtml += "<td style='height: 26px; text-align:left'>" + acl.name + "</td>";

					var $acl = $("<td style='height: 26px; text-align:left'></td>");
					var $select = $("<select style='min-width:90px; width: 90%;'></select>");

					for (var j = 0; j < rightLst.length; j++) {
						var right = rightLst[j];
						var selected = acl.acls == right.codevalue ? "selected" : "";
						$select.append($("<option value='"+right.codevalue+"' "+selected+" >" + right.name + "</option>"));
					}
					console.log("====defaultAcl : ", acl);
					var setted = false;

					for (var i = 0; i < aclList.length; i++) {
						var settedAcl = aclList[i];
						if (settedAcl.aclobjid == acl.aclobjid) {
							setted = true;
							continue;
						}
					}
					if (!setted) {
						acl.objAction = "ADD";//신규권한
						$acl.append($select);
						$tr.data('meta', null);
						$tr.data('meta', acl).append(inHtml).append($acl);
						$("#defaultAclList").append($tr);
					}
				});
	}
	//권한 추가
	var addAclNew = function(acl) {
		var aclList = settedAclList();
		console.log("====acl : " + acl);
		var setted = false;
		for (var i = 0; i < aclList.length; i++) {
			var settedAcl = aclList[i];
			if (settedAcl.aclobjid == acl.aclobjid) {
				setted = true;
				continue;
			}
		}
		//설정되지 않은 권한만 추가
		if (!setted) {
			var $tr = $('<tr></tr>');
			var inHtml = "";
			var typeName = "<spring:eval expression="@${lang}['USER']"/>";
			var type = acl.aclobjtype;
			if (type == "02") {
				typeName = "<spring:eval expression="@${lang}['DEPARTMENT']"/>"
			} else if (type == "03") {
				typeName = "<spring:eval expression="@${lang}['GROUP']"/>"
			}

			inHtml += "<td>" + typeName + "</td>";
			inHtml += "<td>" + acl.name + "</td>";
			var $acl = $("<td></td>");
			var $select = $("<select style='width:90px'></select>");
			var rightLst = rightList();

			for (var j = 0; j < rightLst.length; j++) {
				var right = rightLst[j];
				var selected = acl.acls == right.codevalue ? "selected" : "";
				var disabled = right.codevalue > acl.acls ? "disabled" : "";
				$select.append($("<option value='"+right.codevalue+"' "+selected+" "+disabled+">" + right.name + "</option>"));
			}

			$select.bind("change", changeAcls);
			$acl.append($select);

			$tr.data('meta', acl).append(inHtml).append($acl);
			$("#regAclList").append($tr);
		}
	}

	//변경된 권한정보를 메타에 반영한다.
	var changeAcls = function(e) {
		var acls = $(this).val();
		var item = $(this).parent().parent().data("meta");
		item.acls = acls;
	}

	var addCls = function(cls) {
		var clsList = settedClsList();

		if (clsList.length == 0)
			$("#regClsList").empty();
		console.log("====cls : ", cls);
		var setted = false;
		for (var i = 0; i < clsList.length; i++) {
			var settedCls = clsList[i];
			if (settedCls.classid == cls.classid) {
				setted = true;
				continue;
			}
		}
		if (!setted) {
			var $tr = $('<tr></tr>');
			var inHtml = "";
			inHtml += "<td align='center'><input type='checkbox' name='chkAddCls' id='chkAddCls"+cls.classid+"' value='"+cls.classid+"'><label for='chkAddCls"+cls.classid+"'></label></td>";
			inHtml += "<td>" + cls.name + "</td>";

			$tr.data('meta', cls).append(inHtml);
			$("#regClsList").append($tr);
		}
	}

	var delCls = function() {
		$("#regClsList").find("input[name='chkAddCls']:checked").each( function() {
			var $tr = $(this).parent().parent();
			var data = $tr.data("meta");
			fn_DocRegCls_Tree.unCheckNode(data.classid);
			$tr.remove();
		});
	}

	// file send
	var fileSend = function() {

		var fileExist = false;
		var formData = new FormData();
		for ( var key in fileData) {
			var fileKey = key;
			var fileObj = fileData[key];
			console.log("=====fileKey : " + fileKey + ", fileObj:", fileObj)
			formData.append(fileKey, fileObj);
			fileExist = true;
		}
		if (fileExist == false) {
			alert("<spring:eval expression="@${msgLang}['NO_FILE_REGISTERED']" />");
			return;
		}
		//메타정보
		formData.append("param", "value");

		noty({
			layout : "center",
			text : "<spring:eval expression="@${msgLang}['ARE_YOU_REG_DOC']"/>",
			buttons : [
					{
						addClass : 'b_btn',
						text : "Ok",
						onClick : function($noty) {
							$noty.close();
							openLayer("<spring:eval expression="@${msgLang}['REGISTERING']" />");
							
							fileReg(fileData);
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

	//설정된 권한 목록
	var settedAclList = function() {
		var Items = [];
		$("#defaultAclList").find('tr').each(function(idx) {
			var item = $(this).data('meta');
			if (item) {
				if (item.objAction) {
					Items.push(item);
				}
			}
		});
		return Items;
	}
	//변경할 권한 목록
	var getAddAclList = function() {
		var Items = [];
		//권한 목록에서 변경된 사항만 담는다.
		$("#defaultAclList").find('tr').each(function(idx) {
			var item = $(this).data('meta');
			console.log("===getEditAclList==", item);
			if (item) {
				if (item.objAction) {
					var selected = $(this).find("option:selected").val();
					console.log("====selected : " + selected);
					item.acls = selected;
					Items.push(item); //변경사항이 있을경우에만 추가
				}
			}
		});
		return Items;
	}
	//설정된 권한 목록
	var settedClsList = function() {
		var Items = [];

		$("#regClsList").find('tr').each(function(idx) {
			var item = $(this).data('meta');
			if (item)
				Items.push(item);
		});
		return Items;
	}

	var getRetentionList = function() {

		var data = {};
		data.isactive = "Y";
		data.companyid = "${Authentication.objCompanyid}";
		data.types = "05";

		$.ajax({
			url : "${ctxRoot}/api/system/code/list",
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(data),
			success : function(data) {
				console.log("===retentionList : ", data);
				if (data.status == "0000") {
					var option = "<option id='regRetentionid' value='0' selected>"
							+ "<spring:eval expression="@${lang}['DIRECT_INPUT']"/>" + "</option>";
					data.result.sort(custonSort);
					$.each(data.result, function(idx, result) {
						var codeid = result.codeid;
						var name = result.name;
						if (idx == 0) { //수정필요 영문 or 한글 값 
							option += "<option id='regRetentionid' value='"+codeid+"' selected>" + name + "</option>";
						} else {
							option += "<option id='regRetentionid' value='"+codeid+"'>" + name + "</option>";
						}

					});
					$("#selRetention").append(option);
					
					$("#selRetention").change(function(){
			    		var retention = $("#selRetention option:selected").val();
			    		if (retention == "0") {
			    			$("#expireDate").css('visibility', 'visible');
			    		} else {
			    			$("#expireDate").css('visibility', 'hidden');
			    		}
			    	});
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			},
			beforeSend : function() {
			},
			complete : function() {
			}
		});
	}

	var getDocTypeList = function() {
		$.ajax({
			url : "${ctxRoot}/api/classification/list/down",
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify({
				objIsTest : "N",
				types : "03", // 문서유형
				isactive : "Y"
			}),
			success : function(data) {
				console.log("=== doctype list:", data);
				
				if (data.status == "0000") {
					$("#regDocType").empty();
					var option = "";
					DocTypeList = data.result;
					data.result.sort(custonSort);
					$.each(data.result, function(idx, result) {
						var classification = result.zappClassification;
						var codeid = classification.code;
						var name = classification.name;
						var classid = classification.classid;
						if (idx == 0) { //수정필요 영문 or 한글 값 
							option += "<option id='regDocType' value='" + classid + "' selected>" + name + "</option>";
						} else {
							option += "<option id='regDocType' value='" + classid + "'>" + name + "</option>";
						}
					});
					$("#regDocType").append(option);
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			},
			beforeSend : function() {
			},
			complete : function() {
			}
		});
	}
	
	//변경된 권한정보를 메타에 반영한다.
	var changeExpire = function(e) {
		var retention = $("#selRetention option:selected").val();
		if (retention == "0") {
			$("#regExpireDate").show();
		} else {
			$("#regExpireDate").hide();
		}
	}
	
	var changeDocType = function(e) {
		var docType = $("#regDocType option:selected").val();
		var retentions = document.getElementById("selRetention").options;
		
		$.each(DocTypeList, function(index, item) {
			var doctypeid = item.zappClassification.classid;
			var retentionid = item.zappClassification.retentionid;
			if (docType == doctypeid) {
				$.each(retentions, function(index1, item1) {
					if (retentionid == retentions[index1].value) {
						retentions[index1].selected = true;
					}
				});				
			}
		});
	}
	
	function custonSort(a, b) {
		if (a.priority == b.priority) {
			return 0
		}
		return a.priority > b.priority ? 1 : -1;
	}

	// ECM 등록 API 호출
	var fileReg = function(fileInfo) {
		console.log("=== fileInfo:", fileInfo);	
		
		var mainMeta = {};
		//문서번호
		mainMeta.bno = $("#docNo").val();
		//문서제목
		mainMeta.title = $("#title").val();
		mainMeta.btype = "02"; // 무조건 가상문서 (01:Normal, 02:Virutal)

		//선택된 트리 정보
		var additoryMeta = {};
		additoryMeta.summary = $.trim($("#desc").val()); // 문서에대한 설명
		var classObject = {};
		classObject.classid = selectNodeId(selectedClsid);
		classObject.classtype = selectedClsType;
		var classObjects = settedClsList();//문서분류 정보
		classObjects.push(classObject);//선택문서함 정보 추가
		
		// 문서유형 지정
		classObject = {};
		var docType = $("#regDocType option:selected").val();
		console.log("=== docType:" +  docType);
		classObject.classid = docType;
		classObject.classtype = "03";
		classObjects.push(classObject);//문서유형 추가
		
		//보존년한
		var retention = $("#selRetention option:selected").val();
		//키워드 
		var regKeyword = $("#regKeyword").data("keywords");
		var data = {};
		data.objIsTest = "N";
		data.objTaskid = taskid;
		console.log("=====taskid : " + taskid);
		data.zappClassObjects = classObjects;
		//문서 설정 권한
		if (selectedClsType == "N3") {
			data.zappAcls = [];
		} else {
			data.zappAcls = getAddAclList();
		}
		
		data.objType = "01"; //가상문서의 objType은 무조건 번들임
		data.zappBundle = mainMeta;
		
		//첨부 파일 정보
		var tmpFileInfo = [];
		$.each(fileInfo, function(index, item) {
			var tmpFile = {};
			tmpFile.mfileid = item.mfileid;
			tmpFile.action = item.ctype; //01:Bundle, 02:File
			tmpFileInfo.push(tmpFile);
		});
		data.zappFiles = tmpFileInfo;

		console.log("====retention : ", retention);
		if (retention == "0") {
			var expireDate = $("#expireDate").val();
			console.log("====expireDate : ", expireDate);
			data.zappBundle.expiretime = expireDate;
		} else {
			data.objRetention = retention;
		}

		console.log("===regKeyword : ", regKeyword);
		if (regKeyword && regKeyword.length > 0) {
			var keyObj = [];
			for ( var k in regKeyword) {
				console.log("===keywors : " + regKeyword[k]);
				keyObj.push({"kword" : regKeyword[k]});
			}
			data.zappKeywords = keyObj;
		}
		sendFileInfo = $.extend(true, {}, data);

		console.log("====addContent  : ", data);
		$.ajax({
			url : "${ctxRoot}/api/content/add",
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(data),
			success : function(data) {
				console.log("====add : ", data);
				if (data.status == "0000") {
					alert("<spring:eval expression="@${msgLang}['REG_DOC_SUCCEEDED']" />");
					sendFileInfo = {};
					listSearch();
				} else {
					alertErr(data.message);
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error,
						"<spring:eval expression="@${msgLang}['DOCUMENT_REGI_FAILED']" />");
			},
			beforeSend : function() {
			},
			complete : function() {
				console.log("=== complete");
				closeLayer();
			}
		});
	}
	
	var docRegInitData = function() {
		$("#regUser").val(username);

		for ( var key in fileData) {
			delete fileData[key];
		}
		addFileNO = 0;
		fileList = [];
		dontDouble = false;
	}

	var closeKeyword = function() {
		$("#keywordLayer input[name='kword']").val('');
		$("#keywordLayer").hide();
	}

	var saveKeyword = function() {
		var keywords = $("input[name='kword']");
		var addkeywords = "";
		var objKeys = [];
		keywords.each(function(index) {
			var keyword = $.trim($(this).val());
			if (keyword !== '') {
				var dupKey = ""
				if (objKeys.length > 0) {
					dupKey = objKeys.find(function(item) {
						return item == keyword;
					});
					if (objectIsEmpty(dupKey)) {
						if (index > 0) {
							addkeywords += ",";
						}
						addkeywords += keyword;
						objKeys.push(keyword);
					}
				} else {
					addkeywords += keyword;
					objKeys.push(keyword);
				}
			}
		});
		$("#regKeyword").data("keywords", objKeys);
		$("#regKeyword").val(addkeywords);
		closeKeyword();
	}

	var closeDupicateFile = function() {
		$("input[type='radio'][name='group']:eq(0)").prop("checked", true);
		$("#renameFile").val('');
		$("#duplicateFileLayer").hide();
	}
	var showDuplicateFile = function(type) {
		var css = {
			"position" : 'absolute',
			"width" : '97%',
			"height" : '100%',
			"padding" : '10px',
			"top" : '-20px',
			"left" : '-20px'
		}

		//2 : 파일명만 변경, 3 : 파일명변경, 버전업 선택
		if (type == "02") {
			$("#dupVersionUp").hide();
		} else if (type == "03" || type == "04") {
			$("#dupVersionUp").show();
		}
		console.log("====css : ", css);
		$('#duplicateFileLayer').css(css).show();

		var filename = sendFileInfo.zappFile.filename;
		filename = filename.substr(0, filename.lastIndexOf("."));
		console.log("=====filename : " + filename);
		$("#renameFile").val(filename);

	};
	var saveDupliteFile = function() {
		var checked = $("input[type='radio'][name='group']:checked").val();
		if (checked == 'rename') {
			var rename = $.trim($("#renameFile").val());
			console.log("===rename : " + rename);
			console.log("=====sendFileInfo : ", sendFileInfo);

			if (objectIsEmpty(rename)) {
				alert("<spring:eval expression="@${msgLang}['ERR_MIS_FILENAME']"/>");//파일명 없음 
				return;
			}
			var filename = sendFileInfo.zappFile.filename;
			console.log("=====filename : " + filename);
			var chgName = rename;
			if (rename.lastIndexOf(".") > 0) {
				chgName = rename.substr(0, rename.lastIndexOf("."));
			}
			//if(rename.lastIndexOf(".")>0){
			filename = filename.substr(0, filename.lastIndexOf("."));
			//}
			console.log("=====filename : " + filename + ", chgName : " + chgName);
			if (filename == chgName) {
				alert("<spring:eval expression="@${msgLang}['CANNOT_ADD_SAME_FILE_NAME']"/>");//중복 파일명  
				return;
			}
			sendFileInfo.zappFile.filename = chgName + "." + sendFileInfo.zappFile.objFileExt;
			console.log("====change sendFileInfo : ", sendFileInfo);
			addContent(sendFileInfo);
		} else if (checked == 'versionup') {
			addContent(sendFileInfo);
		}

	}
	var getToday = function() {
		var date = new Date();
		var year = date.getFullYear();
		var month = ("0" + (1 + date.getMonth())).slice(-2);
		var day = ("0" + date.getDate()).slice(-2);
		return year + "-" + month + "-" + day;
	}

	$(document).ready( function() {
			docRegInitData();
			
			// 보존년한 목록 조회
			getRetentionList();
			// 문서유형 목록 조회
			getDocTypeList();
			
			changeDocType();
			
			fn_DocReg_Tree.jstree();
			fn_DocRegGroup_Tree.jstree();
			fn_DocRegCls_Tree.jstree();
			console.log("===USERTYPE : " + selectedClsType);
			if (selectedClsType == "N3") {
				$("#Reg_Tab2").hide();
			} else {
				defaultAcl(defaultDocAclArr);
				listdefaultAcl(defaultDocAclArr);
			}
			
			console.log("=== ZecmLinkReg.jsp lsCallBack : ", lsCallBack);
			console.log("=== ZecmLinkReg.jsp lsCallBack.gubun : ", lsCallBack.gubun);
			console.log("=== ZecmLinkReg.jsp lsCallBack.func : ", lsCallBack.func);
			
			var files = [];
			$.each(lsCallBack.func, function(index, item) {
				//console.log("=== index:" + index + ", item:", item);
				var file = {};
				file.name = item.split("：")[0];
				file.mfileid = item.split("：")[1];
				file.ctype = item.split("：")[2];
				file.size = item.split("：")[3];
				files.push(file);
			});
			
			fileHandle(files);
			
			//첨부 파일 추가
			$("#addFile").click(function() {
				js_addFileBtn();
			});

			//파일 삭제
			$("#delFile").click(function() {
				js_addFileDel();
			});

			//권한설정 폴더 설정 top 전체 체크
			$("input[name='chkAllAcl']").click( function() {
				$("input[name='chkAddAcl']").prop( "checked",
					$("input[name='chkAllAcl']").is(":checked"));
			});
			//권한설정 사용자 bottom 전체 체크
			$("input[name='chkAllTarget']").click( function() {
				var isAllCheck = $("input[name='chkAllTarget']").is(":checked");
				$("input[name='chkTargetAcl']").prop("checked", isAllCheck);
			});

			//분류설정 오른쪽 box 전체 체크
			$("input[name='chkAllCls']").click( function() {
				$("input[name='chkAddCls']").prop("checked", $("input[name='chkAllCls']").is(":checked"));
			});

			$("#regKeyword").click( function() {
				var loadkeywords = $("#regKeyword").data("keywords");
				console.log("==loadkeywords : " + objectIsEmpty(loadkeywords));
				if (!objectIsEmpty(loadkeywords)) {
					var inputKeyword = $("#keywordLayer input[name='kword']");
					inputKeyword.each(function(index) {
						if (!objectIsEmpty(loadkeywords[index])) {
							$(this).val(loadkeywords[index]);
						}
					});
				}

				var offset = $(this).offset();
				var divTop = offset.top - $("#docInfoDefault").offset().top; //상단 좌표
				var divLeft = offset.left - $("#docInfoDefault").offset().left; //좌측 좌표
				$('#keywordLayer').css({
					"top" : divTop,
					"left" : divLeft,
					"position" : "absolute"
				}).show();
			});

			$("input[type='radio'][name='group']").click( function() {
				var checked = $(this).val();
				console.log("===checked : " + checked);
				if (checked == 'rename') {
					$("#dupRename").show();
				} else if (checked == 'versionup') {
					$("#dupRename").hide();
				}
			});

			$("#expireDate").datepicker({
				dateFormat : 'yy-mm-dd'
			});
			$("#expireDate").datepicker().datepicker("setDate", new Date()); //오늘기준 일전 날짜
			var today = new Date();
			today.setDate(today.getDate() + 1);

			$('#expireDate').datepicker("option", "minDate", today);

			$("#expireCal").click(function() {
				$('#expireDate').focus();
			});
			
			// 팝업 닫기
	  		$('#closeBtn').unbind("click").bind("click", function(){
	  			$('.bg').fadeOut();
	  	        $('.popup').fadeOut();
	  	    });
			
		});
	
</script>
</head>
<body>
	<!-- 팝업 -->
	<div class="popup" style="display: block;">
		<h3 class="pageTit">가상문서등록</h3>
		<button type="button" id="closeBtn">
			<img src="${image}/icon/x.png">
		</button>
		<ul class="tabmenu">
			<li class="on">기본 정보</li>
			<li>권한 설정</li>
			<li>분류 정보</li>
		</ul>
		<div class="tabCont">
			<div class="contdiv" id="cont01">
				<h3 class="innerTit">문서 기본 정보</h3>
				<div>
					<p>문서 제목</p>
					<input type="text" class="docTitle" id="title" valeu="">
					<p>문서 번호</p>
					<input type="text" class="docNum" id="docNo" value="${docNo}">
					<p>등록자</p>
					<input type="text" class="docWtr" id="regUser">
					<p>보존 기간</p>
					<select class="docDate" id="selRetention">
					</select>
					<input type ="date" max="9999-12-31" id = "expireDate" style="height: 32px; visibility: hidden;"/>
					<p>문서 유형</p>
					<select class="docType" id="regDocType">
					</select>
					<p>설명</p>
					<input type="text" class="docDes" id="beforeDesc"
						placeholder="해쉬태그 포함">
				</div>
				<h3 class="innerTit">파일 정보</h3>
				<div class="btn_wrap">
					<button type="button" class="btbase" id="delFile">파일 삭제</button>
				</div>
				<div>
					<table class="fileList">
						<colgroup>
							<col width="8%">
							<col width="73%">
							<col width="20%">
						</colgroup>
						<thead>
							<th><input type="checkbox" name="selectAll02" id="allchk2"><label
								for="allchk2"></label></th>
							<th>파일명</th>
							<th>용량</th>
						</thead>
						<tbody id="fileInfo">
						</tbody>
					</table>
				</div>
			</div>
			<!--cont01//-->
			<div class="contdiv" id="cont02">
				<h3 class="innerTit">문서 권한</h3>
				<div>
					<table class="pop_tbl">
						<colgroup>
							<col width="9%">
							<col width="22.75%">
							<col width="22.75%">
							<col width="22.75%">
							<col width="22.75%">
						</colgroup>
						<thead>
							<th><button type="button">
									<img src="${image}/icon/Group 200.png" alt="취소">
								</button></th>
							<th>문서 정보</th>
							<th>권한 유형</th>
							<th>권한 대상</th>
							<th>권한 규칙</th>
						</thead>
						<tbody id="defaultAclList">

						</tbody>
					</table>
				</div>
				<div>
					<button type="button">
						<img src="${image}/icon/bt_up.png" onclick="addAcl();">
					</button>
					<button type="button">
						<img src="${image}/icon/bt_down.png" onclick="delAcl();">
					</button>
				</div>
				<div>
					<table class="pop_tbl">
						<colgroup>
							<col width="9%">
							<col width="45.5%">
							<col width="45.5%">
						</colgroup>
						<thead>
							<th><button type="button">
									<img src="${image}/icon/Group 200.png" alt="취소">
								</button></th>
							<th>권한 대상</th>
							<th>권한 규정</th>
						</thead>
						<tbody id="regAclList">
						</tbody>
					</table>
				</div>
			</div>
			<!--cont02-->
			<div id="cont03" class="contdiv">
				<h3 class="innerTit">분류체계 정보</h3>
				<div class="flex-content">
					<div class="cont_list">
						<div id="clsTree" class="sub"></div>
					</div>
					<div>
						<button type="button" onclick="fn_DocRegCls_Tree.getCheckNodes();">
							<img src="${image}/icon/bt_right.png">
						</button>
						<button type="button" onclick="delCls();">
							<img src="${image}/icon/bt_left.png">
						</button>
						
					</div>
					<div>
						<table class="pop_tbl">
							<colgroup>
								<col width="20%">
								<col width="80%">
							</colgroup>
							<thead>
								<th><button type="button">
										<img src="${image}/icon/Group 200.png" alt="취소">
									</button></th>
								<th>분류명</th>
							</thead>
							<tbody id = "regClsList">
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
		<button type="button" class="btbase" onclick="javascript:fileSend();" style="margin-top: 10px; margin-bottom:-15px; position: relative; left: 45%">저장</button>
	</div>
</body>
</html>