<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp" %>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ECM4.0 :: 문서수정</title>
<link  type="text/css"rel="stylesheet"  href="${css}/common.css"/>
<link  type="text/css"rel="stylesheet"  href="${css}/jquery-ui-1.11.0.min.css"/>
<link  type="text/css"rel="stylesheet"  href="${css}/jstree.css"/>
<style>
  table {
    width: 100%;
    border: 1px solid #444444;
  }
  th, td {
    border: 1px solid #c4b8b8;
  }
  
  .dragAndDropDiv {
		border: 2px dashed #92AAB0;
		width: 300px;
		color: #000000;
		text-align: center;
		vertical-align: middle;
		padding: 2px 0px 10px 2px;
		font-sie:100%;
		display: table-cell;
	}
.fileInfo  {
		border: 2px solid #92AAB0;
		width: 300px;
		color: #92AAB0;
		text-align: center;
		vertical-align: middle;
		padding: 2px 0px 10px 2px;
		font-sie:100%;
		display: table-cell;
	}
.btnClsInfo  {
		width: 100%;
		text-align: center;
		vertical-align: middle;
		padding: 2px 0px 10px 2px;
		font-sie:100%;
		display: table-cell;
	}	
 .tdStyle{
 	padding-left:5px;
 	overflow:hidden;
 	white-space:nowrap;
 	text-overflow:ellipsis;
 }
 .divMid  {		
		text-align: center;
		vertical-align: middle;
		padding: 2px 0px 10px 2px;
		font-sie:100%;
		display: table-cell;
	}
