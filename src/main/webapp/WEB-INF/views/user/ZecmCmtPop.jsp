<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp"%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ECM4.0 :: 권한</title>
<script type="text/javascript">

if (!String.prototype.startsWith) {
	String.prototype.startsWith = function(search, pos) {
		return this.substr(!pos || pos < 0 ? 0 : +pos, search.length) === search;
	};
}

$(document).ready(function() {
	// 팝업 닫기
	$('#closeBtn').unbind("click").bind("click", function(){
		$('.bg').fadeOut();
        $('.popup').fadeOut();
    });
});

var CmtRegCallBack;//레이어 팝업 호출시 데이터 할당
//메인페이지에서 넘긴 콜백 함수 호출
var callback = function(){
	CmtRegCallBack.data.comments= $("#cmtArea").val();
	//현재 레이어에서 선택된 데이터 담기
	console.log("cmtReg pop data : ",CmtRegCallBack.data);
	CmtRegCallBack["func"](CmtRegCallBack.data);
}
	

</script>
</head>
<body>
<div class="popup" style="display: block;">
	<h3 class="pageTit">코멘트</h3>
	<button type="button" id="closeBtn">
		<img src="${image}/icon/x.png">
	</button>
	<div class="tabCont" style="width: 320px; height: 140px;">
		<!--cont01-->
		<div id="cont03" class="contdiv" style="display:block;">
			<div class="flex-content">
				<textarea id = "cmtArea" rows="7" cols="5" style="border: 1px solid; width:100%" onkeyup="pubByteCheckTextarea(event,100)"></textarea>
			</div>
		</div>
	</div>
	<div style="text-align: center;">
		<button class="btbase" onclick="javascript:callback();">저장</button>
	</div>
</div>
</body>
</html>