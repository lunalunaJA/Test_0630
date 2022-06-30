<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp"%>

<!DOCTYPE html>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" content="IE=edge">
<title>NADi4.0 :: 메인</title>

<link type="text/css" rel="stylesheet" href="${css}/reset.css">
<link type="text/css" rel="stylesheet" href="${css}/style.css">
<link type="text/css" rel="stylesheet" href="${css}/common.css" />
<link type="text/css" rel="stylesheet" href="${css}/jstree/style.css" />
<link type="text/css" rel="stylesheet" href="${js}/jquery-ui-1.13.1/jquery-ui.min.css" />
<link type="text/css" rel="stylesheet" href="${css}/jquery.contextMenu.css?15" />

<script type="text/javascript" src="${js}/jquery-1.12.2.min.js"></script>
<script type="text/javascript" src="${js}/jquery-ui-1.13.1/jquery-ui.min.js"></script>
<script type="text/javascript" src="${js}/jstree.js"></script>
<script type="text/javascript" src="${js}/common.js"></script>
<script type="text/javascript" src="${js}/jquery.noty.packaged.min.js"></script>
<script type="text/javascript" src="${js}/jquery.contextMenu.min.js"></script>

<script type="text/javascript">
	//문서 상태
	var docInfo ={};
	docInfo.no = "<spring:eval expression="@${lang}['DOC_NO']"/>";
	docInfo.authority = "<spring:eval expression="@${lang}['DOC_AUTHORITY']"/>";
	docInfo.title = "<spring:eval expression="@${lang}['DOC_TITLE']"/>";
	docInfo.folderName = "<spring:eval expression="@${lang}['FOLDER_NAME']"/>";
	docInfo.register = "<spring:eval expression="@${lang}['OWNER']"/>";
	docInfo.time = "<spring:eval expression="@${lang}['LAST_MODIFY_TIME']"/>";
	docInfo.expired = "<spring:eval expression="@${lang}['EXPIRED_DATE']"/>";
	docInfo.status = "<spring:eval expression="@${lang}['STATE']"/>";
	docInfo.reason = "<spring:eval expression="@${lang}['REASON']"/>";
	docInfo.reject = "<spring:eval expression="@${lang}['REJECT']"/>";
	docInfo.approvalTime= "<spring:eval expression="@${lang}['APPROVAL_TIME']"/>";
	docInfo.rejectTime= "<spring:eval expression="@${lang}['REJECT_TIME']"/>";
	docInfo.appAuthority= "<spring:eval expression="@${lang}['APPROVER']"/>";
	
	//문서함
	var docBox ={};
	docBox.enterprise="<spring:eval expression="@${lang}['COMPANY_FOLDER_BOX']"/>"
	docBox.department="<spring:eval expression="@${lang}['DEPARTMENT_FOLDER_BOX']"/>"
	docBox.collaborative="<spring:eval expression="@${lang}['COLLABORATIVE_FOLDER_BOX']"/>"
	docBox.personal="<spring:eval expression="@${lang}['PERSONAL_FOLDER_BOX']"/>"
	docBox.classification="<spring:eval expression="@${lang}['DOC_CLASSIFICATION']"/>"
	docBox.general="<spring:eval expression="@${lang}['GENERAL_DOCUMENT_BOX']"/>"
	
	
	$(function () {
	 	 
	 	initAclList();

		var contextMove = true;
		var contextCopy = true;
		var contextShare = true;
		var contextUnShare = false;
		var contextDele = true;
		var contextUnDele = false;
		var contextFav = true;
		var contextUnFav = false;
		var contextUnFavFld = false;
		var contextView = true;
		var contextCmt = true;
	    $.contextMenu({	
	        // 선택자 어떤 dom의 요소를 선택할 것인가?	
	        selector: '#bundleList > tr',	
	        // 클래스 접근 방법	
	        // selector: '.class_context_menu', 	
	        // 어느 위치에 우클릭 menu를 생성할 것인가? , 없을 경우 클릭한 위치에서 생성	
	        //appendTo: '.contextmenu',	
	        // 마우스가 생성하는 dom 위치에 있을 때, accesskey 누른 경우 메뉴 화면 생성	
	        accesskey: 'a', 	
	        callback: function (key, options) {	
	            // 메뉴 아이템을 클릭한 경우, callback 이벤트 동작
	            //console.log("=== contextMenu callback key:" + key + " on element id " + options.$trigger.attr("id") + " on element name " + options.$trigger.attr("name"));

	            if (key == "MOVE"){	            	
	            	moveDocPop(options.$trigger.attr("id"));
	            } else if (key == "COPY"){	            	
	            	copyDocPop(options.$trigger.attr("id"));
	            } else if (key == "SHARE"){
	            	docSharePop(options.$trigger.attr("id"));
	            } else if (key == "UNSHARE"){
	            	unShareDoc(options.$trigger.attr("id"));
	            	listSearch();
	            } else if (key == "FAV"){
	            	favDoc(options.$trigger.attr("id"));
	            } else if (key == "UNFAV"){
	            	unfavDoc(options.$trigger.attr("id"));
	            } else if (key == "UNFAVFLD"){
	            	unfavFld(options.$trigger.attr("id"));
	            } else if (key == "DEL"){
	            	DelContextSelect(options.$trigger.attr("id"))
	            	//delDoc(options.$trigger.attr("id"));
	            	//페이지 갱신
	            } else if (key == "UNDEL"){
	            	cancelDelDoc(options.$trigger.attr("id"));
	            	//페이지 갱신
	            } else if (key == "VIEW") {
	            	docInfoOpen(options.$trigger.attr("id"));
	            }else if  (key == "COMMENT") {
	            	CmtRegOpen(options.$trigger.attr("id"));
	            }
	            // return false; // 리턴값이 false인 경우, 메뉴가 사라지지 않음 	
	        },	
	        // 메뉴 생성 트리거 요소 [right , left , hover, touchstart, none]	
	        trigger: 'right',	
	        items: {
	            "VIEW": { name: "<spring:eval expression="@${lang}['DOC_INFO']"/>", icon: "view",visible: function(){
                	return contextView;
                }},
	            "SHARE": { name: "<spring:eval expression="@${lang}['SHARE_DOC']"/>", icon: "share",visible: function(){
                	return contextShare;
                }},
	            "UNSHARE": { name: "<spring:eval expression="@${lang}['RELEASE_SHARING']"/>", icon: "share",visible: function(){
                	return contextUnShare;
                }},	
                "FAV": { name: "<spring:eval expression="@${lang}['FAVORITE']"/>", icon: "fvt",visible: function(){
                	return contextFav;
                }},
                "UNFAV": { name: "<spring:eval expression="@${lang}['UN-FAVORITE']"/>", icon: "fvt",visible: function(){
                	return contextUnFav;
                }},
                //contextUnFavFld
                "UNFAVFLD": { name: "<spring:eval expression="@${lang}['UN-FAVORITE']"/>", icon: "fvt",visible: function(){
                	return contextUnFavFld;
                }},
	            "DEL": { name: "<spring:eval expression="@${lang}['DELETE_DOC']"/>", icon: "delete",visible: function(){
                	return contextDele;
                }},
                "UNDEL": { name: "<spring:eval expression="@${lang}['RECOVER_DOC']"/>", icon: "delete",visible: function(){
                	return contextUnDele;
                }},
                "COMMENT": { name: "<spring:eval expression="@${lang}['DOC_COMMENT']"/>", icon: "cmt",visible: function(){
                	return contextCmt;
                }},
	        },	
	        events: {	
	            show: function (options) {	
	                // S.fn.init [div#id_context_menu] 메뉴화면 	                
	                // 메뉴 생성 시 클래스 추가 	
	                this.addClass('currently-showing-menu');
	                var data = $("#"+options.$trigger.attr("id")).data();
					console.log("selectedClsid : " + selectedClsid);
					var acls = 0;
					var islocked = data.islocked;
					var show = true;
					try{
						acls = data.meta.zappAcl.acls;
					}catch(e){acls = 0 }
	                //문서권한에 따라 보여줄 메뉴를 설정한다.
	               
	                console.log("=== selectedClsid: " + selectedClsid + ", acls:" + acls);
	                
	                if (acls<2){
	                	show = false;//컨텍스트 메뉴를 보여주지 않는다.
	                	
	                	if(acls == 0){
	                		if(selectedClsid == "sharedby"){
	                			contextMove = false;
	     		                contextCopy = false;
	     		                contextShare = false;
	     		                contextUnShare = true;
	     		                contextDele = false;
	     		                contextFav = false;
	     	                	contextUnDele = false;
	     	                	contextCmt = false;
	     	                	show = true;
	                		}
	                	}
	                	
	                	if(selectedClsid == "faveriteFld"){
	                		contextMove = false;
	 		                contextCopy = false;
	 		                contextShare = false;
	 		                contextDele = false;
		                	contextFav = false;
		                	contextUnFav = false;
		                	contextUnFavFld = true;
		                	contextView = false;
		                	contextCmt = false;
		                	show = true;
		                }
	                } else if (acls == 2){
		                contextMove = false;
		                contextCopy = false;
		                contextShare = false;
		                contextDele = false;
		                contextFav = true;
	                } else if(acls == 4){
	                	contextDele = false;
	                	contextShare = false;
	                } else if(acls == 5){
	                	if(selectedClsid == "sharedby"){
		                	contextMove = false;
	 		                contextCopy = false;
	 		                contextShare = false;
	 		                contextUnShare = true;
	 		                contextDele = false;
	 		                contextFav = false;
	 	                	contextUnDele = false;
	 	                	contextCmt = false;
	                	}
	                }
	                
	                if(selectedClsid == "lock"){
                    	contextMove = false;
 		                contextCopy = false;
 		                contextShare = false;
 		                contextUnShare = false;
 		                contextDele = false;
 		                contextFav = false;
 	                	contextUnDele = false;
 	                	contextCmt = true;
                	}
	                
	                if (selectedClsid == "faveriteDoc"){
	                	contextMove = false;
 		                contextCopy = false;
 		                contextShare = false;
 		                contextDele = false;
	                	contextFav = false;
	                	contextFav = false;
	                	contextUnFav = true;
	                }
	                if (selectedClsid == "TRASH"){
		                contextMove = false;
		                contextCopy = false;
		                contextShare = false;
		                contextDele = false;
		                contextFav = false;
	                	contextUnDele = true;
	                	contextCmt = false;
	                }
	                if (selectedClsid == "shared" || selectedClsid == "sharedby"){
                		contextMove = false;
 		                contextCopy = false;
 		                contextShare = false;
 		                contextUnShare = true;
 		                contextDele = false;
 		                contextFav = false;
 	                	contextUnDele = false;
 	                	contextCmt = false;
	                }
	                //NP11:승인요청 문서, NP12:승인완료 문서, NP13:승인반려 문서, NP14:내가 승인할 문서
	                if (selectedClsid == "NP11" || selectedClsid == "NP12" || selectedClsid == "NP13" || selectedClsid == "NP14"){
	                  	contextMove = false;
		                contextCopy = false;
		                contextShare = false;
		                contextUnShare = false;
		                contextDele = false;
		                contextFav = false;
	                	contextUnDele = false;
	                	contextView = true;
	                	contextCmt = false;
	                }
	                return show;	
	            },	
	            hide: function (options) {	
	                // 메뉴 화면이 사라질떄
	                contextMove = true;
	                contextCopy = true;
	                contextShare = true;
	                contextUnShare = false;
	                contextDele = true;
	                contextUnDele = false;
	                contextFav = true;
	                contextUnFav = false;
	                contextUnFavFld = false;
	                return true;	
	            },	
	            activated: function (options) {	
	                // 메뉴화면이 활성화 되었을 때 	
	                return false;	
	            }	
	        }	
	    });

	    createDocHeader();
	});
	
	var extFileSize=[];
	var treeTargetId ="";
	var modalZIndex = 100;
	var infoOpen = true;
	var selectedClsid = "";
	var selectedClsType = "";
	var selectedContentid = "";
	var selectedUpid ="";
	var selectReasonType ="";
	var searchparam = {};
  	//문서 등록시 세팅되는 기본 문서 권한
 	var defaultDocAclArr = [];
 	var folderAttr = {};
 	
	//Tree Start
	if (!String.prototype.startsWith) {
		String.prototype.startsWith = function(search, pos) {
			return this.substr(!pos || pos < 0 ? 0 : +pos, search.length) === search;
		};
	}
	var MyDept="M:";
	var fn_fldTree = { 
		consoleLog : function(data) {                
		    $.each(data, function(i,v) {
		         //console.log(i + "===>>" + v); 
		    });               
		},
		treeId : "fldjstree",
		$tree : {},
		root : {
			id : companyid,//"N4",//companyid
			type : "N1"//"N4"//N1
		},
		
		initData : function(){	  
			var rootData = [];
	        rootData.push({ id : companyid ,parent:"#",icon : TREEICONS["COMPANY"], text : docBox.enterprise ,a_attr : {type:CLSTYPES["COMPANY"],nsearch:true,objType:"01"}});
	        rootData.push({ id : MyDept+deptid ,parent:"#",icon : TREEICONS["DEPT"], text : deptname ,a_attr : {type:CLSTYPES["DEPT"],isactive:"Y",objType:"01",objAction:"N", upid:companyid}});
	        rootData.push({ id : "N2" ,parent:"#",icon : TREEICONS["DEPTGROUP"], text : docBox.department ,a_attr : {type:CLSTYPES["DEPT"],nsearch:true,objType:"01"}});
	        rootData.push({ id : "N4" ,parent:"#",icon : TREEICONS["COLLABOGROUP"], text : docBox.collaborative ,a_attr : {type:CLSTYPES["COLLABO"],nsearch:true,objType:"01"}});//collabo
	        rootData.push({ id : userid ,parent:"#",icon : TREEICONS["USER"], text : docBox.personal ,a_attr : {type:CLSTYPES["USER"],nsearch:true,objType:"01"}});//my 
	      	return rootData;
		},

		bdata : [],
		adata : [],
		currentNodeId : "",
		jstree : function() {
			var tree = this;
			tree.$tree = $("#"+tree.treeId);			
			tree.$tree.jstree({ 
					core: { 
						data: tree.initData(), /* 최초에 보여지 최상위 Root Tree */
						check_callback: true,
						animation : 0
					},
					themes: {
						"theme" : "default",//classic
						"dots" : false,
						"icons" : true 
					},
					types: { 
						"default" : {"icon" : "glyphicon glyphicon-flash"},
						file: {icon: "fa fa-file text-inverse fa-lg"},
						search: {
		                    "case_sensitive": false,
		                    "show_only_matches": true,
		                    "search_callback" : function(key,node){
		                    }
		                }
					},
				plugins : ["massload" ,"ui", "search", "unique", "types" ,  "changed" ,"hotkeys" ,"contextmenu","themes","conditionalselect"],
				contextmenu : {                						            							
				    "items": {
				    	"create" : {
							"separator_before"	: false,
							"separator_after"	: true,
							"_disabled"			: false,
							"label"				: "<spring:eval expression="@${lang}['ADD_FOLDER']"/>",//
							"action"			: function (data) {								
								var inst = $.jstree.reference(data.reference);
								obj = inst.get_node(data.reference);
								console.log("===obj : ",obj);
							 	var child ={ id : "newid" ,parent: obj.id,icon : TREEICONS["CREATE"], text : "<spring:eval expression="@${lang}['NEW_FOLDER']"/>" ,a_attr : {type:obj.a_attr.type,nsearch:true,objType:"01"}};
								fldCreate(obj);
							}
						},
						"rename" : {
							"separator_before"	: false,
							"separator_after"	: false,
							"_disabled"			: false, 
							"label"				: "<spring:eval expression="@${lang}['RENAME_FOLDER']"/>",//
							"action"			: function (data) {
								console.log("context rename");
								var inst = $.jstree.reference(data.reference),
								obj = inst.get_node(data.reference);
								obj.oldtext = obj.text;
								console.log("obj : ", obj);
								console.log("obj : "+ obj.a_attr.holderid);
								console.log("deptuserid : " + deptuserid);
								if(obj.a_attr.holderid == deptuserid){
									inst.edit(obj,obj.text);	
					        	}else{
					        		alert("폴더 생성자가 아니기 때문에 수정이 불가능 합니다.");
					        		$("fldjstree").jstree("edit", obj.nodeid, obj.oldtext);
					        		return;
					        	}
							}
						},
						"openFolder" : {
							"separator_before"	: false,
							"separator_after"	: false,
							"_disabled"			: false, 
							"label"				: "<spring:eval expression="@${lang}['OPEN']"/>",//
							"action"			: function (data) {								
								var inst = $.jstree.reference(data.reference),
								obj = inst.get_node(data.reference);
							   	expandNode(obj.id);							   
							   	function expandNode(nodeID) {
							        tree.$tree.jstree("open_node", nodeID);							       
							        var thisNode = tree.$tree.jstree("get_node", nodeID);							        
							        var children = tree.$tree.jstree("get_children_dom", thisNode);
							        $.each(children,function(i,child){
							        	expandNode(child.id);
							        });								    
								}					
							}
						},
						"closeFolder" : {
							"separator_before"	: false,
							"separator_after"	: false,
							"_disabled"			: false, 
							"label"				: "<spring:eval expression="@${lang}['CLOSE']"/>",//
							"action"			: function (data) {								
								var inst = $.jstree.reference(data.reference),
								obj = inst.get_node(data.reference);
								var nodeid = obj.id;
								tree.$tree.jstree("close_node", nodeid);				
							}
						},						
						"faverite" : {
							"separator_before"	: false,
							"separator_after"	: true,
							"_disabled"			: false, 
							"label"				: "<spring:eval expression="@${lang}['FAVORITE']"/>",//
							"action"			: function (data) {
							  	//console.log("===faverite : ",data);
								var inst = $.jstree.reference(data.reference),
								obj = inst.get_node(data.reference);
								var nodeid = obj.id;
								favFld(nodeid);								
							}
						},
						"disable" : {
							"separator_before"	: false,
							"separator_after"	: false,
							"_disabled"			: false, 
							"label"				: "<spring:eval expression="@${lang}['DELETE_FOLDER']"/>",
							"action"			: function (data) {
								var inst = $.jstree.reference(data.reference),
								obj = inst.get_node(data.reference);
								selectedUpid=obj.parent;
								noty({
								    layout : "center",
								    text : "<spring:eval expression="@${msgLang}['ARE_YOU_DISABLE_FOLDER']"/>",
								    buttons : [ {
								    	addClass : 'btbase',
								      	text : "Ok",
								      	onClick : function($noty) {
								        	$noty.close();
								        	fldDisable(inst,obj);     
								      	}
								    }, {
								      addClass : 'btbase',
								      text : "Cancel",
								      onClick : function($noty) {
								        $noty.close();
								      }
								    } ],
								    type : "information",
								    killer : true
								  });
							}
						},
						"enable" : {
							"separator_before"	: false,
							"separator_after"	: false,
							"_disabled"			: false,
							"label"				: "<spring:eval expression="@${lang}['RESTORE_FOLDER']"/>",//
							"action"			: function (data) {								
								var inst = $.jstree.reference(data.reference),
								obj = inst.get_node(data.reference);
								selectedUpid=obj.parent;
								noty({
								    layout : "center",
								    text : "<spring:eval expression="@${msgLang}['ARE_YOU_RESTORE_FOLDER']"/>",
								    buttons : [ {
								      	addClass : 'btbase',
								      	text : "Ok",
								      	onClick : function($noty) {
								        	$noty.close();
								        	fldRestore(inst,obj);
								      	}
								    }, {
								      addClass : 'btbase',
								      text : "Cancel",
								      onClick : function($noty) {
								        $noty.close();
								      }
								    } ],
								    type : "information",
								    killer : true
								  });
							}
						},
						"discard" : {
							"separator_before"	: false,
							"separator_after"	: false,
							"_disabled"			: false,
							"label"				: "<spring:eval expression="@${lang}['DISCARD_FOLDER']"/>",//
							"action"			: function (data) {								
								var inst = $.jstree.reference(data.reference),
								obj = inst.get_node(data.reference);
								selectedUpid=obj.parent;
									
								noty({
								    layout : "center",
								    text : "<spring:eval expression="@${msgLang}['ARE_YOU_DISCARD_FOLDER']"/>",
								    buttons : [ {
								      	addClass : 'btbase',
								      	text : "Ok",
								      	onClick : function($noty) {
								        	$noty.close();
								        	fldDiscard(inst,obj);
								      	}
								    }, {
								      addClass : 'btbase',
								      text : "Cancel",
								      onClick : function($noty) {
								        $noty.close();
								      }
								    } ],
								    type : "information",
								    killer : true
								  });	
							}
						}
                    }  				 
                }				
			})
			.on("select_node.jstree", function (event, data) { // 노드가 선택된 뒤 처리할 이벤트
			 	treeTargetId = event.target.id;
			   	closeAdSearch();
			   	
			   	$("#shareDocType").hide(); 		// 공유문서 종류
				$("#aprovType").hide();			// 승인문서 종류 
				$("#latelyType").hide();		// 최근 문서
				$("#fvtType").hide();			// 즐겨찾기 종류
				$("#SearchForm").show();		// 검색창
				$("#detailSearch").show();		// 상세검색
				$("#docRegistration").show();	// 문서등록
				$("#moreMenu").show();			// 더보기메뉴
				
			   	
			   	
			  	selectedClsid = data.node.id;
				selectedClsType = data.node.a_attr.type;
				
				
				
				
				if(selectedClsType != "N3"){
					
					if(selectedClsType == "N1"){
						$("#docRegistration").hide();
						$("#folderMenu").hide();
						$("#moreMenu").hide();
					}else{
						$("#folderMenu").show();		// 폴더메뉴
					}
				}else{
					$("#folderMenu").hide();		// 폴더메뉴	
				}
				
				selectedUpid = data.node.a_attr.upid;
				var nsearch = data.node.a_attr.nsearch;
				var logic = data.node.a_attr.logic;
				var level = data.node.parents.length; //폴더 뎁스 제어시 사용
				var isactive = data.node.a_attr.isactive;
				var acl = data.node.a_attr.acl;
				var acls = data.node.a_attr.acls;
				
				searchparam.objType = "";
				searchparam.objHandleType = "";
				searchparam.objType = data.node.a_attr.objType;
				if(data.node.a_attr.objHandleType) 
					searchparam.objHandleType = data.node.a_attr.objHandleType;
				if(selectedClsType.startsWith("N")){
					tree.getNodeList(data.node.id,data.node.a_attr.type); 
				}
				
				if(data.node.a_attr.objAction !=="N"){
					fn_fldTree.nodeInfo(); //기본권한 확인
				  	// 폴더 속성정보 표시
					var folderInfo = "";
					var folderInfo2 = "";
					if (acl == "1" || acls == "1") {
						folderInfo = "(조회가능, 등록불가)";
						folderInfo2 = "조회가능, 등록불가";
					} else if (acl == "2" || acls == "2") {
						folderInfo = "(조회가능, 등록가능)";
						folderInfo2 = "조회가능, 등록가능";
					} else {
						folderInfo = "";
						folderInfo2 = "접근불가";
					}
				  	// 폴더 속성정보 표시
					var html = "";
					html += "<ul>";
					html += "<li>";
					html += "<img src='${image}/icon/Group 43.png'>";
					html += "</li>";
					
					// 폼더명
					html += "<li>";
					html += "<h3><img src='${image}/icon/Path 51.png'>[폴더명]</h3>";
					html += "<p>"+data.node.text+"</p>";
					html += "</li>";
					
			    	//폴더 종류 / N1:전사함, N2:전체부서, N3:개인, N4:협업
					var folderType = "";
					if (data.node.a_attr.type == "N1")
						folderType = "전사문서함";
					else if (data.node.a_attr.type == "N2")
						folderType = "부서문서함";
					else if (data.node.a_attr.type == "N3")
						folderType = "개인문서함";
					else if (data.node.a_attr.type == "N4")
						folderType = "협업문서함";
					html += "<li>";
					html += "<h3><img src='${image}/icon/Path 51.png'>[폴더종류]</h3>";
					if (folderAttr.wfid == null || folderAttr.wfid == "") {
						html += "<p>" + folderType + "</p>";
					} else {
						html += "<p>" + folderType + "</p>";;							
					}
					html +="</li>";
					
					
					//폴더 권한
					html += "<li>";
					html += "<h3><img src='${image}/icon/Path 51.png'>[폴더권한]</h3>";
					html += "<p>" + folderInfo2 + "</p>";
					html += "</li>";
					
					// 소유자
					if (data.node.a_attr.type != "N3") {
						if(folderAttr.holderName != undefined){
							html += "<li>";
							html += "<h3><img src='${image}/icon/Path 51.png'>[소유자]</h3>";
							html += "<p>" +  folderAttr.holderName + " (" + folderAttr.holderDeptName + ")</p>";
							html += "</li>";
						}
					}
					html += "</ul>";
					
					$("#bundleInfo").empty().append(html);
				}
				
				allFldBtnHide();
				allBtnHide();	
				
				//기업관리인 경우에만 전사문서함 등록가능
				if (!nsearch){
				  	//부서 N
				  	if (data.node.a_attr.objAction =="N"){
				    	fn_fldTree.getUseNode(data);
				    	
						createDocEmptyList();
				    	
				  	} else {
				    	//N1:전사함, N2:전체부서, N3:개인, N4:협업
				    	if(data.node.a_attr.type == "N1"){
							if(userType == "03"){
								if(data.node.a_attr.isactive == 'Y'){
									$("#RegBtn").show();
								}else{
									$("#RegBtn").hide();	
								}
								$("#DocDelBtn").show();		
						 	}
							$("#ShareRegBtn").hide();
							$("#MoveRegBtn").hide();
							$("#CopyRegBtn").hide();
							$("#LinkRegBtn").hide();
						} else {
							fn_fldTree.getUseNode(data);
							if(data.node.a_attr.isactive == 'Y' && (data.node.a_attr.acl > 1 || data.node.a_attr.acls > 1)){
								$("#RegBtn").show();
								$("#MoveRegBtn").show();
								$("#CopyRegBtn").show();
								$("#DocDelBtn").show();	
								
								////$("#contents").show();
							}else{
								$("#RegBtn").hide();
								$("#MoveRegBtn").hide();
								$("#CopyRegBtn").hide();
								$("#DocDelBtn").hide();	
							}
							$("#ShareRegBtn").show();
							$("#LinkRegBtn").show();
						}
				   		
						$("#searchTitle").val('');
						$("#adSearchOK").hide();
					 	listSearch();
				  	}
					
					//해당 위치 스크롤 이동
					try{
						data.inst._fix_scroll(data.rslt.obj);
					}catch(e){
					  
					}
				}else{
					
					console.log("!nsearch false");
					
					createDocEmptyList();
				}			
			})
			.on('changed.jstree', function (e, data) {
				try{
					//폴더 네비게이션
					$("#SelectText").empty();
					$("#SelectText").append('<li><a href="#none"><img src="${image}/icon/Path 52.png" ></a></li>');
					var path = data.instance.get_path(data.node);
					var parentnodes = tree.getNodeIdPath(data.node.id);
					for(var i=0;i<path.length;i++){
						var $span = $("<li></li>");
						$span.text(path[i]);
						var nodeid = parentnodes[i];
						
						$span.data('meta', nodeid);
						$span.bind("click", fldNavigate);
						$span.hover(
					        function () { $(this).addClass('hover'); },
					        function () { $(this).removeClass('hover'); }
					    );
						
						$("#SelectText").append($span);
					}
				}catch(e){
				}
			})
			.on("after_open.jstree", function (e, data) {
			  	if(data.node.parent !="#" ){
			    	if(data.node.state.selected == false){
				   		selectedClsid = data.node.id;
						selectedClsType = data.node.a_attr.type;	
						selectedUpid = data.node.a_attr.upid;
						var nsearch = data.node.a_attr.nsearch;
						var logic = data.node.a_attr.logic;
						var level = data.node.parents.length; //폴더 뎁스 제어시 사용
						searchparam.objType = "";
						searchparam.objHandleType = "";
						searchparam.objType = data.node.a_attr.objType;
						if(data.node.a_attr.objHandleType) 
							searchparam.objHandleType = data.node.a_attr.objHandleType;
						
						if(selectedClsType.startsWith("N")){
							  tree.getNodeList(data.node.id,data.node.a_attr.type); 
						}
						try{
							data.inst._fix_scroll(data.rslt.obj);
						}catch(e){}
			    	}
			  	}
			})
			.on("loaded.jstree", function (e,data) {
				$.each(tree.initData(), function(i, item){
					if(item.id == MyDept+deptid){
				  	    tree.getMyRootNodeList(item.id,item.a_attr.type);	
				  	}else{
				  	    tree.getRootNodeList(item.id,item.a_attr.type);		
				  	}
				  	//tree.openNode(item.id);
				});
	        })
	        .on("search.jstree", function (nodes, str) { 
	            if (str.res.length >0) {	
		        	//tree.openAll();	        		
		        	var data = str.res;
		        	var nodeid = data.shift();
		        	tree.bdata.push(nodeid);
		        	tree.adata = data;
		        	tree.clickNode(nodeid);        		
		        }
	        })
	        .on("rename_node.jstree", function (e, data) {
	        	console.log("====rename_node : ",data);
	        	fldNameEdit(data.node);	
	        	//console.log("data.node : ", data.node);
	        })	  	        
	        .on("show_contextmenu.jstree", function (e, data) {
	           	//특정 폴더에서만 컨텍스트 메뉴가 나오도록(개인 문서함)
		        //addfolder(0),separator(1), rename(2), open(3), close(4),
		    	//Favorate(5),separator(6), delete(7), restore(8), discard(9),
		    	//change(10),separator(11)
	          	var menuList =[3,4,5];// open, close, favorite
			
	           	if(data.node.a_attr.type !== "N3") {
	           		console.log("show context not N3");
	           		if(data.node.a_attr.isactive == 'Y'){
	           			var $li6 = $('.vakata-context').find('li:eq(-2)');
    	    		   	$li6.remove();
    	               	var $li7 = $('.vakata-context').find('li:eq(-1)');
      		           	$li7.remove();
	           		}else{
	           			$('.vakata-context li').each(function( index ) {
		    		       	if(index >= 8){  //폴더 삭제
		    		       		return;
			              	}
		    		       	$(this).remove();
				         }); 	           			
	           		}
	           	} else if(data.node.a_attr.type == "N3"){ //개인폴더
	             	var isChildren = (data.node.children.length > 0);
	 	          	//console.log("===isChildren : "+isChildren);
	             	if(data.node.parent == "#"){ //최상위 일경우에는
	  		           	$('.vakata-context li').each(function( index ) {
	 		            	if(index >= 5 || index== 2){ //rename, favorate 제거
	 		               		$(this).remove();
	 		              	}
	 		              	if(!isChildren){ //자식값이 없는경우에 open, close 제거
	 		               		if(index ==3 || index== 4){
	 		              	 		$(this).remove();
	 		               		}
	 		              	}
	 		            });
		          	 } else {
	    	         	if(data.node.a_attr.isactive == 'Y'){ //disable discard 빼기
	    	            	var $li6 = $('.vakata-context').find('li:eq(-2)');
	    	    		   	$li6.remove();
	    	               	var $li7 = $('.vakata-context').find('li:eq(-1)');
	      		           	$li7.remove();
	    	           	} else {
	    	           		$('.vakata-context li').each(function( index ) {
	    		            	if(index >= 8){  //폴더 삭제
	    		                	return;
	    		              	}
	    		              	$(this).remove();
	    		            });
	    	           	}
	  	          	 }
	           }
	        });
		},
		getRootNodeList : function(selectedNode,type) {
			var url = "";
			var sendData= { 
				objIsTest : "N",
				companyid : companyid,
				types : type
			};
			//down_1st - types :  N1 (전체부서), N2 (소속부서)
			if(type == CLSTYPES["DEPT"] || type ==CLSTYPES["COLLABO"]){
			  	url ="${ctxRoot}/api/classification/list/down_1st";
			    sendData.types=(type == CLSTYPES["DEPT"]) ? CLSTYPES["COMPANY"] : CLSTYPES["COLLABO"];
			}else{
			  	url ="${ctxRoot}/api/classification/list/down";
			  	sendData.upid = selectedNode;
			}
			if(type == CLSTYPES["USER"]){
			  	sendData.objIsMngMode =true;
			}else{
			  	sendData.isactive="Y";
			}
			var tree = this;
			$.ajax({ 
				url :  url,
				type : "POST" , 
				dataType : 'json',
				contentType : 'application/json',
				async : false , 
				data : JSON.stringify(sendData) , 
				success : function(data){
					if(objectIsEmpty(data.result)){
						return;
					}
					$.each(data.result, function(i, result) {		
						var attr = {}
						var obj;
						var acl;
						if(type == CLSTYPES["DEPT"] || type == CLSTYPES["COLLABO"]){
						    obj = result;
						}else{
						  	obj = result.zappClassification;
						  	acl = result.zappClassAcl;
							attr.acls = acl.acls;
						}
						if(obj.companyid !== companyid){
						  	return;
						}
						attr.objType = "01";
						attr.type = obj.types;
						
						var child = {};
						child.id = obj.classid;
						child.text = obj.name;
		
						if (type == CLSTYPES["DEPT"] || type == CLSTYPES["COLLABO"]){
						  	if(obj.isactive == 'Y'){
  						   		child.icon =(type == CLSTYPES["DEPT"]) ? TREEICONS["DEPT"] : TREEICONS["COLLABO"];
  							}else{
  								child.icon = TREEICONS["DISABLE"];
  							}
  						  	attr.affiliationid = obj.affiliationid;
  							attr.wfid = obj.wfid;
  							attr.wfrequired = obj.wfrequired; 
						} else {
						  	child.icon =(obj.isactive == 'Y') ? TREEICONS[acl.acls] : TREEICONS[obj.isactive];
						}
						attr.upid = obj.classid;
						attr.isactive = obj.isactive;
						attr.objAction= obj.objAction;
						child.a_attr = attr;
						if(obj.isactive == 'Y'|| type == CLSTYPES["USER"]){
						  if((obj.objAction == "N") && (obj.upid == companyid)){
						      tree.appendNode((type ==CLSTYPES["DEPT"])?CLSTYPES["DEPT"]:CLSTYPES["COLLABO"] , child ,"last" ,false ,false);
						  }else{
						    tree.appendNode(obj.upid , child ,"last" ,false ,false);
						   }
						}
					}); 
				}, 
			  error : function(request, status, error) {
	        alertNoty(request,status,error);
				} , 
				beforeSend: function(xhr) {
					xhr.setRequestHeader("AJAX", true);
				},
				complete : function() {} 
			}); 
		},
		getMyRootNodeList : function(selectedNode,type) {
			var sendData= { 
				objIsTest : "N",
				companyid : companyid,
				types : type
			};
			//down_1st - types :  N1 (전체부서), N2 (소속부서)
			sendData.isactive="Y";
			var tree = this;
			$.ajax({ 
				url :  "${ctxRoot}/api/classification/list/down_1st",
				type : "POST" , 
				dataType : 'json',
				contentType : 'application/json',
				async : false , 
				data : JSON.stringify(sendData), 
				success : function(data){ 		
					if(objectIsEmpty(data.result)){
						return;
					}
					var core = [];
					$.each(data.result, function(i, result) {		
						var attr = {}
						var obj;
						var acl;
						obj = result;
						attr.objType = "01";

						var child = {};
						child.id = MyDept+obj.classid;
						child.text = obj.name;
						attr.type = obj.types;
		
  						if(obj.isactive == 'Y'){
  							child.icon =TREEICONS["DEPT"];
  						}else{
  							child.icon = TREEICONS["DISABLE"];
  						}
  						attr.affiliationid = obj.affiliationid;
  						attr.wfid =obj.wfid;
  						attr.wfrequired =obj.wfrequired; 
						attr.upid = child.id;
						attr.isactive = obj.isactive;
						attr.objAction= obj.objAction;
						child.a_attr = attr;
						
						if(obj.isactive == 'Y'){
						    child.parent=MyDept+obj.upid;
						    tree.appendNode(MyDept+obj.upid , child ,"last" ,false ,false);
						}
					}); 
					
					//부서의 폴더를 가져온다.
					 tree.getNodeList(selectedNode,type); 
					
				}, 
			  error : function(request, status, error) {
	        alertNoty(request,status,error);
				} , 
				beforeSend: function(xhr) {
					xhr.setRequestHeader("AJAX", true);
				},
				complete : function() {} 
			}); 
		},
		getNodeList : function(selectedNode,type) {
			var sendData = { objIsTest : "N",
					companyid : "${Authentication.objCompanyid}",
					upid : selectNodeId(selectedNode),
					types : type
					
			};
			
			if(selectedClsid == companyid){
				sendData.isactive="Y";
			}
			
			if(userType == '01' && type=="N2"){
				sendData.isactive="Y";
			}
			
			if(type == "N3"){
			  	sendData.objIsMngMode =true;
			}else{
			  	sendData.objIsMngMode =true;
			}
			var isMyDept = getMyDeptCheck(selectedNode);
			
			var tree = this;
			
			$.ajax({ 
				url : "${ctxRoot}/api/classification/list/down" ,
				type : "POST" , 
				dataType : 'json',
				contentType : 'application/json',
				async : false , 
				data : JSON.stringify(sendData) , 
				success : function(data){ 		
					if (data.status == "0000") {
						$.each(data.result, function(i, result) {		
							var attr = {}
							var acl = result.zappClassAcl;
							var obj = result.zappClassification;
							attr.type = obj.types;
							attr.objType = "01";
							attr.acl = acl.acls;
							
							var child = {};
							child.id = (isMyDept) ? MyDept + obj.classid : obj.classid;
							child.text = obj.name;
							if(obj.isactive == 'Y'){
								child.icon = TREEICONS[acl.acls];
							}else{
								child.icon = TREEICONS[obj.isactive];
							}
							attr.holderid = obj.holderid;
							attr.isactive = obj.isactive;
							attr.upid = selectedNode;
							attr.wpid = obj.wfid;
							child.a_attr = attr;
							  if(isMyDept){
							     tree.appendNode(MyDept+obj.upid, child, "last", false, false);
							  }else{
							    tree.appendNode(obj.upid, child, "last", false, false);
							  }
						}); 
					}
					 if(selectedClsid){
					   tree.openNode(selectedClsid);
					  }
					}, 
			  	error : function(request, status, error) {
	        		alertNoty(request,status,error);
				} , 
				beforeSend: function(xhr) {
					xhr.setRequestHeader("AJAX", true);
				},
				complete : function() {} 
			}); 
		},

		getCoreData : function(){
	  		return this.$tree.jstree(true).settings.core.data;
		},
		setCoreData : function(item){
		  	this.$tree.jstree(true).settings.core.data = item;
			this.$tree.jstree(true).refresh();
		},
		appendNode : function(upid,child,last,flag1,flag2){			
			this.$tree.jstree('create_node' ,upid,child,last,flag1,flag2);
		},
		getNode : function(nodeId){
			return this.$tree.jstree(true).get_node(nodeId);
		},
		getParent : function(nodeId){
			return this.$tree.jstree(true).get_parent(nodeId);		
		},
		getAllChild : function(nodeId){
			//하위 노드가 레더링되어 있어야 함
			return this.$tree.jstree(true).get_children_dom(nodeId);		
		},
		getNodeIdPath : function(nodeId){
			var ids = [];
			var cnt = 0;
			var parentid = "";
			while(parentid !='#'){
				ids.unshift(nodeId);
				parentid = this.getParent(nodeId);							
				nodeId = parentid;
				cnt++;
				if(cnt>16) break;//무한 루프 방지
			}
			return ids;
		},
		closeAll : function(){
		  	this.$tree.jstree("close_all");
		},
		openAll : function(){
		  	this.$tree.jstree("open_all");
		},
		search : function(nodename){
		  	this.closeAll();
			this.openNode(this.root.id);
			this.clearSearch();
			this.bdata = [];
			this.adata = [];
			this.$tree.jstree("deselect_all");
			this.$tree.jstree("search",nodename);
		 },
		clearSearch : function(){
		  	this.$tree.jstree("clear_search");
		},
		openNode : function(nodeId){
		  	this.$tree.jstree("open_node", nodeId);				
		},
		closeNode : function(nodeId){
			this.$tree.jstree("close_node", nodeId);				
		},
		clickNode : function(nodeId){
			var node = this.$tree.jstree(true).get_node(nodeId);
			
			if(node){
				this.$tree.jstree("deselect_all");
				this.$tree.jstree("select_node", nodeId);	
				this.$tree.jstree("toggle_node", nodeId);
				this.openNode(nodeId);
				var duration = setTimeout(function(){
					try{
						var offset = $("#"+nodeId).offset();
						
						$("#fldTreeWrap").scrollTop(offset.top-200);//트리영역의 상단 높이 영역을 뺀다
					}catch(e){					
					}
				},200);
				this.currentNodeId = nodeId;
			}
		},
		searchNext : function(){			
			var nodeid = "";
			if(this.adata.length>1){
				nodeid = this.adata.shift();
				this.bdata.push(nodeid);
			}else{
				nodeid = this.adata[0];
			}

			if(nodeid){				
				this.clickNode(nodeid);	
				var duration = setTimeout(function(){
					this.clickNode(nodeid);	//스크롤 위치 문제로 한번더 호출
				},200);
			}
		},
		searchBefore : function(){			
			var nodeid = "";
			if(this.bdata.length>1){
				nodeid = this.bdata.shift();
				this.adata.push(nodeid);
			}else{
				nodeid =this.bdata[0];
			}
			if(nodeid){				
				this.clickNode(nodeid);	
				var duration = setTimeout(function(){
					this.clickNode(nodeid);	//스크롤 위치 문제로 한번더 호출
				},200);
			}
		},
		editName : function(nodeId,name){
			this.$tree.jstree("edit", nodeId,name);
		},
		clickNode : function(nodeId){			
			this.$tree.jstree("deselect_all");
			this.$tree.jstree("select_node", nodeId);
			this.$tree.jstree("toggle_node", nodeId);
			this.$tree.jstree("open_node", nodeId);
		},
		nodeInfo: function() { //분류 권한 목록 정보를 조회한다.
			if(selectedClsid == companyid || selectedClsid == "N2" || selectedClsid == "N3" || selectedClsid == "N4" || selectedClsid == userid){
				return;
			}
		
    		if(defaultDocAclArr.length > 0){
    			defaultDocAclArr.splice(0, defaultDocAclArr.length);
    		}
			var sendData = {
    	        objIsTest : "N",
    	        classid : selectNodeId(selectedClsid)
	     	 };
  	  
    	    $.ajax({
    	      	url : "${ctxRoot}/api/classification/get",
				type : "POST",
				dataType : 'json',
				contentType : 'application/json',
				async : false,
				data : JSON.stringify(sendData),
				success : function(data) {
    	        	//hide button (등록,수정,삭제, 이동, 복사)
	    	        if(data.status == "0000"){
	    				if(data.result.holdername != null && data.result.holdername.split("：").length > 1) {
	    					folderAttr.holderName = data.result.holdername.split("：")[1];
	    					folderAttr.holderDeptName = data.result.holdername.split("：")[0];
	    					folderAttr.wfid = data.result.zappClassification.wfid;
	    				}
		    	        	
	        	    	$.each(data.result.zappContentAcls, function(i, obj) {
	        	        	var acl = {};
	        	          	acl.aclobjid = obj.aclobjid;
	        	          	acl.name = obj.objname;
	        	          	acl.aclobjtype = obj.aclobjtype; //사용자:01,부서:02,그룹:03
	        	          	acl.acls = obj.acls;
	        	          	defaultDocAclArr.push(acl);
	        	        });
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
    	      	}
    	    });
	  	},
	  	nodeClassInfo: function() { //분류 권한 목록 정보를 조회한다.
	    	var sendData={
        		objIsTest : "N",
        		classid : selectNodeId(selectedClsid)
      		}
		    $.ajax({
		    	url:"${ctxRoot}/api/acl/get_class", //result 숫자 2인경우에만
		      	type : "POST",
		      	dataType : 'json',
		      	contentType : 'application/json',
		      	async : false,
		      	data : JSON.stringify(sendData),
		      	success : function(data) {
		        	//hide button (등록,수정,삭제, 이동, 복사)
		        	//console.log("===list_class", data);
		        	if(data.status == "0000"){
        	  	 		if(data.result == 2){
        	  	   			$('input[id=RegBtn]').show();	
        	  	 		} else {
        	  	   			$('input[id=RegBtn]').hide(); 
        	  	 		}
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
		      	}
		    });
		},
		getUseNode : function(data){
        	var isactive = data.node.a_attr.isactive;
        	var isAcl = false;
      
    		if(data.node.a_attr.type == "N2"){ //전체부서그룹
            	if(selectNodeId(data.node.id) == deptid) {
              		isAcl = true;
            	} else {
                	$.each(data.node.parents,function(i,parent){
	                  	if(deptid == selectNodeId(parent)){
	                    	isAcl = true;
	                    	return false;
	                  	}
                	});
            	}
    
          		//폴더 생성 소유자
          		var isholder = false;
            	if(data.node.a_attr.holderid){
                	isholder=(deptuserid == data.node.a_attr.holderid);
            	}
            	if(isAcl == true && isactive == "Y"){ 
              		$("#btnFldReg").show();
              		if(isholder == true){
                		$("#btnFldMod").show();
                		$("#btnFldDel").show();
              		}
            	} 
            	else if (isAcl == true && isactive == "N"){ 
              		if(isholder == true){
                		$("#btnFldRes").show();
                		$("#btnFldDis").show();
              		}
            	}
          	} 
    		else if (data.node.a_attr.type == "N4"){ //협업그룹
            	if(data.node.a_attr.holderid){ //소유자인경우
                	var isholder = false;
                  	if(data.node.a_attr.holderid) {
                    	isholder=(deptuserid == data.node.a_attr.holderid);
                  	}
                  	if(isholder == true && isactive == "Y"){ 
                    	$("#btnFldReg").show();
                    	if(isholder == true){
                      		$("#btnFldMod").show();
                      		$("#btnFldDel").show();
                    	}
                  	} else if (isholder == true && isactive == "N"){ 
                    	if(isholder == true){
                      		$("#btnFldRes").show();
                      		$("#btnFldDis").show();
                    	}
                  	}
           	}
            else{ // 협업인경우에는 권한이 있는것만 협업그룹 목록이 나타남
                if(data.node.parent == CLSTYPES["COLLABO"]){
              	  	$("#btnFldReg").show();
              	}
            }
        }
    }
    	
	} 
	var selectNodeId = function(nodeId){
		var selectNodeId = nodeId;
		var isMyDept = getMyDeptCheck(selectNodeId);
  		if(isMyDept){
  		 var selectId  = selectNodeId.replace(MyDept,'');
  		 selectNodeId = selectId; 
  		}
  		return selectNodeId;
	}
	var getMyDeptCheck = function(code){
	  return code.indexOf(MyDept)>-1;
	}
	var btnAcl = function(){
    // 등록 권한에 따라서 버튼 활성화 정보를 보여줄 예정 
    //acls권한에 따라 값 변경이 필요하다
    
    allBtnHide();
    $("#MoveRegBtn").hide();
	$("#CopyRegBtn").hide();
	$("#LinkRegBtn").hide();
    if(acl.aclobjtype == "01"){ 
      if(deptuserid == acl.aclobjid && acl.acls == 5){
     	 $('input[id$=RegBtn]').show();	
   		  $("#DocDelBtn").show();	
      }
    }else{
      if(deptid == acl.aclobjid && acl.acls == 5){
       $('input[id$=RegBtn]').show();	
         $("#DelBtn").show();	
      }
    }
    
	}
	
	var fldNavigate = function(){
		var nodeid = $(this).data("meta");
		fn_fldTree.clickNode(nodeid);	
		var duration = setTimeout(function(){
			fn_fldTree.clickNode(nodeid);//IE에서 한번 클릭했을때 비정상위치 한번 더 클릭하면 정상위치로 스크롤 되는 문제(jstree 에서 돔객체의 offset 값 리턴에 문제)
		},200);
	}
	//폴더 생성 임시
	var fldCreate = function(obj){
		console.log("fldCrate : ", obj);
		var childCnt = obj.children.length;
		var objType = obj.a_attr.type;
		var subName = childCnt ? "("+childCnt+")" :"";
		var fldName = "<spring:eval expression="@${lang}['NEW_FOLDER']"/>"+subName;
		var code = "";
		var data = {};
		if(obj.a_attr.type != CLSTYPES["USER"]){
			code = companyAbbrName+getTodateInfo();
			var id = obj.id;
			var parentid = obj.parent;
			if(id.split(":").length > 1 && parentid.split(":").length > 1){ // M:id 일경우
				parentid = parentid.split(":")[1];
				id = id.split(":")[1];
			}else if(id.split(":").length && parentid == "#"){
				parentid = id.split(":")[1];
				id = id.split(":")[1];
			}
			console.log("id : "+ id);
			var zappClassAcls =[];
  			var zappContentAcls =[];
  			zappClassAcls.push({aclobjid : deptid, aclobjtype : "02", acls :2, objAction:"ADD"});
  			zappContentAcls.push({aclobjid : deptid, aclobjtype : "02", acls :5, objAction:"ADD"});
			data.objIsTest = "N";
			data.objDebugged = false;
			data.companyid = companyid;
			data.code = code;
			data.name = fldName;
			data.upid = id;
			data.holderid = deptuserid;
			data.types = obj.a_attr.type;
			data.zappClassAcls = zappClassAcls;
			data.zappContentAcls = zappContentAcls,
			data.wfid = "";
			data.affiliationid = id;
			data.wfrequired = 0;
		}else{
			data.objIsTest = "N";
			data.objDebugged = false;
			data.companyid = companyid;
			data.code = "";
			data.name = fldName;
			data.upid = obj.id;
			data.holderid = userid;
			data.types = obj.a_attr.type;
		}
		
		$.ajax({ 
			url : "${ctxRoot}/api/classification/add" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){               	  
				if(data.status == "0000"){
					var childId = "";
					if(objType == "N2"){
						childId = "M:"+data.result;
					}else{
						childId = data.result;
					}
					
					var child ={ 
						id : childId ,
						parent: obj.id,
						icon : TREEICONS["CREATE"], 
						text : fldName ,
						a_attr : {type:obj.a_attr.type, objType:01,isactive:"Y",acls:2, holderid:deptuserid }}; //신규생성시 nsearch:true 제거 					
					fn_fldTree.appendNode(obj.id, child, 'last', false, false);	
					selectedClsid = child;
					fn_fldTree.editName(selectedClsid,fldName);
				} else {
					alert("<spring:eval expression="@${msgLang}['REGISTRATION_FAILED']"/> Error: " + data.status);
				}
			}, 
		  	error : function(request, status, error) {
      			  alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		});
	}
	
	//폴더 명 수정
	var fldNameEdit = function(obj){
		console.log("obj : ", obj);
		var data = {};
			data.objIsTest = "N";
			data.objDebugged = false;
			data.classid = selectNodeId(obj.id);
			data.name = obj.text;
		$.ajax({ 
			url : "${ctxRoot}/api/classification/change/name" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){
				if(data.status == "0000"){										
				}else{					
					alert("<spring:eval expression="@${msgLang}['FAILED_FOLDER_NAME']"/>");
				}
			}, 
		  error : function(request, status, error) {
        		alertNoty(request,status,error);
			  } , 
			beforeSend : function() {} , 
			complete : function() {} 
		});
		
		return data.stauts;
	}
	
	//폴더 삭제
	var fldDisable = function(){
		
		var inst = $.jstree.reference("#"+treeTargetId);
    	var obj = inst.get_node(selectedClsid);
    	if(!obj){
    		alert("삭제할 폴더를 선택해주세요");
    		return;
    	}else{
    		var data = {};
    		data.objIsTest = "N";
    		data.objDebugged = false;
    		data.classid = selectNodeId(obj.id);
    		data.objIncLower = "Y";
    		$.ajax({ 
    			url : "${ctxRoot}/api/classification/disable" ,
    			type : "POST" , 
    			dataType : 'json',
    			contentType : 'application/json',
    			async : false , 
    			data : JSON.stringify(data) , 
    			success : function(data){               	  
    				if(data.status == "0000"){
    				  selectedClsid = obj.id;
    				  inst.delete_node(obj);
    				  fn_fldTree.clickNode(selectedUpid);
    				  alert("<spring:eval expression="@${msgLang}['DISABLE_FOLDER_SUCCEEDED']"/>");
    				}else{
    					alertErr(data.message);
    				}
    			}, 
    		  error : function(request, status, error) {
           		 alertNoty(request,status,error);
    			  } , 
    			beforeSend : function() {} , 
    			complete : function() {} 
    		});
    	}
    	
		
	}
//폴더 복구
	var fldRestore = function(){
		
		var inst = $.jstree.reference("#"+treeTargetId);
    	var obj = inst.get_node(selectedClsid);
	
    	if(!obj){
    		alert("복구할 폴더를 선택해주세요");
    		return;
    	}else{
    		var data = {};
    		data.objIsTest = "N";
    		data.objDebugged = false;
    		data.classid = selectNodeId(obj.id);
    		data.objIncLower = "Y";
    		$.ajax({ 
    			url : "${ctxRoot}/api/classification/enable" ,
    			type : "POST" , 
    			dataType : 'json',
    			contentType : 'application/json',
    			async : false , 
    			data : JSON.stringify(data) , 
    			success : function(data){               	  
    				if(data.status == "0000"){
    				  selectedClsid = obj.id;
    				  inst.delete_node(obj);
    				  fn_fldTree.clickNode(selectedUpid);
    				  alert("<spring:eval expression="@${msgLang}['RESTORE_FOLDER_SUCCEEDED']"/>");
    				}else{
    					alertErr(data.message);
    				}
    			}, 
    		  error : function(request, status, error) {
           		 alertNoty(request,status,error);
    			  } , 
    			beforeSend : function() {} , 
    			complete : function() {} 
    		});
   		}
	}
//폴더 폐기
	var fldDiscard = function(){
	
		var inst = $.jstree.reference("#"+treeTargetId);
    	var obj = inst.get_node(selectedClsid);
	
    	
    	if(!obj){
    		alert("복구할 폴더를 선택해주세요");
    		return;
    	}else{
			var data = {};
			data.objIsTest = "N";
			data.objDebugged = false;
			data.classid = selectNodeId(obj.id);
			data.objIncLower = "Y";
			$.ajax({ 
				url : "${ctxRoot}/api/classification/discard" ,
				type : "POST" , 
				dataType : 'json',
				contentType : 'application/json',
				async : false , 
				data : JSON.stringify(data) , 
				success : function(data){               	  
				  //console.log("====fldDiscard result : ",data);
					if(data.status == "0000"){
					  selectedClsid =obj.parent;
					  inst.delete_node(obj);
					  fn_fldTree.clickNode(selectedUpid);
					  
					  alert("<spring:eval expression="@${msgLang}['DISCARD_FOLDER_SUCCEEDED']"/>");
					  
					}else{
						alertErr(data.message);
					}
				}, 
			  error : function(request, status, error) {
	        alertNoty(request,status,error);
				  } , 
				beforeSend : function() {} , 
				complete : function() {} 
			});
		}
	}
	////fldtree start
	//clsTree start
	var fn_MainClsTree = { 
		consoleLog : function(data) {                
			$.each(data, function(i,v) {              
			});               
		},
		treeId : "MainClsTree",
		$tree : {},
		root : {
			id : companyid//"CLASS01"
			,type : ""
		},
		initData : function(){
			var MainClsRoot = { id : this.root.id ,parent:"#",icon : TREEICONS["CLS_ROOT"], text : "<spring:eval expression="@${lang}['CLASSIFICATION']"/>" ,a_attr : {type:"02",nsearch:true,objType:01}};
			var clsRootNodeData = [MainClsRoot];
			return clsRootNodeData;
		},
		jstree : function() {
			var tree = this;
			tree.$tree = $("#"+tree.treeId);			
			tree.$tree.jstree({ 
					core: { 
						check_callback: true,
						data: tree.initData() /* 최초에 보여지 최상위 Root Tree */ 
					},
					types: { 
						"default" : {
						"icon" : "glyphicon glyphicon-flash" 
					},
					file: {
						icon: "fa fa-file text-inverse fa-lg" 
					},
					search: {
	                    "case_sensitive": false,
	                    "show_only_matches": true,
	                    "search_callback" : function(key,node){
	                    }
	                }
				},
				checkbox: {
					"three_state": false
				},
				plugins : ["massload","unique","search",  "changed"] 
			}).on("select_node.jstree", function (event, data) {
				console.log("fn_MainClsTree select_node", data);
				selectedClsid = data.node.id;
				selectedClsType = data.node.a_attr.type;
				searchparam.objType = "";
				searchparam.objHandleType = "";
				searchparam.objType = data.node.a_attr.objType;
				if(data.node.a_attr.objHandleType) 
					searchparam.objHandleType = data.node.a_attr.objHandleType;

				$("#folderInfo").empty();
				$("#searchField").hide();
				var nsearch = data.node.a_attr.nsearch;
				if (!data.node.children.length)
					tree.getClsList(data.node.id);
				
				allBtnHide();
				listSearch();
				
				$("#DocDelBtn").show();
				tree.openNode(selectedClsid);
				
				var html = "";
				html += "<tr><td style='padding:5px;'><b>[분류체계]</b></td></tr>";
				html += "<tr><td style='padding:5px;'>" + data.node.text + "</td></tr>";
				html += "<tr><td style='padding:5px;'></td></tr>";

				$("#bundleInfo").empty().append(html);

			})
			.on('changed.jstree', function (e, data) {
				try{
					var path = data.instance.get_path(data.node,' > ');				    
					$("#SelectText").text(path); 
				}catch(e){}
			})
			.on("loaded.jstree", function () {
				//root node 로드된후 처음 한번 이벤트
				tree.getClsList(fn_MainClsTree.root.id);
				//첫뎁스 펼치기
				tree.openNode(fn_MainClsTree.root.id);
			})
			.on("search.jstree", function (nodes, str) { 
	        	if (str.res.length >0) {
	        		tree.clickNode(str.res[0]);
	            }
		    });
		},
		getClsList : function(upid) {
			var tree = this;
			$.ajax({ 
				url : "${ctxRoot}/api/classification/list/down" ,
				type : "POST" , 
				dataType : 'json',
				contentType : 'application/json',
				async : false , 
				data : JSON.stringify({ objIsTest : "N",
					upid : upid,
					types : "02",
					isactive:"Y"}) , 
				success : function(data){
					if(objectIsEmpty(data.result)){
						return;
					}
					$.each(data.result, function(i, result) {
						var attr = {}
						var obj;
						obj =(result.zappClassification)?result.zappClassification:result;
						attr.type = "02";
						attr.objType = 01;
						var child = {};
						child.id = obj.classid;
						child.text = obj.name;
						child.icon = TREEICONS["CLS_SUB"];
						child.a_attr = attr;
						tree.appendNode(obj.upid , child ,"last" ,false ,false); 
					}); 
				}, 
			  error : function(request, status, error) {
	        	alertNoty(request,status,error);
			 	} , 
				beforeSend : function() {} , 
				complete : function() {} 
			}); 
		},
		appendNode : function(upid,child,last,flag1,flag2){			
			this.$tree.jstree('create_node' ,upid,child,last,flag1,flag2);
		},
		search : function(nodename){		
			this.$tree.jstree("deselect_all");
			this.$tree.jstree("search",nodename);			
		},
		clearSearch : function(){
			this.$tree.jstree("clear_search");
		},
		openNode : function(nodeId){
			this.$tree.jstree("open_node", nodeId);		
		},
		clickNode : function(nodeId){			
			this.$tree.jstree("deselect_all");
			this.$tree.jstree("select_node", nodeId);
			this.$tree.jstree("toggle_node", nodeId);
			this.$tree.jstree("open_node", nodeId);
		}
	}
	//clsTree end

	var totCnt = 0;
	//각메뉴별 조회 메서드를 분기한다.
	var listSearch = function(pageno){
		$(".bg").fadeOut();
		openLayer("<spring:eval expression="@${msgLang}['RETRIEVING_LIST_LIST']"/>");
		var ftrSearch = $("#ftrSearch").val();
		
		if (ftrSearch != "" && ($('#adSearchOK').is(':visible'))) {//adSearchLayer->adSearchOK
			listSearchFTR(pageno, "createtime", "desc");//물리파일 목록 조회(테이블)
		}
		else if(selectedClsid == fn_fldTree.root.id){
			alertNoty("request1", "status1", "error1");
			//no event
			closeLayer();
			createDocEmptyList();
		}else if(selectedClsid == fn_MainClsTree.root.id){
			alertNoty("request2", "status2", "error2");
			//no event
			closeLayer();
			createDocEmptyList();
		}else if(selectedClsid =="myjob"){
			//no event
			closeLayer();
			createDocEmptyList();
			
		//NP11:승인요청 문서, NP12:승인완료 문서, NP13:승인반려 문서, NP14:내가 승인할 문서	
		}else if(selectedClsid =="mydoc" ||selectedClsid == "NP11"||selectedClsid == "NP12"||selectedClsid == "NP13"||selectedClsid == "NP14"){
			if(selectedClsid =="mydoc"){//내가 등록한 문서
				$("#searchField").show();
			}else{
				$("#searchField").hide();
			}
			console.log("selectedClsid : " + selectedClsid);
			listSearchN(pageno);//논리파일 목록 조회(조건)
		}else if(selectedClsid =="lately"){
			searchparam.objType = "01";
			searchparam.objHandleType = "01";
			listSearchN(pageno);//논리파일 목록 조회(조건)
		}else if(selectedClsid =="lock"){
			locklistSearch(pageno);//물리파일 목록 조회(테이블)	
		}else if(selectedClsid =="appExpire"){
			listSearchN(pageno);//논리파일 목록 조회(조건)
		}else if(selectedClsid =="expired"){
			listSearchN(pageno);//논리파일 목록 조회(조건)
		}else if(selectedClsid =="shared"){
			sharelistSearch(pageno);
		}else if(selectedClsid =="sharedby"){
			sharelistSearch(pageno);
		}else if(selectedClsid =="faveriteDoc"){
			FavlistSearch(pageno);
		}else if(selectedClsid =="faveriteFld"){
			FavFldlistSearch(pageno);		
		}else if(selectedClsid =="TRASH"){
			searchparam.objType = "01";
			searchparam.objHandleType = "07";
			listSearchN(pageno);//논리파일 목록 조회(조건)
		}else if(searchparam.objType == "01"){
			var ftrSearch = $("#ftrSearch").val();
			if (ftrSearch != "" && ($('#adSearchOK').is(':visible'))) {
				listSearchFTR(pageno, "createtime", "desc");//물리파일 목록 조회(테이블)
			} else {
				listSearchP(pageno, "createtime", "desc");//물리파일 목록 조회(테이블)
			}
		}else{
			closeLayer();
		}
	}
	
	var listAdSearch = function(){//상세검색
		$("#adSearchOK").show();
		listSearch();
	}
	
	var listSearchP = function(pageno, order, sort){
		var title = $("#searchTitle").val();
		var data = {};
		
		var SDate = "";
		var EDate = "";
		
		var orderSort = {};
		//검색 조건 설정(날짜만 있을때)
		var bundle = {};
		var file = {};
		orderSort[order] = sort;
		
		//상세검색이 오픈되경우에는 상세검색의 검색값 사용
		var isIncLower = "N";
		
		 if($('#adSearchOK').css('display') === 'block'){//adSearchLayer
		 	title = $("#adSearchTitle").val();
			console.log("title : " + title);
		 	console.log("$('#adRegSDate').val() : " + $('#adRegSDate').val());
		 	console.log("$('#adRegEDate').val() : " + $('#adRegEDate').val());
		 
			SDate = $('#adRegSDate').val() + " 00:00:00";
			EDate = $("#adRegEDate").val() + " 23:59:59";
	         //상세검색 조건 추가: 등록자 검색
	        var creator = $.trim($("#creatorTitle").val());
	   		var DocNo = $.trim($("#adSearchDocNo").val());
	   	 	var searchKeyword =$.trim($("#keywordTitle").val());
	   	 	
	   		
			var serchDate = SDate + "：" + EDate;
			file["createtime"] = serchDate;
			bundle["createtime"] = serchDate;
	  	      // 등록자
	  	 		if(creator.length > 0){
	  	 			bundle["creatorname"] = creator;
	  	 			file["creatorname"] = creator;
	  	 		}
	  	 		
	  	 		// 문서번호
	  	 		if(DocNo.length > 0){
	  	 			bundle["bno"] = DocNo;
	  	 			file["fno"] = DocNo;
	  	 		}
	  	 		// 하위폴더 조회
	  			if($("#incLowYn").is(":checked") == true){
	  				isIncLower = "Y";
	  			}
	  	 		
	  			if(searchKeyword.length > 0){
					 var keyObj = [];	
					 var keywords =searchKeyword.split(" ");
					for(var key in keywords){
						keyObj.push({"kword":keywords[key]})
					} 
					data.zappKeywords=keyObj;
				}
	  	 		
	    }
		// 제목
		if(title.length > 0){
			bundle["title"] = title;
			file["filename"] = title;
		}
      
		pageno = pageno ? pageno : 1;
		var classObject = {};
			classObject.classid = selectNodeId(selectedClsid);
			classObject.types = selectedClsType;
			data.objIsTest = "N";
			data.objTaskid = taskid;
			data.objType = searchparam.objType; 			 
			data.objRes = "LIST";
			data.objIncLower = isIncLower;
			data.objmaporder = orderSort;
			data.zappBundle = bundle;//검색조건
			data.zappFile = file;//검색조건
			data.objpgnum = pageno;			
			data.zappClassification = classObject;
			data.zArchMFile = {"createtime":serchDate};
		var datacnt = 0;
		$.ajax({ 
			url : "${ctxRoot}/api/content/list_p" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : true , 
			data : JSON.stringify(data) , 
			success : function(data){
				if(data.status == "0000"){	
					datacnt = data.result.length;
					createDocList(data.result, title, order, sort);
				}else{
					createDocEmptyList();
					alertErr(data.message);
				}				
			}, 
		  	error : function(request, status, error) {
				closeLayer();
	        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {		
			 	closeAdSearch();
				//첫 페이지에서만 전체 카운트를 확인한다.
				if(pageno == 1&&datacnt>0){
					totCnt = listCnt(data,pageno);
					$("#totCnt").empty();
					$("#totCnt").text("<spring:eval expression="@${lang}['TOTAL']"/>"+" : "+totCnt);					
				}
				if(datacnt>0){  //데이터가 없는 경우에는 페이지 처리 안함
					createPageNavi(totCnt,pageno,10);
				}
				
				
				$("#adSearchOK").hide();
			} 
		}); 
		
	}

	var listSearchFTR = function(pageno, order, sort){
		var title = $("#searchTitle").val();
		var data = {};
		
		//검색조건:날짜 추가 21.09
		/* var SDate = $('#RegSDate').val() + " 00:00:00";
		var EDate = $("#RegEDate").val() + " 23:59:59"; */
		
		var SDate = "";
		var EDate = "";
		var orderSort = {};
		//검색 조건 설정(날짜만 있을때)
		var bundle = {};
		var file = {};
		orderSort[order] = sort;
		
		//상세검색이 오픈되경우에는 상세검색의 검색값 사용
		var isIncLower = "N";
		 if($('#adSearchOK').is(':visible')){
			title = $("#adSearchTitle").val();
			SDate = $('#adRegSDate').val() + " 00:00:00";
			EDate = $("#adRegEDate").val() + " 23:59:59";
	         //상세검색 조건 추가: 등록자 검색
	        var creator = $.trim($("#creatorTitle").val());
	   		var DocNo = $.trim($("#adSearchDocNo").val());
	   	 	var searchKeyword =$.trim($("#keywordTitle").val());
	   	 	var ftrSearch = $.trim($("#ftrSearch").val());
	   		var serchDate = SDate + "：" + EDate;
			bundle["createtime"] = serchDate;
			file["createtime"] = serchDate;
			
	  	      // 등록자
	  	 		if(creator.length > 0){
	  	 			bundle["creatorname"] = creator;
	  	 			file["creatorname"] = creator;
	  	 		}
	  	 		
	  	 		// 문서번호
	  	 		if(DocNo.length > 0){
	  	 			bundle["bno"] = DocNo;
	  	 			file["fno"] = DocNo;
	  	 		}
	  	 		// 하위폴더 조회
	  			if($("#incLowYn").is(":checked") == true){
	  				isIncLower = "Y";
	  			}
	  	 		
	  			if(searchKeyword.length > 0){
					 var keyObj = [];	
			 		 //var searchKeyword =title ;
					 var keywords =searchKeyword.split(" ");
					for(var key in keywords){
						keyObj.push({"kword":keywords[key]})
					} 
					//keyObj.push({"kword":"kk"});
					data.zappKeywords=keyObj;
				}
	    }
		
		// 제목
		if(title.length > 0){
			bundle["title"] = title;
			file["filename"] = title;
		}

		pageno = pageno ? pageno : 1;
		var classObject = {};
			console.log("ftr : " + selectNodeId(selectedClsid));
			classObject.classid = selectNodeId(selectedClsid);
			//classObject.classtype = selectedClsType;	
			classObject.types = selectedClsType;
			data.objIsTest = "N";
			data.objTaskid = taskid;
			data.objType = searchparam.objType; 			 
			data.objRes = "LIST";
			data.objIncLower = isIncLower;
			data.objmaporder = orderSort;
			data.zappBundle = bundle;//검색조건
			data.zappFile = file;//검색조건
			data.objpgnum = pageno;			
			data.zappClassification = classObject;
			//data.zArchMFile = {"createtime":serchDate};
			data.sword = ftrSearch;
			//확장 테이블의 조건(사업지구별 코드 지정사용)
			//data.zappAdditoryBundle = {"dynamic01":"TEST"};
			//console.log("===ftr list sendData", data);
		/* 	var isMyDept = getMyDeptCheck(selectedClsid);
			 if(isMyDept){
				  var selectId  = selectedClsid.replace(MyDept,'');
				  classObject.classid = selectId; 
			} */
			
		var datacnt = 0;
			
			
		$.ajax({ 
			//url : "${ctxRoot}/api/content/list_p" ,
			url : "${ctxRoot}/api/ftr/list" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : true , 
			data : JSON.stringify(data) , 
			success : function(data){		
			  //console.log("===ftr list return: ", data);
				if(data.status == "0000"){	
					datacnt = data.result.length;
					createDocList(data.result, title, order, sort, true);
				}else{
					createDocEmptyList();
					alertErr(data.message);
				}				
			}, 
		  	error : function(request, status, error) {
		  		//console.log("=== ftr list error:" + error + ", status:" + status);
				closeLayer();
	        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {		
				//console.log("=== ftr list complete");
			 	closeAdSearch();
				//첫 페이지에서만 전체 카운트를 확인한다.
				if(pageno == 1&&datacnt>0){
					totCnt = listCntFTR(data, pageno);
					$("#totCnt").empty();
					$("#totCnt").text("<spring:eval expression="@${lang}['TOTAL']"/>"+" : "+totCnt);					
				}
				if(datacnt>0){  //데이터가 없는 경우에는 페이지 처리 안함
					createPageNavi(totCnt,pageno,10);
				}
				
				$("#adSearchOK").hide();
			} 
		}); 
		
	}
	
	var listCnt = function(param,pageno){
		
		var count = 0;
		param.objRes = "COUNT";
	
		$.ajax({ 
			url : "${ctxRoot}/api/content/list_p" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(param) , 
			success : function(data){	
				count = data.result;
			}, 
			error : function(request, status, error) {
	        alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		}); 
		return count;
	}

	var listCntFTR = function(param,pageno){
		
		var count = 0;
		param.objRes = "COUNT";
	
		$.ajax({ 
			url : "${ctxRoot}/api/ftr/list" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(param) , 
			success : function(data){	
				count = data.result;
			}, 
			error : function(request, status, error) {
	        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		}); 
		return count;
	}
	
	//논리 문서 리스트 조회
	var listSearchN = function(pageno){	
		var title = $("#searchTitle").val();
		
    	//상세검색이 오픈되경우에는 상세검색의 검색값 사용
        if($('#adSearchOK').is(':visible')){
          title = $("#adSearchTitle").val();
        }
		var bundle = undefined;
		var type = selectedClsType;
		var datacnt = 0;
		pageno = pageno ? pageno : 1;

		var classObject = {};
		classObject.classid = "A";
		classObject.classtype = "01";	
		
		var data = {};
		
			if(selectedClsid =="mydoc"){//내가 등록한 문서
				
				$("#btnAdvanceSearch").hide();//상세검색 버튼
				
				bundle = {};
				
				var file = {};
				
				if(title.length > 0){
					bundle["title"] = title;
					file["filename"] = title;
				}
				
				data.zappFile = file;//검색조건
			}
		
		data.objIsTest = "N";
		data.objTaskid = taskid;
		data.objHandleType = searchparam.objHandleType;
		data.objType = searchparam.objType; //bundle : 01, file : 02			
		data.objRes = "LIST";
		data.objmaporder = {"createtime":"desc"};
		data.zappBundle = bundle;
		data.objpgnum = pageno;
		
		if(data.objHandleType == "11"||data.objHandleType == "12"||data.objHandleType == "13"){
		 
		}else{
		  data.zappClassObject = classObject;
		}
			
		$.ajax({ 
			url : "${ctxRoot}/api/content/list_np" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : true , 
			data : JSON.stringify(data) , 
			success : function(data){			
			  console.log("list_np : ",data);
				if(data.status == "0000"){	
					datacnt = data.result.length;
					if(selectedClsid == "NP12" || selectedClsid == "NP13"){//NP12:승인완료 문서, NP13:승인반려 문서
					  createAppDocList(data.result);
					}else{
						console.log("NOT NP12 && NP13");
					  createDocList(data.result);
					}
					
				}else{
					createDocEmptyList();
				}
			}, 
				error : function(request, status, error) {
					closeLayer();
	        	alertNoty(request,status,error);
				} , 
			beforeSend : function() {} , 
			complete : function() {	
			  closeAdSearch();
				//첫 페이지에서만 전체 카운트를 확인한다.
				if(pageno == 1&&datacnt>0){
					totCnt = listCntN(data,pageno);
					//totCnt =19;
					$("#totCnt").empty();
					$("#totCnt").text("<spring:eval expression="@${lang}['TOTAL']"/>"+" : "+totCnt);
				}	
				if(datacnt>0){  //데이터가 없는 경우에는 페이지 처리 안함
					createPageNavi(totCnt,pageno,10);
				}
			} 
		}); 
		
	}
	
	var listCntN = function(param,pageno){
		var count = 0;
		param.objRes = "COUNT";
	
		$.ajax({ 
			url : "${ctxRoot}/api/content/list_np" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(param) , 
			success : function(data){						
				count = data.result;
			}, 
			error : function(request, status, error) {
        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		}); 
		return count;
	}
	
	var locklistSearch = function(pageno){
		
		$("#SelectText").empty();
		$("#SelectText").append('<li><a href="#none"><img src="${image}/icon/Path 52.png" ></a></li>');
		$("#SelectText").append('<span>반출문서</span>');
		
		$("#shareDocType").hide(); 		// 공유문서 종류
		$("#aprovType").hide();			// 승인문서 종류 
		$("#latelyType").hide();		// 최근 문서
		$("#fvtType").hide();			// 즐겨찾기 종류
		$("#SearchForm").hide();		// 검색창
		$("#detailSearch").hide();		// 상세검색
		$("#docRegistration").hide();	// 문서등록
		$("#moreMenu").hide();			// 더보기메뉴
		$("#folderMenu").hide();		// 폴더메뉴
		
		pageno = pageno ? pageno : 1;
		var locker = {};
			locker.lockerid = deptuserid;
		var data = {};
			data.objIsTest = "N";
			data.objTaskid = taskid;
			data.objType = "04"; //bundle : 01, file : 02,lock : 04
			data.objRes = "LIST";
			data.objmaporder = {"createtime":"desc"};
			data.objpgnum = pageno;
			data.zappLockedObject = locker;
		var datacnt = 0;
		$.ajax({ 
			url : "${ctxRoot}/api/content/list_p" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : true , 
			data : JSON.stringify(data) , 
			success : function(data){
				console.log("=== list_p data", data);
				if(data.status == "0000"){
					datacnt = data.result.length;
					createDocList(data.result);
				}else{
					createDocEmptyList();
					//alert(data.message);
				}
			}, 
			error : function(request, status, error) {
				closeLayer();
        		alertNoty(request,status,error);
				 } , 
			beforeSend : function() {} , 
			complete : function() {			
				//첫 페이지에서만 전체 카운트를 확인한다.
				if(pageno == 1&&datacnt>0){
					totCnt = locklistCnt(data,pageno);
					$("#totCnt").empty();
					$("#totCnt").text("<spring:eval expression="@${lang}['TOTAL']"/>"+" : "+totCnt);
				}
				if(datacnt>0){  //데이터가 없는 경우에는 페이지 처리 안함
					createPageNavi(totCnt,pageno,10);
				}
			} 
		}); 
	}
	
	var sharelistSearch = function(pageno){		
		
		pageno = pageno ? pageno : 1;
		var data = {};
			data.objIsTest = "N";
			data.objTaskid = taskid;
			data.objType = "03"; //bundle : 01, file : 02,lock : 04
			data.objRes = "LIST";
			if(selectedClsid =="shared"){
				data.zappSharedObject = {"sharerid":"Y"};
			}else if(selectedClsid =="sharedby"){
				data.zappSharedObject = {"readerid":"Y"};
			}
			data.objmaporder = {"createtime":"desc"};
			data.objpgnum = pageno;

		var datacnt = 0;	
		$.ajax({ 
			url : "${ctxRoot}/api/content/list_p" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : true , 
			data : JSON.stringify(data) , 
			success : function(data){	
			  	console.log("===share : ",data);
				if(data.status == "0000"){
					datacnt = data.result.length;
					createShareDocList(data.result);
				}else{
					createShareDocEmptyList();
					alertErr(data.message);
				}
			}, 
			error : function(request, status, error) {
				closeLayer();
        	alertNoty(request,status,error);
				 } , 
			beforeSend : function() {} , 
			complete : function() {			
				//첫 페이지에서만 전체 카운트를 확인한다.
				if(pageno == 1&&datacnt>0){
					totCnt = locklistCnt(data,pageno);
					$("#totCnt").empty();
					$("#totCnt").text("<spring:eval expression="@${lang}['TOTAL']"/>"+" : "+totCnt);
				}
				if(datacnt>0){  //데이터가 없는 경우에는 페이지 처리 안함
					createPageNavi(totCnt,pageno,10);
				}
			} 
		}); 
	}
	
	var locklistCnt = function(param,pageno){
		var count = 0;
		param.objRes = "COUNT";
	
		$.ajax({ 
			url : "${ctxRoot}/api/content/list_p" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(param) , 
			success : function(data){						
				count = data.result;
			}, 
			error : function(request, status, error) {
        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		}); 
		return count;
	}
	
	// 최근항목
	var latelySearch = function(){
		
		// 검색창 숨김 및 공유선택창 보이기
		$("#SelectText").empty();
		$("#SelectText").append('<li><a href="#none"><img src="${image}/icon/Path 52.png" ></a></li>');
		$("#SelectText").append('<span>최근항목</span>');
			
		selectedClsid = "lately";
		listSearch();
	}
	
	
	// 휴지통
	var TrashSearch = function(){
		
		$("#SelectText").empty();
		$("#SelectText").append('<li><a href="#none"><img src="${image}/icon/Path 52.png" ></a></li>');
		$("#SelectText").append('<span>휴지통</span>');
		
		$("#shareDocType").hide(); 		// 공유문서 종류
		$("#aprovType").hide();			// 승인문서 종류 
		$("#latelyType").hide();		// 최근 문서
		$("#fvtType").hide();			// 즐겨찾기 종류
		$("#SearchForm").hide();		// 검색창
		$("#detailSearch").hide();		// 상세검색
		$("#docRegistration").hide();	// 문서등록
		$("#moreMenu").show();			// 더보기메뉴
		$("#folderMenu").hide();		// 폴더메뉴
		
		
		
		$('li[id$=RegBtn]').hide();
		$('li[id$=DelBtn]').hide();
		$("#canCelDelBtn").show();
		$("#disDelBtn").show();
		selectedClsid = "TRASH";
		
		listSearch();
	}
	
	// 즐겨찾기
	var FavlistSearch = function(pageno){
	
		selectedClsid = "faveriteDoc";
		
		var title = $("#searchTitle").val();
    	//상세검색이 오픈되경우에는 상세검색의 검색값 사용
        if($('#adSearchOK').is(':visible')){
          title = $("#adSearchTitle").val();
        }
		var bundle = undefined;
		if(title)bundle = {title:title};
		pageno = pageno ? pageno : 1;

		var data = {};
			data.objIsTest = "N";
			data.objTaskid = taskid;
			data.objType = "05"; //즐겨찾기
			data.objRes = "LIST";
			data.objmaporder = {"createtime":"desc"};
			data.objpgnum = pageno;
			//data.zappClassObject = classObject;
		var datacnt = 0;	
		$.ajax({ 
			url : "${ctxRoot}/api/content/list_p" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){	
				console.log("====fav : ",data);
				if(data.status == "0000"){
					datacnt = data.result.length;
					createDocList(data.result);
				}else{
					createDocEmptyList();
					//alertNoty(data.message);
				}
			}, 
			
			error : function(request, status, error) {
				closeLayer();
        	alertNoty(request,status,error);
				} , 
			beforeSend : function() {} , 
			complete : function() {		
			  closeAdSearch();
				//첫 페이지에서만 전체 카운트를 확인한다.
				if(pageno == 1&&datacnt>0){
					totCnt = FavlistCnt(data,pageno);
					$("#totCnt").empty();
					$("#totCnt").text("<spring:eval expression="@${lang}['TOTAL']"/>"+" : "+totCnt);
				}
				if(datacnt>0){  //데이터가 없는 경우에는 페이지 처리 안함
					createPageNavi(totCnt,pageno,10);
				}
			} 
		}); 
	}
	
	var FavlistCnt = function(param,pageno){
		var count = 0;
		param.objRes = "COUNT";
	
		$.ajax({ 
			url : "${ctxRoot}/api/content/list_p" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(param) , 
			success : function(data){						
				count = data.result;
			}, 
			error : function(request, status, error) {
				closeLayer();
        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		}); 
		return count;
	}
	
	var FavFldlistSearch = function(pageno){
	  	//console.log("====selectedContentid : "+selectedContentid);
		var title = $("#searchTitle").val();
    	//상세검색이 오픈되경우에는 상세검색의 검색값 사용
        if($('#adSearchOK').is(':visible')){
          title = $("#adSearchTitle").val();
        }
		var bundle = undefined;
		if(title)bundle = {title:title};
		pageno = pageno ? pageno : 1;

		var data = {};
			data.objIsTest = "N";
			data.objRes = "LIST";
			data.objpgnum = pageno;
		var datacnt = 0;
		$.ajax({ 
			url : "${ctxRoot}/api/classification/list/mark" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : true , 
			data : JSON.stringify(data) , 
			success : function(data){	
				if(data.status == "0000"){				
					datacnt = data.result.length;
					createFavFldList(data.result);
				}else{
					createFavFldEmptyList();
					alertErr(data.message);
				}
			}, 
			error : function(request, status, error) {
				closeLayer();
        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {		
			  closeAdSearch();
				//첫 페이지에서만 전체 카운트를 확인한다.
				if(pageno == 1&&datacnt>0){
					totCnt = FavFldlistCnt(data,pageno);
					$("#totCnt").empty();
					$("#totCnt").text("<spring:eval expression="@${lang}['TOTAL']"/>"+" : "+totCnt);
				}
				if(datacnt>0){  //데이터가 없는 경우에는 페이지 처리 안함
					createPageNavi(totCnt,pageno,10);
				}
			} 
		}); 
	}
	
	var FavFldlistCnt = function(param,pageno){
		var count = 0;
		param.objRes = "COUNT";
	
		$.ajax({ 
			url : "${ctxRoot}/api/classification/list/mark" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(param) , 
			success : function(data){						
				count = data.result;
			}, 
			error : function(request, status, error) {
        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		}); 
		return count;
	}
	
	//TODO db에서 조회하는 API로 변경필요
	var getDocStateList = function(){
		var data = [];
		data.push({"name":"<spring:eval expression="@${lang}['NORMAL']"/>","codevalue":"00"});
		data.push({"name":"<spring:eval expression="@${lang}['DISPOSAL_WAIT']"/>","codevalue":"01"});
		data.push({"name":"<spring:eval expression="@${lang}['LOCK']"/>","codevalue":"03"});
		data.push({"name":"<spring:eval expression="@${lang}['CHANGE_REQUEST']"/>","codevalue":"A0"});
		data.push({"name":"<spring:eval expression="@${lang}['DELETE_REQUEST']"/>","codevalue":"A1"});
		data.push({"name":"<spring:eval expression="@${lang}['RECOVER_REQUEST']"/>","codevalue":"A2"});
		data.push({"name":"<spring:eval expression="@${lang}['MOVE_REQUEST']"/>","codevalue":"A3"});
		data.push({"name":"<spring:eval expression="@${lang}['COPY_REQUEST']"/>","codevalue":"A4"});
		data.push({"name":"<spring:eval expression="@${lang}['LOCK_REQUEST']"/>","codevalue":"A5"});
		data.push({"name":"<spring:eval expression="@${lang}['MOVE_REQUEST']"/>","codevalue":"A6"});

		data.push({"name":"<spring:eval expression="@${lang}['CREATE_REQUEST']"/>","codevalue":"B1"});
		data.push({"name":"<spring:eval expression="@${lang}['DISCARD_REQUEST']"/>","codevalue":"B2"});
		data.push({"name":"<spring:eval expression="@${lang}['CREATE_REQUEST']"/>","codevalue":"B3"});

		data.push({"name":"<spring:eval expression="@${lang}['CHANGE_REJECT']"/>","codevalue":"C0"});
		data.push({"name":"<spring:eval expression="@${lang}['DELETE_REJECT']"/>","codevalue":"C1"});
		data.push({"name":"<spring:eval expression="@${lang}['RECOVER_REJECT']"/>","codevalue":"C2"});
		data.push({"name":"<spring:eval expression="@${lang}['MOVE_REJECT']"/>","codevalue":"C4"});
		data.push({"name":"<spring:eval expression="@${lang}['COPY_REJECT']"/>","codevalue":"C5"});
		data.push({"name":"<spring:eval expression="@${lang}['LOCK_REJECT']"/>","codevalue":"C6"});
		
		data.push({"name":"<spring:eval expression="@${lang}['CREATE_REJECT']"/>","codevalue":"D1"});
		data.push({"name":"<spring:eval expression="@${lang}['DISCARD_REJECT']"/>","codevalue":"D2"});
		
		return data;		
	}
	
	
	var aclList =[];
	//TODO db에서 조회하는 API로 변경필요
	//권한 목록 가져오기
    var initAclList = function(){
      aclList = sysCodeList("${ctxRoot}","07","${Authentication.objCompanyid}");	
    }
	
  	var rightList = function(){
		return aclList;		
	}
	var createDocHeader = function(order, sort){
		var html = "<tr>";
		var sortImg = "arrow_up.png";
		if (sort == "desc") {
			sortImg = "arrow_down.png";			
		} else {
			sortImg = "arrow_up.png";						
		}
		html += "<th class='fixedHeader' style='width: 5%;'><input type='checkbox' name='allchk' id='allchk'/><label for='allchk'></label></th>";
		html += "<th class='fixedHeader' style='width: 7%;'>구분</th>";
		html += "<th class='fixedHeader' style='width: 17.5%;'>"+docInfo.folderName+"</th>";
		if (order == "title") {
			html += "<th class='fixedHeader' style='width: 30%; cursor:pointer;' onclick=sortHeader('T')>"+docInfo.title+"&nbsp<img src='${image}/icon/" + sortImg + "' id='sort_title' style='width:10px; vertical-align:middle;'</th>";
		} else {
			html += "<th class='fixedHeader' style='width: 30%; cursor:pointer;' onclick=sortHeader('T')>"+docInfo.title+"</th>";
		}
		if (order == "CREATORNAME") {
			html += "<th class='fixedHeader' style='width: 13%; cursor:pointer;' onclick=sortHeader('R')>"+docInfo.register+"&nbsp<img src='${image}/icon/" + sortImg + "' id='sort_register' style='width:10px; vertical-align:middle; '</th>";
		} else {
			html += "<th class='fixedHeader' style='width: 13%; cursor:pointer;' onclick=sortHeader('R')>"+docInfo.register+"</th>";			
		}
		if (order == "createtime") {
			html += "<th class='fixedHeader' style='width: 11%; cursor:pointer;' onclick=sortHeader('C')>"+docInfo.time+"&nbsp<img src='${image}/icon/" + sortImg + "' id='sort_time' style='width:10px; vertical-align:middle;'</th>";
		} else {
			html += "<th class='fixedHeader' style='width: 11%; cursor:pointer;' onclick=sortHeader('C')>"+docInfo.time+"</th>";			
		}
		html += "<th class='fixedHeader' style='width: 8.5%;'>"+docInfo.authority+"</th>";
		html += "<th class='fixedHeader' style='width: 6%;'>"+docInfo.status+"</th>";
		html += "<th class='fixedHeader'></th>";
		html += "</tr>";
		$("#contentHeader").empty().append(html);
		var html1 ="<tr><td colspan='8'><spring:eval expression="@${msgLang}['NOTEXIST_DOC']"/></td></tr>";
		$("#bundleList").empty().append(html1);		
	}
	
	var createAppDocHeader = function(){
		var html = "<tr>";
		html += "<th class='fixedHeader' style='width: 3%;'><input type='checkbox' name='selectAll' id='allchk'/><label for='allchk'></label></th>";
		html += "<th class='fixedHeader' style='width: 5%;'>구분</th>";
		html += "<th class='fixedHeader' style='width: 13%;'>"+docInfo.folderName+"</th>";
		html += "<th class='fixedHeader' style='width: 25%;'>"+docInfo.title+"</th>";
		html += "<th class='fixedHeader' style='width: 13%;'>"+docInfo.time+"</th>";		
		html += "<th class='fixedHeader' style='width: 13%;'>"+docInfo.approvalTime+"</th>";	
		html += "<th class='fixedHeader' style='width: 13%;'>"+docInfo.appAuthority+"</th>";	
		html += "<th class='fixedHeader' style='width: 21%;'>"+docInfo.reason+"</th>";
		html += "<th class='fixedHeader' style='width: 3%;'></th>";
		html += "</tr>";
		$("#contentHeader").empty().append(html);
		var html1 ="<tr><td colspan='8'><spring:eval expression="@${msgLang}['NOTEXIST_DOC']"/></td></tr>";
		$("#bundleList").empty().append(html1);		
	}
	
	var createRejectDocHeader = function(){
		var html = "<tr>";
		html += "<th class='fixedHeader' style='width: 3%;'><input type='checkbox' name='selectAll' id='allchk'/><label for='allchk'></label></th>";
		html += "<th class='fixedHeader' style='width: 5%;'>구분</th>";
		html += "<th class='fixedHeader' style='width: 13%;'>"+docInfo.folderName+"</th>";
		html += "<th class='fixedHeader' style='width: 25%;'>"+docInfo.title+"</th>";
		html += "<th class='fixedHeader' style='width: 13%;'>"+docInfo.time+"</th>";		
		html += "<th class='fixedHeader' style='width: 13%;'>"+docInfo.rejectTime+"</th>";	
		html += "<th class='fixedHeader' style='width: 13%;'>"+docInfo.appAuthority+"</th>";	
		html += "<th class='fixedHeader' style='width: 21%;'>"+docInfo.reason+"</th>";
		html += "<th class='fixedHeader' style='width: 3%;'></th>";
		html += "</tr>";
		$("#contentHeader").empty().append(html);
		var html1 ="<tr><td colspan='8'><spring:eval expression="@${msgLang}['NOTEXIST_DOC']"/></td></tr>";
		$("#bundleList").empty().append(html1);		
	}
	var createShareDocHeader = function(){
		var html = "<tr>";
		html += "<th class='fixedHeader' style='width: 5%;'><input type='checkbox' name='selectAll' id='allchk'/><label for='allchk'></label></th>";
		html += "<th class='fixedHeader' style='width: 7%;'>구분</th>";
		html += "<th class='fixedHeader' style='width: 8%;'>"+docInfo.authority+"</th>";
		html += "<th class='fixedHeader' style='width: 13%;'>"+docInfo.folderName+"</th>";
		html += "<th class='fixedHeader' style='width: 38%;'>"+docInfo.title+"</th>";
		html += "<th class='fixedHeader' style='width: 8%;'>"+docInfo.register+"</th>";
		html += "<th class='fixedHeader' style='width: 13%;'>"+docInfo.time+"</th>";
		html += "<th class='fixedHeader' style='width: 3%;'>"+docInfo.status+"</th>";
		html += "<th class='fixedHeader'></th>";
		html += "</tr>";
		$("#contentHeader").empty().append(html);
		var html1 ="<tr><td colspan='8'><spring:eval expression="@${msgLang}['NOTEXIST_DOC']"/></td></tr>";
		$("#bundleList").empty().append(html1);		
	}
	
	var createFavFldHeader = function(){
		var html = "<tr>";
		html += "<th class='fixedHeader' style='width: 5%;'><input type='checkbox' name='selectAll' id='allchk'/><label for='allchk'></label></th>";
		html += "<th class='fixedHeader' style='width: 15%;'><spring:eval expression="@${lang}['FOLDER_BOX_TYPE']"/></th>";
		html += "<th class='fixedHeader' style='width: 80%;'><spring:eval expression="@${lang}['DOC_PATH']"/></th>";
		html += "<th class='fixedHeader' style='width: 3%;'></th>";
		html += "</tr>";
		$("#contentHeader").empty().append(html);
		var html1 ="<tr><td colspan='8'><spring:eval expression="@${msgLang}['NOTEXIST_DOC']"/></td></tr>";
		$("#bundleList").empty().append(html1);		
	}
	
	var createDocList = function(data, isSearch, order, sort, isFtr){
		createDocHeader(order, sort);
		if(data.length>0){
			$("#bundleList").empty();
			var inHtml = "";
			var docStateLst = getDocStateList();
			var rightLst = rightList();
			for(var i=0;i<data.length;i++){
				try{
					var bundle = data[i];
					if (bundle.files) {
						bundle.files = bundle.files.replace(/\'/g,"&#39;");	// FTR결과에 color='blue'가 포함됨
					}
					bundle.title = bundle.title.replace(/\'/g,"&#39;");	// 파일명에 '가 포함된 경우
					var acls = 0;
					var islocked = bundle.islocked;
					try{
						acls = bundle.zappAcl.acls;
					}catch(e){acls = 0 }
					var contentno = bundle.contentno ? bundle.contentno : "";
					
					//NP12:승인완료 문서, NP13:승인반려 문서, NP14:내가 승인할 문서, NP15:
					if(selectedClsid == "NP12" || selectedClsid == "NP13" || selectedClsid == "NP14" || selectedClsid == "NP15" ){
						inHtml += "<tr id='"+bundle.contentid+"' data-meta='"+JSON.stringify(bundle)+"'>";
					}else{
						inHtml += "<tr id='"+bundle.contentid+"' data-meta='"+JSON.stringify(bundle)+"'>";
					}
					
					var titles = bundle.title.split("：");
					var filename = titles[0];
					var extnames = filename.split(".");
					var extname = extnames[extnames.length-1].toLowerCase();
					var iconfile = "";
					if (extname == "hwp" || extname == "hwpx")
						iconfile = "icon_HWP.png";
					else if (extname == "pdf")
						iconfile = "icon_PDF.png";
					else if (extname == "txt")
						iconfile = "icon_TXT.png";
					else if (extname == "png")
						iconfile = "icon_PNG.png";
					else if (extname == "gif")
						iconfile = "icon_GIF.png";
					else if (extname == "tif")
						iconfile = "icon_TIF.png";
					else if (extname == "bmp")
						iconfile = "icon_BMP.png";
					else if (extname == "jpg" || extname == "jpeg")
						iconfile = "icon_JPG.png";
					else if (extname == "doc" || extname == "docx")
						iconfile = "icon_DOC.png";
					else if (extname == "xls" || extname == "xlsx")
						iconfile = "icon_XLS.png";
					else if (extname == "ppt" || extname == "pptx")
						iconfile = "icon_PPT.png";
					else 
						iconfile = "icon_ETC.png";
					//체크박스
					inHtml += "<td><input type='checkbox' name='chkbox' id='chkbox"+(i+1)+"' value='"+bundle.contentid+"'><label for='chkbox"+(i+1)+"'><label></td>";
					// 파일/번들 구분
					var gubun = "<spring:eval expression="@${lang}['BUNDLE']"/>";
					if(bundle.contenttype != "01"){ // 파일
						gubun = "<spring:eval expression="@${lang}['FILE']"/>";
						
						if(acls >= 5){
							inHtml += "<td title='" + gubun + "'>&nbsp<img src='${image}/icon/" + iconfile + "' style='vertical-align:middle; cursor:pointer;' onclick=fileDownload('"+titles[1]+"');></td>";	
						}else{
							inHtml += "<td title='" + gubun + "'>&nbsp<img src='${image}/icon/" + iconfile + "' style='vertical-align:middle;'></td>";
						}
						
					} else { // 번들
						if (bundle.ctype == "02") { // Virtual
							gubun = "<spring:eval expression="@${lang}['LINK']"/>";
							inHtml += "<td title='" + gubun + "'>&nbsp<img src='${image}/icon/icon_admin_13.gif' style='vertical-align:middle;'/></td>";							
						} else { // 01:Normal or else
							inHtml += "<td title='" + gubun + "'>&nbsp<img src='${image}/icon/Group 19.png' style='vertical-align:middle;'/></td>";
						}
					}

					//폴더경로
					var classidname = bundle.classname;
					var classid = "";
					var classname = "";
					if(classidname){
						var names = classidname.split("：");
						classid = names[0];
						classname = names[1]; 
					}
					if(isSearch){
					  inHtml += "<td  class='mob_none' title='" + bundle.classpath+"' onclick='fn_FldClick('"+classid+"');' style='cursor: pointer;'>"+classname+"</td>";
					}else{
					  inHtml += "<td  class='mob_none' title='" + bundle.classpath+"' >"+classname+"</td>";
					}
					
					
					var summary = "";
					if (bundle.summary != null){
						summary = bundle.summary;
					} else {
					}
					if(acls == 1){
						inHtml += "<td class='subject' title='"+summary+"' onclick=Docalert();>" + titles[0] + "</td>";
					}else if(acls>1){
						var titles = bundle.title.split("：");
						if (bundle.files) {
							var title = "<b>" + titles[0].replace(/\'/g,"&#39;") + "</b>";
							// ftr검색결과
							title = title + "<br><font size=-1>" + bundle.files + "</font>";
						} else {
							var title = titles[0];						
						}
						var versionid = titles[1];
						inHtml += "<td class='subject' title='" + summary + "' onclick=docInfoView('" + bundle.contentid + "','" + versionid + "','"+bundle.contenttype+"','" + extname + "');>" + title + "</td>";
					}else{
						inHtml += "<td class='subject' title='"+summary+"'>" + titles[0] + "</td>";
					}
					
					// 소유자
					inHtml += "<td class='mob_none'>[" + bundle.creatordeptname + "] " + bundle.creatorname + "</td>";
					
					// 수정시간
					if(bundle.updatetime != null){
						var updateTime = bundle.updatetime.split(".");
						inHtml += "<td class='mob_none'>" + updateTime[0] + "</td>";
					}else{
						inHtml += "<td class='mob_none'>" + bundle.createtime + "</td>";
					}
					
					var state = "";
					var code = "";
					for(var j=0;j <docStateLst.length; j++ ){
						var docState = docStateLst[j];
						if(docState.codevalue == bundle.state) {
							state = docState.name;
							code = docState.codevalue
							break;
						}				
					}
					
					
					// 조회 권한
					//inHtml += "<td class='mob_none'><span class='authWrite on'></span><span class='authRead on'></span></td>";
					var rightName = "";
					for(var j=0;j <rightLst.length; j++ ){
						var right = rightLst[j];
						if(right.codevalue == acls) {
							rightName = right.name; 
							continue;
						}				
					}
					
					inHtml += "<td class='mob_none'>"+rightName+"</span></td>";
						
					
					// 상태
					// A0:편집, A1:삭제, A2:복구, A3:이동, A4:복사, A5:잠금, A6:이동, B1:등록, B2:폐기, 
					if(code == "A0" || code == "B1"){
						inHtml += "<td class='mob_none'>"+bundle.apporder +"<spring:eval expression="@${msgLang}['WAITING_APL']"/></td>";	
					} else if (code == "03"){ // 잠금
						inHtml += "<td class='mob_none'>" + state + "<img src='${image}/icon/icon-zenithlist-lock.gif' style='width:17px; vertical-align:middle;'/></td>";	
					}else{
						inHtml += "<td class='mob_none'>" + state + "</td>";
					}
					// 메뉴
					inHtml += ListMenu(bundle.contentid, state);
					inHtml += "</tr>";
					
				}catch(e){
					console.log("error : ",e);
				}
			}
			$("#bundleList").html(inHtml);
			
			
			sideMenuClickEvent();
			
		}else{
			createDocEmptyList();
		}
		closeLayer();
	}
	
	var ListMenu = function(contentid,state){
		var inHtml = "";
		
		inHtml += "<td><div class='tooltip_wrap'>";
		inHtml += "<button type='button'><img src='${image}/icon/Group 8.png'></button>";
        <!-- tooltip -->
        inHtml += "<div class='ui_popup widePop'>"
        inHtml += "<ul>";
        
        inHtml += "<li><a href='#none' onclick=docInfoOpen('"+contentid+"')><img src='${image}/icon/icon_c08.png'>문서정보</a></li>";	
        if(selectedClsid == "TRASH"){
        	inHtml += "<li><a href='#none' onclick=cancelDelDocOne('"+contentid+"')><img src='${image}/icon/icon_c05.png'>복구</a></li>";
        	inHtml += "<li><a href='#none' onclick=discardDocOne('"+contentid+"')><img src='${image}/icon/icon_c05.png'>폐기</a></li>";
        }else if(selectedClsid == "faveriteDoc"){
        	inHtml += "<li><a href='#none' onclick=unfavDoc('"+contentid+"')><img src='${image}/icon/icon_c05.png'>즐겨찾기해제</a></li>";	
        }else if(selectedClsid == "NP11" || selectedClsid == "NP12" || selectedClsid == "NP13" || selectedClsid == "NP14"){
        }else{
        	inHtml += "<li><a href='#none' onclick=favDoc('"+contentid+"')><img src='${image}/icon/icon_c07.png'>즐겨찾기</a></li>";
        	inHtml += "<li><a href='#none' onclick=delDocOne('"+contentid+"')><img src='${image}/icon/icon_c05.png'>삭제</a></li>";
        	inHtml += "<li><a href='#none' onclick=CmtRegOpen('"+contentid+"')><img src='${image}/icon/icon_c09.png'>코멘트</a></li>";
        }
        
        inHtml += "</ul>";
        inHtml += "</div>";
        inHtml += "</div></td>";
        
        return inHtml;
	}
	
	
	var Docalert = function(){
		alert("조회 권한이 없습니다.");
	}
	
	/*
		name : createAppDocList
		param : result data
		desc : success. reject DocList
	*/
	var createAppDocList = function(data){
		if(selectedClsid =="NP12"){//NP12:승인완료 문서, NP13:승인반려 문서
		 	createAppDocHeader();
		} else if(selectedClsid =="NP13"){
		    createRejectDocHeader();
		}
		if(data.length>0){
			$("#bundleList").empty();
			var inHtml = "";
			var docStateLst = getDocStateList();
			var rightLst = rightList();
			for(var i=0;i<data.length;i++){
				try{
					var bundle = data[i];
					bundle.title = bundle.title.replace(/\'/g,"&#39;");
					
					var acls = 0;
					var islocked = bundle.islocked;
					try{
						acls = bundle.zappAcl.acls;
					}catch(e){acls = 0 }
					var contentno = bundle.contentno ? bundle.contentno : "";
					inHtml += "<tr id='"+bundle.contentid+"' data-meta='"+JSON.stringify(bundle)+"'>";
					inHtml += "<td><input type='checkbox' name='chkbox' id='chkbox"+i+"' value='"+bundle.contentid+"'><label for=id='chkbox"+i+"'></label></td>";
					//inHtml += "<td title='"+contentno+"'>"+contentno+"</td>";
					
					var titles = bundle.title.split("：");
					var filename = titles[0];
					var extnames = filename.split(".");
					var extname = extnames[extnames.length-1].toLowerCase();
					
					var iconfile = "";
					if (extname == "hwp" || extname == "hwpx")
						iconfile = "icon_HWP.png";
					else if (extname == "pdf")
						iconfile = "icon_PDF.png";
					else if (extname == "txt")
						iconfile = "icon_TXT.png";
					else if (extname == "png")
						iconfile = "icon_PNG.png";
					else if (extname == "gif")
						iconfile = "icon_GIF.png";
					else if (extname == "tif")
						iconfile = "icon_TIF.png";
					else if (extname == "bmp")
						iconfile = "icon_BMP.png";
					else if (extname == "jpg" || extname == "jpeg")
						iconfile = "icon_JPG.png";
					else if (extname == "doc" || extname == "docx")
						iconfile = "icon_DOC.png";
					else if (extname == "xls" || extname == "xlsx")
						iconfile = "icon_XLS.png";
					else if (extname == "ppt" || extname == "pptx")
						iconfile = "icon_PPT.png";
					else 
						iconfile = "icon_ETC.png";

					// 파일/번들 구분
					var gubun = "<spring:eval expression="@${lang}['BUNDLE']"/>";
					if(bundle.contenttype != "01"){ // 파일
						gubun = "<spring:eval expression="@${lang}['FILE']"/>";
						inHtml += "<td title='" + gubun + "'>&nbsp<img src='${image}/icon/" + iconfile + "' style='vertical-align:middle;'/></td>";
					} else { // 번들
						if (bundle.ctype == "02") { // Virtual
							gubun = "<spring:eval expression="@${lang}['LINK']"/>";
							inHtml += "<td title='" + gubun + "'>&nbsp<img src='${image}/icon/icon_admin_13.gif' style='vertical-align:middle;'/></td>";							
						} else { // 01:Normal or else
							inHtml += "<td title='" + gubun + "'>&nbsp<img src='${image}/icon/icon_directory.png' style='vertical-align:middle;'/></td>";
						}
					}
					var classidname = bundle.classname;
					var classid = "";
					var classname = "";
					if(classidname){
						var names = classidname.split("：");
						classid = names[0];
						classname = names[1]; 
					}									
					inHtml += "<td title='"+bundle.classpath+"'>"+classname+"</td>";

					var summary = "";
					if (bundle.summary != null){
						summary = bundle.summary;
					} else {
					}					
					if(acls>1){
						var titles = bundle.title.split("：");
						if (bundle.files) {
							var title = "<b>" + titles[0].replace(/\'/g,"&#39;") + "</b>";
							// ftr검색결과
							title = title + "<br><font size=-1>" + bundle.files + "</font>";
						} else {
							var title = titles[0];						
						}
						var versionid = titles[1];						
						inHtml += "<td style='text-align:left;padding:5px;cursor:pointer' title='" + summary + "' onclick=docInfoView('" + bundle.contentid + "','" + versionid + "','"+bundle.contenttype+"','" + extname + "');>" + title + "</td>";
					}else{
						inHtml += "<td style='text-align:left;padding:5px;'>" + title + "</td>";
					}
					
					inHtml += "<td style='text-overflow:ellipsis;' title='"+bundle.createtime+"'>"+bundle.createtime+"</td>";
					if(objectIsEmpty(bundle.wfinf)){
					  inHtml += "<td></td>";
					  inHtml += "<td></td>";
					}else{
					  var wfInfos  = bundle.wfinf.split("_");
					  inHtml += "<td style='text-overflow:ellipsis;' title='"+wfInfos[0]+"'>"+wfInfos[0]+"</span></td>";
					  inHtml += "<td style='text-overflow:ellipsis;' title='"+wfInfos[1]+"'>"+wfInfos[1]+"</span></td>";
					}
					
					inHtml += "<td style='text-overflow:ellipsis;' title='"+bundle.reasons+"' >" + IsNull(bundle.reasons) + "</span></td>";
					// 사이드 메뉴
					inHtml += "<td><div class='tooltip_wrap'>";
					inHtml += "<button type='button'><img src='${image}/icon/Group 8.png'></button>";
			        inHtml += "<div class='ui_popup widePop'>"
			        inHtml += "<ul>";
			        inHtml += "<li><a href='#none' onclick=docInfoOpen('"+bundle.contentid+"')><img src='${image}/icon/icon_c08.png'>문서정보</a></li>";
			        inHtml += "</ul>";
			        inHtml += "</div>";
			        inHtml += "</div></td>";
					inHtml += "</tr>";
				}catch(e){
					console.log("error : ",e);
				}
			}
			$("#bundleList").html(inHtml);
			
			sideMenuClickEvent();
		}else{
			
			createDocEmptyList();
		}
		closeLayer();
	}

	var createShareDocList = function(data){
		createShareDocHeader();
		if(data.length>0){
			$("#bundleList").empty();
			var inHtml = "";
			var docStateLst = getDocStateList();
			var rightLst = rightList();
			for(var i=0;i<data.length;i++){
				try{
					var bundle = data[i];
					var zappSharedObject = bundle.zappSharedObject;
					var acls = 0;
					var islocked = bundle.islocked;
					try{
						acls = bundle.zappAcl.acls;
					}catch(e){acls = 0 }
					var contentno = bundle.contentno ? bundle.contentno : "";
					inHtml += "<tr id='"+bundle.contentid+"' data-meta='"+JSON.stringify(bundle)+"' class='contextmenu'>";
					
					// 체크박스
					inHtml += "<td><input type='checkbox' name='chkbox' id='chkbox"+i+"' value='"+bundle.contentid+"'><label for='chkbox"+i+"'></label></td>";
					
					var titles = bundle.title.split("：");
					var filename = titles[0];
					var extnames = filename.split(".");
					var extname = extnames[extnames.length-1].toLowerCase();
					var iconfile = "";
					if (extname == "hwp" || extname == "hwpx")
						iconfile = "icon_HWP.png";
					else if (extname == "pdf")
						iconfile = "icon_PDF.png";
					else if (extname == "png")
						iconfile = "icon_PNG.png";
					else if (extname == "txt")
						iconfile = "icon_TXT.png";
					else if (extname == "gif")
						iconfile = "icon_GIF.png";
					else if (extname == "tif")
						iconfile = "icon_TIF.png";
					else if (extname == "bmp")
						iconfile = "icon_BMP.png";
					else if (extname == "jpg" || extname == "jpeg")
						iconfile = "icon_JPG.png";
					else if (extname == "doc" || extname == "docx")
						iconfile = "icon_DOC.png";
					else if (extname == "xls" || extname == "xlsx")
						iconfile = "icon_XLS.png";
					else if (extname == "ppt" || extname == "pptx")
						iconfile = "icon_PPT.png";
					else 
						iconfile = "icon_ETC.png";

					// 파일/번들 구분
					var gubun = "<spring:eval expression="@${lang}['BUNDLE']"/>";
					if(bundle.contenttype != "01"){ // 파일
						gubun = "<spring:eval expression="@${lang}['FILE']"/>";
						inHtml += "<td title='" + gubun + "'>&nbsp<img src='${image}/icon/" + iconfile + "' style='vertical-align:middle;'/></td>";
					} else { // 번들
						if (bundle.ctype == "02") { // Virtual
							gubun = "<spring:eval expression="@${lang}['LINK']"/>";
							inHtml += "<td title='" + gubun + "'>&nbsp<img src='${image}/icon/icon_admin_13.gif' style='vertical-align:middle;'/></td>";							
						} else { // 01:Normal or else
							inHtml += "<td title='" + gubun + "'>&nbsp<img src='${image}/icon/icon_directory.png' style='vertical-align:middle;'/></td>";
						}
					}
					
					//권한명 지정
					var rightName = "";
					for(var j=0;j <rightLst.length; j++ ){
						var right = rightLst[j];
						if(right.codevalue == acls) {
							rightName = right.name; continue;
						}				
					}
					inHtml += "<td>"+rightName+"</td>";
					
					var classidname = bundle.classname;
					var classid = "";
					var classname = "";
					if(classidname){
						var names = classidname.split("：");
						classid = names[0];
						classname = names[1]; 
					}		
					
					//폴더명
					inHtml += "<td title='"+bundle.classpath+"' onclick=fn_FldClick('"+classid+"');>"+classname+"</td>";
					
					var titles = bundle.title.split("：");
					
					//문서제목
					var title = titles[0];
					var versionid = titles[1];
					if(acls == 0 && selectedClsid == "sharedby"){
						inHtml += "<td style='text-align:left;padding:5px;' onclick=docInfoView('" + bundle.contentid + "','" + versionid + "','"+bundle.contenttype+"','" + extname + "');>" + title + "</td>";
					}else if (acls > 1){
						inHtml += "<td style='text-align:left;padding:5px;' onclick=docInfoView('" + bundle.contentid + "','" + versionid + "','"+bundle.contenttype+"','" + extname + "');>" + title + "</td>";
					}else{
						inHtml += "<td style='text-align:left;padding:5px;'>" + title + "</td>";
					}
					
					//등록자
					inHtml += "<td>"+bundle.creatorname+"</td>";
					
					//등록 일시
					inHtml += "<td>"+bundle.createtime+"</td>";
					//inHtml += "<td>"+bundle.expiretime+"</td>";
					var state = "";
					for(var j=0;j <docStateLst.length; j++ ){
						var docState = docStateLst[j];
						if(docState.codevalue == bundle.state) {
							state = docState.name; break;
						}				
					}					
					// 상태
					inHtml += "<td>"+state+"</td>";
					
					// 사이드 메뉴
					inHtml += "<td><div class='tooltip_wrap'>";
					inHtml += "<button type='button'><img src='${image}/icon/Group 8.png'></button>";
			        inHtml += "<div class='ui_popup widePop'>"
			        inHtml += "<ul>";
			        inHtml += "<li><a href='#none' onclick=docInfoOpen('"+bundle.contentid+"')><img src='${image}/icon/icon_c08.png'>문서정보</a></li>";
			        inHtml += "<li><a href='#none' onclick=unShareDoc('"+bundle.contentid+"')><img src='${image}/icon/icon_c05.png'>공유해제</a></li>";
			        inHtml += "</ul>";
			        inHtml += "</div>";
			        inHtml += "</div></td>";
					inHtml += "</tr>";
				}catch(e){
					console.log("error : ",e);
				}
			}
			$("#bundleList").html(inHtml);
			
			sideMenuClickEvent();
			
		}else{
			createDocEmptyList();
		}
		closeLayer();
	}
	
	
	var createDocEmptyList = function(){
		$("#totCnt").text("<spring:eval expression="@${lang}['TOTAL']"/>"+" : 0");
		$("#bundleList").empty();
		var inHtml = "";
			inHtml += "<tr>";
			if(selectedClsid == "TRASH"){
				inHtml += "<td colspan='8'><spring:eval expression="@${msgLang}['NOTRASH_DOC']"/></td>";
			}else{
				inHtml += "<td colspan='8'><spring:eval expression="@${msgLang}['NOTEXIST_DOC']"/></td>";
			}
			
			inHtml += "</tr>";
		$("#bundleList").html(inHtml);	
		$("#pageing").empty();
		closeLayer();
	}
	
	var createShareDocEmptyList = function(){
		$("#totCnt").text("<spring:eval expression="@${lang}['TOTAL']"/>"+" : 0");
		$("#bundleList").empty();
		var inHtml = "";
			inHtml += "<tr>";
			inHtml += "<td colspan='8'><spring:eval expression="@${msgLang}['NOSHARED_DOC']"/></td>";
			inHtml += "</tr>";
		$("#bundleList").html(inHtml);	
		$("#pageing").empty();
		closeLayer();
	}
	
	
	var fldTypeName = {
			"01":docBox.general,
			"02":docBox.classification,
			"N1":docBox.enterprise,
			"N2":docBox.department,
			"N3":docBox.personal,
			"N4":docBox.collaborative}
	var createFavFldList = function(data){
		createFavFldHeader();
		if(data.length>0){
			$("#bundleList").empty();
			for(var i=0;i<data.length;i++){
				try{
					var bundle = data[i];
					var types = bundle.types;
					var name = bundle.name;
					var inHtml = "";
					var $tr = $("<tr id='"+bundle.classid+"' class='contextmenu'></tr>");					
					inHtml += "<td><input type='checkbox' name='chkbox' id='chkbox"+i+"' value='"+bundle.classid+"'><label for='chkbox"+i+"'></label></td>";
					inHtml += "<td>"+fldTypeName[types]+"</td>";
					inHtml += "<td style='text-align:left;padding:5px;'>"+name+"</td>";
					// 서브메뉴
					inHtml += "<td><div class='tooltip_wrap'>";
					inHtml += "<button type='button'><img src='${image}/icon/Group 8.png'></button>";
			        inHtml += "<div class='ui_popup widePop'>"
			        inHtml += "<ul>";
			        inHtml += "<li><a href='#none' onclick=unfavFld('"+bundle.classid+"')><img src='${image}/icon/icon_c08.png'>즐겨찾기해제</a></li>";
			        inHtml += "</ul>";
			        inHtml += "</div>";
			        inHtml += "</div></td>";
					inHtml += "</tr>";
					$tr.append(inHtml);
					$tr.data('meta', bundle);
					
					console.log("tr meta : ", $tr.data());
					
					//$tr.children().eq(1).on('click', function(e) {				
					$tr.on('click', function(e) {
						var data = $(this).data('meta');
						console.log("fav fld data :", data);
						var classid = data.classid;
						var types = data.types;
						
						console.log("types : " + types);
						console.log("classid : " + classid);
						if(types=="02"){
							//문서분류탭
							fn_ClsClick(classid);
						}else{
							//문서함탭
							console.log("fn_FldClick");
							if(types=="N2"){
								classid = "M:"+classid;
							}
							fn_FldClick(classid);
						}						
					});
					$("#bundleList").append($tr);
					sideMenuClickEvent();
				}catch(e){
					console.log("error : ",e);
				}
			}
		}else{
			createFavFldEmptyList();
		}		
		closeLayer();
	}
	
	var fn_ClsClick = function(classid){
		$("#Btn_Env1").trigger("click");
		fn_MainClsTree.clickNode(classid);
		var duration = setTimeout(function(){
			fn_MainClsTree.clickNode(classid);
		},200);
	}
	
	var fn_FldClick = function(classid){
		
		console.log("classid : " + classid);
		
		var node = fn_fldTree.getNode(classid);
		console.log("node : " + node);
		if(node){
			fn_fldTree.clickNode(classid);
			var duration = setTimeout(function(){
				fn_fldTree.clickNode(classid);
			},200);
		}
	}
	
	
	var createFavFldEmptyList = function(){
		$("#totCnt").text("<spring:eval expression="@${lang}['FAVORITE']"/>"+" : 0");
		$("#bundleList").empty();
		var inHtml = "";
			inHtml += "<tr>";
			inHtml += "<td colspan='3'><spring:eval expression="@${msgLang}['NOTEXISTFAVORITE']"/></td>";
			inHtml += "</tr>";
		$("#bundleList").html(inHtml);	
		$("#pageing").empty();
	}
	
    var folderRegOpen = function(){
    	
    	console.log("selectedClsid : " + selectedClsid);
		if(selectedClsid == '' || selectedClsid == 'undefined'){
    		alert("상위 문서함을 선택해 주세요");
    		return;
    	}else{
    		$("#openPop").empty();
           	$("#openPop").load("${ctxRoot}/go/folderReg?type="+selectedClsType); 
           	fn_openPop("openPop");
    	}
    }
    var folderRegEdit = function(){	 
    	if(selectedClsid == '' || selectedClsid == 'undefined'){
    		alert("수정할 문서함을 선택해 주세요");
    		return;
    	}else{
	    	$("#openPop").empty();
	     	$("#openPop").load("${ctxRoot}/go/folderEdit?type="+selectedClsType);  
	     	fn_openPop("openPop");
    	}
	}

	//컨텍스트 메뉴에서는 contentid가 입력됨
	var linkRegOpen = function(contentid){	
		var data = {};
		data.objIsTest = "N";
		data.objDebugged = false;
		data.objTaskid = taskid;
		data.objType = "01"; //해당 문서의 타입이 필요			 
		var vDocList = [];
		if(!contentid){
			$("input[name='chkbox']:checked").each(function(){
				contentid = $(this).val();
				
				var dataMeta = JSON.parse($("#" + contentid).attr("data-meta"));
				var title = dataMeta.title.split("：")[0];

				if (dataMeta.contenttype == "01" && dataMeta.ctype == "02") { //가상문서
					alert("가상문서는 가상문서로 재등록할 수 없습니다.");
				} else {
					vDocList.push(title + "：" + contentid + "：" + dataMeta.contenttype + "：" + dataMeta.filesize);					
				}
			});
		}
		if(vDocList.length>0){
			var callback = {};
				callback.param = {vDocList};
				callback.data = data;
				callback.func = vDocList;
				callback.gubun = "VIRTUALDOC";
				
			// virtual doc reg popup
			$("#openPop").load("${ctxRoot}/go/linkReg", function(){
				//레이어로 데이터와 이벤트를 전달한다.
		 	   	//$('#btnClsInfoOk').val('<spring:eval expression="@${lang}['MOVE']" />');
			});
			fn_openPop("openPop");//레이어 보이기
			lsCallBack = callback;
		}else{
			alert("<spring:eval expression="@${msgLang}['SELECT_VIRTUAL_DOC']"/>");
		}
	}
	
	var docInfoOpen = function(contentid){
		selectedContentid = contentid;
		var content = $("#"+contentid).data("meta");
		var acls = 0;
		var islocked = content.islocked;
		var lockername = content.lockername;
		try{
			acls = content.zappAcl.acls;
		}catch(e){acls = 0 }
		
		console.log("docInfoOpen content.state : " + content.state);
		console.log("docInfoOpen acls : " + acls);
		
		
		//삭제 대기 상태일때는 목록만 보여준다
		if (content.state == "01"){
			alert("상태가 정상인 문서만 조회가 가능합니다.");
		  	return;
		} else if (content.state == "A0"){ //변경 승인대기
			if (content.contenttype == "01") { //번들
				if (content.ctype == "02") { // Virtual
					linkViewOpen(contentid);
				} else { // 01:Normal, or else
					docEditOpen(contentid);
				}
			} else {
				docViewOpen(contentid);
			}
		} else {
			//acls 권한 확인이 필요하다 추후 변경시에 변경 
			if (acls==5 && islocked == "YS"){ //편집권한이 있고 반출상태 / 본인 반출
				if (content.ctype == "02") { // Virtual
					linkEditOpen(contentid);					
				} else { // 01:Normal or else
					docEditOpen(contentid);
				}
			} else if (acls==5&&islocked == "N"){ // 반출상태 X
				console.log("acls==5 && islocked == N");
				if (content.ctype == "02") { // Virtual
					linkEditOpen(contentid);					
				} else { //01:Normal or else
					docEditOpen(contentid);
				}			
			} else {
				if (acls>=2){
					console.log("acls>=2");
					if (content.ctype == "02") { // Virtual
						linkViewOpen(contentid);
					} else { // 01:Normal or else
						docViewOpen(contentid);
					}				
				}
			}	
		}
	}
	
	var fileDownload = function(versionid) {
		var link = document.createElement("a");
		document.getElementById("filedown").appendChild(link);
		link.href = "${ctxRoot}/api/file/fileDown/" + versionid;
		link.click();
	}
	
	var pdfDownload = function(versionid) {
		// PDF 다운로드
		var link = document.createElement("a");
		//link.download = item.filename;
		link.href = "${ctxRoot}/api/file/office2pdfDown/" + versionid;
		link.click();		
	}
	
	var pdfConvert = function (versionid) {
		var data = {};
		data.objIsTest = "N";
		data.objTaskid = taskid;
		data.objViewtype = "01";//01:조회용,02:편집용
		
		$.ajax({
			url : "${ctxRoot}/api/file/pdfConvertByPath/" + versionid,
			type : "GET",
			contentType : 'application/json',
			async : false,
			success : function(data) {
				if (data.status == "0000") {
					docData = data.result;
					var docState = docData.state;
					// 문서기본정보 셋팅
					MainsetDocInfo(docData);
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
	
	var officePdfView = function(versionid) {
	   	
        //var url = "${ctxRoot}/api/file/officePdfConvert/" + versionid;
        var url = "${ctxRoot}/api/file/pdfdown/" + versionid;
        var xhr = new XMLHttpRequest();
        xhr.open('GET', url);
        xhr.responseType = 'blob';
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.onload = function () {
            var blob = xhr.response;
            var pdfview = document.getElementById('PdfView'); // PDFVIEW 영역
            BlobUrl = URL.createObjectURL(blob);
            
            var popupWidth = 800;
			var popupHeight = 900;
			var popupX = (window.screen.width / 2) - (popupWidth / 2);
			var popupY= (window.screen.height / 2) - (popupHeight / 2);			
    	   	var pdfWin = window.open("${ctxRoot}/go/pdfView", "PDF Viewer", 'status=no, height=' + popupHeight  + ', width=' + popupWidth  + ', left='+ popupX + ', top='+ popupY +' toolbar=no, menubar=no, scrollbars=no, resizable=no' );
    	   	var testValue = 22;
    	   	
    	   	setTimeout(function () { openNewTab(testValue, pdfWin); }, 1000);
    	   	
    	   	function openNewTab(testValue, vWindow) { 
    	   		var test = pdfWin.document.body; // 팝업창의 html 전체 
    	   		console.log("test : " + test);
    	   		if (test == null) { 
    	   		} else { 
					console.log("BlobUrl : " + BlobUrl);    	   			
            		pdfWin.FileOpen(BlobUrl, 1); // PDF뷰어 파일오픈 함수 호출
    	   		} 
    	   	}
        }
        xhr.onerror = function () {
            console.error('could not download file');
        }
        xhr.send();
	}

	var hwpPdfView = function(versionid) {
        var url = "${ctxRoot}/api/file/hwpPdfConvert/" + versionid;
        var xhr = new XMLHttpRequest();
        xhr.open('GET', url);
        xhr.responseType = 'blob';
        xhr.setRequestHeader('Content-Type', 'application/json');
        xhr.onload = function () {
            var blob = xhr.response;
            var pdfview = document.getElementById('PdfView'); // PDFVIEW 영역
            BlobUrl = URL.createObjectURL(blob);
            
    	   	var pdfWin = window.open("${ctxRoot}/go/pdfView", "PDF Viewer", "width=800, height=700, toolbar=no, menubar=no, scrollbars=no, resizable=yes" );
    	   	var testValue = 22;
    	   	
    	   	setTimeout(function () { openNewTab(testValue, pdfWin); }, 500);
    	   	
    	   	function openNewTab(testValue, vWindow) { 
    	   		var test = pdfWin.document.body; // 팝업창의 html 전체 
    	   		if (test == null) { 
    	   		} else { 
            		pdfWin.FileOpen(BlobUrl, 1); // PDF뷰어 파일오픈 함수 호출
    	   		} 
    	   	}
        }
        xhr.onerror = function () {
            console.error('could not download file');
        }
        xhr.send();
	}

	var docInfoView = function(contentid, versionid, contenttype, extname) {
		
		var data = {};
		data.objIsTest = "N";
		data.objTaskid = taskid;
		data.objType = contenttype;//01:bundle,02:file
		data.objViewtype = "01";//01:조회용,02:편집용
		data.contentid = contentid;
		$.ajax({
			url : "${ctxRoot}/api/content/view",
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(data),
			success : function(data) {
				console.log("=== docInfoView return: ", data);
				if (data.status == "0000") {
					docData = data.result;
					var docState = docData.state;
					// 문서기본정보 셋팅
					MainsetDocInfo(docData,contenttype);
				}
			},
			error : function(request, status, error) {
				//alertNoty(request, status, error);
			},
			beforeSend : function() {
			},
			complete : function() {
			}
		});
	}

	var MainsetDocInfo = function(data, contenttype) {
		var filenames = data.title.split("：");
		var extnames = filenames[0].split(".");
		var extname = extnames[extnames.length-1];
		extname = extname ? extname.toLowerCase() : 'etc';
		var versionid = data.zappFiles[0].zArchVersion.versionid;
		var fldName = data.classpath.split("：")[0];
		
		$("#bundleInfo").empty();
		
		var html = "<ul style='width: 170px;'>";
		
		//썸네일
		html += "<li>";
		html += "<img id=img"+versionid+" width='170px' height='170px' style='cursor:pointer;'>";		
		html +="</li>"
		
		// 폴더명
		html += "<li style='list-style-type: disclosure-closed; margin-left:20px;'>";
		html +="<h3>[폴더명]</h3>";
		html +="<p style='margin-left:-20px;'>"+fldName+"</p>"
		html +="</li>";
		
		// 문서번호
		html += "<li style='list-style-type: disclosure-closed; margin-left:20px;'>";
		html +="<h3>[문서번호]</h3>";
		html +="<p>"+data.contentno+"</p>"
		html +="</li>";
		
		// 파일명
		var filename = data.title.split("：")[0];
		html += "<li style='list-style-type: disclosure-closed; margin-left:20px;'>";
		html +="<h3>[문서제목]</h3>";
		html +="<p style='word-break: break-all;'>"+filename+"</p>";
		html +="</li>";
		
		// 버전
		html += "<li style='list-style-type: disclosure-closed; margin-left:20px;'>";
		html +="<h3>[버전]</h3>";
		html +="<p>"+data.version+"</p>"
		html +="</li>";
		
		// 파일 사이즈
		var filesize = data.filesize;
		if(filesize >= Math.pow(1024, 1) && filesize < Math.pow(1024, 2)){
			filesize = parseInt(data.filesize / 1024).toLocaleString('ko-KR') + " KB";
		}else if(filesize >= Math.pow(1024, 2) && filesize < Math.pow(1024, 3)){
			filesize = parseInt(data.filesize / Math.pow(1024, 2)).toLocaleString('ko-KR') + " MB";
		}else if(filesize >= Math.pow(1024, 3)){
			filesize = parseInt(data.filesize / Math.pow(1024, 3)).toLocaleString('ko-KR') + " GB";
		}else if(filesize < 1024){
			filesize += " Bytes";
		}
		
		html += "<li style='list-style-type: disclosure-closed; margin-left:20px;'>";
		html +="<h3>[파일사이즈]</h3>";
		html +="<p>"+filesize+"</p>"
		html +="</li>";
		
		// 등록일자
		html += "<li style='list-style-type: disclosure-closed; margin-left:20px;'>";
		html +="<h3>[등록일자]</h3>";
		html +="<p>"+data.createtime+"</p>"
		html +="</li>";
		
		// 등록자
		html += "<li style='list-style-type: disclosure-closed; margin-left:20px;'>";
		html +="<h3>[등록자]</h3>";
		html +="<p>"+data.creatorname+"</p>"
		html +="</li>";

		// 보존기간 목록 조회후 표시
		html += retentionList(data.retentionid, data.expiretime);
		
		// 코멘트 목록 조회후 표시;
		html += MainCommentList(data.contentid, data.contenttype);
		html += "</ul>";
		
		$("#bundleInfo").append(html);
		
		var officefile = "hwp,hwpx,pdf,doc,docx,xls,xlsx,ppt,pptx,txt";
		var imgfile = "png,jpg,jpeg,gif,bmp";
		var txtfile = "txt";
		var isOfficeFile = !!officefile.match(extname);
		var isImgFile = !!imgfile.match(extname);
		var isTxtFile = !!txtfile.match(extname);
		var previewUrl = "";
		var $preViewImg = $("#img"+versionid);
		//파일 종류에 따른 URL 및 클릭 이벤트 부여 분기
		
		console.log("isOfficeFile : " + isOfficeFile);
		console.log("isImgFile : " + isImgFile);
		
		if(isOfficeFile){				
			//previewUrl = "${ctxRoot}/api/file/officePdfConvertWithThumb/" + versionid ;
			previewUrl = "${ctxRoot}/api/file/convertOfficeThumb/" + versionid ;
			$preViewImg.bind('click', function(e) {officePdfView(versionid);});
		//} else if(isTxtFile) {//txt 파일 변환서버 libreoffice 변환 모듈 ?? 				
		//	previewUrl = "${ctxRoot}/api/file/thumbView/" + versionid ;
		//	$preViewImg.bind('click', function(e) {officePdfView(versionid);});
		} else if(isImgFile) {			
			//previewUrl = "${ctxRoot}/api/file/thumbView/" + versionid ;
			previewUrl = "${ctxRoot}/api/file/fileDown/" + versionid ;
			$preViewImg.bind('click', function(e) {fileDownload(versionid);});
		} else{
			previewUrl = "${image}/visual/img_404.jpg" ;//변환 못하는 걸 알수 있는 이미지로 대체 필요 
			$preViewImg.bind('click', function(e) {fileDownload(versionid);});
		}
		
		//@TODO 변환할수 없는 파일일경우 에러 이미지를 세팅하고 preView를 호출하지 않는다.
		var disableFile = "erwin";//변환 불가 파일 추가
		var disable = !!disableFile.match(extname);
		
		if(disable){
			$preViewImg.attr("src", "${image}/visual/img_404.jpg");			
		}else{	
			$preViewImg.attr("src", "${ctxRoot}/resources/images/visual/loading_admin.gif");
			preView(versionid,previewUrl);			
		}		
	}
	
	//문서 preview
	var preView = function(versionid,url){
		var $preViewImg = $("#img"+versionid);
		var xhr = new XMLHttpRequest;	
		xhr.open('GET', url, true);
		xhr.responseType = "arraybuffer";
		xhr.send(null);
		xhr.onload = function () {	
			if (xhr.status == 200 || xhr.status == 201) {
				var array = new Uint8Array(xhr.response);
			    var bb = new Blob([array]);
				var url = URL.createObjectURL(bb);			
				$preViewImg.attr("src", url);
			}else{
				//변환 실패시 이미지 주소및 클릭 이벤트를 변경한다.(뷰어를 오픈하지 않고 다운로드 이벤트로 변경)
				$preViewImg.attr("src", "${image}/visual/img_404.jpg");
				$preViewImg.bind('click', function(e) {fileDownload(versionid);});
			}
		}		
		//다운로드 에러일경우
		xhr.onerror = function (err) {
			$preViewImg.attr("src", "${image}/visual/img_404.jpg");
			$preViewImg.bind('click', function(e) {fileDownload(versionid);});
		}
		//변환된 이미지가 정상이 아닐경우
		$preViewImg.bind({
			error : function(){	
				$preViewImg.attr("src", "${image}/visual/img_404.jpg");
				$preViewImg.bind('click', function(e) {fileDownload(versionid);});
			}
		});
	}
	
	// 보존년한 목록 조회
	var retentionList = function(retentionid, expiretime) {
		var data = {};
		var html = "";
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
					$.each(data.result, function(idx, result) {
						var codeid = result.codeid;
						var name = result.name;
						if (codeid == retentionid) {
							html += "<li style='list-style-type: disclosure-closed; margin-left:20px;'>";
							html +="<h3>[보존기간]</h3>";
							html +="<p>"+name + "</p>";
							html +="(만료일자: " + expiretime + ")</p>";
							html +="</li>";
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
		return html;
	}
	
	// 코멘트 리스트
	var MainCommentList = function(contentid, contenttype){
		console.log("MainCommentList");
		var data = {};
		var html = "";
		data.objIsTest = "N";
		data.cobjid = contentid;
		data.cobjtype = contenttype;
		data.objmaporder = {"commenttime":"desc"};
		$.ajax({ 
			url : "${ctxRoot}/api/content/comment/list",
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){		
				console.log("cmt list data : ", data);
				html += "<li style='list-style-type: disclosure-closed; margin-left:20px;'>";
				html +="<h3>[코멘트]</h3>";
				if(data.result.length == 0 ){
					html += "<p>없    음</p>";
				}else{
					$.each(data.result, function(index, item) {
						if(index < 3){
							html += "<p>"+item.comments+"</p>";
						}
					});
				}
			}, 
			error : function(request, status, error) {
				
			}
		});
		return html;
	}
	
	var docEditOpen = function(contentid){
		selectedContentid = contentid;
		$("#openPop").empty();
	   	$("#openPop").load("${ctxRoot}/go/fileEdit"); 
	   	fn_openPop("openPop");
	}

	var linkEditOpen = function(contentid){
		selectedContentid = contentid;
		$("#openPop").empty();
	   	$("#openPop").load("${ctxRoot}/go/linkEdit");
	   	fn_openPop("openPop");
	}
	
	var docViewOpen = function(contentid){
		selectedContentid = contentid;
	   	$("#openPop").load("${ctxRoot}/go/fileInfo"); 
	   	fn_openPop("openPop");
	}

	var linkViewOpen = function(contentid){
		selectedContentid = contentid;
	   	$("#openPop").load("${ctxRoot}/go/linkInfo"); 
	   	fn_openPop("openPop");
	}
	
	var docSharePop = function(contentid){
		var shareList = [];
		var LockCnt = 0; // 잠김 또는 다른 상태 카운트
		if(!contentid){
			$("input[name='chkbox']:checked").each(function(){
				contentid = $(this).val();
				console.log("contentid : " + contentid);
				var DocState = $("#"+contentid).data('meta').state; // 문서의 상태 확인
				console.log("DocState : " + DocState);
				if(DocState == "00"){								// 문서가 정상일 경우 공유 문서 리스트에 push
					shareList.push(contentid);	
				}else if(DocState != "00"){												// 문서가 정상이 아닐 경우 체크 해제
					$(this).prop('checked',false);
					LockCnt++;
				}
			});
		}else{
			shareList.push(contentid);
		}
		
		if(LockCnt > 0 && shareList.length == 0){
			alert("잠금 문서는 공유 할 수 없습니다.");
		}else if((LockCnt > 0 && shareList.length > 0) || (LockCnt == 0 && shareList.length > 0)){
				var data = {};
				data.objIsTest = "N";
				data.objDebugged = false;
				data.contentid = contentid;
				data.zappSharedObjects = []; //공유할 대상 지정
				
				var callback = {};
				callback.param = {list:shareList};
				callback.data = data;
				callback.func = shareDocList;
				callback.gubun = "SHARE";
				console.log("callback.data : ", callback.data);
				$("#openPop").load("${ctxRoot}/go/authPop",function(){
					//레이어로 데이터와 이벤트를 전달한다.
					shareCallBack = callback; 
					fn_getSharedList();
				});
				$('.bg').fadeIn();
				fn_openPop("openPop");
				
		}else if(LockCnt == 0 && shareList.length == 0){
			alert("<spring:eval expression="@${msgLang}['SELECT_SHARE_DOC']"/>");
		}
	}
	
	var shareDocList = function(data,contentList){
		var Scnt = 0;
		var errmassege = "";
		$.each(contentList,function(index,contentid){
			var rtn = shareDoc(contentid,data);
			
			if(rtn == 1){
				Scnt++;
			}else{
				errmassege = rtn;
			}
			
		});
		
		if(contentList.length == Scnt){
			alert("<spring:eval expression="@${msgLang}['IT_SHARED']"/>");	
		}else{
			alert(errmassege);
		}
		sharePopClose();	
	}
	
	var sharePopClose = function(){
		$("#shareRegLayer").empty();
		$("#shareRegLayer").hide();
	    $(".modalLayer:last-of-type").remove();
	    modalZIndex = 100;
	}
	
	
	var shareDoc = function(id,data){
		var meta = $("#"+id).data("meta");
		data.contentid = id;	
		data.objType = meta.contenttype;//00:분류(폴더),01:번들,02:파일
		var rtnVal =0;
		$.ajax({ 
			url : "${ctxRoot}/api/content/share" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){
				 if(data.status == "0000"){
					 rtnVal = 1;
				  }else{
					 rtnVal = data.message;
				  }
			}, 
			error : function(request, status, error) {
        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		});
		return rtnVal;
	}
	
	var unShareDoc = function(id){
		var content = $("#"+id).data("meta");
		var unShare = {};
			unShare.shareobjid = content.zappSharedObject.shareobjid;
			unShare.objAction ="DISCARD";
		var data = {};
		data.objIsTest = "N";
		data.objDebugged = false;
		data.contentid = content.contentid;
		data.objType = content.contenttype;
		data.zappSharedObjects = [unShare];
		
		$.ajax({ 
			url : "${ctxRoot}/api/content/changeshare" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){
				if(data.status == "0000"){
					alert("<spring:eval expression="@${msgLang}['OPERATION_IS_COMPLETED']"/>");
					sharelistSearch();
				  }else{
					  alertErr(data.message);
				  }
			}, 
			error : function(request, status, error) {
        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {
				
			} 
		});	
	}
	
	var copyDocPop = function(contentid){
		
		var classList = [];
		var orgClsId = {};
		orgClsId.classid = selectNodeId(selectedClsid);
		classList.push(orgClsId);
		
		var data = {};
		data.objIsTest = "N";
		data.objDebugged = false;
		data.objTaskid = taskid;
		data.objType = "01"; //해당 문서의 타입이 필요					
		data.zappClassObjects = classList;
	  	var LockCnt =0;
		var copyList = [];
		if(!contentid){
			$("input[name='chkbox']:checked").each(function(){
				contentid = $(this).val();
				//삭제 불가 권한 체크
				var DocState = $("#"+contentid).data('meta').state; // 문서의 상태 확인
				if(DocState == "00"){								// 문서가 정상일 경우 공유 문서 리스트에 push
					copyList.push(contentid);
				}else{												// 문서가 정상이 아닐 경우 체크 해제
					console.log("State not 00 ");
					$(this).prop('checked',false);
					LockCnt++;
				}
			});	
		}else{
			copyList.push(contentid);
		}
		
		if(LockCnt == 1 && copyList.length == 0){
			alert("정상 문서가 아니기에 복사 할 수 없습니다.");
		} else if((LockCnt > 0 && copyList.length>0) || (LockCnt == 0 && copyList.length>0)){
			alert("잠금 문서를 제외하고 복사 진행 합니다.");
			var callback = {};
			callback.param = {list:copyList};
			callback.data = data;
			callback.func = copyDocList;
			callback.gubun = "COPY";
				
			$("#openPop").load("${ctxRoot}/go/fldTreePop",function(){
				//레이어로 데이터와 이벤트를 전달한다.
				lsCallBack = callback;
				if(lsCallBack.gubun == "MOVE"){
					$(".pageTit").text("문서이동");
				}else if(lsCallBack.gubun == "COPY"){
					$(".pageTit").text("문서복사");
				}
			});
			$(".bg").fadeIn();
			fn_openFldPop("openPop");
		}else if(LockCnt == 0 && copyList.length == 0){
			alert("<spring:eval expression="@${msgLang}['SELECT_COPY_DOC']"/>");
		}
	};

	//컨텍스트 메뉴에서는 contentid가 입력됨
	var moveDocPop = function(contentid){
		var objClass ={classid: selectNodeId(selectedClsid)}
	  
		var data = {};
			data.objIsTest = "N";
			data.objDebugged = false;
			data.objTaskid = taskid;
			data.objType = "01"; //해당 문서의 타입이 필요			 
			data.zappClassObjects = [objClass];
			
		var moveList = [];
		if(!contentid){
			$("input[name='chkbox']:checked").each(function(){
				contentid = $(this).val();
				//삭제 불가 권한 체크
				moveList.push(contentid);
			});
		}else{
			moveList.push(contentid);
		}
		
		if(moveList.length>0){
			var callback = {};
				callback.param = {list:moveList};
				callback.data = data;
				callback.func = moveDocList;
				callback.gubun = "MOVE";
			// docmove	
			$("#openPop").load("${ctxRoot}/go/fldTreePop",function(){
				//레이어로 데이터와 이벤트를 전달한다.
				lsCallBack = callback;
		 	   $('#btnClsInfoOk').val('<spring:eval expression="@${lang}['MOVE']" />');
		 	  if(lsCallBack.gubun == "MOVE"){
					$(".pageTit").text("문서이동");
				}else if(lsCallBack.gubun == "COPY"){
					$(".pageTit").text("문서복사");
				}
			});
			$(".bg").fadeIn();
			fn_openFldPop("openPop");//레이어 보이기
		}else{
			alert("<spring:eval expression="@${msgLang}['SELECT_MOVE_DOC']"/>");
		}
	};
	
	var CmtRegOpen = function(contentid){
		var content = $("#"+contentid).data("meta");
		
		var data = {};
			data.objIsTest = "N";
			data.cobjid = contentid;
			data.cobjtype = content.contenttype;
			data.comments = "";
			
		var callback = {};
			callback.data = data;
			callback.func = CmtReg;
			
		// docmove	
		$("#openPop").load("${ctxRoot}/go/cmtRegPop",function(){
			//레이어로 데이터와 이벤트를 전달한다.
			CmtRegCallBack = callback;
		});
		fn_openPop("openPop");//레이어 보이기
	};
	
	var CmtReg = function(data){
		$.ajax({ 
			url : "${ctxRoot}/api/content/comment/add",
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){		
				$('.bg').fadeOut();
				$('.popup').fadeOut();
			}, 
			error : function(request, status, error) {
				
			}
		});	
	}
	
	var reasonPop = function(type){
	  selectReasonType = type;
		var reason = {reason:""};
		var data = {};
			data.objIsTest = "N";
			data.objDebugged = false;
			data.contentid = taskid;
			data.zappLockedObject = reason; //승인사유
			
		var reasonList = [];
		$("input[name='chkbox']:checked").each(function(){
			contentid = $(this).val();
			//삭제 불가 권한 체크
			reasonList.push(contentid);
		});	
		
		if(reasonList.length>0){
			var callback = {};
				callback.param = {list:reasonList};
				callback.data = data;
				callback.type=type;
				callback.func = reasonDocList;
				
			 $("#openPop").load("${ctxRoot}/go/reasonReg",function(){
				//레이어로 데이터와 이벤트를 전달한다.
				lsCallBack = callback;
				//console.log("=====lsCallBack : ",lsCallBack);
				
				
			});
			 fn_openPop("openPop");
		}else{
		  if(type == "approve"){
		    alert("<spring:eval expression="@${msgLang}['SELECT_APPROVE_DOC']"/>");
		  }else{
		    alert("<spring:eval expression="@${msgLang}['SELECT_RETURN_DOC']"/>");
		  }
			
		}
	};
	var reasonDocList = function(data,contentList,type){
		$.each(contentList,function(index,contentid){
		  reasonDoc(contentid,data,type);
		});
		$('.bg').fadeOut();
		$('.popup').fadeOut();
		fn_Job_Tree.clickNode(selectedClsid);
	}
	
	var reasonDoc = function(id,data,type){
		var meta = $("#"+id).data("meta");
		data.contentid = id;	
		data.objType=meta.contenttype;
		console.log("====id : ",id);
		console.log("====data : ",data);
		console.log("====type : ",type);
		$.ajax({ 
			url : "${ctxRoot}/api/content/"+type ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){		
			  if(data.status == "0000"){
			    if(type == "approve"){
			    	alert("문서를 승인 했습니다.");
			    }else if(type == "return"){
			    	alert("문서를 반려 했습니다.");
			    }
				  
			  }else{
				  alertErr(data.message);
			  }
			
			}, 
			error : function(request, status, error) {
        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		});
	}
	var approvalPopClose = function(){
		 $("#approvalPop").empty();
		 $("#approvalPop").hide();
	    $(".modalLayer:last-of-type").remove();
	    modalZIndex = 100;
	}
	var fldTreePopClose = function(){
		 $("#fldTreePop").empty();
		 $("#fldTreePop").hide();
		 $("#fldTreePopClose").hide();
	    $(".modalLayer:last-of-type").remove();
	    modalZIndex = 100;
	}
	//common paging
	var createPageNavi = function (totalCnt, curPage, pageSize) {

		var pageCnt = pagecnt;
		var str = "";
		var numberOfBlock   = 5;
		var block       = Math.ceil(curPage / numberOfBlock);
		var startPage   = (block - 1) * numberOfBlock + 1;
		var endPage     = startPage + numberOfBlock - 1;
		var totalPage   = Math.ceil(totalCnt / pageCnt);
		if (endPage > totalPage) endPage = totalPage;
		var totalBlock  = Math.ceil(totalCnt / (numberOfBlock*pageCnt));
		
		var prevIndex = (block-1)*numberOfBlock;
		var nextIndex = block*numberOfBlock+1;
		str +="<ul>";
		//totalCnt -> totalBlock로 변경
		if(totalBlock>0){	
			if(block > 1){
				str += "<li><a href=\"javascript:pageLink"+"(1);\" class = 'pagBtn pprev'><img src='${image}/icon/double_arrow.png'></a></li>";
				str += "<li><a href=\"javascript:pageLink"+"("+prevIndex+");\" class = 'pagBtn prev'><img src='${image}/icon/next_arrow.png'></a></li>";
			}
		
			for(var i = startPage; i <= endPage ; i++) {
				if(i == curPage) {
					str += "<li><a href=\"#\" class = 'numBtn'>"+i+"&nbsp;</a>";
				} else {
					str += "<li><a href=\"javascript:pageLink"+"("+i+");\" class = 'pagb num'>"+i+"&nbsp;</a>";
				}
			}
		
			if((block != totalBlock) && totalBlock >0){	
				str += "<li><a href=\"javascript:pageLink"+"("+nextIndex+");\" class = 'pagBtn next'><img src='${image}/icon/next_arrow.png'></a></li>";
				str += "<li><a href=\"javascript:pageLink"+"("+totalPage+");\" class = 'pagBtn nnext'><img src='${image}/icon/double_arrow.png'></a></a></li>";
			}
		}
		str+="</ul>"
		$("#pageing").empty();
		$("#pageing").html(str);
	}
	
	//페이징 이동
	var pageLink = function (page) {
	    $("#page").val(page);
	    listSearch(page);
	}
	
		
	//선택된 삭제
	var delDocList = function(){
		var deldocListCnt = $("input[name='chkbox']:checked").length;
		var delSCnt = 0;
		var errmessage = "";
		if($("input[name='chkbox']:checked").length > 0){
			$("input[name='chkbox']:checked").each(function(){
				var contentid = $(this).val();
				//삭제 불가 권한 체크
				var rtn = delDoc(contentid);
				if(rtn == 1){
					delSCnt++;
				}else{
					errmessage = rtn;
				}
			});
			if(deldocListCnt == delSCnt){
				alert("<spring:eval expression="@${msgLang}['DEL_DOC_SUCCEEDED']" />");
			}else{
				alert(errmessage);
			}
			listSearch();
		}
	}
	var delDoc = function(contentid){
		var meta = $("#"+contentid).data("meta");
		var data = {};
			data.objIsTest = "N";
			data.objDebugged = false;
			data.objTaskid = taskid;
			data.objType = meta.contenttype;
			data.contentid = contentid;
			data.zappClassObject = { "classid" : selectNodeId(selectedClsid) };
		  var rtnVal = 0;
			//console.log("delDoc : ",data);
		$.ajax({ 
			url : "${ctxRoot}/api/content/disable" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){	
			  console.log("disable : ",data);
			  if(data.status == "0000"){
				 rtnVal = 1;
			  }else{
				 rtnVal = data.message;
			  }
			}, 
			error : function(request, status, error) {
        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		});
		return rtnVal;
	}
	
	var delDocOne = function(contentid){
		if(!confirm("문서를 삭제하시겠습니까?")){
			return;
		}else{
			var meta = $("#"+contentid).data("meta");
			var data = {};
				data.objIsTest = "N";
				data.objDebugged = false;
				data.objTaskid = taskid;
				data.objType = meta.contenttype;
				data.contentid = contentid;
				data.zappClassObject = { "classid" : selectNodeId(selectedClsid) };
			$.ajax({ 
				url : "${ctxRoot}/api/content/disable" ,
				type : "POST" , 
				dataType : 'json',
				contentType : 'application/json',
				async : false , 
				data : JSON.stringify(data) , 
				success : function(data){
					listSearch();
				}, 
				error : function(request, status, error) {
	        	alertNoty(request,status,error);
				} , 
				beforeSend : function() {} , 
				complete : function() {} 
			});
		}
	}
	
	function DelContextSelect(key){
		if(key != null){
			noty({
			    layout : "center",
			    text : "<spring:eval expression="@${msgLang}['ARE_YOU_DEL_DOC']"/>",
			    buttons : [ {
			      addClass : 'btbase',
			      text : "Ok",
			      onClick : function($noty) {
			        $noty.close();
			        delDoc(key);
			        listSearch();
			      }
			    }, {
			      addClass : 'btbase',
			      text : "Cancel",
			      onClick : function($noty) {
			        $noty.close();
			      }
			    } ],
			    type : "information",
			    killer : true
			  });
		} else {
			alert("<spring:eval expression="@${msgLang}['SELECT_DELETE_DOC']"/>");
		}
    };
    
    
	//선택된 파일 복원(휴지통에서)
	var cancelDelList = function(){
		var cancelDelListCnt = $("input[name='chkbox']:checked").length;
		var SCnt = 0;
		$("input[name='chkbox']:checked").each(function(){
			var contentid = $(this).val();
			//삭제 불가 권한 체크
			var rtn = cancelDelDoc(contentid);
			if(rtn == 1){
				SCnt++;
			}
		});
		if(cancelDelListCnt == SCnt){
			alert("<spring:eval expression="@${msgLang}['RESTORE_DOC_SUCCEEDED']" />");
		}else{
			
		}
		listSearch();
	}
	
	var cancelDelDoc = function(contentid){
		var meta = $("#"+contentid).data("meta");
		var rtnVal = 0;
		
		var data = {};
			data.objIsTest = "N";
			data.objDebugged = false;
			data.objTaskid = taskid;
			data.objType = meta.contenttype;
			data.contentid = contentid;
		$.ajax({ 
			url : "${ctxRoot}/api/content/enable" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){
				rtnVal = 1;
			}, 
			error : function(request, status, error) {
        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		});
		return rtnVal;
	}
	
	var cancelDelDocOne = function(contentid){
		if(!confirm("문서를 복구하시겠습니까?")){
			return;
		}else{
			var meta = $("#"+contentid).data("meta");
			
			var data = {};
				data.objIsTest = "N";
				data.objDebugged = false;
				data.objTaskid = taskid;
				data.objType = meta.contenttype;
				data.contentid = contentid;
			$.ajax({ 
				url : "${ctxRoot}/api/content/enable" ,
				type : "POST" , 
				dataType : 'json',
				contentType : 'application/json',
				async : false , 
				data : JSON.stringify(data) , 
				success : function(data){
					TrashSearch();
				}, 
				error : function(request, status, error) {
	        	alertNoty(request,status,error);
				} , 
				beforeSend : function() {} , 
				complete : function() {} 
			});
		}
	}
	
	
	
	//선택된 삭제
	var discardDocList = function(){
		var discardDocListCnt = $("input[name='chkbox']:checked").length;
		var SCnt = 0;
		var errmassege = "";
		$("input[name='chkbox']:checked").each(function(){
			var contentid = $(this).val();
			//삭제 불가 권한 체크
			if(selectedClsid != "NP13"){
				//console.log("NP13 not");
				var rtn = discardDoc(contentid);
				if(rtn == 1){
					SCnt++;	
				}else{
					errmassege = rtn;
				}
			}else{
				var rtn = UndoDoc(contentid);
				if(rtn == 1){
					SCnt++;	
				}else{
					errmassege = rtn;
				}
			}
		});
		if(discardDocListCnt == SCnt){
			alert("<spring:eval expression="@${msgLang}['DISCARD_DOC_SUCCEEDED']" />");
		}else{
			alert(errmassege);
		}
		listSearchN();
		
	}
	
	// 문서폐기
	var discardDoc = function(contentid){
		var meta = $("#"+contentid).data("meta");
		var rtnVal = 0;
		var data = {};
			data.objIsTest = "N";
			data.objDebugged = false;
			data.objTaskid = taskid;
			data.objType = meta.contenttype;
			data.contentid = contentid;
		
		$.ajax({ 
			url : "${ctxRoot}/api/content/discard" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){	
				if(data.status == "0000"){
					rtnVal = 1
				}else{
					rtnVal = data.message;
				}
				
			}, 
			error : function(request, status, error) {
        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		});	
		return rtnVal;
	}
	
	var discardDocOne = function(contentid){
		
		if(!confirm("문서를 폐기하시겠습니까?")){
			return;
		}else{
			var meta = $("#"+contentid).data("meta");
			var data = {};
				data.objIsTest = "N";
				data.objDebugged = false;
				data.objTaskid = taskid;
				data.objType = meta.contenttype;
				data.contentid = contentid;
			
			$.ajax({ 
				url : "${ctxRoot}/api/content/discard" ,
				type : "POST" , 
				dataType : 'json',
				contentType : 'application/json',
				async : false , 
				data : JSON.stringify(data) , 
				success : function(data){	
					if(data.status == "0000"){
						TrashSearch();
					}
				}, 
				error : function(request, status, error) {
	        	alertNoty(request,status,error);
				} , 
				beforeSend : function() {} , 
				complete : function() {} 
			});	
		}
	}
	
	// 반려문서 폐기
	var UndoDoc = function(contentid){
		var meta = $("#"+contentid).data("meta");
		var rtnVal = 0;
		
		var data = {};
			data.objIsTest = "N";
			data.objDebugged = false;
			data.objTaskid = taskid;
			data.objType = meta.contenttype;
			data.contentid = contentid;
			
		$.ajax({ 
			url : "${ctxRoot}/api/content/undo" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){	
				if(data.status == "0000"){
					rtnVal = 1;
				}else{
					rtnVal = data.message;
				}
				
			}, 
			error : function(request, status, error) {
        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		});	
		return rtnVal;
	}
	
	
	var moveDocList = function(data, contentList){	
	  noty({
		    layout : "center",
		    text : "<spring:eval expression="@${msgLang}['ARE_YOU_MOVE_DOC']"/>",
		    buttons : [ {
		      addClass : 'btbase',
		      text : "Ok",
		      onClick : function($noty) {
		        $noty.close();

		        if (contentList.length > 1) {
		        	moveDocMulti (contentList, data);
		        } else {
					$.each(contentList, function(index, contentid){
						moveDoc(contentid, data);
					});		        	
		        }
				
				listSearch();
				$("#fldTreePop").empty();
				$("#fldTreePop").hide();
			
			    $(".modalLayer:last-of-type").remove();
			    modalZIndex = 100;	       
		      }
		    }, {
		      addClass : 'btbase',
		      text : "Cancel",
		      onClick : function($noty) {
		        $noty.close();
		      }
		    } ],
		    type : "information",
		    killer : true
		  });
	}
	
	var moveDoc = function(contentid, data){	
		var meta = $("#"+contentid).data("meta");
		data.contentid = contentid;	
		data.objType = meta.contenttype;
		var rtnVal = 0;
		$.ajax({ 
			url : "${ctxRoot}/api/content/relocate" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){	
    	        if(data.status == "0000"){
    	        	rtnVal = 1;
    	        } else {
    	        	rtnVal = data.message;
    	        }
			}, 
			error : function(request, status, error) {
        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		});
		return rtnVal;
	}

	var moveDocMulti = function(contentList, data){
        $.each(contentList, function(index, contentid) {
			var meta = $("#"+contentid).data("meta");
			data.contentid = contentid;	
			data.objType = meta.contenttype;
			$.ajax({ 
				url : "${ctxRoot}/api/content/relocate" ,
				type : "POST" , 
				dataType : 'json',
				contentType : 'application/json',
				async : false , 
				data : JSON.stringify(data) , 
				success : function(data){	
				 	if (contentList.length -1 == index) {
		    	        if(data.status == "0000"){
					 		alert("<spring:eval expression="@${msgLang}['MOVE_DOC_SUCCEEDED']"/>");
		    	        } else {
		    	        	alertErr(data.message);
		    	        }
				 	}
				}, 
				error : function(request, status, error) {
	        	alertNoty(request,status,error);
				} , 
				beforeSend : function() {} , 
				complete : function() {} 
			});	
        });
	}
	
	//폴더트리팝업 에서 callback 으로 선택 폴더정보를 받아온다.
	var copyDocList = function(data,contentList){
		
		var Scnt = 0;
		var errmassege = "";''
		noty({
		    layout : "center",
		    text : "<spring:eval expression="@${msgLang}['ARE_YOU_COPY_DOC']"/>",
		    buttons : [ {
		      addClass : 'btbase',
		      text : "Ok",
		      onClick : function($noty) {
		        $noty.close();

		        $.each(contentList,function(index,contentid){
					var rtn = copyDoc(contentid,data);
					if(rtn == 1){
						Scnt++;
					}else{
						errmassege = rtn;
					}
				});
		        
		        if(contentList.length == Scnt){
		        	alert("<spring:eval expression="@${msgLang}['COPY_DOC_SUCCEEDED']"/>");
		        }else{
		        	alert(errmassege);
		        }
		        

				//listSearch();
				$("#fldTreePop").empty();
				 $("#fldTreePop").hide();
			
			    $(".modalLayer:last-of-type").remove();
			    modalZIndex = 100;		       
		      }
		    }, {
		      addClass : 'btbase',
		      text : "Cancel",
		      onClick : function($noty) {
		        $noty.close();
		      }
		    } ],
		    type : "information",
		    killer : true
		  });
	}

	// 문서복사
	var copyDoc = function(contentid,data){
		var meta = $("#"+contentid).data("meta");
		data.contentid = contentid;
		data.objType = meta.contenttype;
		
		var rtnVal = 0;
		
		$.ajax({ 
			url : "${ctxRoot}/api/content/replicate" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){	
				console.log("== copyDoc return data:", data);
    	        if(data.status == "0000"){
    	        	rtnVal = 1
    	        } else {
    	        	rtnVal = 0;
    	        }
			}, 
			error : function(request, status, error) {
        		alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		});
		return rtnVal;
	}
	
	// 문서즐겨찾기
	var favDoc = function(contentid){	
		var meta = $("#"+contentid).data('meta');
		var data = {};
			data.objIsTest = "N";
			data.objDebugged = false;
			data.objType = meta.contenttype; //00:분류,01:번들,02:파일
			data.contentid = contentid;
			data.objTaskid = taskid;
			
		$.ajax({ 
			url : "${ctxRoot}/api/content/mark" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){
    	        if(data.status == "0000"){
					alert("<spring:eval expression="@${msgLang}['FAVORITE_DOC_SUCCEEDED']"/>");
    	        } else {
    	        	alertErr(data.message);    	        	
    	        }
			}, 
			error : function(request, status, error) {
        		alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		});	
	}
	
	// 폴더 증겨찾기
	var favFld = function(contentid){	
		var data = {};
			data.objIsTest = "N";
			data.objDebugged = false;
			data.objType = "00"; //00:분류,01:번들,02:파일
			data.contentid = selectNodeId(contentid);
		$.ajax({ 
			url : "${ctxRoot}/api/content/mark" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){
				//console.log("====favFld result : ",data);
				if (data.status == "0000") {
					alert("<spring:eval expression="@${msgLang}['FAVORITE_DOC_SUCCEEDED']"/>");
				} else {
					alert(data.message);
				}
			}, 
			error : function(request, status, error) {
        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		});	
	}
	
	// 문서 즐겨찾기 해제
	var unfavDoc = function(contentid){	
		var meta = $("#"+contentid).data("meta");
		var data = {};
			data.objIsTest = "N";
			data.objDebugged = false;
			data.objType = meta.contenttype; //00:분류,01:번들,02:파일
			data.contentid = contentid;
			data.objTaskid = taskid;
			
		$.ajax({ 
			url : "${ctxRoot}/api/content/unmark" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){
				alert("<spring:eval expression="@${msgLang}['UNFAVORITE_DOC_SUCCEEDED']"/>");
				FavlistSearch();
			}, 
			error : function(request, status, error) {
        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		});	
	}
	
	// 폴더 즐겨찾기 해제
	var unfavFld = function(contentid){			
		var data = {};
			data.objIsTest = "N";
			data.objDebugged = false;
			data.objType = "00"; //00:분류,01:번들,02:파일
			data.contentid = contentid;
			
		$.ajax({ 
			url : "${ctxRoot}/api/content/unmark" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){						
				alert("<spring:eval expression="@${msgLang}['UNFAVORITE_DOC_SUCCEEDED']"/>");
				FavFldlistSearch();
			}, 
			error : function(request, status, error) {
        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		});	
	}
	
//폴더트리팝업 에서 callback 으로 선택 폴더정보를 받아온다.
	var resonList = function(data,contentList){		
		$.each(contentList,function(index,contentid){
			copyDoc(contentid,data);
		});

		//listSearch();
		$("#fldTreePop").empty();
		 $("#fldTreePop").hide();
	
	    $(".modalLayer:last-of-type").remove();
	    modalZIndex = 100;		
	}
	
	//
	var resonDoc = function(contentid,data){
		var meta = $("#"+contentid).data("meta");
		data.contentid = contentid;
		data.objType = meta.contenttype;
		
		$.ajax({ 
			url : "${ctxRoot}/api/content/approve" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){						
				
			}, 
			error : function(request, status, error) {
        	alertNoty(request,status,error);
			} , 
			beforeSend : function() {} , 
			complete : function() {} 
		});	
	}
	
	var allBtnHide = function(){
		$('li[id$=RegBtn]').hide();
		$('li[id$=DelBtn]').hide();
		$("#approvalBtn").hide();
		$("#returnBtn").hide();
	}
	var allFldBtnHide = function(){
		$('li[id^=btnFld]').hide();
	}

	$(document).ready(function() {
		initAclList();
	 	allFldBtnHide();
		allBtnHide();	
		fn_MainClsTree.jstree();
		fn_fldTree.jstree();
		extFileSizeSet();
	    $("#SearchBtn").click(function() {	//검색   
	    	$("#adSearchOK").hide();
	    	listSearch();	    	
	    });
	  
	    // 상세검색 시간 설정
	    var now = new Date();
	    document.getElementById('adRegSDate').value = new Date(now.setDate(now.getDate()-30)).toISOString().substring(0,10);
	    document.getElementById('adRegEDate').value = new Date().toISOString().substring(0,10);
	    
	    $("#btnInitAdSearchData").click(function() { //상세검색 조건 데이터 초기화  
	    	$("input:checkbox[id='incLowYn']").attr("checked", false);
	    	$("#adSearchTitle").val("");
	    	$("#adSearchDocNo").val("");
	    	$("#keywordTitle").val("");
	    	$("#creatorTitle").val("");
	    	$("#ftrSearch").val("");
	    	
	    });	    
	    
		//삭제
		$("#DocDelBtn").click(function() {
			if($("input[name='chkbox']:checked").length > 0){
				noty({
				    layout : "center",
				    text : "<spring:eval expression="@${msgLang}['ARE_YOU_DEL_DOC']"/>",
				    buttons : [ {
				      addClass : 'btbase',
				      text : "Ok",
				      onClick : function($noty) {
				        $noty.close();
				        delDocList();			       
				      }
				    }, {
				      addClass : 'btbase',
				      text : "Cancel",
				      onClick : function($noty) {
				        $noty.close();
				      }
				    } ],
				    type : "information",
				    killer : true
				  });
			} else {
				alert("<spring:eval expression="@${msgLang}['SELECT_DELETE_DOC']"/>");
			}
	    });
		
		//복원
		$("#canCelDelBtn").click(function() {
			if($("input[name='chkbox']:checked").length > 0){
				noty({
				    layout : "center",
				    text : "<spring:eval expression="@${msgLang}['ARE_YOU_RESTORE_DOC']"/>",
				    buttons : [ {
				      addClass : 'btbase',
				      text : "Ok",
				      onClick : function($noty) {
				        $noty.close();
				        cancelDelList();  
				      }
				    }, {
				      addClass : 'btbase',
				      text : "Cancel",
				      onClick : function($noty) {
				        $noty.close();
				      }
				    } ],
				    type : "information",
				    killer : true
				  });
			} else {
				alert("<spring:eval expression="@${msgLang}['SELECT_RESTORE_DOC']"/>");
			}
	    });
		
		//폐기
		$("#disDelBtn").click(function() {
			if($("input[name='chkbox']:checked").length > 0){
				noty({
				    layout : "center",
				    text : "<spring:eval expression="@${msgLang}['ARE_YOU_DISCARD_DOC']"/>",
				    buttons : [ {
				      addClass : 'btbase',
				      text : "Ok",
				      onClick : function($noty) {
				        $noty.close();
				        discardDocList();
				      }
				    }, {
				      addClass : 'btbase',
				      text : "Cancel",
				      onClick : function($noty) {
				        $noty.close();
				      }
				    } ],
				    type : "information",
				    killer : true
				  });
			} else {
				alert("<spring:eval expression="@${msgLang}['SELECT_DISCARD_DOC']"/>");
			}
	    });
		
		//fld search
		$("#searchFldBtn").click(function() {
			fldSearch();
		});
		//fld search
		$("#searchFldBeforeBtn").click(function() {
			fn_fldTree.searchBefore();
		});
		//fld search
		$("#searchFldNextBtn").click(function() {
			fn_fldTree.searchNext();
		});
		$('#searchFldName').keydown( function(event) {
			if(event.keyCode == 13)	fldSearch(); 	
		});

		 $("#searchTitle").on("keyup",function(key){
	        if(key.keyCode==13) {
	        	$("#adSearchOK").hide();
		    	listSearch();	    
	        }
	    });

		$("#searchClsBtn").click(function() {
			ClsSearch();
		});
		
		$('#searchClsName').keydown( function(event) {
			if(event.keyCode == 13)	ClsSearch(); 	
		});
		
		//리스트 전체선택 header에 name추가 selectAllDoc 
		$(document.body).delegate('input[name=selectAll]','click',function() {
			var isCheck = $(this).is(":checked");
			$("input:checkbox[name='chkbox']").prop("checked",isCheck);

		});
		
       // 문서업로드 팝업
  		$(document).on("click", "#upload", function(){
  			console.log("selectedClsid : " + selectedClsid);
  			if(selectedClsid == '' || selectedClsid == 'undefined'){
  				alert("문서함을 선택해 주세요.");
  				$('.bg').fadeOut();
  				$('.popup').fadeOut();
  				return;
  			}else{
  			 	$("#openPop").load("${ctxRoot}/go/fileReg"); 
  			   	fn_openPop("openPop");  				
  			}
  		});

        //드래그&드롭 등록
		$(document).on("dragenter", ".tbl_wrap", function(e) {
			e.stopPropagation();
			e.preventDefault();
			
			if($("#openPop").css('display') !='none'){
				return false;
			}else{
				$(".tbl_wrap").css('border', '2px solid #0B85A1');	
			}
		});

		$(document).on("dragover", ".tbl_wrap", function(e) {
			
			e.stopPropagation();
			e.preventDefault();
			if($("#openPop").css('display') !='none'){
				return false;
			}else{
				$(".tbl_wrap").css('border', '2px dotted #0B85A1');	
			}
		});
			
		$(document).on("drop", ".tbl_wrap", function(e) {
			e.stopPropagation();
			e.preventDefault();

			if($("#openPop").css('display') !='none'){
				return false;
			}
			$(".tbl_wrap").css('border', '');
			
			fileData_dragEx = {};
			addFileNO_dragEx = 0;
			var files = e.originalEvent.dataTransfer.files;
			console.log("files : ", files);
			for (var i = 0; i < files.length; i++) {
				var filename = files[i].name;
				var filesize = files[i].size;
				console.log("files[i].type : " + files[i].type);
				var ext = filename.substring(filename.lastIndexOf(".")+1).toLowerCase();
				
				if(files[i].type != "" && files[i].size != 0){//폴더를 선택한 경우 제외하기 위해 추가함
					
					if(extFileSize.hasOwnProperty(ext)){
						if(filesize < extFileSize[ext]){
							fileData_dragEx['addfile' + addFileNO_dragEx] = files[i];
							addFileNO_dragEx++;	
						}else{
							alert("해당 파일 사이즈는 등록 할 수 없습니다.");
						}
					}else{
						alert("등록된 확장자가 아니면 저장 할 수 없습니다.");
					}
				}else if(files[i].type == "" && files[i].size != 0){
					if(!extFileSize.hasOwnProperty(ext)){
						alert("등록된 확장자가 아니면 저장 할 수 없습니다.");
					}
				}else if(files[i].type != "" && files[i].size == 0){
					alert("폴더는 저장 할 수 없습니다.");
				}
				
					
			}
			
			var selNodeObj = fn_fldTree.$tree.jstree('get_node', selectedClsid);
			
			if(addFileNO_dragEx != 0){
				//부서 N
				if(selNodeObj.a_attr.objAction != "N"){ 
					
					if(selNodeObj.a_attr.type != "N1"){//N1:전사함, N2:전체부서, N3:개인, N4:협업
					
						if(selNodeObj.a_attr.isactive == 'Y' && (selNodeObj.a_attr.acl > 1 || selNodeObj.a_attr.acls > 1)){
							//등록 가능
							fileSend_dragEx(files[0].name);
						}
					}
				}	
			}
		});    
		
		$("input[type='radio'][name='group_dragEx']").click( function() {
			var checked = $(this).val();
			if (checked == 'rename') {
				$("#dupRename_dragEx").show();
			} else if (checked == 'versionup') {
				$("#dupRename_dragEx").hide();
			}
		});
		
		// 반출문서
		$("#lockList").click(function(){
			locklistSearch();
		})
		
		// 상세검색 입력내용 초기화
		// 상세검색 키 입력시 X 버튼 보이기
		// 제목
		$('#adSearchTitle').keydown( function(event) {
			var len = $(this).length;
			if(len > 0){
				$("#resetAdTitle").css('display','block');	
			}else{
				$("#resetAdTitle").css('display','none');
			}
		});
		
		$("#resetAdTitle").click(function(){
			$("#adSearchTitle").val("");
			$("#resetAdTitle").css('display','none');
			
		});

		// 문서번호
		$('#adSearchDocNo').keydown( function(event) {
			var len = $(this).length;
			if(len > 0){
				$("#resetadSearchDocNo").css('display','block');	
			}else{
				$("#resetadSearchDocNo").css('display','none');
			}
		});
		
		$("#resetadSearchDocNo").click(function(){
			$("#adSearchDocNo").val("");
			$("#resetadSearchDocNo").css('display','none');
			
		});
		// 키워드
		$('#keywordTitle').keydown( function(event) {
			var len = $(this).length;
			if(len > 0){
				$("#resetkeywordTitle").css('display','block');	
			}else{
				$("#resetkeywordTitle").css('display','none');
			}
		});
		
		$("#resetkeywordTitle").click(function(){
			$("#keywordTitle").val("");
			$("#resetkeywordTitle").css('display','none');
			
		});
		// 등록자
		$('#creatorTitle').keydown( function(event) {
			var len = $(this).length;
			if(len > 0){
				$("#resetcreatorTitle").css('display','block');	
			}else{
				$("#resetcreatorTitle").css('display','none');
			}
		});
		
		$("#resetcreatorTitle").click(function(){
			$("#creatorTitle").val("");
			$("#resetcreatorTitle").css('display','none');
			
		});
		
		// 내용
		$('#ftrSearch').keydown( function(event) {
			var len = $(this).length;
			if(len > 0){
				$("#resetftr").css('display','block');	
			}else{
				$("#resetftr").css('display','none');
			}
		});
		
		$("#resetftr").click(function(){
			$("#ftrSearch").val("");
			$("#resetftr").css('display','none');
			
		});
		
	});
	
	//공유문서
	var shareDocEvt = function(type){
		$("#SelectText").empty();
		$("#SelectText").append('<li><a href="#none"><img src="${image}/icon/Path 52.png" ></a></li>');
		$("#SelectText").append('<span>공유문서</span>');
					
		$("#shareDocType").show(); 		// 공유문서 종류
		$("#aprovType").hide();			// 승인문서 종류 
		$("#latelyType").hide();		// 최근 문서
		$("#fvtType").hide();			// 즐겨찾기 종류
		$("#SearchForm").hide();		// 검색창
		$("#detailSearch").hide();		// 상세검색
		$("#docRegistration").hide();	// 문서등록
		$("#moreMenu").hide();			// 더보기메뉴
		$("#folderMenu").hide();		// 폴더메뉴
		
		
		if(type == "shared"){
			$("input[name=shareDocType]:radio[value='shared']").prop("checked",true);
		}else if(type == "sharedby"){
			$("input[name=shareDocType]:radio[value='sharedby']").prop("checked",true);
		}
		
		selectedClsid = $("input[name=shareDocType]:checked").val();
		console.log("selectedClsid : " + selectedClsid);
		sharelistSearch();
		$("input[name=shareDocType]:radio").change(function(){
			selectedClsid = $("input[name=shareDocType]:checked").val();
			sharelistSearch();
		})
	}
	
	//승인문서
	var aprovEvt = function(Type){
		// 검색창 숨김 및 공유선택창 보이기
		$("#SelectText").empty();
		$("#SelectText").append('<li><a href="#none"><img src="${image}/icon/Path 52.png" ></a></li>');
		$("#SelectText").append('<span>승인문서</span>');
		
		$("#shareDocType").hide(); 		// 공유문서 종류
		$("#aprovType").show();			// 승인문서 종류 
		$("#latelyType").hide();		// 최근 문서
		$("#fvtType").hide();			// 즐겨찾기 종류
		$("#SearchForm").hide();		// 검색창
		$("#detailSearch").hide();		// 상세검색
		$("#docRegistration").hide();	// 문서등록
		$("#moreMenu").show();			// 더보기메뉴
		$("#approvalBtn").show();
		$("#returnBtn").show();
		$("#folderMenu").hide();		// 폴더메뉴
		
		
		
		if(Type == "NP11"){
			$("input[name=aprovType]:radio[value=NP11]").prop("checked", true);
			searchparam.objHandleType = "11";
		}else if(Type == "NP12"){
			$("input[name=aprovType]:radio[value=NP12]").prop("checked", true);
			searchparam.objHandleType = "12";
		}else if(Type == "NP13"){
			$("input[name=aprovType]:radio[value=NP13]").prop("checked", true);
			searchparam.objHandleType = "13";
		}else if(Type == "NP14"){
			$("input[name=aprovType]:radio[value=NP14]").prop("checked", true);
			searchparam.objHandleType = "14";
		}
		selectedClsid = $("input[name=aprovType]:checked").val();
		searchparam.objType = "01";
		listSearch();
		
		$("input[name=aprovType]:radio").change(function(){
			selectedClsid = $("input[name=aprovType]:checked").val();
			console.log("aprovType selectedClsid : " + selectedClsid);
			if(selectedClsid == "NP11"){
				searchparam.objHandleType = "11";
			}else if(selectedClsid == "NP12"){
				searchparam.objHandleType = "12";
			}else if(selectedClsid == "NP13"){
				searchparam.objHandleType = "13";
			}else if(selectedClsid == "NP14"){
				searchparam.objHandleType = "14";
				$('li[id$=RegBtn]').hide();
				$('li[id$=DelBtn]').hide();
				$("#moreMenu").show();
				$("#approvalBtn").show();
				$("#returnBtn").show();
			}
			listSearch();
		});
	}
	
	var latelyEvt = function(Type){
		$("#SelectText").empty();
		$("#SelectText").append('<li><a href="#none"><img src="${image}/icon/Path 52.png" ></a></li>');
		$("#SelectText").append('<span>최근 항목</span>')
		
		$("#shareDocType").hide(); 		// 공유문서 종류
		$("#aprovType").hide();			// 승인문서 종류 
		$("#latelyType").show();		// 최근 문서
		$("#fvtType").hide();			// 즐겨찾기 종류
		$("#SearchForm").hide();		// 검색창
		$("#detailSearch").hide();		// 상세검색
		$("#docRegistration").hide();	// 문서등록
		$("#moreMenu").hide();			// 더보기메뉴
		$("#folderMenu").hide();		// 폴더메뉴
		
		searchparam.objType = "01";
		if(Type == "mydoc"){
			$("input[name=latelyType]:radio[value=mydoc]").prop("checked", true);
			searchparam.objHandleType = "02";
		}else if(Type == "lately"){
			$("input[name=latelyType]:radio[value=lately]").prop("checked", true);
			searchparam.objHandleType = "01";
		}
		selectedClsid = $("input[name=latelyType]:checked").val();
		listSearch();
		
		$("input[name=latelyType]:radio").change(function(){
			selectedClsid = $("input[name=latelyType]:checked").val();
			
			if(selectedClsid == "mydoc"){
				searchparam.objHandleType = "02";
			}else{
				searchparam.objHandleType = "01";
			}
			
			listSearch();
		});
	}
	
	
	var fvtEvt = function(Type){
		$("#SelectText").empty();
		$("#SelectText").append('<li><a href="#none"><img src="${image}/icon/Path 52.png" ></a></li>');
		$("#SelectText").append('<span>즐겨찾기</span>')
		
		$("#shareDocType").hide(); 		// 공유문서 종류
		$("#aprovType").hide();			// 승인문서 종류 
		$("#latelyType").hide();		// 최근 문서
		$("#fvtType").show();			// 즐겨찾기 종류
		$("#SearchForm").hide();		// 검색창
		$("#detailSearch").hide();		// 상세검색
		$("#docRegistration").hide();	// 문서등록
		$("#moreMenu").hide();			// 더보기메뉴
		$("#folderMenu").hide();		// 폴더메뉴
		
		
		if(Type == "faveriteDoc"){
			$("input[name=fvtType]:radio[value=faveriteDoc]").prop("checked", true);
		}else if(Type == "faveriteFld"){
			$("input[name=fvtType]:radio[value=faveriteFld]").prop("checked", true);
		}
		
		
		selectedClsid = $("input[name=fvtType]:checked").val();
		searchparam.objType = "05";
		listSearch();
		
		$("input[name=fvtType]:radio").change(function(){
			selectedClsid = $("input[name=fvtType]:checked").val();
			searchparam.objType = "05";
			listSearch();
		});
	}
	
	var getTodateInfo = function() {
		var today = new Date();

		var year = today.getFullYear();
		var month = ('0' + (today.getMonth() + 1)).slice(-2);
		var day = ('0' + today.getDate()).slice(-2);

		var hours = ('0' + today.getHours()).slice(-2); 
		var minutes = ('0' + today.getMinutes()).slice(-2);
		var seconds = ('0' + today.getSeconds()).slice(-2); 		
		
		var dateString = year + '-' + month + '-' + day + '-' + hours+minutes+seconds;		
		return dateString;
	}
	
	var fileData_dragEx = {};
	var addFileNO_dragEx = 0;
	var sendFileInfo_dragEx = {};
	
	//multipart file send
	var fileSend_dragEx = function(f_fileName) {
		var fileExist = false;
		var formData = new FormData();
		for ( var key in fileData_dragEx) {
			var fileKey = key;
			var fileObj = fileData_dragEx[key];
			formData.append(fileKey, fileObj);
			fileExist = true;
		}
		if (fileExist == false) {
			alert("<spring:eval expression="@${msgLang}['NO_FILE_REGISTERED']" />");
			return;
		}
		//메타정보
		formData.append("param", "value");
		
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
					
					if(sendFilesInfo.length > 1){
						showbundlechk(sendFilesInfo,f_fileName);		// 파일 여러건 번들 / 개별 체크
					}else{
						fileReg_dragEx(sendFilesInfo,f_fileName, "02");	// 파일 한건 등록
					}
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
	
	
	//파일전송후 ECM 등록 API 호출
	var fileReg_dragEx = function(fileInfo, f_fileName, objType) {
		var mainMeta = {};
		
		//문서번호
		var DocNnoVal = "${Authentication.sessCompany.name}"+getTodateInfo();
		mainMeta.bno = DocNnoVal;
		//문서제목
		mainMeta.title = f_fileName;
		
		mainMeta.btype = "01"; // 01:Normal, 02:Virutal
		
		
		//선택된 트리 정보
		var additoryMeta = {};
		additoryMeta.summary = ""; // 문서에대한 설명
		
		var classObject = {};
		classObject.classid = selectNodeId(selectedClsid);
		classObject.classtype = selectedClsType;

		var classObjects = Items = [];//문서분류 정보
		classObjects.push(classObject);//선택문서함 정보 추가
		
		// 문서유형 지정
		classObject = {};

		
		var docType = "${DOCTYPE_DEFAULT}";
		
		classObject.classid = docType;
		classObject.classtype = "03";
		classObjects.push(classObject);//문서유형 추가
		var data = {};
		data.objIsTest = "N";
		data.objTaskid = taskid;

		data.zappClassObjects = classObjects;
		data.zappAcls = [];

		var objType = objType;
		//첨부 파일이 하나 이상이면 번들로 등록한다.
		//@TODO 사이트에서는 둘중 하나를 선택해서 개발
		
		data.objType = objType; //bundle:01, file:02	
		data.objRetention = "${RETENTION_DEFAULT}";
		//파일인 경우에는 파일명 중복 체크
		if (objType == "02") {
			var rtn = chkfile(data, fileInfo, 0);
			listSearch();
		} else {
			
			data.zappBundle = mainMeta;
			//첨부 파일 정보
			data.zappFiles = fileInfo;
			data.zappAdditoryBundle = additoryMeta;//추가 정보
			sendFileInfo_dragEx = $.extend(true, {}, data);
			var rtn = addContent_dragEx(data);
			if(rtn == 0){
				alert("<spring:eval expression="@${msgLang}['REG_DOC_SUCCEEDED']" />");
			}
			$("#adSearchOK").hide();
			listSearch();
		}
	}
	
	var chkfile = function(senddata, fileInfo, i){
		console.log("chkfile start : " + i);
		if(fileInfo.length == i){
			alert("<spring:eval expression="@${msgLang}['REG_DOC_SUCCEEDED']" />");
			$("#adSearchOK").hide();
			listSearch();
			return;
		}else{
			var contNo = getContNo();
			console.log(i + " / chkfile");
			fileInfo[i].fno = contNo;
			fileInfo[i].summary = "";
			senddata.zappFile = fileInfo[i];
			senddata.zappFile.creatorname = username;
			sendFileInfo_dragEx = $.extend(true, {}, senddata);
			$.ajax({
				url : "${ctxRoot}/api/content/checkfilename",
				type : "POST",
				dataType : 'json',
				contentType : 'application/json',
				async : false,
				data : JSON.stringify(senddata),
				success : function(data) {
					console.log("data.result : " + data.result);
					if (data.status == "0000") {
						sendFileInfo_dragEx.addmode = data.result;
						if (data.result == "01") {
							var rtn = addContent_dragEx(sendFileInfo_dragEx);
							console.log(i + " / rtn : " + rtn);
							if(rtn == 0){
								return chkfile(senddata, fileInfo, ++i);
							}
						} else if (data.result == "02" || data.result == "03" || data.result == "04") {
							showDuplicateFile_dragEx(data.result);
							if($("#duplicateFileLayer_dragEx").css('display') !='none'){
								$("#saveDupliteFile_dragEx").off('click').on('click', function(){
									var rtn = saveDupliteFile_dragEx();
									console.log("rtn : " + rtn);
									if(rtn == 0){
										return chkfile(senddata, fileInfo, ++i);	
									}
								})	
							}
						}
					} else {
						alert(data.message);
					}
				},
				error : function(request, status, error) {
					alertNoty(request, status, error, "<spring:eval expression="@${msgLang}['DOCUMENT_REGI_FAILED']" />");
				},
				beforeSend : function() {
				},
				complete : function() {
					closeLayer();
				}
			});
		}
	}
	
	
	var getContNo = function(){
		var contNo = "";
		
		$.ajax({
			url : "${ctxRoot}/api/file/seqnum",
			type : "GET",
			contentType : 'application/json',
			async : false,
			success : function(Data) {
				console.log("data : " + Data);
				contNo = Data;
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
		
		
		return contNo;
	}
	
	var addContent_dragEx = function(data) {
		console.log("====addContent_dragEx  : ", data);
		var rtn = 1;
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
					sendFileInfo_dragEx = {};
					if($("#duplicateFileLayer_dragEx").css('display') != 'none'){
						closeDupicateFile_dragEx();
					}
					rtn = 0
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
		return rtn;
	}	
	
	var closeDupicateFile_dragEx = function() {
		$("input[type='radio'][name='group_dragEx']:eq(0)").prop("checked", true);
		$("#renameFile_dragEx").val('');
		$("#duplicateFileLayer_dragEx").hide();
	}
	
	var closebundelchk_dragEx = function(){
		$("#BundleCheckLayer_dragEx").hide();
	}
	
	var showDuplicateFile_dragEx = function(type) {
		console.log("showDuplicateFile_dragEx");
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
			$("#dupVersionUp_dragEx").hide();
		} else if (type == "03" || type == "04") {
			$("#dupVersionUp_dragEx").show();
		}
		//console.log("====css : ", css);
		$('#duplicateFileLayer_dragEx').css(css).show();

		var filename = sendFileInfo_dragEx.zappFile.filename;
		filename = filename.substr(0, filename.lastIndexOf("."));
		//console.log("=====filename : " + filename);
		$("#renameFile_dragEx").val(filename);

	};
	
	var saveDupliteFile_dragEx = function() {
		console.log("saveDupliteFile_dragEx");
		var checked = $("input[type='radio'][name='group_dragEx']:checked").val();
		if (checked == 'rename') {
			var rename = $.trim($("#renameFile_dragEx").val());
			//console.log("===rename : " + rename);
			//console.log("=====sendFileInfo_dragEx : ", sendFileInfo_dragEx);

			if (objectIsEmpty(rename)) {
				alert("<spring:eval expression="@${msgLang}['ERR_MIS_FILENAME']"/>");//파일명 없음 
				return;
			}
			var filename = sendFileInfo_dragEx.zappFile.filename;
			var chgName = rename;
			if (rename.lastIndexOf(".") > 0) {
				chgName = rename.substr(0, rename.lastIndexOf("."));
			}
			filename = filename.substr(0, filename.lastIndexOf("."));
			if (filename == chgName) {
				alert("<spring:eval expression="@${msgLang}['CANNOT_ADD_SAME_FILE_NAME']"/>");//중복 파일명  
				return;
			}
			sendFileInfo_dragEx.zappFile.filename = chgName + "." + sendFileInfo_dragEx.zappFile.objFileExt;
			rtn = addContent_dragEx(sendFileInfo_dragEx);
		} else if (checked == 'versionup') {
			rtn =addContent_dragEx(sendFileInfo_dragEx);
		}

		return rtn;
	}	
	
	var showbundlechk = function(sendFilesInfo,f_fileName){
		var rtnVal = "";
		$("#BundleCheckLayer_dragEx").show();
		$("#bundlechkbtn").off('click').on('click', function(){
			rtnVal = $("input[type='radio'][name='bundlechk_dragEx']:checked").val();
			$("#BundleCheckLayer_dragEx").hide();
			
			if($("#duplicateFileLayer_dragEx").css('display') == 'none'){
				fileReg_dragEx(sendFilesInfo,f_fileName, rtnVal);
			}
		});
		
		
	}
	
	var closeAdSearch  = function(){
	 	$("#searchTitle").attr("disabled",false);
		$("#adSearchLayer").hide();
	}
	var fldSearch = function(){
		var fldName = $("#searchFldName").val();
		fn_fldTree.search(fldName); 
	}
	
	var ClsSearch = function(){
		var ClsName = $("#searchClsName").val();
		fn_MainClsTree.search(ClsName); 
	}
	
	var addModal = function() {
	    $('.sepage_data').append("<div class = 'modalLayer' style = 'position: fixed;z-index: " + modalZIndex + ";left: 0;top: 0;width: 100%;height: 100%;overflow: auto;background-color: rgb(0,0,0);background-color: rgba(0,0,0,0.4);'></div>");
	}
	
	var fn_init = function() {
	    $("#holdArea").hide();
	};
	
	var fn_openPop = function(selector) {
		
		//console.log("selector : " + selector);
		$(".bg").fadeIn();
	    //레이어팝업 중앙에 띄우기
	    var $layerPopupObj = $('#'+selector);
	    $layerPopupObj.css({
	        'left': '50%',
	        'top': '50%',
	        'position': 'absolute'
	    });
	    addModal();
	
	    $('#'+selector).show();
		try{
	    }catch(e){
	    	console.log("e : "+e);
	    	
	    }
	
	    $('#'+selector).css('z-index', modalZIndex + 10);
	    modalZIndex = modalZIndex + 100;
	};
	
	var fn_editPop = function(selector) {
	    //레이어팝업 중앙에 띄우기
	    var $layerPopupObj = $('#'+selector);
	    var left = ($(window).scrollLeft() + ($(window).width() - $layerPopupObj.width()) / 2);
	    var top = ($(window).scrollTop() + ($(window).height() - $layerPopupObj.height()) / 5);
	    $layerPopupObj.css({
	        'left': '50%',
	        'top': '50%',
	        'position': 'absolute'
	    });
	    addModal();
	
	    $('#'+selector).show();
		try{
		    $('#'+selector).draggable({
		        containment: 'body',
		        scroll: false
		    });
	    }catch(e){
	    	console.log("e : "+e);
	    }
	
	    $('#'+selector).css('z-index', modalZIndex + 10);
	    modalZIndex = modalZIndex + 100;
	};
	
	var fn_openFldPop = function(selector) {
	    //레이어팝업 중앙에 띄우기
	    var $layerPopupObj = $('#'+selector);
	    $layerPopupObj.css({
	        'left': "50%",
	        'top': "50%",
	        'position': 'absolute'
	    });
	    addModal();
	
	    $('#'+selector).show();
		try{
		    $('#'+selector).draggable({
		        containment: 'body',
		        scroll: false
		    });
	    }catch(e){
	    	console.log("e : "+e);
	    }
	
	    $('#'+selector).css('z-index', modalZIndex + 10);
	    modalZIndex = modalZIndex + 100;
	};
	
	var fn_openApprovalPop = function(selector) {
	    //레이어팝업 중앙에 띄우기
	    var $layerPopupObj = $('#'+selector);
	    $layerPopupObj.css({
	        'left': '50%',
	        'top': '50%',
	        'position': 'absolute'
	    });
	    addModal();
	
	    $('#'+selector).show();
		try{
		    $('#'+selector).draggable({
		        containment: 'body',
		        scroll: false
		    });
	    }catch(e){
	    	console.log("e : "+e);
	    }
	
	    $('#'+selector).css('z-index', modalZIndex + 10);
	    modalZIndex = modalZIndex + 100;
	};
	
	// 우측 메뉴 리사이징
	$(function() {
		$("#rigthMenu").resizable({
			minWidth: 10,
			maxWidth: 600, 
			handles: "w", 
			resize: function( e, ui){
				var duration = setTimeout(function(){
					var containerWidth = parseInt($('#container').css('width').replace(/[^-\d\.]/g, '')); 	// body width
					
					var leftPixel = parseInt($('.sepage_l_Tree').css('width').replace(/[^-\d\.]/g, '')); 	// left width 
					var middlePixel = parseInt($('.sepage_r').css('width').replace(/[^-\d\.]/g, '')); 		// middle width
					var rightPixel = ui.size.width;	// right width
					
					var lWidth = Math.round(leftPixel / containerWidth * 1000) / 10;
					var mWidth = Math.round(middlePixel / containerWidth * 1000) / 10;
					var rWidth = Math.round(rightPixel / containerWidth * 1000) / 10;
	
					$("#rigthMenu").css("width", "calc(98% - "+ mWidth +"% - " + lWidth + "%)");
					$(".sepage_r").css("width", "calc(98% - "+ lWidth +"% - " + rWidth + "%)");
				},30);
			}
		}); 
	});
	
	// 좌축 트리 리사이징
	$(function() {
		$("#resizable").resizable({
			minWidth: 300, 
			maxWidth: 600, 
			handles: "e", 
			resize: function( e, ui){
				var duration = setTimeout(function(){
					var containerWidth = parseInt($('#container').css('width').replace(/[^-\d\.]/g, '')); // body width
					
					var leftPixel = ui.size.width; // resizing width
					var middlePixel = parseInt($('.sepage_r').css('width').replace(/[^-\d\.]/g, '')); // middle width
					var rightPixel = parseInt($('#rigthMenu').css('width').replace(/[^-\d\.]/g, '')); // right width
	
					var lWidth = Math.round(leftPixel / containerWidth * 1000) / 10;
					var mWidth = Math.round(middlePixel / containerWidth * 1000) / 10;
					var rWidth = Math.round(rightPixel / containerWidth * 1000) / 10;
	
					$(".sepage_l_Tree").css("width", "calc(98% - "+ mWidth +"% - " + rWidth + "%)");
					$(".sepage_r").css("width", "calc(98% - "+ lWidth +"% - " + rWidth + "%)");
				},30);
			}
		}); 
	});
	
	var sortStat = true;
	var sortHeader = function(type){
		var sort = "desc";
		if(!sortStat){
			sort = "asc";
			sortStat = true;
		}else{
			sort = "desc";
			sortStat = false;
		}
		
		if(type == "N"){
			listSearchP(1, "contentno", sort);
		}else if(type == "T"){
			listSearchP(1, "title", sort);
		}else if(type == "R"){
			listSearchP(1, "CREATORNAME", sort);
		}else if(type == "C"){
			listSearchP(1, "createtime", sort);	
		}
	}
	
	var extFileSizeSet = function(){
		var sendData = {};
	    $.ajax({
	      type : 'POST',
	      url : '${ctxRoot}/api/system/format/list',
	      contentType : 'application/json',
	      async : false,
	      data : JSON.stringify(sendData),
	      success : function(data) {
	        var dataRtn = data.result;
	        
	        for(var i = 0 ; i < dataRtn.length ; i++){
	        	 extFileSize[dataRtn[i].code.toLowerCase()] = dataRtn[i].mxsize;
	        }
	      },
	      error : function(request, status, error) {
	        alertNoty(request,status,error);
	      }

	    });
	  }
	
	
var EnterSearch = function(e){
	if(e.keycode == 13){
		listSearch();
	}
	return false;
}


var sideMenuClickEvent = function(){
	

	$('#allchk').unbind("click").bind("click", function(){
		console.log("allchk 클릭");
		
        if($('#allchk').prop("checked")){
            $('table input[type=checkbox]').prop('checked',true);
            $('table tbody input[type=checkbox]').closest('tr').css('background-color','#fff6de');
        } else {
            $('table input[type=checkbox]').prop('checked',false);
            $('table tbody input[type=checkbox]').closest('tr').css('background-color','inherit');
        }
    });
	
	$('.tooltip_wrap button').unbind("click").bind("click", function(){
        $(this).parent().find('.ui_popup').slideDown();
    });
    $('.ui_popup ul li a').unbind("click").bind("click", function(){
        $('.ui_popup').slideUp();
    });
}


var slideMenuInfo = function(Menu){
	var fldName = "";
	switch(Menu){
	case "shareDocList":
		fldName ="공유문서";
		break;
	case "aprovList":
		fldName ="승인문서";
		break;
	case "latelyList":
		fldName ="최근항목";
		break;
	case "fvtList":
		fldName ="즐겨찾기";
		break;
	
	}
	
	
	var html = "";
	html += "<ul>";
	html += "<li>";
	html += "<img src='${image}/icon/Group 43.png'>";
	html += "</li>";
	
	// 폼더명
	html += "<li>";
	html += "<h3><img src='${image}/icon/Path 51.png'>[폴더명]</h3>";
	html += "<p>"+fldName+"</p>";
	html += "</li>";
	html += "</ul>";
	
	$("#bundleInfo").empty().append(html);
}

	
</script>
</head>

<body>
	<input type="hidden" id = "page">
	<!-- header start-->
	<c:import url="../common/TopPage.jsp" />
	<!-- header end -->
	<main>
		<div class="bg"></div>
		<div class="flx">
			<nav>
				<!-- 사이드 메뉴 -->
				<div id="resizable" class="nav_wrap ui-widget-content">
					<div class="area-custom-select btn">
						<div class="custom-select" tabindex="0">
						   <img src="${image }/icon/Group 79.png" class="custom-select-img"><span class="custom-select-text foldingMn">문서함</span>
						   <img src="${image }/icon/arrow.png" class="custom-select-arrow foldingMn">
						</div>
						<ul class="custom-select-list" style="display:none; box-shadow: 0 3px 6px 0 white" >
							<li value="selectOption1" class="custom-select-option"><img src="${image }/icon/Group 79.png">문서함</li>
							<li value="selectOption2" class="custom-select-option"><img src="${image }/icon/Group 81.png">분류체계</li>
							<%-- <li value="selectOption3" class="custom-select-option"><img src="${image }/icon/Group 80.png">작업문서</li> --%>
						</ul>
						<select class="select-origin">
							<option value="selectOption1" selected="selected">문서함</option>
							<option value="selectOption2">분류체계</option>
							<!-- <option value="selectOption3">작업문서</option> -->
						</select>
					</div>
					<ul class="gnb">
						<li class="slideMenu"><a href="#none" title="모든 문서"><img src="${image }/icon/icon_1.png"><span class="foldingMn">모든 문서</span></a>
							<div>
								<input type="text" id = "searchFldName" style="margin-left:10px; margin-bottom: 10px" placeholder="검색할 폴더명 입력">
							</div>
							<div>
								<input type="text" id = "searchClsName" style="margin-left:10px; margin-bottom: 10px; display:none" placeholder="검색할 분류명 입력">
							</div>
							<div id="fldjstree" class="sub">
							</div>
							<div id ="MainClsTree" class="sub" style="display: none;">
							</div>
						</li>
						<li class="shareDocMenu" id = "shareDocList" title="공유문서"><a href="#none"><img src="${image }/jstree/white/ic_tree_share_01.png"><span class="foldingMn"><spring:eval expression="@${lang}['SHARED_DOC']"/><img src="${image }/icon/arrow.png" class="openBtn"></span></a>
							<div class ="sub" style="display: none;">
								<ul class="subMenu1">
									<li id = "sharingDoc" onclick="shareDocEvt('shared')"><span style="color: white;">- <spring:eval expression="@${lang}['SHARING_DOC']"/></span></li>
									<li id = "sharedInbox" onclick="shareDocEvt('sharedby')"><span style="color: white;">- <spring:eval expression="@${lang}['SHARED_INBOX']"/></span></li>
								</ul>
							</div>
						</li>
						<li class="aprovMenu" id = "aprovList" title="승인문서"><a href="#none"><img src="${image }/jstree/white/ic_document_w01.png"><span class="foldingMn"><spring:eval expression="@${lang}['APPROVAL_DOC']"/><img src="${image }/icon/arrow.png" class="openBtn"></span></a>
							<div class ="sub" style="display: none;">
								<ul class="subMenu2">
									<li id ="Aprwating" onclick="aprovEvt('NP11')"><span style="color: white;">- <spring:eval expression="@${lang}['WAITING_FOR_APPROVAL']"/></span></li>
									<li id ="Aprfin" onclick="aprovEvt('NP12')"><span style="color: white;">- <spring:eval expression="@${lang}['APPROVAL_FINISH']"/></span></li>
									<li id ="Aprrej" onclick="aprovEvt('NP13')"><span style="color: white;">- <spring:eval expression="@${lang}['APPROVAL_REJECTION']"/></span></li>
									<li id ="Aprauth" onclick="aprovEvt('NP14')"><span style="color: white;">- <spring:eval expression="@${lang}['APPROVAL_AUTHORITY']"/></span></li>
								</ul>
							</div>
						</li>
						<li class="latelyMenu" id = "latelyList" title ="최근 항목"><a href="#none"><img src="${image }/jstree/white/ic_tree_arrived_01.png"><span class="foldingMn">최근 항목<img src="${image }/icon/arrow.png" class="openBtn"></span></a>
							<div class ="sub" style="display: none;">
								<ul class="subMenu3">
									<li id ="RegisteredDoc" onclick="latelyEvt('mydoc')"><span style="color: white;">- <spring:eval expression="@${lang}['REGISTERED_DOC']"/></span></li>
									<li id ="RecentDoc" onclick="latelyEvt('lately')"><span style="color: white;">- <spring:eval expression="@${lang}['RECENT_DOC']"/></span></li>
								</ul>
							</div>
						</li>
						<li class="fvtMenu" id = "fvtList" title="즐겨찾기"><a href="#none"><img src="${image }/icon/icon_3.png"><span class="foldingMn">즐겨찾기<img src="${image }/icon/arrow.png" class="openBtn"></span></a>
							<div class ="sub" style="display: none;">
								<ul class="subMenu4">
									<li id ="fvtDoc" onclick="fvtEvt('faveriteDoc')" ><span style="color: white;">- <spring:eval expression="@${lang}['DOC']"/></span></li>
									<li id ="fvtFolder" onclick="fvtEvt('faveriteFld')"><span style="color: white;">- <spring:eval expression="@${lang}['FOLDER']"/></span></li>
								</ul>
							</div>
						</li>
						<li id = "lockList" onclick="locklistSearch();" title="반출 문서"><a href="#none"><img src="${image }/jstree/white/ic_tree_lock_02.png"><span class="foldingMn"><spring:eval expression="@${lang}['CHECKED-OUT_DOC']"/></span></a></li>
						<li id = "TrashList" onclick="TrashSearch();" title="휴지통"><a href="#none"><img src="${image }/icon/Path 15.png"><span class="foldingMn">휴지통</span></a></li>
					</ul>
					<p id="footer" class="foldingMn">서울특별시 금천구 벚꽃로 278 SJ테크노빌 704호<br />  
                            TEL 02-6244-4000 / FAX 02-6224-4001<br />  
                            Copyright © Zenith solution technology
					</p>
				</div>
				<div class="nav_btn"> 
					<img src="${image}/icon/menu_r_02.png" alt="">
				</div>
			</nav>
			<section id="content" style="padding-right: 210px;">
				<div class="breadcrumb">
					<ul id = "SelectText">
					</ul>
				</div>
				<div class="innerWrap" style="height: 91%">
					<div class="uiGroup">
						<div>
							<div id = "shareDocType" style="display: none;">
								<input type="radio" name= "shareDocType" value="shared" checked="checked"><spring:eval expression="@${lang}['SHARING_DOC']"/>
								<input type="radio" name= "shareDocType" value="sharedby"><spring:eval expression="@${lang}['SHARED_INBOX']"/>
							</div>
							<div id = "aprovType" style="display: none;">
								<input type="radio" name= "aprovType" value="NP11" checked="checked"><spring:eval expression="@${lang}['WAITING_FOR_APPROVAL']"/>
								<input type="radio" name= "aprovType" value="NP12"><spring:eval expression="@${lang}['APPROVAL_FINISH']"/>
								<input type="radio" name= "aprovType" value="NP13"><spring:eval expression="@${lang}['APPROVAL_REJECTION']"/>
								<input type="radio" name= "aprovType" value="NP14"><spring:eval expression="@${lang}['APPROVAL_AUTHORITY']"/>								
							</div>
							<div id = "latelyType" style="display: none;">
								<input type="radio" name= "latelyType" value="mydoc" checked="checked"><spring:eval expression="@${lang}['REGISTERED_DOC']"/>
								<input type="radio" name= "latelyType" value="lately"><spring:eval expression="@${lang}['RECENT_DOC']"/>
							</div>
							<div id = "fvtType" style="display: none;">
								<input type="radio" name= "fvtType" value="faveriteDoc" checked="checked"><spring:eval expression="@${lang}['DOC']"/>
								<input type="radio" name= "fvtType" value="faveriteFld"><spring:eval expression="@${lang}['FOLDER']"/>
							</div>
							<form action="#" id = "SearchForm">
							    <input type="text" id ="searchTitle" placeholder="문서명을 입력해주세요">
							    <input type="submit" id="SearchBtn">
							    <label for="SearchBtn"><img src="${image}/icon/Group 57.png"></label>
							</form>
							<div class="tooltip_wrap" id ="detailSearch">
							    <button type="button"><img src="${image}/icon/Group 58.png"><span class="mob_none">상세검색</span></button>
									<!-- tooltip -->
							        <div class="ui_popup" id ="adSearchOK">
							            <ul>
							                <li>
							                    <p>검색기간</p>
							                    <input type="date" max="9999-12-31" id="adRegSDate"><input type="date" max="9999-12-31" id="adRegEDate">
							                </li>
							                <li>
							                    <p>제목</p>
							                    <input type="text" placeholder="내용을 입력하세요" id="adSearchTitle">
							                    <img src="${image}/icon/x.png" style="width:15px; height: 15px; margin-top:8px; margin-left: -20px; cursor: pointer; display: none;" id ="resetAdTitle">
							                </li>
							                <li>
							                    <p>문서 번호</p>
							                    <input type="text" placeholder="내용을 입력하세요" id="adSearchDocNo">
							                    <img src="${image}/icon/x.png" style="width:15px; height: 15px; margin-top:8px; margin-left: -20px; cursor: pointer; display: none;" id ="resetadSearchDocNo">
							                </li>
							                <li>
							                    <p>키워드</p>
							                    <input type="text" placeholder="내용을 입력하세요" id="keywordTitle">
							                    <img src="${image}/icon/x.png" style="width:15px; height: 15px; margin-top:8px; margin-left: -20px; cursor: pointer; display: none;" id ="resetkeywordTitle"> 
							                </li>
							                <li>
							                    <p>등록자</p>
							                    <input type="text" placeholder="내용을 입력하세요" id="creatorTitle">
							                    <img src="${image}/icon/x.png" style="width:15px; height: 15px; margin-top:8px; margin-left: -20px; cursor: pointer; display: none;" id ="resetcreatorTitle">
							                </li>
							                <li>
							                    <p>내용</p>
							                    <input type="text" placeholder="내용을 입력하세요" id="ftrSearch">
							                    <img src="${image}/icon/x.png" style="width:15px; height: 15px; margin-top:8px; margin-left: -20px; cursor: pointer; display: none;" id ="resetftr">
							                </li>
							                <li>
							                	<p>하위 포함</p>
							                	<input type="checkbox" id="incLowYn"><label for="incLowYn" style="margin-top: 5px;"></label>
							                </li>
							            </ul>
							            <button type="button" class="submit" onclick="listAdSearch()">확인</button>
							        </div>
							    </div>
							</div>
                            <div>
                                <div class="tooltip_wrap" id = "docRegistration">
                                    <button type="button" id="upload"><img src="${image}/icon/Group 46.png"><spring:eval expression="@${lang}['DOC_REGISTRATION']"/></button>
                                </div>
                                <div class="tooltip_wrap" id = "folderMenu">
                                	<button type="button"><img src="${image}/icon/Path 43.png">폴더 메뉴 <img src="${image}/icon/icon_off.png"></button>
                                	<div class="ui_popup">
                                		<ul>
                                			<li id = "btnFldReg"><a href="#" onclick="javascript:folderRegOpen();"><img src="${image}/icon/Path 43.png"><spring:eval expression="@${lang}['ADD_FOLDER']"/></a></li>
                                			<li id = "btnFldMod"><a href="#" onclick="javascript:folderRegEdit();"><img src="${image}/icon/Path 43.png"><spring:eval expression="@${lang}['CHANGE_FOLDER']"/></a></li>
                                			<li id = "btnFldDel"><a href="#" onclick="javascript:fldDisable();"><img src="${image}/icon/Path 43.png"><spring:eval expression="@${lang}['DELETE_FOLDER']"/></a></li>
                                			<li id = "btnFldRes"><a href="#" onclick="javascript:fldRestore();"><img src="${image}/icon/Path 43.png"><spring:eval expression="@${lang}['RESTORE_FOLDER']"/></a></li>
                                			<li id = "btnFldDis"><a href="#" onclick="javascript:fldDiscard();"><img src="${image}/icon/Path 43.png"><spring:eval expression="@${lang}['DISCARD_FOLDER']"/></a></li>
                                		</ul>
                                	</div>
                                </div>
                                <div class="tooltip_wrap" id ="moreMenu">
                                    <button type="button" id="more"><img src="${image}/icon/icon_7.png">더보기 <img src="${image}/icon/icon_off.png"></button>
                                    <!-- tooltip -->
                                    <div class="ui_popup">
                                        <ul>
                                            <li id = "ShareRegBtn"><a href="#none" onclick="javascript:docSharePop()"><img src="${image}/icon/icon_c02.png"><spring:eval expression="@${lang}['SHARE_DOC']"/></a></li>
                                            <li id = "MoveRegBtn"><a href="#none" onclick="javascript:moveDocPop()"><img src="${image}/icon/icon_c04.png"><spring:eval expression="@${lang}['MOVE_DOC']"/></a></li>
                                            <li id = "CopyRegBtn"><a href="#none" onclick="javascript:copyDocPop()"><img src="${image}/icon/icon_c03.png"><spring:eval expression="@${lang}['COPY_DOC']"/></a></li>
                                            <li id = "LinkRegBtn"><a href="#none" onclick="javascript:linkRegOpen()"><img src="${image}/icon/icon_c01.png"><spring:eval expression="@${lang}['LINK']"/></a></li>
                                            <li id = "DelBtn"><a href="#none"><img src="${image}/icon/Group 127.png"><spring:eval expression="@${lang}['DELETE_DOC']"/></a></li>
                                            <li id = "canCelDelBtn"><a href="#none"><img src="${image}/icon/Group 127.png"><spring:eval expression="@${lang}['RECOVER_DOC']"/></a></li>
                                            <li id = "disDelBtn"><a href="#none" ><img src="${image}/icon/Group 127.png"><spring:eval expression="@${lang}['DISCARD_DOC']"/></a></li>
                                            <li id = "approvalBtn"><a href="#none" onclick="javascript:reasonPop('approve')"><img src="${image}/icon/Group 127.png"><spring:eval expression="@${lang}['APPROVAL']"/></a></li>
                                            <li id = "returnBtn"><a href="#none" onclick="javascript:reasonPop('return')"><img src="${image}/icon/Group 127.png"><spring:eval expression="@${lang}['RETURN']"/></a></li>
                                        </ul>
                                    </div>
                                </div>
                                
                            </div>
                        </div><!--uiGroup-->
                        <div class="tbl_wrap">
                            <table>
                                <thead class="mob_none" id ="contentHeader">
                                    <th><input type="checkbox" name="selectAll" id="allchk"><label for="allchk"></label></th>
                                    <th>구분</th>
                                    <th>이름</th>
                                    <th>등록자</th>
                                    <th>등록일시</th>
                                    <th>권한</th>
                                    <th>상태</th>
                                    <th>&nbsp;</th>
                                </thead>
                                <tbody id = "bundleList">
                                    
                                </tbody>
                            </table>
                        </div>
						<div class="pagination" id = "pageing">
						   
						</div>
						<div id="openPop" style="display: none;">
						</div>
					</div><!--innerWrap-->
                </section>
                <section id="detailContent" class="mob_none">
                    <div class="detail_btn">
                        <img src="${image}/icon/menu_r_04.png">
                    </div>
                    <div class="detail_wrap detailSlide" id = "bundleInfo">
                    </div>
			</section>
		</div>
		<div id="filedown" style="display: none;"></div>
	</main>
</body>

</html>