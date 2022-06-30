<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${js}/common.js"></script>
<title>ECM4.0 :: 문서수정</title>
<script type="text/javascript">
	var dontDouble = false;

	var download = function(versionid) {
		var link = document.createElement("a");
		document.getElementById("filedown").appendChild(link);
		link.href = "${ctxRoot}/api/file/fileDown/" + versionid;
		link.click();
	}
	if (!String.prototype.startsWith) {
		String.prototype.startsWith = function(search, pos) {
			return this.substr(!pos || pos < 0 ? 0 : +pos, search.length) === search;
		};
	}
	var icon_root = "${ctxRoot}/resources/images/jstree/ic_classification_01.png";
	var icon = "${ctxRoot}/resources/images/jstree/tree_user_icon15.png";
	var userIcon = "${ctxRoot}/resources/images/jstree/tree_user_icon03.png";
	var docData = "";
	var fn_DocReg_Tree = {
		consoleLog : function(data) {
			$.each(data, function(i, v) {
			});
		},
		treeId : "organTree",
		$tree : {},
		root : {
			id : companyid,
			type : "N1"
		},
		initData : function() {
			var companyData = [];
			companyData.push({
				id : companyid,
				parent : "#",
				icon : icon,
				text : companyName,
				a_attr : {
					type : "N1"
				}
			});//companyid
			return companyData;
		},
		jstree : function() {
			var that = this;
			this.$tree = $('#' + this.treeId);
			this.$tree.jstree({
				core : {
					check_callback : true,
					data : fn_DocReg_Tree.initData()
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
				fn_DocReg_Tree.openNode(fn_DocReg_Tree.root.id);
			});
		},
		getCheckNodes : function() {
			//부서 체크 정보
			$.each(this.$tree.jstree("get_checked", true), function() {
				var id = this.id;
				console.log("====organTree : " + id);
				if (id == companyid) {
					return;
				}
				var node = fn_DocReg_Tree.getNode(id);//$('#organTree').jstree(true).get_node(id);

				var attr = node.a_attr;
				var acl = {};
				acl.aclobjid = node.id;
				acl.name = node.text;
				acl.aclobjtype = attr.type;
				acl.acls = "2";
				addAclRow(acl);
			});
			
			//사용자 체크 정보
			$("#organTreeUserList").find("input[name='chkUser']:checked").each( function() {
				let $li = $(this).parent().parent();
				var node = $li.data("meta");
				var attr = node.a_attr;
				var acl = {};
				acl.aclobjid = node.id;
				acl.name = node.text;
				acl.aclobjtype = attr.type;
				acl.acls = "2";
				addAclRow(acl);
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

	var fn_DocRegGroup_Tree = {
		consoleLog : function(data) {
			$.each(data, function(i, v) {
			});
		},
		treeId : "groupTree",
		$tree : {},
		initData : function() {
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
			var groupRootNode = [ companyGroup, deptGroup, userGroup,
					collaborGroup ];
			return groupRootNode;
		},
		jstree : function() {
			this.$tree = $("#" + this.treeId);
			this.$tree.jstree({
				core : {
					check_callback : true,
					data : fn_DocRegGroup_Tree.initData()
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
			var result = this.$tree.jstree('get_selected');
			for (var i = 0; i < result.length; i++) {
				var id = result[i];
				var node = fn_DocRegGroup_Tree.getNode(id);
				var attr = node.a_attr;
				if (!attr.root) {
					var acl = {};
					acl.aclobjid = node.id;
					acl.name = node.text;
					acl.aclobjtype = "03"; //그룹 유형
					acl.acls = "2";
					addAclRow(acl);
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
					console.log("grouplist result : " + JSON.stringify(data));
					$.each(data.result, function(i, obj) {
						var attr = {}
						attr.type = obj.types;
						var child = {};
						child.id = obj.groupid;
						child.text = obj.name;
						child.icon = icon;
						child.a_attr = attr;
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

	var fn_DocRegCls_Tree = {
		consoleLog : function(data) {
			$.each(data, function(i, v) {
			});
		},
		treeId : "clsTree",
		$tree : {},
		root : {
			id : companyid,//"CLASS01",
			type : ""
		},
		initData : function() {
			var clsRoot = {
				id : companyid,//"CLASS01",
				parent : "#",
				icon : POPTREE["COMPANY"],
				text : "<spring:eval expression="@${lang}['CLASSIFICATION']"/>",
				a_attr : {
					type : "02",
					class : "no_checkbox"
				}
			};
			var clsRootNode = [ clsRoot ];
			return clsRootNode;
		},
		jstree : function() {
			this.$tree = $('#' + this.treeId);
			this.$tree.jstree({
				core : {
					check_callback : true,
					data : fn_DocRegCls_Tree.initData()
				/* 최초에 보여지 최상위 Root Tree */
				},
				types : {
					"default" : { "icon" : "glyphicon glyphicon-flash" },
					file : { icon : "fa fa-file text-inverse fa-lg" }
				},
				checkbox : { "three_state" : false },
				plugins : [ "checkbox", "massload", "unique" ]
			}).on("select_node.jstree", function(event, data) {
				var id = data.node.id;
				var type = data.node.a_attr.type;
				fn_DocRegCls_Tree.getClsList(data.node.id);
			}).on("loaded.jstree", function() {
				//root node 로드된후 처음 한번 이벤트
				fn_DocRegCls_Tree.getClsList(fn_DocRegCls_Tree.root.id);
			});
		},
		getCheckNodes : function() {
			var result = this.$tree.jstree('get_selected');
			for (var i = 0; i < result.length; i++) {
				var id = result[i];

				if (id == companyid) { //root id인 경우에는 추가 안함
					continue;
				}
				var id = result[i];
				var node = fn_DocRegCls_Tree.getNode(id);
				var attr = node.a_attr;
				var cls = {};
				cls.classid = node.id;
				cls.name = node.text;
				cls.classtype = attr.type;
				addCls(cls);
			}
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
		createNode : function(upid, child, gubun, flag1, flag2) {
			this.$tree.jstree('create_node', upid, child, gubun, flag1, flag2);
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
					console.log("getClsList : ", data);
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

						fn_DocRegCls_Tree.createNode(obj.upid, child, "last", false, false);
					});
					fn_DocRegCls_Tree.openNode(fn_DocRegCls_Tree.root.id);
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
		inHtml += "<td align='center'><input type='checkbox' name='chkUser' id='chkUser"+user.id+"' value='"+user.id+"'></td>";
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
			inHtml += "<td style='height: 26px; text-align:left;padding-left:5px;'><input type='checkbox' disabled><label></label></td>";
			inHtml += "<td style='height: 26px; text-align:left;'>" + "<spring:eval expression="@${lang}['DOC_BASIC_INFO']" />" + "</td>";
			inHtml += "<td style='height: 26px; text-align:left;'>" + typeName + "</td>";
			inHtml += "<td style='height: 26px; text-align:left;'>" + acl.name + "</td>";


			for (var j = 0; j < rightLst.length; j++) {
				var right = rightLst[j];
				if (acl.acls == right.codevalue) {
					inHtml += "<td style='height: 26px; text-align:left;'>" + right.name + "</td>";
				}
			}
			$tr.data('meta', null);
			$tr.append(inHtml);
			$("#defaultAclList").append($tr);
		}
	}

	//기본 권한
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

			$tr.data('meta', null);
			$tr.data('meta', acl).append(inHtml);
			$("#regAclList").append($tr);
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
			inHtml += "<td style='height: 26px; text-align:left; padding-left:5px'><input type='checkbox' name='chkAddAcl' id='chkAddAcl"+acl.aclobjid+"' value='"+acl.aclobjid+"'></td>";
			inHtml += "<td style='height: 26px; text-align:left;'>" + "<spring:eval expression="@${lang}['ADD_AUTHORITY_TARGET']"/>" + "</td>";
			inHtml += "<td style='height: 26px; text-align:left;'>" + typeName + "</td>";
			inHtml += "<td style='height: 26px; text-align:left;'>" + acl.name + "</td>";

			var $acl = $("<td style='height: 26px; text-align:left;'></td>");
			var $select = $("<select style='min-width:90px; width: 90%;'></select>");

			for (var j = 0; j < rightLst.length; j++) {
				var right = rightLst[j];
				var selected = acl.acls == right.codevalue ? "selected" : "";
				$select.append($("<option value='"+right.codevalue+"' "+selected+" >" + right.name + "</option>"));
			}
			$acl.append($select);
			$tr.data('meta', null);
			$tr.data('meta', acl).append(inHtml).append($acl);
			$("#defaultAclList").append($tr);
		}
		//}

	}

	//조직탭,그룹탭에따라 호출 함수 분기
	var selectAcl = 1;
	var addAcl = function() {
		var data = [];
		if (!objectIsEmpty(docData)) {
			data = $.extend(true, [], docData.zappAcls);
		}
		console.log("====addAcl data : ", data);
		var rightLst = rightList();
		var aclList = settedAclList();
		console.log("=======addAcl======:", aclList);
		$("#regAclList").find("input[name='chkTargetAcl']:checked").each( function() {
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
			inHtml += "<td style='height: 26px; text-align:left; padding-left: 5px;'><input type='checkbox' name='chkAddAcl' id='chkAddAcl"+acl.aclobjid+"' value='"+acl.aclobjid+"'><label for='chkAddAcl"+acl.aclobjid+"'></label></td>";
			inHtml += "<td style='height: 26px; text-align:left;'>" + "<spring:eval expression="@${lang}['ADD_AUTHORITY_TARGET']"/>" + "</td>";
			inHtml += "<td style='height: 26px; text-align:left;'>" + typeName + "</td>";
			inHtml += "<td style='height: 26px; text-align:left;'>" + acl.name + "</td>";

			var $acl = $("<td style='height: 26px; text-align:left;'></td>");
			var $select = $("<select style='min-width:90px; width: 90%;'></select>");
			var setted = false;

			for (var i = 0; i < aclList.length; i++) {
				var settedAcl = aclList[i];
				if (settedAcl.aclobjid == acl.aclobjid) {
					setted = true;
					break;
				}
			}
			if (!objectIsEmpty(data)) {
				for (var i = 0; i < data.length; i++) {
					var settedAcl = data[i];
					if (settedAcl.aclobjid == acl.aclobjid) {
						setted = true;
						continue;
					}
				}
			}
			if (!setted) {
				for (var j = 0; j < rightLst.length; j++) {
					var right = rightLst[j];
					var selected = acl.acls == right.codevalue ? "selected" : "";
					$select.append($("<option value='"+right.codevalue+"' "+selected+" >" + right.name + "</option>"));
				}

				$tr.data('meta', null);
				$select.bind("change", changeAcls);
				$acl.append($select);
				acl.objAction = "ADD";//신규권한
				$tr.data('meta', acl).append(inHtml).append($acl);
				$("#defaultAclList").append($tr);
			} else {
				//변경 권한을 삭제했다 동일 권한을 추가하는 경우에 기존에 가지고있는 권한으로 변경한다.
				console.log("====delSeverAclArr : ", delSeverAclArr);
				if (!objectIsEmpty(delSeverAclArr)) {
					for (var i = 0; i < delSeverAclArr.length; i++) {
						var delAcl = delSeverAclArr[i];

						if (delAcl.aclobjid == acl.aclobjid) {
							var addAcl = {};
							addAcl.name = delAcl.name;
							addAcl.aclid = delAcl.aclid;
							addAcl.acls = delAcl.acls;
							addAcl.aclobjtype = delAcl.aclobjtype;
							addAcl.aclobjid = delAcl.aclobjid;
							console.log("====delAcl : ", addAcl);
							var acls = getAclValue(data, delAcl.aclobjid);
							for (var j = 0; j < rightLst.length; j++) {
								var right = rightLst[j];
								var selected = acls == right.codevalue ? "selected" : "";
								$select.append($("<option value='"+right.codevalue+"' "+selected+" >"
												+ right.name + "</option>"));
							}
							$tr.data('meta', null);
							$select.bind("change", changeAcls);
							$acl.append($select);
							$tr.data('meta', addAcl).append(inHtml).append($acl);
							delSeverAclArr.splice(i, i + 1);
							$("#defaultAclList").append($tr);
							break;
						}
					}
					console.log("=====delSeverAclArr : ", delSeverAclArr);
				}
			}
		});
	}
	var getAclValue = function(data, aclobjid) {
		var acls = -99;
		if (!objectIsEmpty(data)) {
			for (var i = 0; i < data.length; i++) {
				var settedAcl = data[i];
				if (settedAcl.aclobjid == aclobjid) {
					acls = settedAcl.acls;
					break;
				}
			}
		}
		return acls;
	}
	//권한 추가
	var addAclRow = function(acl) {
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
				typeName = "<spring:eval expression="@${lang}['DEPARTMENT']"/>";
			} else if (type == "03") {
				typeName = "<spring:eval expression="@${lang}['GROUP']"/>";
			}

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
			console.log("====addAclRow : ", acl);
			acl.objAction = "ADD";//신규권한
			$tr.data('meta', acl).append(inHtml).append($acl);
			$("#regAclList").append($tr);
		}
	}

	//변경된 권한정보를 메타에 반영한다.
	var changeAcls = function(e) {
		var acls = $(this).val();
		var item = $(this).parent().parent().data("meta");
		item.acls = acls;
		console.log("===changeAcls : ," + item.aclid);
		if (item.aclid) {
			item.objAction = "CHANGE";
		} else {
			item.objAction = "ADD";
		}
	}

	var delSeverAclArr = [];

	//설정된 권한을 삭제한다.
	var delAcl = function() {
		$("#defaultAclList").find("input[name='chkAddAcl']:checked").each( function() {
			var $tr = $(this).parent().parent();
			var data = $tr.data("meta");
			if (data.aclid) {
				console.log("====data.aclid : ", data);
				data.objAction = "DISCARD";
				delSeverAclArr.push(data);
			}
			$tr.remove();
		});
		$("input[name='chkAllTarget']").prop("checked", false);
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
	var getEditAclList = function() {
		var Items = [];
		var data = $.extend(true, [], docData.zappAcls);
		console.log("====docData : ", data);
		//권한 목록에서 변경된 사항만 담는다.
		$("#defaultAclList").find('tr').each(function(idx) {
			var item = $(this).data('meta');
			console.log("===getEditAclList==", item);

			if (item) {
				var selected = $(this).find("option:selected").val();
				console.log("====selected : " + selected);
				item.acls = selected;
				if (item.objAction) {
					Items.push(item); //변경사항이 있을경우에만 추가
				} else {
					for (var i = 0; i < data.length; i++) {
						var acl = data[i];
						if (acl.aclobjid == item.aclobjid) {
							if (acl.acls != item.acls) {
								item.objAction = "CHANGE";
								Items.push(item);
							}
						}
					}

				}
			}
		});
		console.log("====getEditAclList 1 : ", Items);
		//삭제된 목록이 있으면 삭제된 목록 병합

		var aclsArray = Items.concat(delSeverAclArr);
		console.log("====getEditAclList 2 : ", aclsArray);
		return aclsArray;
	}

	//권한 관련 끝

	//문서 분류 시작
	var fldCls = {};
	var setServerCls = function(data) {
		if (data.length > 0) {
			var isCls = false;
			for (var i = 0; i < data.length; i++) {
				var cls = data[i];
				if (cls.types == "02") {//문서분류 타입인 데이터만 설정
					isCls = true;
				}
			}
			if (isCls)
				$("#regClsList").empty();
			for (var i = 0; i < data.length; i++) {
				var cls = data[i];

				if (cls.types == "02") {//문서분류 타입인 데이터만 설정				
					var $tr = $('<tr></tr>');
					var inHtml = "";
					inHtml += "<td align='center'><input type='checkbox' name='chkAddCls' id='chkAddCls"+cls.classid+"' value='"+cls.classid+"'><label for='chkAddCls"+cls.classid+"'></label></td>";
					inHtml += "<td>" + cls.name + "</td>";
					var item = {};
					item.classid = cls.classid;
					item.classtype = cls.types;

					$tr.data('meta', item).append(inHtml);
					$("#regClsList").append($tr);
				}
				if (cls.types.startsWith("N")) {
					fldCls.classid = cls.classid;
				}
			}
		}

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
			inHtml += "<td align='center'><input type='checkbox' name='chkAddCls' id='chkAddCls"+cls.classid+"' value='"+cls.classid+"'><label for='chkAddCls"+cls.classid+"'></label></td>";
			inHtml += "<td>" + cls.name + "</td>";

			cls.objAction = "ADD";
			$tr.data('meta', cls).append(inHtml);
			$("#regClsList").append($tr);
		}
	}

	//서버권한 목록이 삭제됐을경우에만 데이터를 담는다.
	var delClsArr = [];

	var delCls = function() {
		$("#regClsList").find("input[name='chkAddCls']:checked").each( function() {
			var $tr = $(this).parent().parent();
			var data = $tr.data("meta");
			fn_DocRegCls_Tree.unCheckNode(data.classid);
			if (!data.objAction) {
				data.objAction = "DISCARD";
				delClsArr.push(data);
			}
			$tr.remove();
		});
		console.log("====delcls length : " + $("#regClsList").children().length);
		if ($("#regClsList").children().length == 0) {
			var $tr = $('<tr></tr>');
			var inHtml = "<td colspan='2' align='center'><spring:eval expression="@${msgLang}['NOCLASSIFICATION']"/></td>";
			$tr.append(inHtml);
			$("#regClsList").append($tr);
		}
		$("input[name='chkAllCls']").prop("checked", false);
	}

	//설정된 분류 목록
	var settedClsList = function() {
		var Items = [];

		$("#regClsList").find('tr').each(function(idx) {
			var item = $(this).data('meta');
			if (item)
				Items.push(item);
		});
		return Items;
	}

	//변경된 분류 목록
	var getEditedClsList = function() {
		var Items = [];
		$("#regClsList").find('tr').each(function(idx) {
			var item = $(this).data('meta');
			if (item) {
				//신규추가된 데이터만 담는다.
				if (item.objAction)
					Items.push(item);
			}
		});
		//삭제된 목록을 담는다.

		var clsArray = Items.concat(delClsArr);
		return clsArray;
	}

	//문서 분류 종료

	//보존년한
	var getRetentionList = function(retentionid) {
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
				if (data.status == "0000") {
					retentionData = data.result;
					var option = "<option id='regRetentionid' value='0' selected>"+ "<spring:eval expression="@${lang}['DIRECT_INPUT']"/>" + "</option>";
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
	//변경된 권한정보를 메타에 반영한다.
	var changeExpire = function(e) {
		var retention = $("#regRetention option:selected").val();
		if (retention == "0") {
			$("#regRetentionDate").show();
		} else {
			$("#regRetentionDate").hide();
		}
	}
	function custonSort(a, b) {
		if (a.priority == b.priority) {
			return 0
		}
		return a.priority > b.priority ? 1 : -1;
	}
	var fileData = {};
	var addFileNO = 0;
	var fileList = [];

	var fileHandle = function(files) {

		//fileInfo에 중복으로 데이터가 들어가는걸 막기위해서 추가 데이터가 있으면 목록에서 지워준다.
		var keys = Object.keys(fileData);
		console.log("====keys : " + keys.length);
		if (keys.length > 0) {
			for (var i = 0; i < keys.length; i++) {
				$("#fileInfo tr:last").remove();
			}
		}

		for (var i = 0; i < files.length; i++) {
			if (i == 0) {
				if ($("#title").val() == "")
					$("#title").val(files[i].name);
			}
			if (dupChk(files[i].name)) {
				alert("<spring:eval expression="@${msgLang}['CANNOT_ADD_SAME_FILE_NAME']"/>"
						+ "\n" + files[i].name);
			} else {
				if(files[i].type != ""){//폴더를 선택한 경우 제외하기 위해 추가함
					fileData['addfile' + addFileNO] = files[i];
					addFileNO++;
				}
			}
		}
		var inHtml = "";
		var fileCnt = "";
		var sizeStr = "";
		for ( var key in fileData) {
			var fileKey = key;
			var fileObj = fileData[key];
			var size = fileObj.size;
			var sizeKB = size / 1024;

			if(fileObj.type != ""){//폴더를 선택한 경우 제외하기 위해 추가함
				if (parseInt(sizeKB) > 1024) {
					var sizeMB = sizeKB / 1024;
					sizeStr = sizeMB.toFixed(2) + " MB";
				} else {
					sizeStr = sizeKB.toFixed(2) + " KB";
				}
	
				inHtml += "<tr style='height:30px;'>";
				inHtml += "<td style='width:30px;'><input type='checkbox' name='chkAddFile' id='chkAddFile"+fileKey+"' value='"+fileKey+"'><label for='chkAddFile"+fileKey+"'></label></td>";
				inHtml += "<td class='tdStyle' align='left' title='"+fileObj.name+"'>" + fileObj.name + "</td>";
				inHtml += "<td style='width:70px;padding-left:5px;'>" + sizeStr + "</td>";
				inHtml += "</tr>";
				fileCnt++;
			}
		}

		$("#fileInfo").append(inHtml);
	}

	var lockData;
	var unlockData;
	var setServerFile = function(zappFiles) {
		var inHtml = "";
		var fileCnt = "";
		var sizeStr = "";
		var bundleLinkId = "";
		for (var i = 0; i < zappFiles.length; i++) {
			var fileinfo = zappFiles[i];

			if (fileinfo.state == "") { //번들
				filename = fileinfo.filename;
				bundleLinkId = fileinfo.mfileid;
				fileKey = fileinfo.mfileid;
				console.log("== bundle name:" + filename);
				var $tr = $("<tr style='height:30px;'></tr>");
				var $td1 = $("<td style='width:30px;'><input type='checkbox' name='chkAddFile' id='chkAddFile"+fileKey+"' value='"+fileKey+"'><label for='chkAddFile"+fileKey+"'></label></td>");
				var $td2 = $("<td class='tdStyle' align='left' title='"+filename+"'>" + filename + "</td>");
				var $td3 = $("<td style='width:70px;padding-left:5px;text-align:center;'>번들</td>");
	
				$tr.data('meta', fileinfo).append($td1).append($td2).append($td3);
				$("#fileInfo").append($tr);

			} else { //파일
				var fileKey = fileinfo.mfileid;
				//var filename = fileinfo.filename;
				var titles = fileinfo.filename.split("：");
				var filename = titles[0];
	
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
					var $td1 = $("<td style='width:30px;'><input type='checkbox' name='chkAddFile' id='chkAddFile"+fileKey+"' value='"+fileKey+"'><label for='chkAddFile"+fileKey+"'></label></td>");					
					var $td2 = $("<td class='tdStyle' align='left' title='"+filename+"'>" + filename + "</td>");
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
	
				$tr.on('click', '.btnlock', function(e) {
					var data = $(this).parent().parent().data('meta');
					lockData = data;
					var offset = $(this).offset();
	
					var divTop = offset.top - $("#docInfoDefault").offset().top; //상단 좌표
					var divLeft = offset.left - $("#docInfoDefault").offset().left; //좌측 좌표
					$('#lockReasonLayer').css({
						"top" : divTop,
						"left" : divLeft,
						"position" : "absolute"
					}).show();
	
				});
				$tr.on('click', '.btnunlock', function() {
					var data = $(this).parent().parent().data('meta');
					unlockData = data;//전역 변수로 지정 
	
					var offset = $(this).offset();
	
					var divTop = offset.top - $("#docInfoDefault").offset().top; //상단 좌표
					var divLeft = offset.left - $("#docInfoDefault").offset().left; //좌측 좌표
					$('#unlockReason').css({
						"top" : divTop,
						"left" : divLeft,
						"position" : "absolute"
					}).show();
				});
				$("#fileInfo").append($tr);
				fileCnt++;
			}
		}
	}

	var setServerDetailFile = function(zappFiles) {
		var inHtml = "";
		var fileCnt = "";
		var sizeStr = "";
		$("#fileListInfo").empty();

		for (var i = 0; i < zappFiles.length; i++) {
			var fileinfo = zappFiles[i];

			if (fileinfo.state == "") { //번들
			} else { //파일
				var filename = fileinfo.filename;
				var filenames = filename.split("：");
				filename = filenames[0];
				
				var createtime = fileinfo.createtime;
				var version = fileinfo.zArchVersion.hver + "." + fileinfo.zArchVersion.lver
				var state = fileinfo.state;
				var etc = "";
	
				var size = fileinfo.zArchFile.filesize;
				var sizeKB = size / 1024;
	
				if (parseInt(sizeKB) > 1024) {
					var sizeMB = sizeKB / 1024;
					sizeStr = sizeMB.toFixed(2) + " MB";
				} else {
					sizeStr = sizeKB.toFixed(2) + " KB";
				}
	
				if (createtime.length > 19)
					createtime = createtime.substring(0, 19);
	
				var $tr = $("<tr style='height:20px;'></tr>");
				var inHtml = "<td align='center'>" + (i + 1) + "</td>";
				inHtml += "<td align='left'>" + filename + "</td>";
				inHtml += "<td align='center' >" + sizeStr + "</td>";
				inHtml += "<td align='center'>" + version + "</td>";
				inHtml += "<td align='center'>" + createtime + "</td>";
				inHtml += "<td align='center'>" + docStateList(state) + "</td>";
				inHtml += "<td align='center'>" + etc + "</td>";
	
				$tr.data('meta', fileinfo).append(inHtml);
				$tr.on('click', function() {
					var data = $(this).data('meta');
					getFileVersion(data.mfileid);
				});
				$("#fileListInfo").append($tr);
			}
		}
	}
	
	var docStateList = function(state) {
		var data = [];
		data.push({"name" : "<spring:eval expression="@${lang}['NORMAL']"/>", "codevalue" : "00"});
		data.push({"name" : "<spring:eval expression="@${lang}['DISPOSAL_WAIT']"/>", "codevalue" : "01"});
		data.push({"name" : "<spring:eval expression="@${lang}['LOCK']"/>", "codevalue" : "03"});
		data.push({"name" : "<spring:eval expression="@${lang}['CHANGE_REQUEST']"/>", "codevalue" : "A0"});
		data.push({"name" : "<spring:eval expression="@${lang}['DELETE_REQUEST']"/>", "codevalue" : "A1"});
		data.push({"name" : "<spring:eval expression="@${lang}['RECOVER_REQUEST']"/>", "codevalue" : "A2"});
		data.push({"name" : "<spring:eval expression="@${lang}['MOVE_REQUEST']"/>", "codevalue" : "A3"});
		data.push({"name" : "<spring:eval expression="@${lang}['COPY_REQUEST']"/>", "codevalue" : "A4"});
		data.push({"name" : "<spring:eval expression="@${lang}['LOCK_REQUEST']"/>", "codevalue" : "A5"});

		var codevalue = "";
		$.each(data, function(index, item) {
			if (state == item.codevalue) {
				codevalue = item.name;
			}
		});
		return codevalue;
	}

	//선택한 파일의 버전 리스트를 조회한다.
	var getFileVersion = function(mfileid) {
		console.log("=== getFileVersion");
		$.ajax({
			url : "${ctxRoot}/api/content/viewversion",
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify({
				objIsTest : "N",
				contentid : mfileid,
				objViewtype : "02" //01:조회,02:수정
			}),
			success : function(data) {
				setVersionFileList(data);
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

	var setVersionFileList = function(data) {
		var inHtml = "";
		var fileCnt = "";
		var sizeStr = "";
		$("#fileVersionList").empty();
		$.each(data.result, function(i, versioninfo) {
			var filenames = versioninfo.filename.split("：");
			var filename = filenames[0];
			console.log("=== versioninfo.filename:" + versioninfo.filename);
			console.log("=== filename: " + filename);

			var createtime = versioninfo.createtime;
			var version = versioninfo.hver + "."
					+ versioninfo.lver;
			var size = versioninfo.zArchFile.filesize;
			var creator = versioninfo.creatorname;
			var state = "";
			var etc = "";

			var sizeKB = size / 1024;

			if (parseInt(sizeKB) > 1024) {
				var sizeMB = sizeKB / 1024;
				sizeStr = sizeMB.toFixed(2) + " MB";
			} else {
				sizeStr = sizeKB.toFixed(2) + " KB";
			}
			if (createtime.length > 19)
				createtime = createtime.substring(0, 19);

			var $tr = $("<tr style='height:20px;'></tr>");
			var inHtml = "<td align='center'><input type=checkbox name='docCompareVer' id='docCompareVer' value='"+versioninfo.versionid+"'></td>";
			inHtml += "<td align='left'>" + filename + "</td>";
			inHtml += "<td align='center' >" + sizeStr
					+ "</td>";
			inHtml += "<td align='center'>" + version + "</td>";
			inHtml += "<td align='center'>" + creator + "</td>";
			inHtml += "<td align='center'>" + createtime
					+ "</td>";

			$tr.data('meta', versioninfo).append(inHtml);
			$tr.on('click', function() {
				var data = $(this).data('meta');
			});
			$("#fileVersionList").append($tr);
		});
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
	var delFileArr = [];
	var js_addFileDel = function() {
		$("input[name='chkAddFile']:checked").each( function() {
			var fileKey = $(this).val();
			var fileObj = fileData[fileKey];
			if (fileObj) {
				console.log("== if");
				delete fileObj;
				$(this).parent().parent('tr').remove();
			} else {
				console.log("== else");
				var data = $(this).parent().parent().data('meta');
				console.log("== data:", data);
				
				if (data.state != "03") {
					var serverFile = {};
					serverFile.mfileid = fileKey;
					serverFile.action = "DISCARD";
					delFileArr.push(serverFile);
					$(this).parent().parent('tr').remove();
					
					// 번들 하위의 파일목록 삭제
					var childLink = $("input[id='chkAddFile" + data.mfileid + "']");
					childLink.parent().parent('tr').remove();
				} else {
					alert("<spring:eval expression="@${msgLang}['CANNOT_DELETE_FILE']"/>");
				}
			}
		});
	}

	//수정전 삭제정보를 초기화
	var editDelInfoInit = function() {
		delFileArr = [];
		fileData = {};
		delClsArr = [];
		delSeverAclArr = [];
	}

	var docEdit = function() {
		//이전 정보 초기화
		noty({
			layout : "center",
			text : "<spring:eval expression="@${msgLang}['ARE_YOU_MODIFY_DOC']"/>",
			buttons : [ {
				addClass : 'b_btn',
				text : "Ok",
				onClick : function($noty) {
					$noty.close();
					// 파일 등록
					fileSend();
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

	//multipart file send
	var fileSend = function() {
		var formData = new FormData();
		var fileExist = false;
		for ( var key in fileData) {
			var fileKey = key;
			var fileObj = fileData[key];
			console.log("====FormData  fileKey : " + fileKey + ", fileObj : " + fileObj);
			formData.append(fileKey, fileObj);
			fileExist = true;
		}
		console.log("====fileExist : " + fileExist);
		console.log(formData);
		if (fileExist) {
			//파일 및 메타정보 변경
			$.ajax({
				url : "${ctxRoot}/api/file/fileSend",
				data : formData,
				enctype : "multipart/form-data",
				async : true,
				type : "POST",
				dataType : "json",
				processData : false,
				contentType : false,
				cache : false,
				timeout : 600000,
				success : function(data) {
					console.log("====fileSend data : ", data);
					if (data.result == 0) {
						var sendFilesInfo = data.zappFiles;
						//파일전송후 ECM 등록 API 호출
						
						docMetaEdit(sendFilesInfo);
					}
				},
				complete : function() {
					closeLayer();
				},
				error : function(request, status, error) {
					closeLayer();
					alertNoty(request, status, error);
				}
			});
		} else {
			//메타 정보 변경
			var files = [];
			docMetaEdit(files);
		}
	}

	//파일전송후 ECM 등록 API 호출
	var docMetaEdit = function(fileInfo) {
		var item = $("#" + selectedContentid).data("meta");
		var mainMeta = {};
		var zappfile = {};
		//문서제목
		mainMeta.title = $("#title").val();
		var additoryMeta = {};
		additoryMeta.summary = $.trim($("#beforeDesc").text()); // 문서에대한 설명

		//보존년한
		var retention = $("#regRetention option:selected").val();
		console.log("====retention : ", retention);
		
		var editFileInfo = fileInfo.concat(delFileArr);
		var data = {};
		data.objIsTest = "N";
		data.objTaskid = taskid;
		data.objType = item.contenttype; //bundle : 01, file : 02
		data.contentid = selectedContentid;
		data.zappClassObjects = getEditedClsList(); //문서 분류 정보

		// 문서유형 지정
		var docType = $("#regDocType option:selected").val();
		console.log("=== docType:" +  docType);
		classObject = {};
		classObject.classid = docType;
		classObject.classtype = "03";
		data.zappClassObject = classObject; //문서유형  정보

		//문서 설정 권한
		data.zappAcls = getEditAclList();
		//컨텐츠 타입에 따라 설정 정보를 분기한다.
		if (data.objType == "01") {
			if (retention == "0") {
				mainMeta.expiretime = $("#retentionDate").val();
			} else {
				data.objRetention = retention;
			}
			data.zappBundle = mainMeta;
			//첨부 파일 정보
			data.zappFiles = editFileInfo;
			data.zappAdditoryBundle = additoryMeta;//추가 정보
		} else { //파일
			zappfile.filename = $("#title").val();
			zappfile.fno = $("#docNo").val();
			zappfile.summary = additoryMeta.summary;
			if (retention == "0") {
				zappfile.expiretime = $("#retentionDate").val();
			} else {
				data.objRetention = retention;				
			}
			data.zappFile = zappfile;
		}

		//@TODO -- data validate 
		//keyword 변경시 기존값이랑 체크를 해서 처리한다.
		var hashkeyword =$.trim($("#beforeDesc").text()); 
		//값이 있는 경우에만 keywords를 전송 ?? 확인 필요

		data.zappKeywords = getKeywords();
		console.log("data.zappKeywords",data.zappKeywords);

		console.log("param data : ", data);
		//return;
		$.ajax({
			url : "${ctxRoot}/api/content/edit",
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(data),
			success : function(data) {
				console.log("edit result : ", data);
				if (data.status == "0000") {
					alert("<spring:eval expression="@${msgLang}['MODIFY_DOC_SUCCEEDED']"/>");
					listSearch();
				} else {
					alertErr(data.message);
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error,
						"<spring:eval expression="@${msgLang}['DOCUMENT_MODI_FAILED']" />");
			},
			beforeSend : function() {
			},
			complete : function() {
				editDelInfoInit();
			}
		});
	}
	
	var getKeywords = function() {
		var keyObjs = [];  // 수정된 키워드 array
		var newKeyObjs = [];  // 서버에 전달하기 위한 키워드 array (add, discard)
		var newDesc = $("#beforeDesc").text(); // 수정입력된 데이터
			//값이 있는 경우
			console.log("===newDesc : ", newDesc);
			var words = newDesc.split(" ");
			var words = words.filter((element, index) => {
				return words.indexOf(element) === index;
			});
			console.log("words array", words)
			//해쉬값이 있는지 유무 체크
		if (words && words.length > 0) {
			for ( var k in words) { // 빈 값이나 #를 제거했을때 빈 값 키워드에서 제외
				console.log("===words : ", words[k]);
				if(words[k].startsWith('#')){
					if(words[k].indexOf(' ') !== -1 || words[k].replace("#","") == ""){
						continue;
					}else{
						keyObjs.push(words[k].replace("#",""));
					}
				}
			}
			console.log("keyObjs=============", keyObjs);
		}
		//setKeywords
		var orgKeywords = $("#beforeDesc").data("keywords"); // 기존 키워드
		
		console.log("===orgKeywords : ", orgKeywords);
		//오리지널 값이 있는 경우에는 비교하고 없는 경우에는 입력된 해시값을 넘겨준다.
		if (!objectIsEmpty(orgKeywords)) {
				//변경된 값
				var removeKeyword = orgKeywords.filter(function(item) {
					return keyObjs.every(function(key) {
						return item.kword.indexOf(key) === -1;
					});
				});
				console.log("===removeKeyword : ", removeKeyword);
				//추가된 값
				var addKeyword = keyObjs.filter(function(item) {
					return orgKeywords.every(function(key) {
						return key.kword !== item;
					});
				});
				console.log("===addKeyword : ", addKeyword);
				for ( var keyword in addKeyword) {
					newKeyObjs.push({"kword" : addKeyword[keyword], "objAction" : "ADD"});
				}
				for ( var keyword in removeKeyword) {
					newKeyObjs.push({"kwobjid" : removeKeyword[keyword].kwobjid, "objAction" : "DISCARD"});
				}

			} else {
				for ( var keyword in keyObjs) {
					newKeyObjs.push({"kword" : keyObjs[keyword], "objAction" : "ADD"});
				}
			}
		console.log("newKeyObjs======",newKeyObjs);
		return newKeyObjs;
	}
	var isCreator = false;
	
	var setDocInfo = function(data) {
		for ( var key in fileData) {
			delete fileData[key];
		}
		//등록자만 설정을 수정가능
		isCreator = (deptuserid == data.creatorid);

		addFileNO = 0;
		dontDouble = false;

		//첨부파일 표시
		console.log("====setDocInfo : ", data);
		//$("#title").val(data.title);
		var titles = data.title.split("：");
		$("#title").val(titles[0]);

		$("#docNo").val(data.contentno);
		$("#regUser").val(data.creatorname);
		$("#expireDate").val(data.expiretime);
		if (data.summary)
			$("#beforeDesc").text(data.summary);
		
		//키워드 정보
		islocked = data.islocked;
		setServerFile(data.zappFiles);
		
		// 파일상세정보
		setServerDetailFile(data.zappFiles);
		//설정권한 표시 - 기본 폴더 권한을 먼저 그리고 추가하기 위해서 addInitAcl에서 설정해줌
		//설정 분류 표시
		setServerCls(data.zappClassifications);
		var retentionid = data.retentionid;
		
		getRetentionList(retentionid);//보존기간 목록 조회
		
		var expiretime = data.expiretime;		
		if (retentionid == "") { //직접입력
			$("#regRetentionDate").show();
			$("#retentionDate").val(expiretime);
			console.log("== expiretime:" + expiretime);
		}

		console.log("=== contenttype:" + data.contenttype);
		if (data.contenttype == "02") { // File
			document.getElementById('title').readOnly = true;
		} else {
			document.getElementById('title').readOnly = false;
		}
		
		//키워드정보 표시
		setKeywords(data.zappKeywords);
		
		// 문서유형 표시
		setDocType(data.zappClassifications);
	}
	
	var setKeywords = function(data) {
		$("#beforeDesc").data("keywords", data);
	}

	var setLockInfo = function(data) {
		var zappLockedObject = data[0];
		if (zappLockedObject) {
			$("#chkoutUser").val(zappLockedObject.lockername);
			$("#lockReason").val(zappLockedObject.reason);
			$("#unLockDate").val(zappLockedObject.releasetime);
		}
	}
	
	var setDocType = function(data) {
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
		var docTypes = document.getElementById("regDocType").options;
		$.each(docTypes, function(index, item) {
			if (docTypeId == item.value) {
				docTypes[index].selected = true;
			}
		});
	}
	
	//편집 상세 정보 조회
	var getDocInfo = function() {
		var meta = $("#" + selectedContentid).data("meta");
		console.log("=== meta:", meta);
		
		if (meta) {
			var data = {};
			data.objIsTest = "N";
			data.objTaskid = taskid;
			data.objType = meta.contenttype;//01:bundle,02:file
			data.objViewtype = "02";//02:편집용
			data.contentid = selectedContentid;

			console.log("==getDocInfo : " + JSON.stringify(data));
			$.ajax({
				url : "${ctxRoot}/api/content/view",
				type : "POST",
				dataType : 'json',
				contentType : 'application/json',
				async : false,
				data : JSON.stringify(data),
				success : function(data) {
					console.log("editDocInfo : ", data);
					if (data.status == "0000") {
						docData = data.result;
						var docState = docData.state;
						setDocInfo(docData);
					}
				},
				error : function(request, status, error) {
					alertNoty(request, status, error);
				},
				beforeSend : function() {
				},
				complete : function() {
					if (meta.contenttype == "02") {
						$("#fileBtns").hide();
						$("#fileInfos").css({
							"float" : "left",
							"width" : "99%",
							"height" : "220px",
							"overflow" : "auto"
						});
					} else {
						$("#fileBtns").show();
					}
				}
			});
		} else {
			console.log("list select item data undefined");
		}
	}

	// 문서타입 리스트 조회
	var getDocTypeList = function() {
		$.ajax({
			url : "${ctxRoot}/api/classification/list/down",
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify({
				objIsTest : "N",
				types : "03",
				isactive : "Y"
			}),
			success : function(data) {
				console.log("=== doctype list:", data);
				
				if (data.status == "0000") {
					var option = "";
					DocTypeList = data.result;
					data.result.sort(custonSort);
					$.each(data.result, function(idx, result) {
						var classification = result.zappClassification;
						var codeid = classification.code;
						var name = classification.name;
						var classid = classification.classid;
						if (idx == 0) {
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

	// 문서유형에 따른 보존년한 셋팅 (등록시에만 문서유형별 보존년한 세팅하고, 수정시에는 사용자가 선택하도록? 아님 문서유형 변경시 보존년한 변경되도록? 사용자가 등록시 세팅한 보존년한값은 없어짐..)
	var changeDocType = function(e) {
		console.log("=== changeDocType");
		var docType = $("#regDocType option:selected").val();
		console.log("=== docType:" + docType);
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
	
	
	//파일 없이 반입
	var histtotCnt = 0;
	var contentHistory = function(pageno) {
		var data = {};
		data.objIsTest = "N";
		data.objDebugged = false;
		data.logobjid = selectedContentid;
		pageno = pageno ? pageno : 1;
		data.objpgnum = pageno;
		data.objmaporder = {
			"logtime" : "desc"
		};

		$.ajax({
			url : "${ctxRoot}/api/log/content/list",
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(data),
			success : function(data) {
				if (data.status == "0000") {
					console.log("==history :", data);
					setHistoryList(data.result, pageno);
				} else {
					alert("<spring:eval expression="@${msgLang}['CONTENT_INQUIRY_FAILED']"/>");
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			},
			beforeSend : function() {
			},
			complete : function() {
				//첫 페이지에서만 전체 카운트를 확인한다.
				if (pageno == 1) {
					histtotCnt = contentHistoryCnt(data, pageno);
					$("#histtotCnt").empty();
					$("#histtotCnt").text("<spring:eval expression="@${lang}['TOTAL']"/>" + " : " + histtotCnt);
				}
				createHistPageNavi(histtotCnt, pageno, 10);
				//closeLayer();
			}
		});
	}

	var contentHistoryCnt = function(data, pageno) {
		var count = 0;
		$.ajax({
			url : "${ctxRoot}/api/log/content/count",
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(data),
			success : function(data) {
				if (data.status == "0000") {
					count = data.result;
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
		return count;
	}

	//common paging
	var createHistPageNavi = function(totalCnt, curPage, pageSize) {
		var pageCnt = pagecnt;
		var str = "";
		var numberOfBlock = 5;

		var block = Math.ceil(curPage / numberOfBlock);
		var startPage = (block - 1) * numberOfBlock + 1;
		var endPage = startPage + numberOfBlock - 1;
		var totalPage = Math.ceil(histtotCnt / pageCnt);
		if (endPage > totalPage)
			endPage = totalPage;
		var totalBlock = Math.ceil(totalCnt / (numberOfBlock * pageCnt));

		var prevIndex = (block - 1) * numberOfBlock;
		var nextIndex = block * numberOfBlock + 1;

		if (totalCnt > 0) {
			if (block > 1) {
				str += "<a href=\"javascript:pageHistLink" + "(" + prevIndex
						+ ");\" class = 'pagb pg_prev'><span class=\"hdtext\">Go to prev</span></a>";
			}

			for (var i = startPage; i <= endPage; i++) {

				// str += "<a href=\"javascript:pageLink"+"("+i+");\">"+i+"&nbsp;</a>";
				if (i == curPage) {
					str += "<a href=\"#\" class = 'pagb num active'>" + i
							+ "&nbsp;</a>";
				} else {
					str += "<a href=\"javascript:pageHistLink" + "(" + i
							+ ");\" class = 'pagb num'>" + i + "&nbsp;</a>";
				}
			}

			if ((block != totalBlock) && totalBlock > 0) {
				str += "<a href=\"javascript:pageHistLink" + "(" + nextIndex
						+ ");\" class = 'pagb pg_next'><span class=\"hdtext\">Go to next</span></a>";
			}
		}

		$("#pageinghist").empty();
		$("#pageinghist").html(str);
	}

	//페이징 이동
	var pageHistLink = function(page) {
		$("#page").val(page);
		contentHistory(page);
	}

	//로그 action의 한글명
	var logActionName = {
		"A1" : "<spring:eval expression="@${lang}['NEW']"/>",
		"A2" : "<spring:eval expression="@${lang}['LOG_NEW_REQUEST']"/>",
		"A3" : "<spring:eval expression="@${lang}['LOG_NEW_APPROVAL']"/>",
		"A4" : "<spring:eval expression="@${lang}['LOG_NEW_RETURN']"/>",
		"B1" : "<spring:eval expression="@${lang}['MODIFY']"/>",
		"C1" : "<spring:eval expression="@${lang}['MOVE']"/>",
		"C2" : "<spring:eval expression="@${lang}['LOG_MOVE_REQUEST']"/>",
		"C3" : "<spring:eval expression="@${lang}['LOG_MOVE_APPROVAL']"/>",
		"C4" : "<spring:eval expression="@${lang}['LOG_MOVE_RETURN']"/>",
		"D1" : "<spring:eval expression="@${lang}['COPY']"/>",
		"E1" : "<spring:eval expression="@${lang}['DELETE']"/>",
		"E5" : "<spring:eval expression="@${lang}['RESTORE']"/>",
		"F1" : "<spring:eval expression="@${lang}['DISCARD']"/>",
		"G1" : "<spring:eval expression="@${lang}['VIEW']"/>",
		"H1" : "<spring:eval expression="@${lang}['SORT']"/>",
		"Y1" : "<spring:eval expression="@${lang}['LINK']"/>",
		"Z1" : "<spring:eval expression="@${lang}['SHARE']"/>",
		"Z5" : "<spring:eval expression="@${lang}['UNSHARE']"/>",
		"Z9" : "<spring:eval expression="@${lang}['MODIFY_SHARE']"/>",
		"B5" : "<spring:eval expression="@${lang}['CHECK-IN']"/>",
		"B9" : "<spring:eval expression="@${lang}['CHECK-OUT']"/>",
		"I1" : "<spring:eval expression="@${lang}['FAVORITE']"/>",
		"I2" : "<spring:eval expression="@${lang}['UN-FAVORITE']"/>",
		"J1" : "<spring:eval expression="@${lang}['APPROVAL']"/>",
		"J2" : "<spring:eval expression="@${lang}['RETURN']"/>"
	};
	
	// 문서 이력
	var setHistoryList = function(data, pageno) {
		var inHtml = "";
		var pageCnt = "${Authentication.sessEnv.get('SYS_LIST_CNT_PER_PAGE').setval}"; //pageSize -> session의 env pageCnt로 변경
		
		$("#docHistory").empty();
		for (var i = 0; i < data.length; i++) {
			var historyInfo = data[i];

			var contentname = historyInfo.logtext;
			var contentnames = contentname.split("：");
			contentname = contentnames[0];
			
			var gubun = historyInfo.logtype;
			var loggername = historyInfo.loggername;
			var logtime = historyInfo.logtime;
			var action = historyInfo.action;

			var actionname = logActionName[action];
			if (!actionname) {
				actionname = action;
			}

			var listno = (pageno - 1) * pageCnt + i + 1;

			var $tr = $("<tr style='height:20px;'></tr>");
			var inHtml = "<td align='center'>" + listno + "</td>";
			inHtml += "<td align='left'>" + contentname + "</td>";
			inHtml += "<td align='center' >" + actionname + "</td>";
			inHtml += "<td align='center'>" + loggername + "</td>";
			inHtml += "<td align='center'>" + logtime + "</td>";

			$tr.append(inHtml);
			$("#docHistory").append($tr);
		}
	}
	var closeKeyword = function() {
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
		$("#regKeyword").data("editkeywords", objKeys);
		$("#regKeyword").val(addkeywords);
		closeKeyword();
	}

	var getToday = function() {
		var date = new Date();
		var year = date.getFullYear();
		var month = ("0" + (1 + date.getMonth())).slice(-2);
		var day = ("0" + date.getDate()).slice(-2);
		return year + "-" + month + "-" + day;
	}

	$(document).ready(function() {
		if (lang == 'en') {
			$("#chkoutDate").attr("placeholder", "MM/DD/YYYY")
		}

		$("#chkoutDate").datepicker($.datepicker.regional[lang]);
		
		$("#retentionDate").datepicker({dateFormat : 'yy-mm-dd'});
		$("#retentionDate").datepicker().datepicker("setDate", new Date()); //오늘기준 일전 날짜
		var today = new Date();
		today.setDate(today.getDate() + 1);
		
		$('#retentionDate').datepicker("option", "minDate", today);

		$("#retentionCal").click(function() {
			$('#retentionDate').focus();
		});

		// 문서타입 리스트 조회
		getDocTypeList();
		// 문서기본정보 조회
		getDocInfo();				
		
		fn_DocReg_Tree.jstree();
		fn_DocRegGroup_Tree.jstree();
		fn_DocRegCls_Tree.jstree();
		console.log("===USERTYPE : " + selectedClsType);
		if (selectedClsType == CLSTYPES["COMPANY"]
				|| selectedClsType == CLSTYPES["USER"]) {
			$("#Reg_Tab2").hide();
			// $("#Div_Reg2").hide();
		} else {
			//console.log("===defaultDocAclArr : ",defaultDocAclArr);
			defaultAcl(defaultDocAclArr);
			listdefaultAcl(defaultDocAclArr);
			addInitAcl();
			//listdefaultAcl(defaultDocAclArr);
		}
		
		$("#cmtList").click(function(){
			commentList();	
		})
		contentHistory(1);
		
		//파일 삭제
		$("#delFile").click(function() {
			js_addFileDel();
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
		$('#chkoutDate').datepicker("option", "minDate", '0');

		// 파일비교
		$("#docCompare").click(function() {
			docCompareOpen();
		});

		//권한설정 폴더 설정 top 전체 체크
		$("input[name='chkAllAcl']").click( function() {
			$("input[name='chkAddAcl']").prop("checked", $("input[name='chkAllAcl']").is(":checked"));
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

		//폴더경로
		$("#fldpath").val($("#SelectText").text());
		
		 
		$('#beforeDesc').on('input keyup', converter);
    	$('div[contenteditable]').keydown(function(e) {
     		 if (e.keyCode === 13) {
        	 	return false;
      		 }
     		 $(this).focus();
    	});
    	$('#beforeDesc').click(function(){
    		setTimeout(function(){
    			$('#beforeDesc').focus();
    		}, 0);
    	});
    	
		converter();
		
		// 팝업 닫기
  		$('#closeBtn').unbind("click").bind("click", function(){
  			$('.bg').fadeOut();
  	        $('.popup').fadeOut();
  	    });
	});

	var openCalendar = function() {
		$('#chkoutDate').datepicker('show');
	}

	// 파일비교
	var docCompareOpen = function() {

		var arrDocCompareVer = [];
		$('input:checkbox[name="docCompareVer"]:checked').each(function() {
			arrDocCompareVer.push({
				versionid : $(this).val()
			});
		});

		if (arrDocCompareVer.length > 2 || arrDocCompareVer.length <= 1) {
			alert("<spring:eval expression="@${msgLang}['TWO_FILES_COMPARE']"/>");
			return;
		}
		//172.16.44.66:60080
		//alert(arrDocCompareVer[0].versionid);
		window.open("${ctxRoot}/go/docComparePop?uFileId1="
				+ arrDocCompareVer[0].versionid + "&uFileId2="
				+ arrDocCompareVer[1].versionid, '', 'width=1100, height=850;');
	}

	$("#regKeyword").click( function() {
		var inputKeyword = $("#keywordLayer input[name='kword']");
		var loadkeywords = $("#regKeyword").data("keywords");
		console.log("=====inputKeyword : ", inputKeyword);
		console.log("=====loadkeywords : ", loadkeywords);
		if (!objectIsEmpty(inputKeyword)) {
			inputKeyword.each(function(index) {
				if (!objectIsEmpty(loadkeywords)
						&& !objectIsEmpty(loadkeywords[index])) {
					$(this).val(loadkeywords[index].kword);
				}
			});
		}

		var offset = $(this).offset();

		var divTop = offset.top - $("#docInfoDefault").offset().top; //상단 좌표
		var divLeft = offset.left - $("#docInfoDefault").offset().left; //좌측 좌표
		$('#keywordLayer').css({"top" : divTop, "left" : divLeft,"position" : "absolute"}).show();

	});
</script>
</head>
<body>
	<!-- 팝업 -->
    <div class="popup" style="display: block;" id ="docInfoDefault">
        <h3 class="pageTit">가상문서수정</h3>
        <button type="button" id="closeBtn"><img src="${image}/icon/x.png"></button>
        <ul class="tabmenu">
            <li class="on">기본 정보</li>
            <li>권한 설정</li>
            <li>분류 정보</li>
            <li>파일 상세</li>
            <li>문서 이력</li>
            <li id ="cmtList">코멘트</li>
        </ul>
        <div class="tabCont">
        	<!-- 문서 기본 정보 -->
            <div class="contdiv" id="cont01">
                <h3 class="innerTit">기본 정보</h3>
                <div>
                    <p>문서 제목</p>
                    <input type="text" class="docTitle" id = "title" valeu="">
                    <p>문서 번호</p>
                    <input type="text" class="docNum" id="docNo" value="${docNo}">
                    <p>등록자</p>
                    <input type="text" class="docWtr" id = "regUser">
                    <p>보존 기간</p>
                    <select class="docDate" id = "selRetention">
                    </select>
                    <input type ="date" max="9999-12-31" id = "expireDate" style="height: 32px; visibility: hidden;"/>
                    <p>문서 유형</p>
                    <select class="docType" id = "regDocType">
                    </select>
                    <p>설명</p>
                    <input type="text" class="docDes" id ="beforeDesc" placeholder="해쉬태그 포함">
                </div>
                <h3 class="innerTit">파일 정보</h3>
                <div class="btn_wrap">
                    <button type="button" class="btbase" id ="delFile">파일 삭제</button>
                </div>
                <div id="fileInfos">
                	<table id="fileInfo" style='table-layout: fixed'>
					</table>
				</div>
            </div><!--cont01//-->
            <!-- 문서 권한 -->
            <div class="contdiv" id="cont02">
                <h3 class="innerTit">권한 설정</h3>
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
                            <th><button type="button"><img src="${image}/icon/Group 200.png" alt="취소"></button></th>
                            <th>문서 정보</th>
                            <th>권한 유형</th>
                            <th>권한 대상</th>
                            <th>권한 규칙</th>
                        </thead>
                        <tbody id ="defaultAclList">
                           
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
                            <th><button type="button"><img src="${image}/icon/Group 200.png" alt="취소"></button></th>
                            <th>권한 대상</th>
                            <th>권한 규정</th>
                        </thead>
                        <tbody id="regAclList">
                        </tbody>
                    </table>
                </div>
            </div> <!--cont02-->
            <!-- 분류체계 정보 -->
            <div id="cont03" class="contdiv">
				<h3 class="innerTit">분류 정보</h3>
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
			 <!--파일상세-->
			<div class="contdiv" id="cont02">
                <h3 class="innerTit">파일 상세</h3>
                <div>
                    <table class="pop_tbl">
                        <colgroup>
                            <col style="width: 5%" />
							<col style="width: 42%" />
							<col style="width: 11%" />
							<col style="width: 7%" />
							<col style="width: 18%" />
							<col style="width: 8%" />
							<col style="width: 10%" />
                        </colgroup>
                        <thead>
                            <th>순번</th>
                            <th>파일명</th>
                            <th>파일크기</th>
                            <th>버전</th>
                            <th>등록일자</th>
                            <th>상태</th>
                            <th>비고</th>
                        </thead>
                        <tbody id ="fileListInfo">
							<tr>
								<td colspan="7" align="center"><spring:eval expression="@${msgLang}['NO_FILE_REGISTERED']" /></td>
							</tr>
                        </tbody>
                    </table>
                </div>
                <div>
                </div>
                 <h3 class="innerTit">버 전<img alt="<spring:eval expression="@${lang}['COMPARE_FILES']"/>" src="${image}/icon/icon_b01.png" id="docCompare" style="width: 24px; height: 24px; cursor: pointer; padding-left:10px;"></h3>
                <div>
                    <table class="pop_tbl">
                        <colgroup>
                            <col style="width: 5%" />
							<col style="width: 46%" />
							<col style="width: 11%" />
							<col style="width: 6%" />
							<col style="width: 14%" />
							<col style="width: 18%" />
                        </colgroup>
                        <thead>
                            <th>순번</th>
                            <th>파일명</th>
                            <th>파일크기</th>
                            <th>버전</th>
                            <th>등록자</th>
                            <th>등록일자</th>
                        </thead>
                        <tbody id="fileVersionList">
                        	<tr>
								<td colspan="6" align="center"><spring:eval expression="@${msgLang}['NO_SELECTED_FILE']" /></td>
							</tr>
                        </tbody>
                    </table>
                </div>
            </div>
             <!--문서이력-->
            <div class="contdiv" id="cont05">
                <h3 class="innerTit">문서 이력</h3>
                <div>
                    <table class="pop_tbl">
                        <colgroup>
							<col style="width: 5%" />
							<col style="width: 49%" />
							<col style="width: 11%" />
							<col style="width: 13%" />
							<col style="width: 22%" />
                        </colgroup>
                        <thead>
                            <th>순번</th>
                            <th>문서명</th>
                            <th>구분</th>
                            <th>처리자</th>
                            <th>처리일시</th>
                        </thead>
                        <tbody id ="docHistory">
							<tr>
								<td colspan="5" align="center"><spring:eval expression="@${msgLang}['DONT_HISTORY']" /></td>
							</tr>
                        </tbody>
                    </table>
                </div>
                <div id = "pageinghist" class="pagination" style="text-align: center; margin-top: 10px;">
						   
				</div>
            </div>
            <!-- 코멘트 -->
            <div class="contdiv" id="cont02">
                <h3 class="innerTit">코멘트</h3>
                <div style="height: 400px;">
					<table class="pop_tbl">
                        <colgroup>
							<col style="width: 15%" />
							<col style="width: 15%" />
							<col style="width: 70%" />
                        </colgroup>
                        <thead>
                           <th>코멘트 등록자</th>
                           <th>코멘트 등록일자</th>
                           <th>코멘트 등록내용</th>
                        </thead>
                        <tbody id ="CmtHistory">
							<tr>
								<td colspan="3" align="center"><spring:eval expression="@${msgLang}['DONT_HISTORY']" /></td>
							</tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
        <button type="button" class="btbase" onclick="javascript:fileSend();" style="margin-top: 10px; margin-bottom:-15px; position: relative; left: 45%">수정</button>
    </div>
</body>
</html>