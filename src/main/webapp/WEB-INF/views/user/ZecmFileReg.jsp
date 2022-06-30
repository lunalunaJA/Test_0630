<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${js}/common.js"></script>
<script type="text/javascript">
	var fileData = {};
	var addFileNO = 0;
	var fileList = [];
	var dontDouble = false;
	var sendFileInfo = {};

	var DocTypeList;
	
	var fileHandle = function(files) {
		var overSizeCnt = 0;
		
		for (var i = 0; i < files.length; i++) {
			if (dupChk(files[i].name)) {
				alert("<spring:eval expression="@${msgLang}['CANNOT_ADD_SAME_FILE_NAME']" />" + "\n" + files[i].name);
			} else {
				if(files[i].type != ""){//폴더를 선택한 경우 제외하기 위해 추가함
					var ext = files[i].name.substring(files[i].name.lastIndexOf(".")+1);
					if(extFileSize.hasOwnProperty(ext)){
						if(files[i].size < extFileSize[ext]){
							if (i == 0) {
								if ($("#title").val() == "")
									$("#title").val(files[i].name);
							}
							fileData['addfile' + addFileNO] = files[i];
							addFileNO++;	
						}else{
							overSizeCnt++;
						}
					}else{
						alert("등록된 확장자가 아니면 저장 할 수 없습니다.");
					}
				}else if(files[i].type == "" && files[i].size != 0){
					if(!extFileSize.hasOwnProperty(ext)){
						alert("등록된 확장자가 아니면 저장 할 수 없습니다.");
					}
				}
			}
		}
		
		if(overSizeCnt != 0){
			alert("첨부하신 파일 사이즈는 등록 할 수 없습니다.");
		}
		
		var inHtml = "";
		var fileCnt = "";
		var sizeStr = "";
		$("#fileInfo").empty();
		console.log("fileData : ", fileData);
		if(addFileNO > 1){
			console.log("readonly 삭제");
			$("#title").prop('readonly',false);
		}else if(addFileNO == 1 || addFileNO == 0){
			console.log("readonly 추가");
			$("#title").prop('readonly', true);
		}
		
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
				inHtml += "<tr style='height:30px;'><td style='width:30px;'><input type='checkbox' name='chkAddFile' id='chkAddFile"+fileKey+"' value='"+fileKey+"'><label for='chkAddFile"+fileKey+"'><label></td>";
				inHtml += "<td class='tdStyle' align='left' title='"+fileObj.name+"'>" + fileObj.name + "</td>";
				inHtml += "<td style='width:70px;padding-left:5px;'>" + sizeStr + "</td>";
				inHtml += "</tr>";
				fileCnt++;
			}
		}
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

	var js_addFileDel = function() {
		$("input[name='chkAddFile']:checked").each(function() {
			var fileKey = $(this).val();
			delete fileData[fileKey];
			addFileNO--;
			$(this).parent().parent('tr').remove();
			if(addFileNO == 1 || addFileNO == 0){
				console.log("readonly 추가");
				$("#title").prop('readonly', true);
			}
		});

	}

	if (!String.prototype.startsWith) {
		String.prototype.startsWith = function(search, pos) {
			return this.substr(!pos || pos < 0 ? 0 : +pos, search.length) === search;
		};
	}

	var icon = "${ctxRoot}/resources/images/jstree/tree_user_icon15.png";
	var userIcon = "${ctxRoot}/resources/images/jstree/tree_user_icon03.png";

	//ClsTree Start
	/* var clsRoot = {id : companyid, parent : "#", icon : POPTREE["CLS_ROOT"], text : "<spring:eval expression="@${lang}['CLASSIFICATION']"/>", a_attr : {type : "02", class : "no_checkbox"}};
	var clsRootNode = [ clsRoot ]; */
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
				icon :  POPTREE["CLS_ROOT"],
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
					"default" : {"icon" : "glyphicon glyphicon-flash"},
					file : {icon : "fa fa-file text-inverse fa-lg"}
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
				fn_DocRegCls_Tree.getClsList(fn_DocRegCls_Tree.root.id);
				fn_DocRegCls_Tree.openNode(fn_DocRegCls_Tree.root.id);
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
						console.log("upid : "+obj.upid+" / child : ", child);
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
			inHtml += "<td style='height: 26px; text-align:left; padding-left:5px;'><input type='checkbox' disabled id=''><label></label></td>";
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

			/*  inHtml += "<td align='center'><input type='checkbox' name='chkAddAcl' id='chkAddAcl"+acl.aclobjid+"' value='"+acl.aclobjid+"'></td>"; */
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

	//multipart file send
	var fileSend = function() {

		var fileExist = false;
		var formData = new FormData();
		for ( var key in fileData) {
			console.log("=====fileData : " + key);
			console.log("=====fileData : ", fileData[key]);
			var fileKey = key;
			var fileObj = fileData[key];
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
										console.log("====sendFilesInfo : ", sendFilesInfo);
										fileReg(sendFilesInfo);
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
					//  if (item.objAction == "ADD"){
					// delete item.aclobjid;
					//} 
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
			$("#expireDate").show();
		} else {
			$("#expireDate").hide();
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

	//파일전송후 ECM 등록 API 호출
	var fileReg = function(fileInfo) {
		var mainMeta = {};
		//문서번호
		mainMeta.bno = $("#docNo").val();
		//문서제목
		mainMeta.title = $("#title").val();
		mainMeta.btype = "01"; // 01:Normal, 02:Virutal
		
		//선택된 트리 정보
		var additoryMeta = {};
		additoryMeta.summary = $.trim($("#beforeDesc").val()); // 문서에대한 설명
		
		var classObject = {};
		classObject.classid = selectNodeId(selectedClsid);
		classObject.classtype = selectedClsType;
		var classObjects = settedClsList();//문서분류 정보
		classObjects.push(classObject);//선택문서함 정보 추가
		
		// 문서유형 지정
		classObject = {};
		var docType = $("#regDocType option:selected").val();
		classObject.classid = docType;
		classObject.classtype = "03";
		classObjects.push(classObject);//문서유형 추가
		
		//보존년한
		var retention = $("#selRetention option:selected").val();
		//키워드 
		var newDesc = $("#beforeDesc").val();
		var data = {};
		data.objIsTest = "N";
		data.objTaskid = taskid;
		data.zappClassObjects = classObjects;
		//문서 설정 권한
		if (selectedClsType == "N3") {
			data.zappAcls = [];
		} else {
			data.zappAcls = getAddAclList();
		}

		var objType = "02";
		//첨부 파일이 하나 이상이면 번들로 등록한다.
		//@TODO 사이트에서는 둘중 하나를 선택해서 개발
		if (fileInfo.length > 1) {
			objType = "01";
		}
		data.objType = objType; //bundle:01, file:02	
		if (objType == "01") {
			data.zappBundle = mainMeta;
			//첨부 파일 정보
			data.zappFiles = fileInfo;
			data.zappAdditoryBundle = additoryMeta;//추가 정보
		} else {
			fileInfo[0].fno = $("#docNo").val();
			fileInfo[0].summary = $("#beforeDesc").val();
			data.zappFile = fileInfo[0];
			data.zappFile.creatorname = username;
		}
		
		if (retention == "0") {
			var expireDate = $("#expireDate").val();
			console.log("====expireDate : ", expireDate);
			if (objType == "01") {
				data.zappBundle.expiretime = expireDate;
			} else {
				data.zappFile.expiretime = expireDate;
			}
		} else {
			data.objRetention = retention;
		}

		if(!objectIsEmpty(newDesc)){
			//값이 있는 경우
			var words = newDesc.split(" ");// 띄어쓰기로 검색할 단어 배열화
			var words = words.filter((element, index) => {
				return words.indexOf(element) === index;
			});//중복된 배열값 제거
		if (words && words.length > 0) {
			var keyObjs = [];
			for ( var k in words) {
				if(words[k].startsWith('#')){//해시태그로 시작되는 단어 키워드 오브젝트에 추가
					if(words[k].indexOf(' ') !== -1 || words[k].replace("#","") == ""){
						continue;
					}else{
						keyObjs.push({"kword" : words[k].replace("#","")});
					}
				}
			}
			console.log("keyObjs=============",keyObjs);
			data.zappKeywords = keyObjs;
		}
		}else{
			console.log("no keyword");
		}
		data.title = $("#title").val();
		console.log("===regdata===", data);
		sendFileInfo = $.extend(true, {}, data);
		//파일인 경우에는 파일명 중복 체크
		if (objType == "02") {
			$.ajax({
				url : "${ctxRoot}/api/content/checkfilename",
				type : "POST",
				dataType : 'json',
				contentType : 'application/json',
				async : false,
				data : JSON.stringify(data),
				success : function(data) {
					console.log("====add : ", data);
					if (data.status == "0000") {
						sendFileInfo.addmode = data.result;
						if (data.result == "01") {
							addContent(sendFileInfo);
						} else if (data.result == "02") {
							showDuplicateFile("02")
						} else if (data.result == "03") {
							showDuplicateFile("03")
						} else if (data.result == "04") {
							showDuplicateFile("04")
						}
					} else {
						alertErr(data.message);
					}
				},
				error : function(request, status, error) {
					alertNoty(request, status, error, "<spring:eval expression="@${msgLang}['DOCUMENT_REGI_FAILED']" />");
				},
				beforeSend : function() {
				},
				complete : function() {
					$();
				}
			});
		} else {
			addContent(data);
		}
	}
	
	var addContent = function(data) {
		console.log("====addContent  : ", data);

		$.ajax({
			url : "${ctxRoot}/api/content/add",
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(data),
			success : function(retData) {
				console.log("==== add receive : ", retData);
				if (retData.status == "0000") {
					alert("<spring:eval expression="@${msgLang}['REG_DOC_SUCCEEDED']" />");
					sendFileInfo = {};
					$("#adSearchOK").hide();
					listSearch();
					$('.popup').fadeOut();
				} else {
					alert(retData.message);
				}
			},
			error : function(request, status, error) {
				alertNoty(request, status, error,
						"<spring:eval expression="@${msgLang}['DOCUMENT_REGI_FAILED']" />");
			},
			beforeSend : function() {
			},
			complete : function() {
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
			fn_DocRegCls_Tree.jstree();
			console.log("===USERTYPE : " + selectedClsType);
			if (selectedClsType == "N3") {
				$("#Reg_Tab2").hide();
			} else {
				defaultAcl(defaultDocAclArr);
				listdefaultAcl(defaultDocAclArr);
			}

			//dragAndDrop
			var objDragAndDrop = $(".fileListDiv");

			$(document).on("dragenter", ".fileListDiv", function(e) {
				e.stopPropagation();
				e.preventDefault();
				$(this).css('border', '1px solid #0B85A1');
			});

			$(document).on("dragover", ".fileListDiv", function(e) {
				e.stopPropagation();
				e.preventDefault();
			});

			//2번 호출되어 setTimeout을 이용해서 처리
			$(document).on("drop", ".fileListDiv", function(e) {
				$(this).css('border', '1px dotted #0B85A1');
				e.stopPropagation();
				e.preventDefault();
				if (dontDouble) {
					return;
				}
				dontDouble = true;

				var files = e.originalEvent.dataTransfer.files;
				for (var i = 0; i < files.length; i++) {
					var filename = files[i].name;
					var filesize = files[i].size;
				}
				fileHandle(files);

				setTimeout(function() {
					dontDouble = false;
				}, 1000);
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
			
			$('#beforeDesc').on('input keyup', converter);
	    	$('div[contenteditable]').keydown(function(e) {
	     		 if (e.keyCode === 13) {
	        	 	return false;
	      		 }
	     		 $(this).focus();
	    	});
	    	$('#beforeDesc').click(function(){
	    		console.log("===beforeDesc===");
	    		setTimeout(function(){
	    			$('#beforeDesc').focus();
	    		}, 0);
	    	});
	    	
	    	$('#allchk2').unbind("click").bind("click", function(){
    	        if($('#allchk2').prop("checked")){
    	            $('.popup .tabCont #cont01 div .fileList input[type=checkbox]').prop('checked',true);
    	            $('.popup .tabCont #cont01 div .fileList tbody input[type=checkbox]').closest('tr').css('background-color','#fff6de');
    	        } else {
    	            $('.popup .tabCont #cont01 div .fileList input[type=checkbox]').prop('checked',false);
    	            $('.popup .tabCont #cont01 div .fileList tbody input[type=checkbox]').closest('tr').css('background-color','inherit');
    	        }
    	    });
	    	
	    	// 팝업 닫기
	  		$('#closeBtn').unbind("click").bind("click", function(){
	  			$('.bg').fadeOut();
	  	        $('.popup').fadeOut();
	  	    });
		});
</script>
<title>ECM4.0 :: 문서등록</title>
</head>
<body>
	<!-- 팝업 -->
	<div class="popup" style="display: block;">
		<h3 class="pageTit">문서등록</h3>
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
					<p>키워드</p>
					<input type="text" class="docDes" id="beforeDesc"
						placeholder="해쉬태그 포함">
				</div>
				<h3 class="innerTit">파일 정보</h3>
				<div class="btn_wrap">
					<button type="button" class="btbase" id="addFile">파일 추가</button>
					<button type="button" class="btbase" id="delFile">파일 삭제</button>
				</div>
				<div class="fileListDiv">
					<table class="fileList">
						<colgroup>
							<col width="8%">
							<col width="73%">
							<col width="20%">
						</colgroup>
						<thead>
							<th><input type="checkbox" name="selectAll02" id="allchk2"><label for="allchk2"></label></th>
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