.listskin_w{border-top:1px solid #3c73c4;border-bottom:1px solid #444;font-size:14px;margin-top:5px;}
.listskin{width:100%;table-layout:fixed;}
.listskin th,
.listskin td{border-right:1px solid #dddddd;border-bottom:1px solid #dddddd;padding:3px 3px 3px;}
.listskin_w.tdtype2 .listskin td{padding:2px;vertical-align: middle;}
.listskin th{background:#f1f2f8;color:#000;font-weight:800;}
.listskin td{}
.listskin td.alignleft{text-align:left;}
.listskin tr th:last-of-type,
.listskin tr td:last-of-type{border-right:0;}
.listskin tr:last-of-type td{border-bottom:0;}
.p_user_box {
    border: 1px solid #9f9f9f;
    margin-top: -1px;
    padding: 2px 1px;
}
</style>
<script type="text/javascript" src="${js}/jquery-1.11.0.min.js"></script>
<script type="text/javascript" src="${js}/jstree.js"></script>
<script type="text/javascript">

	var download = function(versionid) {
		var link = document.createElement("a");
		//link.download = item.filename;
		link.href = "${ctxRoot}/api/file/fileDown/" + versionid;
		link.click();
	}
	if (!String.prototype.startsWith) {
		String.prototype.startsWith = function(search, pos) {
			return this.substr(!pos || pos < 0 ? 0 : +pos, search.length) === search;
		};
	}
	var icon_root = "${ctxRoot}/resources/images/jstree/ic_classification_01.png";
	var icon = "${ctxRoot}/resources/images/jstree/ic_classification_02.png";
	var userIcon = "${ctxRoot}/resources/images/jstree/tree_user_icon03.png";

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
				var node = fn_DocReg_Tree.getNode(id);

				var attr = node.a_attr;
				var acl = {};
				acl.aclobjid = node.id;
				acl.name = node.text;
				acl.aclobjtype = attr.type;
				acl.acls = "2";
				addAclRow(acl);
			});
			//사용자 체크 정보
			$("#organTreeUserList").find("input[name='chkUser']:checked").each(
					function() {
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
	group.enterprise="<spring:eval expression="@${lang}['COMPANY_FOLDER_BOX']"/>"
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
				icon : icon,
				text : group.enterprise,
				a_attr : {
					type : "01",
					root : true
				}
			};
			var deptGroup = {
				id : deptid,
				parent : "#",
				icon : icon,
				text : group.department,
				a_attr : {
					type : "02",
					root : true
				}
			};
			var userGroup = {
				id : deptuserid,
				parent : "#",
				icon : icon,
				text : group.personal,
				a_attr : {
					type : "03",
					root : true
				}
			};
			var collaborGroup = {
				id : "04",
				parent : "#",
				icon : icon,
				text : group.collaborative,
				a_attr : {
					type : "04",
					root : true
				}
			};
			var freeAccessGroup = {
				id : "99",
				parent : "#",
				icon : icon,
				text : group.supergroup,
				a_attr : {
					type : "99",
					root : true
				}
			};
			var groupRootNode = [ companyGroup, deptGroup, userGroup,
					collaborGroup, freeAccessGroup ];
			return groupRootNode;
		},
		jstree : function() {
			this.$tree = $("#"+this.treeId);
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
				fn_DocRegGroup_Tree.getGroupList(data.node.id);
			}).on("loaded.jstree", function() {
				//root node 로드된후 처음 한번 이벤트
				// fn_DocRegGroup_Tree.getDeptList();
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
		getGroupList : function(upid) {
			var zappGroup = {};
			zappGroup.upid = upid;
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
					$.each(data.result, function(i, obj) {
						var attr = {}
						attr.type = "03";
						var child = {};
						child.id = obj.groupid;
						child.text = obj.name;
						child.icon = icon;
						child.a_attr = attr;
						$('#groupTree').jstree('create_node', obj.upid, child,
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
				icon : icon_root,
				text : "<spring:eval expression="@${lang}['CLASSIFICATION']"/>",
				a_attr : {
					type : "02"
				}
			};
			var clsRootNode = [ clsRoot ];
			return clsRootNode;
		},
		jstree : function() {
			this.$tree = $('#'+this.treeId);
			this.$tree.jstree({
				core : {
					check_callback : true,
					data : fn_DocRegCls_Tree.initData()
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
			}).on("select_node.jstree", function(event, data) { 
				var id = data.node.id;
				var type = data.node.a_attr.type;
			}).on("loaded.jstree", function() {
				//root node 로드된후 처음 한번 이벤트
				fn_DocRegCls_Tree.getClsList(fn_DocRegCls_Tree.root.id);
				fn_DocRegCls_Tree.openNode(fn_DocRegCls_Tree.root.id);
			});
		},
		getCheckNodes : function() {
			var result = this.$tree.jstree('get_selected');
			for (var i = 0; i < result.length; i++) {
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
		createNode : function(upid,child,gubun,flag1,flag2) {	
			this.$tree.jstree('create_node', upid,child,gubun,flag1,flag2);
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
					 isactive:"Y"
				}),
				success : function(data) {
					$.each(data.result, function(i, obj) {
						var attr = {}
						attr.type = "02";
						var child = {};
						child.id = obj.classid;
						child.text = obj.name;
						child.icon = icon;
						child.a_attr = attr;
						
						fn_DocRegCls_Tree.createNode(obj.upid, child,"last", false, false);
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

	//문서 등록시 세팅되는 기본 문서 권한
	var defaultDocAclArr = [];

	var docAcl = {};
	docAcl.aclobjid = deptid;
	docAcl.name = deptname;
	docAcl.aclobjtype = "02"; //사용자:01,부서:02,그룹:03
	docAcl.acls = "2";
	defaultDocAclArr.push(docAcl);

	//기본 권한
	var addDefaultAcl = function(aclArr) {
		$("#regAclList").empty();

		for (var i = 0; i < aclArr.length; i++) {
			var acl = aclArr[i];
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
			inHtml += "<td align='center'><input type='checkbox' name='chkAddAcl' id='chkAddAcl"+acl.aclobjid+"' value='"+acl.aclobjid+"'></td>";
			inHtml += "<td>" + typeName + "</td>";
			inHtml += "<td>" + acl.name + "</td>";
			var $acl = $("<td></td>");
			var $select = $("<select style='width:90px'></select>");

			for (var j = 0; j < rightLst.length; j++) {
				var right = rightLst[j];
				var selected = acl.acls == right.codevalue ? "selected" : "";
				$select
						.append($("<option value='"+right.codevalue+"' "+selected+">"
								+ right.name + "</option>"));
			}

			$select.bind("change", changeAcls);
			$acl.append($select);

			$tr.data('meta', acl).append(inHtml).append($acl);
			$("#regAclList").append($tr);
		}
	}

	//등록된 문서 권한 정보 세팅
	var setServerAcl = function(data) {

		if (data.length > 0) {
			$("#regAclList").empty();

			for (var i = 0; i < data.length; i++) {
				var acl = data[i];
				console.log("server acl : " + JSON.stringify(acl));
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
				inHtml += "<td align='center'><input type='checkbox' name='chkAddAcl' id='chkAddAcl"+acl.aclobjid+"' value='"+acl.aclobjid+"'></td>";
				inHtml += "<td>" + typeName + "</td>";
				inHtml += "<td>" + acl.objname + "</td>";
				var $acl = $("<td></td>");
				var $select = $("<select style='width:90px'></select>");
				var selected = "";
				var rightLst = rightList();
				for (var j = 0; j < rightLst.length; j++) {
					var right = rightLst[j];
					selected = acl.acls == right.codevalue ? "selected" : "";
					$select
							.append($("<option value='"+right.codevalue+"' "+selected+">"
									+ right.name + "</option>"));
				}
				$select.bind("change", changeAcls);
				$acl.append($select);
				var item = {};
				item.aclid = acl.aclid;
				item.acls = acl.acls;
				item.aclobjtype = acl.aclobjtype;
				item.aclobjid = acl.aclobjid;
				$tr.data('meta', item).append(inHtml).append($acl);
				$("#regAclList").append($tr);

			}
		}
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
				typeName = "<spring:eval expression="@${lang}['DEPARTMENT']"/>"
			} else if (type == "03") {
				typeName = "<spring:eval expression="@${lang}['GROUP']"/>"
			}
			;
			inHtml += "<td align='center'><input type='checkbox' name='chkAddAcl' id='chkAddAcl"+acl.aclobjid+"' value='"+acl.aclobjid+"'></td>";
			inHtml += "<td>" + typeName + "</td>";
			inHtml += "<td>" + acl.name + "</td>";
			var $acl = $("<td></td>");
			var $select = $("<select style='width:90px'></select>");

			for (var j = 0; j < rightLst.length; j++) {
				var right = rightLst[j];
				var selected = acl.acls == right.codevalue ? "selected" : "";
				$select
						.append($("<option value='"+right.codevalue+"' "+selected+">"
								+ right.name + "</option>"));
			}

			$select.bind("change", changeAcls);
			$acl.append($select);
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
		if (item.aclid) {
			item.objAction = "CHANGE";
		} else {
			item.objAction = "ADD";
		}
	}

	var delSeverAclArr = [];

	//설정된 권한을 삭제한다.
	var delAcl = function() {
		$("#regAclList").find("input[name='chkAddAcl']:checked").each(
				function() {
					var $tr = $(this).parent().parent();
					var data = $tr.data("meta");
					//$("#organTree").jstree("uncheck_node",data.aclobjid );
					if (selectAcl == 1) {
						//체크 되어 있을경우 언체크
						if (data.aclobjtype == "02") {
							fn_DocReg_Tree.unCheckNode(data.aclobjid);
						} else {

						}
					} else {
						fn_DocRegGroup_Tree.unCheckNode(data.aclobjid);
					}
					$tr.remove();
					//서버에 등록된 데이터 여부 판별하여 서버 데이터를 삭제 했을경우 삭제 데이터에 추가
					if (data.aclid) {
						data.objAction = "DISCARD";
						delSeverAclArr.push(data);
					}
				});
	}

	//설정된 권한 목록
	var settedAclList = function() {
		var Items = [];
		$("#regAclList").find('tr').each(function(idx) {
			Items.push($(this).data('meta'));
		});
		return Items;
	}

	//변경할 권한 목록
	var getEditAclList = function() {
		var Items = [];
		//권한 목록에서 변경된 사항만 담는다.
		$("#regAclList").find('tr').each(function(idx) {
			var item = $(this).data('meta');
			if (item) {
				if (item.objAction)
					Items.push(item); //변경사항이 있을경우에만 추가
			}
		});
		//삭제된 목록이 있으면 삭제된 목록 병합
		console.log("delSeverAclArr : " + JSON.stringify(delSeverAclArr));
		var aclsArray = Items.concat(delSeverAclArr);
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
				console.log("server cls data : " + JSON.stringify(cls));
				if (cls.types == "02") {//문서분류 타입인 데이터만 설정				
					var $tr = $('<tr></tr>');
					var inHtml = "";
					inHtml += "<td align='center'><input type='checkbox' name='chkAddCls' id='chkAddCls"+cls.classid+"' value='"+cls.classid+"'></td>";
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
			inHtml += "<td align='center'><input type='checkbox' name='chkAddCls' id='chkAddCls"+cls.classid+"' value='"+cls.classid+"'></td>";
			inHtml += "<td>" + cls.name + "</td>";

			cls.objAction = "ADD";
			$tr.data('meta', cls).append(inHtml);
			$("#regClsList").append($tr);
		}
	}

	//서버권한 목록이 삭제됐을경우에만 데이터를 담는다.
	var delClsArr = [];

	var delCls = function() {
		$("#regClsList").find("input[name='chkAddCls']:checked").each(
				function() {
					var $tr = $(this).parent().parent();
					var data = $tr.data("meta");
					fn_DocRegCls_Tree.unCheckNode(data.classid);
					if (!data.objAction) {
						data.objAction = "DISCARD";

						delClsArr.push(data);
					}
					$tr.remove();
				});
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
		console.log("delClsArr : " + JSON.stringify(delClsArr));
		var clsArray = Items.concat(delClsArr);
		return clsArray;
	}

	//문서 분류 종료

	//보존년한
	var retentionList = function(retentionid) {
		var data = {};
		//data.upid = "2DCB13FD60CB53E2B97D56C86EAE26DF9DFDE8FCB0551562CED851ADEC3C7221";
		data.isactive = "Y";
		data.companyid = "${Authentication.objCompanyid}";
		data.types="05";
		$
				.ajax({
					url : "${ctxRoot}/api/system/code/list",
					type : "POST",
					dataType : 'json',
					contentType : 'application/json',
					async : false,
					data : JSON.stringify(data),
					success : function(data) {
						if (data.status == "0000") {
							retentionData = data.result;
							$("#regRetention").empty();
							var $select = $("<select style='width:80px'></select>");
							data.result.sort(custonSort);
							var option = "";
							$.each(data.result,function(idx, result) {
									var codeid = result.codeid;
									var name = result.name;
									var selected = "";
									console.log("codeid : "+ codeid	+ " retentionid : "+ retentionid);
									if (codeid == retentionid) {
										selected = "selected";
									} else {
										selected = "";
									}
									option += "<option id='regRetentionid' value='"+codeid+"' "+selected+">"+ name + "</option>";
							});
							$select.append(option);
							$("#regRetention").append($select);
						}
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
	function custonSort(a, b) { if(a.priority == b.priority){ return 0} return a.priority > b.priority ? 1 : -1; }
	var fileData = {};
	var addFileNO = 0;
	var fileList = [];

	var fileHandle = function(files) {
		for (var i = 0; i < files.length; i++) {
			if (i == 0) {
				if ($("#title").val() == "")
					$("#title").val(files[i].name);
			}
			if (dupChk(files[i].name)) {
				alert("<spring:eval expression="@${msgLang}['CANNOT_ADD_SAME_FILE_NAME']"/>"+"\n" + files[i].name);
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
	
				inHtml += "<tr style='height:30px;'><td style='width:30px;'><input type='checkbox' name='chkAddFile' id='chkAddFile"+fileKey+"' value='"+fileKey+"'></td>";
				inHtml += "<td class='tdStyle' align='left' title='"+fileObj.name+"'>"
						+ fileObj.name + "</td>";
				inHtml += "<td align='left' ></td>";
				inHtml += "<td style='width:70px;padding-left:5px;'>" + sizeStr
						+ "</td>";
				inHtml += "</tr>";
				fileCnt++;
			}
			//js_addImageView(fileKey);
		}

		$("#fileInfo").append(inHtml);
	}

	var setServerFile = function(data) {
		var inHtml = "";
		var fileCnt = "";
		var sizeStr = "";
		for (var i = 0; i < data.length; i++) {
			var fileinfo = data[i];
			console.log("server file info : "+JSON.stringify(fileinfo));
			var fileKey = fileinfo.mfileid;
			var filename = fileinfo.filename;

			var size = fileinfo.zArchFile.filesize;
			var sizeKB = size / 1024;

			if (parseInt(sizeKB) > 1024) {
				var sizeMB = sizeKB / 1024;
				sizeStr = sizeMB.toFixed(2) + " MB";
			} else {
				sizeStr = sizeKB.toFixed(2) + " KB";
			}

			var $tr = $("<tr style='height:30px;'></tr>");
			var inHtml = "<td style='width:30px;'><input type='checkbox' name='chkAddFile' id='chkAddFile"+fileKey+"' value='"+fileKey+"'></td>";
			inHtml += "<td class='tdStyle' align='left' title='"+filename+"'>"
					+ filename + "</td>";
			inHtml += "<td align='center' style='width:60px;'>";
			//반출된 문서가 아닐경우 반출 버튼 표시
			if(fileinfo.state !="03"){
				inHtml += "<input type='button' class='b_btn btnlock' style='margin-right: 5px;' value='반출'>";
			}
			//반출문서일경우 반입버튼 표시
			if(fileinfo.state =="03"){
				inHtml += "<input type='button' class='b_btn btnunlock' style='margin-right: 5px;' value='반입'>";
			}
			inHtml += "</td>";
			inHtml += "<td style='width:70px;padding-left:5px;'>" + sizeStr
					+ "</td>";

			$tr.data('meta', fileinfo).append(inHtml);
			$tr.on('click', '.btnlock', function() {
				var data = $(this).parent().parent().data('meta');

				lockDoc(data);
			});
			$tr.on('click', '.btnunlock', function() {
				var data = $(this).parent().parent().data('meta');
				for (key in data) {
					console.log("unlock key : "+key+" data :"+JSON.stringify(data[key]));
				}
				//unlockDoc(data);
				js_unLockFileBtn(data);
			});
			$("#fileInfo").append($tr);

			fileCnt++;
		}
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

	//버튼 메뉴 파일 추가
	var js_addFileBtn = function() {
		$("#inputFile").remove();
		var $fileInput = $("<input id='inputFile' name='file' type='file' multiple style='display:none'/>");
		$fileInput.appendTo("body");
		$('#inputFile').on('change', function(e) {
			var files = e.originalEvent.target.files;
			fileHandle(files);
		});
		$("#inputFile").click();
	}

	var delFileArr = [];
	var js_addFileDel = function() {
		$("input[name='chkAddFile']:checked").each(function() {
			var fileKey = $(this).val();
			var fileObj = fileData[fileKey];
			if (fileObj) {
				delete fileObj;
			} else {
				var serverFile = {};
				serverFile.mfileid = fileKey;
				serverFile.action = "DISCARD";
				delFileArr.push(serverFile);
			}
			$(this).parent().parent('tr').remove();
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
		    text : "<spring:eval expression="@${msgLang}['ARE_YOU_MOVE_DOC']"/>",
		    buttons : [ {
		      addClass : 'b_btn',
		      text : "Ok",
		      onClick : function($noty) {
		        $noty.close();
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
			formData.append(fileKey, fileObj);
			fileExist = true;
		}
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
					if (data.result == 0) {
						var sendFilesInfo = data.zappFiles;
						docMetaEdit(sendFilesInfo);
					}
				},
				complete : function() {

				},
			  error : function(request, status, error) {
	        alertNoty(request,status,error);
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
		var mainMeta = {};
		//문서제목
		mainMeta.title = $("#title").val();
		//보존년한
		var retention = $("#regRetention option:selected").val();
		var editFileInfo = fileInfo.concat(delFileArr);
		var data = {};
		data.objIsTest = "N";
		data.objTaskid = taskid;
		data.objType = "01"; //bundle : 01, file : 02
		data.contentid = selectedContentid;
		data.zappClassObject = fldCls; //물리 문서함 정보
		data.zappClassObjects = getEditedClsList(); //문서 분류 정보
		//문서 설정 권한
		data.zappAcls = getEditAclList();
		data.zappBundle = mainMeta;
		//첨부 파일 정보
		data.zappFiles = editFileInfo;
		data.objRetention = retention;
		//TODO -- data validate 
		console.log("Edit doc info : " + JSON.stringify(data));
		noty({
		    layout : "center",
		    text : "<spring:eval expression="@${msgLang}['ARE_YOU_MOVE_DOC']"/>",
		    buttons : [ {
		      addClass : 'b_btn',
		      text : "Ok",
		      onClick : function($noty) {
		        $noty.close();
		        $.ajax({
					url : "${ctxRoot}/api/content/edit",
					type : "POST",
					dataType : 'json',
					contentType : 'application/json',
					async : false,
					data : JSON.stringify(data),
					success : function(data) {
						console.log("edit result : " , data);
						if (data.status == "0000") {
							alert("<spring:eval expression="@${msgLang}['MODIFY_DOC_SUCCEEDED']"/>");
							//listSearch();
						} else {
							alertErr(data.message);
						}
					},
				  error : function(request, status, error) {
		        alertNoty(request,status,error);
					},
					beforeSend : function() {
					},
					complete : function() {
						editDelInfoInit();
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

	//문서 잠금
	var lockDocOld = function() {
		var classList = [];
		classList.push(fldCls);

		var lock = {};
		lock.releasetime = "2020-11-07";//yyyy-mm-dd
		lock.reason = "<spring:eval expression="@${lang}['LOCKING_REASON']"/>";

		var data = {};
		data.objIsTest = "N";
		data.objDebugged = false;
		data.objTaskid = taskid;
		data.objType = "01";
		data.contentid = selectedContentid;
		data.zappClassObjects = classList;
		data.zappLockedObject = lock;
		console.log("lock param : " + JSON.stringify(data));
		$.ajax({
			url : "${ctxRoot}/api/content/lock",
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(data),
			success : function(data) {
				console.log("lock result : " + JSON.stringify(data));
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

	/**
	 * 반출시 잠금
	 */
	var lockDoc = function(data) {
		//잠그는 파일 정보 추가
		var lockFile = {};
		lockFile.mfileid = data.mfileid;
		lockFile.versionid = data.zArchVersion.versionid;
		//레이어에서 아래 정보를 입력 받는다.
		var lockObject = {};
			lockObject.releasetime = "2020-11-20";
			lockObject.reason = "<spring:eval expression="@${lang}['LOCKING_REASON']"/>";
		var data = {};
		data.objIsTest = "N";
		data.objDebugged = false;
		data.objType = "01";
		data.contentid = selectedContentid;
		data.zappFile = lockFile;
		data.zappLockedObject = lockObject;
		console.log("lock param : " + JSON.stringify(data));
		$.ajax({
			url : "${ctxRoot}/api/content/lock",
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(data),
			success : function(data) {
				console.log("lock result : " + JSON.stringify(data));
				if(data.status == "0000"){
					download(lockFile.versionid);
				}else{
					alertErr(data.message);
				}				
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
	
	//버튼 메뉴 파일 추가
	var js_unLockFileBtn = function(data) {
		$("#inputFile").remove();
		var $fileInput = $("<input id='inputFile' name='file' type='file' style='display:none'/>");
		$fileInput.appendTo("body");
		$('#inputFile').on('change', function(e) {
			var files = e.originalEvent.target.files;
			unLockfileSend(data,files);
		});
		$("#inputFile").click();
	}	
	
	var unLockfileSend = function(editdata,files) {
		var formData = new FormData();
		var fileExist = false;
		for ( var i=0 ;i<files.length;i++) {			
			var fileObj = files[i];
			formData.append("file"+i, fileObj);
			fileExist = true;
		}
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
					if (data.result == 0) {
						var sendFilesInfo = data.zappFiles;
						unlockDoc(editdata,sendFilesInfo);
					}
				},
				complete : function() {

				},
			  error : function(request, status, error) {
	        alertNoty(request,status,error);
				}

			});
		} else {
			//파일 없이 반입
			var rtn = confirm("파일없이 반입 처리 하시겠습니까?");
			var files = [];
			//docMetaEdit(files);
		}
	}

	var unlockDoc = function(data,lockFiles) {		
		//잠그는 파일 정보 추가
		var lockFile = lockFiles[0];
		lockFile.mfileid = data.mfileid;
		lockFile.versionid = data.zArchVersion.versionid;		
		lockFile.isreleased = false;//true:상위버전업,false:하위버전업

		var data = {};
		data.objIsTest = "N";
		data.objDebugged = false;
		data.contentid = selectedContentid;
		data.objTaskid = taskid;
		data.objType = "01";
		data.zappFile = lockFile;

		console.log("unlock param : " + JSON.stringify(data));
		$.ajax({
			url : "${ctxRoot}/api/content/unlock",
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(data),
			success : function(data) {
				console.log("lock result : " + JSON.stringify(data));
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

	var setDocInfo = function(data) {

		//첨부파일 표시
		for (key in data) {
			console
					.log("key : " + key + " data : "
							+ JSON.stringify(data[key]));

		}
		$("#title").val(data.title);
		$("#docNo").val(data.contentno);
		$("#regUser").val(data.creatorname);
		$("#expireDate").val(data.expiretime);

		setServerFile(data.zappFiles);
		//설정권한 표시
		setServerAcl(data.zappAcls);
		//설정 분류 표시
		setServerCls(data.zappClassifications);
		var retentionid = data.retentionid;
		retentionList(retentionid);//보존기간 설정
	}
	//편집 상세 정보 조회
	var getDocInfo = function() {
		var item = $("#" + selectedContentid).data("meta");
		if (item) {
			var data = {};
			data.objIsTest = "N";
			data.objTaskid = taskid;
			data.objType = item.contenttype;//01:bundle,02:file
			data.objViewtype = "02";//02:편집용
			data.contentid = selectedContentid;
			$.ajax({
				url : "${ctxRoot}/api/content/view",
				type : "POST",
				dataType : 'json',
				contentType : 'application/json',
				async : false,
				data : JSON.stringify(data),
				success : function(data) {
				  console.log("getDocInfo : " , data);
					if (data.status == "0000") {
						var docData = data.result;
						var docState = docData.state;
						setDocInfo(docData);
					}
				},
			  error : function(request, status, error) {
	        alertNoty(request,status,error);
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

		getDocInfo();
		fn_DocReg_Tree.jstree();
		fn_DocRegGroup_Tree.jstree();
		fn_DocRegCls_Tree.jstree();

		//dragAndDrop
		var objDragAndDrop = $(".dragAndDropDiv");

		$(document).on("dragenter", ".dragAndDropDiv", function(e) {
			e.stopPropagation();
			e.preventDefault();
			$(this).css('border', '1px solid #0B85A1');
		});

		$(document).on("dragover", ".dragAndDropDiv", function(e) {
			e.stopPropagation();
			e.preventDefault();
		});

		$(document).on("drop", ".dragAndDropDiv", function(e) {
			$(this).css('border', '1px dotted #0B85A1');
			e.preventDefault();
			var files = e.originalEvent.dataTransfer.files;
			for (var i = 0; i < files.length; i++) {
				var filename = files[i].name;
				var filesize = files[i].size;
			}
			fileHandle(files);
		});

		$(document).on("dragenter", function(e) {
			e.stopPropagation();
			e.preventDefault();
		});

		$(document).on("dragover", function(e) {
			e.stopPropagation();
			e.preventDefault();
			objDragAndDrop.css('border', '1px dotted #0B85A1');
		});

		$(document).on("drop", function(e) {
			e.stopPropagation();
			e.preventDefault();

		});

		//dragAndDrop

		//첨부 파일 추가
		$("#addFile").click(function() {
			js_addFileBtn();
		});

		//파일 삭제
		$("#delFile").click(function() {
			js_addFileDel();
		});

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

	});
</script>
</head>
<body>
<div id="fvtheader">
	<div style="margin-left:20px; margin-top: 5px;">
		<div style="color:#FFFFFF">
			<span style="font-size: 26px; font-weight: bold;">문서수정</span>			
		</div>			
	</div>
</div>
<div class="DocReg" style="position:absolute; width: 100%; top:60px; bottom:10px; margin-right: 15px; margin-left:20px;">
	<!-- 트리화면 -->
	<div id="docInfoDefault">
		<div class="popup_container" style="width: 95%; height: 350px;">
			<div>
 <li><a id="Reg_Tab1" class="current" href="#"><spring:eval expression="@${lang}['BASIC_INFO']" /></a></li>
            <li><a id="Reg_Tab2" href="#"><spring:eval expression="@${lang}['AUTHORITY_MANAGEMENT']" /></a></li>
            <li><a id="Reg_Tab3" href="#"><spring:eval expression="@${lang}['CLASSIFICATION_INFO']" /></a></li>
            <li><a id="Reg_Tab4" href="#"><spring:eval expression="@${lang}['FILE_DETAILS']" /></a></li>
            <li><a id="Reg_Tab5" href="#"><spring:eval expression="@${lang}['DOC_HISTORY']" /></a></li>
		        </ul>
		    </div>
			<div id="Div_Reg1" class="p_tab_box" style="height:330px;">
				<div style="margin-left: 3px; margin-top: 5px; font-size: 15px; font-weight: bold; padding-bottom: 5px;display:table-cell;vertical-align:middle" class="Div_SelectText">
	                <img alt="" src="${image}/iconext/icon_sep_lnb_on.png" style="width: 17px; height: 15px;"> <span id="SelectText">문서 정보</span>
	            </div>
				<div class="DocInfo" style="height: 35%;">
            <table id="docContent">
              <tr style="height: 30px;">
                <td bgcolor="#c7cbd4" style="width: 100px; text-align: center;">
                  <spring:eval expression="@${lang}['DOC_TITLE']" />
                </td>
                <td colspan="7">
                  <input type="text" id="title" name="title" onkeyup='pubByteCheckTextarea(event,500)' style="width: 100%; border: none; background: #fff;" />
                </td>
              </tr>
              <tr style="height: 3px; border: 1px;">
                <td colspan="4"></td>
              </tr>
              <tr>
                <td bgcolor="#c7cbd4" style="width: 100px; text-align: center">
                  <spring:eval expression="@${lang}['DOC_NO']" />
                </td>
                <td colspan="3">
                  <input type="text" readonly disabled id="docNo" name="docNo" style="width: 100%; border: none; background: #fff;" />
                </td>
                <td bgcolor="#c7cbd4" style="width: 100px; text-align: center">
                  <spring:eval expression="@${lang}['REGISTER']" />
                </td>
                <td colspan="3">
                  <input type="text" readonly disabled id="regUser" name="regUser" style="width: 100%; border: none; background: #fff;" />
                </td>
              </tr>
              <tr style="height: 3px; border: 1px;">
                <td colspan="4"></td>
              </tr>
              <tr>
                <td bgcolor="#c7cbd4" style="width: 100px; text-align: center;">
                  <spring:eval expression="@${lang}['RETENTION_PERIOD']" />
                </td>
                <td style="width: 150px;" id="regRetention" colspan="3">
                  <select>
                    <option id="objRetention" value="objRetentionid">5년</option>
                  </select>
                </td>

                <td bgcolor="#c7cbd4" style="width: 100px; text-align: center">
                  <spring:eval expression="@${lang}['EXPIRED_DATE']" />
                </td>
                <td colspan="3">
                  <input type="text" readonly disabled id="expireDate" name="expireDate" style="width: 100%; border: none; background: #fff;" />
                </td>
              </tr>
              <tr style="height: 3px; border: 1px;">
                <td colspan="4"></td>
              </tr>
              <tr>
                <td bgcolor="#c7cbd4" style="width: 100px; text-align: center">
                  <spring:eval expression="@${lang}['EXPORTER']" />
                </td>
                <td colspan="3">
                  <input type="text" readonly disabled id="chkoutUser" name="chkoutUser" style="width: 100%; border: none; background: #fff;" />
                </td>
                <td bgcolor="#c7cbd4" style="width: 100px; text-align: center">
                  <spring:eval expression="@${lang}['SCHEDULED_DATE_OF_CHECK-IN']" />
                </td>
                <td colspan="3">
                  <input type="text" readonly disabled id="unLockDate" name="unLockDate" style="width: 100%; border: none; background: #fff;" />
                </td>
              </tr>
              <tr style="height: 3px; border: 1px;">
                <td colspan="4"></td>
              </tr>
              <tr>
                <td bgcolor="#c7cbd4" style="width: 100px; text-align: center">
                  <spring:eval expression="@${lang}['CHECK-OUT_REASON']" />
                </td>
                <td colspan="3">
                  <input type="text" readonly disabled id="lockReason" name="lockReason" style="width: 100%; border: none; background: #fff;" />
                </td>
                <td bgcolor="#c7cbd4" style="width: 100px; text-align: center">
                  <spring:eval expression="@${lang}['KEYWORD']" />
                </td>
                <td colspan="3">
                  <input type="text" id="regKeyword" name="regKeyword" style="width: 100%;" id="regKeyword" readOnly />
                </td>
              </tr>
            </table>
          </div>
				<div style="margin-left: 3px; margin-top: 5px; font-size: 15px; font-weight: bold; padding-bottom: 5px;display:table-cell;vertical-align:middle" class="Div_SelectText">
	                <img alt="" src="${image}/iconext/icon_sep_lnb_on.png" style="width: 17px; height: 15px;"> <span id="SelectText"><spring:eval expression="@${lang}['FILE_INFO']" /></span>
	            </div>
	            <!-- 첨부 파일 -->
				<div id="attach" class="p_tab_box" style="height:40%;">
			    	<ul >	            
				        <li>
				          	<input type="button" class="b_btn" style="margin-right: 5px;" id="addFile" value="<spring:eval expression="@${lang}['ADD_FILE']"/>">
				           	<input type="button" class="b_btn" style="margin-right: 5px;" id="delFile" value="<spring:eval expression="@${lang}['DELETE_FILE']"/>">
				        </li>	            	
				    </ul>
					<div class="fileInfo" style="float: left;width:68%;height:100px;overflow:auto;">
						<table id="fileInfo" style='table-layout:fixed'>
						
						</table>						
					</div>
					<div class="dragAndDropDiv" style="float: right;width:28%;height:100px;">
						<spring:eval expression="@${msgLang}['DRAGATTACHEDFILE']"/>
					</div>
			    </div>
		    </div>
		    
		    <!-- 권한 정보 start-->
		    <div id="Div_Reg2" class="p_tab_box" style="height:330px;display:none;">
				<div class="docInfoAcl" style="height:90%;">
					<!-- <div style="float: left;width:54%;height:320px;">
						<div>
					        <ul class="p_tab">
					            <li><a id="Btn_Acl1" class="current" href="#">조직</a></li>
					            <li><a id="Btn_Acl2" href="#">그룹</a></li>				          
					        </ul>
					    </div>
					    조직
					    <div id="Div_Acl1" class="p_tab_box" style="height:254px;">
						    <div id="organTree" style="border: 1px solid #f4f5f7;float:left;width:70%;height:262px;overflow:auto;">
								조직트리
						    </div>
						    <div style="border: 1px solid #f4f5f7;float:right;width:28%;height:262px;overflow:auto;">
								<table class="listskin">
								    <caption>사용자 목록</caption>
								    <colgroup>
									    <col style="width:20%" />
									    <col style="width:80%" />
								    </colgroup>
								    <thead>
									    <tr>
									    	<th scope="col" style="align: center;" ><input type="checkbox"></th>
									    	<th scope="col">사용자명</th>
									    </tr>
								    </thead>
								    <tbody id="organTreeUserList">
							            <tr>
							            	<td colspan="2" align="center">사용자 없음.</td>
							            </tr>								  							
								    </tbody>
								</table>
							</div>						    
					    </div>
					    조직
					    그룹
					    <div id="Div_Acl2" class="p_tab_box" style="height:254px;display:none;">
							<div id="groupTree" style="border: 1px solid #f4f5f7;float:left;width:98%;height:262px;overflow:auto;">
								그룹
						    </div>
					    </div>
					    그룹
					</div>
					<div class="divMid" style="margin-left:8px;float: left;width:6%;height:320px;vertical-align:middle;">
						<li style="margin-top: 130px; ">
				          	<input type="button" class="b_btn" style="margin-right: 5px;" id="addAcl" value="+" onclick="addAcl()"><br><br>
				           	<input type="button" class="b_btn" style="margin-right: 5px;" id="delAcl" value="-" onclick="delAcl();">
				        </li>
					</div> -->
					<!-- <div id="aclList" class="fileInfo" style="float: right; width: 37%; height: 320px; overflow: auto;"> -->
            <div id="aclList" style="width: 100%; height: 320px; overflow: auto;">
						<table class="listskin">
						    <caption><spring:eval expression="@${lang}['USER_LIST']" /></caption>
						    <colgroup>
							   <!--  <col style="width:10%" /> -->
							    <col style="width:20%" />
							    <col style="width:35%" />
							    <col style="width:35%" />		
						    </colgroup>
						    <thead>
							    <tr>
							    	<!-- <th scope="col"><input type="checkbox"></th> -->
						         <th scope="col"><spring:eval expression="@${lang}['AUTHORITY_TYPE']" /></th>
                    <th scope="col"><spring:eval expression="@${lang}['AUTHORITY_TARGET']" /></th>
                    <th scope="col"><spring:eval expression="@${lang}['AUTHORITY_ROLE']" /></th>
							    </tr>
						    </thead>
						    <tbody id="regAclList">
					            <tr>
					            	<td colspan="4" align="center"><spring:eval expression="@${msgLang}['NOAUTHORITY']" /></td>
					            </tr>								  							
						    </tbody>
						</table>
					</div>					
				</div>
		    </div>
		     <!-- 권한 정보 end-->
		     <!-- 분류 정보 start-->
		     <div id="Div_Reg3" class="p_tab_box" style="height:330px;display:none;">
		     	<div style="margin-left: 3px; margin-top: 5px; font-size: 15px; font-weight: bold; padding-bottom: 5px;display:table-cell;vertical-align:middle" class="Div_SelectText">
	                <img alt="" src="${image}/iconext/icon_sep_lnb_on.png" style="width: 17px; height: 15px;"> <span id="SelectText"><spring:eval expression="@${lang}['DOC_CLASSIFICATION']" /></span>
	            </div>
				<div class="docInfoClass" style="height:90%;">
					<div style="float: left;width:54%;height:320px;">

					    <!-- 조직 -->
					    <div id="Div_Cls1" class="p_tab_box" style="height:254px;">
						    <div id="clsTree" style="border: 1px solid #f4f5f7;float:left;width:98%;height:262px;overflow:auto;">
								<spring:eval expression="@${lang}['CLASSIFICATION_TREE']" />
						    </div>						    
					    </div>
					    <!-- 조직 -->
					</div>
					<div class="divMid" style="margin-left:8px;float: left;width:6%;height:320px;vertical-align:middle;">
						<li style="margin-top: 130px; ">
				          	<input type="button" class="b_btn" style="margin-right: 5px;" id="addCls" value="+" onclick="fn_DocRegCls_Tree.getCheckNodes();"><br><br>
				           	<input type="button" class="b_btn" style="margin-right: 5px;" id="delCls" value="-" onclick="delCls();">
				        </li>
					</div>
					<div id="clsList" class="fileInfo" style="float: right;width:37%;height:280px;">
						<table class="listskin">
						    <caption><spring:eval expression="@${lang}['CLASSIFICATION_LIST']" /></caption>
						    <colgroup>
							    <col style="width:10%" />
							    <col style="width:90%" />		
						    </colgroup>
						    <thead>
							    <tr>
							    	<th scope="col"><input type="checkbox"></th>
							    	<th scope="col"><spring:eval expression="@${lang}['CLASSIFICATION_NAME']"/></th>
							    </tr>
						    </thead>
						    <tbody id="regClsList">
					            <tr>
					            	<td colspan="2" align="center"><spring:eval expression="@${msgLang}['NOCLASSIFICATION']"/></td>
					            </tr>								  							
						    </tbody>
						</table>
					</div>
					
				</div>
		    </div>
			<div class="btnClsInfo" style="display: flex;justify-content: center;padding-top: 20px;cursor: Pointer;">
				<input type="button"  class="btn_dg" onclick="javascript:docEdit();" value="<spring:eval expression="@${lang}['MODIFY']"/>">
				<input type="button" style="margin-left: 10px; cursor: pointer;" class="btn_dg" onclick="javascript:docRegClose();" value="<spring:eval expression="@${lang}['CLOSE']"/>">
			</div>
		     <!-- 분류 정보 end-->
		</div>

	</div>
	

</div>
</body>
</html>