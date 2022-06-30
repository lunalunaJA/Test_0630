<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ECM4.0 :: 문서조회</title>
<script type="text/javascript" src="${js}/common.js"></script>
<script type="text/javascript">
	var download = function(versionid) {
		var link = document.createElement("a");
		link.href = "${ctxRoot}/api/file/fileDown/" + versionid;
		link.click();
	}
	var docData = "";
	var docClsId = "";
	//서버에 등록된 파일 리스트 생성
	var setServerFile = function(data, acls) {
		var inHtml = "";
		var fileCnt = 0;
		var sizeStr = "";
		for (var i = 0; i < data.length; i++) {
			var fileinfo = data[i];
			var fileKey = fileinfo.mfileid;
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
			var $td1 = $("<td style='width:30px;text-align:center;'>" + (i + 1)
					+ "</td>");
			var $td2 = $("<td style='text-align:left'  title='" + filename + "'>"
					+ filename + "</td>");
			var $td3 = $("<td style='width:70px;padding-left:5px;'>" + sizeStr
					+ "</td>");

			$tr.data('meta', fileinfo).append($td1).append($td2).append($td3);
			//클릭이벤트시 다운로드
			$td2.bind('click', function() {
				var data = $(this).parent().data('meta');
				if(acls >= 5){
					download(data.zArchVersion.versionid);//versionid	
				}
					
			});

			//마우스 오버시 스타일
			$td2.hover(function() {
				$(this).addClass('hover');
			}, function() {
				$(this).removeClass('hover');
			});
			fileCnt++;
		}
		if (fileCnt > 0) {
			$("#fileInfo").empty();
			$("#fileInfo").append($tr);
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
		option = "<option value='" + docTypeId + "' selected>" + docTypeName + "</option>";
		$("#regDocType").append(option);
	}
	
	if (!String.prototype.startsWith) {
		String.prototype.startsWith = function(search, pos) {
			return this.substr(!pos || pos < 0 ? 0 : +pos, search.length) === search;
		};
	}

	var icon_root = "${ctxRoot}/resources/images/jstree/yellow/icon_24px_class_01.png";
	var icon = "${ctxRoot}/resources/images/jstree/yellow/ic_document_w00.png";
	var userIcon = "${ctxRoot}/resources/images/jstree/tree_user_icon03.png";

	//OgranTree Start
	var companyData = [];
	companyData.push({
		id : companyid,
		parent : "#",
		icon : icon,
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
		icon : icon_root,
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
				// fn_DocRegGroup_Tree.getDeptList();
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
						child.icon = icon;
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
	var defaultAcl = function() {
		console.log("defaultAcl");
		$("#defaultAclList").empty();
		var rightLst = rightList();
		var sendData = {
    	        objIsTest : "N",
    	        classid : docClsId
	    };
		
		var addata = $.extend(true, [], docData.zappAcls);
		console.log("==== defaultAcl docData : ", addata);
		
		
		
		$.ajax({
 	      	url : "${ctxRoot}/api/classification/get",
 	      	// url:"${ctxRoot}/api/acl/get_class", result 숫자 2인경우에만
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
 	        	console.log("===Edit list_class : ", data.result);
 	        	
 	        	$.each(data.result.zappContentAcls, function(i, obj) {
    	        	var acl = {};
    	          	acl.aclobjid = obj.aclobjid;
    	          	acl.name = obj.objname;
    	          	acl.aclobjtype = obj.aclobjtype; //사용자:01,부서:02,그룹:03
    	          	acl.acls = obj.acls;
    	          	
    				var $tr = $('<tr></tr>');
    				var inHtml = "";
    				var typeName = "<spring:eval expression="@${lang}['USER']"/>";
    				var type = acl.aclobjtype;
    				if (type == "02") {
    					typeName = "<spring:eval expression="@${lang}['DEPARTMENT']"/>";
    				} else if (type == "03") {
    					typeName = "<spring:eval expression="@${lang}['GROUP']"/>";
    				}
    				inHtml += "<td style='height: 26px; text-align:left;'><input type='checkbox' disabled></td>";
    				inHtml += "<td style='height: 26px; text-align:left;'>" + "<spring:eval expression="@${lang}['DOC_BASIC_INFO']" />" + "</td>";
    				inHtml += "<td style='height: 26px; text-align:left;'>" + typeName + "</td>";
    				inHtml += "<td style='height: 26px; text-align:left;'>" + acl.name + "</td>";
    				
    				console.log("acl.acls : " + acl.acls);
    				console.log("addata.length : " + addata.length);
    				var $acl = $("<td></td>");
    				for (var j = 0; j <= acl.acls; j++) {
    					var right = rightLst[j];
    					// 추가권한 체크
    					if(addata.length == 0){ // 추가권한 없음
    						if (acl.acls == right.codevalue) {
    							console.log("acl.acls : " + acl.acls+ " / right.codevalue : " + right.codevalue + " / " + right.name);
    							$acl.append("(기본 권한 : " + right.name +")");	
    						}
    					}else{					// 추가권한 있음
    						for(var k = 0 ; k < addata.length ; k++){
    							console.log("acl.aclobjid : " + acl.aclobjid + " / " + addata[k].aclobjid);
    							if(acl.aclobjid == addata[k].aclobjid){
    								if(addata[k].acls == right.codevalue){
    									$acl.append(right.name);	
    								}
    								if (acl.acls == right.codevalue) {
            							$acl.append(" / (기본 권한 : " + right.name +")");	
            						}
    							}else{
    								if (acl.acls == right.codevalue) {
    									$acl.append("(기본 권한 : " + right.name +")");
            						}
    							}
    						}
    					}
    				}

    				$tr.data('meta', null);
    				$tr.append(inHtml).append($acl);
    				
    				$("#defaultAclList").append($tr);
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

	var addInitAcl = function() {
		//추가권한 변경된 권한

		if (objectIsEmpty(docData))
			return;
		var data = $.extend(true, [], docData.zappAcls);
		console.log("====docData : ", data);
		
		
		var acltrLen = $("#defaultAclList tr").length;
		console.log("acltrLen : " + acltrLen);
		
		
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
					$("#selRetention").empty();
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
					$("#selRetention").append(option);
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
		if(data.summary == null){
			$("#beforeDesc").text("");	
		}else{
			$("#beforeDesc").text(data.summary);
		}
		
		console.log("data.zappAcl.acls : " + data.zappAcl.acls);
		//첨부파일 표시
		setServerFile(data.zappFiles, data.zappAcl.acls);

		//설정 분류 표시
		setServerCls(data.zappClassifications);
		//반출정보 표시
		setLockInfo(data.zappLockedObject);
		//보존기간 설정
		var retentionid = data.retentionid;
		retentionList(retentionid);
		
		setDocType(data.zappClassifications);
		//키워드정보 표시
		//setKeywords(data.zappKeywords);
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
		
		console.log("getDocInfo selectedContentid : " + selectedContentid);
		
		var item = $("#" + selectedContentid).data("meta");
		
		console.log("item : " , item);

		//item.islocked  -- YS :내가 잠금, Y : 잠금  N : 안잠긴 파일
		if (item) {
			var data = {};
			data.objIsTest = "N";
			data.objTaskid = taskid;
			data.objType = item.contenttype;//01:bundle,02:file
			data.objViewtype = "01";//01:조회용,02:편집용
			data.contentid = selectedContentid;
			console.log("getDocInfo param: " + JSON.stringify(data));
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
						
						var ClsName = docData.classname;
						console.log("ClsName : " + ClsName);
						
						var ClsId = ClsName.split("：");
						console.log("ClsId : ", ClsId);
						
						docClsId = ClsId[0];
						
						
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
	
	var DocClassId= ""; // 폴더소속 폴더ID
	$(document).ready(function() {
		//문서 기본정보 조회
		getDocInfo();
		
		console.log("fileinfo selectedClsid : " + selectedClsid);
		
		if(selectedClsid == "NP14"){
			$("#fileInfo").css("height", 65)
			$("#fileInfo").css("border","1px solid #b4b4b4");
			$("#fileInfo").css("border-radius","5px !important");
			
			
			$("#appokrtn").css("display", "inline !important");
			$("#Aprov").css("display","block");
			$("#AprovText").css("display","block");
			$("#AprovOk").css("display","block");
			$("#AprovRtn").css("display","block");
		}else{
			$("#appokrtn").css("display", "none");
			$("#fileInfo").css("border","1px solid #b4b4b4");
			$("#fileInfo").css("border-radius","5px !important");
		}
		
		//retentionList();
		fn_DocReg_Tree.jstree();
		fn_DocRegGroup_Tree.jstree();
		fn_DocRegCls_Tree.jstree();
		console.log("==== fileInfo selectedClsType : " + selectedClsType);
		if (selectedClsType == "N1" || selectedClsType == "N3") {
			$("#Reg_Tab2").hide();
		} else {
			defaultAcl();
		}

		$("#cont01 input[type=text]").attr("disabled", true);
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
	});
	
	
	var Aprov = function(AprovType){
		var AprovType = AprovType;
		
		var item = $("#" + selectedContentid).data("meta");
		console.log("item : ", item);
		
		var reason = $.trim($("#reasonText").val());
		var data = {};
			data.objIsTest = "N";
			data.objDebugged = false;
			data.zappLockedObject = {"reason" : reason}; //승인사유
		
		console.log("data : ", data);
			
		if(AprovType == "APPROVAL"){
			reasonDoc(item.contentid, data, "approve");
			docRegClose();
			fn_Job_Tree.clickNode(selectedClsid);
		}else if(AprovType == "RTN"){
			reasonDoc(item.contentid, data, "return");
			docRegClose();
			fn_Job_Tree.clickNode(selectedClsid);
		}
		
	}
</script>
</head>
<body>
	<!-- 팝업 -->
	<div class="popup" style="display: block;">
		<h3 class="pageTit">문서정보</h3>
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
					<select class="docDate" id="selRetention" disabled="disabled">
					</select>
					<input type ="date" max="9999-12-31" id = "expireDate" style="height: 32px; visibility: hidden;"/>
					<p>문서 유형</p>
					<select class="docType" id="regDocType" disabled="disabled">
					</select>
					<p>키워드</p>
					<input type="text" class="docDes" id="beforeDesc"
						placeholder="해쉬태그 포함">
				</div>
				<h3 class="innerTit">파일 정보</h3>
				<div class="btn_wrap" id = "fileBtns">
                </div>
				<div>
					<table class="fileList">
						<colgroup>
							<col width="8%">
							<col width="73%">
							<col width="20%">
						</colgroup>
						<thead>
							<th>순번</th>
							<th>파일명</th>
							<th>용량</th>
						</thead>
						<tbody id="fileInfo">
						</tbody>
					</table>
				</div>
				<h3 class="innerTit" id ="appokrtn">승인 / 반려</h3>
				<div id="AprovText" style="height: 35px; display: none;">
					<textarea style="resize: none;border: none; width: 100%; height: 100%; padding:6px;" id="reasonText" onkeyup="pubByteCheckTextarea(event,100)"></textarea>
				</div>
				<input type="button" style="float:left; display: none; margin-left: 35%;" class="btbase" id ="AprovOk" onclick="javascript:Aprov('APPROVAL');" value="<spring:eval expression="@${lang}['APPROVAL']"/>">
				<input type="button" style="display: none; margin-left:45%;" class="btbase" id ="AprovRtn" onclick="javascript:Aprov('RTN');" value="<spring:eval expression="@${lang}['RETURN']"/>">
			</div>
			<!--cont01//-->
			<div class="contdiv" id="cont02">
				<h3 class="innerTit">문서 권한</h3>
				<div>
					<table class="pop_tbl">
						<colgroup>
                            <col width="9%">
                            <col width="15.75%">
                            <col width="15.75%">
                            <col width="22.75%">
                            <col width="32.75%">
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
	</div>
</body>
</html>