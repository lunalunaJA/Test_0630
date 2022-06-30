<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp" %>
<%
	String uFileId1 = request.getParameter("uFileId1");
	String uFileId2 = request.getParameter("uFileId2");

System.out.println("j uFileId1:"+uFileId1);
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ECM4.0 :: 파일비교</title>
<link  type="text/css"rel="stylesheet"  href="${css}/diff2html.css"/>
<script type="text/javascript" src="${js}/jquery-1.12.2.min.js"></script>
<script type="text/javascript" src="${js}/jquery-ui-1.13.1/jquery-ui.min.js"></script>
<script type="text/javascript" src="${js}/jquery.noty.packaged.min.js"></script>
<script type="text/javascript" src="${js}/diff2html.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	loadFileInfo();
});

var loadFileInfo = function() {
	$.ajax({
		url : "${ctxRoot}/api/file/compareVersion?uFileId1=<%=uFileId1%>&uFileId2=<%=uFileId2%>&objTaskId=A",
		type : "POST",
		contentType : 'application/json',
		headers: {
			Accept: "text/plain; charset=utf-8",
			"Content-Type":"text/plain; charset=utf-8"
		},
		data : null,
		success : function(data){
		  console.log("====loadFileInfo : ",data);
			if(data){
			var diffHtml = Diff2Html.getPrettyHtml(
					data,
					{inputFormat: 'diff', showFiles: true, matching: 'lines', outputFormat: 'side-by-side'}
				);
				document.getElementById("divDiffInfo").innerHTML = diffHtml;
			}else{
				alert("<spring:eval expression="@${msgLang}['NO_CHANGE_FILE']"/>");
				setTimeout(function(){
				  window.open("about:blank","_self").close();
				},1500);
				 
			}
		},
	  error : function(request, status, error) {
      alertNoty(request,status,error);
		}
	});
}

</script>
</head>
<body>
<div id="fvtheader">
	<div style="margin-left:20px; margin-top: 5px;">
		<div style="color:#FFFFFF">
			<span style="font-size: 26px; font-weight: bold;">파일비교</span>			
		</div>			
	</div>
</div>
<!-- 
<div class="DocCompare" style="position:absolute; width: 100%; top:0px; bottom:0px; margin-right: 5px; margin-left:5px;">
 -->
	<!--  버전 비교용 영역 -->	
	<div id="divDiff" style="width: 98%;height: 100%; padding-right: 10px; display: none1; border: 1px solid;">
		<div id="divDiffInfo" >
		</div>
	</div>	
<!-- 
</div>
 -->
</body>
</html>