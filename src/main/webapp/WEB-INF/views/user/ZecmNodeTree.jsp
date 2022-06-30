<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp"%>
<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width" />
    <link type="text/css" rel="stylesheet" href="${css}/jstree.css">
    <script type="text/javascript" src="${js}/jquery-1.11.0.min.js"></script>
    <script type="text/javascript" src="${js}/jstree.js"></script>
    <script type="text/javaScript" language="javascript" defer="defer">
	    if (!String.prototype.startsWith) {
	    	String.prototype.startsWith = function(search, pos) {
	    		return this.substr(!pos || pos < 0 ? 0 : +pos, search.length) === search;
	    	};
	    }
		var companyid = "${Authentication.objCompanyid }";
		var deptid = "${Authentication.sessOnlyDeptUser.deptid }";
		var deptuserid = "${Authentication.sessOnlyDeptUser.deptuserid }";

		var icon = "${ctxRoot}/resources/images/jstree/tree_user_icon15.png";
		var icons = {N1 : "${ctxRoot}/resources/images/jstree/tree_user_icon15.png",
				N2 : "${ctxRoot}/resources/images/jstree/tree_user_icon15.png",
				N3 : "${ctxRoot}/resources/images/jstree/tree_user_icon03.png",
				N4 : "${ctxRoot}/resources/images/jstree/tree_user_icon05.png",
				FAV : "${ctxRoot}/resources/images/jstree/tree_user_icon06.png",
				TRH : "${ctxRoot}/resources/images/jstree/tree_user_icon07.png"}
		
		var rootData = [];
		rootData.push({ id : companyid ,parent:"#",icon : icons["N1"], text : "<spring:eval expression="@${lang}['COMPANY_FOLDER_BOX']"/>" ,a_attr : {type:"N1"}});//전사
		rootData.push({ id : deptid ,parent:"#",icon : icons["N2"], text : "<spring:eval expression="@${lang}['DEPARTMENT_FOLDER_BOX']"/>" ,a_attr : {type:"N2"}});//부서
		rootData.push({ id : deptuserid ,parent:"#",icon : icons["N3"], text : "<spring:eval expression="@${lang}['PERSONAL_FOLDER_BOX']"/>" ,a_attr : {type:"N3"}});//개인
		rootData.push({ id : "COOPERATE" ,parent:"#",icon : icons["N4"], text : "<spring:eval expression="@${lang}['COLLABORATIVE_FOLDER_BOX']"/>" ,a_attr : {type:"N4"}});//협업
		rootData.push({ id : "FAVERITE" ,parent:"#",icon : icons["FAV"], text : "<spring:eval expression="@${lang}['FAVORITE']"/>" ,a_attr : {type:"FAV"}});//즐겨찾기
		rootData.push({ id : "TRASH" ,parent:"#",icon : icons["TRH"], text : "<spring:eval expression="@${lang}['RECYCLE_BIN']"/>" ,a_attr : {type:"TRH"}});//휴지통
			
			
        var fn_Common = { 
            consoleLog : function(data) {                
                $.each(data, function(i,v) {
                     console.log(i + "===>>" + v); 
                });               
            },
            jstree : function() { 
            	
                $('#jstree') .jstree({ 
                    core: { 
                        check_callback: true,
                        data: rootData /* 최초에 보여지 최상위 Root Tree */ 
                    },
                    types: { 
                        "default" : {
                             "icon" : "glyphicon glyphicon-flash" 
                        },
                        file: {
                             icon: "fa fa-file text-inverse fa-lg" 
                        } 
                    },
                    plugins : ["massload" , "state" , "types" , "unique" ,  "changed" ] 
                }) 
                .on("select_node.jstree", function (event, data) { // 노드가 선택된 뒤 처리할 이벤트 
                	 
                	var id = data.node.id;
                	var type = data.node.a_attr.type;
                	console.log("data.node.id : " + id);
                	console.log("data.node.id : " + type);
                    // 선택한 Node에 따라 하위 목록 가져오기 
                    if(type.startsWith("N")){
                   		if(!data.node.children.length) fn_Common.jstreeDynamic(data.node.id,data.node.a_attr.type); 
                    }
                });
             },
            jstreeDynamic : function(selectedNode,type) { 
                $.ajax({ 
                   url : "${ctxRoot}/api/classification/list/down" ,
                   type : "POST" , 
                   dataType : 'json',
            	   contentType : 'application/json',
                   async : false , 
                   // data : { id : selectedNode } , 
                   data : JSON.stringify({ objIsTest : "Y",
                            upid : selectedNode,
                            types : type,
                            isactive:"Y"}) , 
                    success : function(data){ 
                      console.log("data : ", data);
              			$.each(data.result, function(i, result) {
            				var attr = {}
            				var obj;
            				obj =(result.zappClassification)?result.zappClassification:result;
                        	attr.type = obj.types;
                        	var child = {};
                        	child.id = obj.classid;
                        	child.text = obj.name;
                        	child.icon = icon;
                        	child.a_attr = attr;
                            $('#jstree').jstree('create_node' ,obj.upid , child ,"last" ,false ,false); 
                        }); 
                    }, 
                  	error : function(request, status, error) {
            	        	alertNoty(request,status,error);
                    } , 
                    beforeSend : function() {} , 
                    complete : function() {} 
                }); 
            } 
        } 
    </script>
</head>

<body>
    <div id="jstree"></div>
    <script>
        $(document).ready(function () {
            $(document).keypress(function (e) {
                if (e.keyCode == 13) {
                    return false;
                }
            }); 
            fn_Common.jstree(); 
        }); 
    </script>
</body>

</html>