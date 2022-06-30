<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ECM4.0 :: 폴더트리</title>

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
	var selectReasonType = "";
	var type = "";
	
	selectedContentid = "${contentid}";
	taskid = "${taskid}";
	opType = "${opType}";
	
	console.log("=== selectedContentid: " + selectedContentid);
	console.log("=== taskid: " + taskid);
	console.log("=== opType: " + opType);

  $(document).ready(function() {
    console.log("====selectReasonType : " + selectReasonType);
    if (opType == "exportappr"){
      	$("#reasonTitle").text("<spring:eval expression="@${lang}['REASON_FOR_APPROVAL']" />");
      	$("#reasonText").val("<spring:eval expression="@${lang}['APPROVAL']" />");
      	$('#btnClsInfoOk').val('<spring:eval expression="@${lang}['APPROVAL']" />');
		type = "approve";
    } else {
      	$("#reasonTitle").text("<spring:eval expression="@${lang}['REASON_FOR_REJECTION']" />");
      	$("#reasonText").val("<spring:eval expression="@${lang}['REJECTION']" />");
      	$('#btnClsInfoOk').val('<spring:eval expression="@${lang}['REJECTION']" />');
		type = "return";
    }
    
    
 // 팝업 닫기
	$('#closeBtn').unbind("click").bind("click", function(){
		$('.bg').fadeOut();
        $('.popup').fadeOut();
    });
    
    
  });

	var reasonDocList = function(data,contentList,type){
		$.each(contentList,function(index,contentid){
		  	reasonDoc(contentid,data,type);
		});
		//approvalPopClose();
	}

	var reasonDoc = function(id, data, type){
		var meta = $("#"+id).data("meta");
		data.contentid = id;	
		data.objType = "02"; //02:File
		console.log("====id : ", id);
		console.log("====data : ", data);
		console.log("====type : ", type);
		$.ajax({ 
			url : "${ctxRoot}/api/content/" + type ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){		
			  if(data.status == "0000"){
			    if(type == "approve"){
			    	confirmCS("문서를 승인 했습니다.");
			    } else if (type == "return"){
			    	confirmCS("문서를 반려 했습니다.");
			    }
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
  
  //var lsCallBack;//레이어 팝업 호출시 데이터 할당
  
  //메인페이지에서 넘긴 콜백 함수 호출
  var callback = function() {
		var data = {};
		data.objIsTest = "N";
		data.objDebugged = false;
		data.contentid = taskid;
		data.zappLockedObject = reason; //승인사유

		var reasonList = [];
		reasonList.push(selectedContentid);

		var lsCallBack = {};
		lsCallBack.param = {list : reasonList};
		lsCallBack.data = data;
		lsCallBack.type = type;
		lsCallBack.func = reasonDocList;

		console.log("lsCallBack : ", lsCallBack);
		var reason = $.trim($("#reasonText").val());
		if (objectIsEmpty(reason)) {
			if (selectReasonType == "approve") {
				reason = "<spring:eval expression="@${lang}['APPROVAL']" />";
			} else {
				reason = "<spring:eval expression="@${lang}['REJECTION']" />";
			}
		}

		lsCallBack.data.zappLockedObject = {
			"reason" : reason
		};
		lsCallBack.func(lsCallBack.data, lsCallBack.param.list, lsCallBack.type);
  }
</script>
</head>
<body>
<div class="popup" style="display: block; width:97%; height:89%; top:5px; border:0px; box-shadow:none; padding:15px;">
	<h3 class="pageTit">코멘트</h3>
	<div class="tabCont" style="width: 352px; height: 225px;">
		<!--cont01-->
		<div id="cont03" class="contdiv" style="display:block; height:90%;">
			<div class="flex-content">
				<textarea id = "reasonText" rows="7" cols="5" style="border: 1px solid; width:100%; height:100%;"  onkeyup="pubByteCheckTextarea(event,100)"></textarea>
			</div>
		</div>
	</div>
	<div style="text-align: center;">
		<button class="btbase" onclick="javascript:callback();" id ="btnClsInfoOk">저장</button>
	</div>
</div>
</body>
</html>