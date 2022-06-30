<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${js}/common.js"></script>
<title>ECM4.0 :: 문서조회</title>
<script type="text/javascript">
	var download = function(versionid) {
		var link = document.createElement("a");
		//link.download = item.filename;
		link.href = "${ctxRoot}/api/file/fileDown/" + versionid;
		link.click();
	}
	
	var docData = "";
	
	//서버에 등록된 파일 리스트 생성
	var setServerFile = function(zappFiles) {
		var inHtml = "";
		var fileCnt = 1;
		var sizeStr = "";
		var $table = $("<table style='table-layout:fixed'>");
		
		console.log("zappFiles.length : " + zappFiles.length);
		for (var i = 0; i < zappFiles.length; i++) {
			var fileinfo = zappFiles[i];
			console.log(i + " / fileinfo.state : " + fileinfo.state);
			if (fileinfo.state == "") { //번들
				filename = fileinfo.filename;
				bundleLinkId = fileinfo.mfileid;
				fileKey = fileinfo.mfileid;
				console.log("== bundle name:" + filename);
				var $tr = $("<tr style='height:30px;'></tr>");
				var $td1 = $("<td style='width:30px;text-align:center;'>" + (fileCnt) + "</td>");
				var $td2 = $("<td style='text-align:left;' title='"+filename+"'>" + filename + "</td>");
				var $td3 = $("<td style='width:70px;padding-left:5px;text-align:center;'>번들</td>");
	
				$tr.data('meta', fileinfo).append($td1).append($td2).append($td3);
				$table.append($tr);
				fileCnt++;

			} else {
				var fileKey = fileinfo.mfileid;
				//var filename = fileinfo.filename;
				var titles = fileinfo.filename.split("：");
				var filename = titles[0];
	
				var version = fileinfo.zArchVersion;
				var versionid = version.versionid;
	
				var size = fileinfo.zArchFile.filesize;
				var sizeKB = size / 1024;
	
				if (parseInt(sizeKB) > 1024) {
					var sizeMB = sizeKB / 1024;
					sizeStr = sizeMB.toFixed(2) + " MB";
				} else {
					sizeStr = sizeKB.toFixed(2) + " KB";
				}
				var $tr = $("<tr style='height:30px;'></tr>");
				if (bundleLinkId == fileinfo.linkid) { // 번들내에 포함된 파일
					fileKey = fileinfo.linkid;
					var $td1 = $("<td style='width:30px;'></td>");
					var $td2 = $("<td class='tdStyle' align='left' title='"+filename+"'><img src='${image}/icon/icon_re.gif'>" + filename + "</td>");
				} else { // 파일
					var $td1 = $("<td style='width:30px;text-align:center;'>" + (fileCnt) + "</td>");
					var $td2 = $("<td class='tdStyle' align='left'  title='" + filename + "'>" + filename + "</td>");
					fileCnt++;
				}
				var $td3 = $("<td style='width:70px;padding-left:5px;text-align:center;'>" + sizeStr + "</td>");
	
				$tr.data('meta', fileinfo).append($td1).append($td2).append($td3);
				
				//클릭이벤트시 다운로드
				$td2.bind('click', function() {
					var data = $(this).parent().data('meta');
					download(data.zArchVersion.versionid);//versionid
				});
	
				//마우스 오버시 스타일
				$td2.hover(function() {
					$(this).addClass('hover');
				}, function() {
					$(this).removeClass('hover');
				});
				$table.append($tr);
			}			
			
		}
		if (fileCnt > 0) {
			$("#fileInfo").empty();
			$("#fileInfo").append($table);
		}
	}

	var setServerCls = function(data) {
		console.log("=== setServerCls data len:" + data.length);
		for (var i = 0; i < data.length; i++) {
			if (data[i].types == "02") {//문서분류 타입인 데이터만 설정
				$("#regClsList").empty();
			}
		}
		for (var i = 0; i < data.length; i++) {
			var cls = data[i];
			if (cls.types == "02") {//문서분류 타입인 데이터만 설정
				var $tr = $('<tr></tr>');
				var inHtml = "";
				inHtml += "<td align='center'><input type='checkbox' name='chkAddCls' id='chkAddCls"+cls.classid+"' value='"+cls.classid+"'></td>";
				inHtml += "<td>" + cls.name + "</td>";

				$tr.data('meta', cls).append(inHtml);
				$("#regClsList").append($tr);
			}
		}
	}

	var setDocType = function(data) {
		console.log("==== setDocType");
		var docTypeName = "";
		var docTypeId = "";
		if (data.length > 0) {
			for (var i = 0; i < data.length; i++) {
				var zappClassification = data[i];
				if (zappClassification.types == '03') {
					docTypeName = zappClassification.name;
					docTypeId = zappClassification.classid;
				}
			}
		}
		console.log("=== docTypeName:" + docTypeName + ", docTypeId:" + docTypeId);
		$("#regDocType").empty();
		var $select = $("<select style='width:60%' id='selDocType'></select>");
		option = "<option id='regDocType' value='" + docTypeId + "' selected>" + docTypeName + "</option>";
		$select.append(option);
		$("#regDocType").append($select);
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
		icon : TREEICONS["CLS_ROOT"],
		text : companyName,
		a_attr : {
			type : "N1"
		}
	});

	var fn_DocReg_Tree = {
		consoleLog : function(data) {
			$.each(data, function(i, v) {
			});
		},
		treeId : "organTree",
		$tree : {},
		jstree : function() {
			this.$tree = $('#' + this.treeId);
			this.$tree.jstree({
				core : {
					check_callback : true,
					data : companyData
				/* 최초에 보여지 최상위 Root Tree */
				},
				types : {
					"default" : {"icon" : "glyphicon glyphicon-flash"},
					file : {icon : "fa fa-file text-inverse fa-lg"}
				},
				checkbox : {"three_state" : false},
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
			var result = this.$tree.jstree('get_selected');
			for (var i = 0; i < result.length; i++) {
				var id = result[i];
				var node = this.$tree.jstree(true).get_node(id);
				var attr = node.a_attr;
				var acl = {};
				acl.aclobjid = node.id;
				acl.name = node.text;
				acl.aclobjtype = attr.type;
				acl.acls = "2";
				addAclNew(acl);
			}
		},
		unCheckNode : function(id) {
			this.$tree.jstree("uncheck_node", id);
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
						child.icon = icon;
						child.a_attr = attr;
						that.$tree.jstree('create_node', obj.upid, child, "last", false, false);
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
			var that = this;
			$.ajax({
				url : "${ctxRoot}/api/organ/deptuser/list",
				type : "POST",
				dataType : 'json',
				contentType : 'application/json',
				async : false,
				// data : { id : selectedNode } , 
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
						child.icon = userIcon;
						child.a_attr = attr;
						that.$tree.jstree('create_node', obj.deptid, child, "last", false, false);
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
					"default" : {"icon" : "glyphicon glyphicon-flash"},
					file : {icon : "fa fa-file text-inverse fa-lg"}
				},
				checkbox : {"three_state" : false},
				plugins : [ "checkbox", "massload", "unique" ]
			}).on("select_node.jstree", function(event, data) { // 노드가 선택된 뒤 처리할 이벤트   	 
				var id = data.node.id;
				var type = data.node.a_attr.type;
				if (type == "01" || type == "02" || type == "03"
						|| type == "04") { //전사, 부서, 개인, 협업 노드인경우에만 리스트를 받아옴
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
			$.ajax({
				url : "${ctxRoot}/api/organ/group/list",
				type : "POST",
				dataType : 'json',
				contentType : 'application/json',
				async : false,
				data : JSON.stringify(data),
				success : function(data) {
					console.log("grouplist : " + JSON.stringify(data));
					$.each(data.result, function(i, obj) {
						var attr = {}
						attr.type = obj.types;
						var child = {};
						child.id = obj.groupid;
						child.text = obj.name;
						child.icon = icon;
						child.a_attr = attr;
						/* 	$('#groupTree').jstree('create_node', obj.upid, child,
									"last", false, false); */
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
		id : companyid,//"CLASS01",
		parent : "#",
		icon : TREEICONS["CLS_ROOT"],
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
					"default" : {"icon" : "glyphicon glyphicon-flash"},
					file : {icon : "fa fa-file text-inverse fa-lg"}
				},
				checkbox : {"three_state" : false},
				plugins : [ "massload", "unique" ]
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
					console.log("====getClsList : ", data);
					$.each(data.result, function(i, result) {
						var attr = {}
						var obj = (result.zappClassification) ? result.zappClassification : result;
						//console.log("===obj : ", obj);
						attr.type = "02";
						var child = {};
						child.id = obj.classid;
						child.text = obj.name;
						child.icon = TREEICONS["CLS_SUB"];
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
			inHtml += "<td align='center' style='height: 26px;'><input type='checkbox' disabled></td>";
			inHtml += "<td>" + "<spring:eval expression="@${lang}['DOC_BASIC_INFO']" />" + "</td>";
			inHtml += "<td>" + typeName + "</td>";
			inHtml += "<td>" + acl.name + "</td>";

			for (var j = 0; j < rightLst.length; j++) {
				var right = rightLst[j];
				if (acl.acls == right.codevalue) {
					inHtml += "<td>" + right.name + "</td>";
				}
			}

			console.log("====defaultAcl : ", acl);
			$tr.data('meta', null);
			$tr.append(inHtml);
			$("#defaultAclList").append($tr);
		}
	}

	var addInitAcl = function() {
		//추가권한 변경된 권한

		if (objectIsEmpty(docData))
			return;
		var data = $.extend(true, [], docData.zappAcls);
		console.log("====docData : ", data);
		for (var i = 0; i < data.length; i++) {
			var acl = data[i];
			acl.name = acl.objname;
			var rightLst = rightList();
			console.log("====addInitAcl : ", acl);

			var $tr = $('<tr></tr>');
			var inHtml = "";
			var typeName = "<spring:eval expression="@${lang}['USER']"/>";
			var type = acl.aclobjtype;
			if (type == "02") {
				typeName = "<spring:eval expression="@${lang}['DEPARTMENT']"/>";
			} else if (type == "03") {
				typeName = "<spring:eval expression="@${lang}['GROUP']"/>";
			}
			inHtml += "<td align='center' style='height: 26px;'><input type='checkbox' disabled></td>";
			inHtml += "<td>" + "<spring:eval expression="@${lang}['ADD_AUTHORITY_TARGET']"/>" + "</td>";
			inHtml += "<td>" + typeName + "</td>";
			inHtml += "<td>" + acl.name + "</td>";

			for (var j = 0; j < rightLst.length; j++) {
				var right = rightLst[j];
				if (acl.acls == right.codevalue) {
					inHtml += "<td>" + right.name + "</td>";
				}
			}

			console.log("====defaultAcl : ", acl);
			$tr.data('meta', null);
			$tr.append(inHtml);
			$("#defaultAclList").append($tr);
		}

	}
	var delAcl = function() {
		$("#regAclList").find("input[name='chkAddAcl']:checked").each( function() {
			var $tr = $(this).parent().parent();
			var data = $tr.data("meta");
			if (selectAcl == 1) {
				fn_DocReg_Tree.unCheckNode(data.aclobjid);
			} else {
				fn_DocRegGroup_Tree.unCheckNode(data.aclobjid);
			}
			$tr.remove();
		});
	}
	
	//조직탭,그룹탭에따라 호출 함수 분기
	var selectAcl = 1;
	var addAcl = function() {
		if (selectAcl == 1) {
			fn_DocReg_Tree.getCheckNodes();
		} else {
			fn_DocRegGroup_Tree.getCheckNodes();
		}
	}
	
	//권한 추가
	var addAclNew = function(acl) {
		var aclList = settedAclList();
		var rightLst = rightList();
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
			;
			inHtml += "<td>" + typeName + "</td>";
			inHtml += "<td>" + acl.name + "</td>";
			var $acl = $("<td></td>");
			var $select = $("<select style='width:90px'></select>");

			for (var j = 0; j < rightLst.length; j++) {
				var right = rightLst[j];
				var selected = acl.acls == right.codevalue ? "selected" : "";
				var disabled = (selected !== "") ? "disabled" : "";
				$select.append($("<option value='"+right.codevalue+"' "+selected+"''>" + right.name + "</option>"));
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
			inHtml += "<td align='center'><input type='checkbox' name='chkAddCls' id='chkAddCls"+cls.classid+"' value='"+cls.classid+"'></td>";
			inHtml += "<td>" + cls.name + "</td>";

			$tr.data('meta', cls).append(inHtml);
			$("#regClsList").append($tr);
		}
	}

	//설정된 권한 목록
	var settedAclList = function() {
		var Items = [];

		$("#regAclList").find('tr').each(function(idx) {
			Items.push($(this).data('meta'));
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

	var retentionList = function(retentionid) {

		var data = {};
		data.isactive = "Y";
		data.types = "05"; // 보존연한
		data.companyid = companyid; //
		$.ajax({
			url : "${ctxRoot}/api/system/code/list",
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(data),
			success : function(data) {
				if (data.status == "0000") {
					$("#regRetention").empty();
					var $select = $("<select style='width:100px;'></select>");
					var option = "<option id='regRetentionid' value='0' selected>"
							+ "<spring:eval expression="@${lang}['DIRECT_INPUT']"/>" + "</option>";
					data.result.sort(custonSort);
					$.each(data.result, function(idx, result) {
						var codeid = result.codeid;
						var name = result.name;
						var selected = "";
						if (codeid == retentionid) {
							selected = "selected";
						} else {
							selected = "";
						}
						option += "<option id='regRetentionid' value='"+codeid+"' "+selected+">" + name + "</option>";
					});
					$select.append(option);
					$("#regRetention").append($select);
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
	function custonSort(a, b) {
		if (a.codevalue == b.codevalue) {
			return 0
		}
		return a.codevalue > b.codevalue ? 1 : -1;
	}
	var isCreator = false;
	var setDocInfo = function(data) {
		//등록자만 설정을 수정가능
		isCreator = (deptuserid == data.creatorid);
		console.log("=== setDocInfo data : ", data);
		var titles = data.title.split("：");
		$("#title").val(titles[0]);

		$("#docNo").val(data.contentno);
		$("#expireDate").val(data.expiretime);
		$("#regUser").val(data.creatorname);
		if (data.summary)
			$("#beforeDesc").text(data.summary);
		//첨부파일 표시
		setServerFile(data.zappFiles);

		//설정 분류 표시
		setServerCls(data.zappClassifications);
		//반출정보 표시
		setLockInfo(data.zappLockedObject);
		//보존기간 설정
		var retentionid = data.retentionid;
		retentionList(retentionid);
		
		setDocType(data.zappClassifications);
		//키워드정보 표시
	}
	var setKeywords = function(data) {
		var keyword = "";
		if (data.length > 0) {
			for (var i = 0; i < data.length; i++) {
				if (i > 0) {
					keyword += ",";
				}
				keyword += data[i].kword;
			}
			$("#regKeyword").data("keywords", data);
		} else {
			$("#regKeyword").data("keywords", null);
		}
		$("#regKeyword").val(keyword);
	}

	var setLockInfo = function(data) {
		var zappLockedObject = data[0];
		if (zappLockedObject) {
			$("#chkoutUser").val(zappLockedObject.lockername);
			$("#lockReason").val(zappLockedObject.reason);
			$("#unLockDate").val(zappLockedObject.releasetime);
		}
	}

	//상세 정보 조회
	var getDocInfo = function() {
		var item = $("#" + selectedContentid).data("meta");
		//item.islocked  -- YS :내가 잠금, Y : 잠금  N : 안잠긴 파일
		if (item) {
			var data = {};
			data.objIsTest = "N";
			data.objTaskid = taskid;
			data.objType = item.contenttype;//01:bundle,02:file
			data.objViewtype = "01";//01:조회용,02:편집용
			data.contentid = selectedContentid;
			console.log("Link Info getDocInfo param: " + JSON.stringify(data));
			$.ajax({
				url : "${ctxRoot}/api/content/view",
				type : "POST",
				dataType : 'json',
				contentType : 'application/json',
				async : false,
				data : JSON.stringify(data),
				success : function(data) {
					console.log("getDocInfo : ", data);
					if (data.status == "0000") {
						docData = data.result;
						var docState = docData.state;
						// 문서기본정보 셋팅
						setDocInfo(docData);
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
		} else {
			console.log("list select item data undefined");
		}
	}
	
	$(document).ready(function() {
		console.log("=== ZecmLinkInfo.jsp");
		
		$("#RegSDate").datepicker($.datepicker.regional[lang]);
		//문서 기본정보 조회
		getDocInfo();
		
		fn_DocReg_Tree.jstree();
		fn_DocRegGroup_Tree.jstree();
		fn_DocRegCls_Tree.jstree();
		console.log("====selectedClsType : " + selectedClsType);
		if (selectedClsType == "N1" || selectedClsType == "N3") {
			$("#Reg_Tab2").hide();
		} else {
			defaultAcl(defaultDocAclArr);
			addInitAcl();
		}
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

		$("#Reg_Tab3").click(function() {
			$('a[id^=Reg_Tab]').removeClass('current');
			$('div[id^=Div_Reg]').hide();
			$("#Reg_Tab3").addClass('current');
			$("#Div_Reg3").show();
		});

		$("#Btn_Acl1").click(function() {
			selectAcl = 1;
			$('a[id^=Btn_Acl]').removeClass('current');
			$('div[id^=Div_Acl]').hide();
			$("#Btn_Acl1").addClass('current');
			$("#Div_Acl1").show();
		});

		$("#Btn_Acl2").click(function() {
			selectAcl = 2;
			$('a[id^=Btn_Acl]').removeClass('current');
			$('div[id^=Div_Acl]').hide();
			$("#Btn_Acl2").addClass('current');
			$("#Div_Acl2").show();
		});

		//$("input[type=text]").attr("disabled", true);
		//$('select').attr('disabled', 'true');
		$('input[type=button][id$=Cls]').hide();
		$("#fldpath").val($("#SelectText").text());
		
		
		
		
		$('#beforeDesc').on('input keyup', converter);
    	$('div[contenteditable]').keydown(function(e) {
     		 if (e.keyCode === 13) {
        	 	return false;
      		 }
     		
     		 $(this).focus();
    	});
		converter();
		
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
		<h3 class="pageTit">가상문서정보</h3>
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
				<div id="fileInfo">
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
			</div>
			<!--cont02-->
			<div id="cont03" class="contdiv">
				<h3 class="innerTit">분류체계 정보</h3>
				<div class="flex-content">
					<div class="cont_list">
						<div id="clsTree" class="sub"></div>
					</div>
					<div>
						<button type="button">
							<img src="${image}/icon/bt_left.png">
						</button>
						<button type="button">
							<img src="${image}/icon/bt_right.png">
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
							<tbody>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>