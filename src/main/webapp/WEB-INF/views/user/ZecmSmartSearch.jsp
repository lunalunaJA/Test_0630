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
<style>
input[type="submit"] + label {
    position: absolute;
    cursor: pointer;
    transform: translate(-30px, 7px);
}
p{
	display:inline-block;    
	line-height: 30px;
    margin-right: 8px;
    margin-bottom: 10px;
    width:4%;
    min-width : 61px;
}

#content .innerWrap .uiGroup div form input[type="text"]{
	 width: 21.5%;
}
#content .innerWrap .uiGroup div form input[type="date"]{
	width: 10.3%;
}
@media screen and (min-width: 1920px){
	#content .innerWrap .uiGroup div form input[type="text"]{
		width: 26.2%;
	}
	#content .innerWrap .uiGroup div form input[type="date"]{
		width: 13%;
	}
}

@media screen and (max-width: 1919px) and (min-width:1750px){
	#content .innerWrap .uiGroup div form input[type="text"]{
		width: 25.5%;
	}
	#content .innerWrap .uiGroup div form input[type="date"]{
		width: 12.6%;
	}
}
@media screen and (max-width: 1749px) and (min-width:1564px){
	#content .innerWrap .uiGroup div form input[type="text"]{
		width: 24.5%;
	}
	#content .innerWrap .uiGroup div form input[type="date"]{
		width: 12.1%;
	}
}
@media screen and (max-width:1563px) and (min-width: 1365px){
	#content .innerWrap .uiGroup div form input[type="text"]{
		width: 23%;
	}
	#content .innerWrap .uiGroup div form input[type="date"]{
		width: 11.2%;
	}
}
@media screen and (max-width:1365px) and (min-width: 1100px){
	#content .innerWrap .uiGroup div form input[type="text"]{
		width: 19.7%;
	}
	#content .innerWrap .uiGroup div form input[type="date"]{
		width: 9.6%;
	}
}
@media screen and (min-width: 750px) and (max-width: 1100px){
	nav {top: 0px;}
	#content {margin-top:0px;}
	#content .innerWrap .uiGroup div form input[type="text"]{
	 width: 21.5%;
	}
	#content .innerWrap .uiGroup div form input[type="date"]{
	width: 10.3%;
	}
}
@media screen and (max-width: 749px){
	#content {margin-top:0px;}
	nav{top:0px;}
	
}

