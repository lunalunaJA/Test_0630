<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="../common/CommonInclude.jsp" %>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>ECM4.0 :: 문서 분류 엑셀 일괄 등록</title>

<script type="text/javascript" src="${js}/jquery-1.11.0.min.js"></script>
<script type="text/javascript">
	var fileData  ={};
	//multipart file send
	var fileSend = function(){
		var formData = new FormData();
		for(var key in fileData){
			var fileKey = key;
			var fileObj = fileData[key];
			formData.append(fileKey, fileObj);
		}
		//메타정보
		formData.append("param", "value");
		
		$.ajax({
			url : "${ctxRoot}/api/file/fileSend",
			data: formData,
			enctype : "multipart/form-data",
			async : true,
			type : "POST",
			dataType : "json",
			processData : false,
			contentType : false,
			cache : false,
			timeout : 600000,
			success : function(data){			
				if(data.result == 0){
					var sendFilesInfo = data.zappFiles;
					console.log(JSON.stringify(sendFilesInfo));
					ClsReg(sendFilesInfo);
				}
			},
			complete : function(){
				
			},
			error : function(request,status,error){
			
			}				
	
		});
	}
	

	//파일전송후 ECM 등록 API 호출
	var ClsReg = function(fileInfo){
		var data = {};			
		data.name = fileInfo[0].objFileName;
		data.classid = $("#classid").val();
		console.log(""+JSON.stringify(data));
		if(!data.classid){
			alert("등록할 문서분류를 입력하세요");
			return false;
		}
		//TODO -- data validate 
		$.ajax({ 
			url : "${ctxRoot}/go/regClsByExcel" ,
			type : "POST" , 
			dataType : 'json',
			contentType : 'application/json',
			async : false , 
			data : JSON.stringify(data) , 
			success : function(data){
				if(data.status == "0000"){
					alert("등록 완료 되었습니다.");
				}else{
					alertErr(data.message);				
				}
			}, 
			error : function(e) {alert("등록에 실패했습니다.")} , 
			beforeSend : function() {} , 
			complete : function() {} 
		}); 
	}
	
	//버튼 메뉴 파일 추가
	var js_addFileBtn = function(){
		$("#inputFile").remove();
		var $fileInput = $("<input id='inputFile' name='file' type='file' multiple style='display:none'/>");
		$fileInput.appendTo("body");
		$('#inputFile').on('change',function(e){
			var files = e.originalEvent.target.files;
			//fileHandle(files);
			fileData['addfile0'] = files[0];
		});
		$("#inputFile").click();
	}


	

	$(document).ready(function () {		
	
		//dragAndDrop
		
		//첨부 파일 추가
		$("#addFile").click(function(){
			js_addFileBtn();
		});
		
		//파일 삭제
		$("#delFile").click(function(){
			fileSend();
		});
		
		$("#Reg_Tab1").click(function(){
			$('a[id^=Reg_Tab]').removeClass('current');
			$('div[id^=Div_Reg]').hide();
			$("#Reg_Tab1").addClass('current');
			$("#Div_Reg1").show();	
		});
		
		$("#Btn_Acl1").click(function(){
			selectAcl = 1;
			$('a[id^=Btn_Acl]').removeClass('current');
			$('div[id^=Div_Acl]').hide();
			$("#Btn_Acl1").addClass('current');
			$("#Div_Acl1").show();		
		});
		
		$("#Btn_Acl2").click(function(){
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
			<span style="font-size: 26px; font-weight: bold;">문서등록</span>			
		</div>			
	</div>
</div>
<div class="DocReg" style="position:absolute; width: 100%;">
	<!-- 트리화면 -->
	<div id="docInfoDefault">
		<div class="popup_container" style="width: 95%; height: 50px;">
            <!-- 첨부 파일 -->
            현재 클래스 아이디 : <input type="text" id="classid" name="classid" style="width:50px;" />
			<div id="attach" class="p_tab_box" style="height:40%;">
		    	<ul >	            
			        <li>
			          	<input type="button" class="b_btn" style="margin-right: 5px;" id="addFile" value="파일추가">
			           	<input type="button" class="b_btn" style="margin-right: 5px;" id="delFile" value="등록">
			        </li>	            	
			    </ul>
				<div id="fileInfo" class="fileInfo" style="float: left;width:48%;height:100px;overflow:auto;">
					<spring:eval expression="@${msgLang}['NOATTACHMENT']"/>
				</div>
		    </div>
		</div>
	</div>
</div>
</body>
</html>