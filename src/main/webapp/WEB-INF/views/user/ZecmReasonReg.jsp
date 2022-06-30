<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ECM4.0 :: 폴더트리</title>
<script type="text/javascript" src="${js}/common.js"></script>
<script type="text/javascript">
  $(document).ready(function() {
    console.log("====selectReasonType : " + selectReasonType);
    if(selectReasonType == "approve"){
      $("#reasonTitle").text("<spring:eval expression="@${lang}['REASON_FOR_APPROVAL']" />");
      $("#reasonText").val("<spring:eval expression="@${lang}['APPROVAL']" />");
      $('#btnClsInfoOk').val('<spring:eval expression="@${lang}['APPROVAL']" />');
    }else{
      $("#reasonTitle").text("<spring:eval expression="@${lang}['REASON_FOR_REJECTION']" />");
      $("#reasonText").val("<spring:eval expression="@${lang}['REJECTION']" />");
      $('#btnClsInfoOk').val('<spring:eval expression="@${lang}['REJECTION']" />');
    }
    
    
 // 팝업 닫기
	$('#closeBtn').unbind("click").bind("click", function(){
		$('.bg').fadeOut();
        $('.popup').fadeOut();
    });
    
    
  });

  var lsCallBack;//레이어 팝업 호출시 데이터 할당
  //메인페이지에서 넘긴 콜백 함수 호출
  var callback = function() {
    console.log("lsCallBack : ", lsCallBack);
    var reason = $.trim($("#reasonText").val());
    if(objectIsEmpty(reason)){
      if(selectReasonType == "approve"){
        reason = "<spring:eval expression="@${lang}['APPROVAL']" />";
      }else{
        reason = "<spring:eval expression="@${lang}['REJECTION']" />";
      }	
	}
    
    lsCallBack.data.zappLockedObject = {"reason" : reason};
    lsCallBack.func(lsCallBack.data, lsCallBack.param.list, lsCallBack.type);

  }
</script>
</head>
<body>
<div class="popup" style="display: block;">
	<h3 class="pageTit">사유 입력</h3>
	<button type="button" id="closeBtn"><img src="${image}/icon/x.png"></button>
	<div class="tabCont" style="width: 320px; height: 140px;">
		<!--cont01-->
		<div id="cont03" class="contdiv" style="display:block;">
			<div class="flex-content">
				<textarea id = "reasonText" rows="7" cols="5" style="border: 1px solid; width:100%" onkeyup="pubByteCheckTextarea(event,100)"></textarea>
			</div>
		</div>
	</div>
	<div style="text-align: center;">
		<button class="btbase" onclick="javascript:callback();" id ="btnClsInfoOk">저장</button>
	</div>
</div>
</body>
</html>