</style>
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
	
	selectedClsid = "${contentid}";
	taskid = "${taskid}";
	opType = "${opType}";

	console.log("=== selectedClsid: " + selectedClsid);
	console.log("=== taskid: " + taskid);
	console.log("=== opType: " + opType);
	
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
	            "UNSHARE": { name: "<spring:eval expression="@${lang}['RELEASE_SHARING']"/>", icon: "paste",visible: function(){
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
	//var selectedClsid = "";
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
	var icon = "${image}/jstree/tree_user_icon15.png";
	
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
							 	var child ={ id : "newid" ,parent: obj.id,icon : obj.icon, text : "<spring:eval expression="@${lang}['NEW_FOLDER']"/>" ,a_attr : {type:obj.a_attr.type,nsearch:true,objType:"01"}};
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
								    	addClass : 'b_btn',
								      	text : "Ok",
								      	onClick : function($noty) {
								        	$noty.close();
								        	fldDisable(inst,obj);     
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
								      	addClass : 'b_btn',
								      	text : "Ok",
								      	onClick : function($noty) {
								        	$noty.close();
								        	fldRestore(inst,obj);
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
								      	addClass : 'b_btn',
								      	text : "Ok",
								      	onClick : function($noty) {
								        	$noty.close();
								        	fldDiscard(inst,obj);
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
						}
                    }  				 
                }				
			})
			.on("select_node.jstree", function (event, data) { // 노드가 선택된 뒤 처리할 이벤트
				console.log("fn_fldTree.select_node.jstree");
			
				try{
					//폴더 네비게이션
					$("#SelectText").empty();
					$("#SelectText").append('<li><a href="#none"><img src="${image}/icon/Path 52.png" ></a></li>');
					var path = data.instance.get_path(data.node);
					var parentnodes = tree.getNodeIdPath(data.node.id);
					//console.log("=== parentnodes:", parentnodes);
					
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
						//console.log("=== $span:", $span);
						
						$("#SelectText").append($span);
					}
				}catch(e){
				}

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
				$("#folderMenu").show();		// 폴더메뉴
			   	
			   	
			  	selectedClsid = data.node.id;
				selectedClsType = data.node.a_attr.type;
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
						html += "<li>";
						html += "<h3><img src='${image}/icon/Path 51.png'>[소유자]</h3>";
						html += "<p>" +  folderAttr.holderName + " (" + folderAttr.holderDeptName + ")</p>";
						html += "</li>";
					}
					html += "</ul>";
					
					$("#bundleInfo").empty().append(html);
				}
				
				//기업관리인 경우에만 전사문서함 등록가능
				if (!nsearch){
				  	//부서 N
				  	if (data.node.a_attr.objAction =="N"){
				    	fn_fldTree.getUseNode(data);
				    	
						createDocEmptyList();
				    	
				  	} else {
				    	//N1:전사함, N2:전체부서, N3:개인, N4:협업
				    	if(data.node.a_attr.type == "N1"){
						} else {
							fn_fldTree.getUseNode(data);
						}
				   		
						$("#searchTitle").val('');
						//$("#adSearchOK").hide();
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
				console.log("=== fn_fldTree.changed.jstree data", data);
				
				if (data.node == null)
					return;
				
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
					console.log("== fn_fldTree.changed.jstree Exception....", e);
				}
			})
			.on("after_open.jstree", function (e, data) {
				
				console.log("after_open.jstree");
				
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
				fldSearch();
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
			
			console.log("sendData : "+  JSON.stringify(sendData));
			
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
			this.$tree.jstree("search", nodename);
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
			console.log("selectedClsid : " + selectedClsid);
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
    
    if(acl.aclobjtype == "01"){ 
      if(deptuserid == acl.aclobjid && acl.acls == 5){
     	 $('input[id$=RegBtn]').show();	
   		  $("#DelBtn").show();	
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
				
				$("#DelBtn").show();
				tree.openNode(selectedClsid);
				
				var html = "";
				html += "<tr><td style='padding:5px;'><b>[분류체계]</b></td></tr>";
				html += "<tr><td style='padding:5px;'>" + data.node.text + "</td></tr>";
				html += "<tr><td style='padding:5px;'></td></tr>";

				$("#bundleInfo").empty().append(html);

			})
			.on('changed.jstree', function (e, data) {
				console.log("=== fn_MainClsTree.changed.jstree");
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
		event.preventDefault();
		$(".bg").fadeOut();
		openLayer("<spring:eval expression="@${msgLang}['RETRIEVING_LIST_LIST']"/>");
		var ftrSearch = $("#ftrSearch").val();
		
		//if (ftrSearch != "" && ($('#adSearchOK').is(':visible'))) {//adSearchLayer->adSearchOK
		if (ftrSearch != "") {
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
			//if (ftrSearch != "" && ($('#adSearchOK').is(':visible'))) {
			if (ftrSearch != "") {
				listSearchFTR(pageno, "createtime", "desc");//물리파일 목록 조회(테이블)
			} else {
				listSearchP(pageno, "createtime", "desc");//물리파일 목록 조회(테이블)
			}
		}else{
			//closeLayer();
		}
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
		
		title = $("#searchTitle").val();
	 
		console.log("title : " + title);
	 	console.log("$('#regSDate').val() : " + $('#regSDate').val());
	 	console.log("$('#regEDate').val() : " + $('#regEDate').val());
	 
		SDate = $('#regSDate').val() + " 00:00:00";
		EDate = $("#regEDate").val() + " 23:59:59";
         //상세검색 조건 추가: 등록자 검색
        var creator = $.trim($("#creatorTitle").val());
   		var DocNo = $.trim($("#searchDocNo").val());
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

		title = $("#searchTitle").val();
		SDate = $('#regSDate').val() + " 00:00:00";
		EDate = $("#regEDate").val() + " 23:59:59";
         //상세검색 조건 추가: 등록자 검색
        var creator = $.trim($("#creatorTitle").val());
   		var DocNo = $.trim($("#searchDocNo").val());
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
		
		// 제목
		if(title.length > 0){
			bundle["title"] = title;
			file["filename"] = title;
		}

		pageno = pageno ? pageno : 1;
		var classObject = {};
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
			
		console.log("=== ftr list data", data);
			
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
			  	console.log("===ftr list return: ", data);
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
		console.log("listSearchN 시작");
		var title = $("#searchTitle").val();
		
    	//상세검색이 오픈되경우에는 상세검색의 검색값 사용

       title = $("#searchTitle").val();

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
		
		console.log("searchparam.objHandleType : " + searchparam.objHandleType);
		
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
			
		console.log("listSearchN : "+JSON.stringify(data));
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
		html += "<th class='fixedHeader' style='width: 4%; text-align:center;'><input type='checkbox' name='allchk' id='allchk'/><label for='allchk'></label></th>";
		html += "<th class='fixedHeader' style='width: 5%; text-align:center;'>구분</th>";
		html += "<th class='fixedHeader' style='width: 15%; text-align:center;'>"+docInfo.folderName+"</th>";
		if (order == "title") {
			html += "<th class='fixedHeader' style='cursor:pointer; text-align:center;' onclick=sortHeader('T')>"+docInfo.title+"&nbsp<img src='${image}/icon/" + sortImg + "' id='sort_title' style='width:10px; vertical-align:middle;'</th>";
		} else {
			html += "<th class='fixedHeader' style='cursor:pointer; text-align:center;' onclick=sortHeader('T')>"+docInfo.title+"</th>";
		}
		if (order == "CREATORNAME") {
			html += "<th class='fixedHeader' style='width: 12%; text-align:center; cursor:pointer;' onclick=sortHeader('R')>"+docInfo.register+"&nbsp<img src='${image}/icon/" + sortImg + "' id='sort_register' style='width:10px; vertical-align:middle; '</th>";
		} else {
			html += "<th class='fixedHeader' style='width: 12%; text-align:center; cursor:pointer;' onclick=sortHeader('R')>"+docInfo.register+"</th>";			
		}
		if (order == "createtime") {
			html += "<th class='fixedHeader' style='width: 13%; text-align:center; cursor:pointer;' onclick=sortHeader('C')>"+docInfo.time+"&nbsp<img src='${image}/icon/" + sortImg + "' id='sort_time' style='width:10px; vertical-align:middle;'</th>";
		} else {
			html += "<th class='fixedHeader' style='width: 11%; text-align:center; cursor:pointer;' onclick=sortHeader('C')>"+docInfo.time+"</th>";			
		}
		html += "<th class='fixedHeader' style='width: 8%; text-align:center;'>"+docInfo.authority+"</th>";
		html += "<th class='fixedHeader' style='width: 6%; text-align:center;'>"+docInfo.status+"</th>";
		html += "<th class='fixedHeader'></th>";
		html += "</tr>";
		$("#contentHeader").empty().append(html);
		var html1 ="<tr><td colspan='8' style='text-align:center;'><spring:eval expression="@${msgLang}['NOTEXIST_DOC']"/></td></tr>";
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
					inHtml += "<td style='text-align:center;'><input type='checkbox' name='chkbox' id='chkbox"+(i+1)+"' value='"+bundle.contentid+"'><label for='chkbox"+(i+1)+"'><label></td>";

					// 파일/번들 구분
					var gubun = "<spring:eval expression="@${lang}['BUNDLE']"/>";
					if(bundle.contenttype != "01"){ // 파일
						gubun = "<spring:eval expression="@${lang}['FILE']"/>";
						inHtml += "<td title='" + gubun + "' style='text-align:center;' >&nbsp<img src='${image}/icon/" + iconfile + "' style='vertical-align:middle;'/></td>";
					} else { // 번들
						if (bundle.ctype == "02") { // Virtual
							gubun = "<spring:eval expression="@${lang}['LINK']"/>";
							inHtml += "<td title='" + gubun + "' style='text-align:center;' >&nbsp<img src='${image}/icon/icon_admin_13.gif' style='vertical-align:middle;'/></td>";							
						} else { // 01:Normal or else
							inHtml += "<td title='" + gubun + "' style='text-align:center;' >&nbsp<img src='${image}/icon/Group 19.png' style='vertical-align:middle;'/></td>";
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
					  inHtml += "<td  class='mob_none' title='" + bundle.classpath+"' style='text-align:center;' onclick='fn_FldClick('"+classid+"');' style='cursor: pointer;'>"+classname+"</td>";
					}else{
					  inHtml += "<td  class='mob_none' title='" + bundle.classpath+"' style='text-align:center;' >"+classname+"</td>";
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
						inHtml += "<td class='subject' title='" + summary + "' onclick='';>" + title + "</td>";
					}else{
						inHtml += "<td class='subject' title='"+summary+"'>" + titles[0] + "</td>";
					}
					
					// 소유자
					inHtml += "<td class='mob_none' style='text-align:center;' >[" + bundle.creatordeptname + "] " + bundle.creatorname + "</td>";
					
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
					inHtml += "<td class='mob_none' style='text-align:center;' >"+rightName+"</span></td>";
					
					// 상태
					// A0:편집, A1:삭제, A2:복구, A3:이동, A4:복사, A5:잠금, A6:이동, B1:등록, B2:폐기, 
					if(code == "A0" || code == "B1"){
						inHtml += "<td class='mob_none' style='text-align:center;' >"+bundle.apporder +"<spring:eval expression="@${msgLang}['WAITING_APL']"/></td>";	
					} else if (code == "03"){ // 잠금
						inHtml += "<td class='mob_none' style='text-align:center;' >" + state + "<img src='${image}/icon/icon-zenithlist-lock.gif' style='width:17px; vertical-align:middle;'/></td>";	
					}else{
						inHtml += "<td class='mob_none' style='text-align:center;' >" + state + "</td>";
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
	
	var createDocEmptyList = function(){
		$("#totCnt").text("<spring:eval expression="@${lang}['TOTAL']"/>"+" : 0");
		$("#bundleList").empty();
		var inHtml = "";
			inHtml += "<tr>";
			if(selectedClsid == "TRASH"){
				inHtml += "<td colspan='8' style='text-align:center;'><spring:eval expression="@${msgLang}['NOTRASH_DOC']"/></td>";
			}else{
				inHtml += "<td colspan='8' style='text-align:center;'><spring:eval expression="@${msgLang}['NOTEXIST_DOC']"/></td>";
			}
			
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
					$tr.children().eq(1).on('click', function(e) {				
						var data = $(this).data('meta');
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
	
	var docEditOpen = function(contentid){
		selectedContentid = contentid;
		$("#openPop").empty();
	   	$("#openPop").load("${ctxRoot}/go/fileEdit"); 
	   	fn_openPop("openPop");
	}

	var docViewOpen = function(contentid){
		selectedContentid = contentid;
	   	$("#openPop").load("${ctxRoot}/go/fileInfo"); 
	   	fn_openPop("openPop");
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
					//수정오류일경우 원래 폴더명으로 되돌린다.
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
				str += "<li><a href=\"javascript:pageLink"+"("+nextIndex+");\" class = 'pagBtn next'><img src='${image}/icon/double_arrow.png'></a></li>";
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
	
	function DelContextSelect(key){
		if(key != null){
			noty({
			    layout : "center",
			    text : "<spring:eval expression="@${msgLang}['ARE_YOU_DEL_DOC']"/>",
			    buttons : [ {
			      addClass : 'b_btn',
			      text : "Ok",
			      onClick : function($noty) {
			        $noty.close();
			        delDoc(key);
			        listSearch();
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
		} else {
			alert("<spring:eval expression="@${msgLang}['SELECT_DELETE_DOC']"/>");
		}
    };
    
	$(document).ready(function() {
		initAclList();

		fn_MainClsTree.jstree();
		//fn_Job_Tree.jstree();
		fn_fldTree.jstree();
		clsLoadList("${contentid}");
		
	    $("#SearchBtn").click(function() {	//검색   
	    	//$("#adSearchOK").hide();
	    	listSearch();	    	
	    });
	  
	    // 상세검색 시간 설정
	    var now = new Date();
	    document.getElementById('regSDate').value = new Date(now.setDate(now.getDate()-30)).toISOString().substring(0,10);
	    document.getElementById('regEDate').value = new Date().toISOString().substring(0,10);
	    
	    $("#btnInitAdSearchData").click(function() { //상세검색 조건 데이터 초기화  
	    	$("input:checkbox[id='incLowYn']").attr("checked", false);
	    	$("#searchTitle").val("");
	    	$("#searchDocNo").val("");
	    	$("#keywordTitle").val("");
	    	$("#creatorTitle").val("");
	    	$("#ftrSearch").val("");
	    	
	    });	    

	    /*
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
		*/
		
		// 문서제목
		 $("#searchTitle").on("keyup",function(key){
				var len = $(this).length;
				if(len > 0){
					$("#resetTitle").css('display','inline');	
				}else{
					$("#resetTitle").css('display','none');
				}
	        if(key.keyCode==13) {
		    	listSearch();	    
	        }
	    });
		$("#resetTitle").click(function(){
			$("#searchTitle").val("");
			$("#resetTitle").css('display','none');			
		});

		// 문서번호
		 $("#searchDocNo").on("keyup",function(key){
				var len = $(this).length;
				if(len > 0){
					$("#resetDocNo").css('display','inline');	
				}else{
					$("#resetDocNo").css('display','none');
				}
		     if(key.keyCode==13) {
		    	listSearch();	    
		     }
		 });
			$("#resetDocNo").click(function(){
				$("#searchDocNo").val("");
				$("#resetDocNo").css('display','none');			
			});

		 // 키워드
		 $("#keywordTitle").on("keyup",function(key){
				var len = $(this).length;
				if(len > 0){
					$("#resetKeyword").css('display','inline');	
				}else{
					$("#resetKeyword").css('display','none');
				}
		     if(key.keyCode==13) {
		    	listSearch();	    
		     }
		 });
			$("#resetKeyword").click(function(){
				$("#keywordTitle").val("");
				$("#resetKeyword").css('display','none');			
			});

		 // 작성자
		 $("#creatorTitle").on("keyup",function(key){
				var len = $(this).length;
				if(len > 0){
					$("#resetCreator").css('display','inline');	
				}else{
					$("#resetCreator").css('display','none');
				}
		     if(key.keyCode==13) {
		    	listSearch();	    
		     }
		 });
			$("#resetCreator").click(function(){
				$("#creatorTitle").val("");
				$("#resetCreator").css('display','none');			
			});
			
		// 내용
		 $("#ftrSearch").on("keyup",function(key){
				var len = $(this).length;
				if(len > 0){
					$("#resetFtrSearch").css('display','inline');	
				}else{
					$("#resetFtrSearch").css('display','none');
				}
		        if(key.keyCode==13) {
		        	listSearch();	    
		        }
	    });
		$("#resetFtrSearch").click(function(){
			$("#ftrSearch").val("");
			$("#resetFtrSearch").css('display','none');			
		});
		 
		$('#searchClsName').keydown( function(event) {
			var len = $(this).length;
			if(len > 0){
				$("#resetClsName").css('display','inline');	
			}else{
				$("#resetClsName").css('display','none');
			}
			if(event.keyCode == 13)	ClsSearch(); 	
		});
		$("#resetClsName").click(function(){
			$("#searchClsName").val("");
			$("#resetClsName").css('display','none');			
		});
		
		$("#searchClsBtn").click(function() {
			ClsSearch();
		});
		
		//리스트 전체선택 header에 name추가 selectAllDoc 
		$(document.body).delegate('input[name=selectAll]','click',function() {
			var isCheck = $(this).is(":checked");
			$("input:checkbox[name='chkbox']").prop("checked",isCheck);
		});
		
		$("input[type='radio'][name='group_dragEx']").click( function() {
			var checked = $(this).val();
			if (checked == 'rename') {
				$("#dupRename_dragEx").show();
			} else if (checked == 'versionup') {
				$("#dupRename_dragEx").hide();
			}
		});
	});
	
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
	
	var closeAdSearch  = function(){
	 	$("#searchTitle").attr("disabled",false);
		//$("#adSearchLayer").hide();
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
	
	var EnterSearch = function(e){
		if(e.keycode == 13){
			listSearch();
		}
		return false;
	}

	var clsLoadList = function(selectedClsid) {
		var sendData = {
			objIsTest : "N",
			classid : selectNodeId(selectedClsid),
		};
		var tree = this;
		$.ajax({
			url : "${ctxRoot}/api/classification/get",//"${ctxRoot}/api/classification/list/down"
			type : "POST",
			dataType : 'json',
			contentType : 'application/json',
			async : false,
			data : JSON.stringify(sendData),
			success : function(data) {
				console.log("clsLoadList data : ", data);
				if (objectIsEmpty(data.result)) {
					return;
				}
				var fldName = data.result.zappClassification.name;
				$("#searchFldName").val(fldName);
			},
			error : function(request, status, error) {
				alertNoty(request, status, error);
			},
			beforeSend : function() {
			},
			complete : function() {
				fldSearch();
			}
		});
	}
	
	var chkfile = function(senddata, fileInfo, i){
		console.log("chkfile start : " + i);
		if(fileInfo.length == i){
			alert("<spring:eval expression="@${msgLang}['REG_DOC_SUCCEEDED']" />");
			//$("#adSearchOK").hide();
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

	var openLayer = function(message) {
		if (!$('.opacity_bg_layer').length) { //
			var innerTable = "<table height='100%' width='100%' border='0'><tr>";
			innerTable += "<td valign='middle' align='center'>";
			innerTable += "<div class='notice'><div class='loading'>";
			innerTable += "		<p style='width:10%;'>" + message + ". <br /><spring:eval expression="@${msgLang}['WAIT_A_MINUTE']"/></p></div>";
			innerTable += "		<img src='${image}/visual/loading_admin.gif' />";
			innerTable += "</div></td></tr></table>";
			$('<div class="opacity_bg_layer" style="position:absolute;top:0;z-index:2000;"></div>').html(innerTable).prependTo($('body'));
		}
		
		var oj = $(".opacity_bg_layer");
		var w = $(document).width();
		var h = $(window).height();
		
		oj.css({
			'width' : w,
			'height' : h
		});
		oj.fadeIn(0);
	}

</script>
</head>

<body>
	<main style="height:calc(100vh);">
		<div class="bg" style="top:unset;"></div>
		<div class="flx">
			<nav>
				<!-- 사이드 메뉴 -->
				<div id="resizable" class="nav_wrap ui-widget-content">
					
					<ul class="gnb" style="height:100%">
						<li class="slideMenu"><a href="#none"><img src="${image }/icon/icon_1.png"><span class="foldingMn">모든 문서</span></a>
							<div style="visibility: hidden;height: 0px;">
								<input type="text" id = "searchFldName" style="margin-left:10px; margin-bottom: 10px" placeholder="검색할 폴더명 입력">
							</div>
							<ul class="custom-select-list" style="display:none;">
								<li value="selectOption1" class="custom-select-option"><img src="${image }/icon/Group 79.png">문서함</li>
							</ul>
							<select class="select-origin">
								<option value="selectOption1" selected="selected">문서함</option>
							</select>
							<div id="fldjstree" class="sub" style="display:unset;">
							</div>
							<!-- 
							<div id ="MainClsTree" class="sub" style="display: none;">
							</div>
							-->
						</li>
					</ul>
				</div>
			</nav>
			<section id="content">
				<div class="breadcrumb">
					<ul id = "SelectText">
					</ul>
				</div>
				<div class="innerWrap" style="height: 91%; padding:30px; padding-top:0px;">
					<div style="float:right;display:inline;padding-right: 22px;">
						<p style="width:unset;">하위 폴더 포함</p>
						<input type="checkbox" id="incLowYn"><label for="incLowYn" style="margin-top: 5px;"></label>
						<input type="submit" id="SearchBtn" style="display:none;">
						<label for="SearchBtn" style="position:unset;padding-left:33px;"><img src="${image}/icon/Group 57.png"></label>
					</div>

					<div class="uiGroup" style="width:99%;background-color:#e7eaf5;border-radius:6px;">
						<div style="display:block;width:100%">
							<form style="width:100%" action="#" id = "SearchForm">
							<br>
								<p align="center" style="margin-left:10px;">문서 제목</p>
									<input type="text" id ="searchTitle" placeholder="" >
				                    <img src="${image}/icon/x.png" style="width:14px; height: 14px; margin-top:8px; margin-left: -34px; margin-right:15px; cursor: pointer; display: none;" id ="resetTitle">
								<p align="center">문서 번호</p>
									<input type="text" placeholder="" id="searchDocNo"> 
				                    <img src="${image}/icon/x.png" style="width:14px; height: 14px; margin-top:8px; margin-left: -34px; margin-right:15px; cursor: pointer; display: none;" id ="resetDocNo">
								<p align="center">키워드</p>
									<input type="text" placeholder="" id="keywordTitle">
				                    <img src="${image}/icon/x.png" style="width:14px; height: 14px; margin-top:8px; margin-left: -34px; cursor: pointer; display: none;" id ="resetKeyword">
							<br>
								<p align="center" style="margin-left:10px;">등록자</p> 
									<input type="text" placeholder="" id="creatorTitle">
				                    <img src="${image}/icon/x.png" style="width:14px; height: 14px; margin-top:8px; margin-left: -34px; margin-right:15px; cursor: pointer; display: none;" id ="resetCreator">
								<p align="center">내용</p>
									<input type="text" placeholder="" id="ftrSearch">	
				                    <img src="${image}/icon/x.png" style="width:14px; height: 14px; margin-top:8px; margin-left: -34px; margin-right:15px; cursor: pointer; display: none;" id ="resetFtrSearch">
								<p>검색 기간</p>
									<div style="display:inline;text-align:center;">
									<input type="date" max="9999-12-31" id="regSDate"> ~ <input type="date" max="9999-12-31" id="regEDate">	
									</div>			  
							</form>
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
		</div>
	</main>
</body>

</html>